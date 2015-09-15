package org.epiclouds.handlers.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.epiclouds.client.netty.handler.NettyHttpClientHandler;
import org.epiclouds.netty.NettyHttpClient;
/**
 * the manager to manage the channel
 * @author xianglong
 *
 */
public class ChannelManager implements Runnable{

	
	private NettyHttpClient client;
	/**
	 * the queue to contain the requests
	 */
	private LinkedBlockingQueue<BPRequest> requestQue=new LinkedBlockingQueue<BPRequest>(1000);
	/**
	 * host's requests
	 */
	private Map<String,LinkedBlockingQueue<BPRequest>> host_queues=new ConcurrentHashMap<String, LinkedBlockingQueue<BPRequest>>();
	/**
	 * host's free channel
	 */
	private Map<String,DelayQueue<BPChannel>> freeChannels=new ConcurrentHashMap<String, DelayQueue<BPChannel>>();

	/**
	 * max request num for poll from requestQue
	 */
	private  long max_poll_request_num=Constants.max_poll_request_num;
	/**
	 * the max num for handle num from host_queues
	 */
	private  long max_poll_handle_num=Constants.max_poll_handle_num;
	
	
	public ChannelManager(NettyHttpClient client){
		this.client=client;
	}
	public void putRequestBack(BPRequest request){
		try {
			requestQue.put(request);
		} catch (InterruptedException e) {
		}
	}
	public void addBPRequest(BPRequest request){
		try {
			requestQue.put(request);
			HostStatusManager.incrementRequestNum(request.getHost());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
	}
	public int getSizeOfHostChannel(String host){
		DelayQueue<BPChannel> dq=freeChannels.get(host);
		if(dq==null){
			return 0;
		}
		return dq.size();
	}
	public void start(){
		Executors.newSingleThreadExecutor().execute(this);
	}
	public void addBPChnnelToFreeQueue(BPChannel ch){
		DelayQueue<BPChannel> dq=null;
		synchronized (freeChannels) {
			dq=freeChannels.get(ch.getHost());
			if(dq==null){
				dq= new DelayQueue<BPChannel>();
				freeChannels.put(ch.getHost(), dq);
			}
		}
		dq.add(ch);
	}
	
	public void removeBPChnnelFromFreeQueue(BPChannel ch){
		DelayQueue<BPChannel> dq=null;
		synchronized (freeChannels) {
			dq=freeChannels.get(ch.getHost());
			if(dq==null){
				dq= new DelayQueue<BPChannel>();
				freeChannels.put(ch.getHost(), dq);
			}
		}
		dq.remove(ch);
	}
	
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
			for(int i=0;i<max_poll_request_num;i++){
				BPRequest br=requestQue.poll();
				if(br==null){
					break;
				}
				if(!br.getCh().isActive()){
					HostStatusManager.incrementHandledNum(br.getHost());
					br.release();
					continue;
				}
				host_queues.putIfAbsent(br.getHost(), new LinkedBlockingQueue<BPRequest>());
				LinkedBlockingQueue<BPRequest> lq=host_queues.get(br.getHost());
				lq.add(br);
			}
			Iterator<String> iter=host_queues.keySet().iterator();
			for(int i=0;i<max_poll_handle_num;i++){
				if(!iter.hasNext()){
					iter=host_queues.keySet().iterator();
				}
				if(!iter.hasNext()){
					break;
				}
				String host=iter.next();
				LinkedBlockingQueue<BPRequest> lq=host_queues.get(host);
				final BPRequest re=lq.peek();
				if(re==null) continue;
				ProxyStateBean psb=null;
				if(!re.getCh().isActive()){
					lq.poll();
					re.release();
					HostStatusManager.incrementHandledNum(re.getHost());
					continue;
				}
				do{
					psb=ProxyManager.getHostProxy(host);
				}while((psb!=null&&psb.isRemoved()));
				/**
				 * if i get the not initialized  proxy,
				 */
				if(psb!=null){
					client.connect(psb, re, this);
					lq.poll();
					HostStatusManager.incrementHandledNum(re.getHost());
					continue;
				}
				/**
				 * no not initialized proxy, we need the free channel
				 */
				DelayQueue<BPChannel> dq=null;
				synchronized (freeChannels) {
					dq=freeChannels.get(host);
					if(dq==null){
						dq= new DelayQueue<BPChannel>();
						freeChannels.put(host, dq);
					}
				}
				if(dq.peek()==null){
					continue;
				}
				BPChannel ch=dq.poll();
				if(ch==null||!ch.getCh().isActive()){
					continue;
				}
				if(ch.getPsb().isRemoved()){
					ch.getCh().close();
					continue;
				}
				NettyHttpClientHandler han=(NettyHttpClientHandler) ch.getCh().pipeline().get(Constants.CLIENT_HANDLER);
				if(han==null){
					continue;
				}
				han.setRequest(re);
				lq.poll();
				if(ch.getPsb().getAuthStr()!=null){
					try {
						re.getRequest().headers().add("Proxy-Authorization", "Basic "
							+new String(Base64.getEncoder().encode(ch.getPsb().getAuthStr().getBytes("utf-8")),"utf-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				ch.getCh().writeAndFlush(re.getRequest());
				HostStatusManager.incrementHandledNum(re.getHost());
			}
		}
	}
	
}

package org.epiclouds.handlers.util;

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
		requestQue.add(request);
	}
	public void addBPRequest(BPRequest request){
		requestQue.add(request);
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
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0;i<max_poll_request_num;i++){
				BPRequest br=requestQue.poll();
				if(br==null){
					break;
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
				BPRequest re=lq.peek();
				if(re==null) continue;
				ProxyStateBean psb=null;
				do{
					psb=ProxyManager.getHostProxy(host);
				}while((psb!=null&&psb.isRemoved()));
				/**
				 * if i get the not initialized  proxy,
				 */
				if(psb!=null){
					//System.err.println("use init proxy:"+psb);
					client.connect(psb, re, this);
					lq.poll();
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
				if(ch==null||!ch.getCh().isOpen()){
					continue;
				}
				if(ch.getPsb().isRemoved()){
					ch.getCh().close();
					continue;
				}
				//System.err.println("use channel:"+ch.getCh()+":"+ch.getCh().isOpen()+":"+dq.size());
				NettyHttpClientHandler han=(NettyHttpClientHandler) ch.getCh().pipeline().get(Constants.CLIENT_HANDLER);
				if(han==null){
					//System.err.println("han channel");
					continue;
				}
				han.setRequest(re);
				lq.poll();
				if(ch.getPsb().getAuthStr()!=null){
					re.getRequest().headers().add("Proxy-Authorization", "Basic "
						+new sun.misc.BASE64Encoder().encode(ch.getPsb().getAuthStr().getBytes()));
				}
				ch.getCh().writeAndFlush(re.getRequest());
			}
		}
	}
	
}

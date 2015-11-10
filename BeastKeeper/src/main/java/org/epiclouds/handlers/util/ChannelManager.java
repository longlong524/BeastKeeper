package org.epiclouds.handlers.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.epiclouds.client.netty.handler.NettyHttpClientHandler;
import org.epiclouds.handlers.util.BPChannel.WHERE;
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
	private ConcurrentHashMap<String,LinkedBlockingQueue<BPRequest>> host_queues=new ConcurrentHashMap<String, LinkedBlockingQueue<BPRequest>>();
	/**
	 * host's free channel
	 */
	private ConcurrentHashMap<String,DelayQueue<BPChannel>> freeChannels=new ConcurrentHashMap<String, DelayQueue<BPChannel>>();

	/**
	 * host's channel waiting for being recover.
	 */
	private ConcurrentHashMap<String,DelayQueue<BPChannel>> backChannels=new ConcurrentHashMap<String, DelayQueue<BPChannel>>();
	/**
	 * max request num for poll from requestQue
	 */
	private  long max_poll_request_num=Constants.max_poll_request_num;
	
	/**
	 * max command num once
	 */
	private  long max_poll_command_num=Constants.max_poll_command_num;
	/**
	 * the max num for handle num from host_queues
	 */
	private  long max_poll_handle_num=Constants.max_poll_handle_num;
	
	private volatile Thread thisthread;
	
	private LinkedBlockingQueue<ChannelCommandBean> commandQue=new LinkedBlockingQueue<ChannelCommandBean>(1000);
	
	public ChannelManager(NettyHttpClient client){
		this.client=client;
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
	/**
	 * add channel to a free channel queue
	 * @param ch
	 */
	public void addBPChnnelToFreeQueue(BPChannel ch){
		if(Thread.currentThread()==thisthread){
			DelayQueue<BPChannel> dq=null;
			dq=freeChannels.get(ch.getHost());
			if(dq==null){
				dq= new DelayQueue<BPChannel>();
				freeChannels.put(ch.getHost(), dq);
			}
			dq.add(ch);
		}else{
			commandQue.add(new ChannelCommandBean(Command.AddBPChnnelToFreeQueue, new Object[]{ch}));
		}
	}
	
	public void addBPChnnelToRecoverQueue(BPChannel ch){
		if(Thread.currentThread()==thisthread){
			DelayQueue<BPChannel> dq=null;
			dq=backChannels.get(ch.getHost());
			if(dq==null){
				dq= new DelayQueue<BPChannel>();
				backChannels.put(ch.getHost(), dq);
			}
			dq.add(ch);
		}else{
			commandQue.add(new ChannelCommandBean(Command.AddBPChnnelToRecoverQueue, new Object[]{ch}));
		}
	}
	public void putbackProxyToBackQueue(BPChannel channel,BPRequest request){
		if(Thread.currentThread()==thisthread){
			
			DelayQueue<BPChannel> dq=null;
				dq=backChannels.get(channel.getHost());
				if(dq==null){
					dq= new DelayQueue<BPChannel>();
					backChannels.put(channel.getHost(), dq);
				}
			
			BPChannel tmp=null;
				for(BPChannel ch:dq){
					if(ch.equals(channel)){
						tmp=ch;
						break;
					}
				}
			
			if(tmp==null){
				//no element
				return;
			}else{
				//have element
				dq.remove(tmp);
				if(!tmp.getPsb().isRemoved()){
					tmp.setVisit_time(System.currentTimeMillis());
					tmp.setWh(WHERE.FREEQUEUE);
					addBPChnnelToFreeQueue(tmp);
				}
			}
			
		}else{
			commandQue.add(new ChannelCommandBean(Command.PutbackProxyToBackQueue, new Object[]{channel,request}));
		}
	}
	
	public void removeBPChnnelFromFreeQueue(BPChannel ch){
		if(Thread.currentThread()==thisthread){
			DelayQueue<BPChannel> dq=null;
			dq=freeChannels.get(ch.getHost());
			if(dq==null){
				dq= new DelayQueue<BPChannel>();
				freeChannels.put(ch.getHost(), dq);
			}
		
			dq.remove(ch);
		}else{
			commandQue.add(new ChannelCommandBean(Command.RemoveBPChnnelFromFreeQueue, new Object[]{ch}));
		}
	}
	
	public void removeBPChnnelFromRecoverQueue(BPChannel ch){
		if(Thread.currentThread()==thisthread){
			DelayQueue<BPChannel> dq=null;
			dq=backChannels.get(ch.getHost());
			if(dq==null){
				dq= new DelayQueue<BPChannel>();
				backChannels.put(ch.getHost(), dq);
			}
			dq.remove(ch);
		}else{
			commandQue.add(new ChannelCommandBean(Command.RemoveBPChnnelFromRecoverQueue, new Object[]{ch}));
		}
	}
	
	@Override
	public void run() {
		thisthread=Thread.currentThread();
		while(true){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
			for(int i=0;i<max_poll_command_num;i++){
				ChannelCommandBean ccb=commandQue.poll();
				if(ccb==null){
					break;
				}
				switch(ccb.cc){
					case AddBPChnnelToFreeQueue:{
						addBPChnnelToFreeQueue((BPChannel)ccb.args[0]);
						break;
					}
					case AddBPChnnelToRecoverQueue:{
						addBPChnnelToRecoverQueue((BPChannel)ccb.args[0]);
						break;
					}
					case PutbackProxyToBackQueue:{
						putbackProxyToBackQueue((BPChannel)ccb.args[0], (BPRequest)ccb.args[1]);
						break;
					}
					case RemoveBPChnnelFromFreeQueue:{
						removeBPChnnelFromFreeQueue((BPChannel)ccb.args[0]);
						break;
					}
					case RemoveBPChnnelFromRecoverQueue:{
						removeBPChnnelFromRecoverQueue((BPChannel)ccb.args[0]);
						break;
					}
				}
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
				backChannels.putIfAbsent(br.getHost(), new DelayQueue<BPChannel>());
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
				DelayQueue<BPChannel> recoverChannelQue=backChannels.get(host);
				BPChannel expiredChannel=recoverChannelQue.poll();
				while(expiredChannel!=null){
					expiredChannel.setVisit_time(System.currentTimeMillis());
					expiredChannel.setWh(WHERE.FREEQUEUE);
					addBPChnnelToFreeQueue(expiredChannel);
					expiredChannel=recoverChannelQue.poll();
				}
				LinkedBlockingQueue<BPRequest> lq=host_queues.get(host);
				final BPRequest re=lq.peek();
				if(re==null) continue;
				
				if(re.getRequest().headers().contains(Constants.PUTPROXYHEADER)){
					lq.poll();
					String proxy=re.getRequest().headers().get(Constants.PUTPROXYHEADER)+"";
					String[] hs=proxy.split(":");
					RecoverChannelManager.putbackProxyToBackQueue(new BPChannel(re.getHost(), null,
							new ProxyStateBean(hs[0], Integer.parseInt(hs[1]), hs[2]+":"+hs[3]),
							this,WHERE.RECOVERQUEUE), re);
					HostStatusManager.incrementHandledNum(re.getHost());
					continue;
				}
				
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
				if(re.getRequest().headers().contains(Constants.GETPROXYHEADER)){
					lq.poll();
					RecoverChannelManager.sendProxyToChannel(ch, re); 
					HostStatusManager.incrementHandledNum(re.getHost());
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
	
	
	private enum Command{
		AddBPChnnelToFreeQueue,
		AddBPChnnelToRecoverQueue,
		PutbackProxyToBackQueue,
		RemoveBPChnnelFromFreeQueue,
		RemoveBPChnnelFromRecoverQueue
	}
	
	private class ChannelCommandBean{
		private Command cc;
		private Object[] args;
		public ChannelCommandBean(Command cc,Object[] args){
			this.cc=cc;
			this.args=args;
		}
	}
}

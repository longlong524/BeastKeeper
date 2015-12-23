package org.epiclouds.handlers.util;

import java.util.Random;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;

/**
 * the channel in beastkeeper
 * @author xianglong
 *
 */
public class BPChannel implements Delayed{
	/**
	 * the channel's host
	 */
	private volatile String host;
	/**
	 * the channel
	 */
	private volatile Channel ch;
	/**
	 * what time visited
	 */
	private volatile long visit_time;

	/**
	 * the proxy bean
	 */
	private volatile ProxyStateBean psb;
	/**
	 * the channel manager
	 */
	private volatile ChannelManager cm;
	/**
	 * timeout in milliseconds
	 */
	private volatile long timeout;
	
	private final  Random random=new Random();
	
	private volatile WHERE wh;
	/**
	 * 
	 * @param host
	 * @param ch
	 * @param psb
	 */
	public BPChannel(String host,Channel ch, ProxyStateBean psb,ChannelManager cm,WHERE wh){
		this.host=host;
		this.ch=ch;
		this.psb=psb;
		this.cm=cm;
		this.timeout=getRandomTimeout();
		this.setWh(wh);
	}
	private long getRandomTimeout(){
		long max_timeout=TimeoutManager.getHostTimout(host);
		long min_timeout=TimeoutManager.getHostMinTimout(host);
		if(min_timeout>max_timeout){
			long tmp=min_timeout;
			min_timeout=max_timeout;
			max_timeout=tmp;
		}
		if(min_timeout==max_timeout){
			return min_timeout;
		}
		if(min_timeout<0){
			return -min_timeout;
		}
		return random.longs(1, min_timeout, max_timeout).findFirst().getAsLong();
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Channel getCh() {
		return ch;
	}
	public void setCh(Channel ch) {
		this.ch = ch;
	}
	public long getVisit_time() {
		return visit_time;
	}
	public void setVisit_time(long visit_time) {
		this.visit_time = visit_time;
		this.timeout=getRandomTimeout();
	}
	@Override
	public int compareTo(Delayed o) {
		// TODO Auto-generated method stub
		long t1=this.getDelay(TimeUnit.MILLISECONDS);
		long t2=o.getDelay(TimeUnit.MILLISECONDS);
		return t1-t2>0?1:(t1-t2==0?0:-1);
	}
	@Override
	public long getDelay(TimeUnit unit) {
		// TODO Auto-generated method stub
		long t1=System.currentTimeMillis()-visit_time;
		if(t1>=timeout){
			return 0;
		}
		return unit.convert(timeout-t1, TimeUnit.MILLISECONDS);
	}
	public ProxyStateBean getPsb() {
		return psb;
	}
	public void setPsb(ProxyStateBean psb) {
		this.psb = psb;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.psb.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		BPChannel b=(BPChannel)obj;
		return this.psb.equals(b.psb);
	}
	public ChannelManager getCm() {
		return cm;
	}
	public void setCm(ChannelManager cm) {
		this.cm = cm;
	}
	
	public WHERE getWh() {
		return wh;
	}
	public void setWh(WHERE wh) {
		this.wh = wh;
	}

	public static enum WHERE{
		FREEQUEUE,
		RECOVERQUEUE
	}
}

package org.epiclouds.handlers.util;

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
	 * the timeout,in milliseconds
	 */
	private volatile long timeout=Constants.timeout;
	/**
	 * the proxy bean
	 */
	private volatile ProxyStateBean psb;
	/**
	 * the channel manager
	 */
	private volatile ChannelManager cm;
	/**
	 * 
	 * @param host
	 * @param ch
	 * @param psb
	 */
	
	public BPChannel(String host,Channel ch, ProxyStateBean psb,ChannelManager cm){
		this.host=host;
		this.ch=ch;
		this.psb=psb;
		this.cm=cm;
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
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
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
	
	
}

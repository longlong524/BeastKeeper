package org.epiclouds.handlers.util;

public class TimeOutBean {
	private volatile String host;
	private volatile long timeout;
	public TimeOutBean(String host,long timeout){
		this.host=host;
		this.timeout=timeout;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
}

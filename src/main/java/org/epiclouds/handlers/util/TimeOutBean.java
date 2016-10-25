package org.epiclouds.handlers.util;

public class TimeOutBean {
	private volatile String host;
	private volatile long timeout;
	private volatile long minTimeout;
	public TimeOutBean(String host,long timeout,long minTimeout){
		this.host=host;
		this.timeout=timeout;
		this.minTimeout=minTimeout;
	}
	public TimeOutBean(){}
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
	public long getMinTimeout() {
		return minTimeout;
	}
	public void setMinTimeout(long minTimeout) {
		this.minTimeout = minTimeout;
	}

	
}

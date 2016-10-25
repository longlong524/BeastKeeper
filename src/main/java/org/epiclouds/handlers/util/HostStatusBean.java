package org.epiclouds.handlers.util;

import java.util.concurrent.atomic.AtomicLong;

public class HostStatusBean {
	private volatile String host;
	private AtomicLong request_num=new AtomicLong(0);
	private AtomicLong handled_num=new AtomicLong(0);
	
	public HostStatusBean(String host){
		this.host=host;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public AtomicLong getRequest_num() {
		return request_num;
	}

	public void setRequest_num(AtomicLong request_num) {
		this.request_num = request_num;
	}

	public AtomicLong getHandled_num() {
		return handled_num;
	}

	public void setHandled_num(AtomicLong handled_num) {
		this.handled_num = handled_num;
	}
	public void incrementRequestNum(){
		this.request_num.incrementAndGet();
	}
	
	public void incrementHandledNum(){
		this.handled_num.incrementAndGet();
	}
	
}

package org.epiclouds.handlers.util;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
/**
 * the my request in beastkeeper
 * @author xianglong
 *
 */
public class BPRequest {
	/**
	 * where is the request
	 */
	private volatile Channel ch;
	/**
	 * the request
	 */
	private volatile FullHttpRequest request;
	/**
	 * the host header
	 */
	private volatile String host;
	/**
	 * 
	 * @param ch
	 * @param request
	 */
	public BPRequest(Channel ch,FullHttpRequest request){
		this.ch=ch;
		this.request=request;
		setHost(request.headers().get("host")+"");
	}
	public Channel getCh() {
		return ch;
	}
	public void setCh(Channel ch) {
		this.ch = ch;
	}
	public FullHttpRequest getRequest() {
		return request;
	}
	public void setRequest(FullHttpRequest request) {
		this.request = request;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	
}

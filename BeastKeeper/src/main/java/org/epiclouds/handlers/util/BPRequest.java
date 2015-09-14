package org.epiclouds.handlers.util;

import org.epiclouds.host.pattern.HostPatternManager;

import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
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
		this.setCh(ch);
		this.setRequest(request);
		String h=request.headers().get("host")+"";
		String pattern=HostPatternManager.getManager().getClosestMatchString(h);
		if(pattern!=null){
			this.host=pattern;
		}else{
			this.host=h;
		}
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
	public FullHttpRequest getRequest() {
		return request;
	}
	public void setRequest(FullHttpRequest request) {
		this.request = request;
	}
	
	public void release(){
		if(this.request!=null){
			this.request.release();
		}
	}
}

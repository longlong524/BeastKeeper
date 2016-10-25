package org.epiclouds.handlers.util;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.UnsupportedEncodingException;

import org.epiclouds.handlers.util.BPChannel.WHERE;

public class RecoverChannelManager {
	/**
	 * send the proxy to client
	 * @param channel
	 * @param request
	 * @return
	 */
	public static boolean sendProxyToChannel(BPChannel channel,BPRequest request){
		channel.setWh(WHERE.RECOVERQUEUE);
		HttpResponseStatus status=new HttpResponseStatus(200, "ok");
		FullHttpResponse res=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
		try {
			res.content().writeBytes(channel.getPsb().toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		res.headers().addInt("Content-Length",res.content().readableBytes());
		request.getCh().writeAndFlush(res);
		channel.setVisit_time(System.currentTimeMillis());
		channel.getCm().addBPChnnelToRecoverQueue(channel);
		request.release();
		return true;
	}
	
	
	public static void putbackProxyToBackQueue(BPChannel channel,BPRequest request){
		channel.getCm().putbackProxyToBackQueue(channel, request);
		HttpResponseStatus status=new HttpResponseStatus(200, "ok");
		FullHttpResponse res=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
		try {
			res.content().writeBytes("ok".getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.headers().addInt("Content-Length",res.content().readableBytes());
		request.getCh().writeAndFlush(res);
		request.release();
		return;
	}
}

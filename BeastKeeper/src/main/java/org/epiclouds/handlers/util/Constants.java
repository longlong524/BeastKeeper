package org.epiclouds.handlers.util;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class Constants {
	/**
	 * the host's time out  in milliseconds.
	 */
	public static long timeout=30000;
	
	public static long max_poll_request_num=100;
	
	public static long max_poll_handle_num=100;
	
	public final static String CLIENT_HANDLER="clienthandler";
	
	public static long request_time=30000;
	
	public static HttpResponse CONNECT_RESPONSE;
	
	static{
		HttpResponseStatus status=new HttpResponseStatus(405, "Don not support the connect method!");
		CONNECT_RESPONSE=new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
		CONNECT_RESPONSE.headers().add("Allow","GET,HEAD,POST,PUT,TRACE,OPTIONS,DELETE");
		CONNECT_RESPONSE.headers().add("Server","Beast Keeper 1.0");
	}
}

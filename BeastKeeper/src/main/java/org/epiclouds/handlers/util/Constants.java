package org.epiclouds.handlers.util;

import java.util.concurrent.atomic.AtomicLong;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class Constants {
	/**
	 * the host's time out  in milliseconds.
	 */
	private static AtomicLong timeout=new AtomicLong(30000);
	
	public static long max_poll_request_num=100;
	
	public static long max_poll_handle_num=100;
	
	public final static String CLIENT_HANDLER="clienthandler";
	
	public static long request_time=30000;
	
	public static HttpResponse CONNECT_RESPONSE;
	
	public static final String PROXYFILE="valid_proxy";
	
	public static final String MONGO_HOST="localhost";
	public static final int MONGO_PORT=27017;
	
	static{
		HttpResponseStatus status=new HttpResponseStatus(405, "Don not support the connect method!");
		CONNECT_RESPONSE=new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
		CONNECT_RESPONSE.headers().add("Allow","GET,HEAD,POST,PUT,TRACE,OPTIONS,DELETE");
		CONNECT_RESPONSE.headers().add("Server","Beast Keeper 1.0");
	}

	public static long getTimeout() {
		return timeout.get();
	}

	public static void setTimeout(long timeout) {
		Constants.timeout.set(timeout);
	}


}

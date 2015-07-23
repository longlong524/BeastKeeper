package org.epiclouds.handlers.util;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.concurrent.atomic.AtomicLong;

/**
 * config and some constants
 * @author xianglong
 *
 */
public class Constants {
	
	/**
	 * the host's default time out  in milliseconds.
	 */
	private static AtomicLong timeout=new AtomicLong(30000);
	/**
	 * the minimal timeout in milliseconds
	 */
	private static AtomicLong min_timeout=new AtomicLong(20000);
	
	/**
	 * once max request number from the queue
	 */
	public static long max_poll_request_num=100;
	
	/**
	 * max number handle request
	 */
	public static long max_poll_handle_num=100;
	/**
	 * the httpclient handler
	 */
	public final static String CLIENT_HANDLER="clienthandler";
	
	/**
	 * the connect method respose
	 */
	public final static HttpResponse CONNECT_RESPONSE;
	
	/**
	 * the connect method respose
	 */
	public final static HttpResponse NOAUTH_RESPONSE;
	
	/**
	 * the request port of client request
	 */
	public static  int REQUEST_PORT=4080;

	/**
	 * jetty's port
	 */
	public static  int JETTYPORT=8002;
	
	private static volatile  String REQUEST_AUTHSTRING=null;
	/**
	 * request timeout, if timeout the request is abandoned
	 */
	private volatile  static int REQUEST_TIMEOUT=60000;
	/**
	 * the max num of unhandled request
	 */
	private volatile static int MAX_UNHADNLED_REQUEST=500;
	/**
	 * the mongodb database config
	 */
	public static String MONGO_HOST="localhost";


	public static int MONGO_PORT=27017;
	private  static String mongo_user="yuanshuju";
	private  static String mongo_pass="123677";
	private  static String mongo_authticateDatabase="admin";
	public static final String MONGO_DATABASE="BeastKeeper";
	public static final String TABLE_TIMEOUT="timeout";
	public static final String TABLE_PROXY="proxy";
	public static final String TABLE_DEFALTTIMEOUT="defaulttimeout";
	
	static{
		HttpResponseStatus status=new HttpResponseStatus(405, "Don not support the connect method!");
		CONNECT_RESPONSE=new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
		CONNECT_RESPONSE.headers().add("Allow","GET,HEAD,POST,PUT,TRACE,OPTIONS,DELETE");
		CONNECT_RESPONSE.headers().add("Server","Beast Keeper 1.0");
		
		status=new HttpResponseStatus(407, "Authorization Required");
		NOAUTH_RESPONSE=new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
		NOAUTH_RESPONSE.headers().add("Proxy-Authenticate","Basic realm=\"login\"");
		NOAUTH_RESPONSE.headers().add("Server","Beast Keeper 1.0");
	}
	
	public static long getMax_poll_request_num() {
		return max_poll_request_num;
	}

	public static void setMax_poll_request_num(long max_poll_request_num) {
		Constants.max_poll_request_num = max_poll_request_num;
	}

	public static long getMax_poll_handle_num() {
		return max_poll_handle_num;
	}

	public static void setMax_poll_handle_num(long max_poll_handle_num) {
		Constants.max_poll_handle_num = max_poll_handle_num;
	}

	public static int getREQUEST_PORT() {
		return REQUEST_PORT;
	}

	public static void setREQUEST_PORT(int rEQUEST_PORT) {
		REQUEST_PORT = rEQUEST_PORT;
	}

	public static int getJETTYPORT() {
		return JETTYPORT;
	}

	public static void setJETTYPORT(int jETTYPORT) {
		JETTYPORT = jETTYPORT;
	}

	public static String getMONGO_HOST() {
		return MONGO_HOST;
	}

	public static void setMONGO_HOST(String mONGO_HOST) {
		MONGO_HOST = mONGO_HOST;
	}

	public static int getMONGO_PORT() {
		return MONGO_PORT;
	}

	public static void setMONGO_PORT(int mONGO_PORT) {
		MONGO_PORT = mONGO_PORT;
	}

	public static String getMongo_user() {
		return mongo_user;
	}

	public static void setMongo_user(String mongo_user) {
		Constants.mongo_user = mongo_user;
	}

	public static String getMongo_pass() {
		return mongo_pass;
	}

	public static void setMongo_pass(String mongo_pass) {
		Constants.mongo_pass = mongo_pass;
	}

	public static String getMongo_authticateDatabase() {
		return mongo_authticateDatabase;
	}

	public static void setMongo_authticateDatabase(String mongo_authticateDatabase) {
		Constants.mongo_authticateDatabase = mongo_authticateDatabase;
	}

	public static String getClientHandler() {
		return CLIENT_HANDLER;
	}

	public static HttpResponse getConnectResponse() {
		return CONNECT_RESPONSE;
	}

	public static String getMongoDatabase() {
		return MONGO_DATABASE;
	}

	public static String getTableTimeout() {
		return TABLE_TIMEOUT;
	}

	public static String getTableProxy() {
		return TABLE_PROXY;
	}

	public static String getTableDefalttimeout() {
		return TABLE_DEFALTTIMEOUT;
	}

	public static void setTimeout(AtomicLong timeout) {
		Constants.timeout = timeout;
	}


	public static long getTimeout() {
		return timeout.get();
	}

	public static void setTimeout(long timeout) {
		Constants.timeout.set(timeout);
	}
	public static String getREQUEST_AUTHSTRING() {
		return REQUEST_AUTHSTRING;
	}

	public static void setREQUEST_AUTHSTRING(String rEQUEST_AUTHSTRING) {
		REQUEST_AUTHSTRING = rEQUEST_AUTHSTRING;
	}

	public static int getREQUEST_TIMEOUT() {
		return REQUEST_TIMEOUT;
	}

	public static void setREQUEST_TIMEOUT(int rEQUEST_TIMEOUT) {
		REQUEST_TIMEOUT = rEQUEST_TIMEOUT;
	}
	public static void setREQUEST_TIMEOUT(String rEQUEST_TIMEOUT) {
		REQUEST_TIMEOUT = Integer.parseInt(rEQUEST_TIMEOUT);
	}

	public static int getMAX_UNHADNLED_REQUEST() {
		return MAX_UNHADNLED_REQUEST;
	}

	public static void setMAX_UNHADNLED_REQUEST(int mAX_UNHADNLED_REQUEST) {
		MAX_UNHADNLED_REQUEST = mAX_UNHADNLED_REQUEST;
	}
	public static void setMAX_UNHADNLED_REQUEST(String mAX_UNHADNLED_REQUEST) {
		MAX_UNHADNLED_REQUEST = Integer.parseInt(mAX_UNHADNLED_REQUEST);
	}

	public static AtomicLong getMin_timeout() {
		return min_timeout;
	}

	public static void setMin_timeout(AtomicLong min_timeout) {
		Constants.min_timeout = min_timeout;
	}
	
	public static void setMin_timeout(String min_timeout) {
		Constants.min_timeout.set(Long.parseLong( min_timeout));
	}


}

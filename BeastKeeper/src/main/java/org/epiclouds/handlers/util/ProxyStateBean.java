package org.epiclouds.handlers.util;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;

/**
 * proxy status
 * @author xianglong
 *
 */
public class ProxyStateBean {
	/**
	 * socket address
	 */
	private volatile String host;
	/**
	 * the port
	 */
	private volatile int port;
	/**
	 * authority info
	 */
	private volatile String authStr;

	/**
	 * the error info
	 */
	private volatile String errorInfo=null;
	/**
	 * this proxy is removed?
	 */
	private volatile boolean removed=false;

	public ProxyStateBean(String host,int port,String authStr){
		this.host=host;
		this.port=port;
		this.setAuthStr(authStr);
	}
	public boolean equals(Object o){
		ProxyStateBean pb=(ProxyStateBean)o;
		return pb.host.equals(host);
	}
	public int hashCode(){
		return host.hashCode();
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getAuthStr() {
		return authStr;
	}
	public void setAuthStr(String authStr) {
		this.authStr = authStr;
	}
	public boolean isRemoved() {
		return removed;
	}
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	
	
}

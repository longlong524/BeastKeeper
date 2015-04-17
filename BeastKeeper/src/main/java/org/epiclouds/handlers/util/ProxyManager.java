package org.epiclouds.handlers.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyManager {

	private static Map<String,ProxyStateBean> all_proxies=new ConcurrentHashMap<String,ProxyStateBean>();
	private static ConcurrentHashMap<String,LinkedBlockingQueue<ProxyStateBean>>	host_proxies=
			new ConcurrentHashMap<String,LinkedBlockingQueue<ProxyStateBean>>(); 
	
	synchronized public static boolean addProxy(ProxyStateBean psb) {
		if(psb==null) return false;
		if(all_proxies.get(psb.getHost())!=null){
			return false;
		}
		all_proxies.put(psb.getHost(), psb);
		for(String str:host_proxies.keySet()){
			host_proxies.get(str).add(psb);
		}
		return true;
	}
	synchronized public static void removeProxy(String host) {
		if(host==null) return;
		if(all_proxies.get(host)==null){
			return;
		}
		ProxyStateBean pb=all_proxies.remove(host);
		pb.setRemoved(true);
	}

	public static void addHostProxy(String host,ProxyStateBean psb) throws InterruptedException{
		if(host==null||psb==null) return;
		host_proxies.putIfAbsent(host, new LinkedBlockingQueue<ProxyStateBean>());
		LinkedBlockingQueue<ProxyStateBean> que=host_proxies.get(host);
		que.put(psb);
	}
	
	 public static ProxyStateBean getHostProxy(String host) {
		if(host==null) return null;
		host_proxies.putIfAbsent(host, new LinkedBlockingQueue<ProxyStateBean>(all_proxies.values()) );
		LinkedBlockingQueue<ProxyStateBean> que=host_proxies.get(host);
		return que.poll();
	}
	
	
	
	
	
}

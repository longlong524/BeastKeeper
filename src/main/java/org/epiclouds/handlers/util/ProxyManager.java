package org.epiclouds.handlers.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyManager {

	private static Map<String,ProxyStateBean> all_proxies=new ConcurrentHashMap<String,ProxyStateBean>();
	private static ConcurrentHashMap<String,LinkedBlockingQueue<ProxyStateBean>>	host_proxies=
			new ConcurrentHashMap<String,LinkedBlockingQueue<ProxyStateBean>>(); 
	
	synchronized public static List<ProxyStateBean> getProxyOrderByHost() {
		List<ProxyStateBean> re=new LinkedList<ProxyStateBean>();
		re.addAll(all_proxies.values());
		Collections.sort(re, new Comparator<ProxyStateBean>() {

			@Override
			public int compare(ProxyStateBean o1, ProxyStateBean o2) {
				// TODO Auto-generated method stub
				return o1.getHost().compareTo(o2.getHost());
			}
			
		});
		return re;
	}
	
	synchronized public static List<ProxyStateBean> getAllProxy() {
		List<ProxyStateBean> re=new LinkedList<ProxyStateBean>();
		re.addAll(all_proxies.values());
		return re;
	}
	synchronized public static boolean addProxy(ProxyStateBean psb) {
		if(psb==null||psb.isRemoved()) return false;
		if(all_proxies.get(psb.getHost())!=null){
			return false;
		}
		all_proxies.put(psb.getHost(), psb);
		for(String str:host_proxies.keySet()){
			host_proxies.get(str).add(psb);
		}
		return true;
	}
	synchronized public static boolean removeProxy(String host) {
		if(host==null) return false;
		if(all_proxies.get(host)==null){
			return false;
		}
		ProxyStateBean pb=all_proxies.remove(host);
		pb.setRemoved(true);
		return true;
	}
	
	
	synchronized public static boolean removeProxyByAuthStr(String authStr) {
		if(authStr==null) return false;
		List<ProxyStateBean> re=new LinkedList<ProxyStateBean>();
		re.addAll(all_proxies.values());
		for(ProxyStateBean psb:re){
			if(psb.getAuthStr().equals(authStr)){
				removeProxy(psb.getHost());
			}
		}
		return true;
	}

	public static void addHostProxy(String host,ProxyStateBean psb) throws InterruptedException{
		if(host==null||psb==null||psb.isRemoved()) return;
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

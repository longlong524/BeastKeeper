package org.epiclouds.handlers.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HostStatusManager {
	private static ConcurrentHashMap<String,HostStatusBean> all_status=new ConcurrentHashMap<String,HostStatusBean>();
	
	public  static void incrementRequestNum(String host){
		if(host==null){
			return;
		}
		all_status.putIfAbsent(host, new HostStatusBean(host));
		HostStatusBean i=all_status.get(host);
		i.incrementRequestNum();
	}
	
	public  static void incrementHandledNum(String host){
		if(host==null){
			return;
		}
		all_status.putIfAbsent(host, new HostStatusBean(host));
		HostStatusBean i=all_status.get(host);
		i.incrementHandledNum();
	}
	
	
	
	public synchronized static List<HostStatusBean> getAllHostStatusByOrder(){
		List<HostStatusBean> re=new LinkedList<HostStatusBean>(all_status.values());
		Collections.sort(re, new Comparator<HostStatusBean>() {

			@Override
			public int compare(HostStatusBean o1, HostStatusBean o2) {
				// TODO Auto-generated method stub
				return o1.getHost().compareTo(o2.getHost());
			}
		});
		return re;
	}
	
	public  static HostStatusBean getRequestNum(String host){
		if(host==null){
			return null;
		}
		all_status.putIfAbsent(host, new HostStatusBean(host));
		return all_status.get(host);
	}
	
	
}

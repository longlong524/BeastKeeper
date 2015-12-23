package org.epiclouds.handlers.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimeoutManager {
	private static Map<String,TimeOutBean> all_timeouts=new ConcurrentHashMap<String,TimeOutBean>();
	
	public  static long getHostTimout(String host){
		all_timeouts.putIfAbsent(host, new TimeOutBean(host, 0,0));
		TimeOutBean i=all_timeouts.get(host);
		if(i==null||i.getTimeout()==0){
			return Constants.getTimeout();
		}
		return i.getTimeout();
	}
	public  static long getHostMinTimout(String host){
		all_timeouts.putIfAbsent(host, new TimeOutBean(host, 0,0));
		TimeOutBean i=all_timeouts.get(host);
		if(i==null||i.getMinTimeout()==0){
			return Constants.getMin_timeout().get();
		}
		return i.getMinTimeout();
	}
	
	public  static TimeOutBean getHostTimoutBean(String host){
		all_timeouts.putIfAbsent(host, new TimeOutBean(host, 0,0));
		TimeOutBean i=all_timeouts.get(host);
		return i;
	}
	
	public static void addHostTimout(TimeOutBean host){
		all_timeouts.put(host.getHost(),host);
	}
	
	
	public synchronized static List<TimeOutBean> getAllHostTimoutByOrder(){
		List<TimeOutBean> re=new LinkedList<TimeOutBean>(all_timeouts.values());
		Collections.sort(re, new Comparator<TimeOutBean>() {

			@Override
			public int compare(TimeOutBean o1, TimeOutBean o2) {
				// TODO Auto-generated method stub
				return o1.getHost().compareTo(o2.getHost());
			}
		});
		return re;
	}
	
	public  static boolean updateHostTimout(String host,long timeout){
		all_timeouts.putIfAbsent(host, new TimeOutBean(host, 0,0));
		TimeOutBean i=all_timeouts.get(host);
		i.setTimeout(timeout);
		return true;
	}
	public  static boolean updateMinHostTimout(String host,long timeout){
		all_timeouts.putIfAbsent(host, new TimeOutBean(host, 0,0));
		TimeOutBean i=all_timeouts.get(host);
		i.setMinTimeout(timeout);
		return true;
	}
	
	
}

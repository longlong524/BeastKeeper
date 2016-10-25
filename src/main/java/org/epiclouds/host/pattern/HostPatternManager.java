package org.epiclouds.host.pattern;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.epiclouds.handlers.util.Constants;
import org.epiclouds.handlers.util.MongoManager;
import org.epiclouds.handlers.util.StorageBean;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class HostPatternManager {
	public static final String PATTERN="pattern";
	private static HostPatternManager manager=new HostPatternManager();
	
	private ConcurrentHashMap<String,String> patternSet=new ConcurrentHashMap<String,String>();
	
	public synchronized void deletePattern(String pattern){
		patternSet.remove(pattern);
		StorageBean sb=new StorageBean();
		sb.setDbstr(Constants.MONGO_DATABASE);
		sb.setTablestr(Constants.TABLE_PATTERN);
		BasicDBObject oo=new BasicDBObject();
		oo.put(HostPatternManager.PATTERN,pattern);
		sb.setCondition(oo);
		MongoManager.delete(sb);
	}
	
	public synchronized void addPattern(String pattern){
		patternSet.put(pattern, PATTERN);
		StorageBean sb=new StorageBean();
		sb.setDbstr(Constants.MONGO_DATABASE);
		sb.setTablestr(Constants.TABLE_PATTERN);
		BasicDBObject oo=new BasicDBObject();
		oo.put(HostPatternManager.PATTERN,pattern);
		sb.setCondition(oo);
		sb.setData(oo);
		MongoManager.UpOrInsert(sb);
	}
	
	public synchronized List<String> getAllPatterns(){
		List<String> re=new LinkedList<String>();
		re.addAll(patternSet.keySet());
		return re;
	}
	public synchronized String getClosestMatchString(String str){
		String re=null;
		int match=-1;
		for(String pattern:patternSet.keySet()){
			int len=StringCompare.isMatch(str, pattern);
			if(len>=0){
				if(match<len){
					match=len;
					re=pattern;
				}
			}
		}
		return re;
	}

	public static HostPatternManager getManager() {
		return manager;
	}

}

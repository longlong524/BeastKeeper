/**
 * @author Administrator
 * @created 2014 2014�?12�?8�? 下午1:30:30
 * @version 1.0
 */
package org.epiclouds.handlers.util;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.epiclouds.client.main.MainRun;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * @author Administrator
 *
 */
public class MongoManager {
	private static MongoClient client;
	static{
			try {
				MongoCredential credential = MongoCredential.createCredential(Constants.getMongo_user(),
						Constants.getMongo_authticateDatabase(), Constants.getMongo_pass().toCharArray());
				client = new MongoClient(new ServerAddress(Constants.MONGO_HOST	,Constants.MONGO_PORT), 
						Arrays.asList(credential));
			} catch (UnknownHostException e) {
				MainRun.mainlogger.error(e.getLocalizedMessage(), e);
			}
	}
	/**
	 * insert operation
	 * @param sb
	 */
	public static void UpOrInsert(StorageBean sb){
		DB db=client.getDB(sb.getDbstr());
		DBCollection col=db.getCollection(sb.getTablestr());
		if(sb.getCondition()!=null){
			col.update(sb.getCondition(),sb.getData(), true, false);
		}else{
			col.insert(sb.getData());
		}
	}
	/**
	 * delete operation
	 * @param sb
	 */
	public static void delete(StorageBean sb){
		DB db=client.getDB(sb.getDbstr());
		DBCollection col=db.getCollection(sb.getTablestr());
		col.remove(sb.getCondition());
	}
	/**
	 * 
	 * @param sb
	 */
	public static List<DBObject> find(StorageBean sb){
		DB db=client.getDB(sb.getDbstr());
		DBCollection col=db.getCollection(sb.getTablestr());
		DBCursor cur=col.find(sb.getCondition());
		List<DBObject> re=new LinkedList<>();
		while(cur.hasNext()){
			re.add(cur.next());
		}
		return re;
	}

	
}

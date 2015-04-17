/**
 * @author Administrator
 * @created 2014 2014年8月27日 下午3:04:28
 * @version 1.0
 */
package org.epiclouds.client.main;

/**
 * @author Administrator
 *
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.epiclouds.handlers.util.ChannelManager;
import org.epiclouds.handlers.util.Constants;
import org.epiclouds.handlers.util.MongoManager;
import org.epiclouds.handlers.util.ProxyManager;
import org.epiclouds.handlers.util.ProxyStateBean;
import org.epiclouds.handlers.util.StorageBean;
import org.epiclouds.handlers.util.TimeOutBean;
import org.epiclouds.handlers.util.TimeoutManager;
import org.epiclouds.netty.NettyHttpClient;
import org.epiclouds.netty.NettyHttpServer;
import org.epiclouds.spiders.webconsole.Login;
import org.epiclouds.spiders.webconsole.Logout;
import org.epiclouds.spiders.webconsole.AddProxy;
import org.epiclouds.spiders.webconsole.RemoveProxy;
import org.epiclouds.spiders.webconsole.UpdateDefaultTimeOut;
import org.epiclouds.spiders.webconsole.UpdateTimeOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Discards any incoming data.
 */
public class MainRun {

	public  static Logger mainlogger = LoggerFactory.getLogger(MainRun.class);

   public static final int port=4080;
   public static final int INNERPORT=8002;

   public static void main(String[] args) throws Exception{
	   //initProxyFromFile(Constants.PROXYFILE);
	   initProxyFromMongo();
	   initTimeoutFromMongo();
	   initDefaultTimeoutFromMongo();
	   startInnerJetty();
	   NettyHttpClient client=new NettyHttpClient();
	   ChannelManager manager=new ChannelManager(client);
	   NettyHttpServer server=new NettyHttpServer(port, manager); 
	   manager.start();
	   
   }

/*	private static void initProxyFromFile(String file) throws NumberFormatException, IOException, InterruptedException {
		BufferedReader reader=new BufferedReader(new FileReader(file));
		String tmp=null;
		while((tmp=reader.readLine())!=null){
			String[] ip_port=tmp.split(":");
			ProxyManager.addProxy(new ProxyStateBean(ip_port[0], 
					Integer.parseInt(ip_port[1]),ip_port.length==2?null:
					(ip_port.length>2?ip_port[2]:"")+":"+(ip_port.length>3?ip_port[3]:"")));
		}
		reader.close();
	}*/
	
	private static void initProxyFromMongo() throws NumberFormatException, IOException, InterruptedException {
		StorageBean sb=new StorageBean();
		sb.setDbstr(Constants.MONGO_DATABASE);
		sb.setTablestr(Constants.TABLE_PROXY);
		sb.setCondition(new BasicDBObject());
		List<DBObject> re=MongoManager.find(sb);
		for(DBObject db:re){
			ProxyManager.addProxy(new ProxyStateBean((String)db.get("host"), 
					(Integer)db.get("port"),(String)db.get("authStr")));
		}
	}
	
	
	private static void initTimeoutFromMongo() throws NumberFormatException, IOException, InterruptedException {
		StorageBean sb=new StorageBean();
		sb.setDbstr(Constants.MONGO_DATABASE);
		sb.setTablestr(Constants.TABLE_TIMEOUT);
		sb.setCondition(new BasicDBObject());
		List<DBObject> re=MongoManager.find(sb);
		for(DBObject db:re){
			TimeoutManager.addHostTimout(new TimeOutBean((String)db.get("host"), 
					(Long)db.get("timeout")));
		}
	}
	private static void initDefaultTimeoutFromMongo() throws NumberFormatException, IOException, InterruptedException {
		StorageBean sb=new StorageBean();
		sb.setDbstr(Constants.MONGO_DATABASE);
		sb.setTablestr(Constants.TABLE_DEFALTTIMEOUT);
		sb.setCondition(new BasicDBObject());
		List<DBObject> re=MongoManager.find(sb);
		for(DBObject db:re){
			Constants.setTimeout((Long)db.get("timeout"));	
		}
	}
	
	/**
	 * start the innner server
	 * 
	 * @throws Exception
	 */
	public static void startInnerJetty() throws Exception {
		mainlogger.info("Start web console in " + INNERPORT);
		Server server = new Server();
		try {

			Connector connector = new SelectChannelConnector();
			connector.setPort(INNERPORT);
			server.setConnectors(new Connector[] { connector });
			WebAppContext webapp = new WebAppContext();
			webapp.setContextPath("/");// url is /jettytest
			webapp.setResourceBase("./WebRoot");// the folder
			webapp.addServlet(Login.class, "/login");
			webapp.addServlet(AddProxy.class, "/addProxy");
			webapp.addServlet(RemoveProxy.class, "/removeProxy");
			webapp.addServlet(UpdateTimeOut.class, "/updateTimeOut");
			webapp.addServlet(UpdateDefaultTimeOut.class, "/updateDefaultTimeOut");
			webapp.addServlet(Logout.class, "/logout");
			/*webapp.addServlet(new ServletHolder(new GetSourceType()),
			"/getSourceType");*/

			server.setHandler(webapp);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
			mainlogger.error("start web console error!", e);
			System.exit(1);
		}
		mainlogger.info("Start web console finished");
	}


}

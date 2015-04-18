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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

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
import org.epiclouds.spiders.webconsole.AddProxy;
import org.epiclouds.spiders.webconsole.Login;
import org.epiclouds.spiders.webconsole.Logout;
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



   public static void main(String[] args) throws Exception{
	   //initProxyFromFile(Constants.PROXYFILE);
	   initProxyFromMongo();
	   initTimeoutFromMongo();
	   initDefaultTimeoutFromMongo();
	   startInnerJetty();
	   NettyHttpClient client=new NettyHttpClient();
	   ChannelManager manager=new ChannelManager(client);
	   NettyHttpServer server=new NettyHttpServer(Constants.REQUEST_PORT, manager); 
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
   /**
    * get config from file
 * @throws IOException 
 * @throws FileNotFoundException 
    */
	private static void getConfig() throws FileNotFoundException, IOException{
		Properties pros=new Properties();
		pros.load(new FileInputStream("config"));
		Constants.REQUEST_TIMEOUT=Integer.parseInt(pros.getProperty("request_timeout", "30000"));
		Constants.REQUEST_PORT=Integer.parseInt(pros.getProperty("request_port", "4080"));
		Constants.JETTYPORT=Integer.parseInt(pros.getProperty("jettyport", "8002"));
		Constants.MONGO_HOST=(pros.getProperty("mongo_host", "localhost"));
		Constants.MONGO_PORT=Integer.parseInt(pros.getProperty("mongo_port", "27017"));
	}
   
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
		mainlogger.info("Start web console in " + Constants.JETTYPORT);
		Server server = new Server();
		try {

			Connector connector = new SelectChannelConnector();
			connector.setPort(Constants.JETTYPORT);
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

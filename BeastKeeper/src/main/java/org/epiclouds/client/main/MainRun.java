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
import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.epiclouds.handlers.util.ChannelManager;
import org.epiclouds.handlers.util.ProxyManager;
import org.epiclouds.handlers.util.ProxyStateBean;
import org.epiclouds.netty.NettyHttpClient;
import org.epiclouds.netty.NettyHttpServer;
import org.epiclouds.spiders.webconsole.Login;
import org.epiclouds.spiders.webconsole.Logout;
import org.epiclouds.spiders.webconsole.AddProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discards any incoming data.
 */
public class MainRun {

	public  static Logger mainlogger = LoggerFactory.getLogger(MainRun.class);

   public static final int port=4080;
   public static final int INNERPORT=8002;

   public static void main(String[] args) throws Exception{
	   initProxyFromFile("valid_proxy");
	   NettyHttpClient client=new NettyHttpClient();
	   ChannelManager manager=new ChannelManager(client);
	   NettyHttpServer server=new NettyHttpServer(port, manager); 
	   manager.start();
	   
   }

	private static void initProxyFromFile(String file) throws NumberFormatException, IOException, InterruptedException {
		// TODO Auto-generated method stub
		BufferedReader reader=new BufferedReader(new FileReader(file));
		String tmp=null;
		while((tmp=reader.readLine())!=null){
			String[] ip_port=tmp.split(":");
			ProxyManager.addProxy(new ProxyStateBean(ip_port[0], 
					Integer.parseInt(ip_port[1]),ip_port.length==2?null:
					(ip_port.length>2?ip_port[2]:"")+":"+(ip_port.length>3?ip_port[3]:"")));
		}
		reader.close();
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
			webapp.addServlet(AddProxy.class, "/addEbaySpider");
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

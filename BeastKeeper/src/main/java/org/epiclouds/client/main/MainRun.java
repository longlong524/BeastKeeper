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

import org.epiclouds.handlers.util.ChannelManager;
import org.epiclouds.handlers.util.ProxyManager;
import org.epiclouds.handlers.util.ProxyStateBean;
import org.epiclouds.netty.NettyHttpClient;
import org.epiclouds.netty.NettyHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discards any incoming data.
 */
public class MainRun {

	public  static Logger mainlogger = LoggerFactory.getLogger(MainRun.class);

   public static final int port=4080;

   public static void main(String[] args) throws Exception{
	   initProxyFromFile("valid_ebay");
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

}

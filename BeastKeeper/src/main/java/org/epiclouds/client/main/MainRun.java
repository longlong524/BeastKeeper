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
import org.epiclouds.handlers.util.ChannelManager;
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
	   NettyHttpClient client=new NettyHttpClient();
	   ChannelManager manager=new ChannelManager(client);
	   NettyHttpServer server=new NettyHttpServer(port, manager);
	   manager.start();
	   
   }

}

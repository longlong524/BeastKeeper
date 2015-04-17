/**
 * @author Administrator
 * @created 2014 2014年8月27日 下午3:04:28
 * @version 1.0
 */
package org.epiclouds.netty;

/**
 * @author Administrator
 *
 */
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import org.epiclouds.client.netty.handler.NettyHttpServerHandler;
import org.epiclouds.handlers.util.ChannelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discards any incoming data.
 */
public class NettyHttpServer {

	public  static Logger mainlogger = LoggerFactory.getLogger(NettyHttpServer.class);

    private EventLoopGroup workers=new NioEventLoopGroup();
    private EventLoopGroup boss=new NioEventLoopGroup();

    public  NettyHttpServer(int port,final ChannelManager manager) throws Exception {   
    	ServerBootstrap sb=new ServerBootstrap();
	        sb.group(boss,workers).channel(NioServerSocketChannel.class).
	        option(ChannelOption.SO_KEEPALIVE, true).
	        option(ChannelOption.TCP_NODELAY, true).
	        childOption(ChannelOption.TCP_NODELAY, true)
	        .childOption(ChannelOption.SO_KEEPALIVE, true)
	        .option(ChannelOption.SO_BACKLOG, 128).
	        childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					
					 ChannelPipeline pipeline = ch.pipeline();
				        /**
				         * http-response解码器
				         * http服务器端对response解码
				         */
					 	
				        pipeline.addLast( new HttpRequestDecoder());
				        /**
				         * 压缩
				         * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
				         * while respecting the "Accept-Encoding" header.
				         * If there is no matching encoding, no compression is done.
				         */
				       
				       pipeline.addLast("aggregator", new HttpObjectAggregator(1048576*1024));
				        pipeline.addLast(new HttpResponseEncoder());
				        pipeline.addLast("redeflater", new HttpContentCompressor());
				        /**
				         * http服务器端对request编码
				         */
				       pipeline.addLast(new NettyHttpServerHandler(manager));
				       
				        
				}	
	        });
	        ChannelFuture f = sb.bind(port).sync(); // (7)
	        System.out.println("server started in port:"+port);
    }
    
   
    
    public void close(){
    	workers.shutdownGracefully();
    }


}

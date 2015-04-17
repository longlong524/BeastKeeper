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
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.epiclouds.client.netty.handler.NettyHttpClientHandler;
import org.epiclouds.client.netty.handler.SimpleEncoder;
import org.epiclouds.handlers.util.BPChannel;
import org.epiclouds.handlers.util.BPRequest;
import org.epiclouds.handlers.util.ChannelManager;
import org.epiclouds.handlers.util.Constants;
import org.epiclouds.handlers.util.ProxyManager;
import org.epiclouds.handlers.util.ProxyStateBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discards any incoming data.
 */
public class NettyHttpClient {

	public  static Logger mainlogger = LoggerFactory.getLogger(NettyHttpClient.class);

    private Bootstrap sb=new Bootstrap();
    private EventLoopGroup workers=new NioEventLoopGroup();

    public  NettyHttpClient() throws Exception {   
	        sb.group(workers).channel(NioSocketChannel.class).
	        option(ChannelOption.SO_KEEPALIVE, true).handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					
					 ChannelPipeline pipeline = ch.pipeline();
				        /**
				         * http-response解码器
				         * http服务器端对response解码
				         */
					 //pipeline.addLast(new SimpleEncoder());
				       pipeline.addLast(new HttpResponseDecoder());

				       
				        pipeline.addLast("redeflater", new HttpContentDecompressor());
				       pipeline.addLast("aggregator", new HttpObjectAggregator(1048576*1024));
				        /**
				         * http服务器端对request编码
				         */
				        pipeline.addLast( new HttpRequestEncoder());

				        /**
				         * 压缩
				         * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
				         * while respecting the "Accept-Encoding" header.
				         * If there is no matching encoding, no compression is done.
				         */
				       //pipeline.addLast("deflater", new HttpContentCompressor());
				       
				        
				}	
	        });
	        System.out.println("client started");
    }
    
    public void connect(final ProxyStateBean psb,final BPRequest request,final ChannelManager manager){
    	ChannelFuture cf=sb.connect(psb.getHost(), psb.getPort());
    	cf.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future)
					throws Exception {
				// TODO Auto-generated method stub
				if(future.isSuccess()){
					Channel n=((DefaultChannelPromise) future).channel();
					BPChannel ch=new BPChannel(request.getHost(), n, psb,manager);
					n.pipeline().addLast(Constants.CLIENT_HANDLER, new NettyHttpClientHandler(ch,request));
					if(psb.getAuthStr()!=null){
						request.getRequest().headers().add("Proxy-Authorization", "Basic "
							+new sun.misc.BASE64Encoder().encode(psb.getAuthStr().getBytes()));
					}
					n.writeAndFlush(request.getRequest());
				}else{
					psb.setErrorInfo(future.cause().toString());
					ProxyManager.addHostProxy(request.getHost(), psb);
					manager.putRequestBack(request);
				}
			}
    		
		});
    }
    
    public void close(){
    	workers.shutdownGracefully();
    }


}

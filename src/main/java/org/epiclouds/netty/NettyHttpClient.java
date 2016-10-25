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
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

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
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.epiclouds.client.netty.handler.NettyHttpClientHandler;
import org.epiclouds.client.netty.handler.SimpleEncoder;
import org.epiclouds.handlers.util.BPChannel;
import org.epiclouds.handlers.util.BPChannel.WHERE;
import org.epiclouds.handlers.util.BPRequest;
import org.epiclouds.handlers.util.ChannelManager;
import org.epiclouds.handlers.util.Constants;
import org.epiclouds.handlers.util.ProxyManager;
import org.epiclouds.handlers.util.ProxyStateBean;
import org.epiclouds.handlers.util.RecoverChannelManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discards any incoming data.
 */
public class NettyHttpClient {

	public  static Logger mainlogger = LoggerFactory.getLogger(NettyHttpClient.class);

    private Bootstrap sb=new Bootstrap();
    private EventLoopGroup workers=new NioEventLoopGroup(Runtime.getRuntime()
			.availableProcessors(), Executors.newFixedThreadPool(Runtime
			.getRuntime().availableProcessors(), new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "beastkeeper");
		}
	}));

    public  NettyHttpClient() throws Exception {   
	        sb.group(workers).channel(NioSocketChannel.class).
	        option(ChannelOption.SO_KEEPALIVE, true).handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {    
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
				if(future.isSuccess()&&!psb.isRemoved()){
					Channel n=((ChannelFuture) future).channel();
					BPChannel ch=new BPChannel(request.getHost(), n, psb,manager,WHERE.FREEQUEUE);
					 ChannelPipeline pipeline = n.pipeline();
				        /**
				         * http-response解码器
				         * http服务器端对response解码
				         */
					 //pipeline.addLast(new SimpleEncoder());
				       pipeline.addLast(new HttpResponseDecoder());
				       n.pipeline().addLast("readtimeouthandler",new ReadTimeoutHandler(Constants.getREQUEST_TIMEOUT(),TimeUnit.MILLISECONDS));
				       
				        pipeline.addLast("redeflater", new HttpContentDecompressor());
				       pipeline.addLast("aggregator", new HttpObjectAggregator(1048576*1024));

				        /**
				         * http服务器端对request编码
				         */
				    pipeline.addLast( new HttpRequestEncoder());
				   
					if(request.getRequest().headers().contains(Constants.GETPROXYHEADER)){
						ch.setWh(WHERE.RECOVERQUEUE);
						pipeline.addLast(Constants.CLIENT_HANDLER, new NettyHttpClientHandler(ch,null));
						RecoverChannelManager.sendProxyToChannel(ch, request); 
						return;
					}
					pipeline.addLast(Constants.CLIENT_HANDLER, new NettyHttpClientHandler(ch,request));
					if(psb.getAuthStr()!=null){
						request.getRequest().headers().add("Proxy-Authorization", "Basic "
							+new String(Base64.getEncoder().encode(psb.getAuthStr().getBytes("utf-8")),"utf-8"));
					}
					n.writeAndFlush(request.getRequest());
					psb.setErrorInfo(null);
				}else{
					Channel n=((ChannelFuture) future).channel();
					n.close();
					if(psb.isRemoved()){
						return;
					}
					Thread.sleep(5);
					psb.setErrorInfo(new DateTime().toString("yyyy-MM-dd HH:mm:ss")+future.cause().toString());
					ChannelFuture cf=sb.connect(psb.getHost(), psb.getPort());
			    	cf.addListener(new GenericFutureListener<Future<? super Void>>() {

						@Override
						public void operationComplete(Future<? super Void> future)
								throws Exception {
							// TODO Auto-generated method stub
							if(future.isSuccess()&&!psb.isRemoved()){
								Channel n=((ChannelFuture) future).channel();
								BPChannel ch=new BPChannel(request.getHost(), n, psb,manager,WHERE.FREEQUEUE);
								 ChannelPipeline pipeline = n.pipeline();
							        /**
							         * http-response解码器
							         * http服务器端对response解码
							         */
								 //pipeline.addLast(new SimpleEncoder());
							       pipeline.addLast(new HttpResponseDecoder());
							       n.pipeline().addLast("readtimeouthandler",new ReadTimeoutHandler(Constants.getREQUEST_TIMEOUT(),TimeUnit.MILLISECONDS));
							       
							        pipeline.addLast("redeflater", new HttpContentDecompressor());
							       pipeline.addLast("aggregator", new HttpObjectAggregator(1048576*1024));

							        /**
							         * http服务器端对request编码
							         */
							    pipeline.addLast( new HttpRequestEncoder());
							    pipeline.addLast(Constants.CLIENT_HANDLER, new NettyHttpClientHandler(ch,null));
								psb.setErrorInfo(null);
								manager.addBPChnnelToFreeQueue(ch);
							}else{
								if(psb.isRemoved()){
									return;
								}
								Channel n=((ChannelFuture) future).channel();
								n.close();
								Thread.sleep(5);
								psb.setErrorInfo(new DateTime().toString("yyyy-MM-dd HH:mm:ss")+future.cause().toString());
								sb.connect(psb.getHost(), psb.getPort()).addListener(this);
							}
						}
			    		
					});
					manager.addBPRequest(request);
				}
			}
    		
		});
    }
    
    public void close(){
    	workers.shutdownGracefully();
    }


}

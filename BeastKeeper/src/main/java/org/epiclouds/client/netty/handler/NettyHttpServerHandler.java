package org.epiclouds.client.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.net.SocketAddress;

import org.epiclouds.handlers.util.BPRequest;
import org.epiclouds.handlers.util.ChannelManager;


/**
 * @author Administrator
 *
 */
public class NettyHttpServerHandler extends ChannelHandlerAdapter{

	private ChannelManager manager;
	public NettyHttpServerHandler(ChannelManager manager){
		this.manager=manager;
	}
	@Override
	public boolean isSharable() {
		// TODO Auto-generated method stub
		return super.isSharable();
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.handlerAdded(ctx);
		
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.handlerRemoved(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		ctx.close();
	}





	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		manager.addBPRequest(new BPRequest(ctx.channel(),(FullHttpRequest)msg));
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelRegistered(ctx);
	}

	
	


	

	
}

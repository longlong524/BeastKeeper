package org.epiclouds.client.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;

import java.net.SocketAddress;

import org.epiclouds.handlers.util.BPRequest;
import org.epiclouds.handlers.util.ChannelManager;
import org.epiclouds.handlers.util.Constants;
import org.epiclouds.handlers.util.HostStatusManager;


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
		FullHttpRequest res=(FullHttpRequest)msg;
		if(res.method().compareTo(HttpMethod.CONNECT)==0){
			ctx.channel().writeAndFlush(Constants.CONNECT_RESPONSE);
			ctx.close();
			return;
		}
		FullHttpRequest re=(FullHttpRequest)msg;
		if(re.headers().get("Host")!=null){
			HostStatusManager.incrementRequestNum(re.headers().get("Host")+"");
		}
		manager.addBPRequest(new BPRequest(ctx.channel(),re));
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelRegistered(ctx);
	}

	
	


	

	
}

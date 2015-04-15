package org.epiclouds.client.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.epiclouds.handlers.util.BPChannel;
import org.epiclouds.handlers.util.BPRequest;
import org.epiclouds.handlers.util.Constants;

/**
 * @author Administrator
 *
 */
public class NettyHttpClientHandler extends ChannelHandlerAdapter{
	final private BPChannel bp;
	private BPRequest request;
	private volatile boolean active=true;
	private volatile long time=System.currentTimeMillis();
	public NettyHttpClientHandler(BPChannel bp,BPRequest request){
		this.bp=bp;
		this.request=request;
	}
	public BPChannel getBp() {
		return bp;
	}

	public BPRequest getRequest() {
		return request;
	}
	public void setRequest(BPRequest request) {
		this.request = request;
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
		cause.printStackTrace();
		super.exceptionCaught(ctx, cause);
	}




	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.err.println("channel active");
		super.channelActive(ctx);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.err.println("channel inactive");
		super.channelInactive(ctx);
	}
	@Override
	public void disconnect(final ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		System.err.println("channel disconnected");
		if(!active){
			super.disconnect(ctx, promise);
			return;
		}
		if(this.getBp().getPsb().isRemoved()&&active){
			this.bp.getCm().removeBPChnnelFromFreeQueue(bp);
			active=false;
			return;
		}
		this.bp.getCm().removeBPChnnelFromFreeQueue(bp);
		ChannelFuture cf=ctx.channel().connect(ctx.channel().remoteAddress());
		cf.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future)
					throws Exception {
				if(future.isSuccess()){
					Channel n=((DefaultChannelPromise) future).channel();
					NettyHttpClientHandler.this.bp.setCh(n);
					if(request!=null){
						n.write(request.getRequest());
					}
					NettyHttpClientHandler.this.bp.getCm().addBPChnnelToFreeQueue(bp);
				}else{
					ChannelFuture cf2=NettyHttpClientHandler.this.bp.getCh().connect(NettyHttpClientHandler.this.bp.getCh().remoteAddress());
					cf2.addListener(this);
				}
			}
			
		});
		// TODO Auto-generated method stub
		super.disconnect(ctx, promise);
	}
	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		// TODO Auto-generated method stub
		System.err.println("channel close");
		if(!active){
			super.close(ctx, promise);
			return;
		}
		if(this.getBp().getPsb().isRemoved()&&active){
			this.bp.getCm().removeBPChnnelFromFreeQueue(bp);
			active=false;
			return;
		}
		
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		// TODO Auto-generated method stub
		this.time=System.currentTimeMillis();
		super.write(ctx, msg, promise);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(!active){
			return;
		}
		if(System.currentTimeMillis()-time>=Constants.request_time){
			this.request=null;
			return;
		}
		FullHttpResponse res=(FullHttpResponse)msg;
		request.getCh().write(res);
		request=null;
		if(this.getBp().getPsb().isRemoved()&&active){
			ctx.channel().close();
			this.bp.getCm().removeBPChnnelFromFreeQueue(bp);
			active=false;
			return;
		}
		this.getBp().setVisit_time(System.currentTimeMillis());
		this.bp.getCm().addBPChnnelToFreeQueue(bp);
	}

	
	


	

	
}

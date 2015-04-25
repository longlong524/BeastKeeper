package org.epiclouds.client.netty.handler;

import java.net.SocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.epiclouds.client.main.MainRun;
import org.epiclouds.handlers.util.BPChannel;
import org.epiclouds.handlers.util.BPRequest;
import org.epiclouds.handlers.util.Constants;
import org.epiclouds.handlers.util.HostStatusManager;
import org.epiclouds.handlers.util.TimeoutManager;

/**
 * @author Administrator
 *
 */
public class NettyHttpClientHandler extends ChannelHandlerAdapter{
	final private BPChannel bp;
	private volatile BPRequest request;
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
		if(bp!=null){
			bp.getPsb().setErrorInfo(cause.toString());
		}
		return;
	}




	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//System.err.println("channel Active");
		super.channelActive(ctx);
		ctx.flush();
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//System.err.println("channel Inactive:"+this.bp.getCm().getSizeOfHostChannel(this.bp.getHost()));
		super.channelInactive(ctx);
		if(!active){
			return;
		}
		if(this.getBp().getPsb().isRemoved()&&active){
			this.bp.getCm().removeBPChnnelFromFreeQueue(bp);
			active=false;
			return;
		}
		this.bp.getCm().removeBPChnnelFromFreeQueue(bp);
		ctx.channel().close();
		NioSocketChannel nchannel=new NioSocketChannel();
		ctx.channel().eventLoop().register(nchannel);
		ChannelFuture cf=nchannel.connect(ctx.channel().remoteAddress());
		SocketAddress remote_addr=ctx.channel().remoteAddress();
		cf.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future)
					throws Exception {
				Channel n=((DefaultChannelPromise) future).channel();
				if(future.isSuccess()){
					NettyHttpClientHandler.this.bp.setCh(n);
					 n.pipeline().addLast(new HttpResponseDecoder());

				       
				       n. pipeline().addLast("redeflater", new HttpContentDecompressor());
				       n.pipeline().addLast("aggregator", new HttpObjectAggregator(1048576*1024));
				        /**
				         * http服务器端对request编码
				         */
				        n.pipeline().addLast( new HttpRequestEncoder());
					n.pipeline().addLast(Constants.CLIENT_HANDLER, new NettyHttpClientHandler(
							NettyHttpClientHandler.this.bp,null));
					NettyHttpClientHandler.this.bp.getCm().addBPChnnelToFreeQueue(bp);
				}else{
					Thread.sleep(10);
					MainRun.mainlogger.error(future.cause().getLocalizedMessage(), future.cause());
					if(bp!=null){
						bp.getPsb().setErrorInfo(future.cause().toString());
					}
					NioSocketChannel nchannel=new NioSocketChannel();
					n.eventLoop().register(nchannel);
					ChannelFuture cf2=nchannel.connect(NettyHttpClientHandler.this.bp.getCh().remoteAddress());
					cf2.addListener(this);
				}
			}
			
		});
	}
	@Override
	public void disconnect(final ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		super.disconnect(ctx, promise);
	}
	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		// TODO Auto-generated method stub
		//System.err.println("channel close");
		super.close(ctx, promise);
		if(!active){
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
		FullHttpRequest re=(FullHttpRequest)msg;
		if(re.headers().get("Host")!=null){
			HostStatusManager.incrementHandledNum(re.headers().get("Host")+"");
		}
		//System.err.println(msg);
		super.write(ctx, msg, promise);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		//System.err.println("channelRead");
		if(!active){
			return;
		}
		FullHttpResponse res=(FullHttpResponse)msg;
		//System.err.println(res.status());
		if(System.currentTimeMillis()-time>=Constants.REQUEST_TIMEOUT){
			if(bp!=null){
				bp.getPsb().setErrorInfo(request.getHost()+" timeout");
			}
			this.request=null;
			this.bp.setVisit_time(System.currentTimeMillis());
			this.bp.getCm().addBPChnnelToFreeQueue(bp);
			return;
		}
		if(request.getCh().isActive()){
			request.getCh().writeAndFlush(res);
		}
		request=null;
		if(this.getBp().getPsb().isRemoved()&&active){
			ctx.channel().close();
			this.bp.getCm().removeBPChnnelFromFreeQueue(bp);
			active=false;
			return;
		}
		this.bp.setVisit_time(System.currentTimeMillis());
		this.bp.getCm().addBPChnnelToFreeQueue(bp);
	}

	
	


	

	
}

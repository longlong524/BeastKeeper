package org.epiclouds.client.netty.handler;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.epiclouds.client.main.MainRun;
import org.epiclouds.handlers.util.BPChannel;
import org.epiclouds.handlers.util.BPChannel.WHERE;
import org.epiclouds.handlers.util.BPRequest;
import org.epiclouds.handlers.util.Constants;
import org.epiclouds.handlers.util.HostStatusManager;
import org.epiclouds.handlers.util.TimeoutManager;
import org.joda.time.DateTime;

/**
 * @author Administrator
 *
 */
public class NettyHttpClientHandler extends ChannelHandlerAdapter{
	private final BPChannel bp;
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
		cause.printStackTrace();
		if(bp!=null){
			bp.getPsb().setErrorInfo(new DateTime().toString("yyyy-MM-dd HH:mm:ss")+cause.toString());
		}
		ctx.close();
		return;
	}




	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//System.err.println("channel Active");
		super.channelActive(ctx);
		ctx.flush();
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//System.err.println("channel Inactive:"+this.bp.getCm().getSizeOfHostChannel(this.bp.getHost()));
		ctx.close();
		if(!active){
			return;
		}
		if(this.getBp().getPsb().isRemoved()&&active){
			if(this.bp.getWh()==WHERE.FREEQUEUE){
				this.bp.getCm().removeBPChnnelFromFreeQueue(bp);
			}
			if(this.bp.getWh()==WHERE.RECOVERQUEUE){
				this.bp.getCm().removeBPChnnelFromRecoverQueue(bp);
			}
			active=false;
			return;
		}
		if(this.bp.getWh()==WHERE.FREEQUEUE){
			this.bp.getCm().removeBPChnnelFromFreeQueue(bp);
		}
		if(this.bp.getWh()==WHERE.RECOVERQUEUE){
			this.bp.getCm().removeBPChnnelFromRecoverQueue(bp);
		}
		ctx.channel().close();
		NioSocketChannel nchannel=new NioSocketChannel();
		ctx.channel().eventLoop().register(nchannel);
		final InetSocketAddress remoteAddr=	new InetSocketAddress(bp.getPsb().getHost(),bp.getPsb().getPort());
		ChannelFuture cf=nchannel.connect(remoteAddr);
		cf.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future)
					throws Exception {
				Channel n=((DefaultChannelPromise) future).channel();
				if(future.isSuccess()){
					NettyHttpClientHandler.this.bp.setCh(n);
					 n.pipeline().addLast(new HttpResponseDecoder());

					 n.pipeline().addLast("readtimeouthandler",new ReadTimeoutHandler(Constants.getREQUEST_TIMEOUT(),TimeUnit.MILLISECONDS));
				       n. pipeline().addLast("redeflater", new HttpContentDecompressor());
				       n.pipeline().addLast("aggregator", new HttpObjectAggregator(1048576*1024));
				        /**
				         * http服务器端对request编码
				         */
				      n.pipeline().addLast( new HttpRequestEncoder());
					n.pipeline().addLast(Constants.CLIENT_HANDLER, new NettyHttpClientHandler(
							NettyHttpClientHandler.this.bp,null));
					
					
					if(NettyHttpClientHandler.this.bp.getWh()==WHERE.FREEQUEUE){
						NettyHttpClientHandler.this.bp.getCm().addBPChnnelToFreeQueue(bp);
					}
					if(NettyHttpClientHandler.this.bp.getWh()==WHERE.RECOVERQUEUE){
						NettyHttpClientHandler.this.bp.getCm().addBPChnnelToRecoverQueue(bp);
					}
					if(bp!=null){
						bp.getPsb().setErrorInfo(null);
					}
				}else{
					Thread.sleep(10);
					MainRun.mainlogger.error(future.cause().getLocalizedMessage(), future.cause());
					if(bp!=null){
						bp.getPsb().setErrorInfo(new DateTime().toString("yyyy-MM-dd HH:mm:ss")+future.cause().toString());
					}
					NioSocketChannel nchannel=new NioSocketChannel();
					n.eventLoop().register(nchannel);
					ChannelFuture cf2=nchannel.connect(new InetSocketAddress(
							remoteAddr.getHostString(),
							remoteAddr.getPort()));
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
		this.time=System.currentTimeMillis();
		FullHttpRequest re=(FullHttpRequest)msg;
		if(re.headers().getInt("Content-Length")==0&&re.method()==HttpMethod.GET){
			re.headers().remove("Content-Length");
		}
		//System.err.println(msg);
		super.write(ctx, msg, promise);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		//System.err.println("channelRead");
			if(!active){
				ReferenceCountUtil.release(msg);
				return;
			}
			FullHttpResponse res=(FullHttpResponse)msg;
			if(System.currentTimeMillis()-time>=Constants.getREQUEST_TIMEOUT()){
				this.request=null;
				this.bp.getPsb().setErrorInfo(new DateTime().toString("yyyy-MM-dd HH:mm:ss")+"timeout:"+Constants.getREQUEST_TIMEOUT());
				this.bp.setVisit_time(System.currentTimeMillis());
				this.bp.getCm().addBPChnnelToFreeQueue(bp);
				ReferenceCountUtil.release(msg);
				return;
			}
			if(request.getCh().isActive()){
				request.getCh().writeAndFlush(res);
			}else{
				ReferenceCountUtil.release(msg);
			}
			this.bp.getPsb().setErrorInfo(null);
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

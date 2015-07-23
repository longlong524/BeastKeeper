package org.epiclouds.client.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.ReferenceCountUtil;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.Base64;

import org.epiclouds.client.main.MainRun;
import org.epiclouds.handlers.util.BPRequest;
import org.epiclouds.handlers.util.ChannelManager;
import org.epiclouds.handlers.util.Constants;
import org.epiclouds.handlers.util.HostStatusBean;
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
		MainRun.mainlogger.error(cause.getLocalizedMessage(), cause);
	}





	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
		ctx.close();
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		FullHttpRequest res=(FullHttpRequest)msg;

		if(res.method().compareTo(HttpMethod.CONNECT)==0){
			ReferenceCountUtil.release(msg);
			ctx.channel().writeAndFlush(Constants.CONNECT_RESPONSE);
			ctx.close();
			return;
		}
		if(res.headers().get("Host")==null){
			ReferenceCountUtil.release(msg);
			ctx.close();
			return;
		}
		if((res.headers().get("Host")+"").startsWith("106.3.38.50")
				||(res.headers().get("Host")+"").startsWith("localhost")){
			ReferenceCountUtil.release(msg);
			ctx.close();
			return;
		}
		HostStatusBean hs=HostStatusManager.getRequestNum(res.headers().get("Host")+"");
		if(hs!=null&&(hs.getRequest_num().get()+1-hs.getHandled_num().
				get()>Constants.getMAX_UNHADNLED_REQUEST())){
			ReferenceCountUtil.release(msg);
			ctx.close();
			return;
		}else{
			HostStatusManager.incrementRequestNum(res.headers().get("Host")+"");
		}
		
		if(Constants.getREQUEST_AUTHSTRING()!=null){
			try{
				if(res.headers().get("Proxy-Authorization")==null){
					ReferenceCountUtil.release(msg);
					ctx.channel().writeAndFlush(Constants.NOAUTH_RESPONSE);
					return;
				}
				String[] auths=(res.headers().get("Proxy-Authorization")+"").split(" ");
				if(auths.length<2){
					ReferenceCountUtil.release(msg);
					ctx.close();
					return;
				}
				String authString=new String(Base64.getDecoder().decode(auths[1].getBytes("utf-8"))
						,"utf-8");
				if(!Constants.getREQUEST_AUTHSTRING().equals(authString)){
					ReferenceCountUtil.release(msg);
					ctx.close();
					return;
				}
				res.headers().remove("Proxy-Authorization");
			}catch(Exception e){
				MainRun.mainlogger.error(e.getLocalizedMessage(), e);
				ReferenceCountUtil.release(msg);
				ctx.close();
				return;
			}
		}

		manager.addBPRequest(new BPRequest(ctx.channel(),res));
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelRegistered(ctx);
	}

	
	


	

	
}

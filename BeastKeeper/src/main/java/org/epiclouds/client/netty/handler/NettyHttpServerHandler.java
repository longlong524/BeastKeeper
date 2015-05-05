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
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		FullHttpRequest res=(FullHttpRequest)msg;
		if(res.method().compareTo(HttpMethod.CONNECT)==0){
			ReferenceCountUtil.release(msg);
			ctx.channel().writeAndFlush(Constants.CONNECT_RESPONSE);
			ctx.close();
			return;
		}
		FullHttpRequest re=(FullHttpRequest)msg;
		HostStatusBean hs=HostStatusManager.getRequestNum(re.headers().get("Host")+"");
		if(hs!=null&&(hs.getRequest_num().get()-hs.getHandled_num().get()>Constants.MAX_UNHADNLED_REQUEST)){
			ReferenceCountUtil.release(msg);
			ctx.close();
			return;
		}
		if(re.headers().get("Host")!=null){
			HostStatusManager.incrementRequestNum(re.headers().get("Host")+"");
		}
		if(Constants.REQUEST_AUTHSTRING!=null){
			try{
				if(re.headers().get("Proxy-Authorization")==null){
					ReferenceCountUtil.release(msg);
					ctx.close();
					return;
				}
				String[] auths=(re.headers().get("Proxy-Authorization")+"").split(" ");
				if(auths.length<2){
					ReferenceCountUtil.release(msg);
					ctx.close();
					return;
				}
				String authString=new String(new sun.misc.BASE64Decoder().decodeBuffer(auths[1]),"utf-8");
				if(!Constants.REQUEST_AUTHSTRING.equals(authString)){
					ReferenceCountUtil.release(msg);
					ctx.close();
					return;
				}
				re.headers().remove("Proxy-Authorization");
			}catch(Exception e){
				MainRun.mainlogger.error(e.getLocalizedMessage(), e);
				ReferenceCountUtil.release(msg);
				ctx.close();
				return;
			}
		}

		manager.addBPRequest(new BPRequest(ctx.channel(),re));
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelRegistered(ctx);
	}

	
	


	

	
}

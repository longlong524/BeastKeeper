package org.epiclouds.client.netty.handler;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class SimpleWriter extends ChannelHandlerAdapter{

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelActive(ctx);
		System.err.println("channelActive:"+ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
		System.err.println("channelInActive:"+ctx.channel());
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		// TODO Auto-generated method stub
/*		ByteBuf bm=((ByteBuf)msg);
		if(bm.readableBytes()==4570){
			bm.setBytes(4566, new byte[]{0x3d,0x12,0x08,0x01});
		}else{
			if(bm.readableBytes()==10){
				bm.capacity(4);
				bm.setInt(0, 0xcf560000);
			}else{
				if(bm.readableBytes()==3){
					bm.setByte(0, '4');
				}
			}
		}
		System.err.print("channelWrite:"+bm.readableBytes()+":"+bm.toString(Charset.forName("utf-8")));
*/
		super.write(ctx, msg, promise);
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.flush(ctx);
	}

	
}

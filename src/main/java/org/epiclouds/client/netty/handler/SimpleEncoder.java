package org.epiclouds.client.netty.handler;

import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.ReferenceCounted;

public class SimpleEncoder  extends MessageToMessageEncoder<Object>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg,
			List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		if(msg instanceof ReferenceCounted){
			((ReferenceCounted)msg).retain();
		}
		
		System.err.println(new String(((ByteBuf)msg).toString(Charset.forName("utf-8"))));
		out.add(msg);
	}

	

}

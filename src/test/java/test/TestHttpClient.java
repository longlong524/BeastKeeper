package test;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import org.epiclouds.handlers.util.BPRequest;
import org.epiclouds.handlers.util.ChannelManager;
import org.epiclouds.handlers.util.ProxyStateBean;
import org.epiclouds.netty.NettyHttpClient;

public class TestHttpClient {
	public static void main(String args[]) throws Exception{
		NettyHttpClient client=new NettyHttpClient();
		ProxyStateBean psb=new ProxyStateBean("155.94.129.226",80,"yuanshuju:yuanshuju");
		FullHttpRequest req=null;

			req=new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.CONNECT, "www.baidu.com:443");
			req.headers().add("Proxy-Connection","Keep-Alive");
		req.headers().add("Host","www.baidu.com");
		req.headers().add("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36");
		client.connect(psb, new BPRequest(null, req), new ChannelManager(client));
		Thread.sleep(1000*1000);
	}
}

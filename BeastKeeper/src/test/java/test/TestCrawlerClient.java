package test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.HashMap;
import java.util.Map;

import org.epiclouds.client.main.MainRun;

public class TestCrawlerClient {
	  private static Bootstrap sb=new Bootstrap();
	    private static EventLoopGroup workers=new NioEventLoopGroup();

	    /**
Accept-Encoding:gzip, deflate, sdch
Accept-Language:zh-CN,zh;q=0.8
Cache-Control:no-cache
Connection:keep-alive
Host:www.oschina.net
Pragma:no-cache
User-Agent:Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.76 Safari/537.36
	     * @param args
	     * @throws Exception
	     */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Map<String,String> headers=new HashMap<>();
		headers.put("Accept-Encoding","gzip, deflate, sdch");
		headers.put("Cache-Control","no-cache");
		headers.put("Connection","keep-alive");
		headers.put("Host","www.oschina.net");
		
		headers.put("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.76 Safari/537.36");
		MainRun cc=new MainRun();
		/*cc.execute(new AbstractNettyCrawlerHandler(null, "http", "www.oschina.net", "/project/tag/106/httpserver?sort=view&lang=19&os=0",
				HttpMethod.GET, headers, null, "utf-8") {
			
			@Override
			public void handle(String content) throws Exception {
				// TODO Auto-generated method stub
				System.err.println(content);
			}
		});*/
	}

}

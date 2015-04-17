package org.epiclouds.spiders.webconsole;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.epiclouds.handlers.util.MongoManager;
import org.epiclouds.handlers.util.ProxyManager;
import org.epiclouds.handlers.util.ProxyStateBean;
import org.epiclouds.handlers.util.StorageBean;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * Servlet implementation class GetChart
 */

public class AddProxy extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public AddProxy() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try{
			String host=request.getParameter("host");
			if(host==null||"".equals(host)){
				throw new Exception("host string is null!");
			}
			int port=Integer.parseInt(request.getParameter("port"));
			if(port<=0||port>65535){
				throw new Exception("port is negtive or bigger than 65535!");
			}
			String authStr=request.getParameter("authstr");
			if(authStr==null||"".equals(authStr)){
				throw new Exception("auth string is null!");
			}
			boolean re=ProxyManager.addProxy(new ProxyStateBean(host, port, authStr));
			if(re){
				request.setAttribute("success",  "增加代理成功！");
				response.sendRedirect("success.jsp");
				StorageBean sb=new StorageBean();
				sb.setDbstr("BeastKeeper");
				sb.setTablestr("proxy");
				DBObject data=new BasicDBObject();
				data.put("host", host);
				data.put("port", port);
				data.put("authStr", authStr);
				sb.setData(data);
				MongoManager.UpOrInsert(sb);
			}else{
				request.setAttribute("error", "增加代理失败！");
				response.sendRedirect("error.jsp");
			}
		}catch(Exception e){
			request.setAttribute("error", "增加代理失败！"+e.toString());
			response.sendRedirect("error.jsp");
			return;
		}
		
		
		
	}


}
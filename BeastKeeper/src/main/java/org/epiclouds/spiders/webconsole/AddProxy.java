package org.epiclouds.spiders.webconsole;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.epiclouds.handlers.util.Constants;
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
		HttpSession s=request.getSession();
		if(s.getAttribute("user")==null){
			response.sendRedirect("login.jsp");
			return;
		}
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
				
				StorageBean sb=new StorageBean();
				sb.setDbstr(Constants.MONGO_DATABASE);
				sb.setTablestr(Constants.TABLE_PROXY);
				DBObject data=new BasicDBObject();
				data.put("host", host);
				data.put("port", port);
				data.put("authStr", authStr);
				sb.setData(data);
				MongoManager.UpOrInsert(sb);
				request.setAttribute("success",  "增加代理成功！");
				request.getRequestDispatcher("success.jsp").forward(request, response);
			}else{
				request.setAttribute("error", "增加代理失败！");
				request.getRequestDispatcher("error.jsp").forward(request, response);
			}
		}catch(Exception e){
			request.setAttribute("error", "增加代理失败！"+e.toString());
			request.getRequestDispatcher("error.jsp").forward(request, response);
			return;
		}
		
		
		
	}


}

package org.epiclouds.spiders.webconsole;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class RemoveProxy extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public RemoveProxy() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
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
			
			boolean re=ProxyManager.removeProxy(host);
			if(re){
				StorageBean sb=new StorageBean();
				sb.setDbstr(Constants.MONGO_DATABASE);
				sb.setTablestr(Constants.TABLE_PROXY);
				DBObject con=new BasicDBObject();
				con.put("host", host);
				sb.setCondition(con);
				MongoManager.delete(sb);
				request.setAttribute("success",  "删除代理成功！");
				request.getRequestDispatcher("success.jsp").forward(request, response);
			}else{
				request.setAttribute("error", "删除代理失败！");
				request.getRequestDispatcher("error.jsp").forward(request, response);
			}
		}catch(Exception e){
			request.setAttribute("error", "删除代理失败！"+e.toString());
			request.getRequestDispatcher("error.jsp").forward(request, response);
			return;
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}


}

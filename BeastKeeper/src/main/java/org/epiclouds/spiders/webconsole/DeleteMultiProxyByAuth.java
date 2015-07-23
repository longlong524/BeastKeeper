package org.epiclouds.spiders.webconsole;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

public class DeleteMultiProxyByAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public DeleteMultiProxyByAuth() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
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
			String authStr=request.getParameter("authStr");
			if(authStr==null||"".equals(authStr)){
				throw new Exception("auth string is null!");
			}
			
			List<ProxyStateBean> re=ProxyManager.getAllProxy();
			for(ProxyStateBean psb:re){
				if(authStr.equals(psb.getAuthStr())){
					boolean result=ProxyManager.removeProxy(psb.getHost());
					if(result){
						StorageBean sb=new StorageBean();
						sb.setDbstr(Constants.MONGO_DATABASE);
						sb.setTablestr(Constants.TABLE_PROXY);
						DBObject con=new BasicDBObject();
						con.put("host", psb.getHost());
						sb.setCondition(con);
						MongoManager.delete(sb);
					}
				}
			}
			
		}catch(Exception e){
			request.setAttribute("error", "删除代理失败！"+e.toString());
			request.getRequestDispatcher("error.jsp").forward(request, response);
			return;
		}
		request.setAttribute("success",  "删除代理成功！");
		request.getRequestDispatcher("success.jsp").forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


}

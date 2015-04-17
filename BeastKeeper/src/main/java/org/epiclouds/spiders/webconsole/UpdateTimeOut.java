package org.epiclouds.spiders.webconsole;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.epiclouds.handlers.util.Constants;
import org.epiclouds.handlers.util.MongoManager;
import org.epiclouds.handlers.util.ProxyManager;
import org.epiclouds.handlers.util.StorageBean;
import org.epiclouds.handlers.util.TimeoutManager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * Servlet implementation class GetChart
 */

public class UpdateTimeOut extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public UpdateTimeOut() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
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
			long timeout=Long.parseLong(request.getParameter("timeout"));
			boolean re=TimeoutManager.updateHostTimout(host, timeout);
			if(re){
				StorageBean sb=new StorageBean();
				sb.setDbstr(Constants.MONGO_DATABASE);
				sb.setTablestr(Constants.TABLE_TIMEOUT);
				DBObject con=new BasicDBObject();
				con.put("host", host);
				DBObject data=new BasicDBObject();
				data.put("host", host);
				data.put("timeout", timeout);
				sb.setData(data);
				sb.setCondition(con);
				MongoManager.UpOrInsert(sb);
				request.setAttribute("success",  "更新"+host+"延时时间成功！");
				response.sendRedirect("success.jsp");
			}else{
				request.setAttribute("error", "更新延时时间失败！");
				response.sendRedirect("error.jsp");
			}
		}catch(Exception e){
			request.setAttribute("error", "更新延时时间失败！"+e.toString());
			response.sendRedirect("error.jsp");
			return;
		}
	}


}

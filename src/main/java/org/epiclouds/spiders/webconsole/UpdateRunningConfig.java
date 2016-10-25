package org.epiclouds.spiders.webconsole;


import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.epiclouds.handlers.util.Constants;



/**
 * Servlet implementation class GetChart
 */

public class UpdateRunningConfig extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public UpdateRunningConfig() {
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
			String para=request.getParameter("name");
			if(para==null||"".equals(para)){
				throw new Exception("host string is null!");
			}
			String value=request.getParameter("value");
			Method m=Constants.class.getMethod("set"+para, String.class);
			m.invoke(null, value);
			request.setAttribute("success",  "更新参数"+para+"成功！");
			request.getRequestDispatcher("success.jsp").forward(request, response);
			
		}catch(Exception e){
			request.setAttribute("error", "更新参数失败！"+e.toString());
			request.getRequestDispatcher("error.jsp").forward(request, response);
			return;
		}
	}


}

package org.epiclouds.spiders.webconsole;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.epiclouds.client.main.MainRun;
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

public class Restart extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public Restart() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession s=request.getSession();
		if(s.getAttribute("user")==null){
			response.sendRedirect("login.jsp");
			return;
		}
		MainRun.restart=true;
		System.exit(0);
	}


}

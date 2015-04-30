<%@page import="org.epiclouds.handlers.util.ProxyManager"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Map"%>
<%@page import="org.epiclouds.handlers.util.*"%>
<%@page import="java.util.*"%>
<%@page import="java.net.SocketAddress"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>代理查看</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link href="css/bootstrap.css" rel="stylesheet" media="screen">
</head>
<body>

	<%@include file="navbar.html"%>


	<div class="container-fluid">
	
		<!-- Main hero unit for a primary marketing message or call to action -->
		<div class="hero-unit"></div>
		<%
		HttpSession s=request.getSession();
		if(s.getAttribute("user")==null){
			response.sendRedirect("login.jsp");
			return;
		}
			String error = (String)request.getAttribute("error");
			String su = (String)request.getAttribute("success");
			if (error != null) {
		%>
		<div class="alert alert-error">
			<button type="button" class="close" data-dismiss="alert">&times;
			</button>
			<h4>错误信息</h4>
			<%=error%>
		</div>
		<%
			}
			if (su != null) {
				%>
		<div class="alert alert-success">
			<button type="button" class="close" data-dismiss="alert">&times;
			</button>
			<h4>成功信息</h4>
			<%=su%>
		</div>
		<% 
			}
		%>
		
		
			<fieldset>所有代理:<%
			List<ProxyStateBean> ppss=ProxyManager.getProxyOrderByHost();
			out.write(ppss.size()+"");
			%></fieldset>
		<table class="table table-bordered">
		<thead>
			<tr class="success">
				<td>代理地址</td>
				<td>代理端口</td>
				<td>认证信息</td>
				<td>已移除</td>
				<td>错误信息</td>
				<td>删除</td>
			</tr>
		</thead>
		<tbody>
		<%
			for(ProxyStateBean eb:ppss){
		%>
			<tr>
				<td>
				<%=eb.getHost()%>
				</td>
				<td>
						<%=eb.getPort()%>
				</td>
				<td>
						<%=eb.getAuthStr() %>
				</td>
				<td>
						<%=eb.isRemoved()%>
				</td>
				<td>
						<%=eb.getErrorInfo()%>
				</td>
				<td>
					<a  class="btn btn-large btn-danger" href="removeProxy?host=<%=eb.getHost()%>">删除</a>
				</td>
			</tr>
			<%
			}
			%>
		</tbody>
	</table>
		

	</div>
	<%@include file="foot.html"%>
	<SCRIPT type="text/javascript">
		setactive("viewProxy");
	</SCRIPT>
</body>

</html>

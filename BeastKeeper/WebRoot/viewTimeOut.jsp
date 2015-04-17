<%@page import="org.epiclouds.handlers.util.TimeoutManager"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Map"%>
<%@page import="org.epiclouds.handlers.util.*"%>
<%@page import="java.net.SocketAddress"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>管理所有延时</title>
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
		
		
			<fieldset>所有延时：默认延时时间为<%=Constants.getTimeout() %>毫秒</fieldset>
		<table class="table table-bordered">
		<thead>
			<tr class="success">
				<td>host地址</td>
				<td>延时(0即为继承默认延时时间)/毫秒</td>
				<td>更新延时</td>
			</tr>
		</thead>
		<tbody>
		<%
			for(TimeOutBean eb:TimeoutManager.getAllHostTimoutByOrder()){
		%>
			<tr>
				<td>
				<%=eb.getHost()%>
				</td>
				<td>
						<%=eb.getTimeout()%>
				</td>
				<td>
					<form class="form-horizontal" action="updateTimeOut" method="post">
						<input class="span2" id="appendedInputButton" type="hidden" value="<%=eb.getHost()%>" name="host">
						<div class="input-append">
 							<input class="span2" id="appendedInputButton" type="text" name="timeout" placeholder="毫秒" >
  							<button class="btn" type="button">更新延时</button>
						</div>
					</form>
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

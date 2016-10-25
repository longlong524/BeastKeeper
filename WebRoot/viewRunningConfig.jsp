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
<title>查看所有运行时配置参数</title>
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
		
		
			<fieldset>所有运行时配置参数</fieldset>
		<table class="table table-bordered">
		<thead>
			<tr class="success">
				<td>参数</td>
				<td>当前值</td>
				<td>解释</td>
				<td>更新</td>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>
				REQUEST_AUTHSTRING
				</td>
				<td>
				<%=Constants.getREQUEST_AUTHSTRING()%>
				</td>
				<td>
						请求的basic认证字符串
				</td>
				<td>
					<form class="form-horizontal" id=1 action="updateRunningConfig" method="post">
						<input class="span2" id="appendedInputButton" type="hidden" value="REQUEST_AUTHSTRING" name="name">
						<div class="input-append">
 							<input class="span2" id="appendedInputButton" type="text" name="value" placeholder="basic认证字符串" >
  							<button class="btn" type="submit">更新</button>
						</div>
					</form>
				</td>
			</tr>
			
			<tr>
				<td>
				REQUEST_TIMEOUT
				</td>
				<td>
				<%=Constants.getREQUEST_TIMEOUT()%>
				</td>
				<td>
						请求超时时间（毫秒）
				</td>
				<td>
					<form class="form-horizontal" id=2 action="updateRunningConfig" method="post">
						<input class="span2" id="appendedInputButton" type="hidden" value="REQUEST_TIMEOUT" name="name">
						<div class="input-append">
 							<input class="span2" id="appendedInputButton" type="text" name="value" placeholder="毫秒" >
  							<button class="btn" type="submit">更新</button>
						</div>
					</form>
				</td>
			</tr>
			
			<tr>
				<td>
				MAX_UNHADNLED_REQUEST
				</td>
				<td>
				<%=Constants.getMAX_UNHADNLED_REQUEST()%>
				</td>
				<td>
						最多同时进行请求的个数
				</td>
				<td>
					<form class="form-horizontal" id=2 action="updateRunningConfig" method="post">
						<input class="span2" id="appendedInputButton" type="hidden" value="MAX_UNHADNLED_REQUEST" name="name">
						<div class="input-append">
 							<input class="span2" id="appendedInputButton" type="text" name="value" placeholder="最多同时进行请求的个数" >
  							<button class="btn" type="submit">更新</button>
						</div>
					</form>
				</td>
			</tr>
			
			
			
			<tr>
				<td>
				min_timeout
				</td>
				<td>
				<%=Constants.getMin_timeout()%>
				</td>
				<td>
						获得random的超时时需要的最小值，毫秒
				</td>
				<td>
					<form class="form-horizontal" id=2 action="updateRunningConfig" method="post">
						<input class="span2" id="appendedInputButton" type="hidden" value="Min_timeout" name="name">
						<div class="input-append">
 							<input class="span2" id="appendedInputButton" type="text" name="value" placeholder="获得random的超时时需要的最小值" >
  							<button class="btn" type="submit">更新</button>
						</div>
					</form>
				</td>
			</tr>
		</tbody>
	</table>
		

	</div>
	<%@include file="foot.html"%>
	<SCRIPT type="text/javascript">
		setactive("viewRunningConfig");
	</SCRIPT>
</body>

</html>

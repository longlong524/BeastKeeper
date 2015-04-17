<%@page import="org.epiclouds.handlers.util.ProxyManger"%>
<%@page import="org.epiclouds.spiders.ebay.bean.EbaySpiderBean"%>
<%@page import="org.epiclouds.spiders.util.StatusManager"%>
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
<title>爬虫状态查看</title>
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
		
		<%
		Map<String, Map<SocketAddress, ProxyStateBean>> mp=ProxyManger.getProxyStatus();
		for(String addr:mp.keySet()){
			int use_count=0;
			int good_count=0;
		%>
		
			<fieldset>代理域名：<%=addr %></fieldset>
		<table class="table table-bordered">
		<thead>
			<tr class="success">
				<td>代理地址</td>
				<td>认证信息</td>
				<td>正在使用</td>
				<td>是否正常</td>
			</tr>
		</thead>
		<tbody>
		<%
			for(ProxyStateBean eb:mp.get(addr).values()){
		%>
			<tr>
				<td>
				<%=eb.getAddr()%>
				</td>
				<td>
						<%=eb.getAuthStr()==null?"无":eb.getAuthStr()%>
				</td>
				<td>
						<%=eb.isUsing() %>
						<%if(eb.isUsing()) {
							use_count++;
							}
						%>
				</td>
				<td>
						<%=eb.getErrorInfo()==null?"正常":eb.getErrorInfo() %>
						<%
							if(eb.getErrorInfo()==null){
								good_count++;
							}
						%>
				</td>
			</tr>
			<%
			}
			%>
		</tbody>
	</table>
		<h2>
		总量：<%=mp.get(addr).size() %>,正在使用：<%=use_count %>,正常代理：<%=good_count %>
		</h2>
	<%
		}
	%>

	</div>
	<%@include file="foot.html"%>
	<SCRIPT type="text/javascript">
		setactive("viewProxy");
	</SCRIPT>
</body>

</html>

<%@page import="org.epiclouds.spiders.ebay.bean.EbaySpiderBean"%>
<%@page import="org.epiclouds.spiders.util.StatusManager"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Map"%>
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
		
		
			<fieldset>ebay店铺待爬数量：<%=StatusManager.ebayQue.size() %></fieldset>
		<table class="table table-bordered">
		<thead>
			<tr class="success">
				<td>时间</td>
				<td>名称</td>
				<td>商品数量</td>
			</tr>
		</thead>
		<tbody>
		<%
			for(EbaySpiderBean eb:StatusManager.ebayQue){
		%>
			<tr>
				<td>
				<%=eb.getTime()%>
				</td>
				<td>
						<%=eb.getStorename() %>
				</td>
				<td>
						<%=eb.getItem_num() %>
				</td>
			</tr>
			<%
			}
			%>
		</tbody>
	</table>
		
	<fieldset>ebay店铺已完成数量：<%=StatusManager.ebayFinished.size() %></fieldset>
		<table class="table table-bordered">
		<thead>
			<tr class="success">
				<td>时间</td>
				<td>名称</td>
				<td>商品数量</td>
			</tr>
		</thead>
		<tbody>
		<%
			for(EbaySpiderBean eb:StatusManager.ebayFinished){
		%>
			<tr>
				<td>
				<%=eb.getTime()%>
				</td>
				<td>
						<%=eb.getStorename() %>
				</td>
				<td>
						<%=eb.getItem_num() %>
				</td>
			</tr>
			<%
			}
			%>
		</tbody>
	</table>
	
	
	<fieldset>ebay店铺未完成数量：<%=StatusManager.ebayStatus.size() %></fieldset>
		<table class="table table-bordered">
		<thead>
			<tr class="success">
				<td>时间</td>
				<td>名称</td>
				<td>商品数量</td>
				<td>进度</td>
			</tr>
		</thead>
		<tbody>
		<%
			for(EbaySpiderBean eb:StatusManager.ebayStatus.values()){
		%>
			<tr>
				<td>
					<%=eb.getTime()%>
				</td>
				<td>
					<%=eb.getStorename()%>
				</td>
				<td>
						<%=eb.getItem_num() %>
				</td>
				<td>
						<div class="progress progress-info">
						  <div class="bar" role="bar" style="width: <%=(int)(eb.getSpided_num()*100.0/(eb.getItem_num()==0?1:eb.getItem_num()))%>%;">
						    <%=eb.getSpided_num()*100.0/(eb.getItem_num()==0?1:eb.getItem_num())%>%
						  </div>
						</div>
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
		setactive("view");
	</SCRIPT>
</body>

</html>

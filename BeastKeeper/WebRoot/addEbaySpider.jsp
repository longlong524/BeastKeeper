<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>增加ebay爬虫</title>
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
			<%} %>
		</div>
<fieldset>增加Ebay爬虫</fieldset>
	<form action="addEbaySpider" method="post" class="form-horizontal">
	<div class="control-group">
    <label class="control-label" for="name">ebay店铺名称</label>
    <div class="controls">
      <input type="text" id="name" placeholder="name"  name="name">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="min">最小销售量</label>
    <div class="controls">
      <input type="text" id="min" placeholder="最小销售量"  name="min">
    </div>
  </div>
    <div class="control-group">
    <label class="control-label" for="ebaysite">爬取的ebay站，默认www.ebay.com</label>
    <div class="controls">
      <input type="text" id="ebaysite" placeholder="爬取的ebay站，如www.ebay.com"  name="ebaysite">
    </div>
  </div>
    <div class="control-group">
    <label class="control-label" for="fcid">地区代码，默认为1</label>
    <div class="controls">
      <input type="text" id="fcid" placeholder="ebay地区代码，如1"  name="fcid">
    </div>
  </div>
    <div class="control-group">
    <label class="control-label" for="maxpagenum">爬取的页数，默认为1</label>
    <div class="controls">
      <input type="text" id="maxpagenum" placeholder="爬取的页数"  name="maxpagenum">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button type="submit" class="btn btn-large btn-primary">增加</button>
    </div>
  </div>
	</form>

	</div>
	<%@include file="foot.html"%>
	<SCRIPT type="text/javascript">
		setactive("addEbay");
	</SCRIPT>
</body>

</html>

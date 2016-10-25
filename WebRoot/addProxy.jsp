<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>增加代理</title>
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
<fieldset>增加代理</fieldset>
	<form action="addProxy" method="post" class="form-horizontal">
	<div class="control-group">
    <label class="control-label" for="host">主机ip</label>
    <div class="controls">
      <input type="text" id="host" placeholder="主机ip"  name="host">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="port">端口</label>
    <div class="controls">
      <input type="text" id="port" placeholder="端口"  name="port">
    </div>
  </div>
    <div class="control-group">
    <label class="control-label" for="authstr">基本认证字符串,如yuanshuju:yuanshuju</label>
    <div class="controls">
      <input type="text" id="authstr" placeholder="基本认证字符串,如yuanshuju:yuanshuju"  name="authstr">
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
		setactive("addProxy");
	</SCRIPT>
</body>

</html>

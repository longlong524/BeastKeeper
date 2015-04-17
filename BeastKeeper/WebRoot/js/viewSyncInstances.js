						$(document).ready(function(){
							setactive("viewsyncinstances");
							getSyncInstances();
						});
												
						function getSyncInstances(){
							var hint_types=$("#hint");
							var txtnode=$("<p>正在加载所有同步实例...</p>");
							hint_types.append(txtnode);

							$.ajax({
								  url: "/getSyncInstances",
								  dataType:"json",
								  error: function(re,errormsg){
									  hint_types.empty();
									  alert("网络出错：错误信息"+errormsg);
								  },
								  success: function(data,statustxt){
									  if(data[0].name!="ok"){
										  alert("发生错误："+data[0].name);
										  return;
									  }
									  var s=$("#data_body");
									  s.empty();
									  for(var i=1;i<data.length;i++){
										  var tmp=$("<tr></tr>");
										  var n=$("<td>"+data[i].node+"</td>");
										  tmp.append(n);
										  n=$("<td>"+data[i].name+"</td>");
										  tmp.append(n);
										  var da=new Date();
										  da.setTime(data[i].time);
										  n=$("<td>"+da.toLocaleString()+"</td>");
										  tmp.append(n);
										  n=$("<td>"+data[i].sourceconfig.name+"</td>");
										  tmp.append(n);
										  var t=$("<td></td>");
										  
										  n=$("<a class=\"btn btn-success btn-large\">查看状态</a>");
										  n.attr("href","viewSyncInstanceStat.jsp?name="+data[i].name);
										  t.append(n);
										  tmp.append(t);
										  
										  t=$("<td></td>");
										  n=$("<a class=\"btn btn-success btn-large\" target=\"_blank\">查看详细</a>");
										  n.attr("href","updateSyncInstance.jsp?name="+data[i].name);
										  t.append(n);
										  tmp.append(t);
										  
										  
										  t=$("<td></td>");
										  n=$("<a href=\"#deleteModal\" data-toggle=\"modal\" class=\"btn btn-danger btn-large\" target=\"_blank\">删除</a>");
										  n.attr("onclick","setdelete('"+data[i].name+"')");
										  t.append(n);
										  tmp.append(t);
										  
										  
										  t=$("<td></td>");
										  n=$("<a class=\"btn btn-success btn-large\" target=\"_blank\">查看</a>");
										  n.attr("href","viewSyncInstancePerformance.jsp?name="+data[i].name);
										  t.append(n);
										  tmp.append(t);
										  
										  s.append(tmp);
									  }
									  hint_types.empty();
								  }
							});
						}
	
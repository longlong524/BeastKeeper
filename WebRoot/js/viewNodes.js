					
						function getNodes(){
							var hint_types=$("#hint");
							var txtnode=$("<p>正在加载节点信息...</p>");
							hint_types.append(txtnode);
							$.ajax({
								  url: "/getNodes",
								  dataType:"json",
								  error: function(re,errormsg){
									  hint_types.empty();
									  alert("网络出错：错误信息"+errormsg);
								  },
								  success: function(data,statustxt){
									  if(data[0].info!="ok"){
										  alert("发生错误："+data[0].info);
										  return;
									  }
									  var s=$("#data_body");
									  s.empty();
									  for(var i=1;i<data.length;i++){
										  var tmp=$("<tr></tr>");
										  var n=$("<td>"+data[i].name+"</td>");
										  tmp.append(n);
										  n=$("<td>"+data[i].ip+"</td>");
										  tmp.append(n);
										  n=$("<td>"+data[i].port+"</td>");
										  tmp.append(n);
										  n=$("<td>"+data[i].syncs.length+"</td>");
										  tmp.append(n);
										  n=$("<td>"+data[i].syncs.join()+"</td>");
										  tmp.append(n);
										  
										  var t=$("<td></td>");
										  n=$("<a class=\"btn btn-success btn-large\" target=\"_blank\">查看</a>");
										  n.attr("href","viewNodeState.jsp?name="+data[i].name);
										  t.append(n);
										  tmp.append(t);
										  s.append(tmp);
									  }
									  hint_types.empty();
								  }
							});
						}
	
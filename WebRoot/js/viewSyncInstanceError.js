					
						function getSyncInstanceError(instance){
							var hint_types=$("#hint");
							var txtnode=$("<p>正在加载错误信息...</p>");
							hint_types.append(txtnode);
							var ins=$("#instance");
							ins.text("同步实例名称："+instance);
							$.ajax({
								  url: "/viewSyncInstanceError",
								  data:{
									name:instance  
								  },
								  dataType:"json",
								  error: function(re,errormsg){
									  hint_types.empty();
									  alert("网络出错：错误信息"+errormsg);
								  },
								  success: function(data,statustxt){
									  if(data.info!="ok"){
										  alert("发生错误："+data.info);
										  return;
									  }
									  var s=$("#data_body");
									  s.empty();
									  data=data.sc;
									  for(var i=0;i<data.length;i++){
										  var tmp=$("<tr></tr>");
										  var n=$("<td>"+(i+1)+"</td>");
										  tmp.append(n);
										  n=$("<td>"+data[i].time+"</td>");
										  tmp.append(n);
										  n=$("<td>"+data[i].error+"</td>");
										  tmp.append(n);
										  s.append(tmp);
									  }
									  hint_types.empty();
								  }
							});
						}
	
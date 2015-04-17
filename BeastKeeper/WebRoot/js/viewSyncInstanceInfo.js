						function getSourceConfigure(instance){
							var hint_types=$("#hint");
							var txtnode=$("<p>正在加载配置信息...</p>");
							hint_types.append(txtnode);
							$("#span1").empty();
							$("#span2").empty();
							$("#span3").empty();
							$.ajax({
								  url: "/viewSyncInstanceInfo",
								  data: {
									  name:instance
								  },
								  dataType:"json",
								  error: function(re,errormsg){
									  hint_types.removeChild(txtnode);
									  alert("网络出错：错误信息"+errormsg);
								  },
								  success: function(data,statustxt){
									  if(data.info!="ok"){
										  alert("发生错误："+data.info);
										  return;
									  }
									  $("#node").val(data.node);
									  $("#name").val(data.name);
									  $("#remoteip").val(data.remoteip);
									  $("#remoteport").val(data.remoteport);
									  $("#src_type").val(data.type);
									  $("#time").val(data.time);
									  $("#source_handler_chain").val(data.sourcehandlers);
									  $("#local_handler_chain").val(data.localchain);
									  $("#local_handler_chain2").val(data.localhandlers);
									  data=data.sc;
									  var divs=new Array(3);
									  divs[0]=Math.floor((data.length)/3)+(((data.length)%3))-(((data.length)%3)>>1);
									  divs[1]=Math.floor((data.length)/3)+(((data.length)%3)>>1);
									  divs[2]=Math.floor((data.length)/3);
									  var k=0;
									  for(var j=1;j<=3;j++){
									  	var parent=$("#span"+j);
										  for(var i=k;i<k+divs[j-1];i++){
											  
											  var node=$("<div class=\"control-group\"></div>");
											 var label=$("<label>"+data[i].displayname+"</label>");
											 label.attr("class","control-label");
											 node.append(label);
											 var controls=$("<div class=\"controls\"></div>");
											 
											 var inp=$("<input type=\"text\">");
											 inp.attr("name",data[i].name);
											 inp.attr("id",data[i].name);
											 var span=$("<span>"+data[i].desc+"</span>");
											 span.attr("class","help-block");
											 controls.append(inp);
											 controls.append(span);
											 node.append(controls);
											 inp.val(data[i].value);
											 inp.attr("readonly","readonly");
											 parent.append(node);
										  }
										  k+=divs[j-1];
									  }
									  hint_types.empty();
								  }
							});
						}
						
						
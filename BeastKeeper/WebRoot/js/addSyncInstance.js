						var oldip="";
						var oldport="";
						var localhandlers=new Object();
						var sourcehandlers=new Object();
						var sourcetype=new Object();
						var oldtype="";
						var validates=new Array();
						var localconfigvalidates=new Array();
						var sourceconfigvalidates=new Array();
						var sourcechain=new Array();
						var localchain=new Array();
						function exeValidates(){
							for(var i=0;i<validates.length;i++){
								validates[i]();
							}
							exeLocalValidates();
							exeSourceValidates();
						}
						function exeLocalValidates(){
							for(var i=0;i<localconfigvalidates.length;i++){
								localconfigvalidates[i]();
							}
						}
						function exeSourceValidates(){
							for(var i=0;i<sourceconfigvalidates.length;i++){
								sourceconfigvalidates[i]();
							}	
						}
						function clearLocalHandlerChain(){
								$("#local_handler_chain2").val("");
								localchain.splice(0,localchain.length);
						}
						function addLocalHandlerChain(){
							if($("#local_handlers").val()==""||$("#local_handlers").val()==null||$("#local_handlers").val()==undefined){
								return;
							}
							// Start validation:
						    $.validity.start();
						    exeLocalValidates();
						    // All of the validator methods have been called:
						    // End the validation session:
						    var result = $.validity.end();
						    
						    if(!result.valid){
						    	alert("请正确填写配置项");
						    	return;
						    }
							var chain=$("#local_handler_chain2");
							if(chain.val()!=""){
								localchain=$.evalJSON( chain.val());
							}
							var hand=localhandlers[$("#local_handlers").val()];
							for(var i=0;i<hand.config.length;i++){
								hand.config[i].value=$("div#localdiv #"+hand.config[i].name).val();
							}
							localchain.push(hand);
							chain.val($.toJSON(localchain));
						}
						function setLocalExplain(){
							var ex=$("#local_handlers").val();
							if(ex==undefined||ex==""||ex==null){
								return;
							}
							setLocalHandlerConfig(ex);
							var op=$("#local"+ex);
							var plain=$("#local_handler_explain");
							plain.empty();
							plain.append(op.attr("title"));
						}
						function clearSourceHandlerChain(){
							sourcechain.splice(0,sourcechain.length);
							$("#source_handler_chain").val("");
						}
						function addSourceHandlerChain(){
							if($("#source_handlers").val()==""||$("#source_handlers").val()==null||$("#source_handlers").val()==undefined){
								return;
							}
							// Start validation:
						    $.validity.start();
						    exeSourceValidates();
						    // All of the validator methods have been called:
						    // End the validation session:
						    var result = $.validity.end();
						    
						    if(!result.valid){
						    	alert("请正确填写配置项");
						    	return;
						    }
							var chain=$("#source_handler_chain");
							if(chain.val()!=""){
								sourcechain=$.evalJSON( chain.val());
							}
							var hand=sourcehandlers[$("#source_handlers").val()];
							for(var i=0;i<hand.config.length;i++){
								hand.config[i].value=$("div#sourcediv #"+hand.config[i].name).val();
							}
							sourcechain.push(hand);
							chain.val($.toJSON(sourcechain));
						}
						function setSourceExplain(){
							var ex=$("#source_handlers").val();
							if(ex==undefined||ex==""||ex==null){
								return;
							}
							setSourceHandlerConfig(ex);
							var op=$("#source"+ex);
							var plain=$("#source_handler_explain");
							plain.empty();
							plain.append(op.attr("title"));
						}
						function getSourceType(){
							var nip=$("#conip").val();
							var nport=$("#conport").val();
							if(nip==oldip&&nport==oldport){
								return;
							}
							var hint_types=$("#src_type_hint");
							var txtnode=$("<p>正在加载...<p>");
							hint_types.append(txtnode);
							oldip=nip;
							oldport=nport;
							getSourceHandlers();
							var src_types=$("#src_type");
							src_types.empty();
							$.ajax({
								  url: "/getSourceType",
								  data: {
									  ip:oldip,
									  port:oldport
								  },
								  error: function(re,errormsg){
									  hint_types.empty();
									  alert("网络出错：错误信息"+errormsg);
								  },
								  success: function(data,statustxt){
									  data=$.evalJSON(data);
									  if(data[0].name!="ok"){
										  hint_types.empty();
										  alert("错误信息:"+data[0].name);
										  return;
									  }
									  for(var i=1;i<data.length;i++){
										  sourcetype[data[i].name]=data[i];
										  var node=$("<option></option>");
										  src_types.append(node);
										  node.attr("value",data[i].name);
										  node.append($("<p>"+data[i].name+"</p>"));
									  }
									  hint_types.empty();
									  if(data.length>1){
										  getSourceConfigure();
									  }
								  }
							});
						};
						

						function getSourceConfigure(){
							var newtype=$("#src_type").val();
							if(newtype==oldtype){
								return;
							}
							$("#hiddensourcetype").val($.toJSON(sourcetype[newtype]));
							var hint_types=$("#source_legend");
							var txtnode=$("<p>正在加载设置...</p>");
							hint_types.append(txtnode);
							oldtype=newtype;
							$("#span1").empty();
							$("#span2").empty();
							$("#span3").empty();
							validates.splice(0,validates.length);
							  var data=sourcetype[newtype].configs;

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
									 if(data[i].datatype!="no"&&data[i].datatype!=undefined){
										 var str=$("<strong>*</strong>");
										 str.attr("class","text-error");;
										 label.append(str);
									 }
									 var controls=$("<div class=\"controls\"></div>");
									 
									 var inp=$("<input>");
									 inp.attr("type",data[i].type);
									 inp.attr("name",data[i].name);
									 inp.attr("id",data[i].name);
									 var span=$("<span>"+data[i].desc+"</span>");
									 span.attr("class","help-block");
									 controls.append(inp);
									 controls.append(span);
									 node.append(controls);
									 parent.append(node);
									 inp.change(function(inp,i){
										 return function(){
											 sourcetype[$("#src_type").val()].configs[i].value=inp.val();
											 $("#hiddensourcetype").val($.toJSON(sourcetype[$("#src_type").val()]));
										 }
									 }(inp,i));
									  if(data[i].datatype!="no"&&data[i].datatype!=undefined){
										 if(data[i].datatype=="required"){
											 validates.push(
												function(inp){
													 return function(){
														 inp.require();
													 };
												}(inp)
											 
											 );
										 	
										 }else{
											 validates.push(
													function(inp,datatype){
													 return function(){
														 inp.require().match(datatype);
													 	};
													 }(inp,data[i].datatype)
													 );
							                
									 	}
									 } 
								  }
								  k+=divs[j-1];
							  }
							  hint_types.empty();
						}
						

						
						function getSourceHandlers(){
							var hint_types=$("#source_handler_legend");
							var txtnode=$("<p>正在加载源处理器...</p>");
							hint_types.append(txtnode);
							$.ajax({
								  url: "/getSourceHandlers",
								  data: {
									  ip:oldip,
									  port:oldport
								  },
								  dataType:"json",
								  error: function(re,errormsg){
									  hint_types.empty();
									  alert("网络出错：错误信息"+errormsg);
								  },
								  success: function(data,statustxt){
									  if(data[0].name!="ok"){
										  hint_types.empty();
										  alert("发生错误："+data[0].name);
										  return;
									  }
									  var s=$("#source_handlers");
									  s.empty();
									  for(var i=1;i<data.length;i++){
										  var tmp=$("<option></option>");
										  tmp.attr("label",data[i].name);
										  tmp.attr("value",data[i].name);
										  tmp.attr("id","source"+data[i].name);
										  tmp.attr("title",data[i].desc);
										  sourcehandlers[data[i].name]=data[i];
										  s.append(tmp);
									  }
									  hint_types.empty();
									  var plain=$("#source_handler_explain");
									  plain.empty();
									  if(data.length>1){
									  	plain.append(data[1].desc);
									  	setSourceHandlerConfig(data[1].name);
									  }
								  }
							});
						}
						
						
						function getLocalHandlers(){
							var hint_types=$("#local_handler_legend");
							var txtnode=$("<p>正在加载本地处理器...</p>");
							hint_types.append(txtnode);
							
							$.ajax({
								  url: "/getLocalHandlers",
								  dataType:"json",
								  error: function(re,errormsg){
									  hint_types.empty();
									  alert("网络出错：错误信息"+errormsg);
								  },
								  success: function(data,statustxt){
									  if(data[0].name!="ok"){
										  hint_types.empty();
										  alert("发生错误："+data[0].name);
										  return;
									  }
									  var s=$("#local_handlers");
									  s.empty();
									  for(var i=1;i<data.length;i++){
										  localhandlers[data[i].name]=data[i];
										  var tmp=$("<option></option>");
										  tmp.attr("label",data[i].name);
										  tmp.attr("value",data[i].name);
										  tmp.attr("id","local"+data[i].name);
										  tmp.attr("title",data[i].desc);
										  
										  s.append(tmp);
									  }
									  hint_types.empty();
									  var plain=$("#local_handler_explain");
									  plain.empty();
									  if(data.length>1){
									  	plain.append(data[1].desc);
									  	setLocalHandlerConfig(data[1].name);
									  }
								  }
							});
						}
						
						function setLocalHandlerConfig(name){
							$("#localconfighint").text("处理器"+name+"设置项");
							$("#localspan1").empty();
							$("#localspan2").empty();
							$("#localspan3").empty();
							var theconfig=localhandlers[name].config;
							  var divs=new Array(3);
							  divs[0]=Math.floor((theconfig.length)/3)+(((theconfig.length)%3))-(((theconfig.length)%3)>>1);
							  divs[1]=Math.floor((theconfig.length)/3)+(((theconfig.length)%3)>>1);
							  divs[2]=Math.floor((theconfig.length)/3);
							localconfigvalidates.splice(0, localconfigvalidates.length);
							var i=0;
							for(var k=1;k<=3;k++){
								var configs=$("#localspan"+k);
								var tmp=i;
							for(;i<tmp+divs[k-1];i++){
								  var node=$("<div class=\"control-group\"></div>");
								 var label=$("<label>"+theconfig[i].displayname+"</label>");
								 label.attr("class","control-label");
								 node.append(label);
								 if(theconfig[i].datatype!="no"&&theconfig[i].datatype!=undefined){
									 var str=$("<strong>*</strong>");
									 str.attr("class","text-error");;
									 label.append(str);
								 }
								 var controls=$("<div class=\"controls\"></div>");
								 
								 var inp=$("<input>");
								 inp.attr("type",theconfig[i].type);
								 inp.attr("name",theconfig[i].name);
								 inp.attr("id",theconfig[i].name);
								 var span=$("<span>"+theconfig[i].desc+"</span>");
								 span.attr("class","help-block");
								 controls.append(inp);
								 controls.append(span);
								 node.append(controls);
								 configs.append(node);
								 inp.change(function(inp,i){
									 return function(){
										 localhandlers[name].config[i].value=inp.val();
									 }
								 }(inp,i));
								 
								  if(theconfig[i].datatype!="no"&&theconfig[i].datatype!=undefined){
									 if(theconfig[i].datatype=="required"){
										 localconfigvalidates.push((function(inp){
												 return function(){
													 inp.require();
												 };
											 }
											 )(inp));
									 	
									 }else{
										 localconfigvalidates.push(
												 (function(inp,datatype)
														 {
																 return function(){
																	 inp.require().match(datatype);
																 };
														 }
												 )(inp,theconfig[i].datatype)
												 );
						                
								 	}
								 } 
							  }
							}
						}
						
						
						function setSourceHandlerConfig(name){
							$("#sourceconfighint").text("处理器"+name+"设置项");
							$("#sourcespan1").empty();
							$("#sourcespan2").empty();
							$("#sourcespan3").empty();
							var theconfig=sourcehandlers[name].config;

							  var divs=new Array(3);
							  divs[0]=Math.floor((theconfig.length)/3)+(((theconfig.length)%3))-(((theconfig.length)%3)>>1);
							  divs[1]=Math.floor((theconfig.length)/3)+(((theconfig.length)%3)>>1);
							  divs[2]=Math.floor((theconfig.length)/3);
							sourceconfigvalidates.splice(0, sourceconfigvalidates.length);
							var i=0;
							for(var k=1;k<=3;k++){
								var configs=$("#sourcespan"+k);
								var tmp=i;
							for(;i<tmp+divs[k-1];i++){
								  var node=$("<div class=\"control-group\"></div>");
								 var label=$("<label>"+theconfig[i].displayname+"</label>");
								 label.attr("class","control-label");
								 node.append(label);
								 if(theconfig[i].datatype!="no"&&theconfig[i].datatype!=undefined){
									 var str=$("<strong>*</strong>");
									 str.attr("class","text-error");;
									 label.append(str);
								 }
								 var controls=$("<div class=\"controls\"></div>");
								 
								 var inp=$("<input>");
								 inp.attr("type",theconfig[i].type);
								 inp.attr("name",theconfig[i].name);
								 inp.attr("id",theconfig[i].name);
								 var span=$("<span>"+theconfig[i].desc+"</span>");
								 span.attr("class","help-block");
								 controls.append(inp);
								 controls.append(span);
								 node.append(controls);
								 configs.append(node);
								 inp.change(function(inp,i){
									 return function(){
										 sourcehandlers[name].config[i].value=inp.val();
									 }
								 }(inp,i));
								  if(theconfig[i].datatype!="no"&&theconfig[i].datatype!=undefined){
									 if(theconfig[i].datatype=="required"){
										 sourceconfigvalidates.push((function(inp){
												 return function(){
													 inp.require();
												 };
											 }
											 )(inp));
									 	
									 }else{
										 sourceconfigvalidates.push(
												 (function(inp,datatype)
														 {
																 return function(){
																	 inp.require().match(datatype);
																 };
														 }
												 )(inp,theconfig[i].datatype)
												 );
						                
								 	}
								 } 
							  }
							}
						}
	
						
						
	
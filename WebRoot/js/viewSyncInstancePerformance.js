
 var chart,type=1;
 var interval=3000;
 $(function () {
    $(document).ready(function() {
        Highcharts.setOptions({
            global: {
                useUTC: false
            },
            lang: {
				months: ['1月', '2月', '3月', '4月', '5月', '6月',  '7月', '8月', '9月', '10月', '11月', '12月'],
				weekdays: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
				contextButtonTitle:'图表菜单',
				downloadJPEG:'下载为JPEG图片',
				downloadPDF:'下载为PDF文件',
				downloadPNG:'下载为PNG图片',
				downloadSVG:'下载为SVG图片',
				drillUpText:'返回{series.name}',
				loading:'加载中',
				printChart:'导出图表',
				resetZoom:'返回',
				resetZoomTitle:'放大比例 1:1',
				shortMonths: ['1月', '2月', '3月', '4月', '5月', '6月',  '7月', '8月', '9月', '10月', '11月', '12月']
			}
        });
    
 

        chart = new Highcharts.Chart({
            chart: {
            	renderTo: 'mychart',
                type: 'spline',
                animation: Highcharts.svg, // don't animate in old IE
                marginRight: 20,
                zoomType: 'x'
            },
            title: {
                text: '<%=instance%>性能、吞吐量监控'
            },
            loading:{
            	hideDuration:500,
            	showDuration:500
            },
            xAxis: {
                type: 'datetime',
                dateTimeLabelFormats:{
                	millisecond: '%y-%m-%d %H:%M:%S',
					second: '%y-%m-%d %H:%M:%S',
					minute: '%y-%m-%d %H:%M:%S',
					hour: '%y-%m-%d %H:%M:%S',
					day: '%y-%m-%d %H:%M:%S',
					week: '%y-%m-%d %H:%M:%S',
					month: '%y-%m-%d %H:%M:%S',
					year: '%y-%m-%d %H:%M:%S'
                },
                tickPixelInterval: 200,
                title:{
                	align:'middle',
                	text:'时间'
                }
            },
            yAxis: {
                title: {
                    text: '吞吐量/性能'
                },
                min:0,
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                },{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            title: {
                text: '同步数据量动态图',
                x: -20 //center
            },
            
            plotOptions: {
                line: {
                    dataLabels: {
                        enabled: true,
                        style: {
                            textShadow: '0 0 3px white, 0 0 3px white'
                        }
                    },
                    enableMouseTracking: false
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
            exporting: {
                enabled: true
            },
            series: [{
                name: '数据量(KB)',
                type: 'spline'
            },
            {
            	name:'数据量(行)',
            	type:'spline'
            }
            ]
        });

    });

    

});



/**
 * Request data from the server, add it to the graph and set a timeout 
 * to request again
 */
 var oldrows=0,oldkb=0;
 var init=false;
function requestData(name) {
    $.ajax({
     	type: "get", 
        url: "viewSyncInstancePer?name="+name,
        dataType: "json", 
        success: function(point) {
        	if(point.info!="ok"){
        		alert("错误信息："+point.info);
        		setTimeout("requestData('"+name+"')", interval); 
        		return;
        	}
        	var shift=chart.series[0].data.length>30;
        	if(init==false){
        		init=true;
        		oldrows=point.rows;
        		oldkb=point.kb;
        	}else{
        		var entry=new Object();
        		var t=new Date();
        		entry.x=t.getTime();
        		entry.y=point.kb-oldkb;
        		chart.series[0].addPoint(entry, false, shift); 
        		entry=new Object();
        		entry.x=t.getTime();
        		entry.y=point.rows-oldrows;
        		chart.series[1].addPoint(entry, false, shift); 
        		oldkb=point.kb;
        		oldrows=point.rows;
        	}
			  
			chart.redraw();
           setTimeout("requestData('"+name+"')", interval);    
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) { 
            alert("发生错误:"+errorThrown); 
             // call it again after one second
            //setTimeout(requestData, 10000);    
        }, 
        cache: false
    });
}


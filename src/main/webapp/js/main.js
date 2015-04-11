$(document).ready(function() {
	$("#collapseOne").click(function(){
		$("#collapseTwo").find("li").removeClass("active");
	});
	$("#collapseTwo").click(function(){
		$("#collapseOne").find("li").removeClass("active");
	});
});

function toggleImportDataModal() {
	$("#importDataModal").modal('toggle');
};

function toggleImportHomeworkModal() {
	$("#importHomeworkModal").modal('toggle');
};

function toggleTopVideosModal() {
	$("#topVideosModal").modal('toggle');
};

function toggleVideoByStudentModal() {
	$("#videoByStudentModal").modal('toggle');
};

function toggleVideoPauseModal() {
	$("#videoPauseModal").modal('toggle');
};

function toggleVideoMoveModal() {
	$("#videoMoveModal").modal('toggle');
};

function toggleVideoInfoModal() {
	$("#videoInfoModal").modal('toggle');
};

function toggleVideoByHomeworkModal(){
	$("#videoByHomeworkModal").modal('toggle');
};

function toggleHomeworkByNameModal() {
	$("#homeworkByNameModal").modal('toggle');
}

function insertTopVideosData(tableData, columnData) {
	var cols = [ "video_id", "count" ];
	var uuid = getUUID();
	var obj = new Object();
	obj = new Object();
	obj.tid = uuid;
	obj.columns = cols;
	var tbl = new EJS({
		url : 'ejs/table.ejs'
	}).render(obj);
	if ($("#topVideosTable").find("table") != null) {
		$("#topVideosTable").remove("table");
	}
	$("#topVideosTable").append(tbl);
	var myRecords = JSON.parse(tableData);
	$("#topVideosTable").find("table").dynatable({
		dataset : {
			records : myRecords
		}
	});
	var dynatable = $("#topVideosTable").find("table").data('dynatable');
	dynatable.sorts.clear();
	dynatable.sorts.add("count", -1);
	dynatable.process();

	var canvas = new EJS({
		url : 'ejs/canvas.ejs'
	}).render();

	if ($("#topVideosColumn").find(".canvas") != null) {
		$("#topVideosColumn").remove(".canvas");
	}
	$("#topVideosColumn").append(canvas);
	var chartData = JSON.parse(columnData);
	var chartObj = {
		'chart' : {
			'type' : 'column'
		},
		'title' : {
			'text' : '热门视频排行'
		},
		'xAxis' : {
			'categories' : chartData['categories'],
		},
		'yAxis' : {
			'min' : 0,
			'title' : {
				'text' : 'Times of watching'
			}
		},
		'tooltip' : {
			'headerFormat' : '<span style="font-size:10px">{point.key}</span><table>',
			'pointFormat' : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>'
					+ '<td style="padding:0"><b>{point.y:.1f}</b></td></tr>',
			'footerFormat' : '</table>',
			'shared' : 'true',
			'useHTML' : 'true'
		},
		'plotOptions' : {
			'column' : {
				'pointPadding' : 0.2,
				'borderWidth' : 0
			}
		},
		'series' : chartData['series']
	};
	$("#topVideosColumn").find(".canvas").highcharts(chartObj);
	$("[href='#topVideos']").tab('show');
};

function insertVideoByStudentData(tableData, columnData) {
	var cols = [ "video_id", "count" ];
	var uuid = getUUID();
	var obj = new Object();
	obj.tid = uuid;
	obj.columns = cols;
	var tbl = new EJS({
		url : 'ejs/table.ejs'
	}).render(obj);
	if ($("#videoByStudentTable").find("table") != null) {
		$("#videoByStudentTable").remove("table");
	}
	$("#videoByStudentTable").append(tbl);
	var myRecords = JSON.parse(tableData);
	$("#videoByStudentTable").find("table").dynatable({
		dataset : {
			records : myRecords
		}
	});
	var dynatable = $("#videoByStudentTable").find("table").data('dynatable');
	dynatable.sorts.clear();
	dynatable.sorts.add("count", -1);
	dynatable.process();

	var canvas = new EJS({
		url : 'ejs/canvas.ejs'
	}).render();

	if ($("#videoByStudentColumn").find(".canvas") != null) {
		$("#videoByStudentColumn").remove(".canvas");
	}
	$("#videoByStudentColumn").append(canvas);
	var chartData = JSON.parse(columnData);
	var chartObj = {
		'chart' : {
			'type' : 'column'
		},
		'title' : {
			'text' : '热门视频排行'
		},
		'xAxis' : {
			'categories' : chartData['categories'],
		},
		'yAxis' : {
			'min' : 0,
			'title' : {
				'text' : 'Times of watching'
			}
		},
		'tooltip' : {
			'headerFormat' : '<span style="font-size:10px">{point.key}</span><table>',
			'pointFormat' : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>'
					+ '<td style="padding:0"><b>{point.y:.1f}</b></td></tr>',
			'footerFormat' : '</table>',
			'shared' : 'true',
			'useHTML' : 'true'
		},
		'plotOptions' : {
			'column' : {
				'pointPadding' : 0.2,
				'borderWidth' : 0
			}
		},
		'series' : chartData['series']
	};
	$("#videoByStudentColumn").find(".canvas").highcharts(chartObj);
	$("[href='#videoByStudent']").tab('show');
};

function insertVideoPauseData(tableData, columnData) {
	var cols = [ "time", "count" ];
	var uuid = getUUID();
	var obj = new Object();
	obj.tid = uuid;
	obj.columns = cols;
	var tbl = new EJS({
		url : 'ejs/table.ejs'
	}).render(obj);
	if ($("#videoPauseTable").find("table") != null) {
		$("#videoPauseTable").remove("table");
	}
	$("#videoPauseTable").append(tbl);
	var myRecords = JSON.parse(tableData);
	console.log(myRecords);
	$("#videoPauseTable").find("table").dynatable({
		dataset : {
			records : myRecords
		}
	});
	var dynatable = $("#videoPauseTable").find("table").data('dynatable');
	dynatable.sorts.clear();
	dynatable.sorts.add("count", -1);
	dynatable.process();

	var canvas = new EJS({
		url : 'ejs/canvas.ejs'
	}).render();

	if ($("#videoPauseColumn").find(".canvas") != null) {
		$("#videoPauseColumn").remove(".canvas");
	}
	$("#videoPauseColumn").append(canvas);
	var chartData = JSON.parse(columnData);
	var chartObj = {
		'chart' : {
			'type' : 'column'
		},
		'title' : {
			'text' : '视频暂停统计'
		},
		'xAxis' : {
			'categories' : chartData['categories'],
		},
		'yAxis' : {
			'min' : 0,
			'title' : {
				'text' : 'Times of watching'
			}
		},
		'tooltip' : {
			'headerFormat' : '<span style="font-size:10px">{point.key}</span><table>',
			'pointFormat' : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>'
					+ '<td style="padding:0"><b>{point.y:.1f}</b></td></tr>',
			'footerFormat' : '</table>',
			'shared' : 'true',
			'useHTML' : 'true'
		},
		'plotOptions' : {
			'column' : {
				'pointPadding' : 0.2,
				'borderWidth' : 0
			}
		},
		'series' : chartData['series']
	};
	$("#videoPauseColumn").find(".canvas").highcharts(chartObj);
	$("[href='#videoPause']").tab('show');
};

function insertVideoMoveData(tableData, columnData) {
	alert(columnData);
	var cols = [ "time", "count" ];
	var uuid = getUUID();
	var obj = new Object();
	obj.tid = uuid;
	obj.columns = cols;
	var tbl = new EJS({
		url : 'ejs/table.ejs'
	}).render(obj);
	if ($("#videoMoveTable").find("table") != null) {
		$("#videoMoveTable").remove("table");
	}
	$("#videoMoveTable").append(tbl);
	var myRecords = JSON.parse(tableData);
	$("#videoMoveTable").find("table").dynatable({
		dataset : {
			records : myRecords
		}
	});
	var dynatable = $("#videoMoveTable").find("table").data('dynatable');
	dynatable.sorts.clear();
	dynatable.sorts.add("count", -1);
	dynatable.process();

	var canvas = new EJS({
		url : 'ejs/canvas.ejs'
	}).render();

	if ($("#videoMoveColumn").find(".canvas") != null) {
		$("#videoMoveColumn").remove(".canvas");
	}
	$("#videoMoveColumn").append(canvas);
	var chartData = JSON.parse(columnData);
	var chartObj = {
		'chart' : {
			'type' : 'column'
		},
		'title' : {
			'text' : '视频快进统计'
		},
		'xAxis' : {
			'categories' : chartData['categories'],
		},
		'yAxis' : {
			'min' : 0,
			'title' : {
				'text' : 'Times of watching'
			}
		},
		'tooltip' : {
			'headerFormat' : '<span style="font-size:10px">{point.key}</span><table>',
			'pointFormat' : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>'
					+ '<td style="padding:0"><b>{point.y:.1f}</b></td></tr>',
			'footerFormat' : '</table>',
			'shared' : 'true',
			'useHTML' : 'true'
		},
		'plotOptions' : {
			'column' : {
				'pointPadding' : 0.2,
				'borderWidth' : 0
			}
		},
		'series' : chartData['series']
	};
	$("#videoMoveColumn").find(".canvas").highcharts(chartObj);
	$("[href='#videoMove']").tab('show');
};

function insertVideoMoveData_Stacked(columnData) {
	var canvas = new EJS({
		url : 'ejs/canvas.ejs'
	}).render();

	if ($("#videoMoveColumn").find(".canvas") != null) {
		$("#videoMoveColumn").remove(".canvas");
	}
	$("#videoMoveColumn").append(canvas);
	var chartData = JSON.parse(columnData);
	var chartObj = {
		'chart' : {
			'type' : 'column'
		},
		'title' : {
			'text' : '视频快进统计'
		},
		'xAxis' : {
			'categories' : chartData['categories'],
		},
		'yAxis' : {
			'min' : 0,
			'title' : {
				'text' : 'Times of watching'
			},
			'stackLabels' : {
				enabled : true,
				style : {
					fontWeight : 'bold',
					color : (Highcharts.theme && Highcharts.theme.textColor)
							|| 'gray'
				}
			}
		},
		'tooltip' : {
			formatter : function() {
				return this.series.name + ': ' + this.y + '<br/>' + 'Total: '
						+ this.point.stackTotal;
			}
		},
		'plotOptions' : {
			'column' : {
				'stacking' : 'normal',
				'dataLabels' : {
					enabled : true,
					color : (Highcharts.theme && Highcharts.theme.dataLabelsColor)
							|| 'white',
					style : {
						textShadow : '0 0 3px black'
					}
				}
			}
		},
		'series' : chartData['series']
	};
	$("#videoMoveColumn").find(".canvas").highcharts(chartObj);
	$("[href='#videoMove']").tab('show');
};

function insertVideoMoveData_Raphael(columnData) {
	columnData = {
		"size" : 3,
		"axis" : [ "0~100s", "100~200s", "200~300s" ],
		"data" : {
			"0~100s" : [ 3, 4, 5 ],
			"100~200s" : [ 1, 2, 3 ],
			"200~300s" : [ 0, 1, 0 ]
		},
		"max" : 5
	}

	var size = columnData["size"];
	var axis = columnData["axis"];
	var data = columnData["data"];
	var maxV = columnData["max"];

	var width = $("#videoMoveColumn").width();
	var height = 400;
	console.log(height);
	var paper = new Raphael(document.getElementById('videoMoveColumn'), width,
			height);
	var offsetX = width * 0.1;
	var offsetY = height * 0.1;
	console.log(offsetY);
	var interX = width * 0.8 / size;
	var interY = height * 0.8 / size;
	var radius = (width > height ? height / 2 : width / 2) * 0.8 * 0.8 / size;
	console.log(radius);
	// paper.circle(100, 100, 80);
	ttt = new Array(axis.length);
	for (var i = 0; i < ttt.length; ++i) {
		ttt[i] = new Array(axis.length);
	}
	for (var i = 0; i < axis.length; ++i) {
		paper.text(offsetX + interX * i, height - offsetY, axis[i]);
		for (var j = 0; j < axis.length; ++j) {
			var cls = (function() {
				var d = data[axis[i]][j];
				var ic = i;
				var jc = j;
				ttt[ic][jc] = paper.text(offsetX + interX * (ic + 0.5), height - offsetY
						- interY * (jc + 0.5), d.toString());
				ttt[ic][jc].hide();
				return {
					"in":function(evt) {
						if (d != 0) {
							ttt[ic][jc].show()
						}
					},
					"out": function(evt) {
						if (d != 0) {
							ttt[ic][jc].hide()
						}
					}
				};
			});
			var circle = paper.circle(offsetX + interX * (i + 0.5), height
					- offsetY - interY * (j + 0.5), radius * data[axis[i]][j] / maxV);
			circle.attr({fill: "#3D5C9D", "stroke-width": "2", stroke:"#3D5C9D"});
			circle.mouseover(cls()["in"]);
			circle.mouseout(cls()["out"])
		}
	}
	// var circle = paper.circle(100, 100, 80);
}

function insertVideoInfoData(columnData) {
	var canvas = new EJS({
		url : 'ejs/canvas.ejs'
	}).render();

	if ($("#videoInfoColumn").find(".canvas") != null) {
		$("#videoInfoColumn").remove(".canvas");
	}
	$("#videoInfoColumn").append(canvas);
	var chartData = JSON.parse(columnData);
	var chartObj = {
		'chart' : {
			'type' : 'area'
		},
		'title' : {
			'text' : '视频播放统计'
		},
		'xAxis' : {
			'categories' : chartData['categories'],
			'tickmarkPlacement': 'on',
            'title': {
                enabled: false
            }
		},
		'yAxis' : {
			'min' : 0,
			'title' : {
				'text' : 'Times of watching'
			}
		},
		'tooltip' : {
			'shared': true
		},
		'plotOptions' : {
			'area': {
                'stacking': 'normal',
                'lineColor': '#666666',
                'lineWidth': 1,
                'marker': {
                    'lineWidth': 1,
                    'lineColor': '#666666'
                }
            }
		},
		'series' : chartData['series']
	};
	$("#videoInfoColumn").find(".canvas").highcharts(chartObj);
	$("[href='#videoInfo']").tab('show');
};

function insertVideoByHomeworkData(tableData, columnData) {
	var cols = [ "day", "count" ];
	var uuid = getUUID();
	var obj = new Object();
	obj = new Object();
	obj.tid = uuid;
	obj.columns = cols;
	var tbl = new EJS({
		url : 'ejs/table.ejs'
	}).render(obj);
	if ($("#videoByHomeworkTable").find("table") != null) {
		$("#videoByHomeworkTable").remove("table");
	}
	$("#videoByHomeworkTable").append(tbl);
	var myRecords = JSON.parse(tableData);
	$("#videoByHomeworkTable").find("table").dynatable({
		dataset : {
			records : myRecords
		}
	});
	var dynatable = $("#videoByHomeworkTable").find("table").data('dynatable');
	dynatable.sorts.clear();
	dynatable.sorts.add("day", 1);
	dynatable.process();

	var canvas = new EJS({
		url : 'ejs/canvas.ejs'
	}).render();

	if ($("#videoByHomeworkColumn").find(".canvas") != null) {
		$("#videoByHomeworkeColumn").remove(".canvas");
	}
	$("#videoByHomeworkColumn").append(canvas);
	var chartData = JSON.parse(columnData);
	var chartObj = {
		'chart' : {
			'type' : 'column'
		},
		'title' : {
			'text' : '观看人数--距离开始时间'
		},
		'xAxis' : {
			'categories' : chartData['categories'],
		},
		'yAxis' : {
			'min' : 0,
			'title' : {
				'text' : 'Times of watching'
			}
		},
		'tooltip' : {
			'headerFormat' : '<span style="font-size:10px">{point.key}</span><table>',
			'pointFormat' : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>'
					+ '<td style="padding:0"><b>{point.y:.1f}</b></td></tr>',
			'footerFormat' : '</table>',
			'shared' : 'true',
			'useHTML' : 'true'
		},
		'plotOptions' : {
			'column' : {
				'pointPadding' : 0.2,
				'borderWidth' : 0
			}
		},
		'series' : chartData['series']
	};
	$("#videoByHomeworkColumn").find(".canvas").highcharts(chartObj);
	$("[href='#videoByHomework']").tab('show');
};

function insertHomeworkByNameData(columnData) {
	var canvas = new EJS({
		url : 'ejs/canvas.ejs'
	}).render();

	if ($("#homeworkByNameColumn").find(".canvas") != null) {
		$("#homeworkByNameColumn").remove(".canvas");
	}
	$("#homeworkByNameColumn").append(canvas);
	var chartData = JSON.parse(columnData);
	var chartObj = {
		'chart' : {
			'type' : 'area'
		},
		'title' : {
			'text' : '完成作业/观看视频'
		},
		'xAxis' : {
			'categories' : chartData['categories'],
			'tickmarkPlacement': 'on',
            'title': {
                enabled: false
            }
		},
		'yAxis' : {
			'min' : 0,
			'title' : {
				'text' : 'Times of watching'
			}
		},
		'tooltip' : {
			'shared': true
		},
		'plotOptions' : {
			'area': {
                'stacking': 'normal',
                'lineColor': '#666666',
                'lineWidth': 1,
                'marker': {
                    'lineWidth': 1,
                    'lineColor': '#666666'
                }
            }
		},
		'series' : chartData['series']
	};
	$("#homeworkByNameColumn").find(".canvas").highcharts(chartObj);
	$("[href='#homeworkByName']").tab('show');
};



function getUUID() {
	var d = new Date().getTime();
	var uuid = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g,
			function(c) {
				var r = (d + Math.random() * 16) % 16 | 0;
				d = Math.floor(d / 16);
				return (c == 'x' ? r : (r & 0x7 | 0x8)).toString(16);
			});
	return uuid;
};

function progressBarUpdate(value, name) {
	$("." + name).attr("style", "width:" + value + "%");
};

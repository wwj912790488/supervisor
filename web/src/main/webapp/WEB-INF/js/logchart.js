function init() {
    var d = new Date();
    d.setDate(d.getDate()-1)

    function addzero(v) {
        if (v < 10) return '0' + v;
        return v.toString();
    }


    var s = d.getFullYear().toString() + '-' + addzero(d.getMonth() + 1) + '-' + addzero(d.getDate())
    $("input[name=startTime]").val(s + " 00:00:00");
    $("input[name=endTime]").val(s + " 23:59:59");


}

// 基于准备好的dom，初始化echarts实例
var myChart = echarts.init(document.getElementById('main'));
myChart.showLoading({
    text: '正在努力的读取数据中...',    //loading话术
});


function chart() {
    var channelNames = [], chartDataConfig = [], logTime = [];
   /* var startVal = $("input[name=startTime]").val();
    var endVal = $("input[name=endTime]").val();*/
    $.ajax({
        url: "/log/baiduchart",
        type: 'GET',
        data: $("#search-form").serialize(),
        dataType: 'json',
        success: function (data) {

            for (var i = 0; i < data.length; i++) {
                var charDataInt = [];
                var chartDataSet = data[i];
                for (var j = 0; j < chartDataSet.length; j++) {
                    charDataInt.push(parseInt(chartDataSet[j].count));
                    if (i == 0) {
                        logTime.push(chartDataSet[j].title);
                    }
                }
                chartDataConfig.push({
                    name: data[i][0].channelName,
                    type: 'bar',
                    data: charDataInt,
                    markPoint: {
                        data: [
                            {type: 'max', name: '最大值'},
                            {type: 'min', name: '最小值'}
                        ]
                    },
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    }
                });
                channelNames.push(data[i][0].channelName);
            }
            getData(channelNames,logTime);
            myChart.setSeries(chartDataConfig);
        }
    });
}


function getData(channelNames,logTime) {
    myChart.hideLoading();
// 指定图表的配置项和数据
    option = {
        title: {
            text: '内容报警日志报表',
            subtext: '日志报表'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: channelNames
        },
        toolbox: {
            show: true,
            feature: {
                mark: {show: true},
                dataView: {show: true, readOnly: false},
                magicType: {show: true, type: ['line', 'bar']},
                restore: {show: true},
                saveAsImage: {show: true}
            }
        },
        calculable: true,
        xAxis: [
            {
                type: 'category',
                data: logTime
            }
        ],
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: []
    };

    // 图表清空-------------------
    myChart.clear();
// 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option, true);
}

init();
chart();
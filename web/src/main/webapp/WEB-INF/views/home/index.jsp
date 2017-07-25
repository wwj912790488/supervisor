<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/jquery-ui.css,/css/home.css"/>
    <utils:js path="/js/plugins/jquery.extend.js,/js/jquery-ui.js,/js/plugins/nicescroll/jquery.nicescroll.min.js,/js/TMPlayerMngr.js,/js/My97DatePicker/WdatePicker.js,/js/Task.js"/>
    <utils:js path="/js/plugins/chart/Chart.js"/>
    <utils:js path="/js/plugins/backbone/underscore.js,/js/plugins/backbone/backbone.js,/js/plugins/backbone/backbone.marionette.js,/js/plugins/backbone/backbone-validation.js"/>

    <utils:js path="/js/home.js"/>

    <script type="text/javascript">
        $(function () {
            StreamManager.init();
            var TMPlayerMngrobj = new TMPlayerMngr();
            TMPlayerMngrobj.initChannelPlayback();
        });
    </script>
</head>

<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
       
        <div class="content-wrapper clearfix content-right-column">

            <div class="channel-lists sidebar">
                <p class="title">频道分组列表
                </p>
                <ul id="accordion"></ul>
            </div>

            <div class="content" style="background:#eee;">
                <div id="DialogFrameTemplate">
                    <div class="TMPlayer" id="TMPlayer">
                        <div>
                            <span class="player-title">正在播放：</span><span class="player-title" id="playbackChannelInfo"></span>
                        </div>
                        <div class="dialog-content">
                            <div id="ProgramPreviewTmpl" style="display: block;margin_left:20px;height:400px;">
                                <div id="PreviewDlgBody">
                                        <div style="width: 520px;height:390px;margin-left: 100px;">
                                            <div class="TMPlayerContainer" style="width:100%; height:100%; text-align:center;">
                                                <object classid="clsid:b14dcdc6-dc3a-4e99-80b2-3169b06ef069"
                                                        codebase="<c:url value="/tmplayer/TMPlayer.CAB#Version=2,0,0,72"/>"
                                                        id="ArcSoft_TMPlayer"
                                                        width="520" height="390"
                                                        standby="Loading ArcSoft TotalMedia Player ...">
                                                    <param name="ApplicationType" value="0"/>
                                                    <param name="PanelType" value="3"/>
                                                    <param name="ResizeMode" value="7"/>
                                                    
                                                    <br/><br/><br/><br/><br/><br/>

                                                    <p style="font-size:12px">
                                                        当前网页需要安装媒体播放器“ ArcSoft TotalMedia Player”。<br/>
                                                        如果你没有看到提示, 请确认系统和浏览器的安全权限。<br/>
                                                    </p>
                                                    <a href="<c:url value="/tmplayer/TMPSetup.exe"/> ">
                                                        <b style="color:green;font-size:13px">或者点击此处安装“ ArcSoft TotalMedia
                                                            Player”。</b><br/>
                                                        <b style="color:green;font-size:13px">请保存安装文件至本地后,使用管理员权限安装。</b>
                                                    </a><br/>
                                                    <a href="#" onclick="location.reload()">
                                                        <b style="font-size:13px">安装完成后，点此刷新本页面。</b>
                                                    </a><br/>
                                                </object>
                                            </div>
                                        </div>
                                </div>
                            </div>
                        </div>          
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <c:if test="${supportmosaic==false}">
        <div class="content-wrapper list">
            <div class="head clearfix">
                <div class="title">

                    <span class="title-icon"></span>
                    <span class="title-name">频道信息</span>
                </div>
                <div class="dialog-btns">
                    <div id="refresh-channel-logs-btn" class="action-btn">
                        <a>
                            <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">手动刷新</span>
                        </span>
                            <span class="btn-right"></span>
                        </a>
                    </div>
                </div>
                <!-- <div class="get-channel-info" style="display:none;">
                    <div>
                        <form id="get-channel-log-form" name="get-channel-log-form" method="post" action="<c:url value="/channel/cd"/>">
                            <input type="hidden" name="export">
                            <input type="text" name="startTime" value="" readonly>
                            <input type="text" name="endTime" value="" readonly>
                            <select name="type" class="status_sel">
                                <option value="99" selected>所有</option>
                            </select>
                            <input type="text" id="channelName" name="channelName" autocomplete="off" value=""/>
                        </form>
                    </div>

                    <div class="action-btn">
                        <a id="get-channel-info-btn">
                            <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">GetChannelInfo</span>
                            <span class="btn-icon"></span>
                        </span>
                            <span class="btn-right"></span>
                        </a>
                    </div>
                </div> -->
            </div>
            <div class="tab-content" id="channel-info-content">
                <table id="channel-info-table">
                    <thead>
                    <tr>
                        <th width="10%;">任务编号</th>
                        <th width="15%;">频道名称</th>
                        <th width="15%;">报警类型</th>
                        <th width="20%;">开始时间</th>
                        <th width="20%;">结束时间</th>
                        <th width="10%;">告警记录</th>
                        <th width="10%;">导出视频</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
                <div id="switch-to-logs-page-btn" style="display:none; width:100%;">
                    <div style="width:8px;display:inline-block;"></div>
                    <div class="action-btn">
                        <a href="<c:url value="/log/cd"/>">
                            <span class="btn-left"></span>
                        <span class="btn-middle" style="width:1000px;">
                            <span class="btn-text">查看更多</span>
                        </span>
                            <span class="btn-right"></span>
                        </a>
                    </div>
                    
                </div>
            </div>
        </div>
        </c:if>
    </div>
    <jsp:include page="/WEB-INF/views/common/pager.jsp"/>
    <div class="push"></div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<script type="text/x-handlebars-template" id="groupTemplate">
<li data-id="-1">
    <h3>未分组</h3>
<div><ul></ul></div>
</li>
{{#each groups}}
<li data-id="{{id}}">
    <h3>{{name}}</h3>
    <div><ul></ul></div>
</li>
{{/each}}
</script>

<script type="text/x-handlebars-template" id="channellogsTemplate">
{{#each channellogs}}
    <tr class="tasks">
        <td class="log-id">{{taskId}}<input type="hidden" id="contentdetectlogid" value="{{id}}"/></td>
        <td>{{channelName}}</td>
        <td>
        <span>
        {{#ifCond type "==" "0"}}黑场{{/ifCond}}
        {{#ifCond type "==" "31"}}流中断{{/ifCond}}
        {{#ifCond type "==" "32"}}
            {{#ifCond soundTrack "==" "0"}}静音-左声道{{/ifCond}}
            {{#ifCond soundTrack "==" "1"}}静音-右声道{{/ifCond}}
            {{#ifCond soundTrack "==" "2"}}静音-中置声道{{/ifCond}}
            {{#ifCond soundTrack "==" "3"}}静音-低音声道{{/ifCond}}
            {{#ifCond soundTrack "==" "4"}}静音-左后置声道{{/ifCond}}
            {{#ifCond soundTrack "==" "5"}}静音-右后置声道{{/ifCond}}
            {{#ifCond soundTrack "==" "6"}}静音-左环绕声道{{/ifCond}}
            {{#ifCond soundTrack "==" "7"}}静音-右环绕声道{{/ifCond}}
            {{#ifCond soundTrack "==" "255"}}静音{{/ifCond}}
        {{/ifCond}}
        {{#ifCond type "==" "33"}}低音{{/ifCond}}
        {{#ifCond type "==" "34"}}高音{{/ifCond}}
        {{#ifCond type "==" "37"}}
            {{#ifCond soundTrack "==" "0"}}爆音-左声道{{/ifCond}}
            {{#ifCond soundTrack "==" "1"}}爆音-右声道{{/ifCond}}
            {{#ifCond soundTrack "==" "2"}}爆音-中置声道{{/ifCond}}
            {{#ifCond soundTrack "==" "3"}}爆音-低音声道{{/ifCond}}
            {{#ifCond soundTrack "==" "4"}}爆音-左后置声道{{/ifCond}}
            {{#ifCond soundTrack "==" "5"}}爆音-右后置声道{{/ifCond}}
            {{#ifCond soundTrack "==" "6"}}爆音-左环绕声道{{/ifCond}}
            {{#ifCond soundTrack "==" "7"}}爆音-右环绕声道{{/ifCond}}
            {{#ifCond soundTrack "==" "255"}}爆音{{/ifCond}}
        {{/ifCond}}

        {{#ifCond type "==" "2"}}静帧{{/ifCond}}
        {{#ifCond type "==" "1"}}绿场{{/ifCond}}
        {{#ifCond type "==" "27"}}CC错误{{/ifCond}}
        {{#ifCond type "==" "28"}}Audio丢失{{/ifCond}}
        {{#ifCond type "==" "29"}}Video丢失{{/ifCond}}
        {{#ifCond type "==" "30"}}信源中断{{/ifCond}}
            </span>
        </td>
 	<td>
		{{#datetime startTimeAsDate}}{{/datetime}}</td>
        <td>
            {{#if endTime}}            
        	{{#datetime endTimeAsDate}}{{/datetime}}</td>
            {{/if}}
        <td>
        {{#ifNotEmpty videoFilePath}}
            <div class="play-btn ProgramPreviewTrigger">
                <a><span class="playbtn"></span></a>
            </div>
        {{/ifNotEmpty}}
        </td>
		<td></td>
    </tr>
{{/each}}
</script>

</body>
</html>

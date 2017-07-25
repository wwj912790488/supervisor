<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css
            path="/css/screen.css,/js/plugins/farbtastic/farbtastic.css,/js/plugins/gridster/jquery.gridster.css,/js/plugins/toast/jquery.toast.min.css"/>
    <utils:js
            path="/js/plugins/backbone/underscore.js,/js/plugins/backbone/backbone.js,/js/plugins/backbone/backbone.marionette.js,/js/plugins/farbtastic/farbtastic.js,/js/plugins/gridster/jquery.gridster.js,/js/plugins/toast/jquery.toast.min.js"/>
    <utils:js path="/js/plugins/nicescroll/jquery.nicescroll.min.js,/js/screen.js"/>
    <script type="text/javascript">
        $(function () {
            var opsList = new OpsList();
            var screenManagerObj = new ScreenManager(opsList);
            screenManagerObj.init();
        });

        function checkDebug(obj) {
            var flag = $("#isCheckDbug").val();
            var id = $(obj).attr("data-id");
            $("#wallId").val(id);
            if (flag == "true") {
                $.ajax({
                    url: "/screen/allScreenByWallId?wallId=" + id,
                    type: 'GET',
                    dataType: 'json',
                    success: function (data) {
                        var str = "";
                        for (var i = 0; i < data.length; i++) {
                            var screen = data[i];
                            str = str + ("<span>编号:" + (i + 1) + "</span>&nbsp;&nbsp;&nbsp;<span>屏幕id:" + screen.id + "</span>&nbsp;&nbsp;&nbsp;<span>运行状态:" + screen.status + "</span>&nbsp;&nbsp;&nbsp;<span>输出地址:" + screen.outputAddr + "    " + screen.outputAddr2 + "</span></br>")

                        }
                        $("#screen-settings-debug-info").html(str);
                    }
                })

            }
        }
    </script>
</head>
<body>

<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container" id="container">

        <div class="secondary-nav">
            <ul>
                <li class="active">
                    <a data-toggle="tab" href="/screen/index">屏幕墙列表</a>
                </li>
                <li>
                    <a data-toggle="tab" href="/screen/control">任务画面控制</a>
                </li>
            </ul>
        </div>

        <div class="content-wrapper clearfix content-right-column">
            <div id="screen-wall-lists" class="sidebar">
                <p class="title">屏幕墙列表
                    <a class="add-btn" id="wall-add-btn"></a>
                </p>
                <ul id="screen-wall-lists-nav">

                </ul>
            </div>
            <div id="screen-settings" class="content">
                <div class="head">
                    <div class="title">
                        <span class="title-icon"></span>
                        <p class="title-name" id="screen-setttings-title"></p>
                    </div>
                </div>
                <div class="body" style="display: none;">
                    <div class="tabs" id="set-wall-tab">
                        <ul class="tab-links clearfix">
                            <li id="tab-link-screen-setting"><a href="#tab-screen-setting">屏幕墙设置</a></li>
                            <li id="tab-link-channel-setting"><a href="#tab-channel-setting">频道设置</a></li>
                        </ul>
                    </div>
                    <div class="tab-content">
                        <div id="tab-screen-setting">
                            <div id="grid"></div>
                            <div id="position-screen-wall-btns">
                                <div id="position-screen-wall-recognize-btn" class="dialog-btn">
                                    <a>
                                        <span class="btn-left"></span>
                                        <span class="btn-middle">
                                            <span class="btn-text">识别</span>
                                        </span>
                                        <span class="btn-right"></span>
                                    </a>
                                </div>
                                <div id="position-screen-wall-save-btn" class="dialog-btn" style="display:none;">
                                    <a>
                                        <span class="btn-left"></span>
                                        <span class="btn-middle">
                                            <span class="btn-text">使用现有顺序</span>
                                        </span>
                                        <span class="btn-right"></span>
                                    </a>
                                </div>
                            </div>
                            <c:if test="${debug}">
                            <div style="margin-left: 350px;margin-top: 25px">
                                <input type="hidden" name="wallId" id="wallId">
                                <select id="screenName" >
                                </select>
                                <input type="text" style="width: 60px;" name="settingScreenName" id="settingScreenName">
                                <input type="button" id="saveScreenName" name="saveScreenName"  value="保存" style="margin-left: 50px;">
                            </div>
                            </c:if>
                        </div>

                        <div id="tab-channel-setting">

                        </div>
                        <div id="opslist"></div>
                        <div id="layoutlist"></div>
                        <div id="message-style-modal" style="width:800px;"></div>

                        <c:if test="${debug}">
                            <div id="screen-settings-debug-info" style="height:150px; overflow:auto">
                            </div>
                        </c:if>
                    </div>

                </div>

            </div>
            <input id="isCheckDbug" type="hidden" value="${debug}"/>


        </div>
    </div>
    <div class="push"></div>
</div>
<div id="add-wall-dialog" class="modal" style="display:none;width: 700px;">
    <input type="hidden" id="wall_type">
    <div class="dialog-caption">添加屏幕墙</div>
    <div class="dialog-content">
        <div id="tab-wall-setting" class="equal-column clearfix">
            <form id="wall-setting-form">
                <div class="left-column">
                    <input type="hidden" name="id" id="id"></input>
                    <input type="hidden" name="version" id="version"></input>
                    <div class="line" style="margin-bottom:20px;">
                        <span style="width:80px;">名称：</span>
                        <input type="text" style="width:160px;" name="name" id="name"></input>
                    </div>
                    <div class="line" style="margin-bottom:30px;">
                        <span style="width:80px;">类型：</span>
                        <select style="width:160px;" name="type" id="type">
                            <option value="1">OPS</option>
                            <option value="2">SDI</option>
                        </select>
                    </div>
                    <div class="line" style="margin-bottom:30px;">
                        <span style="width:80px;">行：</span>
                        <input type="text" style="width:35px;" name="rowCount" id="rowCount"></input>
                        <span style="width:73px;">列：</span>
                        <input type="text" style="width:35px;" name="columnCount" id="columnCount"></input>
                    </div>
                    <div id="small-grid" style="margin-bottom:20px;"></div>
                 <%--   <div id="settingScreenName" style=" margin-left: 30px;">
                        <span>分组名称：</span>
                        <input type="text" style="width: 60px;" name="screenName" id="ScreenName">
                        <input type="button" value="保存" style="margin-left: 50px;">
                    </div>--%>
                </div>

                <div class="right-column" style="display:none;">
                    <span id="device-list-title">设备列表：</span>
                    <div id="device-list">
                    </div>
                    <div id="sdi-list">
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div class="dialog-btns">
        <div id="add-wall-cancel-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">取消</span>
                    </span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="add-wall-ok-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">确定</span>
                    </span>
                <span class="btn-right"></span>
            </a>
        </div>
    </div>

</div>

<div id="singlechannellist" style="display:none;"></div>
<div id="multiplechannellist" style="display:none;"></div>

<div id="loading-dialog" class="modal" style="display:none;width: 350px;"></div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
<script type="text/x-handlebars-template" id="addWallTemplate">
    <div style="text-align:center;margin:20px 0px;">
        <form id="wallForm">
            <span>屏幕墙名称：</span>
            <input type="text" id="wallName" name="wallName" required/>
        </form>
    </div>
</script>

<script type="text/x-handlebars-template" id="wallTemplate">
    {{#each walls}}
    <li data-id="{{id}}" data-version="{{version}}" data-type="{{type}}" data-rowcount="{{rowCount}}"
        data-columncount="{{columnCount}}" data-name="{{name}}" onclick="checkDebug(this)">
        <a>
            <span class="nav-icon"></span>
            <span class="nav-title">{{name}}</span>
            <span class="remove-btn"></span>
            <span class="edit-btn"></span>
        </a>
    </li>
    {{/each}}
</script>

<script type="text/x-handlebars-template" id="opsTemplate">
    {{#each opsServers}}
    <div class="device-item">
        <input type="checkbox" value="{{id}}" name="opsServer"></input>
        <span>{{ip}}</span>
    </div>
    {{/each}}
</script>
<script type="text/x-handlebars-template" id="sdiTemplate">
    {{#each sdiOutputs}}
    {{#each sdis}}
    <div class="device-item">
        <input type="checkbox" value="{{id}}" name="sdiOutput"></input>
        <span class="parent-device-name">设备{{../name}}: </span><span class="sdi-name">SDI槽{{@index}}</span>
    </div>
    {{/each}}
    {{/each}}
</script>
<script type="text/x-handlebars-template" id="loadingTemplate">
    <div class="dialog-caption">提示</div>
    <div class="dialog-content">
        <div style="height: 60px;">
            <div style="margin-left: 30%;padding-top: 20px;">
                <img src="<c:url value="/images/spinner.gif"/> ">
                <span style="position: absolute;margin-left: 5px;">操作处理中，请稍等...</span>
            </div>
        </div>
    </div>
</script>
<script type="text/x-handlebars-template" id="taskProfileTemplate">
    <span>任务模版：</span>

    <select id="taskProfileId" <%--{{#if {{status}} == "RUNNING" }}disabled="disabled"{{/if}}--%>  >
        {{#select selectedTaskProfileId}}
        <option value="-1">请选择</option>
        {{#each taskProfiles}}
        <option value="{{id}}">{{name}}</option>
        {{/each}}
        {{/select}}
    </select>
</script>
<script type="text/x-handlebars-template" id="deviceListTemplate">
    <span>设备：</span>
    <select id="device" style="width: 75px;">
        {{#select selectedDeviceId}}
        <option value="-1">自动分配</option>
        {{#each devices}}
        <option value="{{id}}">{{name}}</option>
        {{/each}}
        {{/select}}
    </select>
    <span>GPU Index：</span>
    <select id="gpuIndex" style="width: 75px;">
        {{#select selectedGpuIndex}}
        <option value="-1">自动分配</option>
        <option value="0">1</option>
        <option value="1">2</option>
        <option value="2">3</option>
        <option value="3">4</option>
        <option value="4">5</option>
        <option value="5">6</option>
        <option value="6">7</option>
        <option value="7">8</option>
        {{/select}}
    </select>
</script>

<script type="text/x-handlebars-template" id="opsItemTemplate">
    <input type="checkbox" name="ops-item-select" {{#ifCond wallPosition '!=' '-1'}}disabled{{/ifCond}}/>
    <span>{{ip}}</span>
</script>
<script type="text/x-handlebars-template" id="opsListTemplate">
    <div id="opsList-title" class="dialog-caption">绑定OPS</div>
    <div id="opsList-filter">
        <span>过滤设备名称:</span><input type="text" id="opsList-name-filter"/>
        <span>隐藏已绑定设备</span><input type="checkbox" id="opsList-used-filter"/>
        <span id="opsList-message"></span>
    </div>
    <div id="opslist-container">
    </div>
    <div id="opslist-submit" class="dialog-btns">
        <div id="opslist-cancel-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">取消</span>
                        </span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="opslist-bind-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">绑定</span>
                        </span>
                <span class="btn-right"></span>
            </a>
        </div>

    </div>
</script>

<script type="text/x-handlebars-template" id="channelItemTemplate">
    <input type="checkbox" name="channel-item-select"></input>
    <span>{{name}}</span>
</script>

<script type="text/x-handlebars-template" id="channelListTemplate">
    <div id="channelList-title" class="dialog-caption"></div>
    <div id="channelList-filter">
        <span>过滤频道名称:</span><input type="text" id="channelList-name-filter"/>
        <span>过滤频道标签:</span><input type="text" id="channelList-tag-filter"/>
        <input type="checkbox" id="channelList-used-filter" name="channelList-used-filter"></input>
        <span>过滤已添加频道</span>
        <span id="channelList-message"></span>
    </div>
    <div id="channellist-container">
    </div>
    <div id="channellist-submit" class="dialog-btns">
        <div id="channellist-cancel-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">取消</span>
                        </span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="channellist-select-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">确定</span>
                        </span>
                <span class="btn-right"></span>
            </a>
        </div>

    </div>
</script>
<script type="text/x-handlebars-template" id="messageStyleTemplate">
    <div id="style-title" class="dialog-caption">编辑样式</div>
    <div id="style-container" class="clearfix">
        <div id="style-edit-container">
            <div class="line">
                <span>字体：</span>
                <select id="style-font">
                    {{#select font}}
                    <option value="仿宋">仿宋</option>
                    <option value="宋体">宋体</option>
                    <option value="楷体">楷体</option>
                    <option value="微软雅黑">微软雅黑</option>
                    <option value="黑体">黑体</option>
                    {{/select}}
                </select>
                <span>字号：</span>
                <input type="text" id="style-font-size" name="style-font-size" value="{{size}}"></input>
            </div>
            <div class="line">
                <span>颜色：</span>
                <input type="text" id="style-font-color" name="style-font-color" value="{{color}}"></input>
                <span>透明度：</span>
                <input type="text" id="style-font-color-alpha" name="style-font-color-alpha" value="{{alpha}}"></input>
            </div>
            <div id="style-font-color-picker">
            </div>
            <div class="line">
                <span>X位置：</span>
                <input type="text" id="style-position-x" name="style-position-x" value="{{x}}"></input>
                <span>Y位置：</span>
                <input type="text" id="style-position-y" name="style-position-y" value="{{y}}"></input>
            </div>
            <div class="line">
                <span>宽度：</span>
                <input type="text" id="style-width" name="style-width" value="{{width}}"></input>
                <span>高度：</span>
                <input type="text" id="style-height" name="style-height" value="{{height}}"></input>
            </div>
        </div>
        <div id="style-preview-container">
            <iframe frameborder="0" id="style-preview-iframe" style="width:400px; height:300px;">

            </iframe>
        </div>
    </div>
    <div id="style-submit" class="dialog-btns">
        <div id="style-cancel-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">取消</span>
                        </span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="style-reset-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">恢复到默认样式</span>
                        </span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="style-save-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">保存</span>
                        </span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="style-save-default-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">保存并设为默认样式</span>
                        </span>
                <span class="btn-right"></span>
            </a>
        </div>
    </div>
</script>
<script type="text/x-handlebars-template" id="messageStylePreviewTemplate">
    <!DOCTYPE html>
    <html>
    <head>
    </head>
    <body>
    <style>
        span {
            display: inline-block;

        width: {

        {
            width
        }
        }
        px

        ;
        vertical-align: bottom

        ;
        word-spacing: normal

        ;
        }
        #border:after {
            display: inline-block;
            width: 0px;

        line-height: {

        {
            height
        }
        }
        px

        ;
        content:

        "\00A0"
        ;
        }
    </style>
    <div style="background-color:black; width:384px; height:216px; overflow:hidden;">
        <div id="border"
             style="width:{{width}}px;height:{{height}}px;position:relative;top:{{y}}px;left:{{x}}px; overflow:hidden;border: dotted white 1px;word-spacing: -10px;">
            <span style="font-family:{{font}};font-size:{{size}}px;color:{{color}};opacity:{{alpha}};margin:0px;text-align:center">消息显示在此处</span>
        </div>
    </div>
    </body>
    </html>
</script>
<script type="text/x-handlebars-template" id="screenTemplate">
    <div class="clearfix">
        <button type="button" id="select-layout-template" class="btn">选择画面模板</button>
        <input type="hidden" id="screen-status">
        <div style="display: inline;margin-left: 10px;" id="task-profile-container">
        </div>
        <div style="display: inline; margin-left: 10px;" id="device-container"></div>
        <c:if test="${supportmosaic}">
            <div style="display: inline;margin-left: 10px;">
                <span> 屏幕id: {{id}}</span>
            </div>
        </c:if>
        <div id="set-channel-btns">
            <div id="set-channel-add-btn" class="dialog-btn">
                <a>
                    <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">批量添加频道</span>
                    </span>
                    <span class="btn-right"></span>
                </a>
            </div>
            <div id="set-channel-start-btn" class="dialog-btn" style="display:none;">
                <a>
                    <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">开始</span>
                    </span>
                    <span class="btn-right"></span>
                </a>
            </div>
            <div id="set-channel-stop-btn" class="dialog-btn" style="display:none;">
                <a>
                    <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">停止</span>
                    </span>
                    <span class="btn-right"></span>
                </a>
            </div>
            <div id="set-channel-disconnected-btn" class="dialog-btn" style="display:none;">
                <a>
                    <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">离线</span>
                    </span>
                    <span class="btn-right"></span>
                </a>
            </div>
        </div>
        <div style="margin-top: 10px; text-align: right;" id="task-disconnected-message" style="display:none;">
            任务运行的agent处于离线状态，请检查系统的网络状况。任务状态将在agent恢复后恢复
        </div>
        <c:choose>
        <c:when test="${debug}">
        <div style="margin-top: 10px;">
            </c:when>
            <c:otherwise>
            <div style="display:none;">
                </c:otherwise>
                </c:choose>
                <c:if test="${!supportmosaic}">
                    <span> 屏幕id: {{id}}</span>
                </c:if>
            </div>
            <c:choose>
            <c:when test="${debug}">
            <div style="margin-top: 10px;">
                </c:when>
                <c:otherwise>
                <div style="display:none;">
                    </c:otherwise>
                    </c:choose>
                    <span>任务进程id：</span><span id="screen-task-pid"></span>
                </div>
                <c:choose>
                <c:when test="${debug}">
                <div style="margin-top: 10px;" id="output-stream-addr">
                    </c:when>
                    <c:otherwise>
                    <div style="display:none;" id="output-stream-addr">
                        </c:otherwise>
                        </c:choose>
                        <span>输出地址：</span>
                        <span id="output-stream-address">{{outputAddr}}  {{outputAddr2}}</span>
                    </div>
                    <div class="clearfix" style="margin-top: 10px;">
                        <div id="wall-position-output-selector-container">
                            <span>输出方式：</span>
                            <select id="wall-position-output-selector">
                                <option value="1" {{#if opsId}}selected{{/if}}>OPS</option>
                                <option value="2" {{#unless opsId}}selected{{/unless}}>固定地址</option>
                            </select>
                        </div>
                        <div id="binding-container" {{#unless opsId}}style="display:none;" {{/unless}}>
                        <span>当前绑定设备：</span>
                        <span id="binding-name">{{opsIp}}</span>
                        <div class="dialog-btn" id="device-unbinding" {{#unless opsId}}style="display:none;" {{/unless}}>
                        <a>
                            <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">解除绑定</span>
                        </span>
                            <span class="btn-right"></span>
                        </a>
                    </div>
                    <div class="dialog-btn" id="device-binding" {{#if opsId}}style="display:none;" {{/if}}>
                    <a>
                        <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">绑定</span>
                        </span>
                        <span class="btn-right"></span>
                    </a>
                </div>
            </div>
            <div id="wall-position-output-container" {{#if opsId}}style="display:none;" {{/if}}>
            <input type="text" name="wall-position-output" id="wall-position-output" value="{{output}}"></input>
            <button type="button" id="save-wall-position-output" class="btn">保存</button>
        </div>
    </div>
    <div id="screen-message-container" style="margin-top: 10px;">
        <input type="text" name="screen-message-text" id="screen-message-text" value="{{message}}"></input>
        <button type="button" id="apply-screen-message" class="btn">应用</button>
        <button type="button" id="edit-screen-message-style" class="btn">编辑样式</button>
    </div>


    <div style="margin-top: 20px;display: none;color: red;" id="error-container"></div>
    <div id="set-channel-tab" class="tabs">
        <ul class="tab-links clearfix">
            <li class="active">
                <form id="schema-name-0" name="schema-name-0">
                    <a href="#schema-0">预设1</a>

                    <input type="text" name="schema-name-0-input" value="预设1" style="width:115px;" required
                           maxlength=8></input>
                </form>
            </li>
            <li>
                <form id="schema-name-1" name="schema-name-1">
                    <a href="#schema-1">预设2</a>

                    <input type="text" value="预设2" name="schema-name-1-input" style="width:115px;" required
                           maxlength=8></input>
                </form>
            </li>
            <li>
                <form id="schema-name-2" name="schema-name-2">
                    <a href="#schema-2">预设3</a>

                    <input type="text" value="预设3" name="schema-name-2-input" style="width:115px;" required
                           maxlength=8></input>
                </form>
            </li>
        </ul>
    </div>
    <div id="current-schema">

    </div>
    </div>
</script>
<script type="text/x-handlebars-template" id="screenSchemaTabItemTemplate">
    <form>
        <a>{{name}}</a>
        <input type="text" value="{{name}}" style="width:115px;" required maxlength=8></input>
    </form>
</script>
<script type="text/x-handlebars-template" id='screenActiveSchemaTemplate'>
    <div style="margin-top:20px;">
        <span>轮播组：</span>
        <select id="schema-group-count" name="schema-group-count">
            {{#select groupCount}}
            <option value="1">1</option>
            <option value="2">2</option>
            {{/select}}
        </select>
        {{#ifCond groupCount '>' '1'}}
        <span>轮播时间：</span>
        <select id="schema-switch-time" name="schema-switch-time">
            {{#select switchTime}}
            <option value="5">5秒</option>
            <option value="10">10秒</option>
            <option value="15">15秒</option>
            <option value="20">20秒</option>
            <option value="30">30秒</option>
            <option value="60">60秒</option>
            <option value="120">120秒</option>
            {{/select}}
        </select>
        {{/ifCond}}

    </div>
    <div id="schema-group-index-container">
    </div>
    <div class="gridster" style="width:480px;margin:10px auto 0px;">
        <div id="active-schema" class="channel-grid" data-rowCount="{{row}}" data-colCount="{{column}}">
            {{#each positions}}
            <div class="position-item" data-row="{{row}}" data-col="{{column}}" data-sizex="{{x}}" data-sizey="{{y}}"
                 data-channel="{{channel_id}}">
                <div class="innerborder">
                    <span>{{channel_name}}</span>
                </div>
            </div>
            {{/each}}
        </div>
    </div>
</script>
<script type="text/x-handlebars-template" id="layoutItemTemplate">
    <input type="radio" name="layout-item-select" value="{{id}}"></input>
    <div class="gridster layout-position-grid-container" style="width:120px;margin:10px auto 0px;">
        <div class="layout-position-grid">
            {{#each positions}}
            <div class="layout-position-item" data-row="{{row}}" data-col="{{column}}" data-sizex="{{x}}"
                 data-sizey="{{y}}">
            </div>
            {{/each}}
        </div>
    </div>
</script>

<script type="text/x-handlebars-template" id="layoutListTemplate">
    <div id="layoutList-title" class="dialog-caption">选择画面模板</div>
    <div id="layoutlist-container">
    </div>
    <div id="layoutlist-submit" class="dialog-btns">
        <div id="layoutlist-cancel-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">取消</span>
                        </span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="layoutlist-select-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">确定</span>
                        </span>
                <span class="btn-right"></span>
            </a>
        </div>
    </div>
</script>
</html>

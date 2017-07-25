<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/common/normalize.css"/>
    <utils:css path="/css/deviceList.css"/>
    <utils:js path="/js/plugins/chartnew/ChartNew.js"/>
    <script type="text/javascript">
        _.extend(window.sv, {
            eventDispatcher: _.extend({}, Backbone.Events),
            model: {},
            view: {},
            app: {},
            utils: {}
        });
        sv.utils.template = function (templateId, model) {
            model = model || {};
            return Handlebars.compile($("#" + templateId).html())(model);
        };
    </script>
    <style type="text/css"></style>

    <utils:js path="/js/deviceList.js"/>
    <script type="text/javascript">
        $(function () {
            Device.init();
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>

    <div id="packageadd" class="modal" style="display:none;">
        <div class="dialog-caption" style="text-align: left;font-weight: bold;">添加合成画面服务器
        </div>
        <div class="Package">
            <div id="dialog-install">

            </div>
        </div>
    </div>

    <div class="container">

        <div class="content-wrapper">
            <div class="add-device">

                <div class="action-btn">
                    <a id="add-device-btn" href="#add-device-dialog">
                        <span class="btn-left"></span>
						<span class="btn-middle">
							<span class="btn-text">搜索设备</span>
							<span class="btn-icon"></span>
						</span>
                        <span class="btn-right"></span>
                    </a>
                </div>

                <div class="action-btn">
                    <a id="install-device-btn">
                        <span class="btn-left"></span>
						<span class="btn-middle">
							<span class="btn-text">安装设备</span>
							<span class="btn-icon"></span>
						</span>
                        <span class="btn-right"></span>
                    </a>
                </div>
            </div>
            <div class="device-list">
                <div class="action-bar">
                    <div class="disable">
                        <div class="action-bar-item edit" id="edit">修改</div>
                    </div>
                    <div class="disable">
                        <div class="action-bar-item config" id="config">配置</div>
                    </div>
                    <div class="disable">
                        <div class="action-bar-item delete" id="delete">删除</div>
                    </div>
                    <div class="disable">
                        <div class="action-bar-item show-tasks" id="show-tasks">任务统计</div>
                    </div>
                    <div class="disable">
                        <div class="action-bar-item show-install" id="show-install">重新安装</div>
                    </div>
                </div>
                <div class="tab-content">
                    <table id="device-list-table" class="device-list-table">
                        <thead>
                        <tr>
                            <th width="50px" class="right-align">
                                <input type="checkbox" id="select-all" class="select-all">
                            </th>
                            <th>设备名称</th>
                            <th>负载情况</th>
                            <th>设备状态</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td class="right-align">
                                <input type="checkbox" class="select-one" name="ids" value="-1">
                            </td>
                            <td>集群管理服务器</td>
                            <td></td>
                            <td>在线</td>
                        </tr>
                        <c:forEach var="device" items="${pageObject.getContent()}">
                            <tr>
                                <td class="right-align">
                                    <input type="checkbox" class="select-one" name="ids" value="${device.id}">
                                </td>
                                <td>${device.name}</td>
                                <td>
									<span class="load-content">
										<span class="load-cpu">CPU
											<div class="load-cpu-status">
                                            </div>
										</span>
										<span class="load-network">网络
											<div class="load-network-status">
                                            </div>
										</span>
										<c:choose>
                                            <c:when test="${device.alive}">
                                                <div class="detail">
                                                    <div class="detail-status"></div>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="detail" style="display:none;">
                                                    <div class="detail-status"></div>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
										
									</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${device.alive}">在线</c:when>
                                        <c:otherwise>离线</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <tr id="details-container">
                            <td colspan="4" id="details-canvas-container">
                            </td>
                        </tr>
                        </tbody>
                    </table>
                    <!--
                    <div class="list-page">
                        <div class="page">
                                <span>
                                    <span class="page-btns previous-btns-disabled"></span>
                                </span>
                            <span class="page-btns num-btns-active">1</span>
                            <span class="page-btns num-btns">2</span>
                                <span>
                                    <span class="page-btns next-btns"></span>
                                </span>
                        </div>
                    </div>
                    -->
                </div>
            </div>
        </div>
        <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script type="text/x-handlebars-template" id="opLoadingTemplate">
    <div class="dialog-caption">&nbsp;</div>
    <div class="dialog-content">
        <div style="height: 60px;">
            <div style="margin-left: 30%;padding-top: 20px;">
                <img src="<c:url value="/images/spinner.gif"/> ">
                <span style="position: absolute;margin-left: 5px;">{{message}}</span>
            </div>
        </div>
    </div>
</script>
<script type="text/x-handlebars-template" id="addDeviceTemplate">
    <div class="dialog-caption">添加设备</div>
    <div class="dialog-content">
        {{#each deviceList}}
        <form id="device-from-{{id}}">
            <div class="device-item">
                <input class="item-select" type="checkbox" name="add-device" id="add-device-{{id}}" value="{{id}}"/>

                <div class="item-detail">
                    <div class="line" style="margin-bottom:18px;">
                        <span style="width:70px;">设备名称：</span>
                        <input style="width:300px;" type="text" name="name" id="name-{{id}}"/>
                    </div>
                    <div class="line" style="margin-bottom:18px;">
                        <span style="width:70px;">服务器：</span>
                        <select style="width:160px;" disabled>
                            <option>{{eth}}</option>
                        </select>
                        <span>IP地址：{{ip}}</span>
                        <span>子网掩码：{{netmask}}</span>
                        <span>网关：{{gateway}}</span>
                    </div>
                    <div class="line" style="margin-bottom:18px;">
                        <span style="width:70px; vertical-align:top;">功能模块：</span>
                        <ul class="device-function-list">
                            <li>
                                <input type="checkbox" name="functions" id="sd" value="sd" {{#ifIn activeFunctionsAsEnum
                                "SIGNAL_DETECT"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "SIGNAL_DETECT"}}disabled{{/ifNotIn}}/>
                                <span>信源检测</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="cd" value="cd" {{#ifIn activeFunctionsAsEnum
                                "CONTENT_DETECT"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "CONTENT_DETECT"}}disabled{{/ifNotIn}}/>
                                <span>内容检测</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="ip" value="ip" {{#ifIn activeFunctionsAsEnum
                                "IP_STREAM_COMPOSE"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "IP_STREAM_COMPOSE"}}disabled{{/ifNotIn}}/>
                                <span>IP多画面合成</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="sdi" value="sdi" {{#ifIn
                                       activeFunctionsAsEnum
                                "SDI_STREAM_COMPOSE"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "SDI_STREAM_COMPOSE"}}disabled{{/ifNotIn}}/>
                                <span>SDI多画面合成</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="stream" value="stream" {{#ifIn
                                       activeFunctionsAsEnum
                                "STREAM_SERVER"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "STREAM_SERVER"}}disabled{{/ifNotIn}}/>
                                <span>流媒体服务</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="encoder" value="encoder" {{#ifIn
                                       activeFunctionsAsEnum
                                "ENCODER"}}checked{{/ifIn}} {{#ifNotIn functionEnums "ENCODER"}}disabled{{/ifNotIn}}/>
                                <span>编码器</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="comm" value="comm" {{#ifIn
                                       activeFunctionsAsEnum
                                "COMMANDER"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "COMMANDER"}}disabled{{/ifNotIn}}/>
                                <span>集群管理</span>
                            </li>
                        </ul>
                    </div>
                    <div class="line">
                        <span style="width:70px;">备注：</span>
                        <input style="width:500px;" type="text" name="remark" id="remark"/>
                    </div>
                </div>
            </div>
        </form>
        {{else}}
        <div style="text-align:center;margin:20px 0px;font-size:14px;">没有可用的设备</div>
        {{/each}}

    </div>
    <div class="dialog-btns">
        <div id="add-device-cancel-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
					<span class="btn-middle">
						<span class="btn-text">取消</span>
					</span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="add-device-ok-btn" class="dialog-btn">
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
<script type="text/x-handlebars-template" id="updateDeviceTemplate">
    <div class="dialog-caption">修改设备</div>
    <div class="dialog-content">
        <form id="device-update-form">
            <div class="device-item">
                <div class="item-detail">
                    <div class="line" style="margin-bottom:18px;">
                        <span style="width:70px;">设备名称：</span>
                        <input style="width:300px;" type="text" name="name" id="name" value="{{name}}"/>
                    </div>
                    <div class="line" style="margin-bottom:18px;">
                        <span style="width:70px;">服务器：</span>
                        <select style="width:160px;" disabled>
                            <option>{{eth}}</option>
                        </select>
                        <span>IP地址：{{ip}}</span>
                        <span>子网掩码：{{netmask}}</span>
                        <span>网关：{{gateway}}</span>
                    </div>
                    <div class="line" style="margin-bottom:18px;">
                        <span style="width:70px; vertical-align:top;">功能模块：</span>
                        <ul class="device-function-list">
                            <li>
                                <input type="checkbox" name="functions" id="sd" value="sd" {{#ifIn activeFunctionsAsEnum
                                "SIGNAL_DETECT"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "SIGNAL_DETECT"}}disabled{{/ifNotIn}}/>
                                <span>信号检测</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="cd" value="cd" {{#ifIn activeFunctionsAsEnum
                                "CONTENT_DETECT"}}checked{{/ifIn}}
                                {{#ifNotIn functionEnums "CONTENT_DETECT"}}disabled{{/ifNotIn}}/>
                                <span>内容检测</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="ip" value="ip" {{#ifIn activeFunctionsAsEnum
                                "IP_STREAM_COMPOSE"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "IP_STREAM_COMPOSE"}}disabled{{/ifNotIn}}/>
                                <span>IP多画面合成</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="sdi" value="sdi" {{#ifIn
                                       activeFunctionsAsEnum
                                "SDI_STREAM_COMPOSE"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "SDI_STREAM_COMPOSE"}}disabled{{/ifNotIn}}/>
                                <span>SDI多画面合成</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="stream" value="stream" {{#ifIn
                                       activeFunctionsAsEnum
                                "STREAM_SERVER"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "STREAM_SERVER"}}disabled{{/ifNotIn}}/>
                                <span>流媒体服务</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="encoder" value="encoder" {{#ifIn
                                       activeFunctionsAsEnum
                                "ENCODER"}}checked{{/ifIn}} {{#ifNotIn functionEnums "ENCODER"}}disabled{{/ifNotIn}}/>
                                <span>编码器</span>
                            </li>
                            <li>
                                <input type="checkbox" name="functions" id="comm" value="comm" {{#ifIn
                                       activeFunctionsAsEnum
                                "COMMANDER"}}checked{{/ifIn}} {{#ifNotIn functionEnums
                                "COMMANDER"}}disabled{{/ifNotIn}}/>
                                <span>集群管理</span>
                            </li>
                        </ul>
                    </div>
                    <div class="line">
                        <span style="width:70px;">备注：</span>
                        <input style="width:500px;" type="text" name="remark" id="remark" value="{{remark}}"/>
                    </div>
                </div>
            </div>
        </form>
    </div>
    <div class="dialog-btns">
        <div id="add-device-cancel-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
					<span class="btn-middle">
						<span class="btn-text">取消</span>
					</span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="edit-device-ok-btn" class="dialog-btn">
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
<script type="text/x-handlebars-template" id="loadingTemplate">
    <div class="dialog-caption">搜索设备</div>
    <div class="dialog-content">
        <div class="device-item" style="height: auto;">
            <div style="margin-left: 35%;">
                <img src="<c:url value="/images/spinner.gif"/>">
                <span style="position: absolute;top: 55px;margin-left: 5px;">搜索设备中，请稍等...</span>
            </div>
        </div>
    </div>
</script>
<script type="text/x-handlebars-template" id="loadingMsg">
    <div class="dialog-caption">提示</div>
    <div class="dialog-content">
        <div style="height: 60px;">
            <div style="margin-left: 30%;padding-top: 20px;">
                <img src="/images/spinner.gif ">
                <span style="position: absolute;margin-left: 5px;">操作处理中，请稍等...</span>
            </div>
        </div>
    </div>
</script>

<script type="text/x-handlebars-template" id="deviceDetailsTemplate">
    <td colspan="4">
        <div class="clearfix device-details">
            <div id="memroy-details" class="detail-item">
                <div class="detail-title">
                    <div class="icon"></div>
                    <span>内存</span>
                </div>
                <div class="detail-content">
                    {{#if MEMORYTOTAL}}
                    {{#with MEMORYTOTAL}}
                    <span>已使用{{usage}}G</span>
                    <span>空闲{{free}}G</span>
                    <span>共{{total}}G</span>
                    {{/with}}
                    {{else}}
                    <span>未知</span>
                    {{/if}}
                </div>
            </div>
            <div id="cpu-details" class="detail-item">
                <div class="detail-title">
                    <div class="icon"></div>
                    <span>CPU</span>
                </div>
                <div class="detail-content">
                    {{#each CPU}}
                    <div>
                        CPU{{name}}: {{usage}}%
                    </div>
                    {{/each}}
                </div>
            </div>
            <div id="gpu-details" class="detail-item">
                <div class="detail-title">
                    <div class="icon"></div>
                    <span>GPU</span>
                </div>
                <div class="detail-content">
                    {{#each GPU}}
                    <div>
                        {{name}}: {{usage}}%
                    </div>
                    {{/each}}
                </div>
            </div>
            <div id="network-details" class="detail-item">
                <div class="detail-title">
                    <div class="icon"></div>
                    <span>网络</span>
                </div>
                <div class="detail-content">
                    {{#each NETWORK}}
                    <div>
                        {{#if on}}
                        {{name}}: {{usage}}%
                        {{else}}
                        {{name}}: 未启用
                        {{/if}}
                    </div>
                    {{/each}}
                </div>
            </div>
            <div id="sdi-details" class="detail-item"></div>
        </div>
    </td>
</script>

<script type="text/template" id="device-config-template">
    <div id="nav">
        <div id="nav-parent" class="nav-parent">
            <ul class="tab-links tab-item">
                <li data-view="host"><a href="#">主机设置</a></li>
                <li data-view="network"><a href="#">网络设置</a></li>
                <li data-view="storage"><a href="#">存储设置</a></li>
                <li data-view="config"><a href="#">参数设置</a></li>
            </ul>
        </div>
        <div id="nav-child"></div>
    </div>
    <div id="content" class="content"></div>
</script>

<script type="text/template" id="nav-child-host-template">
    <div id="nav-child-host" class="nav-child">
        <ul class="tab-links tab-item">
            <li data-view="host-operate"><a href="#">主机操作</a></li>
            <li data-view="host-init"><a href="#">系统初始化</a></li>
        </ul>
    </div>
</script>
<script type="text/template" id="nav-child-network-template">
    <div id="nav-child-network" class="nav-child">
        <ul class="tab-links tab-item">
            <li data-view="network-eth"><a href="#">网卡</a></li>
            <li data-view="network-dns"><a href="#">DNS</a></li>
            <li data-view="network-route"><a href="#">路由表</a></li>
        </ul>
    </div>
</script>
<script type="text/template" id="host-operate-template">
    <div id="content-host-operate">
        <div>
            <button type="button" id="restart" class="btn">重启设备</button>
        </div>
        <div>
            <button type="button" id="shutdown" class="btn">关机</button>
        </div>
    </div>
</script>
<script type="text/template" id="nav-child-config-template">
    <div id="nav-child-config" class="nav-child">
        <ul class="tab-links tab-item">
            <li data-view="config-rtsp"><a href="#">RTMP</a></li>
            <li data-view="config-gpu"><a href="#">GPU</a></li>
            <li data-view="config-record"><a href="#">录制</a></li>
        </ul>
    </div>
</script>

<script type="text/template" id="host-init-template">
    <div id="content-host-init">
        <form>
            <table class="host-init-table">
                <tbody>
                <tr>
                    <td class="text-right">集群地址：</td>
                    <td><input type="text" name="clusterIp" id="clusterIp" value="{{settings.clusterIp}}"
                               tip-gravity="w"></td>
                </tr>
                <tr>
                    <td class="text-right">集群端口：</td>
                    <td><input type="text" name="clusterPort" id="clusterPort" value="{{settings.clusterPort}}"
                               tip-gravity="w"></td>
                </tr>
                <tr>
                    <td class="text-right">集群网卡：</td>
                    <td>
                        <select name="bindAddr" id="bindAddr" style="width: 151px;" tip-gravity="w">
                            <option value="">请选择</option>
                            {{#each eths}}
                            <option value="{{@key}}">{{this}}</option>
                            {{/each}}
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="text-right">集群TTL：</td>
                    <td>
                        <input type="text" name="timeToLive" id="timeToLive" value="{{settings.timeToLive}}"
                               tip-gravity="w">
                    </td>
                </tr>
                <tr>
                    <td class="text-right">心跳间隔时间：</td>
                    <td>
                        <input type="text" name="heartbeatInterval" id="heartbeatInterval"
                               value="{{settings.heartbeatInterval}}" tip-gravity="w">
                    </td>
                </tr>
                <tr>
                    <td class="text-right">心跳超时时间：</td>
                    <td>
                        <input type="text" name="heartbeatTimeout" id="heartbeatTimeout"
                               value="{{settings.heartbeatTimeout}}" tip-gravity="w">
                    </td>
                </tr>
                </tbody>
            </table>
            <div style="padding-top : 5px;">
                <button type="button" id="save" class="btn">保存</button>
            </div>
        </form>
    </div>
</script>
<script type="text/template" id="eth-template">
    <div id="eth-left"></div>
    <div id="eth-right"></div>
</script>
<script type="text/template" id="eth-item-template">
    <div class="float-left left{{#unless m.running}} disable{{/unless}}"></div>
    <div class="float-left right">
        <div>{{m.name}}</div>
        <div>{{rate}}%({{m.speed}})</div>
    </div>
</script>
<script type="text/template" id="eth-content-template">
    <form>
        <table class="host-init-table">
            <tbody>
            <tr>
                <td class="text-right">名称：</td>
                <td id="eth-name">
                    {{name}}
                    <input type="hidden" name="name" id="name" value="{{name}}"/>
                </td>
            </tr>
            <tr>
                <td class="text-right">IP设定方式：</td>
                <td>
                    <select id="isDHCP" name="isDHCP" style="width: 130px;">
                        <option value='true'>IP自动获取</option>
                        <option value='false'>手动设置</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="text-right">IP地址：</td>
                <td>
                    <input type="text" name="ip" id="ip" value="{{ip}}" tip-gravity="w">

                </td>
            </tr>
            <tr>
                <td class="text-right">子网掩码：</td>
                <td>
                    <input type="text" name="mask" value="{{mask}}" id="mask" tip-gravity="w">
                </td>
            </tr>
            <tr>
                <td class="text-right">网关：</td>
                <td>
                    <input type="text" name="gateway" id="gateway" value="{{gateway}}" tip-gravity="w">
                </td>
            </tr>
            </tbody>
        </table>
        <div style="padding-top : 5px;">
            <button type="button" id="save" class="btn">保存</button>
        </div>
    </form>
</script>
<script type="text/template" id="eth-dns-layout-template">
    <div id="dns-items"></div>
    <div id="dns-form" class="last"></div>
</script>
<script type="text/template" id="eth-dns-template">
    <span>DNS Server:</span>
    <span class="ip">{{ip}}</span>
    <span class="op">
			<a href="javascript:void(0)" class="btn-del"></a>
	</span>
</script>
<script type="text/template" id="eth-dns-form-template">
    <span>DNS Server:</span>
    <span class="ip"><form><input type="text" id="dns" name="dns"></form></span>
    <span class="op">
			<button type="button" class="addBtn">添加</button>
	</span>
</script>
<script type="text/template" id="eth-route-container-template">
    <div id="route-table-container"></div>
    <div id="route-add-form-container"></div>
</script>
<script type="text/template" id="eth-route-table-template">
    <div class="title">路由表</div>
    <div>
        <table id="route-table" class="route-table">
            <thead>
            <tr>
                <th>目标</th>
                <th>子网掩码</th>
                <th>网关</th>
                <th>Metric</th>
                <th>Iface</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</script>
<script type="text/template" id="eth-route-add-template">
    <form id="route-form" novalidate="novalidate">
        <label>目标：<input type="text" name="dest" id="route-des" value="" tip-gravity="n" required></label>
        <label>子网掩码：<input type="text" name="mask" id="route-mask" value="" tip-gravity="n" required></label>
        <label>网关：<input type="text" name="gateway" id="route-gateway" value="" tip-gravity="n"></label>
        <label>网口：
            <select name="iface" id="route-iface">
                {{#each .}}
                <option value="{{id}}">{{name}}</option>
                {{/each}}
            </select>
        </label>
        <span>
            <button type="button" id="addBtn">添加</button>
        </span>
    </form>
</script>
<script type="text/template" id="eth-route-item-template">
    <td>{{dest}}</td>
    <td>{{mask}}</td>
    <td>{{gateway}}</td>
    <td>{{metric}}</td>
    <td>{{iface}}</td>
    <td><a href="javascript:void(0)" class="btn-del"></a></td>
</script>

<script type="text/template" id="storage-container-template">
    <div id="storage-table-container"></div>
    <div id="storage-form-container"></div>
</script>
<script type="text/template" id="storage-table-template">
    <table id="storage-table" class="storage-table">
        <thead>
        <tr>
            <th>名称</th>
            <th>远程路径</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
</script>
<script type="text/template" id="storage-item-template">
    <td>{{name}}</td>
    <td>{{path}}</td>
    <td>{{#if mounted}}挂载{{else}}卸载{{/if}}</td>
    <td>
        <div id="ops">
            {{#if mounted}}<a id="umountBtn">卸载</a>{{else}}<a id="mountBtn">挂载</a>{{/if}}
            <a id="editBtn">编辑</a>
            <a id="delBtn">删除</a>
        </div>
    </td>
</script>
<script type="text/template" id="storage-form-template">
    <form id="storage-form">
        <table>
            <tr>
                <td class="text-right">类型：</td>
                <td>
                    <select name="type" id="type">
                        <option value="cifs">cifs</option>
                        <option value="nfs">nfs</option>
                    </select>
                </td>
                <td class="text-right">名称：</td>
                <td>
                    <input type="text" name="name" id="name" tip-gravity="w" required>
                </td>
            </tr>
            <tr>
                <td class="text-right">远程路径：</td>
                <td colspan="3">
                    <input type="text" name="path" id="path" tip-gravity="w" required>
                </td>
            </tr>
            <tr>
                <td class="text-right">用户名：</td>
                <td>
                    <input type="text" name="user" id="user" tip-gravity="w" required>
                </td>
                <td class="text-right">密码：</td>
                <td>
                    <input type="password" name="pwd" id="pwd" tip-gravity="w" required>
                </td>
            </tr>
            <tr>
                <td colspan="4" style="text-align: center;">
                    <button type="button" id="saveBtn" class="btn">保存</button>
                    <button type="button" id="resetBtn" class="btn">重置</button>
                </td>
            </tr>
        </table>
    </form>
</script>
<script type="text/template" id="config-alarm-template">
    <form id="alarmConfigForm">
        <div>
            <label>Android API KEY: <input type="text" name="androidapikey" id="androidapikey" value="{{androidapikey}}"
                                           required/></label>
        </div>
        <div>
            <label>Android SECRET KEY: <input type="text" name="androidsecretkey" id="androidsecretkey"
                                              value="{{androidsecretkey}}" required/></label>
        </div>
        <div><span class="info-tip" style="left: -54px;">Android应用配置中 API KEY 和SECRET KEY 对应的字符串</span></div>
        <div>
            <label>IOS API KEY: <input type="text" name="iosapikey" id="iosapikey" value="{{iosapikey}}"
                                       required/></label>
        </div>
        <div>
            <label>IOS SECRET KEY: <input type="text" name="iossecretsey" id="iossecretsey" value="{{iossecretsey}}"
                                          required/></label>
        </div>
        <div><span class="info-tip" style="left: -54px;">IOS应用配置中 API KEY 和SECRET KEY 对应的字符串</span></div>
        <div>
            <button type="button" id="saveBtn" class="btn">保存</button>
        </div>
    </form>
</script>
<script type="text/template" id="config-rtsp-template">
    <form id="rtspConfigForm">
        <div>
            <label>服务器ip：<input type="text" name="ip" id="ip" value="{{ip}}" required/></label>
            <span class="info-tip" style="left: -54px;">rtmp内网ip地址,将作为转码器的输出ip地址</span>
        </div>
        <div>
            <label>发布目录：<input type="text" name="publishFolderPath" id="publishFolderPath" value="{{publishFolderPath}}"
                               required/></label>
            <span class="info-tip" style="left: -27px;">目录必须存在且集群及转码器所在机器有读写操作权限</span>
        </div>
        <div>
            <label style="position: relative;left: -160px;">发布地址：
                <button type="button" id="addPublishUrl" class="btn">新增</button>
            </label>
            <span class="info-tip" style="left: -54px;">rtmp流发布地址,外部系统将使用该地址访问</span>
            <div id="container-publish-url"></div>
        </div>
        <div>
            <button type="button" id="saveBtn" class="btn">保存</button>
        </div>
    </form>
</script>
<script type="text/template" id="config-rtsp-publish-url-template">
    <span id="url-index" style="width:16px;">{{idx}}</span>.&nbsp;&nbsp;<input type="text" name="url-{{idx}}"
                                                                               value="{{url}}" required/>
    {{#ifCond idx '>' 1}}
    <span id="del-url">&times;</span>
    {{/ifCond}}
</script>
<script type="text/template" id="config-gpu-template">
    <div>
        <span>
            允许跨核：
        </span>
        <span>
            <label>是：</label>
            <input type="radio" name="enableSpanGpu" id="e1" value="1"/>
        </span>
        <span style="margin-left: 10px;"></span>
        <span>
            <label>否：</label>
            <input type="radio" name="enableSpanGpu" id="e2" value="0"/>
        </span>
    </div>
    <div>
        <button type="button" id="saveBtn" class="btn">保存</button>
    </div>
</script>
<script type="text/template" id="config-record-template">
    <form id="recordConfigForm">
        <div>
            <div>
                <label style="position: relative; left: 35px;">录制服务器地址：<input type="text" name="domain" id="domain"
                                                                              value="{{domain}}" required/></label>
                <!-- <span class="info-tip" style="left: -54px;">录制服务器地址</span> -->
            </div>
            <div>
                <label style="position: relative; left: 40px;">录制目录路径：<input type="text" name="supervisorStoragePath"
                                                                             id="supervisorStoragePath"
                                                                             value="{{supervisorStoragePath}}"
                                                                             required/></label>
                <!-- <span class="info-tip" style="left: -27px;">目录必须存在且集群有读写操作权限</span> -->
            </div>
            <div>
                <label>录制服务器访问录制目录路径：<input type="text" name="recorderStoragePath" id="recorderStoragePath"
                                            value="{{recorderStoragePath}}" required/></label>
                <!-- <span class="info-tip" style="left: -54px;"></span> -->
            </div>
            <div>
                <label style="position: relative; left: -105px;">录制文件保留时间：<input type="text" name="keepTime"
                                                                                 id="keepTime" value="{{keepTime}}"
                                                                                 required/> 小时</label>
            </div>
            <div>
                <label style="position: relative; left: -100px;">录制模板id：<input type="text" name="profileId"
                                                                               id="profileId" value="{{profileId}}"
                                                                               required/></label>
            </div>
            <div>
                <label style="position: relative; left: 18px;">内容检测文件保存路径：<input type="text"
                                                                                 name="contentDetectStoragePath"
                                                                                 id="contentDetectStoragePath"
                                                                                 value="{{contentDetectStoragePath}}"
                                                                                 required/></label>
            </div>
            <div>
                <label style="position: relative; left: -120px;">内容检测文件保留时间：<input type="text"
                                                                                   name="contentDetectKeepTime"
                                                                                   id="contentDetectKeepTime"
                                                                                   value="{{contentDetectKeepTime}}"
                                                                                   required/> 小时</label>
            </div>
            <div>
                <button type="button" id="saveBtn" class="btn">保存</button>
            </div>
        </div>
    </form>
</script>
<script type="text/template" id="show-tasks-template">
    <div class="dialog-caption">查看任务数量</div>
    <div class="dialog-content">
        <div style="padding: 20px 20px;text-align: center;">
            <div style="font-size: 20px;padding: 10px;">总数：{{count}}</div>
            <div>
                <table id="show-tasks-table" style="">
                    {{#each channelNames}}
                    <tr>
                        {{#each this}}
                        <td>{{.}}</td>
                        {{/each}}
                    </tr>
                    {{/each}}
                </table>
            </div>
        </div>
    </div>
</script>

<script type="text/x-handlebars-template" id="addPackageTemplate">
    <form id="add-package-form">
        <table width="60%" align="center" class="tblcol2">
            <tbody>


            <tr>
                <td class="collbl TableColTitleText">ssh地址:</td>
                <td class="colval"><input class="version" name="sshIp" id="sshIp" style="width: 240px;" type="text"
                                          value=""  placeholder="请输入ssh地址"/>
                </td>
            </tr>

            <tr>
                <td class="collbl TableColTitleText">ssh端口:</td>
                <td class="colval"><input class="version" name="sshPort" id="sshPort" style="width: 240px;" type="text"
                                          value="" placeholder="请输入ssh端口"/>
                </td>
            </tr>

            <tr>
                <td class="collbl TableColTitleText">ssh用户名:</td>
                <td class="colval"><input class="version" name="sshUserName" id="sshUserName" style="width: 240px;" type="text"
                                          value=""  placeholder="请输入ssh用户名"/>
                </td>
            </tr>

            <tr>
                <td class="collbl TableColTitleText">ssh密码:</td>
                <td class="colval"><input class="version" name="sshPassWord" id="sshPassWord" style="width: 240px;" type="password"
                                          value=""  placeholder="请输入ssh密码"/>
                </td>
            </tr>

            </tbody>
        </table>

        <div class="dialog-btns">
            <div id="add-package-cancel-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span
                    class="btn-middle"> <span class="btn-text">取消</span> </span> <span class="btn-right"></span> </a></div>
            <div id="add-package-ok-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span class="btn-middle"> <span
                    class="btn-text">确定</span> </span> <span class="btn-right"></span> </a></div>
        </div>
    </form>

</script>



<div id="loading-dialog" class="modal" style="display:none;width: 350px;"></div>
<div id="add-device-dialog" class="modal" style="display:none;width: 800px;"></div>
<div id="config-device-dialog" class="modal" style="display:none;width: 800px;">
    <div class="dialog-caption">设备配置</div>
    <div class="dialog-content"></div>
</div>
<div id="show-device-tasks-dialog" class="modal" style="display:none;width: 800px;">
</div>


</body>
</html>

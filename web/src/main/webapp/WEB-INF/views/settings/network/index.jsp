<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/network.css"/>
    <utils:js
            path="/js/plugins/knockout/knockout-3.2.0.js,/js/plugins/knockout/knockout.mapping-latest.js,/js/network.js,
            /js/plugins/jquery.extend.js"/>
    <script type="text/javascript">
        $(function () {
            NetWork.init();
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
        <div id="loading-dialog" class="modal" style="display:none;width: 350px;"></div>
        <div class="logType">
            <div id="logtab" class="tabbable" style=" background-color:#244d75; height:43px;">
                <ul>
                    <li id="logtab-tb1"><a data-toggle="tab" href="<c:url value="/storage/list"/> ">录制存储设置</a>
                    </li>
                    <li id="network-tab" class="active"><a data-toggle="tab" href="<c:url value="/network/index"/> ">网络设置</a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="content-wrapper clearfix content-right-column">
            <div id="network-tab" class="sidebar tabs">
                <ul class="tab-links">
                    <li><a href="#tab-interface-setting">网卡设置</a>
                    </li>
                    <li><a href="#tab-dns-setting">DNS设置</a>
                    </li>
                    <li><a href="#tab-route-table-setting">路由设置</a>
                    </li>
                </ul>
            </div>
            <div class="content tab-content">
                <div id="tab-interface-setting" class="clearfix">
                    <div>
                        <div id='interface-setting-options' data-bind="with: activeItem">
                            <form id="ethernet-form">
                                <div class='line'>
                                    <span class='label'>名称</span>
                                    <span id="name" data-bind="text: name"></span>
                                    <input type="hidden" name="name" data-bind="value: name"/>
                                </div>
                                <div class='line'>
                                    <span class='label'>IP地址设定方式</span>
                                    <select name='ip-setting' id='ip-setting' data-bind="value: isDHCP">
                                        <option value='1'>IP自动获取</option>
                                        <option value='0'>手动设置</option>
                                    </select>
                                </div>
                                <div class='line'>
                                    <span class='label'>IP</span>
                                    <input type='text' name='ip' id='ip'
                                           data-bind="value: ip, disable: isDHCP() == '1'" tip-gravity="w" required>
                                </div>
                                <div class='line'>
                                    <span class='label'>子网掩码</span>
                                    <input type='text' name='mask' id='mask'
                                           data-bind="value: mask, disable: isDHCP() == '1'" tip-gravity="w" required>
                                </div>
                                <div class='line'>
                                    <span class='label'>网关</span>
                                    <input type='text' name='gateway' id='gateway'
                                           data-bind="value: gateway, disable: isDHCP() == '1'" tip-gravity="w">
                                </div>
                                <div class='btn-line'>
                                    <div id="interface-setting-ok-btn" class="dialog-btn">
                                        <a>
                                            <span class="btn-left"></span>
                                        <span class="btn-middle">
                                            <span class="btn-text">应用</span>
                                        </span>
                                            <span class="btn-right"></span>
                                        </a>
                                    </div>
                                </div>
                            </form>
                        </div>
                        <div id='interface-list' data-bind="foreach: items">
                            <div class="interface-item"
                                 data-bind="css: {'cursor-pointer' : $index() != $parent.activeIndex(),
                                    'bg-color' : $index() == $parent.activeIndex(),
                                    disabled: 'RUNNING' != status
                                 },
                                 attr: {'data-index' : $index()}">
                                <span class="eth-icon"></span>
                                <span class="eth-state"></span>
                                <span class="eth-name" data-bind="text: $root.composeText($index())"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="tab-dns-setting">
                    <form id="dns-form">
                        <div class='line'>
                            <span class='label'>DNS Server</span>
                            <input type='text' name='dns1' id='dns1' data-bind="value: dns.dns1" tip-gravity="w" required>
                        </div>
                        <div class='line'>
                            <span class='label'>DNS Server</span>
                            <input type='text' name='dns2' id='dns2' data-bind="value: dns.dns2" tip-gravity="w" required>
                        </div>
                        <div class='btn-line'>
                            <div id="dns-setting-ok-btn" class="dialog-btn">
                                <a>
                                    <span class="btn-left"></span>
                                    <span class="btn-middle">
                                        <span class="btn-text">应用</span>
                                    </span>
                                    <span class="btn-right"></span>
                                </a>
                            </div>
                        </div>
                    </form>
                </div>

                <div id="tab-route-table-setting">
                    <div>
                        <div id='route-table-title'>
                            路由表
                        </div>
                        <table id="route-table">
                            <tr>
                                <td>目标</td>
                                <td>网关</td>
                                <td>子网掩码</td>
                                <td>Metric</td>
                                <td>Iface</td>
                                <td>操作</td>
                            </tr>
                            <!-- ko foreach: items -->
                            <tr class="route-item">
                                <td data-bind="text: dest"></td>
                                <td data-bind="text: gateway"></td>
                                <td data-bind="text: mask"></td>
                                <td data-bind="text: metric"></td>
                                <td data-bind="text: iface"></td>
                                <td><a class="route-del-link" data-bind="attr: {'data-index' : $index()}">删除</a></td>
                            </tr>
                            <!-- /ko -->
                        </table>
                    </div>
                    <form id="route-form">
                        <div id='add-route-table' data-bind="with: route">
                            <div class='arg' style="width:150px;margin-right:20px;">
                                <div class='label'>目标</div>
                                <input type='text' name='route-des' id='route-des' data-bind="value: dest" tip-gravity="n" required >
                            </div>
                            <div class='arg' style="width:150px;margin-right:20px;">
                                <div class='label'>子网掩码</div>
                                <input type='text' name='route-mask' id='route-mask' data-bind="value: mask" tip-gravity="n" required>
                            </div>
                            <div class='arg' style="width:150px;margin-right:20px;">
                                <div class='label'>网关</div>
                                <input type='text' name='route-gateway' id='route-gateway' data-bind="value: gateway" tip-gravity="n">
                            </div>
                            <div class='arg' style="width:80px;margin-right:20px;">
                                <div class='label'>网口</div>
                                <select name='route-iface' id='route-iface' data-bind="options: $root.eths, value: iface">
                                </select>
                            </div>
                            <div class='arg'>
                                <div id="route-setting-ok-btn" class="dialog-btn">
                                    <a>
                                        <span class="btn-left"></span>
                                    <span class="btn-middle">
                                        <span class="btn-text">添加</span>
                                    </span>
                                        <span class="btn-right"></span>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script type="text/x-handlebars-template" id="loadingTemplate">
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
</body>
</html>

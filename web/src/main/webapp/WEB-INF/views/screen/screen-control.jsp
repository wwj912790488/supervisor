<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="/WEB-INF/tags/profiles.tld" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="../common/common.jsp" %>
    <utils:css path="/css/screenControl.css"/>
    <utils:js path="/js/screenControl.js"/>
    <script type="text/javascript">
        $(function () {
            var screen = new sv.screen();
            screen.render();
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">

        <div class="secondary-nav">
            <ul>
                <li>
                    <a data-toggle="tab" href="/screen/index">屏幕墙列表</a>
                </li>
                <li class="active">
                    <a data-toggle="tab" href="/screen/control">任务画面控制</a>
                </li>

            </ul>
        </div>

        <div class="content-wrapper">
            <div class="ops-list">
                <div class="action-bar">
                    <div>
                        <div class="action-bar-item start" id="screen-btn-start">启动</div>
                        <div class="action-bar-item stop" id="screen-btn-stop">停止</div>
                    </div>
                </div>
                <div id="opslist" class="tab-content">
                    <table id="ops-list-table">
                        <thead>
                        <tr class="TableColTitleText" style="text-align: center;">
                            <th width="40px;"><%--<input type="checkbox" class="select-all"/>--%></th>
                            <th>屏幕墙名称</th>
                            <th>编号名称</th>
                            <th>屏幕id</th>
                            <th>运行状态</th>
                            <th>输出地址</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<div id="loading-dialog" class="modal" style="display:none;width: 350px;"></div>
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

<script type="text/x-handlebars-template" id="opsTemplate">
    {{#each opss}}
    {{#each list}}
    <tr class="TableItemText" style="text-align: center;">
        <td width="40px;"><input type="checkbox" class="select-one" name="ops-id" value="{{screenWebBean.id}}"/></td>
        <td>
            {{#ifCond screenWebBean.status "==" "RUNNING"}}
            <a href="/screen/findTaskXMLByTaskId?taskId={{task.id}}" target='_blank' style="font-size: 14px">
            {{/ifCond}}
                {{../name}}
            {{#ifCond screenWebBean.status "==" "RUNNING"}}
                 </a>
             {{/ifCond}}
        </td>

        <td>{{screenWebBean.wallName}}</td>
        <td>{{screenWebBean.id}}</td>
        <td>{{screenWebBean.status}}</td>
        <td>{{screenWebBean.outputAddr}} </p> {{screenWebBean.outputAddr2}}</td>
    </tr>
    {{/each}}
    {{/each}}
</script>
</body>
</html>

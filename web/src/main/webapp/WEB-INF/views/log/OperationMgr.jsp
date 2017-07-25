<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/file/userManage.css,/css/loglist.css"/>
    <utils:js path="/js/plugins/jquery.extend.js,/js/logList.js,/js/My97DatePicker/WdatePicker.js"/>
    <script type="text/javascript">
        $(function () {
            var slogView = new sv.SystemLogView();
            slogView.render();
            $.pagination({"form": "search-form"});
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
        <div class="logType">
            <div id="logtab" class="tabbable" style=" background-color:#244d75; height:43px;">
                <ul>
                    <c:if test="${supportmosaic==false}">
                    <li id="logtab-tb1"><a data-toggle="tab" href="<c:url value="/log/cd"/>">内容报警日志</a></li>
                    </c:if>
                    <li id="logtab-tb2"><a data-toggle="tab" href="<c:url value="/log/s"/>">服务器日志</a></li>
                    <li id="logtab-tb3" class="active"><a data-toggle="tab" href="<c:url value="/log/op"/>">操作日志</a>
                    </li>
                    <c:if test="${supportmosaic==false}">
                        <li id="logtab-tb1"><a data-toggle="tab" href="<c:url value="/log/chart"/>">内容报警报表</a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
        <div class="log-list content-wrapper">
                <div class="searchbox">
                    <form id="search-form" name="search-form" method="post"
                          action="<c:url value="/log/op"/>">
                        <input type="hidden" name="export">
                        <input type="hidden" name="delete">
                        <span style="margin-left:10px;">查找条件：
                        <input class="Wdate"
                               style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                               type="text" name="startTime" value="${q.startTimeAsString}"
                               onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:endTime.value})" >
                        -&nbsp;
                        <input class="Wdate"
                               style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                               type="text" name="endTime" value="${q.endTimeAsString}"
                               onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:startTime.value})">
                        </span>
                        <span style="margin-left:10px;">功能模块：
                        <select name="funcType" class="status_sel">
                            <option value="0">所有</option>
                            <option value="1" <c:if test="${q.funcType==1}">selected</c:if>>设备管理</option>
                            <option value="2" <c:if test="${q.funcType==2}">selected</c:if>>画面管理</option>
                            <option value="3" <c:if test="${q.funcType==3}">selected</c:if>>频道管理</option>
                            <option value="4" <c:if test="${q.funcType==4}">selected</c:if>>用户管理</option>
                            <option value="5" <c:if test="${q.funcType==5}">selected</c:if>>消息管理</option>
                            <option value="6" <c:if test="${q.funcType==6}">selected</c:if>>日志管理</option>

                            <option value="6" <c:if test="${q.funcType==7}">selected</c:if>>模板管理</option>
                            <option value="6" <c:if test="${q.funcType==8}">selected</c:if>>告警管理</option>
                            <option value="6" <c:if test="${q.funcType==9}">selected</c:if>>OPS管理</option>
                        </select>
                        </span>
                        <input class="keyword" type="text" id="operationInfo" name="operationInfo" placeholder="关键词搜索"
                               autocomplete="off" value="${q.operationInfo}"  style="margin-left:5px;"/>

                        <div id="search-btn" class="dialog-btn" style="position:relative; top:10px;margin-left:10px;">
                            <a>
                                <span class="btn-left"></span>
                                <span class="btn-middle">
                                    <span class="btn-text">搜索</span>
				</span>
                                <span class="btn-right"></span>
                            </a>
                        </div>
                        <div id="delete-filtered-btn" class="dialog-btn"
                             style="position:relative; top:10px;margin-left:5px;">
                            <a>
                                <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">删除</span>
                        </span>
                                <span class="btn-right"></span>
                            </a>
                        </div>
                    </form>
                </div>
                <div>
                    <div class="action-bar disable">
                        <div class="action-bar-item export" id="export-btn">导出</div>
                        <div class="action-bar-item delete" id="delete-btn">删除</div>
                    </div>
                    <table style="table-layout:fixed;word-wrap:break-word;word-break:break-all" class="log-list">
                        <thead>
                        <tr>
                            <th width="5%;"><input type="checkbox" class="select-all"/></th>
                            <th width="8%;">编号</th>
                            <th width="10%;">操作用户</th>
                            <th width="10%;">功能模块</th>
                            <th width="512px;">操作内容</th>
                            <th width="15%;">操作时间</th>
                            <th width="10%;">操作结果</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${pager.getContent()}" var="systemlog" varStatus="status">
                            <tr class="tasks">
                                <td>
                                    <input type="checkbox" class="select-one" id="select-one" name="systemLog-id" value="${systemlog.id}"/>
                                </td>
                                <td>${systemlog.id}</td>
                                <td>${systemlog.userName}</td>
                                <td>
                                    <span>
                                    <c:if test="${systemlog.funcType==1}">设备管理</c:if>
                                    <c:if test="${systemlog.funcType==2}">画面管理</c:if>
                                    <c:if test="${systemlog.funcType==3}">频道管理</c:if>
                                    <c:if test="${systemlog.funcType==4}">用户管理</c:if>
                                    <c:if test="${systemlog.funcType==5}">消息管理</c:if>
                                    <c:if test="${systemlog.funcType==6}">日志管理</c:if>
                                    <c:if test="${systemlog.funcType==7}">模板管理</c:if>
                                    <c:if test="${systemlog.funcType==8}">告警管理</c:if>
                                    <c:if test="${systemlog.funcType==9}">OPS管理</c:if>
                                    </span>
                                </td>
                                <td style="WORD-WRAP: break-word" width="200px;">${systemlog.operationInfo}</td>
                                <td>${systemlog.dateTime}</td>
                                <td>${systemlog.operationResult}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
        </div>
        <jsp:include page="/WEB-INF/views/common/pager.jsp"/>
        <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<script type="text/x-handlebars-template" id="systemLogTemplate">
    {{#each systemlogs}}
    <tr class="TableItemText" style="text-align: center;">
        <td width="40px;"><input type="checkbox" class="select-one" name="systemLog-id" value="{{id}}"></input></td>
        <td>{{id}}</td>
        <td>{{userName}}</td>
        <td><span>
		{{#ifCond funcType "==" "1"}}设备管理{{/ifCond}}
        {{#ifCond funcType "==" "2"}}画面管理{{/ifCond}}
		{{#ifCond funcType "==" "3"}}频道管理{{/ifCond}}
		{{#ifCond funcType "==" "4"}}用户管理{{/ifCond}}
		{{#ifCond funcType "==" "5"}}消息管理{{/ifCond}}
		{{#ifCond funcType "==" "6"}}日志管理{{/ifCond}}
		</span></td>
        <td>{{operationInfo}}</td>
        <td>{{dateTime}}</td>
        <td>{{operationResult}}</td>
    </tr>
    {{/each}}
</script>

</body>
</html>

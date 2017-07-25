<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/file/userManage.css,/css/loglist.css"/>
    <utils:js path="/js/plugins/jquery.extend.js,/js/logList.js,/js/My97DatePicker/WdatePicker.js"/>
    <script type="text/javascript">
        $(function () {
            var slView = new sv.ServiceLogView();
            slView.render();
            $.pagination({"form": "search-form"});
        });

    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>

    <div id="downServiceLog" class="modal" style="display:none;width: 500px">
        <div class="dialog-caption" style="text-align: left;font-weight: bold;">下载日志
        </div>
        <div class="Package">
            <div id="dialog-install">
            </div>
        </div>
    </div>

    <div class="container">
        <div class="logType">
            <div id="logtab" class="tabbable" style=" background-color:#244d75; height:43px;">
                <ul>
                    <c:if test="${supportmosaic==false}">
                        <li id="logtab-tb1"><a data-toggle="tab" href="<c:url value="/log/cd"/>">内容报警日志</a></li>
                    </c:if>
                    <li id="logtab-tb2" class="active"><a data-toggle="tab" href="<c:url value="/log/s"/>">服务器日志</a>
                    </li>
                    <li id="logtab-tb3"><a data-toggle="tab" href="<c:url value="/log/op"/>">操作日志</a></li>
                    <c:if test="${supportmosaic==false}">
                        <li id="logtab-tb1"><a data-toggle="tab" href="<c:url value="/log/chart"/>">内容报警报表</a>
                        </li>
                    </c:if>

                </ul>
            </div>
        </div>
        <div class="log-list content-wrapper">
            <div class="searchbox">
                <form id="search-form" name="search-form" method="post" action="<c:url value="/log/s"/>">
                    <input type="hidden" name="export">
                    <input type="hidden" name="delete">
                        <span style="margin-left:5px;">查找条件：
                            <input class="Wdate"
                                   style=" width:150px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="startTime" value="${q.startTimeAsString}"
                                   onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:endTime.value})">
                            &nbsp; 至 &nbsp;
                            <input class="Wdate"
                                   style="width:150px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="endTime" value="${q.endTimeAsString}"
                                   onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:startTime.value})">
                        </span>
                        <span style="margin-left: 5px;">
                            <label for="level">级别：</label>
                            <select name="level" id="level">
                                <option value="-1">所有</option>
                                <option value="10" <c:if test="${q.level == 10}">selected="selected"</c:if>>错误</option>
                                <option value="9" <c:if test="${q.level == 9}">selected="selected"</c:if>>警告</option>
                            </select>
                        </span>
                        <span style="margin-left: 5px;">
                            <label for="module">模块：</label>
                            <select name="module" id="module">
                                <option value="-1">所有</option>
                                <option value="0" <c:if test="${q.module == 0}">selected="selected"</c:if>>频道管理</option>
                                <option value="1" <c:if test="${q.module == 1}">selected="selected"</c:if>>画面管理</option>
                            </select>
                        </span>


                    <input class="keyword" type="text" id="description" name="description" placeholder="描述搜索"
                           autocomplete="off" value="${q.description}" style="width: 150px;margin-left:5px;"/>

                    <div id="search-btn" class="dialog-btn" style="position:relative; top:10px;margin-left:5px;">
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
                <table class="log-list" style="table-layout:fixed;word-wrap:break-word;word-break:break-all">
                    <thead>
                    <tr>
                        <th width="5%;"><input type="checkbox" class="select-all"/></th>
                        <th width="8%;">模块</th>
                        <th width="10%;">ip</th>
                        <th width="5%;">报警级别</th>
                        <th width="20%;">时间</th>
                        <th width="521px;">描述</th>
                        <th width="10%;">附件</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${pager.getContent()}" var="servicelog" varStatus="status">
                        <tr class="tasks">
                            <td>
                                <input type="checkbox" class="select-one" id="select-one"
                                       name="serviceLog-id" value="${servicelog.id}"/>
                            </td>
                            <td>
                                <c:if test="${servicelog.module == 0}">频道管理</c:if>
                                <c:if test="${servicelog.module == 1}">画面管理</c:if>
                                <c:if test="${servicelog.module == 2}">设备管理</c:if>
                                <c:if test="${servicelog.module == 3}">告警上报</c:if>
                            </td>
                            <td>${servicelog.ip}</td>
                            <td>
                                <span>
                                    <c:if test="${servicelog.level==9}">警告</c:if>
                                    <c:if test="${servicelog.level==10}">错误</c:if>
                                </span>
                            </td>
                            <td><fmt:formatDate value="${servicelog.time}"
                                                pattern="yyyy-MM-dd HH:mm:ss"/></td>
                            <td style="WORD-WRAP: break-word;text-align: left">${servicelog.description}</td>
                            <td class="AlertMenuTrigger" style="text-align:center">
                                <c:if test="${servicelog.affix==false}">
                                    <img class="getLog" src="/images/alert_normal.png" width="30px" style="cursor:pointer" height="30px" align="absmiddle" border="0">
                                </c:if>
                                <c:if test="${servicelog.affix==true}">
                                    <img class="getLog" src="/images/alert_handled.png" width="30px" style="cursor:pointer" height="30px" align="absmiddle" border="0">
                                </c:if>

                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <jsp:include page="/WEB-INF/views/common/pager.jsp"/>
        <script type="text/x-handlebars-template" id="dwonSystemLog">
            <form id="add-downServiceLog-form" name="add-downServiceLog-form" method="post" action="/log/downLogs">
                <table style="width: 150%" align="center" class="tblcol2">
                    <tbody>
                    <input type="hidden" id="serviceLogId" name="serviceLogId">
                    <tr style="height:28px">
                        <td style="text-align: right"><input type="checkbox" id="includeSystem" name="includeSystem" checked="checked"></td>
                        <td style="text-align: left"><span style="padding-left: 5px">系统日志</span></td>
                    </tr>
                    <tr style="height:20px">
                        <td style="text-align: right"><input type="checkbox" id="includeApplication" name="includeApplication" checked="checked"></td>
                        <td style="text-align: left"><span style="padding-left: 5px">软件日志</span></td>
                    </tr>

                    </tbody>
                </table>

                <div class="dialog-btns-center">
                    <div id="add-downServiceLog-ok-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span
                            class="btn-middle"> <span
                            class="btn-text">确定</span> </span> <span class="btn-right"></span> </a></div>

                    <div id="add-downServiceLog-cancel-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span
                            class="btn-middle"> <span class="btn-text">取消</span> </span> <span class="btn-right"></span>
                    </a></div>
                </div>
            </form>

        </script>
        <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>

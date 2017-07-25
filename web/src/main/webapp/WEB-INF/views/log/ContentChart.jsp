<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="helper" uri="/WEB-INF/jsphelper/helper.tld" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="../common/common.jsp" %>
    <utils:css path="/css/loglist.css,/css/file/userManage.css,/js/plugins/mulselect/multiple-select.css"/>
    <utils:js path="/js/plugins/jquery.extend.js,/js/logList.js,/js/My97DatePicker/WdatePicker.js,/js/echarts.js,
	/js/common/CommonDefine.js,/js/TMPlayerMngr.js,/js/Task.js,/js/plugins/mulselect/multiple-select.js"/>
    <script type="text/javascript">
        $(function () {
            $("select[name='groups']").multipleSelect({
                selectAllText: "全选",
                allSelected: "全选"
            });

            var cdView = new sv.chartLogView();
            cdView.render();
            $.pagination({"form": "search-form"});
            var TMPlayerMngrobj = new TMPlayerMngr();
            TMPlayerMngrobj.initRecrodPlayback();

        });

    </script>
</head>
<body onload="OnPageReady()">
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
        <div class="logType">
            <div id="logtab" class="tabbable" style=" background-color:#244d75; height:43px;">
                <ul>
                    <c:if test="${supportmosaic==false}">
                        <li id="logtab-tb1"><a data-toggle="tab" href="<c:url value="/log/cd"/>">内容报警日志</a>
                        </li>
                    </c:if>
                    <li id="logtab-tb2"><a data-toggle="tab" href="<c:url value="/log/s"/>">服务器日志</a></li>
                    <li id="logtab-tb3"><a data-toggle="tab" href="<c:url value="/log/op"/>">操作日志</a></li>
                    <c:if test="${supportmosaic==false}">
                        <li id="logtab-tb1" class="active"><a data-toggle="tab"
                                                              href="<c:url value="/log/chart"/>">内容报警报表</a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>

        <div class="log-list content-wrapper">
            <div class="searchbox">
                <form id="search-form" name="search-form" method="post" action="<c:url value="/log/baiduchart"/>">
                    <input type="hidden" name="export">
                        <span style="margin-left:5px;">导出条件：
                            <input class="Wdate"
                                   style=" width:150px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="startTime" value="${q.startTimeAsString}"
                                   onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:endTime.value})" >
                            &nbsp; 至 &nbsp;
                            <input class="Wdate"
                                   style="width:150px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="endTime" value="${q.endTimeAsString}"
                                   onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:startTime.value})">
                        </span>


                      <span style="margin-left: 5px;">频道组：
                            <select name="groups" multiple="multiple" class="status_sel">
                                <option value="-1" <c:if test="${helper:containsInteger(q.groups,-1)}">selected</c:if>>
                                    未分组
                                </option>
                                <c:forEach items="${groups}" var="group" varStatus="status">
                                    <option value="${group.id}"
                                            <c:if test="${helper:containsInteger(q.groups,group.id)}"></c:if>>
                                            ${group.name}</option>
                                </c:forEach>
                            </select>
                        </span>

                   <%-- <span style="margin-left: 5px;">频道：
                            <select name="groups" multiple="multiple" class="status_sel">
                                <option value="-1" <c:if test="${helper:containsInteger(q.groups,-1)}">selected</c:if>>
                                    JTVL
                                </option>
                                <option value="-1" <c:if test="${helper:containsInteger(q.groups,-1)}">selected</c:if>>
                                    hls
                                </option>
                                &lt;%&ndash; <c:forEach items="${groups}" var="group" varStatus="status">
                                     <option value="${group.id}"
                                             <c:if test="${helper:containsInteger(q.groups,group.id)}">selected</c:if>>
                                             ${group.name}</option>
                                 </c:forEach>&ndash;%&gt;
                            </select>
                        </span>--%>

                    <%--<input class="keyword" type="text" id="channelName" name="channelName" placeholder="频道名称搜索"
                           autocomplete="off" value="${q.channelName}" style="margin-left:5px;"/>--%>

                    <div id="check-btn" onclick="chart()" class="dialog-btn"
                         style="position:relative; top:10px;margin-left:5px;">
                        <a>
                            <span class="btn-left"></span>
                                <span class="btn-middle">
                                    <span class="btn-text">查询</span>
                                </span>
                            <span class="btn-right"></span>
                        </a>
                    </div>

                    <div id="export-btn" class="dialog-btn" style="position:relative; top:10px;margin-left:5px;">
                        <a>
                            <span class="btn-left"></span>
                                <span class="btn-middle">
                                    <span class="btn-text">导出</span>
                                </span>
                            <span class="btn-right"></span>
                        </a>
                    </div>

                </form>
            </div>
        </div>
    </div>
    <div id="main" style="width:100%;height:400px;"></div>

    <div class="push"></div>
</div>
</div>
<utils:js path="/js/logchart.js"/>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

</body>
</html>

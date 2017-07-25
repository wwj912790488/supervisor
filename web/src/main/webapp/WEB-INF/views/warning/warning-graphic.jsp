<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="helper" uri="/WEB-INF/jsphelper/helper.tld" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="../common/common.jsp" %>
    <utils:css path="/css/warning_graphic.css,/js/plugins/mulselect/multiple-select.css"/>
    <utils:js path="/js/plugins/momentjs/moment.min.js/,/js/plugins/ocanvas/ocanvas-2.8.1.js,/js/plugins/backbone/underscore.js,/js/plugins/backbone/backbone.js,/js/plugins/mulselect/multiple-select.js"/>
    <utils:js path="/js/warning_graphic.js"/>
    <script type="text/javascript">
        $(function() {
            $("select[name='types']").multipleSelect({
                selectAllText: "全选",
                allSelected: "全选"
            });

            $("select[name='groups']").multipleSelect({
                selectAllText: "全选",
                allSelected: "全选"
            });
            var types = '${types}';
            types = JSON.parse(types);
//            $.pagination({"form": "channel-select-form"});
            WarningGraphic.init(types);
        })
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">

        <div id="channel-graphic">
        <form id="channel-select-form" method="post"  action="<c:url value="/warning/graphic"/>">
            <!-- span>选择频道标签：</span>
            <select name="tags" multiple="multiple">
                <c:forEach items="${tags}" var="tag" varStatus="status">
                    <option value="${tag.id}" <c:if test="${helper:containsInteger(q.tags,tag.id)}">selected</c:if>>${tag.name}</option>
                </c:forEach>
            </select> -->
            <span style="margin-left:5px;">报警类型：
                <select name="types" multiple="multiple" class="status_sel">
                    <option value="0" <c:if test="${helper:containsInteger(q.types,0)}">selected</c:if>>黑场</option>
                    <option value="32" <c:if test="${helper:containsInteger(q.types,32)}">selected</c:if>>静音</option>
                    <option value="33" <c:if test="${helper:containsInteger(q.types,33)}">selected</c:if>>低音</option>
                    <option value="34" <c:if test="${helper:containsInteger(q.types,34)}">selected</c:if>>高音</option>
                    <option value="2" <c:if test="${helper:containsInteger(q.types,2)}">selected</c:if>>静帧</option>
                    <option value="27" <c:if test="${helper:containsInteger(q.types,27)}">selected</c:if>>CC错误</option>
                    <option value="28" <c:if test="${helper:containsInteger(q.types,28)}">selected</c:if>>Audio丢失</option>
                    <option value="29" <c:if test="${helper:containsInteger(q.types,29)}">selected</c:if>>Video丢失</option>
                    <option value="30" <c:if test="${helper:containsInteger(q.types,30)}">selected</c:if>>信源中断</option>

                </select>
            </span>
            <span style="margin-left: 5px;">频道组：
                <select name="groups" multiple="multiple" class="status_sel">
                    <option value="-1" <c:if test="${helper:containsInteger(q.groups,-1)}">selected</c:if>>未分组</option>
                    <c:forEach items="${groups}" var="group" varStatus="status">
                    <option value="${group.id}" <c:if test="${helper:containsInteger(q.groups,group.id)}">selected</c:if>>${group.name}</option>
                </c:forEach>
            </select>
        </span>
        <input class="keyword" type="text" id="channelName" name="channelName" placeholder="频道名称搜索"
        autocomplete="off" value="${q.channelName}" style="margin-left:5px;"/>

        <div id="search-btn" class="dialog-btn" style="position:relative; top:10px;margin-left:5px;">
            <a>
                <span class="btn-left"></span>
                <span class="btn-middle">
                    <span class="btn-text">搜索</span>
                </span>
                <span class="btn-right"></span>
            </a>
        </div>
        </form>
        <canvas id="channel-timeline" width="1100" height="30"></canvas>
        <c:forEach items="${pager.getContent()}" var="channel" varStatus="status">
            <div class="channel-item" data-id="${channel.id}">
                <span class="channel-name">${channel.name}</span>
                <canvas class="channel-canvas" id="channel-canvas-${channel.id}" width="1100" height="50"/>
            </div>
        </c:forEach>
        </div>
        <jsp:include page="/WEB-INF/views/common/pager.jsp"/>
        <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>

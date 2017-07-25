<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/file/userManage.css,/css/msglist.css"/>
    <utils:js path="/js/plugins/jquery.extend.js,/js/table.js,/js/msgManager.js"/>
    <script type="text/javascript">
        $(function () {
            var messageManagerObj = new messageManager();
            messageManagerObj.init();
            $.pagination({"method" : "get"});
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
        <div id="loading-dialog" class="modal" style="display:none;width: 350px;"></div>
        <div id="msglist" class="content-wrapper">
            <div id="messageList" class="msglist">
                <div id="messagepostbar" class="post-message">
                    <form name="post-msg-form" id="post-msg-form" style="margin-left: 20%;margin-right: 15%;" 
                     method="post" action="postMessage" onsubmit="return true">
                        <input class="messageinfo" type="text" id="message" name="message" placeholder="发送信息"
                               autocomplete="off" value="" style="width:400px;" required maxlength="64" minlength="4"/>
                        <input type="hidden" id="userName" name="userName" value="${login_info.userName}"/>
                        <input type="hidden" id="ipAddress" name="ipAddress" value="192.168.0.1"/>
                        <input type="hidden" name="dateTime" id="dateTime" value="2014-10-10 10:10:10">
                        <input id="postMsg" type="submit" style="display:none;">

                        <div class="action-btn">
                            <a id="post-msg-btn">
                                <span class="btn-left"></span>
                                <span class="btn-middle"> <span class="btn-text">发送</span>
                                </span> <span class="btn-right"></span>
                            </a>
                            <a style="margin-left: 5px;" id="clear-msg-btn">
                                <span class="btn-left"></span>
                                <span class="btn-middle"> <span class="btn-text">清除</span>
                                </span> <span class="btn-right"></span>
                            </a>
                        </div>
                    </form>
                </div>
                <div id="wrapper">
                    <div>
                        <div class="action-bar">
                            <div class="action-bar-item delete" id="delete-msg-btn">删除</div>
                        </div>

                        <div id="messagelist" class="tab-content">
                            <table id="message-list-table"
                                   style="table-layout:fixed;word-wrap:break-word;word-break:break-all">
                                <thead>
                                <tr>
                                    <th width="50px;"><input type="checkbox" class="select-all"/>
                                    </th>
                                    <th width="150px;">操作时间</th>
                                    <th width="80px;">操作用户</th>
                                    <th width="260px;">消息内容</th>
                                    <th width="120px;">IP地址</th>
                                    <th width="80px;">后续操作</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${pager.getContent()}" var="message" varStatus="status">
                                    <tr class="tasks">
                                        <td>
                                            <input type="checkbox" class="select-one" id="select-one" name="message-id"
                                                   value="${message.id}"/>
                                        </td>
                                        <td>${message.dateTime}</td>
                                        <td>${message.userName}</td>
                                        <td style="WORD-WRAP: break-word" width="260px;">${message.message}</td>
                                        <td>${message.ipAddress}</td>
                                        <td>
                                            <form class="postAgain" method="post" action="postMessage">
                                                <input id="message" name="message" type="hidden"
                                                       value="${message.message}"/>

                                                <div class="dialog-btn">
                                                    <a>
                                                        <span class="btn-left"></span>
					                <span class="btn-middle"> 
									<span class="btn-text">再次发送 </span>
					                </span>
                                                        <span class="btn-right"></span>
                                                    </a>
                                                </div>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>

                        </div>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="/WEB-INF/views/common/pager.jsp"/>
        <div class="push"></div>
    </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<script type="text/x-handlebars-template" id="messageTemplate">
    {{#each messages}}
    <tr class="TableItemText" style="text-align: center;">
        <td width="40px;"><input type="checkbox" class="select-one" name="message-id" value="{{id}}"></input></td>
        <td>{{dateTime}}</td>
        <td>{{userName}}</td>
        <td>{{message}}</td>
        <td>{{ipAddress}}</td>
        <td>
            <form class="postAgain" method="post" action="postMessage">
                <input id="message" name="message" type="hidden" value="{{message}}"/>

                <div class="dialog-btn">
                    <a>
                        <span class="btn-left"></span>
	                <span class="btn-middle"> 
					<span class="btn-text">再次发送 </span>
	                </span>
                        <span class="btn-right"></span>
                    </a>
                </div>
            </form>
        </td>
    </tr>
    {{/each}}
</script>
<script type="text/x-handlebars-template" id="loadingTemplate">
    <div class="dialog-caption">提示</div>
    <div class="dialog-content">
        <div style="height: 60px;">
            <div style="margin-left: 30%;padding-top: 20px;">
                <img src="<c:url value="/images/spinner.gif"/>">
                <span style="position: absolute;margin-left: 5px;">消息清除中，请稍等...</span>
            </div>
        </div>
    </div>
</script>
</body>
</html>

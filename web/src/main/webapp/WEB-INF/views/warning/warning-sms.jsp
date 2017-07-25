<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="p" uri="/WEB-INF/tags/profiles.tld" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="../common/common.jsp" %>
    <utils:css path="/css/warning.css"/>
    <utils:js path="/js/warning.js"/>
    <script type="text/javascript">
        $(function () {
            var smsView = new sv.SmsView();
            smsView.render();
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">

        <%--<jsp:include page="nav-config.jsp"/>--%>
            <div class="secondary-nav">
                <ul>
                    <li>
                        <a data-toggle="tab" href="/warning/report">告警上报</a>
                    </li>
                    <li>
                        <a data-toggle="tab" href="/warning/email">邮件告警</a>
                    </li>
                    <p:profiles hasProfiles="${_profile_sms}">
                        <li class="active">
                            <a data-toggle="tab" href="/warning/sms">短信告警</a>
                        </li>
                    </p:profiles>
                    <p:profiles hasProfiles="${_profile_voice}">
                        <li >
                            <a data-toggle="tab" href="/warning/push">声音报警</a>
                        </li>
                    </p:profiles>

                </ul>
            </div>

        <div id="sms-content" class="warning-content">
            <form id="sms-form" class="form">
                <s:eval expression="T(com.arcsoft.supervisor.model.domain.user.AbstractUser).ID_ADMINISTRATOR"
                        var="adminstratorId"/>
                <% /* Only visible for super administrator. */ %>
                <c:if test="${sessionScope.login_userinfo.id == adminstratorId}">
                    <p>
                        <label for="url">短信网关地址：</label>
                        <input type="text" id="url" name="url" class="input" placeholder="http://xxxx" tip-gravity="w"
                               value="${cfg.url}">
                    </p>
                    <p>
                        <label for="url">短信网关帐号：</label>
                        <input type="text" id="account" name="account" class="input" tip-gravity="w"
                               value="${cfg.account}">
                    </p>
                </c:if>
                <p>
                    <label for="phoneNumber">手机号码：</label>
                    <input type="text" id="phoneNumber" name="phoneNumber" class="input" placeholder="13800000000"
                           tip-gravity="w" value="${phoneNumber}">
                </p>
                <div class="text-ct">
                    <div id="save-btn" class="dialog-btn">
                        <a>
                            <span class="btn-left"></span>
                                <span class="btn-middle">
                                    <span class="btn-text">保存</span>
                                </span>
                            <span class="btn-right"></span>
                        </a>
                    </div>
                </div>
            </form>
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
</body>
</html>

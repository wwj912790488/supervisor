<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%// Defines all of profiles %>
<spring:eval expression="T(com.arcsoft.supervisor.utils.app.Environment.Profiler).STR_PRODUCTION" var="_profile_production" scope="request"/>
<spring:eval expression="T(com.arcsoft.supervisor.utils.app.Environment.Profiler).STR_SARTF" var="_profile_sartf" scope="request"/>
<spring:eval expression="T(com.arcsoft.supervisor.utils.app.Environment.Profiler).STR_SMS" var="_profile_sms" scope="request"/>
<spring:eval expression="T(com.arcsoft.supervisor.utils.app.Environment.Profiler).STR_VOICE" var="_profile_voice" scope="request"/>
<spring:eval expression="T(com.arcsoft.supervisor.utils.app.Environment.Profiler).STR_MESSAGE" var="_profile_message" scope="request"/>
<spring:eval expression="T(com.arcsoft.supervisor.utils.app.Environment.Profiler).STR_WG" var="_profile_wg" scope="request"/>
<spring:eval expression="T(com.arcsoft.supervisor.utils.app.Environment.Profiler).STR_MOSAIC" var="_profile_mosaic" scope="request"/>

<title>Supervisor</title>
<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.ico">
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<utils:css path="/js/plugins/modal/jquery.modal.css,
/js/plugins/tipsy/tipsy.css,
/js/plugins/prompt/ymPrompt.css,
/css/common/common.css"/>
<utils:js path="/js/jquery-1.11.1.min.js,
/js/plugins/handlebars/handlebars.js,
/js/plugins/modal/jquery.modal.min.js,
/js/plugins/validate/jquery.validate.min.js,
/js/plugins/validate/messages_zh.min.js,
/js/plugins/tipsy/jquery.tipsy.js,
/js/plugins/json/jquery.json.min.js,
/js/plugins/prompt/ymPrompt.js,
/js/plugins/backbone/underscore.js,
/js/plugins/backbone/backbone.js,
/js/plugins/backbone/backbone.marionette.js"/>
<utils:js path="/js/common/base.js"/>

<script type="text/javascript">
    <c:if test="${not empty sessionScope.login_userinfo.userName}">
        function AlertManager() {
            this.lastId = -1;
            this.getAlertTimerId = -1;
        }

        AlertManager.prototype = {
            init: function () {
                var _this = this;
                sv.ajax.getJSON('alert/get', {lastId: _this.lastId}, function (message) {
                    _this.lastId = message.id;
                });
                $("#alert_box").hide();
                $("#alert_close_btn").click(function() {
                    $("#alert_box").hide();
                });

                _this.getAlertTimerId = setInterval(function () {
                    _this.getAlert();
                }, 5000);
            },
            getAlert: function() {
                var _this = this;
                sv.ajax.getJSON('alert/get', {lastId: _this.lastId}, function(alert) {
                    if(alert.id != _this.lastId && alert.id != -1) {
                        _this.lastId = alert.id;
                        $("#alert_title").html(alert.title);
                        $("#alert_body_content").html(alert.message);
                        $("#alert_box").show();
                    }
                });
            }
        };
    </c:if>

    $(function(){
        sv.urlPath.setContextPath('<c:url value="/"/>');
        <c:if test="${not empty sessionScope.login_userinfo.userName}">
            <%-- for ajax session timeout check. --%>
            $.ajaxSetup({
                complete: function(request, status) {
                    var redirect = request.getResponseHeader("Redirect_Location");
                    if(redirect) {
                        window.location = sv.urlPath.getRealPath(redirect);
                    }
                }
            });
            // load global cached data.
            sv._config = {
                gpuConfig : {
                    enableSpan : false
                }
            };
            $.ajax({
                url : sv.urlPath.getRealPath('/cfg/c'),
                method : 'GET',
                async : false
            }).done(function(result){
                var gpuConfig = result.r.gpuConfig;
                if (gpuConfig && _.has(gpuConfig, 'enableSpan')) {
                    _.extend(sv._config.gpuConfig, gpuConfig);
                } else {
                    sv._config.gpuConfig.enableSpan = true;
                }
            });
            var alert = new AlertManager();
            alert.init();
        </c:if>
    });
</script>


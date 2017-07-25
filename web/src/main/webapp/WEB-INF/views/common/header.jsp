<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="p" uri="/WEB-INF/tags/profiles.tld" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<div class="header">
    <div class="left">
        <img src="<c:url value="/images/product_LOGO.png"/>" name="logo" width="110" height="50"
             style="border:0px;margin-top:20px;margin-bottom:22px;"/>
    </div>
    <div id="mag_tab">
        <c:set value="${login_userinfo}" var="login_info"/>
        <div id="inc_tab" class="tabbable">
            <p>
            <ul>
                <li id="tb0"><a data-toggle="tab" href="<c:url value="/home/index"/>">首页</a></li>
                <c:if test="${login_info.role != 0}">
                    <li id="tb2"><a data-toggle="tab" href="<c:url value="/device/index"/>">设备管理</a></li>
                </c:if>
                <li id="tb1"><a data-toggle="tab" href="<c:url value="/screen/index"/>">画面管理</a></li>
                <li id="tb5"><a data-toggle="tab" href="<c:url value="/channel/index"/>">频道管理</a></li>
                <p:profiles hasProfiles="${_profile_wg}">
                    <li id="tb9"><a data-toggle="tab" href="<c:url value="/warning/graphic"/>">告警监看</a></li>
                </p:profiles>
                <li id="tb4"><a data-toggle="tab" href="<c:url value="/profile/task"/>">模板管理</a></li>
                <c:if test="${login_info.role != 0}">
                    <li id="tb6"><a data-toggle="tab" href="<c:url value="/user/index"/>">用户管理</a></li>
                    <p:profiles hasProfiles="${_profile_message}">
                        <li id="tb3"><a data-toggle="tab" href="<c:url value="/msg/index"/>">消息管理</a></li>
                    </p:profiles>
                    <p:profiles hasProfiles="${_profile_mosaic}">
                        <li id="tb7"><a data-toggle="tab" href="<c:url value="/log/s"/>">日志管理</a></li>
                    </p:profiles>
                    <p:profiles nothasProfiles="${_profile_mosaic}">
                        <li id="tb7"><a data-toggle="tab" href="<c:url value="/log/cd"/>">日志管理</a></li>
                    </p:profiles>
                    <p:profiles nothasProfiles="${_profile_mosaic}">
                        <li id="tb10"><a data-toggle="tab" href="<c:url value="/package/ops"/>">OPS管理</a></li>
                    </p:profiles>
                </c:if>
                <c:if test="${login_info.role == 0}">
                    <li id="tb6"><a data-toggle="tab" href="<c:url value="/user/chgPswd"/>">用户管理</a></li>
                </c:if>
                <p:profiles nothasProfiles="${_profile_mosaic}">
                    <p:profiles hasAnyProfile="${_profile_voice},${_profile_sms}">
                        <c:set var="warningUrl" value="/warning/push"/>
                        <p:profiles hasProfiles="${_profile_sms}">
                            <c:set var="warningUrl" value="/warning/index"/>
                        </p:profiles>
                        <c:set var="warningUrl" value="/warning/report"/>
                        <li id="tb8"><a data-toggle="tab" href="<c:url value="${warningUrl}"/>">告警管理</a></li>
                    </p:profiles>
                </p:profiles>
                <p:profiles hasProfiles="${_profile_mosaic}">
                    <li id="tb11"><a data-toggle="tab" href="<c:url value="/master/master"/>">主备管理</a></li>
                    <!--主备管理-->
                </p:profiles>
            </ul>
        </div>
        <div style="position:absolute;right:82px;bottom:90px;width:150px;height:28px;line-height:30px;font-size:13px;font-weight:bold;color:#1b84a9;">
            欢迎您：${login_info.userName}
        </div>
        <div id="login_out"
             style="position:absolute;right:2px;bottom:90px;width:80px;height:28px;line-height:30px;font-size:15px;color:white;cursor:pointer;text-decoration:underline;"
             onclick="location.href='<c:url value="/login/out"/>'">
            退出登录
        </div>
    </div>
    <div class="center">
        <s:eval expression="T(com.arcsoft.supervisor.utils.app.Environment.EnvKey).version.name()" var="versionKey"/>
        <s:eval expression="T(com.arcsoft.supervisor.utils.app.Environment).getProperty(versionKey)" var="version"/>
        <p:profiles hasProfiles="${_profile_mosaic}">
            <div class="titleHead">马赛克导航系统 V${version}</div>
        </p:profiles>
        <p:profiles nothasProfiles="${_profile_mosaic}">
            <div class="titleHead">Supervisor V${version}</div>
        </p:profiles></div>
    <div id="tab_bar"></div>
    <div id="alert_box">
        <div id="alert_title_bar" class="clearfix">
            <div id="alert_title">Alert</div>
            <div id="alert_close_btn"></div>
        </div>
        <div id="alert_body" <c:if test="${login_info.role != 0}"> onclick="location.href='<c:url value="/log/cd"/>'"
        </c:if> >
            <span id="alert_body_content"></span>
        </div>
    </div>
</div>

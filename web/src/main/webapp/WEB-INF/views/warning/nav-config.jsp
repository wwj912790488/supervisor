<%@page pageEncoding="UTF-8" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="p" uri="/WEB-INF/tags/profiles.tld" %>

<s:eval expression="T(com.arcsoft.supervisor.model.domain.user.AbstractUser).ID_ADMINISTRATOR" var="adminstratorId"/>
<p:profiles hasAnyProfile="${_profile_sms},${_profile_voice}">
    <jsp:useBean id="navMap" class="java.util.LinkedHashMap">
        <p:profiles hasProfiles="${_profile_sms}">
            <c:set target="${navMap}" property="/warning/sms" value="短信告警"/>
        </p:profiles>
        <p:profiles hasProfiles="${_profile_voice}">
            <c:if test="${sessionScope.login_userinfo.id == adminstratorId}">
                <c:set target="${navMap}" property="/warning/push" value="声音报警"/>
            </c:if>
        </p:profiles>
        <c:set target="${navMap}" property="/warning/report" value="告警上报"/>
        <c:set target="${navMap}" property="/warning/email" value="邮件告警"/>
    </jsp:useBean>
    <tag:secondary-nav navs="${navMap}" activeIndex="${param.activeIndex eq null ? 0 : param.activeIndex}"/>
</p:profiles>

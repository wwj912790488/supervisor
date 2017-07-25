<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
/*
*  Defines tag file for secondary navigation.
*
*/
%>
<%@attribute name="navs" required="true" type="java.util.LinkedHashMap" description="Links maps.The url as key and text as value" %>
<%@attribute name="activeIndex" type="java.lang.Integer" %>
<c:set var="defaultActiveIndex" value="${activeIndex eq null ? 0 : activeIndex}"/>

<div class="secondary-nav">
    <ul>
        <c:forEach var="nav" items="${navs}" varStatus="s">
            <li ${defaultActiveIndex eq s.index ? 'class="active"' : ''}>
                <a data-toggle="tab" href="<c:url value="${nav.key}"/>">${nav.value}</a>
            </li>
        </c:forEach>
    </ul>

</div>

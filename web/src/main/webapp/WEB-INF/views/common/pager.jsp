<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>

<c:set var="pageCount" value="${pager.getTotalPages()}"/>
<c:set var="curPageNumber" value="${pager.getNumber() + 1}"/>

<jsp:useBean id="pageHepler" class="com.arcsoft.supervisor.commons.page.PageHelper" scope="page"/>
<jsp:setProperty property="curPage" name="pageHepler" value="${curPageNumber}"/>
<jsp:setProperty property="pageTotalCount" name="pageHepler" value="${pageCount}"/>
<jsp:setProperty property="navigatePageCount" name="pageHepler" value="3"/>

<div class="pagination">
  <c:if test="${pageCount > 1}">
    <a id="nav_pre" hideFocus class="<c:choose><c:when test="${curPageNumber > 1}">nav_pre</c:when><c:otherwise>nav_pre_disable</c:otherwise></c:choose>"></a>
    <c:if test="${curPageNumber !=1 }">
      <a class="nav_page" index="0" title="1" href="javascript:void(0)" hideFocus>1</a>
    </c:if>
    <c:if test="${pageHepler.isLeftDotEnabled()}">
      <span>...</span>
    </c:if>
    <c:forEach var="p" items="${pageHepler.getLeft()}">
      <a index="${p-1}" title="${p}" class="nav_page" href="javascript:void(0)" hideFocus>
        <utils:truncate value="${p + ''}" maxLength="4" append="."/>
      </a>
    </c:forEach>
    <a index="${pageHepler.getCurPage() - 1}" title="${pageHepler.getCurPage()}" class="nav_page active" href="javascript:void(0)" hideFocus>
      <utils:truncate value="${pageHepler.getCurPage() + ''}" maxLength="4" append="."/>
    </a>
    <c:forEach var="p" items="${pageHepler.getRight()}">
      <a index="${p - 1}" title="${p}" class="nav_page" href="javascript:void(0)" hideFocus>
        <utils:truncate value="${p + ''}" maxLength="4" append="."/>
      </a>
    </c:forEach>
    <c:if test="${pageHepler.isRightDotEnabled()}">
      <span>...</span>
    </c:if>
    <c:if test="${curPageNumber != pageCount}">
      <a class="nav_page"  index="${pageCount - 1}" title="${pageCount}" href="javascript:void(0)" hideFocus>
        <utils:truncate value="${pageCount + ''}" maxLength="4" append="."/>
      </a>
    </c:if>
    <a id="nav_next" href="javascript:void(0)" hideFocus class="<c:choose><c:when test="${curPageNumber < pageCount}">nav_next</c:when><c:otherwise>nav_next_disable</c:otherwise></c:choose>"></a>

  </c:if>
  <form style="display: none;" method="post" id="pagerForm" action="${requestScope['javax.servlet.forward.request_uri']}">
    <input type="hidden" id="pageIndex" name="page" value='${curPageNumber - 1}'>
    <c:if test="${not empty param.sort}">
      <input type="hidden" name="sort" value="${param.sort}"/>
    </c:if>
    <input type="hidden" id="pageUrl" value="${requestScope['javax.servlet.forward.request_uri']}">
  </form>
</div>

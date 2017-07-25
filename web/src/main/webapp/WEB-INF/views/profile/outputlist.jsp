<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/common/normalize.css"/>
    <utils:css path="/css/profile.css"/>
    <utils:js path="/js/plugins/backbone/underscore.js,/js/plugins/backbone/backbone.js,/js/plugins/backbone/backbone.marionette.js"/>
    <script type="text/javascript">window.state = {};</script>
    <utils:js path="/js/outputProfile.js"/>
    <script type="text/javascript">
        $(function () {
            OutputProfile.initList();
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
        <div>
            <div class="secondaryheadertable" style=" background-color:#244d75; height:43px;">
                <ul>
                    <li ><a data-toggle="tab" href="<c:url value="/profile/task"/>">任务模板</a>
                    </li>
                    <li class="active"><a data-toggle="tab" href="<c:url value="/profile/output"/>">输出参数模板</a></li>
                </ul>
            </div>
        </div>       
    	<div class="content-wrapper" id="outputprofilelist-container">
    	    <div id="create-btn-container">
                <div id="outputprofile-create-btn" class="action-btn">
                    <a>
                        <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">新建</span>
                            <span class="btn-icon"></span>
                        </span>
                        <span class="btn-right"></span>
                    </a>
                </div>
            </div>
            <div>
                <div class="action-bar">
                    <div class="disable">
                        <div class="action-bar-item edit" id="outputprofile-edit-btn">修改</div>
                    </div>
                    <div class="disable">
                        <div class="action-bar-item config" id="outputprofile-copy-btn">复制</div>
                    </div>
                    <div class="disable">
                        <div class="action-bar-item delete" id="outputprofile-delete-btn">删除</div>
                    </div>
                </div>
                <div>
                    <table id="outputprofilelist">
                        <thead>
                        <tr>
                            <th width="50px" class="right-align">
                                <input type="checkbox" id="select-all" class="select-all">
                            </th>
                            <th>名称</th>
                            <th>描述</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="profile" items="${pager.getContent()}">
                            <tr>
                                <td class="right-align">
                                    <input type="checkbox" name="id" value="${profile.id}" class="select-one">
                                </td>
                                <td>${profile.name}</td>
                                <td>${profile.videoAndAudioDescription}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script type="text/template" id="outputprofileitemTemplate">
    <td class="right-align">
        <input type="checkbox" name="id" value="{{id}}" class="select-one">
    </td>
    <td>{{profilename}}</td>
    <td>{{videoprofiles.[0].videocodec}} {{videoprofiles.[0].videowidth}}x{{videoprofiles.[0].videoheight}} {{videoprofiles.[0].videoratecontrol}} {{videoprofiles.[0].videobitrate}}Kbps | {{audioprofiles.[0].audiocodec}} {{audioprofiles.[0].audiochannel}}Channel(s) {{audioprofiles.[0].audiosamplerate}}Hz {{audioprofiles.[0].audiobitrate}}Kbps</td>
</script>
</body>
</html>

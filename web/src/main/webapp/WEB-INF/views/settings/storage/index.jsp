<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/file/userManage.css,/css/loglist.css"/>
    <utils:js path="/js/storage.js"/>
    <script type="text/javascript">
        $(function () {
            var storage = new Storage();
            storage.init();
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
        <div class="logType">
            <div id="logtab" class="tabbable" style=" background-color:#244d75; height:43px;">
                <ul>
                    <li id="logtab-tb1" class="active"><a data-toggle="tab" href="<c:url value="/storage/list"/> ">录制存储设置</a>
                    </li>
                    <li id="network-tab"><a data-toggle="tab" href="<c:url value="/network/index"/> ">网络设置</a>
                    </li>
                </ul>
            </div>
        </div>
        <div id="storage-dialog" class="modal" style="display:none;width: 800px;"></div>
        <div class="content-wrapper" style="margin-top: 15px;">
            <div class="add-user">
                <div class="action-btn" id="add-storage-btn">
                    <a>
                        <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">新增存储</span>
                            <span class="btn-icon"></span>
                        </span>
                        <span class="btn-right"></span>
                    </a>
                </div>
            </div>
            <div class="user-list">
                <div class="action-bar">
                    <div>
                        <div class="action-bar-item edit" id="edit-storage-btn">修改</div>
                    </div>
                    <div>
                        <div class="action-bar-item delete" id="delete-storage-btn">删除</div>
                    </div>
                </div>
                <div id="userlist" class="tab-content">
                    <table id="storage-list-table" style="width: 1024px;">
                        <thead>
                        <tr class="TableColTitleText" style="text-align: center;">
                            <th width="40px;"><input type="checkbox" class="select-all"/></th>
                            <th>名称</th>
                            <th>远程路径</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${allStorage}" var="storage">
                            <tr>
                                <td>
                                    <input type="checkbox" class="select-one" value="${storage.id}">
                                </td>
                                <td>${storage.name}</td>
                                <td>${storage.path}</td>
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

<script type="text/x-handlebars-template" id="editStorageTemplate">
        <div class="dialog-caption">{{#if id}}修改{{else}}添加{{/if}}存储</div>
        <div class="dialog-content">
            <form id="storage-form">
                <table width="60%" align="center" class="tblcol2">
                    <tbody>
                        <tr>
                            <td class="collbl TableColTitleText">类型：</td>
                            <td class="colval">
                                <select id="type" name="type">
                                    <option value="cifs" {{#ifCond type "==" "cifs"}}selected{{/ifCond}}>cifs</option>
                                    <option value="nfs" {{#ifCond type "==" "nfs"}}selected{{/ifCond}}>nfs</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td class="collbl TableColTitleText">远程路径：</td>
                            <td class="colval">
                                <input type="text" name="path" id="path" value="{{path}}" style="width: 240px;" required>
                            </td>
                        </tr>
                        <tr>
                            <td class="collbl TableColTitleText">名称：</td>
                            <td class="colval">
                                <input type="text" name="name" id="name" value="{{name}}" required>
                            </td>
                        </tr>
                        <tr>
                            <td class="collbl TableColTitleText">用户名：</td>
                            <td class="colval">
                                <input class="name" name="user" id="user" style="width: 240px;" type="text" value="{{user}}" required/>
                            </td>
                        </tr>
                        <tr>
                            <td class="collbl TableColTitleText">密码：</td>
                            <td class="colval">
                                <input class="password" style="width: 240px;padding:0;" type="password" name="pwd" id="pwd" value="{{pwd}}" required>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <input type="hidden" name="id" id="storage-id" value="{{id}}">
            </form>
        </div>
        <div class="dialog-btns">
            <div id="storage-cancel-btn" class="dialog-btn">
                <a>
                    <span class="btn-left"></span>
					<span class="btn-middle">
						<span class="btn-text">取消</span>
					</span>
                    <span class="btn-right"></span>
                </a>
            </div>
            <div id="storage-ok-btn" class="dialog-btn">
                <a>
                    <span class="btn-left"></span>
					<span class="btn-middle">
						<span class="btn-text">确定</span>
					</span>
                    <span class="btn-right"></span>
                </a>
            </div>
        </div>
</script>

</body>
</html>

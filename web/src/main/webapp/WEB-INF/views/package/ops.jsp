<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/package.css"/>
    <utils:js path="js/plugins/jquery.md5.js,/js/packageMngr.js"/>
    <script type="text/javascript">
        $(function () {
            var opsManagerObj = new OPSManager();
            opsManagerObj.init();
        });
    </script>
</head>

<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
        <div class="opsType">
            <div id="opstab" class="tabbable" style=" background-color:#244d75; height:43px;">
                <ul>
                    <li id="opstab-tb1" class="active"><a data-toggle="tab" href="<c:url value="/package/ops"/>">OPS设备管理</a>
                    </li>
                    <li id="opstab-tb2"><a data-toggle="tab" href="<c:url value="/package/package"/>">安装包管理</a></li>
                </ul>
            </div>
        </div>
        <div class="content-wrapper">
            <div class="ops-list">
                <div class="action-bar">
                    <div>
                        <div class="action-bar-item delete" id="delete-ops-btn">删除</div>
                    </div>
                </div>
                <div id="opslist" class="tab-content">
                    <table id="ops-list-table">
                        <thead>
                        <tr class="TableColTitleText" style="text-align: center;">
                            <th width="40px;"><input type="checkbox" class="select-all"></input></th>
                            <th>设备名称</th>
                            <th>设备IP地址</th>
                            <th>设备MAC地址</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <div class="push"></div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<script type="text/x-handlebars-template" id="opsTemplate">
    {{#each opss}}
    <tr class="TableItemText" style="text-align: center;">
        <td width="40px;"><input type="checkbox" class="select-one" name="ops-id" value="{{id}}"/></td>
        <td>{{name}}</td>
		<td>{{ip}}</td>
		<td>{{machineMac}}</td>
    </tr>
    {{/each}}
</script>
</body>
</html>

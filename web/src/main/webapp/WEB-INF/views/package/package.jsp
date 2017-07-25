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
            var packageManagerObj = new PackageManager();
            packageManagerObj.init();
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
                    <li id="opstab-tb1"><a data-toggle="tab" href="<c:url value="/package/ops"/>">OPS设备管理</a>
                    </li>
                    <li id="opstab-tb2" class="active"><a data-toggle="tab" href="<c:url value="/package/package"/>">安装包管理</a></li>
                </ul>
            </div>
        </div>
        <div id="packageadd" class="modal" style="display:none;">
            <div class="Package">
                <div class="dialog-caption" style="text-align: left;font-weight: bold;">新增安装包
                </div>
                <div class="dialog-content">

                </div>
            </div>
        </div>
        <div class="content-wrapper">
           <div class="add-package">
                <div class="action-btn" id="add-package-btn"><a> <span class="btn-left"></span> <span
                        class="btn-middle"> <span class="btn-text">新增安装包</span> <span class="btn-icon"></span> </span>
                    <span class="btn-right"></span> </a></div>
            </div>
            <div class="package-list">
                <div class="action-bar">
                    <div>
                        <div class="action-bar-item edit" id="deploy-package-btn">推送</div>
                    </div>
                    <div>
                        <div class="action-bar-item delete" id="delete-package-btn">删除</div>
                    </div>
                </div>
                <div id="packagelist" class="tab-content">
                    <table id="package-list-table">
                        <thead>
                        <tr class="TableColTitleText" style="text-align: center;">
                            <th width="40px;"><input type="checkbox" class="select-all"></input></th>
                            <th>安装包版本</th>
                            <th>上传日期</th>
                            <th>哈希值(MD5)</th>
                            <th>存储路径</th>
                            <th width="80px;">已推送</th>
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

<script type="text/x-handlebars-template" id="packageTemplate">
    {{#each packages}}
    <tr class="TableItemText" style="text-align: center;">
        <td width="40px;"><input type="checkbox" class="select-one" name="package-id" value="{{id}}"/></td>
        <td>{{version}}</td>
        <td>{{uploadDate}}</td>
		<td>{{fileHash}}</td>
		<td>{{uploadPath}}</td>
		<td>{{#ifCond isDeployVersion "==" "1"}}是{{/ifCond}}</td>
    </tr>
    {{/each}}
</script>
<script type="text/x-handlebars-template" id="addPackageTemplate">
    <form id="add-package-form">
        <table width="60%" align="center" class="tblcol2">
            <tbody>
            <tr>
                <td class="collbl TableColTitleText">安装包版本:</td>
                <td class="colval"><input class="version" name="version" id="version" style="width: 240px;" type="text"
                                          value="" required maxlength="20" minlength="7" placeholder="请输入版本号"/>
                </td>
            </tr>
            <tr>
                <td class="collbl TableColTitleText">上传日期:</td>
                <td class="colval">
                    <input class="uploadDate" name="uploadDate" id="uploadDate" style="width: 240px;" type="text"
                                          value="" readonly required placeholder="安装包上传完成后自动更新"/>
				</td>
            </tr>
			<tr>
                <td class="collbl TableColTitleText">文件哈希值:</td>
                <td class="colval">
                    <input class="fileHash" name="fileHash" id="fileHash" style="width: 240px;" type="text"
                                          value="" readonly required placeholder="安装包上传完成后自动更新"/>
				</td>
            </tr>
 			<tr>
                <td class="collbl TableColTitleText">上传路径:</td>
                <td class="colval">
                    <input class="uploadPath" name="uploadPath" id="uploadPath" style="width: 240px;" type="text" 
										 value="" readonly required placeholder="安装包上传完成后自动更新"/>
				</td>
            </tr>
            </tbody>
        </table>
    </form>
        <form id="setup_packge_upload_form">
            <div>
                <input type="file" id="setup_package" name="setup_package" />
                <input type="submit" id="setup_package_submit" value="上传安装包" />
                <span id="setup_package_submit_message"></span>
            </div>
        </form>
    <div class="dialog-btns">
        <div id="add-package-cancel-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span
                class="btn-middle"> <span class="btn-text">取消</span> </span> <span class="btn-right"></span> </a></div>
        <div id="add-package-ok-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span class="btn-middle"> <span
                class="btn-text">确定</span> </span> <span class="btn-right"></span> </a></div>
    </div>
</script>

</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/file/userManage.css"/>
    <utils:js path="js/plugins/jquery.md5.js,/js/userManager.js"/>
    <script type="text/javascript">
        $(function () {
            var userManagerObj = new UserManager();
            userManagerObj.init();
        });
    </script>
</head>

<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
        <div id="useradd" class="modal" style="display:none;">
            <div class="Account">
                <div class="dialog-caption" style="text-align: left;font-weight: bold;">新增用户
                </div>
                <div class="dialog-content">

                </div>
            </div>
        </div>
        <div id="useredit" class="modal" style="display: none;">
            <div class="Account">
                <input type="hidden" id="currentUserName" name="currentUserName" value="${login_info.userName}"/>

                <div class="dialog-caption" style="text-align: left;font-weight: bold;">编辑用户
                </div>
                <div class="dialog-content">

                </div>
            </div>
        </div>
        <div class="content-wrapper">
            <div class="add-user">
                <div class="action-btn" id="add-user-btn"><a> <span class="btn-left"></span> <span
                        class="btn-middle"> <span class="btn-text">新增用户</span> <span class="btn-icon"></span> </span>
                    <span class="btn-right"></span> </a></div>
            </div>
            <div class="user-list">
                <div class="action-bar">
                    <div>
                        <div class="action-bar-item edit" id="edit-user-btn">修改</div>
                    </div>
                    <div>
                        <div class="action-bar-item delete" id="delete-user-btn">删除</div>
                    </div>
                    <div>
                        <div class="action-bar-item edit" id="reset-user-pswd-btn">重置密码</div>
                    </div>
                </div>
                <div id="userlist" class="tab-content">
                    <table id="user-list-table">
                        <thead>
                        <tr class="TableColTitleText" style="text-align: center;">
                            <th width="40px;"><input type="checkbox" class="select-all"></input></th>
                            <th>用户名称</th>
                            <th>用户角色</th>
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

<script type="text/x-handlebars-template" id="userTemplate">
    {{#each users}}
    <tr class="TableItemText" style="text-align: center;">
        <td width="40px;">
            {{#ifCond id "==" "1"}}<input type="checkbox" class="select-one-disabled" disabled name="user-id"
                                          value="{{id}}"/>{{/ifCond}}
            {{#ifCond id ">" "1"}}<input type="checkbox" class="select-one" name="user-id" value="{{id}}"/>{{/ifCond}}
            <input name="username" id="username" type="hidden" value="{{userName}}"/>
            <input name="userrole" id="userrole" type="hidden" value="{{role}}"/></td>
        <td>{{userName}}</td>
        <td><span>{{#ifCond role "==" "0"}}操作员{{/ifCond}}
        {{#ifCond role "==" "1"}}管理员{{/ifCond}}</span></td>
    </tr>
    {{/each}}
</script>
<script type="text/x-handlebars-template" id="adminTemplate">
    {{#each users}}
    <tr class="TableItemText" style="text-align: center;">
        <td width="40px;">
            <input type="checkbox" class="select-one" name="user-id" value="{{id}}"/>
            <input name="username" id="username" type="hidden" value="{{userName}}"/>
            <input name="userrole" id="userrole" type="hidden" value="{{role}}"/></td>
        <td>{{userName}}</td>
        <td><span>{{#ifCond role "==" "0"}}操作员{{/ifCond}}
        {{#ifCond role "==" "1"}}管理员{{/ifCond}}</span></td>
    </tr>
    {{/each}}
</script>
<script type="text/x-handlebars-template" id="addUserTemplate">
    <form id="add-user-form">
        <table width="60%" align="center" class="tblcol2">
            <tbody>
            <tr>
                <td class="collbl TableColTitleText">用户名：</td>
                <td class="colval"><input class="name" name="userName" id="userName" style="width: 240px;" type="text"
                                          value="" required maxlength="12" minlength="4"/>
                </td>
            </tr>
            <tr>
                <td class="collbl TableColTitleText">密码：</td>
                <td class="colval"><input class="password" style="width: 240px;padding:0;" type="password"
                                          id="add-user-password" value="" required minlength="4"></td>
                <input type="hidden" name="password" value="">
            </tr>
            <tr>
                <td class="collbl TableColTitleText">再次确认密码：</td>
                <td class="colval"><input class="repeatpwd" name="repeatpwd" id="repeatpwd"
                                          style="width: 240px;padding:0;" type="password" value="" required
                                          minlength="4" equalTo=#add-user-password>
                </td>
            </tr>
            <tr>
                <td class="collbl TableColTitleText">用户角色：</td>
                <td class="colval">
                    <select id="userRoleId" name="role" class="rolesel">
                        <option value="0">操作员</option>
                        <option value="1">管理员</option>
                    </select>
            </tr>
            </tbody>
        </table>
    </form>
    <div class="dialog-btns">
        <div id="add-user-cancel-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span
                class="btn-middle"> <span class="btn-text">取消</span> </span> <span class="btn-right"></span> </a></div>
        <div id="add-user-ok-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span class="btn-middle"> <span
                class="btn-text">确定</span> </span> <span class="btn-right"></span> </a></div>
    </div>
</script>
<script type="text/x-handlebars-template" id="editUserPswdTemplate">
    <form id="edit-user-pswd-form">
        <table width="60%" align="center" class="tblcol2">
            <tbody>
            <input type="hidden" name="id" value=""/>
            <tr>
                <td class="collbl TableColTitleText">旧密码：</td>
                <td class="colval"><input class="oldpassword" style="width: 240px;padding:0;" type="password"
                                          id="edit-user-old-password" name="edit-user-old-password" value="" required
                                          minlength="4"/></td>
                <input type="hidden" name="password" value="">
            </tr>
            <tr>
                <td class="collbl TableColTitleText">新密码：</td>
                <td class="colval"><input class="newpassword" style="width: 240px;padding:0;" type="password"
                                          id="edit-user-new-password" name="edit-user-new-password" value="" required
                                          minlength="4"/></td>
                <input type="hidden" name="newPassword" value="">
            </tr>
            <tr>
                <td class="collbl TableColTitleText">再次确认新密码：</td>
                <td class="colval"><input class="repeatpwd" style="width: 240px;padding:0;" type="password"
                                          name="repeatpwd" id="edit-repeatpwd" value="" required minlength="4"
                                          equalTo=#edit-user-new-password>
                </td>
            </tr>
            </tbody>
        </table>
    </form>

    <div class="dialog-btns">
        <div class="dialog-btn" id="edit-user-cancel-btn"><a href="#"> <span class="btn-left"></span> <span
                class="btn-middle"> <span class="btn-text">取消</span> </span> <span class="btn-right"></span> </a></div>
        <div class="dialog-btn" id="edit-user-ok-btn"><a href="#"> <span class="btn-left"></span> <span
                class="btn-middle"> <span class="btn-text">确定</span> </span> <span class="btn-right"></span> </a></div>
    </div>
</script>

<script type="text/x-handlebars-template" id="resetUserPswdTemplate">
    <form id="reset-user-pswd-form">
        <table width="60%" align="center" class="tblcol2">
            <tbody>
            <input type="hidden" name="id" value=""/>
            <tr>
                <td class="collbl TableColTitleText">新密码：</td>
                <td class="colval"><input class="newpassword" style="width: 240px;padding:0;" type="password"
                                          id="edit-user-new-password" name="edit-user-new-password" value="" required
                                          minlength="4"/></td>
                <input type="hidden" name="newPassword" value="">
            </tr>
            <tr>
                <td class="collbl TableColTitleText">再次确认新密码：</td>
                <td class="colval"><input class="repeatpwd" style="width: 240px;padding:0;" type="password"
                                          name="repeatpwd" id="edit-repeatpwd" value="" required minlength="4"
                                          equalTo=#edit-user-new-password>
                </td>
            </tr>
            </tbody>
        </table>
    </form>

    <div class="dialog-btns">
        <div class="dialog-btn" id="reset-user-pswd-cancel-btn"><a href="#"> <span class="btn-left"></span> <span
                class="btn-middle"> <span class="btn-text">取消</span> </span> <span class="btn-right"></span> </a></div>
        <div class="dialog-btn" id="reset-user-pswd-ok-btn"><a href="#"> <span class="btn-left"></span> <span
                class="btn-middle"> <span class="btn-text">确定</span> </span> <span class="btn-right"></span> </a></div>
    </div>
</script>

<script type="text/x-handlebars-template" id="editUserRoleTemplate">
    <form id="edit-user-role-form">
        <table width="60%" align="center" class="tblcol2">
            <tbody>
            <input type="hidden" name="id" value=""/>
            <tr>
                <td class="collbl TableColTitleText">用户名：</td>
                <td class="colval"><span id="username" name="username"></span></td>
            </tr>
            <tr>
                <td class="collbl TableColTitleText">用户角色：</td>
                <td class="colval"><select id="userRoleId" class="rolesel" name="role">
                    <option value="0">操作员</option>
                    <option value="1">管理员</option>
                </select>
                </td>
            </tr>
            </tbody>
        </table>
    </form>

    <div class="dialog-btns">
        <div class="dialog-btn" id="edit-user-cancel-btn"><a href="#"> <span class="btn-left"></span> <span
                class="btn-middle"> <span class="btn-text">取消</span> </span> <span class="btn-right"></span> </a></div>
        <div class="dialog-btn" id="edit-user-ok-btn"><a href="#"> <span class="btn-left"></span> <span
                class="btn-middle"> <span class="btn-text">确定</span> </span> <span class="btn-right"></span> </a></div>
    </div>
</script>

</body>
</html>

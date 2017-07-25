<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/file/userManage.css"/>
    <utils:js path="/js/plugins/jquery.md5.js,/js/userManager.js"/>
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
        <div class="content-wrapper">
            <div id="useredit">
                <div class="Account">
                    <input type="hidden" id="currentUserName" name="currentUserName" value="${login_userinfo.userName}"/>
                    <input type="hidden" id="currentUserId" name="currentUserId" value="${login_userinfo.id}"/>

                    <div class="dialog-caption" style="text-align: left;font-weight: bold;">编辑用户
                    </div>
                    <div class="dialog-content">
                        <form id="edit-user-pswd-form">
                            <table width="60%" align="center" class="tblcol2">
                                <tbody>
                                <input type="hidden" name="id" value=""/>
                                <tr>
                                    <td class="collbl TableColTitleText">旧密码：</td>
                                    <td class="colval"><input class="oldpassword" style="width: 240px;padding:0;"
                                                              type="password" id="edit-user-old-password"
                                                              name="edit-user-old-password" value="" required
                                                              minlength="4"/></td>
                                    <input type="hidden" name="password" value="">
                                </tr>
                                <tr>
                                    <td class="collbl TableColTitleText">新密码：</td>
                                    <td class="colval"><input class="newpassword" style="width: 240px;padding:0;"
                                                              type="password" id="edit-user-new-password"
                                                              name="edit-user-new-password" value="" required
                                                              minlength="4"/></td>
                                    <input type="hidden" name="newPassword" value="">
                                </tr>
                                <tr>
                                    <td class="collbl TableColTitleText">再次确认新密码：</td>
                                    <td class="colval"><input class="repeatpwd" style="width: 240px;padding:0;"
                                                              type="password" name="repeatpwd" id="edit-repeatpwd"
                                                              value="" required minlength="4"
                                                              equalTo=#edit-user-new-password>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </form>

                        <div class="dialog-btns" style="margin-left:32%">
                            <!--<div class="dialog-btn" id="edit-user-cancel-btn"><a href="#"> <span
                                    class="btn-left"></span> <span class="btn-middle"> <span class="btn-text">取消</span> </span>
                                <span class="btn-right"></span> </a></div>-->
                            <div class="dialog-btn" id="edit-user-ok-btn"><a href="#"> <span class="btn-left"></span>
                                <span class="btn-middle"> <span class="btn-text">确定</span> </span> <span
                                        class="btn-right"></span> </a></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="push"></div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>

<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/package.css"/>
    <utils:js path="js/plugins/jquery.md5.js"/>
    <utils:js path="js/validdata.js"/>
    <script type="text/javascript">
        $(function () {
            sv.nav.active('tb11');
            $(".select-all").prop("checked",
                    $(".select-one[type='checkbox']:checked").length > 0 && $(".select-one[type='checkbox']:checked").length === $(".select-one[type='checkbox']").length);
            $(".select-all").click(function () {
                $(".select-one").prop("checked", this.checked);
            });

            $("#add-Master").click(function () {

                $("#packageadd .dialog-content").render("addMasterTemplate");
                $("#packageadd").modal({
                    showClose: false, clickClose: false
                });

            });
            //deploy-package-btn
            /*  $(".action-bar-item edit").on("click", "#delete-package-btn", function () {
             alert("delete");
             }, this);*/

            $("#deploy-package-btn").bind("click", function () {
                if (confirm("是否要启动")) {
                    var $userObjs = $("input[name='package-id']:checked");
                    if ($userObjs.length > 0) {
                        var showWarning = false;
                        var params = [];
                        for (var i = 0, len = $userObjs.length; i < len; i++) {
                            var user = {};
                            user.id = $($userObjs.get(i)).val();
                            params[i] = user;
                        }
                        $.get("doshell", {masterId: $.toJSON(params)}, $.proxy(function () {

                        }, this));
                        alert("启动成功！");
                    }
                } else {
                    window.location.reload();

                }
                ;

            });


            //delete-package-btn
            $("#delete-package-btn").bind("click", function () {
                if (confirm("是否要删除")) {
                    var $userObjs = $("input[name='package-id']:checked");
                    var showWarning = false;
                    var params = [];
                    for (var i = 0, len = $userObjs.length; i < len; i++) {
                        var user = {};
                        user.id = $($userObjs.get(i)).val();
                        params[i] = user;
                    }
                    $.get("delete", {masterId: $.toJSON(params)}, $.proxy(function () {
                        $("input[name='package-id']:checked").parent().parent().remove();
                    }, this));

                } else {
                    window.location.reload();
                }
            });


            $("#packageadd").on("click", "#add-package-cancel-btn", $.proxy(function () {
                $.modal.close();
                $(".tipsy").remove();
            }, this));
            $("#packageadd").on("click", "#add-package-ok-btn", $.proxy(function () {
                var validator = $("#add-master-form").validate();
                var ip = $("#ip").val(), port = $("#port").val(), username = $("#username").val();
                if (_.isEmpty(ip.trim())) {
                    validator.showErrors({
                        "ip": "请输入IP"
                    });
                }
                if (_.isEmpty(port.trim())) {
                    validator.showErrors({
                        "port": "请输入端口"
                    });

                }
                if (_.isEmpty(username.trim())) {
                    validator.showErrors({
                        "username": "请输入用户名"
                    });

                }
                if (validator.valid()) {
                    $.ajax({
                        type: "POST",
                        url: "/master/save",
                        data: $('#add-master-form').serialize(),
                        success: function (result) {
                            if (result.status == true) {
                                window.location.reload();
                            } else {
                                alert(result.msg)
                            }


                        }

                    });
                }

            }, this));


        });

    </script>

</head>

<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
        <div id="packageadd" class="modal" style="display:none;">
            <div class="Package">
                <div class="dialog-caption" style="text-align: left;font-weight: bold;">新增地址
                </div>
                <div class="dialog-content">

                </div>
            </div>
        </div>
        <div class="content-wrapper">
            <div class="add-package">
                <div class="action-btn" id="add-Master"><a> <span class="btn-left"></span> <span
                        class="btn-middle"> <span class="btn-text">新增设置</span> <span class="btn-icon"></span> </span>
                    <span class="btn-right"></span> </a></div>
            </div>

            <div class="package-list">
                <div class="action-bar">
                    <div>
                        <div class="action-bar-item edit" id="deploy-package-btn">启动</div>
                    </div>
                    <div>
                        <div class="action-bar-item delete" id="delete-package-btn">删除</div>
                    </div>
                </div>

                <table id="package-list-table">
                    <thead>
                    <tr class="TableColTitleText" style="text-align: center;">
                        <th width="40px;"><input type="checkbox" class="select-all"></th>
                        <th>ID</th>
                        <th>IP地址</th>
                        <th>主标识</th>
                        <th>主地址</th>
                        <th>是否主备</th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach var="result" items="${resultList}">
                        <tr class="TableItemText" style="text-align: center;">
                            <td width="40px;"><input type="checkbox" class="select-one" name="package-id"
                                                     value="${result.id}"/></td>
                            <td>${result.id}</td>
                            <td>${result.ip}</td>
                            <td>${result.backupFlag}</td>
                            <td>${result.backupAdress}</td>
                            <td>${result.flag}</td>
                        </tr>
                    </c:forEach>

                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</div>


</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script type="text/x-handlebars-template" id="addMasterTemplate">
    <form id="add-master-form">
        <table width="60%" align="center" class="tblcol2">
            <tbody>
            <tr>
                <td class="collbl TableColTitleText">IP地址:</td>
                <td class="colval"><input class="ip" name="ip" id="ip" style="width: 240px;" type="text"
                                          value="" required="required"
                                          placeholder="请输入IP地址"/>
                </td>
            </tr>
            <td class="collbl TableColTitleText">ssh端口号</td>
            <td class="colval"><input class="port" name="port" id="port" style="width: 240px;" type="text"
                                      value="" required="required"
                                      placeholder="ssh端口号"/>
            </td>
            </tr>


            <tr>
                <td class="collbl TableColTitleText">用户名:</td>
                <td class="colval">
                    <input class="username" name="username" id="username" style="width: 240px;" type="text"
                           value="" required="required" placeholder="请输入用户名"/>
                </td>
            </tr>
            <tr>
                <td class="collbl TableColTitleText">密 码:</td>
                <td class="colval">
                    <input class="password" name="password" id="password" style="width: 240px;" type="password"
                           value="" required="required" placeholder="请输入密码"/>
                </td>
            </tr>
            <tr>
                <td class="collbl TableColTitleText">是否主从:</td>
                <td class="colval">
                    <%--<input type=”radio” name=”identity” value=”学生” checked=”checked” />学生--%>
                    <input class="flag" name="flag" id="flag" style="width: 60px" type="radio"
                           value="1" required placeholder="主:1" checked=”checked”/>主设备
                    <input class="flag" name="flag" id="flag" style="width: 60px" type="radio"
                           value="0" required placeholder="否:0"/>从设备
                </td>
            </tr>

            </tbody>
        </table>
    </form>

    <div class="dialog-btns">
        <div id="add-package-cancel-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span
                class="btn-middle"> <span class="btn-text">取消</span> </span> <span class="btn-right"></span> </a></div>
        <div id="add-package-ok-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span
                class="btn-middle"> <span
                class="btn-text">确定</span> </span> <span class="btn-right"></span> </a></div>
    </div>
</script>

</body>
</html>

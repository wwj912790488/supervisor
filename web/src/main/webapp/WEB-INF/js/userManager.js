function UserManager() {

}

UserManager.prototype = {
    init: function() {
        var _this = this;
        sv.nav.active('tb6');
        this.reloadUsers();
        $(".select-all").click(function () {
            $(".select-one").prop("checked", this.checked);
            _this.updateActionBar();
        });
        $("#user-list-table").on('click', ".select-one", function () {
            _this.updateActionBar();
        });
        $("#add-user-btn").click(function() {
            $("#useradd .dialog-content").render("addUserTemplate");
            $("#useradd").modal({
                showClose: false, clickClose:false
            });
        });

        $("#useradd").on("click", "#add-user-cancel-btn", $.proxy(function () {
            $.modal.close();
        }, this)).on("click", "#add-user-ok-btn", $.proxy(function () {
            _this.addUser();
        }, this));

        $("#useredit").on("click", "#edit-user-cancel-btn", $.proxy(function () {
            $.modal.close();
        }, this)).on("click", "#edit-user-ok-btn", $.proxy(function () {
            _this.editUser();
        }, this));

        $("#useredit").on("click", "#reset-user-pswd-cancel-btn", $.proxy(function () {
            $.modal.close();
        }, this)).on("click", "#reset-user-pswd-ok-btn", $.proxy(function () {
            _this.resetUserPswd();
        }, this));

        $("#useradd").cleanTipsyOnModalClose();
        $("#useredit").cleanTipsyOnModalClose();

        $("#edit-user-btn").click($.proxy(function () {
            if($("#currentUserName").val() != $(".select-one[type='checkbox']:checked").siblings($("#username")).val())
            {
                var $userObjs = $("input[name='user-id']:checked");
                if ($userObjs.length == 1) {
                    $("#useredit .dialog-content").render("editUserRoleTemplate");
                    $("#edit-user-role-form #username").text($userObjs.siblings($("#username")).val());
//                   console.log("userrole:" + $userObjs.siblings($("#username")).next().val());
                    if( $userObjs.siblings($("#username")).next().val() == "1")
                        $("#edit-user-role-form #userRoleId").val("1");
                    $("#useredit").modal({
                        showClose: false, clickClose:false
                    });
                }
            }
            else
            {
                var $userObjs = $("input[name='user-id']:checked");
                if ($userObjs.length == 1) {
                    $("#useredit .dialog-content").render("editUserPswdTemplate");
                    $("#useredit").modal({
                        showClose: false, clickClose:false
                    });
                }
            }
        }, this));

        $("#delete-user-btn").click(function () {
            if(!($("#delete-user-btn").parent().hasClass("disable"))) {
                sv.prompt.showConfirm2('是否删除所选记录？', function () {
                    _this.deleteUser();
                });
            }
        });

        $("#reset-user-pswd-btn").click($.proxy(function () {
            if(!($("#reset-user-pswd-btn").parent().hasClass("disable"))){
                var $userObjs = $("input[name='user-id']:checked");
                if ($userObjs.length == 1) {
                    $("#useredit .dialog-content").render("resetUserPswdTemplate");
                    $("#useredit").modal({
                        showClose: false, clickClose:false
                    });
                }
            }


        }, this));

        this.updateActionBar();
    },

    updateActionBar: function () {
        $(".select-all").prop("checked",
            $(".select-one[type='checkbox']:checked").length > 0 && $(".select-one[type='checkbox']:checked").length === $(".select-one[type='checkbox']").length);
        if ($(".select-one[type='checkbox']:checked").length > 1) {
            $("#edit-user-btn").parent().addClass("disable");
            $("#delete-user-btn").parent().removeClass("disable");
            $("#reset-user-pswd-btn").parent().addClass("disable");
        } else if($(".select-one[type='checkbox']:checked").length == 1) {
            $("#edit-user-btn").parent().removeClass("disable");
            if($("#currentUserName").val() != $(".select-one[type='checkbox']:checked").siblings($("#username")).val()){
                $("#delete-user-btn").parent().removeClass("disable");
                $("#reset-user-pswd-btn").parent().removeClass("disable");
            }
        } else {
            $("#edit-user-btn").parent().addClass("disable");
            $("#delete-user-btn").parent().addClass("disable");
            $("#reset-user-pswd-btn").parent().addClass("disable");
        }
    },
    reloadUsers: function () {
        var _this = this;
        $.getJSON("users?r=" + Math.random(), function (users) {
            if($("#currentUserName").val() == "admin"){
                $("#user-list-table > tbody").render("adminTemplate", {"users": users});
            }
            else {
                $("#user-list-table > tbody").render("userTemplate", {"users": users});
            }
            _this.updateActionBar();
        });
    },
    addUser: function () {
        var addUserForm = $("#add-user-form");
        var validator = addUserForm.validate({
            rules: {
                userName: {
                    chkchrnum: true
                }
            }
        });
        if (addUserForm.valid()) {
            var md5pw = $.md5($("#add-user-password").val());
            $("#add-user-form input[name='password']").val(md5pw);
            var data = $("#add-user-form").serializeArray();

            $.post("addUser", data, $.proxy(function (result) {
                if(!result) {
                    validator.showErrors({
                        "userName" : "该用户已存在"
                    });
                } else {
                    $(".select-all").prop("checked", false);
                    this.reloadUsers();
                    $.modal.close();
                }
            }, this));
        }

    },
    deleteUser: function () {
        var $userObjs = $("input[name='user-id']:checked");
        if ($userObjs.length > 0) {
            var showWarning = false;
            var params = [];
            for (var i = 0, len = $userObjs.length; i < len; i++) {
                var user = {};
                user.id = $($userObjs.get(i)).val();
                if($($userObjs.get(i)).next().val() == $("#currentUserName").val()){
                    showWarning = true;
                    break;
                }
                params[i] = user;
            }
            if(!showWarning){
                $.post("delete", {usersStr: $.toJSON(params)}, $.proxy(function () {
                    this.reloadUsers();
                }, this));
            }else{
                sv.prompt.error2('无法删除当前登录账号！');
            }
        }
    },
    editUser: function () {

        var changePswd = $("#currentUserId").length > 0 ? true : false;
        if(changePswd || ($("#currentUserName").val() == $(".select-one[type='checkbox']:checked").siblings($("#username")).val())) 	// edit pswd
        {
            var editUserPswdFrom = $("#edit-user-pswd-form");
            var validator = editUserPswdFrom.validate();
            if (editUserPswdFrom.valid()) {
                if(changePswd)
                    $("#edit-user-pswd-form input[name='id']").val($("#currentUserId").val());
                else
                    $("#edit-user-pswd-form input[name='id']").val($("input[name='user-id']:checked").eq(0).val());
                var md5oldpw = $.md5($("#edit-user-old-password").val());
                $("#edit-user-pswd-form input[name='password']").val(md5oldpw);
                var md5newpw = $.md5($("#edit-user-new-password").val());
                $("#edit-user-pswd-form input[name='newPassword']").val(md5newpw);
                var data = $("#edit-user-pswd-form").serializeArray();

                $.post("editUserPswd", data, $.proxy(function (result) {
                    if(!result) {
                        validator.showErrors({
                            "edit-user-old-password" : "密码错误"
                        });
                    } else {
                        alert("密码修改成功！")
                        if(changePswd)
                        {
                            $("#edit-user-old-password").val("");
                            $("#password").val("");
                            $("#edit-user-new-password").val("");
                            $("#newPassword").val("");
                            $("#edit-repeatpwd").val("");
                        }
                        else
                        {
                            $(".select-all").prop("checked", false);
                            this.reloadUsers();
                            $.modal.close();
                        }

                    }
                }, this));
            }
        }
        else  //edit role
        {
            var editUserRoleFrom = $("#edit-user-role-form");
            $("#edit-user-role-form input[name='id']").val($("input[name='user-id']:checked").eq(0).val())
            var data = $("#edit-user-role-form").serializeArray();

            $.post("editUserRole", data, $.proxy(function (result) {
                if(!result) {

                } else {
                    $(".select-all").prop("checked", false);
                    this.reloadUsers();
                    $.modal.close();
                }
            }, this));
        }
    },
    resetUserPswd: function () {
        {
            var resetUserPswdFrom = $("#reset-user-pswd-form");
            var validator = resetUserPswdFrom.validate();
            if (resetUserPswdFrom.valid()) {
                $("#reset-user-pswd-form input[name='id']").val($("input[name='user-id']:checked").eq(0).val());
                var md5newpw = $.md5($("#edit-user-new-password").val());
                $("#reset-user-pswd-form input[name='newPassword']").val(md5newpw);
                var data = $("#reset-user-pswd-form").serializeArray();

                $.post("resetUserPswd", data, $.proxy(function (result) {
                    if(!result) {

                    } else {
                        $(".select-all").prop("checked", false);
                        this.reloadUsers();
                        $.modal.close();
                    }
                }, this));
            }
        }

    }
};


// $(document).ready(function(){
// 	$("#inc_tab #tb2").removeClass();
// 	$("#inc_tab #tb4").addClass("active");

// 	$("#adduser").click(function(){
// 		$("#useradd").show().siblings().hide();
// 	});

// 	$("#useradd").find(".repeatpwd").blur(function(){
// 		var pwd = $("#useradd").find(".password").val();
// 		if($(this).val() != pwd){
// 			$(this).next().show();
// 		}
// 		else{
// 			$(this).next().hide();
// 		}
// 	})
// 	$("#useradd").find(".name").blur(function(){
// 		var name = $.trim($(this).val());
// 		$(this).val(name);

// 		var obj = $(this).parent().find(".functionWarn");
// 		obj.hide();
// 		$.post("name_verify", {name : name}, function(data) {
// 			if(data.toString().length > 0){
// 				obj.show();
// 			}
// 		})
// 	})

// 	$("#useredit").find(".user_name").change(function(){
// 		var name = $.trim($(this).val());
// 		$(this).val(name);

// 		var obj = $(this).parent().find(".functionWarn");
// 		obj.hide();
// 		var id = $("#useredit").find("input[name='user_id']").val();
// 		$.post("name_verify", {id : id, name : name}, function(data) {
// 			if(data.toString().length > 0){
// 				obj.show();
// 			}
// 		})
// 	})
// 	$("#useredit").find(".oldpassword").blur(function(){
// 		var password = $(this).val();
// 		if(password != ""){
// 			var obj = $(this).parent().find(".functionWarn");
// 			obj.hide();

// 			var id = $("#useredit").find("input[name='user_id']").val();
// 			var pwd = $.md5(password);
// 			$.post("password_verify", {id : id, password : pwd}, function(data) {
// 				if(data.toString().length > 0){
// 					obj.show();
// 				}
// 			})
// 		}
// 	})
// 	$("#useredit").find(".repeatpwd").blur(function(){
// 		var pwd = $("#useredit").find(".newpassword").val();
// 		if($(this).val() != pwd){
// 			$(this).next().show();
// 		}
// 		else{
// 			$(this).next().hide();
// 		}
// 	})

// })

// function OnAddSelectRole(obj){
// 	var role = $(obj).val();
// 	if(parseInt(role) == 1){
// 		$("#AddRolePriv-1").show();
// 		$("#AddRolePriv-2").hide();
// 		$(obj).parent().parent().find("input[name='roleID']").attr("value", "1");
// 	}else if(parseInt(role) == 2){
// 		$("#AddRolePriv-2").show();
// 		$("#AddRolePriv-1").hide();
// 		$(obj).parent().parent().find("input[name='roleID']").attr("value", "2");
// 	}
// }
// function OnEditSelectRole(obj){
// 	var role = $(obj).val();
// 	if(parseInt(role) == 1){
// 		$("#EditRolePriv-1").show();
// 		$("#EditRolePriv-2").hide();
// 		$(obj).parent().parent().find("input[name='roleID']").attr("value", "1");
// 	}else if(parseInt(role) == 2){
// 		$("#EditRolePriv-2").show();
// 		$("#EditRolePriv-1").hide();
// 		$(obj).parent().parent().find("input[name='roleID']").attr("value", "2");
// 	}
// }

// function deleteUser(obj){
// 	var msg = "请确认删除此记录！";
// 	if(confirm(msg)){
// 		var user_id = $(obj).parent().parent().find("input[name='user_id']").val();
// 		$.post("deleteUser", {id : user_id}, function(data) {

// 		});
// 		$(obj).parent().parent().remove();
// 	}
// }

// function editUser(obj){
// 	var user_id = $(obj).parent().parent().find("input[name='user_id']").val();
// 	var user_name = $(obj).parent().parent().find("td[name='user_name']").text();
// 	var roleID = $(obj).parent().parent().find("input[name='role_id']").val();

// 	$("#useredit").show().siblings().hide();

// 	$("#useredit").find("input[name='user_id']").attr('Value', user_id);
// 	$("#useredit").find("input[name='user_name']").attr('Value', user_name);
// 	$("#useredit").find("input[name='roleID']").attr('Value', roleID);
// 	$("#useredit").find("option[value='" + roleID+ "']").attr("selected", "selected");
// 	if(parseInt(roleID) == 1){
// 		$("#EditRolePriv-1").show();
// 		$("#EditRolePriv-2").hide();
// 	}else if(parseInt(roleID) == 2){
// 		$("#EditRolePriv-2").show();
// 		$("#EditRolePriv-1").hide();
// 	}
// }

// function OnSaveAddUser(obj){
// 	var flag = true;
// 	$(".functionWarn").each(function(){
// 		if($(this).is(":visible")){
// 			flag = false;
// 		}
// 	})

// 	var name = $(obj).find(".name").val();
// 	if(isSpace(name)){
// 		flag = false;
// 	}
// 	var password = $(obj).find(".password").val();
// 	if(isSpace(password)){
// 		flag = false;
// 	}
// 	var repeatpwd = $(obj).find(".repeatpwd").val();
// 	if(isSpace(repeatpwd)){
// 		flag = false;
// 	}

// 	if(flag){
// 		$(obj).parent().find(".formWarn").hide();
// 	}else{
// 		$(obj).parent().find(".formWarn").show();
// 	}

// 	//MD5
// 	var password = $(obj).find(".password").val();
// 	var md5pw = $.md5(password);
// 	$(obj).find("input[name='password']").val(md5pw);

// 	return flag;
// }

// function OnSaveEditUser(obj){
// 	var flag = true;
// 	$(".functionWarn").each(function(){
// 		if($(this).is(":visible")){
// 			flag = false;
// 		}
// 	})

// 	var name = $(obj).find(".user_name").val();
// 	if(isSpace(name)){
// 		flag = false;
// 	}
// 	var oldpassword = $(obj).find(".oldpassword").val();
// 	if(isSpace(oldpassword)){
// 		flag = false;
// 	}
// 	var newpassword = $(obj).find(".newpassword").val();
// 	if(isSpace(newpassword)){
// 		flag = false;
// 	}
// 	var repeatpwd = $(obj).find(".repeatpwd").val();
// 	if(isSpace(repeatpwd)){
// 		flag = false;
// 	}

// 	if(flag){
// 		$(obj).parent().find(".formWarn").hide();
// 	}else{
// 		$(obj).parent().find(".formWarn").show();
// 	}

// 	//MD5
// 	var password = $(obj).find(".newpassword").val();
// 	var md5pw = $.md5(password);
// 	$(obj).find("input[name='password']").val(md5pw);

// 	return flag;
// }

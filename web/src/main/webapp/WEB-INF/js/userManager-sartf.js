Backbone.Marionette.Renderer.render = function(template, data){
    if (!template) {
        throw new Marionette.Error({
          name: 'TemplateNotFoundError',
          message: 'Cannot render the template since its false, null or undefined.'
        });
    } 
    var renderer = Handlebars.compile($("#" + template).html());
    return renderer(data);
};

var OpsList = (function($, _, Backbone, Marionette, state, sv) {

    var OpsModel = Backbone.Model.extend({
        defaults: {
            'nameFilter': '',
            'usedFilter': false
        },
        shouldBeShown: function() {
            if(this.get('usedFilter') && this.get('userId') != -1) {
                return false;
            }
            if(this.get('nameFilter') != '' && this.get('name').indexOf(this.get('nameFilter')) == -1) {
                return false;
            }
            return true;
        }
    });

    var OpsCollection = Backbone.Collection.extend({
        url : function () {
          return sv.urlPath.getRealPath('/user/ops');
        },
        model: OpsModel
    });

    var OpsListItemView = Marionette.ItemView.extend({
        template: 'opsItemTemplate',
        attributes: {
            class: 'ops-item'
        },
        ui: {
            select: 'input[name="ops-item-select"]'
        },
        events: {
            'click @ui.select': 'onCheckboxClicked',
            'click .ops-item': 'onItemClicked'
        },
        modelEvents: {
            "change nameFilter": "filter",
            "change usedFilter": "filter"
        },
        onCheckboxClicked: function(event) {
            var selected = $(event.currentTarget).prop("checked");
            if(selected) {
                this.triggerMethod("opsitem:selected");
            } else {
                this.triggerMethod("opsitem:unselected");
            }
            event.stopPropagation();
        },
        onItemClicked: function(event) {
            if(this.model.get('userId') != -1) {
                return;
            }
            var selected = this.ui.select.prop("checked");
            if(selected) {
                this.ui.select.prop("checked", false);
                this.triggerMethod("opsitem:unselected");
            } else {
                this.ui.select.prop("checked", true);
                this.triggerMethod("opsitem:selected");
            }
        },
        unselected: function() {
            this.ui.select.prop("checked", false);
        },
        filter: function(model) {
            if(model.shouldBeShown()) {
                this.$el.show();
            } else {
                this.$el.hide();
            }
        }
    })

    var OpsListView = Marionette.CompositeView.extend({
        childView: OpsListItemView,
        childViewContainer: '#opslist-container',
        template: 'opsListTemplate',
        el: '#opslist',
        ui: {
            bind: '#opslist-bind-btn',
            cancel: '#opslist-cancel-btn'
        },
        attributes: {
            class: 'modal',
            id: 'opslist-modal'
        },
        events: {
            'click @ui.bind': 'bindOps',
            'click @ui.cancel': 'closeModal',
            'click #opsList-used-filter': 'filterUsed',
            'keyup #opsList-name-filter': 'filterName'
        },
        childEvents: {
            'opsitem:selected': function(view) {
                this.children.each(function(child) {
                    if(view != child) {
                        child.unselected();
                    }
                });
                this.selectedItem = view.model;
            },
            'opsitem:unselected': function(view) {
                this.selectedItem = null;
            }
        },
        openModal: function(user_id) {
            this.userId = user_id;
            this.clearSelection();
            var _this = this;
            this.collection.fetch({
                success: function(collection, response, options) {
                    _this.collection.invoke('set', 'nameFilter', '');
                    _this.collection.invoke('set', 'usedFilter', false);
                      _this.$el.modal({
                        showClose: false,
                        clickClose: false
                      })
                  }
            });
        },
        closeModal: function() {
            $.modal.close();
        },
        clearSelection: function() {
            this.children.each(function(child) {
                child.unselected();
            });
        },
        bindOps: function() {
            var _this = this;
            $.post("bindOps",{
                        "userId":this.userId,
                        "opsId":this.selectedItem.get('id')}
                ).fail(function(jqXhr){
                    Backbone.trigger('bindOps:fail');
                }).done(function() {
                    _this.selectedItem.set('userId', _this.userId);
                }).always(function(){                
                    Backbone.trigger('bindOps:finish');
                });
            $.modal.close();
        },
        filterName: function() {
            var nameFilter = $('#opsList-name-filter').val() || '';          
            this.collection.invoke('set', 'nameFilter', nameFilter);
            
        },
        filterUsed: function() {
            var usedFilter = $('#opsList-used-filter').prop("checked");
            this.collection.invoke('set', 'usedFilter', usedFilter);
        },
        initialize: function() {
            this.selectedItem = null;
        }
    });

    var OpsList = function() {
        var models = state.opsList || [];
        this.collection = new OpsCollection(models);
    };

    OpsList.prototype = {
        bind: function(user_id) {
            if(!this.view) {
                this.view = new OpsListView({collection: this.collection});              
            }
            this.view.render();
            this.view.openModal(user_id);
        },
        unbind: function(user_id) {
            this.userId = +user_id;
            var _this = this;
            $.post("unbindOps",{
                        "userId":this.userId
                    }
                ).fail(function(jqXhr){
                    Backbone.trigger('unbindOps:fail');
                }).done(function() {
                    var model = _this.collection.find(function(model) {
                        return model.get('userId') == _this.userId;
                    });
                    if(model) {
                        model.set('userId', -1);
                    }
                }).always(function(){
                    
                    Backbone.trigger('unbindOps:finish');
                });
        }
    }

    return OpsList;
}(jQuery, _, Backbone, Marionette, state, window.sv));

function UserManager(opsList) {
    this.opsList = opsList
    _.extend(this, Backbone.Events);
}

UserManager.prototype = {
	init: function() {
		var _this = this;
        sv.nav.active('tb6');
        this.reloadUsers();
        $(".select-all").click(function () {
            $(".select-one").prop("checked", this.checked);
            _this.updateActionBar();
        })
        $("#user-list-table").on('click', ".select-one", function () {
            _this.updateActionBar();
        }).on('click', ".bindops", function() {
            var user_id = $(this).closest("tr").find("input[name='user-id']").val();
            _this.opsList.bind(user_id);
        }).on('click', ".unbindops", function() {
            var user_id = $(this).closest("tr").find("input[name='user-id']").val();
            _this.opsList.unbind(user_id);
        });

        $("#add-user-btn").click(function() {
            $("#useradd .dialog-content").render("addUserTemplate");
            $("#useradd").modal({
                    showClose: false, clickClose:false
                });
        });

        $("#change-password-btn").click(function() {
            $("#changepwd .dialog-content").render("editUserPswdTemplate");
            $("#changepwd").modal({
                showClose: false, clickClose: false
            });
        })

        $("#changepwd").on("click", "#edit-user-cancel-btn", $.proxy(function() {
            $.modal.close();
        }, this)).on("click", "#edit-user-ok-btn", $.proxy(function() {
            _this.changepwd();
        }))

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
                   $("#changepwd .dialog-content").render("editUserPswdTemplate"); 	
                    $("#changepwd").modal({
                        showClose: false, clickClose:false
                    });
                }
            }
        }, this)); 

        $("#delete-user-btn").click(function () {
            if(!($("#delete-user-btn").parent().hasClass("disable"))) {
                sv.prompt.showConfirm({message: '是否删除所选记录？', okFunc: function () {
                    _this.deleteUser();
                }});
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

        this.listenTo(Backbone, 'bindOps:fail', this.bindOpsFailed);
        this.listenTo(Backbone, 'bindOps:finish', this.bindOpsFinished);
        this.listenTo(Backbone, 'unbindOps:fail', this.unbindOpsFailed);
        this.listenTo(Backbone, 'unbindOps:finish', this.unbindOpsFinished);
	},

    bindOpsFailed: function() {

    },

    bindOpsFinished: function() {
        this.reloadUsers();
    },

    unbindOpsFailed: function() {

    },

    unbindOpsFinished: function() {
        this.reloadUsers();
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
            $("#user-list-table > tbody").render("userTemplate", {"users": users});
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
            	sv.prompt.error({message: '无法删除当前登录账号！'});
            }
        }
    },
    editUser: function () {

    	
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
    },
    changepwd: function () {
            var editUserPswdFrom = $("#edit-user-pswd-form");
            var validator = editUserPswdFrom.validate();
            if (editUserPswdFrom.valid()) {
                $("#edit-user-pswd-form input[name='id']").val($("#currentUserId").val());
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
                            $("#edit-user-old-password").val("");
                            $("#password").val("");
                            $("#edit-user-new-password").val("");
                            $("#newPassword").val("");
                            $("#edit-repeatpwd").val("");
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
}

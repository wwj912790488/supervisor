(function(root, factory){
    jQuery(function () {
        factory(jQuery, _, root.sv, Backbone, Marionette);
    });
}(this, function($, _, sv, Backbone, Marionette) {

    var LoginService = Marionette.Object.extend({
        initialize : function () {
            sv.channel.reqres.setHandlers({
                'login:login' : _.bind(this.login, this),
                'login:register' : _.bind(this.register, this),
                'login:isSuccess' : _.bind(this.isSuccess, this)
            });
        },

        login : function (data) {
            sv.ajax.post("/login/sign_in", data, function (json) {
                sv.channel.trigger('login:result', json);
            });
        },

        register : function (data) {
            sv.ajax.post("/login/register", data, function (json) {
                sv.channel.trigger('login:register-result', json);
            });
        },

        isSuccess : function (responseJson) {
            return responseJson.code === 0;
        }
    });

    var LoginView = Marionette.ItemView.extend({
        initialize : function () {
            this.loginValidator = undefined;
            this.registerValidator = undefined;
            sv.channel.vent.on('login:result', _.bind(this.onLogin, this));
            sv.channel.vent.on('login:register-result', _.bind(this.onRegisterResult, this));
        },
        template : false,
        el : '.container',
        ui : {
            userName : '#userName',
            password : '#pwd',
            actualPassword : '#password',
            registerUserName : '#registerUserName',
            registerPassword : '#registerPassword',
            registerPasswordMd5 : '#registerPasswordMd5',
            repeatPassword : '#repeatpwd',
            loginForm : '#loginfrm',
            registerForm : '#registerform',
            loginBtn : '#login-btn',
            registerBtn : '#register-btn',
            registerOkBtn : '#register-ok-btn',
            registerCancelBtn : '#register-cancel-btn',
            loginDialog : '#login-dialog',
            registerDialog : '#register-dialog'
        },

        events : {
            'click @ui.loginBtn' : 'login',
            'keypress @ui.loginBtn' : 'keyEventDoLogin',
            'keydown @ui.userName' : 'keyEventDoLogin',
            'keydown @ui.password' : 'keyEventDoLogin',
            'keypress @ui.registerBtn' : 'doBeforeRegister',
            'click @ui.registerBtn' : 'doBeforeRegister',
            'click @ui.registerOkBtn' : 'register',
            'keydown @ui.registerUserName' : 'keyEventDoRegister',
            'keydown @ui.registerPassword' : 'keyEventDoRegister',
            'keydown @ui.repeatPassword' : 'keyEventDoRegister',
            'keypress @ui.registerOkBtn' : 'keyEventDoRegister',
            'click @ui.registerCancelBtn' : 'doCancelRegister',
            'keypress @ui.registerCancelBtn' : 'doCancelRegister'

        },

        keyEventDoLogin : function(evt){
        	if(this.isEnterKeyDown(evt.which)){
        		this.login();
        	}
        },

        keyEventDoRegister : function(evt){
        	if(this.isEnterKeyDown(evt.which)){
        		this.register();
        	}
        },

        isEnterKeyDown : function(keyCode) {
            return keyCode == 13;
        },

        doBeforeRegister : function(){
	        $.clearValidateError(this.ui.loginForm);
	        var childrens = this.ui.loginDialog.children();
	        this._setTabIndex(childrens, '-1');
	        childrens.end().hide();
	        this._clearValueAndSetTabIndex(this.ui.registerUserName, '1')
	        	._clearValueAndSetTabIndex(this.ui.registerPassword, '2')
	        	._clearValueAndSetTabIndex(this.ui.repeatPassword, '3')
	        	._setTabIndex(this.ui.registerOkBtn, '4')
	        	._setTabIndex(this.ui.registerCancelBtn, '5');
	        this.ui.registerDialog.show();
        },

        doCancelRegister : function(){
        	$.clearValidateError(this.ui.registerForm);
        	var childrens = this.ui.registerDialog.children();
        	this._setTabIndex(childrens, '-1');
        	childrens.end().hide();
        	this._clearValueAndSetTabIndex(this.ui.userName, '1')
        		._clearValueAndSetTabIndex(this.ui.password, '2')
        		._setTabIndex(this.ui.loginBtn, '3')
        		._setTabIndex(this.ui.registerBtn, '4');
        	this.ui.loginDialog.show();
        },

        onRegisterResult : function (result) {
            if(!sv.channel.request('login:isSuccess', result)) {
                this.registerValidator.showErrors({
                    "registerUserName" : "该用户已存在"
                });
            } else {
                this._redirectTo();
            }
        },

        onLogin : function (result) {
            if(!sv.channel.request('login:isSuccess', result)) {
                this.loginValidator.showErrors({
                    "userName" : "用户名或密码错误"
                });
            } else {
                this._redirectTo();
            }
        },

        onRender : function () {
            this._clearValueAndSetTabIndex(this.ui.userName, '1')
				._clearValueAndSetTabIndex(this.ui.password, '2')
            	._clearValueAndSetTabIndex(this.ui.registerUserName, '-1')
            	._clearValueAndSetTabIndex(this.ui.registerPassword, '-1')
            	._clearValueAndSetTabIndex(this.ui.repeatPassword, '-1')
            	._setTabIndex(this.ui.loginBtn, '3')
            	._setTabIndex(this.ui.registerBtn, '4')
            	._setTabIndex(this.ui.registerOkBtn, '-1')
            	._setTabIndex(this.ui.registerCancelBtn, '-1');
            this.ui.userName.focus();
            this.loginValidator = this.ui.loginForm.validate();
            this.registerValidator = this.ui.registerForm.validate({
                rules: {
                    registerUserName: {
                        chkchrnum: true
                    }
                }
            });
        },

        login : function () {
            if (this.ui.loginForm.valid()) {
                var md5OfPwd = $.md5(this.ui.password.val());
                this.ui.actualPassword.val(md5OfPwd);
                var data = this.ui.loginForm.serializeArray();
                sv.channel.request('login:login', data);
            }
        },

        register : function () {
            if (this.ui.registerForm.valid()) {
                var md5pw = $.md5(this.ui.registerPassword.val());
                this.ui.registerPasswordMd5.val(md5pw);
                var data = [{"name": "userName", "value": $("#registerUserName").val()}, {"name":"password", "value": $("#registerPasswordMd5").val()}];
                sv.channel.request('login:register', data);
            }
        },

        _redirectTo : function () {
            location.href = sv.urlPath.getRealPath('/');
        },

        _clearValueAndSetTabIndex : function (ui, index) {
          this._clearValue(ui)._setTabIndex(ui, index);
          return this;
        },

        _clearValue : function (ui) {
            ui.val('');
            return this;
        },

        _setTabIndex : function (ui, index) {
            ui.prop('tabindex', index);
            return this;
        }
    });

	new LoginService();
    var loginView = new LoginView();
    loginView.render();
}));

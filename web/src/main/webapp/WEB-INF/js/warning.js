$(function ($, _, Backbone, Marionette, sv) {

    var messages = {
        90: '短信网关地址无法访问,请检查',
        91: '设备ip网络不可达,请检查网络'
    };

    // Message resource object
    var m = new sv.Message({
        message: messages
    });

    // Define loading modal handler
    var loading = new (Marionette.Object.extend({
        show: function (message) {
            message || (message = m.get('loading'));
            sv.loading.show(message, {
                t: 'opLoadingTemplate'
            });
        }
    }));


    // Extended function for sv.ajax.xhrHandler.
    // The function add an default always callback for switch back
    // the modal dialog.
    var xhrHandler = function (xhr, options) {
        loading.show();
        var _options = {
            always: function () {
                sv.loading.close();
            },
            done: function () {
                sv.prompt.succeed2(m.success());
                sv.channel.vent.trigger("xhr:success");
            },
            fail: function () {

            },
            message: m
        };
        _.extend(_options, options);
        return sv.ajax.xhrHandler(xhr, _options);
    };

    // Warning service for communicate with server
    // --------------
    var WarningService = Marionette.Object.extend({
        initialize: function () {
            sv.channel.reqres.setHandlers({
                'warning:sms-save': _.bind(this.saveSms, this),
                'warning:push-save': _.bind(this.savePush, this),
                'warning:report-save': _.bind(this.saveReport, this),
                'warning:email-save': _.bind(this.saveEmail, this),
            });
        },

        saveSms: function (data) {
            xhrHandler(sv.ajax.postJson('/warning/saveSmsCfg', data));
        },

        savePush: function (data) {
            xhrHandler(sv.ajax.postJson('/warning/saveWarningPushCfg', data));
        },
        saveReport: function (data) {
            xhrHandler(sv.ajax.postJson('/warning/saveReportCfg', data));
        },
        saveEmail: function (data) {
            xhrHandler(sv.ajax.postJson('/warning/saveEmailCfg', data));
        }

    });
    new WarningService();

    // Base view for extend
    // -------
    var BaseWarningView = Marionette.ItemView.extend({
        template: false,
        el: '.warning-content',
        ui: {
            'saveBtn': '#save-btn',
            'form': '.form',
            firstPEl: 'p:eq(0)'
        },
        events: {
            'click @ui.saveBtn': 'doSave'
        },

        onRender: function () {
            sv.nav.active('tb8');
        }
    });



    // Sms view
    // -------
    var SmsView = BaseWarningView.extend({

        initialize: function () {
            sv.channel.vent.on('xhr:success', _.bind(this.onSaveSuccess, this));
        },

        ui: _.extend(BaseWarningView.prototype.ui, {
            'url': '#url',
            'phoneNum': '#phoneNumber',
            'account': '#account'
        }),

        constants: {
            urlRemark: '#url-remark',
            urlRemarkHtml: $.parseHTML("<span id='url-remark' class='input-remark'>清空短信网关地址并保存即可关闭短信告警</span>")
        },

        isUrlExist: function () {
            return this.ui.url.length > 0;
        },

        doSave: function () {
            if (this.ui.form.valid()) {
                var _data = this.ui.form.serializeObject();
                var data = {
                    smsCfg: {
                        url: _data.url,
                        account: _data.account
                    },
                    phoneNumber: _data.phoneNumber
                };
                sv.channel.request('warning:sms-save', data);
            }
        },

        onSaveSuccess: function () {
            this.dynamicUrlRemark();
        },

        onRender: function () {
            BaseWarningView.prototype.onRender.call(this);
            this.initializeFormValidator();
            this.dynamicUrlRemark();
        },

        dynamicUrlRemark: function () {
            var urlRemark = this.ui.form.find(this.constants.urlRemark);
            if (this.isUrlExist() && this.ui.url.val()) {
                if (urlRemark.length == 0) {
                    $(this.constants.urlRemarkHtml).insertAfter(this.ui.firstPEl);
                }
            } else {
                this.ui.form.find(this.constants.urlRemark).remove();
            }
        },

        initializeFormValidator: function () {
            var that = this;
            var rules = {
                'phoneNumber': {
                    validPhoneNum: {
                        depends: function (el) {
                            return $(el).val();
                        }
                    }
                }
            };
            if (this.isUrlExist()) {
                rules.url = {
                    validHTTPUrl: {
                        depends: function (el) {
                            return $(el).val();
                        }
                    }
                };
                rules.account = {
                    required: {
                        depends: function () {
                            return that.ui.url.val();
                        }
                    }

                }
            }
            this.ui.form.validate({
                rules: rules
            });
        }

    });

    jQuery.validator.addMethod('myvalidIpPort', function (value) {
        var ipport = /^(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5])):(\d+)$/;
        var ip = /^(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))$/;
        return value.match(ip)||value.match(ipport);
    }, '请输入正确的ip或ip:port');

    jQuery.validator.addMethod('myvalidHTTP', function (value) {
        var ipport =/^(http|https|ftp)\:\/\/([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.[a-zA-Z]{2,4})(\:[0-9]+)?(\/[^/][a-zA-Z0-9\.\,\?\'\\/\+&amp;%\$#\=~_\-@]*)*$/;
        return value.match(ipport);
    }, '请输入正确的ip或ip:port');

    jQuery.validator.addMethod('myvalidEmail', function (value) {
        var ipport =/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
        return value.match(ipport);
    }, '请输入正确的邮箱');

    //ReportView
    var ReportView = BaseWarningView.extend({

        initialize: function () {
            sv.channel.vent.on('xhr:success', _.bind(this.onSaveSuccess, this));
        },

        constants: {
            'ipRemark': '#ip-remark',
            'ipRemarkHtml': $.parseHTML("<span id='ip-remark' class='input-remark'>清空设备ip并保存即可关闭上传告警</span>")
        },

        ui: _.extend(BaseWarningView.prototype.ui, {
            ip: '#ip'
        }),

        doSave: function () {
            if (this.ui.form.valid()) {
                sv.channel.request('warning:report-save', this.ui.form.serializeObject());
            }
        },

        onSaveSuccess: function () {
            this.dynamicIpRemark();
        },

        onRender: function () {
            BaseWarningView.prototype.onRender.call(this);
            this.initializeFormValidator();
            this.dynamicIpRemark();
        },

        dynamicIpRemark: function () {
            var ipRemark = this.ui.form.find(this.constants.ipRemark);
            if (this.ui.ip.val()) {
                if (ipRemark.length == 0) {
                    $(this.constants.ipRemarkHtml).insertAfter(this.ui.firstPEl);
                }
            } else {
                this.ui.form.find(this.constants.ipRemark).remove();
            }
        },
        initializeFormValidator: function () {
            this.ui.form.validate({
                rules: {
                    'ip': {
                        myvalidHTTP: {
                            depends: function (el) {
                                return $(el).val();
                            }
                        }
                    }
                }
            });
        }

    });

    // Email View
    var EmailView = BaseWarningView.extend({

        initialize: function () {
            sv.channel.vent.on('xhr:success', _.bind(this.onSaveSuccess, this));
        },

        constants: {
            'ipRemark': '#ip-remark',
            'ipRemarkHtml': $.parseHTML("<span id='ip-remark' class='input-remark'>清空设备ip并保存即可关闭声音告警</span>")
        },

        ui: _.extend(BaseWarningView.prototype.ui, {
            userName: '#userName'
        }),

        doSave: function () {
            if (this.ui.form.valid()) {
                sv.channel.request('warning:email-save', this.ui.form.serializeObject());
            }
        },

        onSaveSuccess: function () {
            this.dynamicIpRemark();
        },

        onRender: function () {
            BaseWarningView.prototype.onRender.call(this);
            this.initializeFormValidator();
            this.dynamicIpRemark();
        },

        dynamicIpRemark: function () {
            var ipRemark = this.ui.form.find(this.constants.ipRemark);
            if (this.ui.userName.val()) {
                if (ipRemark.length == 0) {
                    $(this.constants.ipRemarkHtml).insertAfter(this.ui.firstPEl);
                }
            } else {
                this.ui.form.find(this.constants.ipRemark).remove();
            }
        },

        initializeFormValidator: function () {
            this.ui.form.validate({
                rules: {
                    'userName': {
                        myvalidEmail: {
                            depends: function (el) {
                                return $(el).val();
                            }
                        }
                    }
                }
            });
        }
    });

    // Push view
    // -------
    var PushView = BaseWarningView.extend({

        initialize: function () {
            sv.channel.vent.on('xhr:success', _.bind(this.onSaveSuccess, this));
        },

        constants: {
            'ipRemark': '#ip-remark',
            'ipRemarkHtml': $.parseHTML("<span id='ip-remark' class='input-remark'>清空设备ip并保存即可关闭声音告警</span>")
        },
        doSave: function () {
            if (this.ui.form.valid()) {
                sv.channel.request('warning:push-save', this.ui.form.serializeObject());
            }
        },

        onSaveSuccess: function () {
            this.dynamicIpRemark();
        },

        onRender: function () {
            BaseWarningView.prototype.onRender.call(this);
            this.initializeFormValidator();
            this.dynamicIpRemark();

        },

        dynamicIpRemark: function () {
            var ipRemark = this.ui.form.find(this.constants.ipRemark);
            if (this.ui.ip.val()) {
                if (ipRemark.length == 0) {
                    $(this.constants.ipRemarkHtml).insertAfter(this.ui.firstPEl);
                }
            } else {
                this.ui.form.find(this.constants.ipRemark).remove();
            }
        },

        initializeFormValidator: function () {
            this.ui.form.validate({
                rules: {
                    'ip': {
                        myvalidIpPort: {
                            depends: function (el) {
                                return $(el).val();
                            }
                        }
                    }
                }
            });
        }
    });

    sv.SmsView = SmsView;
    sv.PushView = PushView;
    sv.ReportView = ReportView;
    sv.EmailView = EmailView;

}(jQuery, _, Backbone, Marionette, window.sv));

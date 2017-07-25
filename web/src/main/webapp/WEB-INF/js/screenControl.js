$(function ($, _, Backbone, Marionette, sv) {

    /*  var messages = {
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
     }));*/


    /* // Extended function for sv.ajax.xhrHandler.
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
     */
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
            sv.nav.active('tb1');
        }
    });


    //screen
    var screen = BaseWarningView.extend({
        initialize: function () {
            var _this = this;
            this.reloadOPSs();
            $("#screen-btn-start").click(function () {
                if (!$(this).parent().hasClass("disable")) {
                    sv.prompt.showConfirm3("是否启动所选屏幕？", function () {
                        _this.startstopScreen("start");
                    });
                }
            });


            $("#ops-list-table").on('click', ".select-one", function () {
                _this.updateActionBar();
            });

            $("#screen-btn-stop").click(function () {
                if (!($("#screen-btn-stop").parent().hasClass("disable"))) {
                    sv.prompt.showConfirm2('是否停止所选屏幕？', function () {
                        // _this.deleteOps();
                        _this.startstopScreen("stop");
                    });
                }
            });

            _this.updateActionBar();

        },
        updateActionBar: function () {
            $(".select-all").prop("checked",
                $(".select-one[type='checkbox']:checked").length > 0 && $(".select-one[type='checkbox']:checked").length === $(".select-one[type='checkbox']").length);

            if ($(".select-one[type='checkbox']:checked").length > 1) {
                $("#screen-btn-stop").parent().addClass("disable");
            } else if ($(".select-one[type='checkbox']:checked").length == 1) {
                $("#screen-btn-stop").parent().removeClass("disable");
            } else {
                $("#screen-btn-stop").parent().addClass("disable");
            }
        },

        startstopScreen: function (url) {
            var id = this.getCheckedIdsArr2();
            if (id.length > 0) {
                $.ajax({
                    url: "/task/screen/control/" + url,
                    type: 'POST',
                    data: {screenId: id},
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == 0) {
                            //alert("设置成功!");
                            window.location.reload();
                        } else {
                            alert("设置失败!");
                        }
                    }

                })
                /* this.doWithChecked(url, {channelsStr: $.toJSON(ids)}, true);*/
            }
        },

        getCheckedIdsArr2: function () {
            var checkedChannels = $("input[name='ops-id']:checked");
            /* var params = [];
             if (checkedChannels.length > 0) {
             for (var i = 0, len = checkedChannels.length; i < len; i++) {
             params[i] = $(checkedChannels.get(i)).val();
             }
             return params;
             }*/
            return checkedChannels.val();
        },

        reloadOPSs: function () {
            var _this = this;
            $.getJSON("/screen/allScreen?r=" + Math.random(), function (opss) {
                $("#ops-list-table > tbody").render("opsTemplate", {"opss": opss});
                 _this.updateActionBar();

            });
        },

        constants: {
            'ipRemark': '#ip-remark',
            'ipRemarkHtml': $.parseHTML("<span id='ip-remark' class='input-remark'>清空设备ip并保存即可关闭声音告警</span>")
        },

        ui: _.extend(BaseWarningView.prototype.ui, {
            ip: '#ip'
        }),

        doSave: function () {
            if (this.ui.form.valid()) {
                sv.channel.request('warning:push-save', this.ui.form.serializeObject());
            }
        },

        onSaveSuccess: function () {
            this.dynamicIpRemark();
        },

        onRender: function () {
            sv.nav.active("tb1");
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

    sv.screen = screen;

}(jQuery, _, Backbone, Marionette, window.sv));

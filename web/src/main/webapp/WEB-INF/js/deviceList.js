var Device = (function ($, sv, Backbone, Marionette, _, Chart) {


    var messages = {
        error: "操作处理失败",
        host: {
            'savePrompt': '确认系统初始化操作？',
            'saveSuccess': '系统初始化成功',
            'restartPrompt': '确认重启当前服务器？',
            'shutdownPrompt': '确认关闭当前服务器？'
        },
        eth: {
            'savePrompt': '修改当前网卡设置后可能导致无法使用当前的url地址访问系统，确认修改吗？'
        },
        dns: {
            savePrompt: '确认添加DNS？'
        },
        route: {
            savePrompt: '确认添加路由？'
        },
        storage: {
            editPrompt: '请先卸载当前存储'
        },
        100: function () {
            return this.error;
        },
        4001: '存储名称已存在，请修改名称',
        5001: '操作处理失败，请检查参数是否正确',
        6001: '当前服务器不可用',
        6002: '远程服务器处理失败',
        6003: '集群系统没有初始化，请先进行初始化操作'
    };

    // Message resource object
    var m = new sv.Message({
        message: messages
    });

    // Override selectOne method
    sv.Behaviors.TableActionbarToggler = sv.Behaviors.TableActionbarToggler.extend({
        doSelectOne: function () {
            var selected = this.ui.selectOne.filter(':checked');
            if (selected.val() != -1) {
                this.ui.editBtn.parent().removeClass('disable');
                this.ui.configBtn.parent().removeClass('disable');
                this.ui.deleteBtn.parent().removeClass('disable');
                this.ui.showTasksBtn.parent().removeClass('disable');
                this.ui.showInstallBtn.parent().removeClass('disable');
            } else {
                this.ui.configBtn.parent().removeClass('disable');
            }
        }
    });

    //Override Backbone.sync to add custom destroy logically.
    //Support submit data as json with DELETE method; auto handle context path for url.
    var oldBackboneSync = Backbone.sync;
    Backbone.sync = function (method, model, options) {
        // delete request WITH data
        if (method === 'delete' && options && options.data) {
            options.data = JSON.stringify(options.data);
            options.contentType = 'application/json';
        } // else, business as usual.
        if (model.url || (options && options.url)) {
            var url = _.isEmpty(options.url) ? (_.isFunction(model.url) ? model.url() : model.url) : options.url;
            options.url = sv.urlPath.getContextPath() + (url[0] == '/' ? url.substr(1) : url);
        }
        if (method === 'read' || method === 'get') {
            var re = /\?[^=]*=/;
            if (re.exec(options.url) !== null) {
                options.url += ('&r=' + Math.random());
            } else {
                options.url += ('?r=' + Math.random());
            }
        }
        return oldBackboneSync.apply(this, [method, model, options]);
    };

    // Define loading modal handler
    var loading = new (Marionette.Object.extend({

        show: function (message) {
            message || (message = m.get('loading'));
            sv.loading.show(message, {
                t: 'opLoadingTemplate'
            });
        },

        switchBack: function () {
            $("#config-device-dialog").modal(sv.modalOptions.showClose);
        }
    }));


    // Extended function for sv.ajax.xhrHandler.
    // The function add an default always callback for switch back
    // the modal dialog.
    var xhrHandler = function (xhr, options) {
        loading.show();
        var _options = {
            always: function () {
                loading.switchBack();
            },
            message: m
        };
        _.extend(_options, options);
        return sv.ajax.xhrHandler(xhr, _options);
    };


    var deviceService = new (Marionette.Object.extend({
        initialize: function () {
            this.deviceId = -1;
            sv.channel.reqres.setHandlers({
                'device-config:getNavOptions': _.bind(this.getDeviceConfigModalNavOptions, this),
                'device:delete': _.bind(this.deleteDevice, this),
                'device:getOrScanDevice': _.bind(this.doGetOrScanDevice, this),
                'device:getDeviceByIds': _.bind(this.getDeviceByIds, this),
                'device:saveOrUpdate': _.bind(this.saveOrUpdateDevice, this),
                'device:syncCheckName': _.bind(this.syncCheckName, this),
                'device:join': _.bind(this.join, this),
                'device:setId': _.bind(this.setDeviceId, this),
                'device:getId': _.bind(this.getDeviceId, this),
                'device:showTasks': _.bind(this.showTasks, this)
            });
        },

        getDeviceConfigModalNavOptions: function (id) {
            var childNavOptions = {
                host: {
                    templateId: "nav-child-host-template",
                    contentViewOptions: {
                        'host-operate': {
                            view: sv.view.HostOperateView
                        },
                        'host-init': {
                            view: sv.view.HostInitializeView,
                            hide: id != -1
                        }
                    }
                },
                network: {
                    templateId: "nav-child-network-template",
                    contentViewOptions: {
                        'network-eth': {
                            view: sv.view.EthernetView
                        },
                        'network-dns': {
                            view: sv.view.DnsLayoutView
                        },
                        'network-route': {
                            view: sv.view.RouteContainerView
                        }
                    }
                },
                storage: {
                    contentViewOptions: {
                        view: sv.view.StorageContainerView,
                        hasChildNav: false
                    }
                },
                config: {
                    hide: true
                }
            };
            if (id == -1) {
                childNavOptions.config = {
                    templateId: 'nav-child-config-template',
                    contentViewOptions: {
                        'config-rtsp': {
                            view: sv.view.RtspConfigView
                        },
                        'config-gpu': {
                            view: sv.view.GpuConfigView
                        },
                        'config-record': {
                            view: sv.view.RecordConfigView
                        }
                    }
                }
            }
            return childNavOptions;
        },

        showTasks: function (id) {
            sv.ajax.xhrHandler(sv.ajax.get('/device/tasks/' + id), {
                done: function (json) {
                    var channelNames = json.r || [];
                    sv.channel.trigger('device:showTasks:done', {
                        channelNames: channelNames,
                        count: channelNames.length
                    });
                },
                fail: function () {
                    sv.channel.trigger('device:showTasks:fail');
                }
            });
        },

        deleteDevice: function (idsJson) {
            if (idsJson.length > 0) {
                sv.ajax.post('/device/delete', {
                    json: $.toJSON(idsJson)
                }, function () {
                    sv.channel.vent.trigger('reload:location');
                });
            }
        },

        doGetOrScanDevice: function (id) {
            var url = '/device/' + (id ? 'get' : 'scan'),
                data = id ? {id: id} : {};
            sv.ajax.xhrHandler(sv.ajax.getJSON(url, data), {
                done: function (json) {
                    var templateModel = id ? json : {
                            deviceList: json
                        },
                        templateId = id ? 'updateDeviceTemplate' : 'addDeviceTemplate',
                        deviceName = id ? json['name'] : '';
                    var deviceViewOptions = {
                        data: templateModel,
                        templateId: templateId,
                        deviceName: deviceName,
                        id: id
                    };
                    sv.channel.trigger('device:getAndScan:done', deviceViewOptions);
                },
                fail: function () {
                    sv.channel.trigger('device:getAndScan:fail');
                }
            });
        },

        getDeviceByIds: function (ids) {
            if (ids.length > 0) {
                sv.ajax.getJSON("/device/gets", {
                    "ids": ids.join(",")
                }, function (data) {
                    sv.channel.trigger('device:getItems', data);
                });
            }
        },

        saveOrUpdateDevice: function (params) {
            sv.ajax.post('/device/update', params, function () {
                sv.channel.trigger('reload:location');
            });
        },

        syncCheckName: function (name, func) {
            $.ajax({
                async: false,
                url: 'check',
                data: {
                    name: name
                },
                type: 'get',
                success: function (json) {
                    if (json && json['exists']) {
                        func();
                    }
                }
            });
        },

        join: function (params) {
            sv.ajax.post('/device/join', {
                deviceJson: $.toJSON(params)
            }, function () {
                sv.channel.vent.trigger('reload:location');
            });
        },

        setDeviceId: function (id) {
            this.deviceId = id;
        },

        getDeviceId: function () {
            return this.deviceId;
        }
    }));

    // The main view of device
    var DeviceLayoutView = Marionette.LayoutView.extend({
        initialize: function () {
            sv.channel.vent.on('device:getAndScan:done', _.bind(this.onDeviceGetAndScanDone, this));
            sv.channel.vent.on('device:getAndScan:fail', _.bind(this.onDeviceGetAndScanFail, this));
            sv.channel.vent.on('device:showTasks:done', _.bind(this.onDeviceTasksShowDone, this));
            sv.channel.vent.on('device:showTasks:fail', _.bind(this.onDeviceTasksShowFail, this));
        },
        template: false,
        el: 'body',
        ui: {
            configDeviceDialog: '#config-device-dialog'
        },
        events: {
            'click @ui.configDeviceDialog .close-modal': 'destroyConfigDevice'
        },
        regions: {
            addDeviceDialog: '#add-device-dialog',
            content: '.container',
            configDeviceDialogContent: '#config-device-dialog .dialog-content',
            showDeviceTasksDialog: '#show-device-tasks-dialog'
        },

        onDeviceTasksShowDone: function (data) {
            var deviceTasksView = new DeviceTasksView(data);
            this.getRegion('showDeviceTasksDialog').show(deviceTasksView);
            deviceTasksView.$el.width(800).modal(sv.modalOptions.showClose);
        },

        onDeviceTasksShowFail: function () {
            sv.loading.close();
        },

        onDeviceGetAndScanDone: function (deviceViewOptions) {
            var deviceView = new DeviceView(deviceViewOptions);
            this.getRegion('addDeviceDialog').show(deviceView);
            deviceView.$el.width(800).modal(sv.modalOptions.unClosed);
        },

        onDeviceGetAndScanFail: function () {
            sv.loading.close();
            this.addDeviceDialog.$el.width(800);
        },

        destroyConfigDevice: function () {
            $.clearValidateError(this.ui.configDeviceDialog);
            this.getRegion('configDeviceDialogContent').reset();
        },

        childEvents: {
            'show:DeviceModal': function (childView, deviceId) {
                sv.loading.show({
                    d: 'add-device-dialog',
                    w: 400
                });
                sv.channel.request('device:setId', deviceId);
                sv.channel.request('device:getOrScanDevice', deviceId);
            },

            'show:ConfigModal': function (childView, deviceId) {
                var childNavOptions = sv.channel.request('device-config:getNavOptions', deviceId);
                sv.channel.request('device:setId', deviceId);
                var containerView = new sv.view.ContainerView({
                    childNavOptions: childNavOptions
                });
                this.getRegion('configDeviceDialogContent').show(containerView);
                this.ui.configDeviceDialog.modal(sv.modalOptions.showClose);
            },
            'show:DeviceTasksModal': function (childView, deviceId) {
                sv.loading.show({
                    d: 'show-device-tasks-dialog',
                    w: 400
                });
                sv.channel.request('device:showTasks', deviceId);
            }
        },

        onRender: function () {
            sv.nav.active('tb2');
            new sv.view.DeviceDetailContainerView();
            this.ui.configDeviceDialog.cleanTipsyOnModalClose();
            this.getRegion('content').show(new DeviceContentView());
        }
    });

    var DeviceContentView = Marionette.ItemView.extend({
        initialize: function () {
            sv.channel.vent.on('device:getItems', _.bind(this.onGetItems, this));
        },
        template: false,
        el: '.content-wrapper',
        ui: {
            addDeviceBtn: '#add-device-btn',
            editBtn: '.action-bar #edit',
            configBtn: '.action-bar #config',
            deleteBtn: '.action-bar #delete',
            showTasksBtn: '.action-bar .show-tasks',
            showInstallBtn: '.action-bar .show-install',
            selectAll: '.tab-content .select-all',
            selectOne: '.tab-content .select-one'

        },

        events: {
            'click @ui.addDeviceBtn': 'showAddDeviceModal',
            'click @ui.editBtn': 'showEditDeviceModal',
            'click @ui.configBtn': 'showConfigDeviceModal',
            'click @ui.deleteBtn': 'deleteDevice',
            'click @ui.showTasksBtn': 'showTasks',
            'click @ui.showInstallBtn': 'showInstall'

        },

        behaviors: {
            TableActionbarToggler: {
                none: ['editBtn', 'configBtn', 'deleteBtn', 'showTasksBtn', 'showInstallBtn'],
                multi: {
                    enable: ['deleteBtn'],
                    disable: ['editBtn', 'configBtn', 'showTasksBtn', 'showInstallBtn']
                }
            }
        },
        showLoading: function () {
            $("#loading-dialog").render("loadingMsg").modal({
                showClose: false,
                clickClose: false
            });
        },
        showTasks: function () {
            var checkedSelectOneItems = this.getCheckedSelectOneItems();
            if (checkedSelectOneItems.length == 1) {
                this.triggerMethod('show:DeviceTasksModal', checkedSelectOneItems.val());
            }
        },
        showInstall: function (obj) {
            var id = _(this.ui.selectOne).filter(function (v) {
                return v.value != '-1';
            }).map(function (v) {
                return v.value;
            });

            $.getJSON("get_ssh_info?id="+ id, function (sshInfo) {
                $("#dialog-install").render("addPackageTemplate");
                $("#sshIp").val(sshInfo.ip);
                $("#sshPort").val(sshInfo.port);
                $("#sshUserName").val(sshInfo.user);
                /*$('#sshPassWord').val(sshInfo.password);*/
                $("#packageadd").modal({
                    showClose: false, clickClose: false
                });
            });

            $("#packageadd").on("click", "#add-package-cancel-btn", $.proxy(function () {
                $.modal.close();
                $(".tipsy").remove();
            }, this)).on("click", "#add-package-ok-btn", $.proxy(function () {
                var validator = $("#add-package-form").validate();
                var sshIp = $("#sshIp").val(), sshPort = $("#sshPort").val(), sshUserName = $("#sshUserName").val();
                if (_.isEmpty(sshIp.trim())) {
                    validator.showErrors({
                        "sshIp": "请输入IP"
                    });
                }
                if (_.isEmpty(sshPort.trim())) {
                    validator.showErrors({
                        "sshPort": "请输入端口"
                    });

                }
                if (_.isEmpty(sshUserName.trim())) {
                    validator.showErrors({
                        "sshUserName": "请输入用户名"
                    });
                }
                if (validator.valid()) {
                    that = this;
                    $.post("/agentCheck_app", {
                        "ip": $('#sshIp').val(),
                        "port": $('#sshPort').val(),
                        "user": $('#sshUserName').val(),
                        "password": $('#sshPassWord').val()
                    }, function (result) {
                        if (result.status) {
                            that.showLoading();
                            $.post("/agentInstall_app", {
                                "ip": $('#sshIp').val(),
                                "port": $('#sshPort').val(),
                                "user": $('#sshUserName').val(),
                                "password": $('#sshPassWord').val()
                            }, function (result) {
                                setTimeout(function () {
                                    $.modal.close();
                                    if (result.status) {
                                        $("#add-device-btn").click()
                                    } else {
                                        alert(result.msg)
                                    }
                                }, 7000);
                            });

                        } else {
                            alert(result.msg);
                        }
                    });
                }

            }, this));

        },
        showAddDeviceModal: function () {
            this.triggerMethod('show:DeviceModal');
        },

        showEditDeviceModal: function () {
            var checkedSelectOneItems = this.getCheckedSelectOneItems();
            if (checkedSelectOneItems.length == 1) {
                this.triggerMethod('show:DeviceModal', checkedSelectOneItems.val());
            }
        },
        getCheckedSelectOneItems: function () {
            return this.ui.selectOne.filter(':checked');
        },

        showConfigDeviceModal: function () {
            var checkedSelectOneItems = this.getCheckedSelectOneItems();
            if (checkedSelectOneItems.length == 1) {
                this.triggerMethod('show:ConfigModal', checkedSelectOneItems.val());
            }
        },

        deleteDevice: function () {
            var checkedSelectOneItems = this.getCheckedSelectOneItems();
            if (checkedSelectOneItems.length > 0) {
                sv.prompt.showConfirm2('确认删除所选记录吗？', function () {
                        var idsJson = _.map(checkedSelectOneItems, function (v) {
                            return {
                                id: v.value
                            };
                        });
                        sv.channel.request('device:delete', idsJson);
                    }
                );
            }
        },

        syncStatus: function () {
            var ids = _(this.ui.selectOne).filter(function (v) {
                return v.value != '-1';
            }).map(function (v) {
                return v.value;
            });
            sv.channel.request('device:getDeviceByIds', ids);
        },

        onGetItems: function (deviceItems) {
            if (deviceItems && _.isArray(deviceItems)) {
                _.each(deviceItems, function (device) {
                    var deviceId = device.id,
                        statusText = (device['alive'] ? "在" : "离") + "线",
                        deviceSelectOneEl = this.ui.selectOne.filter("[value='" + deviceId + "']"),
                        detailEl;
                    if (deviceSelectOneEl.length > 0) {
                        deviceSelectOneEl.parent().parent().find("td:last").text(statusText);
                        detailEl = deviceSelectOneEl.closest("tr").find(".detail");
                        if (device['alive']) {
                            detailEl.show();
                        } else if (detailEl.hasClass(".more")) {
                            detailEl.trigger("click");
                        } else {
                            detailEl.hide();
                        }
                    }
                }, this);
            }
        },
        onRender: function () {
            $("#install-device-btn").click(function () {
                $("#dialog-install").render("addPackageTemplate");
                $("#packageadd").modal({
                    showClose: false, clickClose: false
                });
            });

            /*$("#install-device-btn").on("click", "#add-package-cancel-btn", $.proxy(function () {
             $.modal.close();
             }, this)).on("click", "#add-package-ok-btn", $.proxy(function () {
             _this.addPackage();
             }, this));*/

            $("#install-device-btn").cleanTipsyOnModalClose();

            $("#packageadd").on("click", "#add-package-cancel-btn", $.proxy(function () {
                $.modal.close();
                $(".tipsy").remove();
            }, this)).on("click", "#add-package-ok-btn", $.proxy(function () {
                var validator = $("#add-package-form").validate();
                var sshIp = $("#sshIp").val(), sshPort = $("#sshPort").val(), sshUserName = $("#sshUserName").val();
                if (_.isEmpty(sshIp.trim())) {
                    validator.showErrors({
                        "sshIp": "请输入IP"
                    });
                }
                if (_.isEmpty(sshPort.trim())) {
                    validator.showErrors({
                        "sshPort": "请输入端口"
                    });

                }
                if (_.isEmpty(sshUserName.trim())) {
                    validator.showErrors({
                        "sshUserName": "请输入用户名"
                    });

                }

                if (validator.valid()) {
                    that = this;
                    $.post("/agentCheck_app", {
                        "ip": $('#sshIp').val(),
                        "port": $('#sshPort').val(),
                        "user": $('#sshUserName').val(),
                        "password": $('#sshPassWord').val()
                    }, function (result) {
                        if (result.status) {
                            that.showLoading();
                            $.post("/agentInstall_app", {
                                "ip": $('#sshIp').val(),
                                "port": $('#sshPort').val(),
                                "user": $('#sshUserName').val(),
                                "password": $('#sshPassWord').val()
                            }, function (result) {
                                setTimeout(function () {
                                    $.modal.close();
                                    if (result.status) {
                                        $("#add-device-btn").click()
                                    } else {
                                        alert(result.msg)
                                    }
                                }, 7000);
                            });

                        } else {
                            alert(result.msg);
                        }

                    });


                }

            }, this));
            setInterval(_.bind(this.syncStatus, this), 3000);
        }

    });

    var DeviceTasksView = Marionette.ItemView.extend({
        initialize: function () {
            this.template = _.bind(function () {
                var channelsNames = this.getOption('channelNames');
                while ((channelsNames.length % 4) != 0) {
                    channelsNames.push("");
                }
                var groupNames = [];
                for (var i = 0; i < channelsNames.length; i += 4) {
                    var group = [];
                    for (var j = 0; j < 4; j++) {
                        group.push(channelsNames[i + j]);
                    }
                    groupNames.push(group);
                }
                return sv.utils.template("show-tasks-template", {
                    channelNames: groupNames,
                    count: this.getOption('count')
                });
            }, this);
        },

        events: {
            'click .close-modal': 'closeModal'
        },

        closeModal: function () {
            $.modal.close();
            this.destroy();
        }
    });

    var DeviceView = Marionette.ItemView.extend({
        initialize: function () {
            this.id = this.getOption('id') || -1; // for edit device
            this.deviceName = this.getOption('deviceName');
            this.template = _.bind(function () {
                return sv.utils.template(this.getOption('templateId'), this.getOption('data'));
            }, this);
            this.$el.cleanTipsyOnModalClose();
        },
        ui: {
            deviceNameInput: 'input[name="name"]',
            addDeviceInput: 'input[name="add-device"]',
            updateForm: '#device-update-form'
        },
        events: {
            'click #add-device-cancel-btn': 'closeModal',
            'click #add-device-ok-btn': 'joinDevice',
            'click #edit-device-ok-btn': 'saveDevice',
            'keyup @ui.deviceNameInput': 'deviceNameKeyUp',
            'change @ui.addDeviceInput': 'deviceNameChange',
            'keypress': 'closeWhenPressEnter',
            'click .close-modal': 'closeModal'
        },

        closeModal: function () {
            $.modal.close();
            this.destroy();
        },

        saveDevice: function () {
            var validator = this.ui.updateForm.validate(),
                name = this.ui.updateForm.find('#name').val();
            if (_.isEmpty(name.trim())) {
                validator.showErrors({
                    "name": "请输入名称"
                });
            } else if (this.deviceName != name) { //if the name has change, we do check on name.
                sv.channel.request('device:syncCheckName', name, function () {
                    validator.showErrors({
                        "name": "设备名称已存在"
                    });
                });
            }
            if (validator.valid()) {
                sv.channel.request('device:saveOrUpdate', this.getParams(false)[0]);
            }
        },

        joinDevice: function () {
            var forms = this.$('form');
            if (forms.length == 0) {
                this.closeModal();
                return;
            }
            $.clearValidateError(forms);

            var checkedDevices = this.$("input[name='add-device']:checked"),
                validates = [],
                formValidate = null;
            if (checkedDevices.length == 0) { //Have't choose any device
                formValidate = forms.filter(':eq(0)').validate();
                formValidate.showErrors({
                    "add-device": "请选择需要加入的设备"
                });
                validates.push(formValidate);
            } else {
                var nameOnPage = checkedDevices.parent().parent().find("input[name='name']"),
                    form, name,
                    errorNames = [],
                    nameValues = _.map(nameOnPage, function (v) {
                        return this.$(v).val();
                    }, this);
                for (var i = 0, len = checkedDevices.length; i < len; i++) {
                    form = $(checkedDevices.get(i)).parent().parent();
                    formValidate = form.validate();
                    name = form.find("input[name='name']").val();
                    formValidate.resetForm();
                    if (_.isEmpty(name.trim())) {
                        formValidate.showErrors({
                            "name": "请输入名称"
                        });
                    } else {
                        var nameExist = false;
                        if (nameValues.length > 1) {
                            var count = 0;
                            _.each(nameValues, function (v) {
                                if (name == v) {
                                    count++;
                                }
                            });
                            if (count > 1 && _.indexOf(errorNames, name) == -1) {
                                errorNames.push(name);
                                nameExist = true;
                                formValidate.showErrors({
                                    "name": "设备名称已存在"
                                });
                            }
                        }
                        if (!nameExist) {
                            sv.channel.request('device:syncCheckName', name, function () {
                                formValidate.showErrors({
                                    "name": "设备名称已存在"
                                });
                            });
                        }
                    }
                    validates.push(formValidate);
                }
            }
            var isValid = true;
            for (var i = 0; i < validates.length; i++) {
                if (!validates[i].valid()) {
                    isValid = false;
                }
            }
            if (isValid) {
                sv.channel.request('device:join', this.getParams(true));
            }
        },

        /**
         * Retrieves form data as a json array with specified operate.
         *
         * @param isJoin true the operate is join otherwise indicate update.
         * if the operate is join then it will compose multiple form.
         * @returns {Array}
         */
        getParams: function (isJoin) {
            var data = [];
            if (isJoin) { // join form
                var checkedDevices = this.$("input[name='add-device']:checked"),
                    form = null;
                _.each(checkedDevices, function (v) {
                    form = this.$(v).parent().parent();
                    var device = this._convertDeviceFormData(form);
                    device.id = this.$(v).val();
                    data.push(device);
                }, this);
            } else { //update form
                var device = this._convertDeviceFormData(this.$("form"));
                device.id = sv.channel.request('device:getId');
                data.push(device);
            }
            return data;
        },
        _convertDeviceFormData: function (form) {
            var device = {};
            device.name = form.find("input[name='name']").val();
            device.remark = form.find("input[name='remark']").val();
            device.activeFunctions = this.composeActiveFunctions(form.find("input[name='functions']:checked")).join(",");
            return device;
        },
        /**
         * Compose the specified checked functions jquery checkbox value as a array indicate
         * it is active functions
         *
         * @param checkedFunctions the checked device functions
         * @returns {Array} the active functions
         */
        composeActiveFunctions: function (checkedFunctions) {
            return _.map(checkedFunctions, function (v) {
                return this.$(v).val();
            }, this);
        },

        closeWhenPressEnter: function (event) {
            if (event.which == 13) {
                $("#add-device-ok-btn").trigger('click');
            }
        },

        deviceNameChange: function (event) {
            var target = $(event.currentTarget);
            if (target.prop("checked")) {
                target.removeClass("error").tipsy('hide').removeAttr('original-title');
            }
        },

        deviceNameKeyUp: function (event) {
            var target = $(event.currentTarget);
            if (target.val()) {
                target.removeClass("error").tipsy('hide').removeAttr('original-title');
            }
        }
    });

    var DeviceDetailStatusModel = Backbone.Model.extend({
        url: function () {
            return "/device/getStatus?id=" + this.get("id");
        },
        timerId: -1,
        initialize: function () {
            this.fetch();
            var that = this;
            this.timerId = setInterval(function () {
                that.fetch();
            }, 5000);
        },
        destroy: function () {
            if (this.timerId != -1) {
                clearInterval(this.timerId);
            }
        }
    });

    sv.view.DeviceDetailContainerView = Marionette.LayoutView.extend({
        el: '#device-list-table',

        events: {
            'click .detail': 'triggerDetail'
        },

        regions: {
            main: '#details-canvas-container'
        },
        initialize: function () {
            $("#details-container").hide();
        },

        triggerDetail: function (e) {
            var $el = this.$(e.currentTarget);
            e.preventDefault();
            this.main.empty();
            if ($el.hasClass("more")) {
                $el.removeClass("more");
                $("#details-container").hide();
            } else {
                $(".detail").removeClass("more");
                $el.addClass("more");
                var $tr = $el.closest("tr");
                $("#details-container").insertAfter($tr).end().show();
                var deviceId = $tr.find("input[name='ids']").val();
                var attributes = {
                    "id": deviceId
                };
                var model = new DeviceDetailStatusModel(attributes);
                var view = new sv.view.DeviceDetailView({
                    model: model
                });
                this.main.show(view);
            }
        }
    });

    sv.view.DeviceDetailView = Marionette.ItemView.extend({
        tagName: "canvas",
        id: "device-detail-canvas",

        template: false,
        canvasInitialized: false,
        initialize: function () {
            this.drawCanvas();
            this.listenTo(this.model, "change", this.render);
        },
        drawCanvas: function () {
            if (!this.model.get("GPU") || !this.model.get("CPU") || !this.model.get("MEMORY") || !this.model.get("NETWORK")) {
                return;
            }
            var setColor = function (area, ctx, data, config, i, j, othervars) {
                var percent = data[0].value * 100 / (data[0].value + data[1].value);
                if (percent < 30) {
                    return ("#4AD964");
                } else if (percent < 70) {
                    return ("#F0C44F");
                } else {
                    return ("#E63434");
                }
            };

            var gpus = this.model.get("GPU").length;
            var eths = this.model.get("NETWORK").length;
            if (gpus == 1 && eths == 1) {
                this.canvasWidth = 200 * 4 + 50;
                this.canvasHeight = 200 + 50;
            } else if (gpus == 1 || eths == 1) {
                var max = gpus > eths ? gpus : eths;
                this.canvasWidth = 200 * 3 + 270 + 50;
                this.canvasHeight = max > 2 ? Math.ceil(max / 2) * 130 + 50 : 200 + 50;
            } else {
                var max = gpus > eths ? gpus : eths;
                this.canvasWidth = 200 * 2 + 270 * 2 + 50;
                this.canvasHeight = max > 2 ? Math.ceil(max / 2) * 130 + 50 : 200 + 50;
            }
            this.$el.attr({
                "width": this.canvasWidth,
                "height": this.canvasHeight
            });
            var memory = this.model.get("MEMORY")[0];
            var memoryCrossText = function (numtxt, valtxt, ctx, config, posX, posY, borderX, borderY, overlay, data, animPC) {
                if (numtxt == 0) return (Math.round(data[0].value * 100 / (data[0].value + data[1].value)) + " %");
            };
            var memory_option = {
                multiGraph: true,
                graphTitle: "Memory Usage",
                graphTitleFontSize: 14,
                segmentShowStroke: false,
                inGraphDataShow: true,
                inGraphDataTmpl: "<%=v1+':'+v2+'G'%>",
                inGraphDataRadiusPosition: 2,
                inGraphDataPaddingRadius: -18,
                //legend: true,
                animation: false,
                crossText: ["%usage"],
                crossTextIter: ["last"],
                crossTextFontColor: ["black"],
                crossTextOverlay: [true],
                crossTextRelativePosX: [2],
                crossTextRelativePosY: [2],
                crossTextFunction: memoryCrossText,
                spaceTop: 25,
                spaceBottom: 25,
                spaceLeft: 25 + 15,
                spaceRight: this.canvasWidth - 25 - 200 + 15,
            };
            var memory_data = [{
                value: memory.usage,
                color: setColor, //"#D97041",
                title: "used"
            }, {
                value: memory.total - memory.usage,
                color: "#79A6CF", //"#C7604C",
                title: "free"
            }];
            this.Chart = new Chart(this.el.getContext("2d"));
            this.Chart.Doughnut(memory_data, memory_option);
            var cpu = this.model.get("CPU")[0];
            var cpuCrossText = function (numtxt, valtxt, ctx, config, posX, posY, borderX, borderY, overlay, data, animPC) {
                if (numtxt == 0) return (Math.round(data[0].value * 100 / (data[0].value + data[1].value)) + " %");
            };
            var cpu_option = {
                multiGraph: true,
                graphTitle: "CPU Usage",
                graphTitleFontSize: 14,
                segmentShowStroke: false,
                animation: false,
                crossText: ["%usage"],
                crossTextIter: ["last"],
                crossTextFontColor: ["black"],
                crossTextOverlay: [true],
                crossTextRelativePosX: [2],
                crossTextRelativePosY: [2],
                crossTextFunction: cpuCrossText,
                spaceTop: 25,
                spaceBottom: 25,
                spaceLeft: 25 + 200 + 15,
                spaceRight: this.canvasWidth - 25 - 200 * 2 + 15,
            };
            var cpu_data = [{
                value: cpu.usage,
                color: setColor, //"#D97041",
                title: "used"
            }, {
                value: cpu.total - cpu.usage,
                color: "#79A6CF", //"#C7604C",
                title: "free"
            }];
            this.Chart.Doughnut(cpu_data, cpu_option);
            for (var i = 0; i < this.model.get("GPU").length; i++) {
                var gpu = this.model.get("GPU")[i];
                var gpu_option = {
                    multiGraph: true,
                    graphTitle: "GPU" + i + " Usage",
                    graphTitleFontSize: 14,
                    segmentShowStroke: false,
                    animation: false,
                    crossText: ["%usage"],
                    crossTextIter: ["last"],
                    crossTextFontColor: ["black"],
                    crossTextOverlay: [true],
                    crossTextRelativePosX: [2],
                    crossTextRelativePosY: [2],
                    crossTextFunction: function (numtxt, valtxt, ctx, config, posX, posY, borderX, borderY, overlay, data, animPC) {
                        if (numtxt == 0) return (Math.round(data[0].value * 100 / (data[0].value + data[1].value)) + " %");
                    },
                    spaceTop: 25 + Math.floor(i / 2) * 130,
                    spaceBottom: this.canvasHeight - 25 - (Math.floor(i / 2) + 1) * 130,
                    spaceLeft: 25 + 200 * 2 + (i % 2) * 135,
                    spaceRight: this.canvasWidth - 25 - 200 * 2 - ((i % 2) + 1) * 135,
                };
                var gpu_data = [{
                    value: gpu.usage,
                    color: setColor, //"#D97041",
                    title: "used"
                }, {
                    value: gpu.total - cpu.usage,
                    color: "#79A6CF", //"#C7604C",
                    title: "free"
                }];
                this.Chart.Doughnut(gpu_data, gpu_option);
            }
            for (var j = 0; j < this.model.get("NETWORK").length; j++) {
                var eth = this.model.get("NETWORK")[j];
                var eth_option = {
                    multiGraph: true,
                    graphTitle: eth.name + " Usage",
                    graphTitleFontSize: 14,
                    segmentShowStroke: false,
                    animation: false,
                    crossText: ["%usage"],
                    crossTextIter: ["last"],
                    crossTextFontColor: ["black"],
                    crossTextOverlay: [true],
                    crossTextRelativePosX: [2],
                    crossTextRelativePosY: [2],
                    crossTextFunction: function (numtxt, valtxt, ctx, config, posX, posY, borderX, borderY, overlay, data, animPC) {
                        if (numtxt == 0) {
                            if (data[1].value > 100) {
                                return "Inactive";
                            } else {
                                return (Math.round(data[0].value * 100 / (data[0].value + data[1].value)) + " %");
                            }
                        }
                    },
                    spaceTop: 25 + Math.floor(j / 2) * 130,
                    spaceBottom: this.canvasHeight - 25 - (Math.floor(j / 2) + 1) * 130,
                    spaceLeft: 25 + 200 * 2 + 270 + (j % 2) * 135,
                    spaceRight: this.canvasWidth - 25 - 200 * 2 - 270 - ((j % 2) + 1) * 135
                };
                var data0, data1;
                if (eth['usage'] < 0) {
                    data0 = 0;
                    data1 = 101;
                } else {
                    data0 = eth['usage'] / 100;
                    data1 = eth['total'] - eth['usage'] / 100;
                }
                var eth_data = [{
                    value: data0,
                    color: setColor, //"#D97041",
                    title: "used"
                }, {
                    value: data1,
                    color: "#79A6CF", //"#C7604C",
                    title: "free"
                }];
                this.Chart.Doughnut(eth_data, eth_option);
            }
        },
        onRender: function () {
            this.drawCanvas();
        },
        onDestroy: function () {
            this.model.destroy();
        }
    });

    // Device config
    // ------------

    // Navigation for device config
    var NavActiveModel = Backbone.Model.extend({
        defaults: {
            activeIndex: 0
        }
    });

    sv.view.NavContainerView = Marionette.LayoutView.extend({
        el: '#nav',
        initialize: function (options) {
            this.bindUIElements();
            var hideChildViewNames = [],
                hideParents = {};

            _.each(options.childNavOptions, function (v, k) {
                if (v.hide) {
                    hideParents[k] = true;
                    delete options.childNavOptions[k];
                }
            });
            //Find child nav item by hide is true
            _.each(_.values(options.childNavOptions), function (val) {
                _.each(val.contentViewOptions, function (childView, k) {
                    if (childView.hide == true) {
                        hideChildViewNames.push(k);
                    }
                });
            });
            this.hideChildViewNames = hideChildViewNames;
            this.childNavOptions = options.childNavOptions;
            this.listenTo(sv.eventDispatcher, 'NavContainerView:switch-child', this.switchChildNav);
            this.listenTo(sv.eventDispatcher, 'NavContainerView:switch-content', this.triggerSwitchContent);
            //create root nav item
            this.rootNavView = new NavBarView({
                el: this.ui.parentNavContainer,
                model: new NavActiveModel(),
                isParent: true,
                hideParents: hideParents
            });
            this.rootNavView.render();
        },
        regions: {
            "child": "@ui.childNavContainer"
        },
        ui: {
            parentNavContainer: '#nav-parent',
            childNavContainer: '#nav-child'
        },
        template: false,

        switchChildNav: function (viewName) {
            //hide all of child nav item
            this.hideChildView();
            var navOptions = this.getChildNavViewOptions(viewName),
                hasChildNavItem = navOptions.templateId,
                contentViewOptions;
            //Create child nav item
            if (hasChildNavItem) {
                var childNavView = new NavBarView({
                    model: new NavActiveModel(),
                    hideView: this.hideChildViewNames,
                    template: function () {
                        return sv.utils.template(navOptions.templateId);
                    }
                });
                this.getRegion('child').show(childNavView);
                this.ui.childNavContainer.show();
            } else {
                contentViewOptions = navOptions.contentViewOptions;
                this.ui.childNavContainer.hide();
                this.triggerSwitchContent({
                    viewOptions: contentViewOptions
                });
            }

        },

        getChildNavViewOptions: function (viewName) {
            return this.childNavOptions[viewName];
        },

        getContentViewOptions: function (contentViewName) {
            var findOptions = _.find(_.values(this.childNavOptions), function (v) {
                return v.contentViewOptions[contentViewName];
            });
            return findOptions.contentViewOptions[contentViewName];
        },

        hideChildView: function () {
            _.each(this.hideChildViewNames, function (v) {
                this.$el.find("li[data-view='" + v + "']").hide();
            }, this);
        },

        triggerSwitchContent: function (options) {
            var viewOptions = options.viewOptions ? options.viewOptions : this.getContentViewOptions(options.viewName);
            sv.eventDispatcher.trigger('ContainerView:switch-content', viewOptions);
        },

        onDestroy: function () {
            this.rootNavView.destroy();
        }

    });

    var NavBarView = Marionette.ItemView.extend({
        initialize: function (options) {
            this.isParent = options.isParent == undefined ? false : options.isParent;
            this.hideParents = options.hideParents || {};
            this.contentViewName = undefined;
            this.hideView = options.hideView;
        },
        modelEvents: {
            "change": 'active'
        },

        events: {
            'click @ui.links': 'setIndex'
        },
        template: false,

        ui: {
            links: 'ul>li>a'
        },

        onRender: function () {
            if (!this.isParent) {
                this.hideChildView();
            } else {
                _.each(_.keys(this.hideParents), function (v) {
                    this.$("li[data-view='" + v + "']").hide();
                }, this);
            }
            this.model.set('activeIndex', 0);
            this.model.trigger('change');
        },

        hideChildView: function () {
            _.each(this.hideView, function (v) {
                this.$el.find("li[data-view='" + v + "']").hide();
            }, this);
        },

        setIndex: function (e) {
            var $el = this.$(e.target);
            var index = this.ui.links.index($el);
            this.model.set('activeIndex', index);
        },

        active: function () {
            $.clearValidateError($("form"));
            this.getActiveNode().removeClass('active');
            this.$el.find('ul>li:eq(' + this.model.get('activeIndex') + ')').addClass('active');
            if (this.isParent) {
                var data = this.getActiveNode().data();
                sv.eventDispatcher.trigger('NavContainerView:switch-child', data['view']);
            } else {
                var $activeNode = this.getActiveNode();
                this.contentViewName = $activeNode.data().view;
                sv.eventDispatcher.trigger('NavContainerView:switch-content', {
                    viewName: this.contentViewName
                });
            }
        },

        getActiveNode: function () {
            return this.$el.find('ul>li.active');
        },

        onDestroy: function () {
            this.model.destroy();
        }
    });

    sv.view.ContainerView = Marionette.LayoutView.extend({
        id: 'container',
        className: 'device-config-container',
        initialize: function (options) {
            this.childNavOptions = options.childNavOptions;
            this.navContainerView = undefined;
            this.listenTo(sv.eventDispatcher, 'ContainerView:switch-content', this.switchContent);
            this.listenTo(sv.channel.vent, 'change:ContentHeight', this.changeContentRegionHeight);
        },
        template: function () {
            return sv.utils.template("device-config-template");
        },
        regions: {
            content: "#content"
        },

        changeContentRegionHeight: function (height, reset) {
            var contentEl = this.getRegion('content').$el,
                h = contentEl.height();
            contentEl.height(reset ? height : h + height);
        },

        switchContent: function (viewOptions) {
            if (viewOptions.options == undefined) {
                viewOptions.options = {};
            }
            var contentRegion = this.getRegion('content'),
                hasChildNav = viewOptions.hasChildNav == undefined ? true : viewOptions.hasChildNav;
            if (hasChildNav) {
                contentRegion.$el.removeClass('no-nav');
            } else {
                contentRegion.$el.addClass('no-nav');
            }
            contentRegion.show(new viewOptions.view(viewOptions.options));
        },

        onRender: function () {
            var $dialogContent = $('.container #config-device-dialog .dialog-content');
            if ($dialogContent.length > 0) {
                $dialogContent.html(this.el);
            } else {
                $("#config-device-dialog").find(".dialog-content").html(this.el);
            }

            this.navContainerView = new sv.view.NavContainerView({
                childNavOptions: this.childNavOptions
            });
            this.navContainerView.render();
        },

        onDestroy: function () {
            this.navContainerView.destroy();
        }
    });


    sv.view.HostOperateView = Marionette.ItemView.extend({
        template: function () {
            return sv.utils.template('host-operate-template');
        },
        events: {
            'click #restart': 'restart',
            'click #shutdown': 'shutdown'
        },

        restart: function () {
            var that = this;
            sv.prompt.showConfirm2(m.get('host.restartPrompt'), function () {
                    that.post('/host/restart/');
                }
            );
        },
        shutdown: function () {
            var that = this;
            sv.prompt.showConfirm2(m.get('host.shutdownPrompt'), function () {
                    that.post('/host/shutdown/');
                }
            );
        },

        post: function (url) {
            return sv.ajax.post2({
                url: url + sv.channel.request('device:getId'),
                loading: '',
                done: function () {
                    sv.channel.vent.trigger('reload:location');
                }
            });
        }
    });

    sv.model.SystemSettings = Backbone.Model.extend({

        fetch: function () {
            return sv.ajax.get('/host/getSettings');
        },

        save: function (json) {
            xhrHandler(sv.ajax.post('/host/init', json), {
                done: function () {
                    sv.prompt.succeed2(m.get('host.saveSuccess'));
                }
            });
        }
    });

    sv.view.HostInitializeView = Marionette.ItemView.extend({

        model: new sv.model.SystemSettings(),

        initialize: function () {
            var that = this;
            xhrHandler(this.model.fetch(), {
                done: function (data) {
                    that.model.set(data);
                    that.ui.bindAddr.val(that.model.get('settings')['bindAddr']);
                }
            });
        },

        template: function (model) {
            return sv.utils.template('host-init-template', model);
        },

        modelEvents: {
            'change': 'render'
        },

        ui: {
            'form': 'form',
            'bindAddr': '#bindAddr'
        },

        events: {
            'click #save': 'save'
        },

        save: function () {
            if (this.ui.form.valid()) {
                var that = this;
                sv.prompt.showConfirm2(m.get('host.savePrompt'), function () {
                        that.model.save(that.ui.form.serializeObject());
                    }
                );
            }
        },

        onRender: function () {
            this.ui.form.validate({
                rules: {
                    clusterIp: {
                        required: true,
                        validIP: true
                    },
                    clusterPort: {
                        required: true,
                        digits: true,
                        range: [0, 65534]
                    },
                    bindAddr: {
                        required: true
                    },
                    timeToLive: {
                        required: true,
                        digits: true
                    },
                    heartbeatInterval: {
                        required: true,
                        digits: true
                    },
                    heartbeatTimeout: {
                        required: true,
                        digits: true
                    }
                }
            });
        }
    });

    //Ethernet setting
    sv.model.EthernetModel = Backbone.Model.extend({
        url: function () {
            return '/network/eth/' + sv.channel.request('device:getId');
        }
    });

    sv.view.EthernetCollection = Backbone.Collection.extend({
        model: sv.model.EthernetModel,
        url: function () {
            return '/network/eth/' + sv.channel.request('device:getId');
        }
    });
    sv.view.EthernetItemView = Marionette.ItemView.extend({
        className: 'eth-item',
        template: function (model) {
            model.rate = model.rate <= 0 ? 0 : model.rate;
            return sv.utils.template('eth-item-template', {
                m: model,
                rate: model.rate / 100
            });
        },
        modelEvents: {
            'change': 'render'
        },
        events: {
            'click': 'active'
        },

        active: function () {
            if (!this.isActive()) {
                sv.eventDispatcher.trigger('sv.view.EthernetItemView:active', this.model);
                this.$el.addClass('active');
            }
        },

        isActive: function () {
            return this.$el.hasClass('active');
        }
    });

    sv.view.EthernetListView = Marionette.CollectionView.extend({
        childView: sv.view.EthernetItemView,
        id: 'eth-items',
        className: 'float-left eth-items ht300',
        initialize: function () {
            var that = this;
            this._syncId = -1;

            xhrHandler(this.collection.fetch(), {
                done: function () {
                    //Active the first eth item
                    that.$el.children().first().trigger('click');
                }
            });

            this.listenTo(sv.eventDispatcher, 'sv.view.EthernetItemView:active', this.removeActiveItemClass);
        },

        onRender: function () {
            this.stopSync();
            var that = this;
            this._syncId = setInterval(function () {
                that.collection.fetch();
            }, 5000);
        },

        removeActiveItemClass: function () {
            this.$el.find('.active').removeClass('active');
        },

        onDestroy: function () {
            this.stopSync();
        },

        stopSync: function () {
            if (this._syncId != -1) {
                clearInterval(this._syncId);
            }
        }
    });

    sv.view.EthernetContentView = Marionette.ItemView.extend({
        id: 'eth-content',
        template: function (model) {
            return sv.utils.template('eth-content-template', model);
        },

        ui: {
            'isDHCP': '#isDHCP',
            'ip': '#ip',
            'mask': '#mask',
            'gateway': '#gateway',
            'form': 'form'
        },

        events: {
            'change @ui.isDHCP': 'onIsDHCPChange',
            'click .btn': 'save'
        },
        //
        //modelEvents: {
        //	'change': 'setValues'
        //},

        //setValues: function() {
        //	if (this.model.get('isDHCP') != undefined) {
        //		this.ui.ip.val(this.model.get('ip'));
        //		this.ui.mask.val(this.model.get('mask'));
        //		this.ui.gateway.val(this.model.get('gateway'));
        //		this.ui.isDHCP.val(this.model.get('isDHCP').toString());
        //	}
        //},

        onIsDHCPChange: function () {
            var disabled = this.ui.isDHCP.val() == 'true';
            this.ui.ip.prop("disabled", disabled);
            this.ui.mask.prop("disabled", disabled);
            this.ui.gateway.prop("disabled", disabled);
            if (disabled) {
                this.ui.ip.val('');
                this.ui.mask.val('');
                this.ui.gateway.val('');
            }
        },

        onRender: function () {
            if (this.model != null && this.model.get('isDHCP') != undefined) {
                this.ui.isDHCP.val(this.model.get('isDHCP').toString());
                this.onIsDHCPChange();
            }
            this.ui.form.validate({
                rules: {
                    ip: {
                        required: true,
                        validIP: true

                    },
                    mask: {
                        required: true,
                        validIP: true
                    },
                    gateway: {
                        validIP: {
                            depends: function (el) {
                                return $(el).val();
                            }
                        }
                    }
                }
            });
        },

        save: function () {
            if (this.ui.form.valid()) {
                var that = this;
                if (this.isAccessedEthernetChanged()) {
                    sv.prompt.showConfirm2(m.get('eth.savePrompt'), function () {
                            xhrHandler(that.model.save(that.ui.form.serializeObject()));
                        }
                    );
                } else {
                    xhrHandler(that.model.save(that.ui.form.serializeObject()));
                }
            }
        },
        isAccessedEthernetChanged: function () {
            var host = location.hostname;
            return host == this.model.get('ip') &&
                (this.model.get('ip') != this.ui.ip.val() || this.model.get('isDHCP') != this.ui.isDHCP.val() || this.model.get('mask') != this.ui.mask.val() || this.model.get('gateway') != this.ui.gateway());
        }
    });

    sv.view.EthernetView = Marionette.LayoutView.extend({
        id: 'content-network-eth',
        className: 'ht300',
        template: function () {
            return sv.utils.template("eth-template");
        },
        regions: {
            'eth-right': '#eth-right',
            'eth-left': '#eth-left'
        },

        initialize: function () {
            this.listenTo(sv.eventDispatcher, 'sv.view.EthernetItemView:active', this.onEthItemActive);
        },

        onRender: function () {
            var ethernetListView = new sv.view.EthernetListView({
                collection: new sv.view.EthernetCollection()
            });
            this.getRegion('eth-left').show(ethernetListView);
        },

        onEthItemActive: function (ethModel) {
            var ethernetContentView = new sv.view.EthernetContentView({
                model: ethModel
            });
            this.getRegion('eth-right').show(ethernetContentView);
        }
    });


    //Dns setting
    sv.model.DnsModel = Backbone.Model.extend({
        idAttribute: 'ip',
        url: function () {
            return '/network/dns/' + sv.channel.request('device:getId');
        }
    });

    sv.model.DnsCollection = Backbone.Collection.extend({
        url: function () {
            return '/network/dns/' + sv.channel.request('device:getId');
        },
        model: sv.model.DnsModel
    });

    sv.view.DnsView = Marionette.ItemView.extend({
        className: 'dns',
        template: function (model) {
            return sv.utils.template('eth-dns-template', model);
        },

        ui: {
            delBtn: '.op > .btn-del'
        },

        events: {
            'click @ui.delBtn': 'del'
        },

        del: function () {
            var that = this;
            xhrHandler(this.model.destroy({
                wait: true,
                data: this.model.toJSON()
            }), {
                done: function () {
                    that.triggerMethod('dns:del');
                }
            });
        }
    });


    sv.view.DnsCollectionView = Marionette.CollectionView.extend({
        className: 'dns-items',
        childView: sv.view.DnsView,
        collection: new sv.model.DnsCollection(),
        initialize: function () {
            this.listenTo(sv.eventDispatcher, 'dns:add', this.addDns);
            xhrHandler(this.collection.fetch());
        },
        childEvents: {
            'dns:del': function (ctx) {
                this.collection.remove(ctx.model);
            }
        },

        addDns: function (dnsModel) {
            this.collection.add(dnsModel);
        }
    });

    sv.view.DnsFormView = Marionette.ItemView.extend({
        className: 'dns',
        ui: {
            addBtn: '.addBtn',
            form: 'form',
            dnsInput: 'form #dns'
        },

        events: {
            'click @ui.addBtn': 'add'
        },

        template: function () {
            return sv.utils.template('eth-dns-form-template');
        },

        onRender: function () {
            this.ui.form.validate({
                rules: {
                    dns: {
                        required: true,
                        validIP: true
                    }
                }
            });
        },

        add: function () {
            if (this.ui.form.valid()) {
                var that = this;
                sv.prompt.showConfirm2(m.get('dns.savePrompt'), function () {
                        that.doAdd();
                    }
                );
            }
        },

        doAdd: function () {
            var data = this.ui.form.serializeObject(),
                dnsModel = new sv.model.DnsModel({
                    ip: data.dns
                }),
                xhr = dnsModel.save(null, {
                    type: 'POST'
                }),
                that = this;
            xhrHandler(xhr, {
                done: function () {
                    that.ui.dnsInput.val('');
                    sv.eventDispatcher.trigger('dns:add', dnsModel);
                }
            });
        }

    });

    sv.view.DnsLayoutView = Marionette.LayoutView.extend({
        template: function () {
            return sv.utils.template('eth-dns-layout-template');
        },

        regions: {
            items: '#dns-items',
            form: '#dns-form'
        },

        onRender: function () {
            this.getRegion('items').show(new sv.view.DnsCollectionView());
            this.getRegion('form').show(new sv.view.DnsFormView());
        }
    });


    //Route setting
    sv.model.Route = Backbone.Model.extend({
        idAttribute: '_id',
        url: function () {
            return '/network/route/' + sv.channel.request('device:getId');
        },
        parse: function (resp) {
            resp._id = _.uniqueId();
            return resp;
        }
    });

    sv.model.RouteItems = Backbone.Collection.extend({
        url: function () {
            return '/network/route/' + sv.channel.request('device:getId');
        },
        model: sv.model.Route

    });

    sv.view.RouteItemView = Marionette.ItemView.extend({
        tagName: 'tr',
        template: function (route) {
            return sv.utils.template("eth-route-item-template", route);
        },

        ui: {
            delBtn: '.btn-del'
        },
        events: {
            'click @ui.delBtn': 'delRoute'
        },

        delRoute: function () {
            var that = this;
            xhrHandler(this.model.destroy({
                wait: true,
                data: this.model.toJSON()
            }), {
                done: function () {
                    that.triggerMethod('route:del');
                }
            });
        }
    });

    sv.view.RouteTableView = Marionette.CompositeView.extend({
        childView: sv.view.RouteItemView,
        childViewContainer: 'tbody',
        collection: new sv.model.RouteItems(),
        template: function (model) {
            return sv.utils.template("eth-route-table-template", model);
        },

        initialize: function () {
            this.listenTo(sv.eventDispatcher, 'route:add', this.syncRoute);
            xhrHandler(this.collection.fetch());
        },

        childEvents: {
            'route:del': function (childView) {
                this.collection.remove(childView.model);
            }
        },

        syncRoute: function () {
            this.collection.fetch();
        }

    });

    sv.view.RouteFormView = Marionette.ItemView.extend({
        template: function (model) {
            return sv.utils.template("eth-route-add-template", model);
        },

        initialize: function () {
            //Fetch all of ethernet information
            this.model.fetch();
        },

        modelEvents: {
            'change': 'render'
        },

        events: {
            'click @ui.addBtn': 'addRoute'
        },

        ui: {
            addBtn: '#route-form #addBtn',
            routeDes: '#route-form #route-des',
            routeMask: '#route-form #route-mask',
            routeGateway: '#route-form #route-gateway',
            routeIface: '#route-form #route-iface',
            routeForm: '#route-form'
        },

        onRender: function () {
            this.ui.routeForm.validate({
                rules: {
                    'dest': {
                        required: true,
                        validIP: true
                    },
                    'mask': {
                        required: true,
                        validIP: true
                    },
                    'gateway': {
                        validIP: {
                            depends: function (el) {
                                return $(el).val();
                            }
                        }
                    }
                }
            });
        },

        addRoute: function () {
            if (this.ui.routeForm.valid()) {
                var that = this;
                sv.prompt.showConfirm2(m.get('route.savePrompt'), function () {
                        that.doAddRoute();
                    }
                );
            }

        },

        doAddRoute: function () {
            var routeModel = new sv.model.Route(this.ui.routeForm.serializeObject()),
                that = this;
            xhrHandler(routeModel.save(), {
                done: function () {
                    sv.eventDispatcher.trigger('route:add', routeModel);
                    that.ui.routeForm.trigger('reset');
                }
            });
        }

    });

    sv.view.RouteContainerView = Marionette.LayoutView.extend({
        className: 'route-container',
        template: function (model) {
            return sv.utils.template("eth-route-container-template", model);
        },

        regions: {
            table: '#route-table-container',
            form: '#route-add-form-container'
        },

        onRender: function () {
            this.getRegion('table').show(new sv.view.RouteTableView());
            this.getRegion('form').show(new sv.view.RouteFormView({
                model: new sv.model.EthernetModel()
            }));
        }

    });

    //Storage
    sv.model.Storage = Backbone.Model.extend({
        url: function () {
            return '/storage/' + sv.channel.request('device:getId');
        },
        unmount: function () {
            return sv.ajax.post(('/storage/' + sv.channel.request('device:getId') + '/' + this.get('id')));
        },

        mount: function () {
            return $.ajax({
                type: 'PUT',
                url: sv.urlPath.getRealPath('/storage/' + sv.channel.request('device:getId') + '/' + this.get('id'))
            });
        },

        destroy: function (options) {
            var opts = _.extend({
                url: (_.isFunction(this.url) ? this.url() : this.url) + '/' + this.id
            }, options || {});
            return Backbone.Model.prototype.destroy.call(this, opts);
        }
    });

    sv.model.StorageItems = Backbone.Collection.extend({
        url: function () {
            return '/storages/' + sv.channel.request('device:getId');
        },
        model: sv.model.Storage
    });

    sv.view.StorageItemView = Marionette.ItemView.extend({
        tagName: 'tr',
        template: function (model) {
            return sv.utils.template('storage-item-template', model);
        },

        ui: {
            umountBtn: '#umountBtn',
            mountBtn: '#mountBtn',
            editBtn: '#editBtn',
            delBtn: '#delBtn'
        },

        modelEvents: {
            'change': 'render'
        },

        events: {
            'click @ui.umountBtn': 'unmount',
            'click @ui.mountBtn': 'mount',
            'click @ui.editBtn': 'edit',
            'click @ui.delBtn': 'deleteItem'
        },

        unmount: function () {
            var that = this;
            xhrHandler(this.model.unmount(), {
                done: function () {
                    that.model.set('mounted', false);
                    sv.eventDispatcher.trigger('storage:unmount');

                }
            });
        },

        mount: function () {
            var that = this;
            xhrHandler(this.model.mount(), {
                done: function () {
                    that.model.set('mounted', true);
                    sv.eventDispatcher.trigger('storage:mount');
                }
            });
        },

        edit: function () {
            if (this.model.get('mounted')) {
                sv.prompt.error2(m.get('storage.editPrompt'));
            } else {
                sv.eventDispatcher.trigger('storage:edit', this.model.toJSON());
            }
        },

        deleteItem: function () {
            xhrHandler(this.model.destroy({
                wait: true
            }));
        }
    });

    sv.view.StorageItemsView = Marionette.CompositeView.extend({
        template: function () {
            return sv.utils.template('storage-table-template');
        },
        initialize: function () {
            this.collection = new sv.model.StorageItems();
            xhrHandler(this.collection.fetch());
            this.listenTo(sv.eventDispatcher, 'storage:add', this.add);
        },
        childView: sv.view.StorageItemView,
        childViewContainer: 'tbody',
        childEvents: {
            'storage:del': function (childView) {
                console.log("remove  ", childView.model.toJSON());
            }
        },

        add: function (model) {
            this.collection.add(model, {
                merge: true
            });
        }
    });

    sv.view.StorageFormView = Marionette.ItemView.extend({
        template: function () {
            return sv.utils.template('storage-form-template');
        },

        initialize: function () {
            this._id = undefined;
            this.listenTo(sv.eventDispatcher, 'storage:edit', this.set);
            this.listenTo(sv.eventDispatcher, 'storage:unmount', this.set);
            this.listenTo(sv.eventDispatcher, 'storage:mount', this.set);
        },

        ui: {
            saveBtn: '#saveBtn',
            resetBtn: '#resetBtn',
            form: '#storage-form',
            name: '#name',
            type: '#type',
            path: '#path',
            user: '#user',
            pwd: '#pwd'
        },

        events: {
            'click @ui.saveBtn': 'save',
            'click @ui.resetBtn': 'reset'
        },

        onRender: function () {
            this.formValidate = this.ui.form.validate();
        },

        save: function () {
            if (this.ui.form.valid()) {
                var data = this.ui.form.serializeObject();
                if (!_.isUndefined(this._id)) {
                    data.id = this._id;
                }
                var storageModel = new sv.model.Storage(data),
                    that = this;
                xhrHandler(storageModel.save(), {
                    done: function () {
                        that.set();
                        sv.eventDispatcher.trigger('storage:add', storageModel);
                    }
                });
            }
        },

        reset: function () {
            $.clearValidateError(this.ui.form);
            this.formValidate.resetForm();
            this._id = undefined;
            this.ui.form.trigger('reset');
        },

        set: function (storage) {
            this.reset();
            if (_.isEmpty(storage)) {
                return;
            }
            this._id = storage.id;
            _.each(storage, function (v, k) {
                if (_.has(this.ui, k)) {
                    this.ui[k].val(v);
                }
            }, this);
        }
    });

    sv.view.StorageContainerView = Marionette.LayoutView.extend({
        id: 'storage-container',
        template: function () {
            return sv.utils.template("storage-container-template");
        },

        regions: {
            table: '#storage-table-container',
            form: '#storage-form-container'
        },

        onRender: function () {
            this.getRegion('table').show(new sv.view.StorageItemsView());
            this.getRegion('form').show(new sv.view.StorageFormView());
        }
    });

    var BaseConfigurationModel = Backbone.Model.extend({
        parse: function (resp) {
            return resp['r'];
        },

        fetch: function () {
            xhrHandler(Backbone.Model.prototype.fetch.apply(this), arguments[0]);
        },

        save: function () {
            xhrHandler(Backbone.Model.prototype.save.call(this), {
                done: function () {
                    sv.prompt.succeed2(m.get('success'));
                }
            });
        }
    });

    // Rtsp configuration
    sv.model.RtspModel = BaseConfigurationModel.extend({
        props: ['id', 'mixedPublishUrl', 'publishFolderPath', 'ip'],
        url: function () {
            return '/cfg/rtsp';
        },

        toJSON: function () {
            return _.pick(this.attributes, this.props);
        },

        getPublishUrls: function () {
            var url = this.get('mixedPublishUrl');
            return url ? url.split(',') : '';
        },

        save: function () {
            xhrHandler(Backbone.Model.prototype.save.call(this), {
                done: function () {
                    sv.prompt.succeed2(m.get('success'));
                    sv.channel.trigger('rtspModel:save-success');
                }
            });
        }

    });

    sv.model.RtspPublishUrlModel = Backbone.Model.extend({
        defaults: {
            idx: 1,
            url: ''
        }
    });
    sv.model.RtspPublishUrlItems = Backbone.Collection.extend({
        model: sv.model.RtspPublishUrlModel
    });

    sv.view.RtspPublishUrlView = Marionette.ItemView.extend({
        className: 'publish-url',
        initialize: function (options) {
            this.model.set({idx: options.idx});
            this.listenTo(this.model, 'change:idx', this.resetIdx);
        },
        template: function (model) {
            return sv.utils.template("config-rtsp-publish-url-template", model);
        },

        events: {
            'change @ui.url': 'changeUrl'
        },

        ui: {
            idxEl: '#url-index',
            url: 'input[type="text"]'
        },

        changeUrl: function (e) {
            this.model.set('url', this.$(e.target).val());
        },

        triggers: {
            'click #del-url': 'remove:publishUrl'
        },

        resetIdx: function (model, idx) {
            this.ui.idxEl.text(idx);
        },

        onBeforeDestroy: function () {
            $.clearValidateError(this.$el);
        }
    });

    sv.view.RtspPublishUrlContainerView = Marionette.CollectionView.extend({
        childView: sv.view.RtspPublishUrlView,
        childViewOptions: function (model, index) {
            return {idx: index + 1};
        },

        onChildviewRemovePublishUrl: function (childView) {
            if (this.collection.size() > 3) {
                sv.channel.trigger('change:ContentHeight', -40);
            }
            this.collection.remove(childView.model);
        },

        onRemoveChild: function (childView) {
            this.collection.forEach(function (v, idx) {
                if (v.get('idx') != (idx + 1)) {
                    v.set('idx', idx + 1);
                }
            });
        },

        onAddChild: function (childView) {
            if (childView._index >= 3 && this.collection.size() > 3) {
                sv.channel.trigger('change:ContentHeight', 40);
            }
        }
    });

    //	rtsp config
    sv.view.RtspConfigView = Marionette.LayoutView.extend({
        id: 'config-rtsp',
        className: 'config-container',
        model: new sv.model.RtspModel(),
        template: function (model) {
            return sv.utils.template("config-rtsp-template", model);
        },
        initialize: function () {
            var that = this;
            this.model.fetch({
                done: function () {
                    that.showPublishUrlsContainer();
                }
            });
            this.listenTo(sv.channel.vent, 'rtspModel:save-success', this.showPublishUrlsContainer);
        },

        regions: {
            'publishUrlRegion': '#container-publish-url'
        },

        modelEvents: {
            'change': 'render'
        },

        ui: {
            form: '#rtspConfigForm',
            publishFolderPath: '#publishFolderPath'
        },

        events: {
            'click #saveBtn': 'save',
            'click #addPublishUrl': 'appendPublishUrl'
        },

        childEvents: {
            'remove:child': function () {
                this.ui.form.valid();
            }
        },

        save: function () {
            if (this.ui.form.valid()) {
                var data = this.ui.form.serializeObject();
                data['mixedPublishUrl'] = this.getMixedPublishUrl();
                data['publishFolderPath'] = this.appendSlashIfMissing(data['publishFolderPath']);
                this.model.set(data);
                this.model.save(data);
            }
        },

        appendPublishUrl: function () {
            var publishUrlContainerView = this.getPublishUrlContainerView();
            if (publishUrlContainerView.collection.size() < 4) {
                publishUrlContainerView.collection.add(new sv.model.RtspPublishUrlModel());
            }
        },

        getMixedPublishUrl: function () {
            return this.getPublishUrlContainerView().collection.map(function (v) {
                return v.get('url');
            }).join(',');
        },

        getPublishUrlContainerView: function () {
            return this.getRegion('publishUrlRegion').currentView;
        },

        appendSlashIfMissing: function (v) {
            return v.match('/$') ? v : v + '/';
        },

        showPublishUrlsContainer: function () {
            var urlsOfModel = this.model.getPublishUrls(),
                urls;
            if (_.isArray(urlsOfModel) && urlsOfModel.length > 0) {
                urls = _.filter(urlsOfModel, function (v) {
                    return v != '';
                }).map(function (v) {
                    return new sv.model.RtspPublishUrlModel({
                        url: v
                    });
                });
            } else {
                urls = [new sv.model.RtspPublishUrlModel()];
            }
            var publishUrlItems = new sv.model.RtspPublishUrlItems(urls);
            var rtspPublishUrlContainerView = new sv.view.RtspPublishUrlContainerView({
                collection: publishUrlItems
            });
            this.getRegion('publishUrlRegion').show(rtspPublishUrlContainerView);
        },

        onRender: function () {
            this.ui.form.validate({
                rules: {
                    ip: {
                        validIP: true
                    }
                }
            });
        },

        onBeforeDestroy: function () {
            $.clearValidateError(this.$el);
        },

        onDestroy: function () {
            sv.channel.trigger('change:ContentHeight', 410, true);
        }
    });

    // Gpu configuration
    sv.model.GpuModel = BaseConfigurationModel.extend({
        defaults: {
            enableSpan: true
        },
        url: function () {
            return '/cfg/gpu';
        }
    });
    sv.view.GpuConfigView = Marionette.ItemView.extend({
        id: 'config-gpu',
        className: 'config-container',
        model: new sv.model.GpuModel(),
        template: function (model) {
            return sv.utils.template("config-gpu-template", model);
        },

        initialize: function () {
            this.model.fetch();
        },

        modelEvents: {
            'change': 'render'
        },

        events: {
            'click #saveBtn': 'save'
        },

        selectors: {
            'checkedSpanGpu': 'input[name="enableSpanGpu"]:checked'
        },

        save: function () {
            var checkedSpanGpu = this.$(this.selectors.checkedSpanGpu);
            this.model.set({
                enableSpan: checkedSpanGpu.val() == '1'
            });
            this.model.save();
        },

        onRender: function () {
            this.$('input[name="enableSpanGpu"][value=' + (this.model.get('enableSpan') ? 1 : 0) + ']').prop('checked', true);
        }
    });

    sv.model.RecordModel = BaseConfigurationModel.extend({
        url: function () {
            return '/cfg/record';
        },
        parse: function (resp, options) {
            resp = BaseConfigurationModel.prototype.parse.call(this, resp);
            if (resp) {
                if (resp.keepTime) {
                    resp.keepTime = resp.keepTime / 60;
                }
                if (resp.contentDetectKeepTime) {
                    resp.contentDetectKeepTime = resp.contentDetectKeepTime / 60;
                }
            }
            return resp;
        },
        toJSON: function (options) {
            var clone = _.clone(this.attributes);
            if (!options.render) {
                if (clone.keepTime) {
                    clone.keepTime = clone.keepTime * 60;
                }
                if (clone.contentDetectKeepTime) {
                    clone.contentDetectKeepTime = clone.contentDetectKeepTime * 60;
                }
            }

            return clone;
        }
    })


    sv.view.RecordConfigView = Marionette.LayoutView.extend({
        id: 'config-record',
        className: 'config-container',
        model: new sv.model.RecordModel(),
        template: function (model) {
            return sv.utils.template("config-record-template", model);
        },
        serializeData: function () {
            return Marionette.ItemView.prototype.serializeData.call(this, {render: true});
        },
        initialize: function () {
            var that = this;
            this.model.fetch({
                done: function () {
                    that.render();
                }
            });
        },

        modelEvents: {
            'change': 'render'
        },

        events: {
            'click #saveBtn': 'save',
            'change input[name="domain"]': 'updateDomain',
            'change input[name="supervisorStoragePath"]': 'updateSupervisorStoragetPath',
            'change input[name="recorderStoragePath"]': 'updateRecorderStoragePath',
            'change input[name="keepTime"]': 'updateKeepTime',
            'change input[name="profileId"]': 'updateProfileId',
            'change input[name="contentDetectStoragePath"]': 'updateContentDetectStoragePath',
            'change input[name="contentDetectKeepTime"]': 'updateContentDetectKeepTime'
        },

        updateDomain: function (event) {
            this.model.set("domain", $(event.currentTarget).val());
        },

        updateSupervisorStoragetPath: function (event) {
            this.model.set("supervisorStoragePath", $(event.currentTarget).val());
        },

        updateRecorderStoragePath: function (event) {
            this.model.set("recorderStoragePath", $(event.currentTarget).val());
        },

        updateKeepTime: function (event) {
            this.model.set("keepTime", $(event.currentTarget).val());
        },

        updateProfileId: function (event) {
            this.model.set("profileId", $(event.currentTarget).val());
        },

        updateContentDetectStoragePath: function (event) {
            this.model.set("contentDetectStoragePath", $(event.currentTarget).val());
        },

        updateContentDetectKeepTime: function (event) {
            this.model.set("contentDetectKeepTime", $(event.currentTarget).val());
        },

        appendSlashIfMissing: function (v) {
            return v.match('/$') ? v : v + '/';
        },

        save: function () {
            if ($("#recordConfigForm").valid()) {
                var domain = this.model.get("domain");
                this.model.set("domain", this.appendSlashIfMissing(domain));
                var supervisorStoragePath = this.model.get("supervisorStoragePath");
                this.model.set("supervisorStoragePath", this.appendSlashIfMissing(supervisorStoragePath));
                var recorderStoragePath = this.model.get("recorderStoragePath");
                this.model.set("recorderStoragePath", this.appendSlashIfMissing(recorderStoragePath));
                var contentDetectStoragePath = this.model.get("contentDetectStoragePath");
                this.model.set("contentDetectStoragePath", this.appendSlashIfMissing(contentDetectStoragePath));
                this.model.save();
            }
        },
        onRender: function () {
            $("#recordConfigForm").validate({
                rules: {
                    keepTime: {
                        number: true,
                        digits: true,
                        min: 1
                    },
                    contentDetectKeepTime: {
                        number: true,
                        digits: true,
                        min: 1
                    }
                }
            });
        },
        onBeforeDestroy: function () {
            $.clearValidateError(this.$el);
        }
    });

    return {
        init: function () {

            new DeviceLayoutView().render();
        }
    };
}(jQuery, window.sv, Backbone, Marionette, _, window.Chart));

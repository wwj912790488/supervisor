(function ($, _, Backbone, Marionette, sv) {

    var BaseLogView = Marionette.ItemView.extend({
        urlPrefix: '/log/',
        urlSuffix: '',
        redirectUrlSuffix: '',
        template: false,
        el: '.container',
        ui: {
            selectAll: '.select-all',
            selectOne: '.select-one',
            searchForm: '#search-form',
            searchBtn: '#search-btn',
            exportBtn: '#export-btn',
            export: 'input[name="export"]',
            delete: 'input[name="delete"]',
            deleteFilterBtn: '#delete-filtered-btn',
            deleteBtn: '#delete-btn',
            scheduleBtn: '#sss',
            exportServiceLog: '.getLog',
            // if you need an keydown event on specified element
            // you must specified it within child.
            keyDownEl: ''
        },

        events: {
            'click @ui.searchBtn': 'doSearch',
            'click @ui.exportBtn': 'doExport',
            'click @ui.deleteBtn': 'doDelete',
            'click @ui.keyDownEl': 'onElKeyDown',
            'click @ui.deleteFilterBtn': 'doDeleteFiltered',
            'click @ui.scheduleBtn': 'scheduleClick',
            'click @ui.exportServiceLog': 'exportServiceLogClick'
        },

        behaviors: {
            TableActionbarToggler: {
                none: ['deleteBtn'],
                one: ['deleteBtn'],
                multi: {
                    enable: ['deleteBtn']
                }
            }
        },
        scheduleClick: function () {
            $("#dialog-install").render("addPackageTemplate");
            $.ajax({
                type: "get",
                url: "/host/getSettings",
                async: false,
                success: function (result) {
                    $("#deleteBeforeDays").val(result.settings.alertAutoDeleteDays);

                }
            })

            $("#packageadd").modal({
                showClose: false, clickClose: false
            });
        },
        exportServiceLogClick: function (obj) {
            $("#dialog-install").render("dwonSystemLog");
            $("#serviceLogId").val($(obj.currentTarget.parentElement.parentElement).find("#select-one").val());
            $("#downServiceLog").modal({
                showClose: false, clickClose: false
            });
        },
        onElKeyDown: function (event) {
            if (event.which === 13) {
                this.doSearch();
            }
        },

        doSearch: function () {
            this.ui.searchForm.submit();
        },

        doExport: function () {
            this.ui.export.val('excel');
            this.doSearch();
            this.ui.export.val('');
        },

        doDelete: function () {
            var selectIdEls = this.getSelectedItems();
            if (selectIdEls.length > 0) {
                var that = this;
                sv.prompt.showConfirm2('是否删除所选记录？', function () {
                    var ids = _.map(selectIdEls, function (v) {
                        return {
                            id: v.value
                        };
                    });
                    that.deleteLogs(ids);

                });
            }
        },

        doDeleteFiltered: function () {
            var that = this;
            sv.prompt.showConfirm2('是否删除所有符合条件的记录？', function () {
                that.ui.delete.val("true");
                that.ui.searchForm.submit();
                that.ui.delete.val("");
            });
        },

        getSelectedItems: function () {
            return this.ui.selectOne.filter(':checked');
        },

        deleteLogs: function (params) {
            var url = this.urlPrefix + this.urlSuffix,
                redirectUrl = this.urlPrefix + this.redirectUrlSuffix;
            var page = $("#pagerForm").serialize()
            sv.ajax.post(url, {
                ids: $.toJSON(params)
            }, function () {

                location.href = sv.urlPath.getRealPath(redirectUrl + "?" + page);
            });
        },

        onRender: function () {
            sv.nav.active('tb7');
            $("#install-device-btn").cleanTipsyOnModalClose();
            $("#packageadd").on("click", "#add-package-cancel-btn", $.proxy(function () {
                $.modal.close();
                $(".tipsy").remove();
            }, this)).on("click", "#add-package-ok-btn", $.proxy(function () {
                var validator = $("#add-package-form").validate();
                var deleteBeforeDays = $("#deleteBeforeDays").val();
                var math = /^[0-9]+[0-9]*]*$/;
                if (!math.test(deleteBeforeDays)) {
                    validator.showErrors({
                        "deleteBeforeDays": "请输入整数"
                    });
                }

                if (validator.valid()) {
                    that = this;
                    $.post("/host/saveDeleteBeforeDays", {
                        "day": deleteBeforeDays,
                    }, function (result) {
                        $.modal.close();
                    });
                }

            }, this))

            $("#downServiceLog").on("click", "#add-downServiceLog-cancel-btn", $.proxy(function () {
                $.modal.close();
                $(".tipsy").remove();
            }, this)).on("click", "#add-downServiceLog-ok-btn", $.proxy(function () {
                that = this;
                var includeSystem = $("#includeSystem").is(':checked');
                $("#includeSystem").val(includeSystem);
                var includeApplication = $("#includeApplication").is(':checked');
                $("#includeApplication").val(includeApplication);
                $("#add-downServiceLog-form").submit();
                $.modal.close();

                /*$.ajax({
                    type: "POST",
                    url: "/log/downLogs",
                    data: {
                        "id": id,
                        "includeSystem": includeSystem,
                        "includeApplication": includeApplication
                    },
                    async: false,
                    success: function (result) {
                        $.get
                        $.modal.close();
                        if (result.status) {
                            $.ajax({
                                type: "GET",
                                url: "/download_app?fileName="+result.path,
                                async: false,
                            }

                            )

                        }

                    }
                })*/


            }, this))


        }
    });

    var ContentDetectLogView = BaseLogView.extend({
        urlSuffix: 'deleteContentDetectLogs',
        redirectUrlSuffix: 'cd',
        events: _.extend(BaseLogView.prototype.events, {
            'click .export-btn': 'exportVideo'
        }),

        ui: _.extend(BaseLogView.prototype.ui, {
            keyDownEl: '#channelName'
        }),

        exportVideo: function (event) {
            var url = $(event.currentTarget).data('url');
            location.href = "/tms.content?url=" + url;
        }
    });

    var ServiceLogView = BaseLogView.extend({
        urlSuffix: 'deleteServiceLogs',
        redirectUrlSuffix: 's',
        ui: _.extend(BaseLogView.prototype.ui, {
            'keyDownEl': '#description'
        })
    });

    var SystemLogView = BaseLogView.extend({
        urlSuffix: 'deleteSystemLogs',
        redirectUrlSuffix: 'op',
        ui: _.extend(BaseLogView.prototype.ui, {
            'keyDownEl': '#operationInfo'
        })
    });

    var chartLogView = BaseLogView.extend({
        urlSuffix: 'deleteContentDetectLogs',
        redirectUrlSuffix: 'cd',
        events: _.extend(BaseLogView.prototype.events, {
            'click .export-btn': 'exportVideo'
        }),

        ui: _.extend(BaseLogView.prototype.ui, {
            keyDownEl: '#channelName'
        }),

        exportVideo: function (event) {
            var url = $(event.currentTarget).data('url');
            location.href = "/tms.content?url=" + url;
        }
    });

    sv.ContentDetectLogView = ContentDetectLogView;
    sv.ServiceLogView = ServiceLogView;
    sv.SystemLogView = SystemLogView;
    sv.chartLogView = chartLogView;

}(jQuery, _, Backbone, Marionette, window.sv));

var ChannelManager = (function ($, sv) {

    function Group(channelList) {
        this.activeId = -1;
        this.channelList = channelList;
    }

    Group.prototype = {

        setActiveId: function (id) {
            this.activeId = id ? id : -1;
        },

        getActiveId: function () {
            return this.activeId;
        },

        setTitle: function (title) {
            $(".list").find(".title-name").text(title);
        },

        reload: function () {
            var that = this;
            $.getJSON("groups?r=" + Math.random(), function (groups) {
                $("#channel-lists-nav").render("groupTemplate", {"groups": groups});
                $("#move-channel-dialog > .dialog-content").render("moveChannelTemplate", {"groups": groups});
                $("#channel-lists-nav").find("li[data-id='" + that.getActiveId() + "']").trigger("click");
            });
            $.getJSON("count?r=" + Math.random(), function (result) {
                var allAcounts = 0;
                for (var i = 0; i < result.length; i++) {
                    var count = result[i];
                    $("#channel-lists-nav").find("li[data-id='" + count.channelGroup.id + "']").find(".nav-title").append(" [" + count.count + "]")
                    allAcounts = allAcounts + count.count;
                }
                $(".channel-lists").find(".title-content").html("").append("频道分组列表 [" + allAcounts + "]");
            });


        },

        remove: function (groupId) {
            var that = this;
            $.post("removeGroup", {"id": groupId}, function () {
                that.setTitle("");
                that.setActiveId(-1);
                that.reload();
            });


        },

        save: function () {
            var groupForm = $("#groupForm");
            var validator = groupForm.validate({
                messages: {
                    groupName: "请输入分组名称"
                }
            });
            if (groupForm.valid()) {
                var that = this;
                $.post("saveGroup", {"name": $("#add-channel-list-dialog").find("#groupName").val()}, function (result) {
                    if (!result) {
                        validator.showErrors({
                            "groupName": "该名称已存在"
                        });
                    } else {
                        that.setActiveId(result);
                        that.reload();
                        $.modal.close();
                    }
                });
            }
        },

        active: function ($li) {
            this.setTitle($li.find(".nav-title").text());
            $li.siblings().removeClass("selected").end().addClass("selected");
            var groupId = $li.attr("data-id");
            this.setActiveId(groupId);
            this.channelList.reload(groupId);
        }

    };

    function Mediainfo() {
        this.mediaLoadingThreadId = -1;
        this.programAndAudio = {};
        this.loadingCount = 0;
        this.maxLoadingCount = 15;
        this.loadingPeriodSeconds = 300;
        this.url = null;
        this.port=null;
    }

    Mediainfo.prototype = {

        setUrl: function (url) {
            this.url = url;
        },
        setPort:function(port){
          this.port=port;
        },
        stop: function () {
            if (this.mediaLoadingThreadId > 0) {
                clearInterval(this.mediaLoadingThreadId);
                this.mediaLoadingThreadId = -1;
            }
        },

        get: function () {
            var loadingDiv = $("#media-loading-div");
            if (loadingDiv.is(":visible")) {
                return;
            }
            var channelForm = $("#channel-form");
            $.clearValidateError(channelForm);
            var formValidate = channelForm.validate();
            formValidate.resetForm();

            this.stop();

            $("#programId").empty();
            $("#audioId").empty();
            loadingDiv.empty().append("读取媒体信息中<span id='media-loading'></span>").show();

            var that = this;
            this.mediaLoadingThreadId = setInterval(function () {
                var loading = $("#media-loading").append(".");
                that.loadingCount += 1;
                if (that.loadingCount == that.maxLoadingCount) {
                    that.loadingCount = 0;
                    loading.empty();
                }
            }, this.loadingPeriodSeconds);

            var url = this.url;
            var protocol = $("#protocol").val();
            if (protocol == "sdi") {
                $("#programId").empty();
                $("#audioId").empty();
                var port=$("#port").val()
                $.get("/channel/sdi/"+port, $.proxy(function (result) {
                    if(result.code ==0){
                        var channelInfo=result.channelInfo;
                        $("#ip").val("sdi://127.0.0.1:"+port)
                        $("#programId").append('<option value="-1">' + channelInfo.vcodec + '</option>');
                        $("#audioId").append('<option value="-1">' + channelInfo.acodec + '</option>');
                        loadingDiv.hide();
                    }else{
                        loadingDiv.empty().append("<span id='media-loading' style='color: red'>媒体信息解析失败</span>");
                    }
                }, this));

            } else {
                $.getJSON('mediainfo?r=' + Math.random(), {"ip": this.url}, function (json) {

                    if (that.url == url) {
                        that.stop();

                        if (json && json.code == 0) {
                            that.programAndAudio = json.m;
                            that.setProgram();
                        } else {
                            that.programAndAudio = {};
                        }

                        if ($.isEmptyObject(that.programAndAudio)) {
                            loadingDiv.empty().append("<span id='media-loading' style='color: red'>媒体信息解析失败</span>");
                        } else {
                            loadingDiv.hide();
                        }

                    }

                });
            }

        },

        set: function () {
            var ip = $("#ip"), ipVal = ip.val();

            if (ipVal && !ip.hasClass("error")) {
                if (ipVal == this.url) {
                    this.setProgram();
                } else {
                    $("#media-loading-div").hide();
                    this.setUrl(ipVal);
                    this.get();
                }
            }
        },

        setProgram: function () {
            if (this.programAndAudio && !$.isEmptyObject(this.programAndAudio)) {
                var $programEl = $("#programId"), defaultProgramId = $("#defaultProgramId").val(), curPa;
                $programEl.empty();
                for (var i = 0; i < this.programAndAudio.length; i++) {
                    curPa = this.programAndAudio[i];
                    $programEl.append('<option value="' + curPa.programId + '">' + curPa.programName + '</option>');
                }
                if (defaultProgramId) {
                    $programEl.val(defaultProgramId);
                    $("#defaultProgramId").remove();
                } else {
                    $programEl.get(0).selectedIndex = 0;
                }
                $programEl.change();
            }
        },

        setAudio: function (index) {
            if (this.programAndAudio && !$.isEmptyObject(this.programAndAudio)) {
                var $audioEl = $("#audioId"), curPa = this.programAndAudio[index], defaultAudioId = $("#defaultAudioId").val(), curAudio;
                $audioEl.empty();
                if (curPa && curPa["audios"]) {
                    for (var i = 0; i < curPa["audios"].length; i++) {
                        curAudio = curPa["audios"][i];
                        $audioEl.append('<option value="' + curAudio.id + '">' + curAudio.name + '</option>');
                    }
                }

                if (defaultAudioId) {
                    $audioEl.val(defaultAudioId);
                    $("#defaultAudioId").remove();
                } else {
                    $audioEl.get(0).selectedIndex = 0;
                }
            }
        }
    };


    function Channel(group, mediainfo, channelList) {
        this.group = group;
        this.mediaInfoObj = mediainfo;
        this.channelList = channelList;
        this.cdDefaultSeconds = 15;
        this.sdDefNotifySeconds = 600;
        this.sdDefWarningMs = 5000;
        this.sdDefWarningCount = 200;
        this.mobileDefaultConfig = {
            "sd": {videoBitrate: "256", audioBitrate: "64", width: "480", height: "320", type: "0", deinterlace: false},
            "hd": {videoBitrate: "768", audioBitrate: "64", width: "0", height: "0", type: "1", deinterlace: false}
        };
        this._supportedProtocols = ["udp", "http", "rtsp", "rtmp"];
        this.message = new sv.Message({
            message: {
                "0": "频道启动成功",
                "100": "系统处理失败",
                "103": "当前记录已被其他用户锁定",
                "104": "当前无可用的服务器",
                "108": "启动/停止频道相关任务时超时",
                "109": "录制存储不存在，请检查存储是否设置",
                "2101": "当前频道不存在"
            }
        });
        this.codeMessage = {
            "0": "配置错误",
            "1": "该名称已存在",
            "2": "该频道已存在",
            "4": "该频道ID已经存在"
        };
        this.uploadSdpMessage = {
            "-1": "请选择一个sdp文件上传",
            "0": "上传成功",
            "1": "sdp 文件上传失败，请重试"
        };

        this.oldChannel = {};
    }

    Channel.prototype = {
        uploadsdp: function () {
            var sdp = $("#sdp").prop("files");
            if (sdp.length == 0) {
                $("#sdp-error-message").text(this.uploadSdpMessage["-1"]);
                return;
            }
            var that = this;
            var formData = new FormData();

            formData.append('file', sdp[0]);

            $.ajax({
                    type: 'POST',
                    url: 'uploadSdp',
                    data: formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                })
                .done(function (data, textStatus, jqXhr) {
                    if (data.code != 0) {
                        $("#sdp-error-message").text(that.uploadSdpMessage[data.code]);
                    } else {
                        $("#sdp-error-message").text(that.uploadSdpMessage[0]);
                        $("#ip").val(data.url);
                        that.mediaInfoObj.set();
                    }
                })
                .fail(function (jqXhr, textStatus, errorThrown) {
                    $("#sdp-error-message").text(that.uploadSdpMessage["1"]);
                });

        },

        save: function () {
            var channelForm = $("#channel-form");
            var validator = channelForm.validate();
            if (channelForm.valid()) {

                var silenceSeconds = $("#channel-form").find("#silenceSeconds");
                if ((false == silenceSeconds.prop("disabled")) && ("" == $("#channel-form").find("#silenceThreshold").val()))
                    $("#channel-form").find("#silenceThreshold").val("-100");
                var enableBoomSonic = $("#channel-form").find("#enableBoomSonic");
                if ((false == enableBoomSonic.prop("disabled")) && ("" == $("#channel-form").find("#boomSonicThreshold").val()))
                    $("#channel-form").find("#boomSonicThreshold").val("-10");

                var lowVolumeSeconds = $("#channel-form").find("#lowVolumeSeconds");
                if ((false == lowVolumeSeconds.prop("disabled")) && ("" == $("#channel-form").find("#lowVolumeSeconds").val()))
                    $("#channel-form").find("#lowVolumeThreshold").val("-50");
                var loudVolumeSeconds = $("#channel-form").find("#loudVolumeSeconds");
                if ((false == loudVolumeSeconds.prop("disabled")) && ("" == $("#channel-form").find("#loudVolumeSeconds").val()))
                    $("#channel-form").find("#loudVolumeThreshold").val("-10");

                var $deinterlace = channelForm.find("#mobile-hd-deinterlace"),
                    isDeinterlaceChecked = $deinterlace.is(":checked");
                $deinterlace.val(isDeinterlaceChecked ? 1 : 0);


                var isDisabled = $("#ip").prop("disabled");
                if (isDisabled) {
                    $("#ip").prop("disabled", false);
                }
                var data = $("#channel-form").serializeArray();
//                console.log(channelForm.find("#mobile-hd-deinterlace").is(":checked"));
                if (this.group.getActiveId() && this.group.getActiveId() != -1) {
                    data.push({"name": "group.id", "value": this.group.getActiveId()});
                }

                if (isDisabled) {
                    $("#ip").prop("disabled", true);
                }

                $(".channel-tag-name").each(function (index) {
                    data.push({"name": "tags[" + index + "].name", "value": $(this).val()});
                })

                $("#loading-dialog").render("loadingTemplate").modal({
                    showClose: false,
                    clickClose: false
                });

                var isSave = !($("#channel-form").find("input[name='id']").val());
                $.post("save", data, $.proxy(function (result) {
                    this.doSave(result, validator, isSave);
                }, this));
            }
        },

        doSave: function (result, validator, isSave) {
            if (result.id) {
                $(".select-all").prop("checked", false);
                var isChannelRunning = this.channelList.isRunning(result.id);
                this.group.reload();
                var mobileEnable = $("#channel-form").find("input[name='isSupportMobile']:checked").length > 0,
                    enableRecord = $("#channel-form").find("input[name='enableRecord']:checked").length > 0,
                    enableContentDetect = $("#channel-form").find("input[name='enableContentDetect']:checked").length > 0,
                    that = this;
                if (!isChannelRunning || !result.channelSame || this.oldChannel.isSupportMobile != mobileEnable
                    || this.oldChannel.enableContentDetect != enableContentDetect
                    || this.oldChannel.enableRecord != enableRecord) {
                    sv.ajax.post('/task/channel', {"channelId": result.id})
                        .then(function (data) {
                            if (isSave) {
                                if (mobileEnable || enableRecord) {
                                    sv.prompt.succeed2(that.message.get(data.code));
                                }
                            }
                        })
                        .fail(function (jqXhr) {
                            if (isSave) {
                                sv.prompt.error2(that.message.get(sv.ajax.getErrorCode(jqXhr)));
                            }
                        })
                        .always(function () {
                            $.modal.close();
                        });
                } else {
                    $.modal.close();
                }
            } else if (result.code) {
                $.modal.close();
                $("#add-channel-dialog").modal({
                    showClose: false,
                    clickClose: false
                });
                if (result.code == 1) {
                    validator.showErrors({
                        "name": this.codeMessage[result.code]
                    })
                } else if (result.code == 2) {
                    validator.showErrors({
                        "programId": this.codeMessage[result.code]
                    })
                }
                else if (result.code == 4) {
                    validator.showErrors({
                        "origchannelid": this.codeMessage[result.code]
                    })
                }
            }
        },

        show: function (channel) {
            if (!channel) {
                channel = {};
                channel.disabled = true;
            }

            if (!channel.enableRecord && !channel.enableTriggerRecord) {
                channel.disableRecord = true;
            }
            $("#add-channel-dialog").render("addChannelTemplate", channel).modal({
                showClose: false,
                clickClose: false
            });
            this.mediaInfoObj.setUrl(null);
            this.oldChannel = {};
            if (channel) {
                this.oldChannel = channel;
                var url = channel.protocol;
                var defaultPort = channel.port;
                if (url == "sdi") {
                    $("#ip").prop("disabled", true);
                    $.ajax({
                        type: "GET",
                        url: "/channel/sdi/counts",
                        async: false,
                        success: function (result) {
                            var ports = result.ports;
                            $("#port").empty()
                            for (var i = 0; i < ports.length; i++) {
                                if (defaultPort == ports[i]) {
                                    $("#port").append('<option  value="' + ports[i] + '" selected>' + ports[i] + '</option>');
                                } else {
                                    $("#port").append('<option value="' + ports[i] + '">' + ports[i] + '</option>');
                                }

                            }
                        }
                    });
                    $("#sdi-div").show();
                }
                if (!channel.isSupportMobile) {
                    this.toggleMobileConfig();
                }
                if (!channel.enableSignalDetectByType) {
                    this.toggleSignalDetect();
                }
                if (!channel.enableContentDetect) {
                    this.toggleContentDetect();
                }
                this.toggleAlarmTime();
            } else {
                $("#isSupportMobile").prop("checked", true);
                this.toggleContentDetect();
                this.toggleMobileConfig();
                this.toggleSignalDetect();
                this.toggleAlarmTime();
                $("#ip").prop("disabled", true);
            }
            if (channel && channel.ip) {
                this.mediaInfoObj.set();
            }
        },

        load: function (id, func) {
            $.getJSON("get", {id: id, r: Math.random()}, function (result) {
                func(result);
            });
        },

        getChecked: function () {
            return $("input[name='channel-ids']:checked");
        },

        getCheckedIdsArr: function () {
            var checkedChannels = this.getChecked();
            var params = [];
            if (checkedChannels.length > 0) {
                for (var i = 0, len = checkedChannels.length; i < len; i++) {
                    var channel = {};
                    channel.id = $(checkedChannels.get(i)).val();
                    params[i] = channel;
                }
                return params;
            }
            return params;
        },

        getCheckedIdsArr2: function () {
            var checkedChannels = this.getChecked();
            var params = [];
            if (checkedChannels.length > 0) {
                for (var i = 0, len = checkedChannels.length; i < len; i++) {
                    params[i] = $(checkedChannels.get(i)).val();
                }
                return params;
            }
            return params;
        },

        doWithChecked: function (url, data, hideModal) {
            $.post(url, data, $.proxy(function () {
                this.group.reload();
                if (hideModal) {
                    $.modal.close();
                }
            }, this));
        },

        remove: function () {
            var ids = this.getCheckedIdsArr();
            if (ids.length > 0) {
                this.doWithChecked("delete", {channelsStr: $.toJSON(ids)});
            }
        },

        move: function () {
            var ids = this.getCheckedIdsArr();
            if (ids.length > 0) {
                this.doWithChecked("move", {channelsStr: $.toJSON(ids), groupId: $("#moveToChannel").val()}, true);
            }
        },

        startstop: function (url) {
            var ids = this.getCheckedIdsArr2();
            if (ids.length > 0) {

                $("#loading-dialog").render("loadingTemplate2").modal({
                    showClose: false,
                    clickClose: false
                });

                this.doWithChecked(url, {channelsStr: $.toJSON(ids)}, true);
            }
        },

        setUrl: function (str) {
            var protocolIndex = _.indexOf(this._supportedProtocols, str),
                isSupportedProtocol = protocolIndex > -1,
                urlPrefix = isSupportedProtocol ? this._supportedProtocols[protocolIndex] + "://" : "";
            $("#ip").val(urlPrefix).prop("disabled", !isSupportedProtocol);
            if (str == "rtp") {
                $("#ip").val("").prop("disabled", true);
                $("#sdp-upload").show();
            } else {
                $("#sdp-upload").hide();
            }
            if (str == "sdi") {
                $("#ip").val("").prop("disabled", true);
                $.get("/channel/sdi/counts", $.proxy(function (result) {
                    var ports = result.ports;
                    $("#port").empty()
                    for (var i = 0; i < ports.length; i++) {
                        $("#port").append('<option value="' + ports[i] + '">' + ports[i] + '</option>');
                    }
                }, this));

                $("#sdi-div").show();
            } else {
                $("#sdi-div").hide();
            }
            this.mediaInfoObj.stop();
            $("#programId").empty();
            $("#audioId").empty();
            $("#media-loading-div").hide();
        },
        getSdiChannelInfo: function (port) {
            $("#programId").empty();
            $("#audioId").empty();
            $.get("/channel/sdi/" + port, $.proxy(function (result) {
                if (result.code == 0) {
                    var channelInfo = result.channelInfo;
                    $("#ip").val("sdi://127.0.0.1:" + port)
                    $("#programId").append('<option value="-1">' + channelInfo.vcodec + '</option>');
                    $("#audioId").append('<option value="-1">' + channelInfo.acodec + '</option>');

                }
            }, this));
        },
        setContentDetectDefaultValue: function () {
            this.setDefaultOrDisableContentDetect(2);
        },

        disableContentDetect: function () {
            this.setDefaultOrDisableContentDetect(1);
        },

        setDefaultOrDisableContentDetect: function (op) {
            var $parent = $(".content-analysis");
            $parent.find("#blackSeconds,#silenceSeconds,#noFrameSeconds,#loudVolumeSeconds").prop("disabled", op == 1).val(op == 1 ? "" : this.cdDefaultSeconds);
            $parent.find("#lowVolumeSeconds").prop("disabled", op == 1).val(op == 1 ? "" : 0);

            //$parent.find("input[type='text']").prop("disabled", op == 1).val(op == 1 ? "" : this.cdDefaultSeconds);
            //$parent.find("#greenSeconds").val("").prop("disabled", true);

            var $enableSilence = $parent.find("#silenceSeconds").prop("disabled");
            $parent.find("#silenceThreshold").prop("disabled", $enableSilence).val($enableSilence ? "" : "-100");

            $parent.find('#enableBoomSonic').prop('disabled', op == 1).prop('checked', op != 1);
            $parent.find("#boomSonicThreshold").prop("disabled", op == 1).val(op == 1 ? "" : "-10");

            var $enableLowVolume = $parent.find("#lowVolumeSeconds").prop("disabled");
            $parent.find("#lowVolumeThreshold").prop("disabled", $enableLowVolume).val($enableLowVolume ? "" : "-50");
            var $enableLoudVolume = $parent.find("#loudVolumeSeconds").prop("disabled");
            $parent.find("#loudVolumeThreshold").prop("disabled", $enableLoudVolume).val($enableLoudVolume ? "" : "-10");
        },

        setSignalDetectDefaultValue: function () {
            this.setDefaultOrDisableSignalDetect(2);
        },

        disableSignalDetect: function () {
            this.setDefaultOrDisableSignalDetect(1);
        },

        updateSignalDetectStatus: function () {

            var $parent = $(".signal-analysis");
            var signalBrokenEnabledFlag = $parent.find("#warningSignalBrokenEnabled").prop("checked");
            $parent.find("#warningSignalBrokenTimeout").prop("disabled", !signalBrokenEnabledFlag).val(!signalBrokenEnabledFlag ? "" : this.sdDefWarningMs);

            var progidLossEnabledFlag = $parent.find("#warningProgidLossEnabled").prop("checked");
            $parent.find("#warningProgidLossTimeout").prop("disabled", !progidLossEnabledFlag).val(!progidLossEnabledFlag ? "" : this.sdDefWarningMs);

            var videoLossEnabledFlag = $parent.find("#warningVideoLossEnabled").prop("checked");
            $parent.find("#warningVideoLossTimeout").prop("disabled", !videoLossEnabledFlag).val(!videoLossEnabledFlag ? "" : this.sdDefWarningMs);

            var audioLossEnabledFlag = $parent.find("#warningAudioLossEnabled").prop("checked");
            $parent.find("#warningAudioLossTimeout").prop("disabled", !audioLossEnabledFlag).val(!audioLossEnabledFlag ? "" : this.sdDefWarningMs);

            var ccErrorEnabledFlag = $parent.find("#warningCcErrorEnabled").prop("checked");
            $parent.find("#warningCcErrorTimeout").prop("disabled", !ccErrorEnabledFlag).val(!ccErrorEnabledFlag ? "" : this.sdDefWarningMs);
            $parent.find("#warningCcErrorCount").prop("disabled", !ccErrorEnabledFlag).val(!ccErrorEnabledFlag ? "" : this.sdDefWarningCount);

        },

        setDefaultOrDisableSignalDetect: function (op) {
            var $parent = $(".signal-analysis");
            $parent.find("#notifyInterval").prop("disabled", op == 1).val(op == 1 ? "" : this.sdDefNotifySeconds);
            $parent.find("#warningSignalBrokenEnabled,#warningProgidLossEnabled,#warningVideoLossEnabled,#warningAudioLossEnabled,#warningCcErrorEnabled").prop("disabled", op == 1).prop("checked", op != 1);
            //$parent.find("#warningSignalBrokenTimeout,#warningProgidLossTimeout,#warningVideoLossTimeout,#warningAudioLossTimeout,#warningCcErrorTimeout").prop("disabled", op == 1).prop("checked",op != 1);
            //$parent.find("#warningSignalBrokenTimeout,#warningProgidLossTimeout,#warningVideoLossTimeout,#warningAudioLossTimeout,#warningCcErrorTimeout").prop("disabled", op == 1).val(op == 1 ? "" : this.sdDefWarningMs);

            this.updateSignalDetectStatus();

        },

        toggleAlarmTime: function () {

            var isChecked1 = $("#enableTime1").prop("checked");
            if (isChecked1) {
                $("#alarmStartTime1").prop("disabled", false);
                $("#alarmEndTime1").prop("disabled", false);
                $("#enableTime1").val("true");
            } else {
                $("#alarmStartTime1").prop("disabled", true);
                $("#alarmEndTime1").prop("disabled", true);
                $("#enableTime1").val("false");
            }

            var isChecked2 = $("#enableTime2").prop("checked");
            if (isChecked2) {
                $("#alarmStartTime2").prop("disabled", false);
                $("#alarmEndTime2").prop("disabled", false);
                $("#enableTime2").val("true");
            } else {
                $("#alarmStartTime2").prop("disabled", true);
                $("#alarmEndTime2").prop("disabled", true);
                $("#enableTime2").val("false");
            }

            var isChecked3 = $("#enableTime3").prop("checked");
            if (isChecked3) {
                $("#alarmStartTime3").prop("disabled", false);
                $("#alarmEndTime3").prop("disabled", false);
                $("#enableTime3").val("true");
            } else {
                $("#alarmStartTime3").prop("disabled", true);
                $("#alarmEndTime3").prop("disabled", true);
                $("#enableTime3").val("false");
            }


            var isChecked4 = $("#enableTime4").prop("checked");
            if (isChecked4) {
                $("#alarmStartTime4").prop("disabled", false);
                $("#alarmEndTime4").prop("disabled", false);
                $("#enableTime4").val("true");
            } else {
                $("#alarmStartTime4").prop("disabled", true);
                $("#alarmEndTime4").prop("disabled", true);
                $("#enableTime4").val("false");
            }

            var isChecked5 = $("#enableTime5").prop("checked");
            if (isChecked5) {
                $("#alarmStartTime5").prop("disabled", false);
                $("#alarmEndTime5").prop("disabled", false);
                $("#enableTime5").val("true");
            } else {
                $("#alarmStartTime5").prop("disabled", true);
                $("#alarmEndTime5").prop("disabled", true);
                $("#enableTime5").val("false");
            }

        },
        toggleContentDetect: function () {
            var isChecked = $("#enableContentDetect").prop("checked");
            if (isChecked) {
                this.setContentDetectDefaultValue();
            } else {
                this.disableContentDetect();
            }
        },

        toggleSignalDetect: function () {
            var isChecked = $("#enableSignalDetectByType").prop("checked");
            if (isChecked) {
                this.setSignalDetectDefaultValue();
            } else {
                this.disableSignalDetect();
            }
        },

        toggleMobileConfig: function () {
            var isChecked = $("#isSupportMobile").prop("checked");
            if (isChecked) {
                $(".mobile").find(":input").prop("disabled", false);
                var configs = ['sd', 'hd'];
                for (var k in configs) {
                    $("#mobile-" + configs[k] + "-audioBitrate").val(this.mobileDefaultConfig[configs[k]].audioBitrate);
                    $("#mobile-" + configs[k] + "-width").val(this.mobileDefaultConfig[configs[k]].width);
                    $("#mobile-" + configs[k] + "-height").val(this.mobileDefaultConfig[configs[k]].height);
                    $("#mobile-" + configs[k] + "-type").val(this.mobileDefaultConfig[configs[k]].type);
//                    $("#mobile_" + configs[k] + "-deinterlace").val(this.mobileDefaultConfig[configs[k]].deinterlace ? 1 : 0);
                    $("#mobile-" + configs[k] + "-deinterlace").prop("checked", this.mobileDefaultConfig[configs[k]].deinterlace);

                    if (configs[k] == 'hd') {
                        if (this.mediaInfoObj.programAndAudio.length > 0) {
                            var vwidth = this.mediaInfoObj.programAndAudio[0].vwidth;
                            var vheight = this.mediaInfoObj.programAndAudio[0].vheight;
                            if (vwidth && vheight && vheight > 0 && vwidth > 0) {
                                $("#mobile-" + configs[k] + "-width").val(vwidth);
                                $("#mobile-" + configs[k] + "-height").val(vheight);
                            }
                            this.updateVideobiterates();
                        }
                    } else {
                        $("#mobile-" + configs[k] + "-videoBitrate").val(this.mobileDefaultConfig[configs[k]].videoBitrate);
                    }
                }
            } else {
                $(".mobile").find(".line").find(":input").val("").prop("disabled", true);
            }
        },

        updateVideobiterates: function () {
            $("#mobile-hd-deinterlace").prop("disabled", false);
            var vwidth = $("#mobile-hd-width").val();
            var vheight = $("#mobile-hd-height").val();
            if (vwidth > 0 && vheight > 0) {
                var vbitrates = this.calvideobitrates(vwidth, vheight);
                $("#mobile-hd-videoBitrate").val(vbitrates);
            } else if (vwidth == 0 && vheight == 0) {
                $("#mobile-hd-videoBitrate").val(0);
                $("#mobile-hd-deinterlace").prop("checked", false);
                $("#mobile-hd-deinterlace").prop("disabled", true);
            }
            else
                this.setDefaultVideobiterateHd();
        },

        setDefaultVideobiterateHd: function () {
            var config = "hd";
            if (this.mediaInfoObj.programAndAudio.length > 0) {
                var vwidth = this.mediaInfoObj.programAndAudio[0].vwidth;
                var vheight = this.mediaInfoObj.programAndAudio[0].vheight;
                if (vwidth && vheight && vheight > 0 && vwidth > 0) {
                    var vbitrates = this.calvideobitrates(vwidth, vheight);
                    $("#mobile-" + config + "-videoBitrate").val(vbitrates);
                }
                else {
                    $("#mobile-" + config + "-videoBitrate").val(this.mobileDefaultConfig[config].videoBitrate);
                }
            }
            else {
                $("#mobile-" + config + "-videoBitrate").val(this.mobileDefaultConfig[config].videoBitrate);
            }
        },

        calvideobitrates: function (width, height) {
            return Math.ceil((width * height * 2) / 1024);
        }
    };

    function Thumbnail() {
        this.threadId = -1;
    }

    Thumbnail.prototype = {

        start: function (channelId) {
            var that = this;
            this.threadId = setInterval(function () {
                that.load(channelId);
            }, 5000);
        },

        stop: function () {
            if (this.threadId != -1) {
                clearInterval(this.threadId);
                this.threadId = -1;
            }
        },

        load: function (channelId) {
            $("#image-samples-info img").attr("src", "frame/" + channelId + "/1?r=" + Math.random());
        }
    };

    function ChannelInfo() {
    }

    ChannelInfo.prototype = {

        setChannelInfo: function (info) {
            if (info) {
                $("#iSrcName .Val").html(info.channelInfo.name);
                if (info.output) {
                    $("#iOutUrl").show();
                    $("#iOutUrl .Val").html(info.output);
                } else {
                    $("#iOutUrl").hide();
                }
                if (info.screenInfo) {
                    $("#screen-position-name").show()
                    var screenList = info.screenInfo;
                    var str = "";
                    for (var i = 0; i < screenList.length; i++) {
                        str = str + "<span>" + screenList[i] + "</span>" + "</br>"
                    }
                    $("#screen-position-name .Val").html(str);
                } else {
                    $("#screen-position-name").hide();
                }
                $("#iSrcUrl .Val").html(info.source);
                $("#iSrcContainer .Val").html(info.channelInfo.container);
                $("#VCodec").html(info.channelInfo.vcodec);
                $("#VBitrate").html(info.channelInfo.vbitrate);
                $("#VFramerate").html(info.channelInfo.vframerate);
                $("#VResolution").html(info.channelInfo.vresolution);
                $("#VAspectRadio").html(info.channelInfo.vratio);
                $("#ALanguage").html(info.channelInfo.alanguage);
                $("#ACodec").html(info.channelInfo.acodec);
                $("#ABitrate").html(info.channelInfo.abitrate);
                $("#ASamplerate").html(info.channelInfo.asamplerate);
                $("#AChannels").html(info.channelInfo.achannels);
                $("#ABitDepth").html(info.channelInfo.abitdepth);
            } else {
                $("#iSrcName .Val").html("");
                $("#iSrcUrl .Val").html("");
                $("#iSrcContainer .Val").html("");
                $("#VCodec").html("");
                $("#VBitrate").html("");
                $("#VFramerate").html("");
                $("#VResolution").html("");
                $("#VAspectRadio").html("");
                $("#ALanguage").html("");
                $("#ACodec").html("");
                $("#ABitrate").html("");
                $("#ASamplerate").html("");
                $("#AChannels").html("");
                $("#ABitDepth").html("");
            }
        },

        getChannelInfo: function (channelId) {
            this.setChannelInfo();
            var that = this;
            $.getJSON('channelInfo', {channelId: channelId, r: Math.random()}, function (info) {
                that.setChannelInfo(info);
            });
        }
    };


    function ChannelList() {
        this.thumbnail = new Thumbnail();
        this.channelInfo = new ChannelInfo();
        this.status = {
            "RUNNING": "运行中",
            "STOP": "停止"
        };
    }

    ChannelList.prototype = {

        reload: function (groupId) {
            var that = this;
            var url = groupId && groupId > 0 ? "channels" : "unGroupedChannels";
            $.getJSON(url + "?r=" + Math.random() + "&groupId=" + groupId, function (channels) {
                $("#channels-table > tbody").render("channelsTemplate", {"channels": channels});
                $(".detail").click(function (event) {
                    event.preventDefault();
                    event.stopPropagation();
                    if ($(this).hasClass("more")) {
                        $(this).removeClass("more");
                        $("#image-samples-info").hide();
                        $("#image-samples-info img").each(function () {
                            $(this).attr("src", "");
                        });
                        that.thumbnail.stop();
                    } else {
                        $(".detail").removeClass("more");
                        $(this).addClass("more");
                        $("#image-samples-info").show();
                        var channelId = $(this).closest("tr").find("input[name='channel-ids']").val();
                        that.channelInfo.getChannelInfo(channelId);
                        that.thumbnail.load(channelId);
                        that.thumbnail.stop();
                        that.thumbnail.start(channelId);
                        $("#image-samples-info").insertAfter($(this).parent().parent().parent()[0]);
                    }
                    $("#channel-list-content").getNiceScroll().resize();
                });

                that.initStatus();

                that.syncStatus();
                updateActionBar();
            });
        },

        syncStatus: function () {
            var channelRootObj = $("#channels-table"),
                channelIdObjs = channelRootObj.find(".select-one");

            if (channelIdObjs.length > 0) {
                var curChannelObj, ids = [];
                for (var i = 0; i < channelIdObjs.length; i++) {
                    curChannelObj = $(channelIdObjs[i]);
                    if (curChannelObj.val()) {
                        ids.push(curChannelObj.val());
                    }
                }
                if (ids.length > 0) {
                    var that = this;
                    sv.ajax.getJSON('/task/getChannelTasks', {ids: ids}, function (res) {
                        if (res && res.code == 0) {
                            var curRes;
                            for (var j = 0; j < res.r.length; j++) {
                                curRes = res.r[j];
                                curChannelObj = channelRootObj.find("input[name='channel-ids'][value='" + curRes.referenceId + "']");
                                if (curChannelObj.length > 0) {
                                    curChannelObj.parent()
                                        .parent()
                                        .find(".channel-status")
                                        .text(curRes.status && $.trim(curRes.status) && curRes.status == 'RUNNING'
                                            ? that.status['RUNNING']
                                            : that.status['STOP']);
                                }
                            }
                        }
                    });
                }
            }
        },

        initStatus: function () {
            var channelRootObj = $("#channels-table"),
                channelIdObjs = channelRootObj.find(".select-one");

            if (channelIdObjs.length > 0) {
                var curChannelObj;
                var that = this;
                for (var i = 0; i < channelIdObjs.length; i++) {
                    curChannelObj = $(channelIdObjs[i]);
                    curChannelObj.parent()
                        .parent()
                        .find(".channel-status")
                        .text(that.status['STOP']);
                }
            }
        },

        getStatus: function (id) {
            var channelIdObj = $("#channels-table").find(".select-one[value='" + id + "']");
            if (channelIdObj.length > 0) {
                var status = channelIdObj.parent().parent().find(".channel-status").text();
                return $.trim(status) == this.status["RUNNING"] ? this.status["RUNNING"] : this.status["STOP"];
            }
            return this.status["STOP"];
        },

        isRunning: function (id) {
            return this.getStatus(id) == this.status['RUNNING'];
        }
    };

    var updateStartstopstatus = function () {
        var $channelObjs = $("input[name='channel-ids']:checked");

        if ($channelObjs.length < 1)
            return;

        var isRunningCount = 0;
        $channelObjs.each(function () {
            if (channelList.isRunning($(this).val())) {
                isRunningCount++;
            }
        })
        var isStopCount = $channelObjs.length - isRunningCount;

        if (isRunningCount < 1) {
            $("#stop-channel-btn").parent().addClass("disable");
        } else {
            $("#stop-channel-btn").parent().removeClass("disable");
        }
        if (isStopCount != 1 || $channelObjs.length != 1) {
            $("#start-channel-btn").parent().addClass("disable");
        } else {
            $("#start-channel-btn").parent().removeClass("disable");
        }
    };

    var updateActionBar = function () {
        $(".select-all").prop("checked",
            $(".select-one[type='checkbox']:checked").length > 0 && $(".select-one[type='checkbox']:checked").length === $(".select-one[type='checkbox']").length);
        if ($(".select-one[type='checkbox']:checked").length > 1) {
            $("#edit-channel-btn").parent().removeClass("disable");
            $("#delete-channel-btn").parent().removeClass("disable");
            $("#move-channel-btn").parent().removeClass("disable");
            $("#stop-channel-btn").parent().removeClass("disable");
            $("#start-channel-btn").parent().removeClass("disable");

            updateStartstopstatus();
        } else if ($(".select-one[type='checkbox']:checked").length == 1) {
            $("#edit-channel-btn").parent().removeClass("disable");
            $("#delete-channel-btn").parent().removeClass("disable");
            $("#move-channel-btn").parent().removeClass("disable");
            $("#stop-channel-btn").parent().removeClass("disable");
            $("#start-channel-btn").parent().removeClass("disable");

            updateStartstopstatus();

        } else {
            $("#edit-channel-btn").parent().addClass("disable");
            $("#delete-channel-btn").parent().addClass("disable");
            $("#move-channel-btn").parent().addClass("disable");
            $("#stop-channel-btn").parent().addClass("disable");
            $("#start-channel-btn").parent().addClass("disable");
        }
    };

    var channelList = new ChannelList(),
        group = new Group(channelList),
        mediainfo = new Mediainfo(),
        channel = new Channel(group, mediainfo, channelList);

    var init = function () {
        setInterval(function () {
            channelList.syncStatus();
        }, 3000);
        $("#inc_tab").find("li").removeClass("active");
        $("#inc_tab").find("#tb5").addClass("active");
        group.reload();
        $(".select-all").click(function () {
            $(".select-one").prop("checked", this.checked);
            updateActionBar();
        });

        $("#channels-table").on('click', ".select-one", function () {
            updateActionBar();
        });


        $("#channel-lists-nav,#channel-list-content").niceScroll();

        $("#group-add-btn").click(function (event) {
            event.preventDefault();
            $("#add-channel-list-dialog").find(".dialog-content").render("addGroupTemplate", {});
            $(this).modal({showClose: false, clickClose: false});
        });

        $("#add-channel-list-dialog")
            .on('click', "#add-channel-list-cancel-btn", function () {
                $.modal.close();
            })
            .on('click', "#add-channel-list-ok-btn", function () {
                group.save();
            })
            .on("keypress", $.proxy(function (event) {
                if (event.which == 13) {
                    $("#add-channel-list-ok-btn").trigger('click');
                    event.preventDefault();
                }
            }, this))
            .cleanTipsyOnModalClose();

        $("#channel-lists-nav").on("click", "li", function () {
            var top = ($(this).prevAll().length + 1) * $(this).outerHeight(false) - $("#channel-lists-nav").height();
            if (top > $("#channel-lists-nav").scrollTop()) {
                $("#channel-lists-nav").scrollTop(top);
            }
            group.active($(this));
        }).on("click", ".remove-btn", function () {
            var id = $(this).parent().parent().attr("data-id");
            $.post("checkGroup", {"id": id}, function (result) {
                if (result == true) {
                    sv.prompt.showConfirm2('删除组将会同时删除组下的所有频道，确认删除吗？', function () {
                        group.remove(id);
                    });
                } else {
                    sv.prompt.error2("不能删除运行频道所在的频道组")
                }
            })

        });

        $("#add-channel-btn").click(function (event) {
            event.preventDefault();
            channel.show();
        });

        var exclusiveSelect = function (event) {
            var selected = $(event.currentTarget).prop("checked");
            if (selected) {
                $(event.currentTarget).siblings().filter("input[type='checkbox']").prop("checked", false);
            } else {
                $(event.currentTarget).prop("checked", true);
            }
        };

        var addTag = function () {
            $("#channel-tag-list").append('<div class="channel-tag"><input type="text" class="channel-tag-name" name="channel-tag-name" value=""/><span class="del-tag-btn">&times;</span></div>');
        };

        var removeTag = function (event) {
            $(event.currentTarget).closest(".channel-tag").remove();
        };

        $("#add-channel-dialog")
            .on("click", "#add-channel-cancel-btn", function () {
                $.modal.close();
            })
            .on("click", "#add-channel-ok-btn", function () {
                channel.save();
            })
            .on("focusout", "#ip", function () {
                var ip = $("#ip").val();
                if (ip && $.inArray(ip, ["udp://", "http://", "rtsp://", "rtmp://"]) == -1) {
                    mediainfo.set();
                }
            })
            .on("change", "#protocol", function () {
                channel.setUrl($(this).children('option:selected').val());
            })
            .on("change", "#port", function () {
                channel.getSdiChannelInfo($(this).children('option:selected').val())
            })
            .on("change", "#programId", function () {
                mediainfo.setAudio(this.selectedIndex);
            })
            .on("change", "#enableContentDetect", function () {
                channel.toggleContentDetect();
            })
            .on("change", "#enableSignalDetectByType", function () {
                channel.toggleSignalDetect();
            })
            .on("change", "#isSupportMobile", function () {
                channel.toggleMobileConfig();
            })
            .on("change", "#enableTime1,#enableTime2,#enableTime3,#enableTime4,#enableTime5", function () {
                channel.toggleAlarmTime();
            })
            .on("change", "#warningSignalBrokenEnabled,#warningProgidLossEnabled,#warningVideoLossEnabled,#warningAudioLossEnabled,#warningCcErrorEnabled", function () {
                channel.updateSignalDetectStatus();
            })
            .on("change", "#disableRecord", function (event) {
                exclusiveSelect(event);
            })
            .on("change", "#enableRecord", function (event) {
                exclusiveSelect(event);
            })
            .on("change", "#enableTriggerRecord", function (event) {
                exclusiveSelect(event);
            })
            .on("change", "#mobile-hd-width,#mobile-hd-height", function () {
                channel.updateVideobiterates();
            })
            .on("click", ".del-tag-btn", removeTag)
            .on("click", "#add-tag-btn", addTag)
            .on("click", "#edit-channels-ok-btn", function () {
                var data = $("#edit-channels-form").serializeArray();

                $(".channel-tag-name").each(function (index) {
                    data.push({"name": "tags[" + index + "].name", "value": $(this).val()});
                })

                for (i = 0; i < channelList.channelIds.length; i++) {
                    data.push({"name": "channelIds[" + i + "]", "value": channelList.channelIds[i]});
                }

                $.post("editChannels", data, function () {
                    group.reload();
                });
                $.modal.close();
            })
            .on("click", "#edit-channels-cancel-btn", function () {
                $.modal.close();
            })
            .on("click", "#sdp-submit", function (event) {
                event.preventDefault();
                channel.uploadsdp();
            })
            .on("keypress", $.proxy(function (event) {
                if (event.which == 13) {
                    $("#add-channel-ok-btn").trigger('click');
                    event.preventDefault();
                }
            }, this));
        ;

        $("#add-channel-dialog").cleanTipsyOnModalClose();

        $("#edit-channel-btn").click(function () {

            var $channelObjs = $("input[name='channel-ids']:checked");
            if ($channelObjs.length == 1) {
                channel.load($channelObjs.val(), $.proxy(channel.show, channel));
            } else {
                channelList.channelIds = [];
                $channelObjs.each(function () {
                    channelList.channelIds.push($(this).val());
                })
                $("#add-channel-dialog").render("editChannelsTemplate", {}).modal({
                    showClose: false,
                    clickClose: false
                });
            }
        });

        $("#delete-channel-btn").click(function () {
            if (!$(this).parent().hasClass("disable")) {
                sv.prompt.showConfirm2('是否删除所选频道？', function () {
                    channel.remove();
                });
            }
        });

        $("#move-channel-btn").click(function () {
            if (!$(this).parent().hasClass("disable")) {
                $("#move-channel-dialog").modal({showClose: false, clickClose: false});
            }
        });

        $("#start-channel-btn").click(function () {
            if (!$(this).parent().hasClass("disable")) {
                sv.prompt.showConfirm3("是否启动所选频道？\n本操作只对支持移动设备的频道生效。", function () {
//                    channel.start();
                    channel.startstop("start");
                });
            }
        });

        $("#stop-channel-btn").click(function () {
            if (!$(this).parent().hasClass("disable")) {
                sv.prompt.showConfirm2('是否停止所选频道？', function () {
//                    channel.stop();
                    channel.startstop("stop");
                });
            }
        });

        $("#move-channel-dialog")
            .on("click", "#move-channel-cancel-btn", function () {
                $.modal.close();
            })
            .on("click", "#move-channel-ok-btn", function () {
                var moveForm = $("#moveForm");
                var validator = moveForm.validate({
                    messages: {
                        "moveToChannel": "没有可用的分组，请先建立分组"
                    }
                });
                if (moveForm.valid()) {
                    channel.move();
                }
            });

        $("#move-channel-dialog").cleanTipsyOnModalClose();

        $("#channel-search-input").on('keyup', function (event) {
            var search = $(event.currentTarget).val();
            $(".channel-item").each(function () {
                var name = $(this).find(".channel-item-name").text() || '';
                if (name.indexOf(search) == -1) {
                    $(this).hide();
                } else {
                    $(this).show();
                }
            });
        })


        updateActionBar();
    };

    return {init: init};

}(jQuery, window.sv));

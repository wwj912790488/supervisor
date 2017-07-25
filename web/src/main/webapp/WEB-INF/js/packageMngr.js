function PackageManager() {

}

PackageManager.prototype = {
    init: function() {
        var _this = this;
        sv.nav.active('tb10');
        this.reloadPackages();
        $(".select-all").click(function () {
            $(".select-one").prop("checked", this.checked);
            _this.updateActionBar();
        });
        $("#package-list-table").on('click', ".select-one", function () {
            _this.updateActionBar();
        });
        $("#add-package-btn").click(function() {
            $("#packageadd .dialog-content").render("addPackageTemplate");
            $("#packageadd").modal({
                showClose: false, clickClose:false
            });
        });

        $("#packageadd").on("click", "#add-package-cancel-btn", $.proxy(function () {
            $.modal.close();
        }, this)).on("click", "#add-package-ok-btn", $.proxy(function () {
            _this.addPackage();
        }, this));

        $("#packageadd").cleanTipsyOnModalClose();


        $("#delete-package-btn").click(function () {
            if(!($("#delete-package-btn").parent().hasClass("disable"))) {
                sv.prompt.showConfirm2('是否删除所选安装包？', function () {
                    _this.deletePackage();
                });
            }
        });
        
        $("#deploy-package-btn").click(function () {
            if(!($("#deploy-package-btn").parent().hasClass("disable"))) {
                sv.prompt.showConfirm2('是否推送当前安装包？', function () {
                    _this.deployPackage();
                });
            }
        });
        
        $("#packageadd")
        .on("submit", "#setup_packge_upload_form", function(e) {
        e.preventDefault();
        var fd = new FormData($(this)[0]);
        _this.uploadPackage(fd);
    });

        this.updateActionBar();
    },
    

    uploadPackage: function(data) {
        var that = this;
        var date = that.getNowFormatDate();
        $.ajax({
            type: 'POST',
            url: 'uploadPackage',
            data: data,
            cache: false,
            processData: false,
            contentType: false,
            success: function(result) {
                if(result.code == 0) {
                    //$("#channel_logo_preview").attr("src", $.common.getRealPath(result.url)).show();
                    $("#uploadPath").val(result.url);
                    $("#fileHash").val(result.md5);
                    $("#uploadDate").val(date);
                    $("#setup_package_submit_message").text("安装包上传成功");
                } else {
                    $("#setup_package_submit_message").text("安装包上传失败");
                }
            },
            error: function(XMLhttpRequest, textStatus, error) {
                $("#setup_package_submit_message").text("安装包上传失败");
            }
        });
    },
    
    updateActionBar: function () {
        $(".select-all").prop("checked",
            $(".select-one[type='checkbox']:checked").length > 0 && $(".select-one[type='checkbox']:checked").length === $(".select-one[type='checkbox']").length);
        
        if ($(".select-one[type='checkbox']:checked").length > 1) {
            $("#deploy-package-btn").parent().addClass("disable");
            $("#delete-package-btn").parent().addClass("disable");
        } else if($(".select-one[type='checkbox']:checked").length == 1) {
            $("#deploy-package-btn").parent().removeClass("disable");
            $("#delete-package-btn").parent().removeClass("disable");
        } else {
            $("#deploy-package-btn").parent().addClass("disable");
            $("#delete-package-btn").parent().addClass("disable");
        }
    },
    
    reloadPackages: function () {
        var _this = this;
        $.getJSON("packages?r=" + Math.random(), function (packages) {
        	$("#package-list-table > tbody").render("packageTemplate", {"packages": packages});
            _this.updateActionBar();
        });
    },
    addPackage: function () {
        var addPackageForm = $("#add-package-form");
        //if (addUserForm.valid()) 
        {
            var data = $("#add-package-form").serializeArray();

            $.post("addPackage", data, $.proxy(function (result) {
                if(!result) {
                    sv.prompt.error2('该版本号已存在！');
                } else {
                    $(".select-all").prop("checked", false);
                    this.reloadPackages();
                    $.modal.close();
                }
            }, this));
        }

    },
    deletePackage: function () {
        var $packageObjs = $("input[name='package-id']:checked");
        if ($packageObjs.length > 0) {
            var showWarning = false;
            var params = [];
            for (var i = 0, len = $packageObjs.length; i < len; i++) {
                var packet = {};
                packet.id = $($packageObjs.get(i)).val();
                params[i] = packet;
            }
            if(!showWarning){
                $.post("deletePackage", {packageInfo: $.toJSON(params)}, $.proxy(function () {
                    this.reloadPackages();
                }, this));
            }else{
                sv.prompt.error2('无法删除当前版本！');
            }
        }
    },
    
    deployPackage: function () {
        var $packageObjs = $("input[name='package-id']:checked");
        if ($packageObjs.length > 0) {
            var showWarning = false;
            var params = [];
            for (var i = 0, len = $packageObjs.length; i < len; i++) {
                var packet = {};
                packet.id = $($packageObjs.get(i)).val();
                params[i] = packet;
            }
            if(!showWarning){
                $.post("deployPackage", {packageInfo: $.toJSON(params)}, $.proxy(function () {
                    this.reloadPackages();
                }, this));
            }else{
                sv.prompt.error2('无法推送当前版本！');
            }
        }
    },
    
    getNowFormatDate:function () {
        var date = new Date();
        var seperator1 = "-";
        var seperator2 = ":";
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
                + " " + date.getHours() + seperator2 + date.getMinutes()
                + seperator2 + date.getSeconds();
        return currentdate;
    }
};

function OPSManager() {

}

OPSManager.prototype = {
    init: function() {
        var _this = this;
        sv.nav.active('tb10');
        this.reloadOPSs();
        $(".select-all").click(function () {
            $(".select-one").prop("checked", this.checked);
            _this.updateActionBar();
        });
        $("#ops-list-table").on('click', ".select-one", function () {
            _this.updateActionBar();
        });

        $("#delete-ops-btn").click(function () {
            if(!($("#delete-ops-btn").parent().hasClass("disable"))) {
                sv.prompt.showConfirm2('是否删除所选设备？', function () {
                    _this.deleteOps();
                });
            }
        });

        this.updateActionBar();
    },

    updateActionBar: function () {
        $(".select-all").prop("checked",
            $(".select-one[type='checkbox']:checked").length > 0 && $(".select-one[type='checkbox']:checked").length === $(".select-one[type='checkbox']").length);
        
        if ($(".select-one[type='checkbox']:checked").length > 1) {
            $("#delete-ops-btn").parent().addClass("disable");
        } else if($(".select-one[type='checkbox']:checked").length == 1) {
            $("#delete-ops-btn").parent().removeClass("disable");
        } else {
            $("#delete-ops-btn").parent().addClass("disable");
        }
    },
    
    reloadOPSs: function () {
        var _this = this;
        $.getJSON("opss?r=" + Math.random(), function (opss) {
        	$("#ops-list-table > tbody").render("opsTemplate", {"opss": opss});
            _this.updateActionBar();
        });
    },

    deleteOps: function () {
        var $opsObjs = $("input[name='ops-id']:checked");
        if ($opsObjs.length > 0) {
            var showWarning = false;
            var params = [];
            for (var i = 0, len = $opsObjs.length; i < len; i++) {
                var ops = {};
                ops.id = $($opsObjs.get(i)).val();
                params[i] = ops;
            }
            if(!showWarning){
                $.post("deleteOps", {opsInfo: $.toJSON(params)}, $.proxy(function () {
                    this.reloadOPSs();
                }, this));
            }else{
                sv.prompt.error2('无法删除当前设备！');
            }
        }
    }
};




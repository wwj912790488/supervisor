/**
 * A plugin combine the validate and tipsy to use tipsy as error message tip of validate plugin.
 */
$(function ($) {
    if (typeof $.fn.validate != 'undefined'
        && typeof $.fn.tipsy != 'undefined') {
        $.validator.setDefaults({
            errorPlacement: function (error, element) {
                if(element.is("select")) {
                    element.attr('not-title', error.text());
                    element.tipsy({gravity: element.attr("tip-gravity") || 's', trigger: 'manual', title: 'not-title'});
                } else {
                    element.attr('title', error.text());
                    element.tipsy({gravity: element.attr("tip-gravity") || 's', trigger: 'manual'});
                }
                element.tipsy('show');
            },
            success: function (label) {
                $(label).each(function () {
                    var forr = $(this).attr('for');
                    var target = $('#' + forr);
                    if (target.length == 0) {
                        target = $("input[name='" + forr + "']");
                    }
                    if (target.hasClass('error')){
                        target.tipsy('hide').removeAttr('original-title');
                    }
                });
            },
            unhighlight: function( element, errorClass, validClass ) {
                if ( element.type === "radio" ) {
                    this.findByName( element.name ).removeClass( errorClass ).addClass( validClass );
                } else {
                    $( element ).removeClass( errorClass ).addClass( validClass );
                }
                var el = $(element);
                if (el.attr('original-title')){
                    el.tipsy('hide').removeAttr('original-title');
                }
            }

        });
        $.validator.addMethod('validIP', function (value) {
            var ip = /^(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))$/;
            return value.match(ip);
        }, '请输入正确的ip地址');

        $.validator.addMethod('validURL', function (value) {
            var ipUdp = /^udp:\/\/(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5])):\d{0,5}$/;
            var ipHttpbyIP = /^http:\/\/(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5])):\d{0,5}/;
            var ipHttpbyStr = /^http:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?$/;
            var ipRtspbyIP = /^rtsp:\/\/(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5])):\d{0,5}/;
            var ipRtspbyStr = /^rtsp:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?$/;
            return value.match(ipUdp) || value.match(ipHttpbyIP) || value.match(ipHttpbyStr) ||value.match(ipRtspbyIP) || value.match(ipRtspbyStr) ;
        }, '请输入正确的URL地址');

        $.validator.addMethod('validPath', function (value) {
            var pathStr = /^\/\/[\w- .\/]+$/;
            return value.match(pathStr);
        }, '请输入正确的路径');
        
    	$.validator.addMethod("chkchrnum", function(value) { 
    		var chrnum = /^[a-zA-Z]{1}([a-zA-Z0-9]+)$/; // /^([a-zA-Z0-9]+)$/;
    		return value.match(chrnum); 
    	},'请输入以字母开头的字母和数字的组合');

        $.validator.addMethod("chkName", function(value) { 
            var chrnum = /^[a-zA-Z]{1}([a-zA-Z0-9]+)$/;
            var chinese = /^([\u4e00-\u9fa5]+)[0-9]{0,}$/;
            return value.match(chrnum)||value.match(chinese); 
        },'请输入以字母开头的字母和数字的组合或者中文后带数字的组合');
    }
    //TODO: extract as a utility module
    $.fn.cleanTipsyOnModalClose = function () {
        this.on($.modal.CLOSE, function () {
            $(":input.error").each(function () {
                $(this).tipsy('hide');
            });
        });
        return this;
    };
    $.clearValidateError = function (p) {
        if (p.length > 0){
            var pNode = p.find(":input.error");
            if (pNode.length > 0){
                for(var i = 0, len = pNode.length; i < len; i++){
                    $(pNode.get(i)).tipsy('hide');
                }
                pNode.removeClass("error").removeAttr('original-title');
            }
        }
    }
}(jQuery));

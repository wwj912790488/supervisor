(function (root, factory) {
    var ym = typeof ymPrompt === 'undefined' ? undefined : ymPrompt,
        h = typeof Handlebars === 'undefined' ? undefined : Handlebars;
    root.sv = factory(jQuery, _, ym, h);

}(this, function ($, _, ymPrompt, Handlebars) {

    var sv = {};

    // Utility function for ymPrompt
    // -----------------------------

    if (ymPrompt) {
        // Sets the default configuration of ymPrompt.
        ymPrompt.setDefaultCfg({closeBtn: false, maskAlpha: 0.75});

        // A function for confirm dialog to do the specific callback of
        // ok or cancel.
        var callbackFunc = function(op, okFunc, cancelFunc) {
            if (op == 'ok') {
                if (okFunc) {
                    okFunc();
                }
            } else {
                if (cancelFunc) {
                    cancelFunc();
                }
            }
        };

        sv.prompt = {
            showConfirm: function (params) {
                params.title = params.title || '确认';
                params.handler = function (op) {
                    callbackFunc(op, params.okFunc, params.cancelFunc);
                };
                ymPrompt.confirmInfo(params);
            },
            showConfirm2 : function(msg, okFunc, cancelFunc) {
                this.showConfirm({message : msg, okFunc : okFunc, cancelFunc : cancelFunc});
            },
            showConfirm3 : function(msg, okFunc, cancelFunc) {
                this.showConfirm({message : msg, width:500,height:170, okFunc : okFunc, cancelFunc : cancelFunc});
            },
            error: function (params) {
                params.title = params.title || '错误';
                ymPrompt.errorInfo(params);
            },

            // Prompts error with given message of s
            error2 : function (s) {
                this.error({message : s});
            },

            succeed: function (params) {
                params.title = params.title || '成功';
                ymPrompt.succeedInfo(params);
            },

            // Prompts success with given message of s
            succeed2 : function (s) {
                this.succeed({message : s, allowSelect:true,allowRightMenu:true});
            },
            succeed3 : function (s) {
                this.succeed({message : s, width:600,allowSelect:true,allowRightMenu:true});
            },
            succeed4 : function (s) {
                this.succeed({message : s, width:600,height:180,allowSelect:true,allowRightMenu:true});
            }
        };
    }

    // Shortcut options for modal
    var modalOptions = {
        unClosed : {
            showClose: false,
            clickClose: false,
            escapeClose: false
        }
    };
    modalOptions.showClose = _.extend({}, modalOptions.unClosed, {showClose : true});

    // loading
    // -------

    // Utility function for loading dialog.The loading dependency the jquery modal and handlebars plugin.
    sv.loading = {

        // Show the loading dialog with given message and options.
        // The default options is below:
        // {
        //      d : '', the id of dialog.The default value is 'loading-dialog'
        //      t : '', the id of loading template.The default value is 'loadingTemplate'
        //      w :  the width of dialog.
        // }
        show: function (message, options) {
            options = _.isObject(message) ? message : options;
            var _options = _.extend({d : 'loading-dialog', t : 'loadingTemplate'}, options);
            var dialog = $("#" + _options.d).render(_options.t, {message: message});
            if (_.has(_options, 'w')) {
                dialog.width(_options.w);
            }
            return dialog.modal(modalOptions.unClosed);
        },
        close: function () {
            $.modal.close();
        }
    };

    // Url path
    // --------

    // Helper function for get relative url path
    sv.urlPath = {
        ctxPath: '',
        getRealPath: function (path) {
            if (path[0] == '/') {
                path = path.substring(1);
            }
            return this.ctxPath + path;
        },
        setContextPath: function (path) {
            this.ctxPath = path;
        },
        getContextPath: function () {
            return this.ctxPath;
        }
    };

    // Ajax
    // ----

    // Disable cache of ajax for HEAD and GET request.
    $.ajaxSetup({
        cache: false
    });

    //Generic utility method for ajax
    sv.ajax = {

        xhrResultWrap: function (xhr, options) {
            return xhr.done(options.done).fail(options.fail).always(options.always);
        },

        xhrHandler: function (xhr, options) {
            var _options = _.extend({}, options);
            this.xhrResultWrap(xhr, {

                done: function (data) {
                    if (_options.done) {
                        _options.done(data);
                    }
                },

                fail: function (xhr, status) {
                    if (status == 'timeout') {
                        sv.prompt.error2('发送请求超时.');
                    } else if (_options.fail) {
                        var message = _options.message;
                        if (!_.isUndefined(message)) {
                            sv.prompt.error2(message.from(xhr));
                        }
                        _options.fail(xhr, status);
                    } else {
                        console.info(status);
                    }
                },

                always: function () {
                    if (_options.always) {
                        _options.always();
                    }
                }
            });
        },

        getErrorCode: function (jqXhr) {
            return jqXhr.responseJSON.code;
        },

        // A shortcut method do DELETE request and support data as additional parameters.
        // Auto set the content type to 'application/json;charset=UTF-8' if the data is json.
        doDelete: function (url, data) {
            var _options = {
                url: sv.urlPath.getRealPath(url),
                type: 'DELETE'
            };
            if (data) {
                _options.data = data;
            }
            if ($.isPlainObject(data)) {
                _options.contentType = 'application/json;charset=UTF-8';
            }
            return $.ajax(_options);
        },

        getJSON: function (url, data, success) {
            return $.getJSON.call($, sv.urlPath.getRealPath(url), data, success);
        },

        post: function (url, data, success, dataType) {
            return $.post.call($, sv.urlPath.getRealPath(url), data, success, dataType);
        },

        postJson: function(url, data) {
            return $.ajax({
                url: sv.urlPath.getRealPath(url),
                data: JSON.stringify(data),
                type: 'post',
                contentType: 'application/json;charset=UTF-8'
            });
        },

        get: function (url, data, success, dataType) {
            return $.get.call($, sv.urlPath.getRealPath(url), data, success, dataType);
        },

        // post method for ajax and add loading and ymPrompt support.
        // The options is below:
        // {
        //      url : '',
        //      data : '',
        //      dataType : '',
        //      loading : 'loading...', a message for display.
        //      done : function(){}, a callback function will be executed after success
        //      dp : {}, a json object or string as message for ymPrompt parameter after post done
        //      fail : function(){} a callback function will be execute after fail
        //      fp : {} a json object or string as message for ymPrompt parameter after post fail
        // }
        post2: function (options) {
            var _options = _.extend({}, options),
                handlerOptions = {},
                ajaxOptions = {type: 'POST'};
            _options.url = sv.urlPath.getRealPath(options.url);
            _options.dp = this._getPromptParam(options.dp);
            _options.fp = this._getPromptParam(options.fp);

            if (options.loading) {
                sv.loading.show(options.loading);
                handlerOptions.always = function () {
                    sv.loading.close();
                };
            }

            if (options.done && typeof options.done === 'function') {
                if (options.dp) {
                    _options.done = function (data) {
                        sv.prompt.succeed(options.dp);
                        options.done(data);
                    };
                }
            } else if (options.dp) {
                _options.done = function () {
                    sv.prompt.succeed(options.dp);
                };
            }

            if (options.fail && typeof options.fail === 'function') {
                if (options.fp) {
                    _options.fail = function (data) {
                        sv.prompt.error(options.fp);
                        options.done(data);
                    };
                }
            } else if (options.fp) {
                _options.fail = function () {
                    sv.prompt.error(options.fp);
                };
            }
            _.extend(ajaxOptions, _.pick(_options, 'url', 'data', 'dataType'));
            _.extend(handlerOptions, {done: _options.done, fail: _options.fail});

            this.xhrHandler($.ajax(ajaxOptions), handlerOptions);
        },

        // A private function for method of post2 get parameter of prompt.
        _getPromptParam: function (p) {
            return _.isString(p) ? {message: p} : p;
        }
    };

    // Message
    // -------
    function Message(options) {
        options || (options = {});
        this.message = {
            error : "操作处理失败",
            success : '操作成功',
            loading: '操作处理中，请稍后...',
            100: function() {
                return this.error;
            },
            '7001' : 'RTSP发布地址未配置',
            '7002' : 'RTSP存储路径未配置',
            '7003' : 'RTSP服务器ip未配置'
        };
        _.extend(this.message, options.message);
        this.initialize.apply(this, arguments);
    }

    _.extend(Message.prototype, {
        initialize: function (options) {
        },
        get : function(code) {
            if (_.isNumber(code)) {
                code += '';
            }
            if (code.indexOf('.') != -1) {
                var ps = code.split('.'),
                    v;
                _.each(ps, function(val, idx) {
                    v =  (idx === 0 ? _.result(this.message, val) : _.result(v, val));
                }, this);
                return v;
            }
            return _.result(this.message, code);
        },
        from : function(jqXhr) {
            return this.get(sv.ajax.getErrorCode(jqXhr));
        },

        success: function() {
            return this.get('success');
        }
    });

    // Channel
    // -------
    var c = Backbone.Wreqr.radio.channel('global-channel');

    var Channel = Marionette.Object.extend({
        initialize : function() {
            this.commands = c.commands;
            this.reqres = c.reqres;
            this.vent = c.vent;
            this.vent.on('reload:location', function(){ location.reload(); });
        },
        execute : function() {
            this.commands.execute.apply(this.commands, arguments);
            return this;
        },
        request : function () {
            return this.reqres.request.apply(this.reqres, arguments);
        },
        trigger : function () {
            this.vent.trigger.apply(this.vent, arguments);
            return this;
        }
    });

    // Behaviors
    // ---------
    sv.Behaviors = {};
    Marionette.Behaviors.behaviorsLookup = function() {
        return sv.Behaviors;
    };

    // A toggler for action bar of table to do enable and disable
    sv.Behaviors.TableActionbarToggler = Marionette.Behavior.extend({

        initialize : function(options){
            /* {
             		one : [],	select only one item
             		multi : [], select multi items
             		none : {disable : [], enable : []}   select none
             } */
            this.hideItems = _.extend({one : {}, multi : {}, none : {}}, options);
        },

        events : {
            'click @ui.selectAll' : 'selectAll',
            'click @ui.selectOne' : 'toggleActionBar'
        },

        selectAll : function(){
            this.ui.selectOne.prop("checked", this.ui.selectAll.prop("checked"));
            this.toggleActionBar();
        },

        toggleActionBar : function(){
            var selectAll = this.ui.selectAll,
                selectOne = this.ui.selectOne,
                checkedSelectOne = selectOne.filter(":checked"),
                selectAllCheckStatus = checkedSelectOne.length > 0 && checkedSelectOne.length == selectOne.length;

            selectAll.prop("checked", selectAllCheckStatus);
            if (checkedSelectOne.length > 1) {
                this.doSelectMulti();
            } else if (checkedSelectOne.length == 1) {
                this.doSelectOne();
            } else {
                this.doSelectNone();
            }
        },

        doSelectOne : function(){
            _.each(this.hideItems.one, function(v){
                this.ui[v].parent().removeClass('disable');
            }, this);
        },

        doSelectNone : function(){
            _.each(this.hideItems.none, function(v){
                this.ui[v].parent().addClass('disable');
            }, this);
        },
        doSelectMulti : function(){
            if(this.hideItems.multi.enable){
                _.each(this.hideItems.multi.enable, function(v){
                    this.ui[v].parent().removeClass('disable');
                }, this);
            }

            if(this.hideItems.multi.disable){
                _.each(this.hideItems.multi.disable, function(v){
                    this.ui[v].parent().addClass('disable');
                }, this);
            }
        },

        onRender : function(){
            this.toggleActionBar();
        }
    });


    // Commons utility function
    // ------------------------

    sv.commons = {};

    // for navigation menu
    sv.commons.nav = {

        // Active the given id of nav.
        active: function (id) {
            var navTab = $("#inc_tab");
            navTab.find("li").removeClass("active");
            navTab.find("#" + id).addClass("active");
        }

    };

    sv.commons.Message = Message;


    // Start for customize jquery and all of plugin
    // ---------------------------

    // A function for serialize json of relative object
    $.fn.serializeObject = function () {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };

    // Extend for handlebars
    if (Handlebars) {
        var _cache = {};
        $.fn.render = function (templateId, data) {
            this.html($.renderString(templateId, data));
            return this;
        };
        $.renderString = function (templateId, data) {
            if (_cache.hasOwnProperty(templateId)) {
                return _cache[templateId](data);
            } else {
                _cache[templateId] = Handlebars.compile($("#" + templateId).html());
                return _cache[templateId](data);
            }
        };
        Handlebars.registerHelper('ifCond', function (v1, operator, v2, options) {

            switch (operator) {
                case '==':
                    return (v1 == v2) ? options.fn(this) : options.inverse(this);
                case '===':
                    return (v1 === v2) ? options.fn(this) : options.inverse(this);
                case '!=':
                    return (v1 != v2) ? options.fn(this) : options.inverse(this);
                case '<':
                    return (v1 < v2) ? options.fn(this) : options.inverse(this);
                case '<=':
                    return (v1 <= v2) ? options.fn(this) : options.inverse(this);
                case '>':
                    return (v1 > v2) ? options.fn(this) : options.inverse(this);
                case '>=':
                    return (v1 >= v2) ? options.fn(this) : options.inverse(this);
                case '&&':
                    return (v1 && v2) ? options.fn(this) : options.inverse(this);
                case '||':
                    return (v1 || v2) ? options.fn(this) : options.inverse(this);
                default:
                    return options.inverse(this);
            }
        });
        Handlebars.registerHelper('ifIn', function (list, item, options) {
            return list && list.length > 0 && list.indexOf(item) > -1 ? options.fn(this) : options.inverse(this);
        });
        Handlebars.registerHelper('ifNotIn', function (list, item, options) {
            return list && list.length > 0 && list.indexOf(item) > -1 ? options.inverse(this) : options.fn(this);
        });
        Handlebars.registerHelper('select', function (value, options) {
            var $el = $('<select />').html(options.fn(this));
            var $value = $el.find('[value="' + value + '"]');
            if ($value.length === 0) {
                $value = $el.find('[value="custom"]');
            }
            if ($value.length === 0) {
                $value = $el.children().eq(0);
            }
            $value.attr({'selected': 'selected'});
            return $el.html();
        });

        Handlebars.registerHelper("datetime", function (value) {
            var datetime = new Date(value);
            return datetime.toLocaleDateString() + " " + datetime.toLocaleTimeString() + "." + datetime.getMilliseconds();
        });

        Handlebars.registerHelper("ifNotEmpty", function (str, options) {
            return str && str.length > 0 ? options.fn(this) : options.inverse(this);
        });
        
        Handlebars.registerHelper("stripString", function (str, len, replStr, options) {
            return new Handlebars.SafeString(str==null?str:str.length > len ? str.substring(0, len)  + replStr : str);
        });
    }

    //A plugin combine the validate and tipsy to use tipsy as error message tip of validate plugin.
    if (typeof $.fn.validate != 'undefined' && typeof $.fn.tipsy != 'undefined') {
        $.validator.setDefaults({
            errorPlacement: function (error, element) {
                if (element.is("select")) {
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
                    if (target.length === 0) {
                        target = $("input[name='" + forr + "']");
                    }
                    if (target.hasClass('error')) {
                        target.tipsy('hide').removeAttr('original-title');
                    }
                });
            },
            unhighlight: function (element, errorClass, validClass) {
                if (element.type === "radio") {
                    this.findByName(element.name).removeClass(errorClass).addClass(validClass);
                } else {
                    $(element).removeClass(errorClass).addClass(validClass);
                }
                var el = $(element);
                if (el.attr('original-title')) {
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
            return value.match(ipUdp) || value.match(ipHttpbyIP) || value.match(ipHttpbyStr) || value.match(ipRtspbyIP) || value.match(ipRtspbyStr);
        }, '请输入正确的URL地址');

        $.validator.addMethod('validHTTPUrl', function(value){
            var pattern = /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i;
            return pattern.test(value);
        }, '请输入正确的http地址');

        $.validator.addMethod('validPhoneNum', function(value){
            var pattern = /^1\d{10}$/;
            return pattern.test(value);
        }, '请输入正确的手机号码');

        $.validator.addMethod('validPath', function (value) {
            var pathStr = /^\/\/[\w- .\/]+$/;
            return value.match(pathStr);
        }, '请输入正确的路径');

        $.validator.addMethod("chkchrnum", function (value) {
            var chrnum = /^[a-zA-Z]{1}([a-zA-Z0-9]+)$/; // /^([a-zA-Z0-9]+)$/;
            return value.match(chrnum);
        }, '请输入以字母开头的字母和数字的组合');

        $.validator.addMethod("chkName", function (value) {
            var chrnum = /^[a-zA-Z]{1}([a-zA-Z0-9]+)$/;
            var chinese = /^([\u4e00-\u9fa5]+)[0-9]{0,}$/;
            return value.match(chrnum) || value.match(chinese);
        }, '请输入以字母开头的字母和数字的组合或者中文后带数字的组合');
    }

    $.fn.cleanTipsyOnModalClose = function () {
        this.on($.modal.CLOSE, function () {
            $(":input.error").each(function () {
                $(this).tipsy('hide');
            });
        });
        return this;
    };
    $.clearValidateError = function (p) {
        if (p.length > 0) {
            var pNode = p.find(":input.error");
            if (pNode.length > 0) {
                for (var i = 0, len = pNode.length; i < len; i++) {
                    $(pNode.get(i)).tipsy('hide');
                }
                pNode.removeClass("error").removeAttr('original-title');
            }
        }
    };

    // for hover
    $.fn.extend({
        /**
         * add effect like css hover
         * @param color - string. color value
         */
        addHover: function (color) {
            this.off("mouseenter mouseleave").hover(function () {
                $(this).css("background-color", color);
            }, function () {
                $(this).css("background-color", "");
            });
        }
    });

    $.extend({
        /**
         * pagination
         *
         * @param params - json object
         *          common params:
         *                type - 0 or 1, default is 1. 0 will use normal request submit(post), 1 will use ajax submit.
         *                data - all of data of submit to server
         *          use in ajax:
         *                success - callback function
         *          use in normal form submit:
         *                form - form id. if this value is exists then method and data value will be ignored and type value will set to 0.
         *                method - get or post. if data is specify then data will append to url.(Only support get for now)
         *
         * Usage:
         *        ajax:
         *                $.pagination({
		 *				   "data" : {"pager.keySearch" : $("#key_search").val()},
		 *				   "success" : function(data){
		 *					 $("#security_content").empty().append(data);
		 *				   }
		 *			    });
         *      form:
         *             $.pagination({"form" : "form"})
         *      get:
         *           $.pagination({"method" : "get"})
         *
         */
        pagination: function (params) {
            var url = $("#pageUrl");
            var pageIndex = $("#pageIndex");
            params = $.extend({type: 1, data: {}}, params);
            if (params.form || params.method) {
                params.type = 0;
            }
            var navPageSelector = ".nav_page";
            init();
            function init() {
                $("#nav_pre").click(function () {
                    if (!$(this).hasClass("nav_pre_disable")) {
                        params.data.page = parseInt(pageIndex.val()) - 1;
                        doQuery();
                    }
                });
                $("#nav_next").click(function () {
                    if (!$(this).hasClass("nav_next_disable")) {
                        params.data.page = parseInt(pageIndex.val()) + 1;
                        doQuery();
                    }
                });
                $(navPageSelector).click(function () {
                    if ($(this).attr("index") != pageIndex.val()) {
                        params.data.page = parseInt($(this).attr("index"));
                        doQuery();
                    }
                }).hover(function () {//set hover effect
                    if ($(this).attr("index") != pageIndex.val()) {
                        $(this).addClass("nav_page_hover");
                    }
                }, function () {
                });
                if ($(this).attr("index") != pageIndex.val()) {
                    $(this).removeClass("nav_page_hover");
                }

                appendQueryNode();
            }

            function doQuery() {
                if (params.type == 1) {
                    ajaxQuery();
                } else {
                    normalQuery();
                }
            }

            function ajaxQuery() {
                $.ajax({
                    url: url.val(),
                    type: "post",
                    data: params.data,
                    success: function (data) {
                        params.success(data);
                    }
                });
            }

            function normalQuery() {
                if (params.form) {
                    var pageForm = $("#pagerForm");
                    pageForm.find("#pageIndex").val(params.data.page);
                    pageForm.submit();
                } else {//Haven't a from and want to use a get request
                    if (params.method) {
                        if (params.method == "get") { //use get request
                            location.href = (url.val() + "?" + ($.param(params.data)));
                        }
                    }
                }
            }

            /**
             * clone all of form's input to pagerForm
             */
            function appendQueryNode() {
                var form = $("#" + params.form);
                var pagerForm = $("#pagerForm");
                $.each(form.find(":input"), function () {
                    $(this).clone().appendTo(pagerForm);
                });
            }
        }

    });

    sv.nav = sv.commons.nav;
    sv.Message = Message;
    sv.modalOptions = modalOptions;
    sv.channel = new Channel();

    return sv;
}));

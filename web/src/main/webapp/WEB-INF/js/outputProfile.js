$(function () {
    $("#inc_tab")
        .find("li")
        .removeClass("active")
        .end()
        .find("#tb4")
        .addClass("active");
});

Backbone.Marionette.Renderer.render = function (template, data) {
    if (!template) {
        throw new Marionette.Error({
            name: 'TemplateNotFoundError',
            message: 'Cannot render the template since its false, null or undefined.'
        });
    }
    var renderer = Handlebars.compile($("#" + template).html());
    return renderer(data);
};

var oldBackboneSync = Backbone.sync;
Backbone.sync = function (method, model, options) {
    if (method === 'read' || method === 'get') {
        var re = /\?[^=]*=/,
            url = _.isEmpty(options.url) ? (_.isFunction(model.url) ? model.url() : model.url) : options.url;
        //options.url = $.common.getContextPath() + (url[0] == '/' ? url.substr(1) : url);
        options.url = url;
        if (re.exec(options.url) !== null) {
            options.url += ('&r=' + Math.random());
        } else {
            options.url += ('?r=' + Math.random());
        }
    }
    if (method === 'create' || method === 'update') {
        var url = _.isEmpty(options.url) ? (_.isFunction(model.url) ? model.url() : model.url) : options.url;
        options.url = sv.urlPath.getRealPath(url);
    }
    return oldBackboneSync.apply(this, [method, model, options]);
};

var Behaviors = {};
Marionette.Behaviors.behaviorsLookup = Behaviors;

var TipsyValidationCallback = {
    isElementInViewport: function (el) {
        if (typeof jQuery === "function" && el instanceof jQuery) {
            el = el[0];
        }
        var rect = el.getBoundingClientRect();
        return (
            rect.top >= 0 &&
            rect.left >= 0 &&
            rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
            rect.right <= (window.innerWidth || document.documentElement.clientWidth)
        );
    },
    valid: function (view, attr, selector) {
        var $el = view.$('[name=' + attr + ']');
        if ($el.length > 0) {
            $el.removeClass('error');
            $el.tipsy('hide').removeAttr('original-title');
        }

    },
    invalid: function (view, attr, error, selector) {
        var $el = view.$('[name=' + attr + ']');
        if ($el.length > 0) {
            $el.addClass('error');
            if ($el.is("select")) {
                $el.attr('not-title', error);
                $el.tipsy({gravity: $el.attr("tip-gravity") || 's', trigger: 'manual', title: 'not-title'});
            } else {
                $el.attr('title', error);
                $el.tipsy({gravity: $el.attr("tip-gravity") || 's', trigger: 'manual'});
            }
            $el.tipsy('show');
            if (!this.isElementInViewport($el)) {
                var top = $el.offset().top - 100;
                $('html, body').scrollTop(top > 0 ? top : 0);
            }
        }
    }
};

var OutputProfile = (function ($, _, Backbone, Validation, TipsyValidationCallback, Marionette, state, sv) {

    var MPEG4VideoProfileModel = Backbone.Model.extend({
        defaults: {
            'videopassthrough': false,
            'videocodec': 'MPEG4',
            'videocodecprofile': 'Simple',
            'videowidth': 640,
            'videoheight': 480,
            'videosourcePAR': true,
            'videoPARX': 3,
            'videoPARY': 2,
            'videosmartborder': 1, //0:线性拉伸，1：智能黑边，2：自动剪裁
            'videosourceframerate': true,
            'videoframerateX': 40,
            'videoframerateY': 1,
            'videoframerateconversionmode': false,
            'videoratecontrol': 'ABR',
            'videobitrate': 1000,
            'videogopsize': 30,
            'videoSCD': false,
            'videodeinterlace': 2,//0:关，1：开，2：自动
            'videodeinterlacealg': 2,//1:单频道转码专用，提升性能2:质量优先，3：速度优先
            'videoresizealg': 3,//3:质量优先，1：速度优先
            'videodenoise': 0,
            'videodeblock': false,
            'videosharpen': 0,
            'videoantialias': false,
            'videobright': 0,
            'videocontrast': 0,
            'videosaturation': 0,
            'videohue': 0,
            'videodelight': 0
        },
        initialize: function () {
            this.updateVideoPAR();
            this.updateVideoFramerate();
            this.listenTo(this, 'change:videoPARX', this.updateVideoPAR);
            this.listenTo(this, 'change:videoPARY', this.updateVideoPAR);
            this.listenTo(this, 'change:videoframerateX', this.updateVideoFramerate);
            this.listenTo(this, 'change:videoframerateY', this.updateVideoFramerate);
        },
        updateVideoPAR: function () {
            var par = this.get('videoPARX') + ":" + this.get('videoPARY');
            this.set('videoPAR', par);
        },
        updateVideoFramerate: function () {
            var framerate = this.get('videoframerateX') + ":" + this.get('videoframerateY');
            this.set('videoframerate', framerate);
        }
    });

    var H264VideoProfileModel = Backbone.Model.extend({
        client_state: ['codecsectionvisible', 'ipsectionvisible'],
        defaults: {
            'videopassthrough': false,
            'videocodec': 'H264',
            'videocodecprofile': 'Main',
            'videointerlace': '0',
            'videotopfieldfirst': '-1',
            'videowidth': 1920,
            'videoheight': 1080,
            'videosourcePAR': false,
            'videoPARX': 16,
            'videoPARY': 9,
            'videosmartborder': 1, //0:线性拉伸，1：智能黑边，2：自动剪裁
            'videosourceframerate': false,
            'videoframerateX': 25,
            'videoframerateY': 1,
            'videoframerateconversionmode': false,
            'videoratecontrol': 'CBR',
            'videobitrate': 4000,
            'videomaxbitrate': '',
            'videotwopass': false,
            'videoqualityleveldisp': -1,
            'videobuffersize': 375,
            'videobufferfill': 1000,
            'videoquantizer': 0,
            'videogopsize': 50,
            'videobframe': 0,
            'videoreferenceframe': 1,
            'videoCABAC': true,
            'videointraprediction': false,
            'videotransform': false,
            'videoSCD': false,
            'videodeinterlace': 2,//0:关，1：开，2：自动
            'videodeinterlacealg': 2,//1:单频道转码占用，提升性能 2:质量优先，3：速度优先
            'videoresizealg': 3,//3:质量优先，1：速度优先
            'videodenoise': 0,
            'videodeblock': false,
            'videosharpen': 0,
            'videoantialias': false,
            'videobright': 0,
            'videocontrast': 0,
            'videosaturation': 0,
            'videohue': 0,
            'videodelight': 0,
            'codecsectionvisible': true,
            'ipsectionvisible': false
        },
        initialize: function () {
            this.updateVideoPAR();
            this.updateVideoFramerate();
            this.listenTo(this, 'change:videoPARX', this.updateVideoPAR);
            this.listenTo(this, 'change:videoPARY', this.updateVideoPAR);
            this.listenTo(this, 'change:videoframerateX', this.updateVideoFramerate);
            this.listenTo(this, 'change:videoframerateY', this.updateVideoFramerate);
        },
        updateVideoPAR: function () {
            //var par = this.get('videoPARX') + ":" + this.get('videoPARY');
            //this.set('videoPAR', par);
        },
        updateVideoFramerate: function () {
            var framerate = this.get('videoframerateX') + ":" + this.get('videoframerateY');
            this.set('videoframerate', framerate);
        },
        toJSON: function (options) {
            if (options && options.render) {
                return _.clone(this.attributes);
            } else {
                return _.omit(this.attributes, this.client_state);
            }
        }
    });

    var AACAudioProfileModel = Backbone.Model.extend({
        defaults: {
            'audiopassthrough': false,
            'audiomix': false,
            'audiocodec': 'AAC',
            'audiocodecprofile': 'LC',
            'audiochannel': 2,//1:Mono,2:Stereo,6:5.1
            'audiosamplerate': 44100,
            'audiobitrate': 64.0,
            'audiovolumemode': 0,//0:跟随源，1：音量增益，2：音量平衡
            'audioboostlevel': 0,
            'audiobalancelevel': 0,//0：低，5：中，10：高
            'audiobalancedb': -30,
            'audiochannelprocessing': 'None'
        }
    });

    var MP2AudioProfileModel = Backbone.Model.extend({
        defaults: {
            'audiopassthrough': false,
            'audiomix': false,
            'audiocodec': 'MP2',
            'audiochannel': 2,//1:Mono,2:Stereo,6:5.1
            'audiosamplerate': 32000,
            'audiobitrate': 96.0,
            'audiovolumemode': 0,//0:跟随源，1：音量增益，2：音量平衡
            'audioboostlevel': 0,
            'audiobalancelevel': 0,//0：低，5：中，10：高
            'audiobalancedb': -30,
            'audiochannelprocessing': 'None'
        }
    });

    var OutputProfileModel = Backbone.Model.extend({
        urlRoot: '/profile/output',
        validation: {
            name: {
                required: true,
                msg: '名称不能为空'
            }
        },
        defaults: function () {
            return {
                'videoprofiles': new VideoProfileCollection([new H264VideoProfileModel()]),
                'audioprofiles': new AudioProfileCollection([new AACAudioProfileModel()]),
                'name': '',
                'description': ''
            };
        },
        parse: function (resp, options) {
            if (resp.videoprofiles) {
                var vcol = new VideoProfileCollection();
                _.each(resp.videoprofiles, function (attr) {
                    vcol.add(attr, options);
                });
                resp.videoprofiles = vcol;
            }
            if (resp.audioprofiles) {
                var acol = new AudioProfileCollection();
                _.each(resp.audioprofiles, function (attr) {
                    acol.add(attr, options);
                });
                resp.audioprofiles = acol;
            }
            if (options.ignore) {
                _.each(options.ignore, function (attr) {
                    if (resp[attr]) {
                        delete resp[attr];
                    }
                })
            }
            return resp;
        },
        toJSON: function (options) {
            var clone = _.clone(this.attributes);
            if (clone.videoprofiles) {
                clone.videoprofiles = clone.videoprofiles.toJSON();
            }
            if (clone.audioprofiles) {
                clone.audioprofiles = clone.audioprofiles.toJSON();
            }
            return clone;
        }
    });

    var SectionTrigger = Marionette.Behavior.extend({
        events: {
            'click .section-trigger': 'onSectionTrigger'
        },
        onSectionTrigger: function (event) {
            if (event.target.name == "mixaudio")
                return;

            event.stopPropagation();
            var collapsed = $(event.currentTarget).parent().hasClass("section-collapse");
            if (collapsed) {
                $(event.currentTarget).parent().removeClass("section-collapse");
            } else {
                $(event.currentTarget).parent().addClass("section-collapse");
            }
        }
    });

    var VideoCommon = Marionette.Behavior.extend({
        events: {
            'change input[name="videopass"]': 'updateVideoPass',
            'change select[name="videodeinterlace"]': 'updateVideoDeinterlace',
            'change select[name="videodeinterlacealg"]': 'updateVideoDeinterlaceAlg',
            'change select[name="videoresizealg"]': 'updateVideoResizeAlg',
            'change select[name="videodenoise"]': 'updateVideoDenoise',
            'change input[name="videodeblock"]': 'updateVideoDeblock',
            'change select[name="videosharpen"]': 'updateVideoSharpen',
            'change input[name="videoantialias"]': 'updateVideoAntiAlias',
            'change input[name="videobright"]': 'updateVideoBright',
            'change input[name="videocontrast"]': 'updateVideoContrast',
            'change input[name="videosaturation"]': 'updateVideoSaturation',
            'change input[name="videohue"]': 'updateVideoHue',
            'change select[name="videodelight"]': 'updateVideoDelight',
            'change input[name="videowidth"]': 'updateVideoWidth',
            'change input[name="videoheight"]': 'updateVideoHeight',
            'change select[name="videoPAR"]': 'updateVideoPAR',
            'change input[name="videoPARX"]': 'updateVideoPARX',
            'change input[name="videoPARY"]': 'updateVideoPARY',
            'change select[name="videosmartborder"]': 'updateVideoSmartBorder',
            'change select[name="videoframerate"]': 'updateVideoFramerate',
            'change input[name="videoframerateX"]': 'updateVideoFramerateX',
            'change input[name="videoframerateY"]': 'updateVideoFramerateY',
            'change input[name="videoframerateconversionmode"]': 'updateVideoFramerateConversionMode',
            'change input[name="videoGOPsize"]': 'updateVideoGOPSize',
            'change input[name="videoSCD"]': 'updateVideoSCD'
        },
        updateVideoPass: function (event) {
            this.view.updateAttribute(event, "videopassthrough", $(event.currentTarget).prop("checked"));
            this.view.triggerMethod('change:summaryprofile');
            this.view.render();
        },
        updateVideoDeinterlace: function (event) {
            this.view.updateAttribute(event, "videodeinterlace", $(event.currentTarget).val());
        },
        updateVideoDeinterlaceAlg: function (event) {
            this.view.updateAttribute(event, "videodeinterlacealg", $(event.currentTarget).val());
        },
        updateVideoResizeAlg: function (event) {
            this.view.updateAttribute(event, "videoresizealg", $(event.currentTarget).val());
        },
        updateVideoDenoise: function (event) {
            this.view.updateAttribute(event, "videodenoise", $(event.currentTarget).val());
        },
        updateVideoDeblock: function (event) {
            this.view.updateAttribute(event, "videodeblock", $(event.currentTarget).prop("checked"));
        },
        updateVideoSharpen: function (event) {
            this.view.updateAttribute(event, "videosharpen", $(event.currentTarget).val());
        },
        updateVideoAntiAlias: function (event) {
            this.view.updateAttribute(event, "videoantialias", $(event.currentTarget).prop("checked"));
        },
        updateVideoBright: function (event) {
            this.view.updateAttribute(event, "videobright", $(event.currentTarget).val());
        },
        updateVideoContrast: function (event) {
            this.view.updateAttribute(event, "videocontrast", $(event.currentTarget).val());
        },
        updateVideoSaturation: function (event) {
            this.view.updateAttribute(event, "videosaturation", $(event.currentTarget).val());
        },
        updateVideoHue: function (event) {
            this.view.updateAttribute(event, "videohue", $(event.currentTarget).val());
        },
        updateVideoDelight: function (event) {
            this.view.updateAttribute(event, "videodelight", $(event.currentTarget).val());
        },
        updateVideoWidth: function (event) {
            this.view.updateAttribute(event, "videowidth", $(event.currentTarget).val());
            this.view.triggerMethod('change:summaryprofile');
        },
        updateVideoHeight: function (event) {
            this.view.updateAttribute(event, "videoheight", $(event.currentTarget).val());
            this.view.triggerMethod('change:summaryprofile');
        },
        updateVideoPAR: function (event) {
            var value = $(event.currentTarget).val();
            if (value == "source") {
                this.view.updateAttribute(event, "videosourcePAR", true);
                $(event.currentTarget).closest(".videoprofile").find('input[name="videoPARX"]').prop('disabled', true).val("")
                    .end()
                    .find('input[name="videoPARY"]').prop('disabled', true).val("");
            } else {
                var parX = 3, parY = 2;
                var re = /(\d+):(\d+)/;
                var m;

                if ((m = re.exec(value)) !== null) {
                    parX = m[1];
                    parY = m[2];
                }

                this.view.updateAttribute(event, "videosourcePAR", false);
                this.view.updateAttribute(event, "videoPAR", value);
                this.view.updateAttribute(event, "videoPARX", parX);
                this.view.updateAttribute(event, "videoPARY", parY);
                $(event.currentTarget).closest(".videoprofile").find('input[name="videoPARX"]').prop('disabled', false).val(parX)
                    .end()
                    .find('input[name="videoPARY"]').prop('disabled', false).val(parY);
            }
        },
        updateVideoPARX: function (event) {
            this.view.updateAttribute(event, "videoPARX", $(event.currentTarget).val());
            this.renderVideoPAR(event);
        },
        updateVideoPARY: function (event) {
            this.view.updateAttribute(event, "videoPARY", $(event.currentTarget).val());
            this.renderVideoPAR(event);
        },
        renderVideoPAR: function (event) {
            var model = this.view.model;
            var value = model.get('videoPARX') + ":" + model.get('videoPARY');
            var $profile = this.view.$el;
            var $option = $profile.find('select[name="videoPAR"]').find('[value="' + value + '"]');
            if ($option.length == 0) {
                $option = $profile.find('select[name="videoPAR"]').find('[value="custom"]');
            }
            $option.attr({'selected': 'selected'});
        },
        updateVideoSmartBorder: function (event) {
            this.view.updateAttribute(event, "videosmartborder", $(event.currentTarget).val());
        },
        updateVideoFramerate: function (event) {
            var value = $(event.currentTarget).val();
            if (value == "source") {
                this.view.updateAttribute(event, "videosourceframerate", true);
                $(event.currentTarget).closest(".videoprofile").find('input[name="videoframerateX"]').prop('disabled', true).val("")
                    .end()
                    .find('input[name="videoframerateY"]').prop('disabled', true).val("");
            } else {
                var framerateX = 3, framerateY = 2;
                var re = /(\d+):(\d+)/;
                var m;

                if ((m = re.exec(value)) !== null) {
                    framerateX = m[1];
                    framerateY = m[2];
                }

                this.view.updateAttribute(event, "videosourceframerate", false);
                this.view.updateAttribute(event, "videoframerateX", framerateX);
                this.view.updateAttribute(event, "videoframerateY", framerateY);
                $(event.currentTarget).closest(".videoprofile").find('input[name="videoframerateX"]').prop('disabled', false).val(framerateX)
                    .end()
                    .find('input[name="videoframerateY"]').prop('disabled', false).val(framerateY);
            }
        },
        updateVideoFramerateX: function (event) {
            this.view.updateAttribute(event, "videoframerateX", $(event.currentTarget).val());
            this.renderVideoFramerate(event);
        },
        updateVideoFramerateY: function (event) {
            this.view.updateAttribute(event, "videoframerateY", $(event.currentTarget).val());
            this.renderVideoFramerate(event);
        },
        renderVideoFramerate: function (event) {
            var model = this.view.model;
            var value = model.get('videoframerateX') + ":" + model.get('videoframerateY');
            var $profile = this.view.$el;
            var $option = $profile.find('select[name="videoframerate"]').find('[value="' + value + '"]');
            if ($option.length == 0) {
                $option = $profile.find('select[name="videoframerate"]').find('[value="custom"]');
            }
            $option.attr({'selected': 'selected'});
        },
        updateVideoFramerateConversionMode: function (event) {
            this.view.updateAttribute(event, "videoframerateconversionmode", $(event.currentTarget).prop("checked"));
        },
        updateVideoGOPSize: function (event) {
            this.view.updateAttribute(event, "videogopsize", $(event.currentTarget).val());
        },
        updateVideoSCD: function (event) {
            this.view.updateAttribute(event, "videoSCD", $(event.currentTarget).prop("checked"));
        }
    });

    Marionette.Behaviors.behaviorsLookup.VideoCommon = VideoCommon;
    Marionette.Behaviors.behaviorsLookup.SectionTrigger = SectionTrigger;

    var ProfileBasicView = Marionette.ItemView.extend({
        template: "basicTemplate",
        events: {
            'change input[name="name"]': 'updateName',
            'change textarea[name="description"]': 'updateDescription'
        },
        updateName: function () {
            this.model.set('name', this.$el.find('input[name="name"]').val(), {validate: true});
        },
        updateDescription: function () {
            this.model.set('description', this.$el.find('textarea[name="description"]').val());
        },
        initialize: function () {
            Validation.bind(this, TipsyValidationCallback);
        },
        onDestroy: function () {
            Validation.unbind(this);
        }
    });

    var MPEG4VideoProfileView = Marionette.ItemView.extend({
        template: "mpeg4videoprofileTemplate",
        events: {
            'click .videoprofile-remove-btn': 'removeVideoProfile',
            'change select[name="videocodec"]': 'updateVideoCodec',
            'change select[name="videoprofile"]': 'updateVideoCodecProfile',
            'change select[name="videoratecontrol"]': 'updateVideoRateControl',
            'change input[name="videobitrate"]': 'updateVideoBitrate'
        },
        behaviors: {
            VideoCommon: {}
        },
        removeVideoProfile: function () {
            this.triggerMethod('remove:videoprofile', this.model);
        },
        updateAttribute: function (event, name, value) {
            this.model.set(name, value);
        },
        updateVideoCodec: function (event) {
            this.triggerMethod('replace:videoprofile', this.model, $(event.currentTarget).val());
        },
        updateVideoCodecProfile: function (event) {
            this.updateAttribute(event, "videocodecprofile", $(event.currentTarget).val());
        },
        updateVideoRateControl: function (event) {
            this.updateAttribute(event, "videoratecontrol", $(event.currentTarget).val());
            this.triggerMethod('change:summaryprofile');
        },
        updateVideoBitrate: function (event) {
            this.updateAttribute(event, "videobitrate", $(event.currentTarget).val());
            this.triggerMethod('change:summaryprofile');
        }
    });

    var H264VideoProfileView = Marionette.ItemView.extend({
        template: 'h264videoprofileTemplate',
        ui: {
            twopass: 'input[name="videotwopass"]',
            qualityleveldisp: 'select[name="videoqualityleveldisp"]'
        },
        events: {
            'click .videoprofile-remove-btn': 'removeVideoProfile',
            'change select[name="videocodec"]': 'updateVideoCodec',
            'change select[name="videoprofile"]': 'updateVideoCodecProfile',
            'change select[name="videocodeclevel"]': 'updateVideoCodecLevel',
            'change select[name="videointerlace"]': 'updateVideoInterlace',
            'change select[name="videotopfieldfirst"]': 'updateVideoTopFieldFirst',
            'change select[name="videoratecontrol"]': 'updateVideoRateControl',
            'change input[name="videobitrate"]': 'updateVideoBitrate',
            'change input[name="videomaxbitrate"]': 'updateVideoMaxBitrate',
            'change input[name="videotwopass"]': 'updateVideoTwoPass',
            'change select[name="videoqualityleveldisp"]': 'updateVideoQualityLevelDisp',
            'change input[name="videobuffersize"]': 'updateVideoBufferSize',
            'change input[name="videobufferfill"]': 'updateVideoBufferFill',
            'change input[name="videoquantizer"]': 'updateVideoQuantizer',
            'change input[name="videobframe"]': 'updateVideoBFrame',
            'change input[name="videoreferenceframe"]': 'updateVideoReferenceFrame',
            'change input[name="videoCABAC"]': 'updateVideoCABAC',
            'change input[name="videointraprediction"]': 'updateVideoIntraprediction',
            'change input[name="videotransform"]': 'updateVideoTransform',
            'click .codecsection': 'onCodecSectionClicked',
            'click .ipsection': 'onIPSectionClicked'
        },
        behaviors: {
            VideoCommon: {}
        },
        initialize: function (options) {
            if (options && options.encodingoption) {
                this.encodingoption = options.encodingoption;
            }
            this.listenTo(this.model, 'change', this.render);
            this.listenTo(Backbone, 'task:encodingoption:change', this.updateEncodingOptionRelated);
        },
        removeVideoProfile: function () {
            this.triggerMethod('remove:videoprofile', this.model);
        },
        updateAttribute: function (event, name, value) {
            this.model.set(name, value);
        },
        updateVideoCodec: function (event) {
            this.triggerMethod('replace:videoprofile', this.model, $(event.currentTarget).val());
        },
        updateVideoCodecProfile: function (event) {
            var codecProfile = $(event.currentTarget).val();
            this.updateAttribute(event, 'videocodecprofile', codecProfile);
            if (codecProfile == "High") {
                this.updateAttribute(event, 'videointerlace', "0");
                this.updateAttribute(event, 'videoCABAC', true);
                this.updateAttribute(event, 'videointraprediction', true);
                this.updateAttribute(event, 'videotransform', true);
            } else if (codecProfile == "Main") {
                this.updateAttribute(event, 'videointerlace', "0");
                this.updateAttribute(event, 'videoCABAC', true);
                this.updateAttribute(event, 'videointraprediction', false);
                this.updateAttribute(event, 'videotransform', false);
            } else if (codecProfile == "Baseline") {
                this.updateAttribute(event, 'videointerlace', "0");
                this.updateAttribute(event, 'videoCABAC', false);
                this.updateAttribute(event, 'videointraprediction', false);
                this.updateAttribute(event, 'videotransform', false);
            }

        },
        updateVideoCodecLevel: function (event) {
            this.updateAttribute(event, 'videocodeclevel', $(event.currentTarget).val());
        },
        updateVideoInterlace: function (event) {
            var videointerlace = $(event.currentTarget).val();
            this.updateAttribute(event, 'videointerlace', videointerlace);
            if (videointerlace == "-1" || videointerlace == "0") {
                this.updateAttribute(event, 'videotopfieldfirst', "-1");
            }
        },
        updateVideoTopFieldFirst: function (event) {
            this.updateAttribute(event, 'videotopfieldfirst', $(event.currentTarget).val());
        },
        updateVideoRateControl: function (event) {
            var videoratecontrol = $(event.currentTarget).val();
            this.updateAttribute(event, 'videoratecontrol', videoratecontrol);
            if (videoratecontrol == "VBR") {
                this.updateAttribute(event, 'videobitrate', 1000);
                this.updateAttribute(event, 'videomaxbitrate', 3000);
                this.updateAttribute(event, 'videobuffersize', 375);
                this.updateAttribute(event, 'videobufferfill', 1000);
                this.updateAttribute(event, 'videoquantizer', "");
            } else if (videoratecontrol == "CBR") {
                this.updateAttribute(event, 'videobitrate', 1000);
                this.updateAttribute(event, 'videomaxbitrate', "");
                this.updateAttribute(event, 'videobuffersize', 125);
                this.updateAttribute(event, 'videobufferfill', 1000);
                this.updateAttribute(event, 'videoquantizer', "");
            } else if (videoratecontrol == "ABR") {
                this.updateAttribute(event, 'videobitrate', 1000);
                this.updateAttribute(event, 'videomaxbitrate', "");
                this.updateAttribute(event, 'videobuffersize', "");
                this.updateAttribute(event, 'videobufferfill', "");
                this.updateAttribute(event, 'videoquantizer', "");
            } else if (videoratecontrol == "CQ" || videoratecontrol == "CRF") {
                this.updateAttribute(event, 'videobitrate', "");
                this.updateAttribute(event, 'videomaxbitrate', "");
                this.updateAttribute(event, 'videobuffersize', "");
                this.updateAttribute(event, 'videobufferfill', "");
                this.updateAttribute(event, 'videoquantizer', 40);
            }
            ;
            this.triggerMethod('change:summaryprofile');
        },
        updateVideoBitrate: function (event) {
            this.updateAttribute(event, 'videobitrate', $(event.currentTarget).val());
            this.triggerMethod('change:summaryprofile');
        },
        updateVideoMaxBitrate: function (event) {
            this.updateAttribute(event, 'videomaxbitrate', $(event.currentTarget).val());
        },
        updateVideoTwoPass: function (event) {
            this.updateAttribute(event, 'videotwopass', $(event.currentTarget).prop("checked"));
        },
        updateVideoQualityLevelDisp: function (event) {
            this.updateAttribute(event, 'videoqualityleveldisp', $(event.currentTarget).val());
        },
        updateVideoBufferSize: function (event) {
            this.updateAttribute(event, 'videobuffersize', $(event.currentTarget).val());
        },
        updateVideoBufferFill: function (event) {
            this.updateAttribute(event, 'videobufferfill', $(event.currentTarget).val());
        },
        updateVideoQuantizer: function (event) {
            this.updateAttribute(event, 'videoquantizer', $(event.currentTarget).val());
        },
        updateVideoBFrame: function (event) {
            this.updateAttribute(event, 'videobframe', $(event.currentTarget).val());
        },
        updateVideoReferenceFrame: function (event) {
            this.updateAttribute(event, 'videoreferenceframe', $(event.currentTarget).val());
        },
        updateVideoCABAC: function (event) {
            this.updateAttribute(event, 'videoCABAC', $(event.currentTarget).prop("checked"));
        },
        updateVideoIntraprediction: function (event) {
            this.updateAttribute(event, 'videointraprediction', $(event.currentTarget).prop("checked"));
        },
        updateVideoTransform: function (event) {
            this.updateAttribute(event, 'videotransform', $(event.currentTarget).prop("checked"));
        },
        updateEncodingOptionRelated: function (encodingoption) {
            this.encodingoption = encodingoption;
            if (encodingoption == "Custom") {
                this.ui.twopass.prop("disabled", false);
                this.ui.qualityleveldisp.prop("disabled", false);
            } else {
                this.ui.twopass.prop("disabled", true);
                this.ui.qualityleveldisp.prop("disabled", true);
            }
        },
        serializeData: function () {
            return Marionette.ItemView.prototype.serializeData.call(this, {render: true});
        },
        onRender: function () {
            this.updateEncodingOptionRelated(this.encodingoption);
        },
        onCodecSectionClicked: function (event) {
            event.stopPropagation();
            var visible = this.model.get('codecsectionvisible');
            this.updateAttribute(event, 'codecsectionvisible', !visible);
        },
        onIPSectionClicked: function (event) {
            event.stopPropagation();
            var visible = this.model.get('ipsectionvisible');
            this.updateAttribute(event, 'ipsectionvisible', !visible);
        }
    });

    var AACAudioProfileView = Marionette.ItemView.extend({
        template: "aacaudioprofileTemplate",
        events: {
            'click .audioprofile-remove-btn': 'removeAudioProfile',
            'change input[name="audiopass"]': 'updateAudioPass',
            'change input[name="mixaudio"]': 'updateAudioMix',
            'change select[name="audiocodec"]': 'updateAudioCodec',
            'change select[name="audiocodecprofile"]': 'updateAudioCodecProfile',
            'change select[name="audiochannel"]': 'updateAudioChannel',
            'change select[name="audiosamplerate"]': 'updateAudioSamplerate',
            'change input[name="audiobitrate"]': 'updateAudioBitrate',
            'change select[name="audiovolumemode"]': 'updateAudioVolumnMode',
            'change select[name="audioboostlevel"]': 'updateAudioBoostLevel',
            'change select[name="audiobalancelevel"]': 'updateAudioBalanceLevel',
            'change input[name="audiobalancedb"]': 'updateAudioBalanceDb',
            'change select[name="audiochannelprocessing"]': 'updateAudioChannelProcessing'
        },
        behaviors: {
            SectionTrigger: {}
        },
        removeAudioProfile: function (event) {
            this.triggerMethod('remove:audioprofile', this.model);
        },
        updateAttribute: function (event, name, value) {
            this.model.set(name, value);
        },
        updateAudioPass: function (event) {
            this.updateAttribute(event, 'audiopassthrough', $(event.currentTarget).prop("checked"));
            this.triggerMethod('change:summaryprofile');
            this.render();
        },
        updateAudioMix: function (event) {
            this.updateAttribute(event, 'audiomix', $(event.currentTarget).prop("checked"));
        },
        updateAudioCodec: function (event) {
            this.triggerMethod('replace:audioprofile', this.model, $(event.currentTarget).val());
        },
        updateAudioCodecProfile: function (event) {
            this.updateAttribute(event, 'audiocodecprofile', $(event.currentTarget).val());
        },
        updateAudioChannel: function (event) {
            this.updateAttribute(event, 'audiochannel', $(event.currentTarget).val());
            this.triggerMethod('change:summaryprofile');
        },
        updateAudioSamplerate: function (event) {
            this.updateAttribute(event, 'audiosamplerate', $(event.currentTarget).val());
            this.triggerMethod('change:summaryprofile');
        },
        updateAudioBitrate: function (event) {
            this.updateAttribute(event, 'audiobitrate', $(event.currentTarget).val());
            this.triggerMethod('change:summaryprofile');
        },
        updateAudioVolumnMode: function (event) {
            var value = $(event.currentTarget).val();
            this.updateAttribute(event, 'audiovolumemode', value);
            var $profile = $(event.currentTarget).closest(".audioprofile");
            if (value == 0) {
                $profile.find('select[name="audioboostlevel"]').prop("disabled", true)
                    .end()
                    .find('select[name="audiobalancelevel"]').prop("disabled", true)
                    .end()
                    .find('input[name="audiobalancedb"]').prop("disabled", true);
            } else if (value == 1) {
                $profile.find('select[name="audioboostlevel"]').prop("disabled", false)
                    .end()
                    .find('select[name="audiobalancelevel"]').prop("disabled", true)
                    .end()
                    .find('input[name="audiobalancedb"]').prop("disabled", true);
            } else if (value == 2) {
                $profile.find('select[name="audioboostlevel"]').prop("disabled", true)
                    .end()
                    .find('select[name="audiobalancelevel"]').prop("disabled", false)
                    .end()
                    .find('input[name="audiobalancedb"]').prop("disabled", false);
            }
        },
        updateAudioBoostLevel: function (event) {
            this.updateAttribute(event, 'audioboostlevel', $(event.currentTarget).val());
        },
        updateAudioBalanceLevel: function (event) {
            this.updateAttribute(event, 'audiobalancelevel', $(event.currentTarget).val());
        },
        updateAudioBalanceDb: function (event) {
            this.updateAttribute(event, 'audiobalancedb', $(event.currentTarget).val());
        },
        updateAudioChannelProcessing: function (event) {
            this.updateAttribute(event, 'audiochannelprocessing', $(event.currentTarget).val());
        }
    });

    var MP2AudioProfileView = Marionette.ItemView.extend({
        template: "aacaudioprofileTemplate_MP2",
        events: {
            'click .audioprofile-remove-btn': 'removeAudioProfile',
            'change input[name="audiopass"]': 'updateAudioPass',
            'change input[name="mixaudio"]': 'updateAudioMix',
            'change select[name="audiocodec"]': 'updateAudioCodec',
            'change select[name="audiochannel"]': 'updateAudioChannel',
            'change select[name="audiosamplerate"]': 'updateAudioSamplerate',
            'change input[name="audiobitrate"]': 'updateAudioBitrate',
            'change select[name="audiovolumemode"]': 'updateAudioVolumnMode',
            'change select[name="audioboostlevel"]': 'updateAudioBoostLevel',
            'change select[name="audiobalancelevel"]': 'updateAudioBalanceLevel',
            'change input[name="audiobalancedb"]': 'updateAudioBalanceDb',
            'change select[name="audiochannelprocessing"]': 'updateAudioChannelProcessing'
        },
        behaviors: {
            SectionTrigger: {}
        },
        removeAudioProfile: function (event) {
            this.triggerMethod('remove:audioprofile', this.model);
        },
        updateAttribute: function (event, name, value) {
            this.model.set(name, value);
        },
        updateAudioPass: function (event) {
            this.updateAttribute(event, 'audiopassthrough', $(event.currentTarget).prop("checked"));
            this.triggerMethod('change:summaryprofile');
            this.render();
        },
        updateAudioMix: function (event) {
            this.updateAttribute(event, 'audiomix', $(event.currentTarget).prop("checked"));
        },
        updateAudioCodec: function (event) {
            this.triggerMethod('replace:audioprofile', this.model, $(event.currentTarget).val());
        },
        updateAudioCodecProfile: function (event) {
            this.updateAttribute(event, 'audiocodecprofile', $(event.currentTarget).val());
        },
        updateAudioChannel: function (event) {
            this.updateAttribute(event, 'audiochannel', $(event.currentTarget).val());
            this.triggerMethod('change:summaryprofile');
        },
        updateAudioSamplerate: function (event) {
            this.updateAttribute(event, 'audiosamplerate', $(event.currentTarget).val());
            this.triggerMethod('change:summaryprofile');
        },
        updateAudioBitrate: function (event) {
            this.updateAttribute(event, 'audiobitrate', $(event.currentTarget).val());
            this.triggerMethod('change:summaryprofile');
        },
        updateAudioVolumnMode: function (event) {
            var value = $(event.currentTarget).val();
            this.updateAttribute(event, 'audiovolumemode', value);
            var $profile = $(event.currentTarget).closest(".audioprofile");
            if (value == 0) {
                $profile.find('select[name="audioboostlevel"]').prop("disabled", true)
                    .end()
                    .find('select[name="audiobalancelevel"]').prop("disabled", true)
                    .end()
                    .find('input[name="audiobalancedb"]').prop("disabled", true);
            } else if (value == 1) {
                $profile.find('select[name="audioboostlevel"]').prop("disabled", false)
                    .end()
                    .find('select[name="audiobalancelevel"]').prop("disabled", true)
                    .end()
                    .find('input[name="audiobalancedb"]').prop("disabled", true);
            } else if (value == 2) {
                $profile.find('select[name="audioboostlevel"]').prop("disabled", true)
                    .end()
                    .find('select[name="audiobalancelevel"]').prop("disabled", false)
                    .end()
                    .find('input[name="audiobalancedb"]').prop("disabled", false);
            }
        },
        updateAudioBoostLevel: function (event) {
            this.updateAttribute(event, 'audioboostlevel', $(event.currentTarget).val());
        },
        updateAudioBalanceLevel: function (event) {
            this.updateAttribute(event, 'audiobalancelevel', $(event.currentTarget).val());
        },
        updateAudioBalanceDb: function (event) {
            this.updateAttribute(event, 'audiobalancedb', $(event.currentTarget).val());
        },
        updateAudioChannelProcessing: function (event) {
            this.updateAttribute(event, 'audiochannelprocessing', $(event.currentTarget).val());
        }
    });

    var VideoProfileCollection = Backbone.Collection.extend({
        model: function (attrs, options) {
            if (attrs.videocodec == "H264") {
                return new H264VideoProfileModel(attrs, options);
            } else if (attrs.videocodec == "MPEG4") {
                return new MPEG4VideoProfileModel(attrs, options);
            }
        }
    });

    var AudioProfileCollection = Backbone.Collection.extend({
        model: function (attrs, options) {
            if (attrs.audiocodec == "AAC") {
                return new AACAudioProfileModel(attrs, options);
            } else if (attrs.audiocodec == "MP2") {
                return new MP2AudioProfileModel(attrs, options);
            }

        }
    });

    var NoVideoProfileView = Marionette.ItemView.extend({
        template: "novideoprofileTemplate",
        events: {
            'click #videoprofiles-add-btn': 'addVideoProfile'
        },
        addVideoProfile: function () {
            this.triggerMethod('add:videoprofile');
        }
    });

    var SummaryProfileView = Marionette.ItemView.extend({
        template: 'summaryprofileTemplate',
        onChangeSummary: function () {
            this.render();
        },
        serializeData: function () {
            var vmodel = this.model.get('videoprofiles');
            var amodel = this.model.get('audioprofiles');
            var videoprofile, audioprofile;

            //default videoprofile
            if (vmodel.size() == 0) {
                videoprofile = null;
            } else {
                videoprofile = {
                    passthrough: vmodel.models[0].get('videopassthrough'),
                    videocodec: vmodel.models[0].get('videocodec'),		//"MPEG4",
                    videowidth: vmodel.models[0].get('videowidth'),		//640,
                    videoheight: vmodel.models[0].get('videoheight'),		//480,
                    videoratecontrol: vmodel.models[0].get('videoratecontrol'),	//"ABR",
                    videobitrate: vmodel.models[0].get('videobitrate')		//1000
                };
            }

            //default audioprofile
            if (amodel.size() == 0) {
                audioprofile = null;
            } else {
                audioprofile = {
                    passthrough: amodel.models[0].get('audiopassthrough'),
                    mixautio: amodel.models[0].get('audiomix'),
                    audiocodec: amodel.models[0].get('audiocodec'),		//"AAC",
                    audiochannel: amodel.models[0].get('audiochannel'),		//2,
                    audiosamplerate: amodel.models[0].get('audiosamplerate') / 1000,	//44100,
                    audiobitrate: amodel.models[0].get('audiobitrate') 		//64.0
                };
            }
            return {
                videoprofile: videoprofile,
                audioprofile: audioprofile
            };
        },
        initialize: function () {
        }
    });

    var VideoProfileCollectionView = Marionette.CollectionView.extend({
        childEvents: {
            'remove:videoprofile': function (view, model) {
                this.collection.remove(model);
                this.summaryProfileView.triggerMethod('change:summary');
            },
            'add:videoprofile': function (view) {
                this.collection.add(new H264VideoProfileModel());
                this.summaryProfileView.triggerMethod('change:summary');
            },
            'replace:videoprofile': function (view, model, newType) {
                this.collection.remove(model);
                if (newType == "MPEG4") {
                    this.collection.add(new MPEG4VideoProfileModel());
                } else if (newType == "H264") {
                    this.collection.add(new H264VideoProfileModel());
                }
                this.summaryProfileView.triggerMethod('change:summary');
            },
            'change:summaryprofile': function () {
                this.summaryProfileView.triggerMethod('change:summary');
            }
        },
        getChildView: function (model) {
            if (model.get('videocodec') == "H264") {
                return H264VideoProfileView;
            } else if (model.get('videocodec') == "MPEG4") {
                return MPEG4VideoProfileView;
            }
        },
        childViewOptions: function (model, index) {
            if (this.taskmodel) {
                return {
                    encodingoption: this.taskmodel.get('taskencodingoption')
                };
            } else {
                return {};
            }
        },
        emptyView: NoVideoProfileView,
        initialize: function (options) {
            if (options && options.taskmodel) {
                this.taskmodel = options.taskmodel;
            }
            if (options && options.summaryProfileView) {
                this.summaryProfileView = options.summaryProfileView;
            }
        }
    });

    var AudioProfileCollectionView = Marionette.CollectionView.extend({
        childEvents: {
            'remove:audioprofile': function (view, model) {
                this.collection.remove(model);
                this.summaryProfileView.triggerMethod('change:summary');
            },
            'replace:audioprofile': function (view, model, newType) {
                this.collection.remove(model);
                if (newType == "AAC") {
                    this.collection.add(new AACAudioProfileModel());
                } else if (newType == "MP2") {
                    this.collection.add(new MP2AudioProfileModel());
                }
                this.summaryProfileView.triggerMethod('change:summary');
            },
            'change:summaryprofile': function () {
                this.summaryProfileView.triggerMethod('change:summary');
            }
        },
        getChildView: function (model) {
            if (model.get('audiocodec') == "AAC") {
                return AACAudioProfileView;
            }
            else if (model.get('audiocodec') == "MP2") {
                return MP2AudioProfileView;
            }
        },
        addAudioProfile: function () {
            this.collection.add(new AACAudioProfileModel());
            this.summaryProfileView.triggerMethod('change:summary');
        },
        initialize: function (options) {
            this.summaryProfileView = options.summaryProfileView;
        }
    });

    var OutputProfileMainLayoutView = Marionette.LayoutView.extend({
        el: '#outputprofile-container',
        regions: {
            basic: '#outputprofile-basic',
            summary: '#outputprofile-summary',
            video: '#videostreamprofile',
            audio: '#audiostreamprofilelist'
        },
        ui: {
            addaudio: '#audioprofiles-add-btn'
        },
        events: {
            'click #outputprofile-save-btn': 'save',
            'click #outputprofile-back-btn': 'back',
            'click #audioprofiles-add-btn': 'addAudioProfile'
        },
        save: function () {
            if (this.model.isValid(true)) {
                sv.ajax.xhrHandler(this.model.save(), {
                    done: function () {
                        location.href = sv.urlPath.getRealPath('/profile/output');
                    }
                });
            }
        },
        back: function () {
            location.href = sv.urlPath.getRealPath('/profile/output');
        },
        addAudioProfile: function () {
            this.getChildView('audio').addAudioProfile();
        },
        template: false,
        onRender: function () {
            this.onAudioProfileCollectionChange();
            this.showChildView('basic', new ProfileBasicView({model: this.model}));
            var summaryProfileView = new SummaryProfileView({model: this.model});
            this.showChildView('summary', summaryProfileView);
            this.showChildView('video', new VideoProfileCollectionView({
                collection: this.model.get('videoprofiles'),
                summaryProfileView: summaryProfileView
            }));
            this.showChildView('audio', new AudioProfileCollectionView({
                collection: this.model.get('audioprofiles'),
                summaryProfileView: summaryProfileView
            }));
        },
        onAudioProfileCollectionChange: function () {
            if (this.model.get('audioprofiles').length == 0) {
                this.ui.addaudio.show();
            } else {
                this.ui.addaudio.hide();
            }
        },
        initialize: function () {
            this.listenTo(this.model.get('audioprofiles'), 'add', this.onAudioProfileCollectionChange);
            this.listenTo(this.model.get('audioprofiles'), 'remove', this.onAudioProfileCollectionChange);
        }
    });

    var OutputProfileCollection = Backbone.Collection.extend({
        url: function () {
            return sv.urlPath.getRealPath('/profile/output/all');
        },
        model: OutputProfileModel
    });

    var ListItem = Marionette.Behavior.extend({
        events: {
            'click @ui.selection': 'selectionChange'
        },
        selectionChange: function (event) {
            var selected = $(event.currentTarget).prop("checked");
            if (selected) {
                Backbone.trigger('list:item:selected', this.view.model);
            } else {
                Backbone.trigger('list:item:unselected', this.view.model);
            }
        }
    });

    Marionette.Behaviors.behaviorsLookup.ListItem = ListItem;

    var ListItemBaseView = Marionette.ItemView.extend({
        itemSelected: function () {
            this.ui.selection.prop("checked", true);
        },
        itemUnSelected: function () {
            this.ui.selection.prop("checked", false);
        },
        initialize: function (options) {
            this.listenTo(Backbone, 'list:items:selected', this.itemSelected);
            this.listenTo(Backbone, 'list:items:unselected', this.itemUnSelected);
        }
    });

    var OutputProfileListItemView = ListItemBaseView.extend({
        template: 'outputprofileitemTemplate',
        tagName: "tr",
        ui: {
            selection: '.select-one'
        },
        behaviors: {
            ListItem: {}
        }
    });

    var ListContainer = Marionette.Behavior.extend({
        events: {
            'click @ui.selectAll': 'selectAllOutputProfile'
        },
        selectAllOutputProfile: function () {
            var selected = this.ui.selectAll.prop("checked");
            if (selected) {
                this.view.selectedItem.reset(this.view.collection.models);
                Backbone.trigger('list:items:selected');
            } else {
                this.view.selectedItem.reset();
                Backbone.trigger('list:items:unselected');
            }
        },
    });

    Marionette.Behaviors.behaviorsLookup.ListContainer = ListContainer;

    var ListBaseView = Marionette.CompositeView.extend({
        itemSelected: function (model) {
            if (this.selectedItem.indexOf(model) == -1) {
                this.selectedItem.add(model);
            }
            if (this.selectedItem.size() == this.collection.size()) {
                this.ui.selectAll.prop("checked", true);
            }
        },
        itemUnSelected: function (model) {
            if (this.selectedItem.indexOf(model) != -1) {
                this.selectedItem.remove(model);
            }
            if (this.selectedItem.size() != this.collection.size()) {
                this.ui.selectAll.prop("checked", false);
            }
        },
        updateActionBar: function () {

        },
        onRender: function () {
            this.updateActionBar();
        },
        initialize: function (options) {
            this.listenTo(this.selectedItem, 'add', this.updateActionBar);
            this.listenTo(this.selectedItem, 'remove', this.updateActionBar);
            this.listenTo(this.selectedItem, 'reset', this.updateActionBar);
            this.listenTo(Backbone, 'list:item:selected', this.itemSelected);
            this.listenTo(Backbone, 'list:item:unselected', this.itemUnSelected);
        }
    });

    var OutputProfileListView = ListBaseView.extend({
        el: '#outputprofilelist-container',
        childView: OutputProfileListItemView,
        childViewContainer: 'tbody',
        template: 'outputprofilelistTemplate',
        ui: {
            selectAll: '#select-all',
            edit: '#outputprofile-edit-btn',
            copy: '#outputprofile-copy-btn',
            delete: '#outputprofile-delete-btn'
        },
        events: {
            'click #outputprofile-create-btn': 'createOutputProfile',
            'click @ui.edit': 'editOutputProfile',
            'click @ui.copy': 'copyOutputProfile',
            'click @ui.delete': 'deleteOutputProfile'
        },
        behaviors: {
            ListContainer: {}
        },
        createOutputProfile: function () {
            location.href = "output/new";
        },
        editOutputProfile: function () {
            if (this.selectedItem.size() == 1) {
                location.href = "output/edit/" + this.selectedItem.models[0].get('id');
            }
        },
        copyOutputProfile: function () {
            if (this.selectedItem.size() == 1) {
                location.href = "output/copy/" + this.selectedItem.models[0].get('id');
            }
        },
        deleteOutputProfile: function () {
        },
        updateActionBar: function () {
            if (this.selectedItem.size() == 0) {
                this.ui.edit.parent().addClass("disable");
                this.ui.copy.parent().addClass("disable");
                this.ui.delete.parent().addClass("disable");
            } else if (this.selectedItem.size() == 1) {
                this.ui.edit.parent().removeClass("disable");
                this.ui.copy.parent().removeClass("disable");
                this.ui.delete.parent().removeClass("disable");
            } else {
                this.ui.edit.parent().addClass("disable");
                this.ui.copy.parent().addClass("disable");
                this.ui.delete.parent().removeClass("disable");
            }
        },
        initialize: function (options) {
            this.selectedItem = new OutputProfileCollection();
            ListBaseView.prototype.initialize.apply(this, arguments);
        }
    });

    var OutputProfileStaticListView = Backbone.View.extend({
        el: '#outputprofilelist-container',
        events: {
            'click .select-one': 'selectionChange',
            'click tr': 'itemClick',
            'click #select-all': 'selectAllChange',
            'click #outputprofile-edit-btn': 'editOutputProfile',
            'click #outputprofile-copy-btn': 'copyOutputProfile',
            'click #outputprofile-delete-btn': 'deleteOutputProfile',
            'click #outputprofile-create-btn': 'createOutputProfile'
        },
        itemClick: function (event) {
            var $check = $(event.currentTarget).find(".select-one");
            if ($check.length > 0) {
                var id = $check.val();
                var prevSelected = $check.prop("checked");
                if (prevSelected) {
                    $check.prop("checked", false);
                } else {
                    $check.prop("checked", true);
                }
                this.itemSelectionChanged(!prevSelected, id);
            }
        },
        selectionChange: function (event) {
            var id = $(event.currentTarget).val();
            var selected = $(event.currentTarget).prop("checked");
            event.stopPropagation();
            this.itemSelectionChanged(selected, id);
        },
        itemSelectionChanged: function (selected, id) {
            if (selected) {
                if (!_.contains(this.selectedItem, id)) {
                    this.selectedItem.push(id);
                }
                var size = this.$table.find('input[name="id"]').length;
                if (this.selectedItem.length == size) {
                    this.$selectAll.prop("checked", true);
                }
            } else {
                var index = this.selectedItem.indexOf(id);
                if (index != -1) {
                    this.selectedItem.splice(index, 1);
                }
                this.$selectAll.prop("checked", false);
            }
            this.updateActionBar();
        },
        selectAllChange: function (event) {
            event.stopPropagation();
            var selected = $(event.currentTarget).prop("checked");
            var selectItem = this.selectedItem;
            if (selected) {
                this.$table.find('input[name="id"]').each(function () {
                    var id = $(this).val();
                    $(this).prop("checked", true);
                    selectItem.push(id);
                });
            } else {
                this.$table.find('input[name="id"]').each(function () {
                    $(this).prop("checked", false);
                });
                this.selectedItem = [];
            }
            this.updateActionBar();
        },
        createOutputProfile: function () {
            location.href = "output/new/0";
        },
        editOutputProfile: function () {
            location.href = "output/edit/" + this.selectedItem[0];
        },
        copyOutputProfile: function () {
            location.href = "output/copy/" + this.selectedItem[0];
        },
        deleteOutputProfile: function () {
            if (this.selectedItem.length) {
                var _selectedItem = this.selectedItem;
                sv.prompt.showConfirm2('是否删除所选记录？', function () {
                    sv.ajax.doDelete(sv.urlPath.getRealPath('/profile/output/' + _selectedItem.join(',')))
                        .always(function () {
                            location.reload();
                        });
                });
            }
        },
        updateActionBar: function () {
            if (this.selectedItem.length == 0) {
                this.$edit.parent().addClass("disable");
                this.$copy.parent().addClass("disable");
                this.$delete.parent().addClass("disable");
            } else if (this.selectedItem.length == 1) {
                this.$edit.parent().removeClass("disable");
                this.$copy.parent().removeClass("disable");
                this.$delete.parent().removeClass("disable");
            } else {
                this.$edit.parent().addClass("disable");
                this.$copy.parent().addClass("disable");
                this.$delete.parent().removeClass("disable");
            }
        },
        initialize: function () {
            var selectedItem = [];
            this.$edit = this.$el.find("#outputprofile-edit-btn");
            this.$copy = this.$el.find("#outputprofile-copy-btn");
            this.$delete = this.$el.find("#outputprofile-delete-btn");
            this.$selectAll = this.$el.find("#select-all");
            this.$table = this.$el.find("#outputprofilelist");
            this.$table.find('input[name="id"]').each(function () {
                var selected = $(this).prop("checked");
                var id = $(this).val();
                if (selected) {
                    selectedItem.push(id);
                }
            });
            this.selectedItem = selectedItem;
            this.updateActionBar();
        }
    });

    return {
        OutputProfileModel: OutputProfileModel,
        OutputProfileCollection: OutputProfileCollection,
        VideoProfileCollectionView: VideoProfileCollectionView,
        AudioProfileCollectionView: AudioProfileCollectionView,
        SummaryProfileView: SummaryProfileView,
        ListItemBaseView: ListItemBaseView,
        ListBaseView: ListBaseView,
        initItem: function () {
            var attrs = state.item || {};
            var model = new OutputProfileModel(attrs, {parse: true});
            var view = new OutputProfileMainLayoutView({model: model});
            view.render();
        },

        initList: function () {
            // var models = state.list || [];
            // var collection = new OutputProfileCollection(models, {parse: true});
            // var view = new OutputProfileListView({collection: collection});
            // view.render();
            var view = new OutputProfileStaticListView();
        }
    };
}(jQuery, _, Backbone, Backbone.Validation, TipsyValidationCallback, Marionette, state, window.sv));

var TaskProfile = (function ($, _, Backbone, Validation, TipsyValidationCallback, Marionette, OutputProfile, state, sv) {
    var TsOptionModel = Backbone.Model.extend({
        defaults: {
            tsservicename: '',
            tspmtpid: '',
            tsserviceprovider: '',
            tsvideopid: '',
            tsserviceid: '',
            tsaudiopid: '',
            tstotalbitrate: '',
            tspcrpid: '',
            tsnetworkid: '',
            tstransportid: '',
            tsinserttottdt: false,
            tstottdtperiod: '',
            tspcrperiod: '',
            tspatperiod: '',
            tssdtperiod: '',
            tsprivatemetadatapid: '',
            tsprivatemetadatatype: ''
        }
    });

    var FileArchiveOutputModel = Backbone.Model.extend({
        defaults: {
            outputtype: 'FileArchive',
            outputcontainer: 'MP4'
        }
    });

    var UdpStreamingOutputModel = Backbone.Model.extend({
        defaults: function () {
            return {
                outputtype: 'UdpStreaming',
                outputcontainer: 'UDPOverTS',
                outputDest: 0,
                outputbuffersize: 65535,
                outputTTL: 255,
                outputtsoption: new TsOptionModel()
            };
        },
        parse: function (resp, options) {
            if (resp.outputtsoption) {
                resp.outputtsoption = new TsOptionModel(resp.outputtsoption);
            }
            return resp;
        },
        toJSON: function (options) {
            var clone = _.clone(this.attributes);
            if (clone.outputtsoption) {
                clone.outputtsoption = clone.outputtsoption.toJSON();
            }
            return clone;
        },
        clone: function () {
            var cloneattr = _.clone(this.attributes);
            if (cloneattr.outputtsoption) {
                cloneattr.outputtsoption = cloneattr.outputtsoption.clone();
            }
            return new this.constructor(cloneattr);
        }
    });

    var FlashStreamingOutputModel = Backbone.Model.extend({
        defaults: function () {
            return {
                outputtype: 'FlashStreaming',
                outputcontainer: 'RTMP',
                outputDest: 0,
//				outputbuffersize: 65535,
//				outputTTL: 255,
                outputtsoption: new TsOptionModel()
            };
        },
        parse: function (resp, options) {
            if (resp.outputtsoption) {
                resp.outputtsoption = new TsOptionModel(resp.outputtsoption);
            }
            return resp;
        },
        toJSON: function (options) {
            var clone = _.clone(this.attributes);
            if (clone.outputtsoption) {
                clone.outputtsoption = clone.outputtsoption.toJSON();
            }
            return clone;
        },
        clone: function () {
            var cloneattr = _.clone(this.attributes);
            if (cloneattr.outputtsoption) {
                cloneattr.outputtsoption = cloneattr.outputtsoption.clone();
            }
            return new this.constructor(cloneattr);
        }
    });

    var TaskOutputCollection = Backbone.Collection.extend({
        model: function (attrs, options) {
            if (attrs.outputtype == 'FileArchive') {
                return new FileArchiveOutputModel(attrs, options);
            }
            else if (attrs.outputtype == 'UdpStreaming') {
                return new UdpStreamingOutputModel(attrs, options);
            }
            else if (attrs.outputtype == 'FlashStreaming') {
                return new FlashStreamingOutputModel(attrs, options);
            }
        }
    });

    var TaskProfileModel = Backbone.Model.extend({
        urlRoot: '/profile/task',
        validation: {
            taskname: {
                required: true,
                msg: '名称不能为空'
            },
            taskoutputs: [{
                fn: function (value, attr, computedState) {
                    if (value.length == 0) {
                        return '输出不能为空';
                    }
                }
            }, {
                fn: function (value, attr, computedState) {
                    if (value.length == 2) {
                        var type1 = value.at(0).get('outputDest');
                        var type2 = value.at(1).get('outputDest');
                        if (type1 == type2) {
                            return '至多可以有一个输出到屏幕和一个输出到移动端';
                        }
                    }
                }
            }, {
                fn: function (value, attr, computedState) {
                    var collection = value;
                    var setted = _.every(collection.models, function (model) {
                        return model.get('linkedprofile') !== undefined && model.get('linkedprofile') !== null;
                    });
                    if (!setted) {
                        return '输出未全部关联输出流参数';
                    }
                }
            }]
        },
        defaults: function () {
            var outputprofilemodel = new OutputProfile.OutputProfileModel();
            return {
                'taskencodingoption': 'Custom',
                'taskpriority': 5,
                'taskgpucores': 1,
                'enableGpu': sv._config.gpuConfig.enableSpan,
                'allowProgramIdChange': false,
                'taskinputrow': 4,
                'taskinputcolumn': 4,
                'taskoutputprofiles': new OutputProfile.OutputProfileCollection([outputprofilemodel]),
                'taskoutputs': new TaskOutputCollection([new UdpStreamingOutputModel({linkedprofile: outputprofilemodel})])
            };
        },
        parse: function (resp, options) {
            if (resp.taskoutputprofiles) {
                var ocol = new OutputProfile.OutputProfileCollection();
                _.each(resp.taskoutputprofiles, function (attr) {
                    ocol.add(attr, options);
                });
                resp.taskoutputprofiles = ocol;
            }
            if (resp.taskoutputs) {
                var opcol = new TaskOutputCollection();
                _.each(resp.taskoutputs, function (attr) {
                    if (attr.linkedprofile >= 0) {
                        attr.linkedprofile = resp.taskoutputprofiles.at(attr.linkedprofile);
                    } else {
                        attr.linkedprofile = null;
                    }
                    opcol.add(attr, options);
                });
                resp.taskoutputs = opcol;
            }
            return resp;
        },
        toJSON: function (options) {
            var clone = _.clone(this.attributes);
            if (clone.taskoutputprofiles) {
                clone.taskoutputprofiles = clone.taskoutputprofiles.toJSON();
            }
            if (clone.taskoutputs) {
                var taskoutputs = _.map(clone.taskoutputs.models, function (model) {
                    var cl = _.clone(model.attributes);
                    if (cl.linkedprofile) {
                        cl.linkedprofile = this.get('taskoutputprofiles').indexOf(cl.linkedprofile);
                    } else {
                        cl.linkedprofile = -1;
                    }
                    cl.uniqueType = cl.outputtype + '-' + cl.outputcontainer;
                    return cl;
                }, this);
                clone.taskoutputs = taskoutputs;
            }
            return clone;
        },
        set: function (key, value, options) {
            options || (options = {});
            options = _.extend(options, {forceUpdate: true});
            return Backbone.Model.prototype.set.call(this, key, value, options);
        }
    });

    var TaskProfileValidationView = Marionette.ItemView.extend({
        template: 'validationTemplate',
        validation: ['taskoutputs'],
        valid: function (view, attr, selector) {
        },
        invalid: function (view, attr, error, selector) {
        },
        onInvalid: function (model, errors) {
            this.errors = _.values(_.pick(errors, this.validation));
            this.render();
        },
        serializeData: function () {
            return {
                errors: this.errors
            };
        },
        initialize: function () {
            Validation.bind(this, this);
            this.listenTo(this.model, 'validated:invalid', this.onInvalid);
            this.errors = [];
        },
        onRender: function () {
            if (this.errors.length > 0) {
                $('html, body').scrollTop(0);
            }
        },
        onDestroy: function () {
            Validation.unbind(this);
        }
    });

    var TaskProfileBasicView = Marionette.ItemView.extend({
        template: 'basicTemplate',
        events: {
            'change input[name="taskname"]': 'updateTaskName',
            'change input[name="taskdescription"]': 'updateTaskDescription',
            'change select[name="taskencodingoption"]': 'updateTaskEncodingOption',
            'change select[name="taskpriority"]': 'updateTaskPriority',
            'change select[name="taskgpucores"]': 'updateTaskGPUCores'
        },
        updateTaskName: function (event) {
            this.model.set('taskname', $(event.currentTarget).val(), {validate: true});
        },
        updateTaskDescription: function (event) {
            this.model.set('taskdescription', $(event.currentTarget).val());
        },
        updateTaskEncodingOption: function (event) {
            var encodingoption = $(event.currentTarget).val();
            this.model.set('taskencodingoption', encodingoption);
            Backbone.trigger('task:encodingoption:change', encodingoption);
        },
        updateTaskPriority: function (event) {
            this.model.set('taskpriority', $(event.currentTarget).val());
        },
        updateTaskGPUCores: function (event) {
            this.model.set('taskgpucores', $(event.currentTarget).val());
        },
        initialize: function () {
            Validation.bind(this, TipsyValidationCallback);
        },
        onDestroy: function () {
            Validation.unbind(this);
        }
    });

    var TaskProfileSaveOutputProfileView = Marionette.ItemView.extend({
        template: 'taskoutputprofilesaveTemplate',
        attributes: {
            class: 'modal',
            id: 'saveoutputprofile-modal'
        },
        ui: {
            name: 'input[name="name"]',
            description: 'textarea[name="description"]'
        },
        events: {
            'click #saveoutputprofile-cancel-btn': 'closeModal',
            'click #saveoutputprofile-ok-btn': 'saveModel',
            'change input[name="name"]': 'updateName',
            'change textarea[name="description"]': 'updateDescription'
        },
        openModal: function (model) {
            this.model = model;
            Validation.bind(this, TipsyValidationCallback);
            this.clearContent();
            this.$el.modal({
                showClose: false,
                clickClose: false
            });
        },
        clearContent: function () {
            this.ui.name.val("");
            this.ui.description.val("");
        },
        closeModal: function () {
            Validation.unbind(this);
            this.$el.find('.error').each(function () {
                $(this).removeClass('error');
                $(this).tipsy('hide').removeAttr('original-title');
            });
            $.modal.close();
        },
        updateName: function (event) {
            this.model.set('name', $(event.currentTarget).val(), {validate: true});
        },
        updateDescription: function (event) {
            this.model.set('description', $(event.currentTarget).val());
        },
        saveModel: function () {
            if (this.model.isValid(true)) {
                var that = this;

                sv.ajax.xhrHandler(this.model.save(), {
                    done: function () {
                        that.closeModal();
                    }
                });
            }
        }
    });

    var TaskProfileOutputProfileItemLayoutView = Marionette.LayoutView.extend({
        template: 'taskoutputprofileTemplate',
        regions: {
            'summary': '.outputprofile-summary',
            'saveModal': '.saveoutputmodal-container',
            'outputprofilevideo': '.outputprofile-video',
            'outputprofileaudio': '.outputprofile-audio'
        },
        behaviors: {
            SectionTrigger: {}
        },
        ui: {
            addaudio: '.audioprofiles-add-btn'
        },
        events: {
            'click .audioprofiles-add-btn': 'addAudioProfile',
            'click .outputprofile-remove-btn': 'removeOutputProfile',
            'click .outputprofile-save-btn': 'saveOutputProfile'
        },
        addAudioProfile: function () {
            this.getChildView('outputprofileaudio').addAudioProfile();
        },
        removeOutputProfile: function () {
            this.triggerMethod('outputprofile:remove', this.model);
        },
        saveOutputProfile: function () {
            var view = new TaskProfileSaveOutputProfileView();
            this.showChildView('saveModal', view);
            view.openModal(new OutputProfile.OutputProfileModel(this.model.toJSON(), {parse: true}));
            /* if (!this.getChildView('saveModal')) {
             var view = new TaskProfileSaveOutputProfileView();
             this.showChildView('saveModal', view);
             view.openModal(new OutputProfile.OutputProfileModel(this.model.toJSON(), {parse: true}));
             } else {
             this.getChildView('saveModal').openModal(new OutputProfile.OutputProfileModel(this.model.clone(), {parse: true}));
             }*/
        },
        onAudioProfileCollectionChange: function () {
            if (this.model.get('audioprofiles').length == 0) {
                this.ui.addaudio.show();
            } else {
                this.ui.addaudio.hide();
            }
        },
        onRender: function () {
            this.onAudioProfileCollectionChange();
            var summaryProfileView = new OutputProfile.SummaryProfileView({model: this.model});
            this.showChildView('summary', summaryProfileView);
            this.showChildView('outputprofilevideo', new OutputProfile.VideoProfileCollectionView({
                collection: this.model.get('videoprofiles'),
                taskmodel: this.taskmodel,
                summaryProfileView: summaryProfileView
            }));
            this.showChildView('outputprofileaudio', new OutputProfile.AudioProfileCollectionView({
                collection: this.model.get('audioprofiles'),
                summaryProfileView: summaryProfileView
            }));
        },
        initialize: function (options) {
            if (options && options.taskmodel) {
                this.taskmodel = options.taskmodel;
            }
            this.listenTo(this.model.get('audioprofiles'), 'add', this.onAudioProfileCollectionChange);
            this.listenTo(this.model.get('audioprofiles'), 'remove', this.onAudioProfileCollectionChange);
        }
    });

    var TaskProfileOutputProfileCollectionView = Marionette.CollectionView.extend({
        childView: TaskProfileOutputProfileItemLayoutView,
        childEvents: {
            'outputprofile:remove': function (view, model) {
                if (this.shouldBeRemoved(model)) {
                    this.collection.remove(model);
                } else {
                    window.alert("输出参数流已被使用");
                }
            }
        },
        childViewOptions: function (model, index) {
            return {
                taskmodel: this.model
            };
        },
        addOutputProfile: function () {
            this.collection.add(new OutputProfile.OutputProfileModel());
        },
        addImportedOutputProfile: function (model) {
            if (model) {
                var add = new OutputProfile.OutputProfileModel(model.toJSON(), {parse: true, ignore: ['id']});
                this.collection.add(add);
            }
        },
        shouldBeRemoved: function (model) {
            return this.model.get('taskoutputs').reduce(function (init, item) {
                var used = item.get('linkedprofile') == model;
                return init && !used;
            }, true);
        },
        initialize: function () {
            this.listenTo(Backbone, 'importitem:add', this.addImportedOutputProfile);
        }
    });

    var TaskOutputTabItemView = Marionette.ItemView.extend({
        template: 'taskoutputtabitemTemplate',
        attributes: {
            class: 'outputtab'
        },
        ui: {
            remove: '.outputtabitem-remove-btn'
        },
        events: {
            'click .tab-content': 'itemSelected',
            'click @ui.remove': 'itemRemove'
        },
        itemSelected: function () {
            this.triggerMethod('item:selected', this.model);
        },
        selected: function () {
            this.$el.addClass("active");
            this.ui.remove.show();
        },
        unselected: function () {
            this.$el.removeClass("active");
            this.ui.remove.hide();
        },
        itemRemove: function () {
            this.triggerMethod('item:remove', this.model);
        },
        serializeData: function () {
            return {
                index: this.index
            };
        },
        indexChanged: function (collection) {
            var index = collection.indexOf(this.model);
            this.index = index + 1;
            this.render();
        },
        initialize: function (options) {
            this.index = options.index;
            this.listenTo(Backbone, "index:change", this.indexChanged);
        }
    });

    var TaskOutputTabsView = Marionette.CompositeView.extend({
        template: 'taskoutputtabsTemplate',
        childView: TaskOutputTabItemView,
        childViewContainer: '#outputtabs',
        ui: {
            tabsWidth: '#outputtabs-width',
            prev: '#outputtabs-prev-btn',
            next: '#outputtabs-next-btn'
        },
        events: {
            'click @ui.prev': 'prevPage',
            'click @ui.next': 'nextPage'
        },
        childEvents: {
            'item:selected': function (view, model) {
                this.setSelected(model);
            },
            'item:remove': function (view, model) {
                var index = this.collection.indexOf(model);
                this.collection.remove(model);
                var size = this.collection.size();
                if (index >= size) {
                    this.setSelected(this.collection.at(size - 1));
                } else {
                    this.setSelected(this.collection.at(index));
                }
                if (size < this.currentPage * 8 + 1) {
                    this.prevPage();
                } else {
                    Backbone.trigger('index:change', this.collection);
                }
            }
        },
        childViewOptions: function (model, index) {
            return {
                index: index + 1
            };
        },
        pageSize: 8,
        currentPage: 0,
        addChild: function (child, ChildView, index) {
            if (this.shouldBeShown(child, ChildView, index)) {
                Marionette.CollectionView.prototype.addChild.apply(this, arguments);
            }
        },
        shouldBeShown: function (child, ChildView, index) {
            return index >= this.currentPage * this.pageSize && index < (this.currentPage + 1) * this.pageSize;
        },
        copyOutput: function () {
            if (this.selected) {
                var newItem = this.selected.clone();
                this.collection.add(newItem);
                this.setSelected(newItem);
            }
        },
        addOutput: function () {
            var newItem = new UdpStreamingOutputModel();
            this.collection.add(newItem);
            this.setSelected(newItem);
        },
        setSelected: function (child) {
            if (this.selected != child) {
                this.selected = child;
                this.updateSelection(child);
                Backbone.trigger('change:output:selected', child);
            }
        },
        initialize: function () {
            if (this.collection.size() > 0) {
                this.setSelected(this.collection.at(0));
            }
            this.listenTo(this.collection, 'remove', this.render);
            this.listenTo(this.collection, 'add', this.updatePageButton);
        },
        nextPage: function () {
            var totalPage = Math.ceil(this.collection.size() / this.pageSize);
            if (this.currentPage < totalPage - 1) {
                this.currentPage = this.currentPage + 1;
                this.render();
            }
        },
        prevPage: function () {
            if (this.currentPage > 0) {
                this.currentPage = this.currentPage - 1;
                this.render();
            }
        },
        updatePageButton: function () {
            if (this.collection.size() > this.pageSize) {
                this.ui.tabsWidth.width(this.$el.width() - this.ui.prev.width() - this.ui.next.width());
                this.ui.prev.show();
                this.ui.next.show();
            } else {
                var width = this.$el.width();
                if (width != 0) {
                    this.ui.tabsWidth.width(width);
                }
                this.ui.prev.hide();
                this.ui.next.hide();
            }
        },
        updateSelection: function (selected) {
            this.children.each(function (view) {
                if (view.model != selected) {
                    view.unselected();
                } else {
                    view.selected();
                }
            });
        },
        onRender: function () {
            this.updatePageButton();
            this.updateSelection(this.selected);
        }
    });

    var TaskOutputBaseView = Marionette.ItemView.extend({
        initialize: function (options) {
            this.taskprofile = options.taskprofile;
            this.listenTo(this.taskprofile.get('taskoutputprofiles'), 'add', this.render);
            this.listenTo(this.taskprofile.get('taskoutputprofiles'), 'remove', this.render);
            this.listenTo(this.model, 'change', this.render);
        },
        serializeData: function () {
            var clone = _.clone(this.model.attributes);
            if (clone.linkedprofile) {
                clone.linkedprofile = this.taskprofile.get('taskoutputprofiles').indexOf(clone.linkedprofile);
            } else {
                clone.linkedprofile = -1;
            }
            var outputprofiles = this.taskprofile.get('taskoutputprofiles').map(function (value, index) {
                return {
                    value: index,
                    name: index + 1
                };
            });
            clone.outputprofiles = outputprofiles;
            if (clone.outputtsoption) {
                clone.outputtsoption = clone.outputtsoption.toJSON();
            }
            return clone;
        }
    });

    var OutputCommon = Marionette.Behavior.extend({
        events: {
            'change select[name="linkedprofile"]': 'updateLinkedProfile',
            'click #output-stream-add-btn': 'addOutputStream',
            'click #output-stream-remove-btn': 'removeOutputStream'
        },
        updateLinkedProfile: function (event) {
            var index = $(event.currentTarget).val();
            this.view.model.set('linkedprofile', this.view.taskprofile.get('taskoutputprofiles').at(index));
        },
        addOutputStream: function () {
            var outputprofiles = this.view.taskprofile.get('taskoutputprofiles');
            if (outputprofiles.size() > 0) {
                this.view.model.set('linkedprofile', outputprofiles.at(0));
            } else {
                window.alert("没有设定输出流参数");
            }

        },
        removeOutputStream: function () {
            this.view.model.set('linkedprofile', null);
        }
    });

    var TsOption = Marionette.Behavior.extend({
        events: {
            'change input[name="tsservicename"]': 'updateTsServiceName',
            'change input[name="tspmtpid"]': 'updateTsPMTID',
            'change input[name="tsserviceprovider"]': 'updateTsServiceProvider',
            'change input[name="tsvideopid"]': 'updateTsVideoID',
            'change input[name="tsserviceid"]': 'updateServiceID',
            'change input[name="tsaudiopid"]': 'updateAudioID',
            'change input[name="tstotalbitrate"]': 'updateTotalBitrate',
            'change input[name="tspcrpid"]': 'updatePCRPID',
            'change input[name="tsnetworkid"]': 'updateNetworkID',
            'change input[name="tstransportid"]': 'updateTransportID',
            'change input[name="tsinserttottdt"]': 'updateInsertTOTTDT',
            'change input[name="tstottdtperiod"]': 'updateTOTTDTPeriod',
            'change input[name="tspcrperiod"]': 'updatePCRPeriod',
            'change input[name="tspatperiod"]': 'updatePATPeriod',
            'change input[name="tssdtperiod"]': 'updateSDTPeriod',
            'change input[name="tsprivatemetadatapid"]': 'updatePrivateMetaDataPID',
            'change input[name="tsprivatemetadatatype"]': 'updatePrivateMetaDataType'
        },
        updateTsServiceName: function (event) {
            this.view.model.get('outputtsoption').set('tsservicename', $(event.currentTarget).val());
        },
        updateTsPMTID: function (event) {
            this.view.model.get('outputtsoption').set('tspmtpid', $(event.currentTarget).val());
        },
        updateTsServiceProvider: function (event) {
            this.view.model.get('outputtsoption').set('tsserviceprovider', $(event.currentTarget).val());
        },
        updateTsVideoID: function (event) {
            this.view.model.get('outputtsoption').set('tsvideopid', $(event.currentTarget).val());
        },
        updateServiceID: function (event) {
            this.view.model.get('outputtsoption').set('tsserviceid', $(event.currentTarget).val());
        },
        updateAudioID: function (event) {
            this.view.model.get('outputtsoption').set('tsaudiopid', $(event.currentTarget).val());
        },
        updateTotalBitrate: function (event) {
            this.view.model.get('outputtsoption').set('tstotalbitrate', $(event.currentTarget).val());
        },
        updatePCRPID: function (event) {
            this.view.model.get('outputtsoption').set('tspcrpid', $(event.currentTarget).val());
        },
        updateNetworkID: function (event) {
            this.view.model.get('outputtsoption').set('tsnetworkid', $(event.currentTarget).val());
        },
        updateTransportID: function (event) {
            this.view.model.get('outputtsoption').set('tstransportid', $(event.currentTarget).val());
        },
        updateInsertTOTTDT: function (event) {
            this.view.model.get('outputtsoption').set('tsinserttottdt', $(event.currentTarget).prop("checked"));
        },
        updateTOTTDTPeriod: function (event) {
            this.view.model.get('outputtsoption').set('tstottdtperiod', $(event.currentTarget).val());
        },
        updatePCRPeriod: function (event) {
            this.view.model.get('outputtsoption').set('tspcrperiod', $(event.currentTarget).val());
        },
        updatePATPeriod: function (event) {
            this.view.model.get('outputtsoption').set('tspatperiod', $(event.currentTarget).val());
        },
        updateSDTPeriod: function (event) {
            this.view.model.get('outputtsoption').set('tssdtperiod', $(event.currentTarget).val());
        },
        updatePrivateMetaDataPID: function (event) {
            this.view.model.get('outputtsoption').set('tsprivatemetadatapid', $(event.currentTarget).val());
        },
        updatePrivateMetaDataType: function (event) {
            this.view.model.get('outputtsoption').set('tsprivatemetadatatype', $(event.currentTarget).val());
        }
    });

    Marionette.Behaviors.behaviorsLookup.OutputCommon = OutputCommon;
    Marionette.Behaviors.behaviorsLookup.TsOption = TsOption;

    var FileArchiveOutputView = TaskOutputBaseView.extend({
        template: 'filearchiveoutputTemplate',
        behaviors: {
            OutputCommon: {}
        }
    });

    var UdpStreamingOutputView = TaskOutputBaseView.extend({
        template: 'udpstreamingoutputTemplate',
        behaviors: {
            OutputCommon: {},
            TsOption: {}
        },
        events: {
            'change input[name="outputdescription"]': 'updateOutputDescription',
            'change select[name="outputDest"]': 'updateOutputDest',
            'change input[name="outputbuffersize"]': 'updateOutputBufferSize',
            'change input[name="outputTTL"]': 'updateOutputTTL',
            'change input[name="outputIGMP"]': 'updateOutputIGMP',
            'change select[name="outputtype"]': 'outputTypeSelectChange',
        },
        updateOutputDescription: function (event) {
            this.model.set('outputdescription', $(event.currentTarget).val());
        },
        updateOutputDest: function (event) {
            this.model.set('outputDest', $(event.currentTarget).val());
        },
        updateOutputBufferSize: function (event) {
            this.model.set('outputbuffersize', $(event.currentTarget).val());
        },
        updateOutputTTL: function (event) {
            this.model.set('outputTTL', $(event.currentTarget).val());
        },
        updateOutputIGMP: function (event) {
            this.model.set('outputIGMP', $(event.currentTarget).val());
        },
        outputTypeSelectChange: function (event) {
            var select = $(event.currentTarget).val();
            var model = undefined;
            if (select == "UdpStreaming")
                model = new UdpStreamingOutputModel();
            else if (select == "FlashStreaming")
                model = new FlashStreamingOutputModel();
            this.model.set('outputDest', $('[name="outputDest"]').val());
            Backbone.trigger('change:outputtype:selected', this.model, model);
        },

    });

    var FlashStreamingOutputView = TaskOutputBaseView.extend({
        template: 'flashstreamingoutputTemplate',
        behaviors: {
            OutputCommon: {},
        },
        events: {
            'change input[name="outputdescription"]': 'updateOutputDescription',
            'change select[name="outputDest"]': 'updateOutputDest',
            'change select[name="outputtype"]': 'outputTypeSelectChange',
//			'change input[name="outputbuffersize"]': 'updateOutputBufferSize',
//			'change input[name="outputTTL"]': 'updateOutputTTL',
//			'change input[name="outputIGMP"]': 'updateOutputIGMP'
        },
        updateOutputDescription: function (event) {
            this.model.set('outputdescription', $(event.currentTarget).val());
        },
        updateOutputDest: function (event) {
            this.model.set('outputDest', $(event.currentTarget).val());
        },
        outputTypeSelectChange: function (event) {
            var select = $(event.currentTarget).val();
            var model = undefined;
            if (select == "UdpStreaming")
                model = new UdpStreamingOutputModel();
            else if (select == "FlashStreaming")
                model = new FlashStreamingOutputModel();
            this.model.set('outputDest', $('[name="outputDest"]').val());
            Backbone.trigger('change:outputtype:selected', this.model, model);
        }
//		updateOutputBufferSize: function(event) {
//			this.model.set('outputbuffersize', $(event.currentTarget).val());
//		},
//		updateOutputTTL: function(event) {
//			this.model.set('outputTTL', $(event.currentTarget).val());
//		},
//		updateOutputIGMP: function(event) {
//			this.model.set('outputIGMP', $(event.currentTarget).val());
//		}
    });

    var TaskOutputLayoutView = Marionette.LayoutView.extend({
        template: 'taskoutputTemplate',
        regions: {
            outputtabs: '#outputtabs-container',
            outputcurrent: '#output-current'
        },
        ui: {
            copy: '#outputtabs-copy-btn',
            add: '#outputtabs-add-btn',
        },
        events: {
            'click @ui.copy': 'copyOutput',
            'click @ui.add': 'addOutput'
        },
        copyOutput: function () {
            this.getChildView('outputtabs').copyOutput();
        },
        addOutput: function () {
            this.getChildView('outputtabs').addOutput();
        },
        initialize: function () {
            this.listenTo(Backbone, 'change:output:selected', this.renderOnSelectionChanged);
            this.listenTo(this.model.get('taskoutputs'), 'add', this.renderCopyAddButton);
            this.listenTo(this.model.get('taskoutputs'), 'remove', this.renderCopyAddButton);
            this.listenTo(Backbone, 'change:outputtype:selected', this.renderOnSelection);
        },
        renderOnSelection: function (oldModel, newModel) {
            var collection = this.model.get('taskoutputs');
            var index = collection.indexOf(oldModel);
            collection.remove(oldModel);
            collection.add(newModel, {at: index});
            this.getChildView('outputtabs').setSelected(newModel);
        },
        renderOnSelectionChanged: function (child) {
            //var outputDest=$('[name="outputDest"]').val();
            if (child) {
                if (this.model.get('taskoutputs').length < 2) {
                    this.ui.copy.show();
                }
                var outputtype = child.get('outputtype');
                if (outputtype == "FileArchive") {
                    this.showChildView('outputcurrent', new FileArchiveOutputView({
                        model: child,
                        taskprofile: this.model
                    }));
                } else if (outputtype == "UdpStreaming") {
                    this.showChildView('outputcurrent', new UdpStreamingOutputView({
                        model: child,
                        taskprofile: this.model
                    }));
                } else if (outputtype == "FlashStreaming") {
                    this.showChildView('outputcurrent', new FlashStreamingOutputView({
                        model: child,
                        taskprofile: this.model
                    }));
                }
                //$('[name="outputDest"]').val(outputDest);
            } else {
                this.ui.copy.hide();
                this.getRegion('outputcurrent').empty();
            }
        },
        renderCopyAddButton: function () {
            if (this.model.get('taskoutputs').length < 2) {
                this.ui.copy.show();
                this.ui.add.show();
            } else {
                this.ui.copy.hide();
                this.ui.add.hide();
            }
        },
        onRender: function () {
            this.renderCopyAddButton();
            this.showChildView('outputtabs', new TaskOutputTabsView({collection: this.model.get('taskoutputs')}));
        }
    });

    var TaskProfileInputView = Marionette.ItemView.extend({
        template: 'taskinputTemplate',
        serializeData: function () {
            var row = this.model.get('taskinputrow');
            var column = this.model.get('taskinputcolumn');
            var rowcolumn = row + "X" + column;
            return {
                taskinputrowcolumn: rowcolumn,
                allowProgramIdChange: this.model.get("allowProgramIdChange")
            };
        },
        events: {
            'change select[name="taskinputrowcolumn"]': 'updateTaskInputRowColumn',
            'change input[name="taskinputallowprogramidchange"]': 'updateAllowProgramIdChange'
        },
        updateTaskInputRowColumn: function (event) {
            var value = $(event.currentTarget).val();
            var row = 4, column = 4;
            var re = /(\d+)X(\d+)/;
            var m;

            if ((m = re.exec(value)) !== null) {
                row = m[1];
                column = m[2];
            }
            this.model.set('taskinputrow', row);
            this.model.set('taskinputcolumn', column);
        },
        updateAllowProgramIdChange: function (event) {
            this.model.set('allowProgramIdChange', $(event.currentTarget).prop("checked"));
        }
    });

    var TaskProfileImportItemView = Marionette.ItemView.extend({
        template: 'importItemTemplate',
        ui: {
            select: 'input[name="import-item-select"]'
        },
        events: {
            'click @ui.select': 'onCheckboxClicked',
            'click .import-item': 'onItemClicked'
        },
        onCheckboxClicked: function (event) {
            var selected = $(event.currentTarget).prop("checked");
            if (selected) {
                this.triggerMethod("importitem:selected");
            } else {
                this.triggerMethod("importitem:unselected");
            }
            event.stopPropagation();
        },
        onItemClicked: function (event) {
            var selected = this.ui.select.prop("checked");
            if (selected) {
                this.ui.select.prop("checked", false);
                this.triggerMethod("importitem:unselected");
            } else {
                this.ui.select.prop("checked", true);
                this.triggerMethod("importitem:selected");
            }
        },
        unselected: function () {
            this.ui.select.prop("checked", false);
        }
    });

    var TaskProfileImportListView = Marionette.CompositeView.extend({
        childView: TaskProfileImportItemView,
        childViewContainer: '#importlist-container',
        template: 'importListTemplate',
        ui: {
            select: '#importlist-select-btn',
            cancel: '#importlist-cancel-btn'
        },
        attributes: {
            class: 'modal',
            id: 'importlist-modal'
        },
        events: {
            'click @ui.select': 'addSelectedItem',
            'click @ui.cancel': 'closeModal'
        },
        childEvents: {
            'importitem:selected': function (view) {
                this.children.each(function (child) {
                    if (view != child) {
                        child.unselected();
                    }
                });
                this.selectedItem = view.model;
            },
            'importitem:unselected': function (view) {
                this.selectedItem = null;
            }
        },
        openModal: function () {
            // this.collection.fetch({
            // 		parse: true,
            // 		success: function(collection, response, options) {
            // 			view.modal();
            // 		}
            // 	});
            this.clearSelection();
            this.$el.modal({
                showClose: false,
                clickClose: false
            });
        },
        closeModal: function () {
            $.modal.close();
        },
        clearSelection: function () {
            this.children.each(function (child) {
                child.unselected();
            });
        },
        addSelectedItem: function () {
            Backbone.trigger("importitem:add", this.selectedItem);
            $.modal.close();
        },
        initialize: function () {
            this.selectedItem = null;
        }
    });

    var TaskProfileMainLayoutView = Marionette.LayoutView.extend({
        el: '#taskprofile-container',
        regions: {
            validation: '#global-validation-message',
            basic: '#taskprofile-basic',
            input: '#taskprofile-input',
            outputprofile: '#outputprofilelist',
            output: '#taskprofile-output',
            importList: '#taskprofile-importlist'
        },
        events: {
            'click #taskprofile-save-btn': 'save',
            'click #taskprofile-back-btn': 'back',
            'click #outputprofile-add-btn': 'addOutputProfile',
            'click #outputprofile-import-btn': 'openImport'
        },
        save: function () {
            if (this.model.isValid(true)) {
                sv.ajax.xhrHandler(this.model.save(), {
                    done: function () {
                        location.href = sv.urlPath.getRealPath('/profile/task');
                    }
                });
            }
        },
        back: function () {
            location.href = sv.urlPath.getRealPath('/profile/task');
        },
        addOutputProfile: function () {
            this.getChildView('outputprofile').addOutputProfile();
        },
        openImport: function () {
            if (!this.getChildView('importList')) {
                var collection = new OutputProfile.OutputProfileCollection();
                collection.fetch({ignore: ['id', 'description']});
                var view = new TaskProfileImportListView({collection: collection});
                this.showChildView('importList', view);
                view.openModal();
            } else {
                this.getChildView('importList').openModal();
            }
        },
        template: false,
        onRender: function () {
            this.showChildView('validation', new TaskProfileValidationView({model: this.model}));
            this.showChildView('basic', new TaskProfileBasicView({model: this.model}));
            this.showChildView('input', new TaskProfileInputView({model: this.model}));
            this.showChildView('outputprofile', new TaskProfileOutputProfileCollectionView({
                model: this.model,
                collection: this.model.get('taskoutputprofiles')
            }));
            this.showChildView('output', new TaskOutputLayoutView({model: this.model}));
        }
    });

    var TaskProfileCollection = Backbone.Collection.extend({
        url: "/profile/task/list",
        model: TaskProfileModel
    });

    var TaskProfileListItemView = OutputProfile.ListItemBaseView.extend({
        template: 'taskprofilelistitemTemplate',
        tagName: "tr",
        ui: {
            selection: '.select-one'
        },
        behaviors: {
            ListItem: {}
        },
        serializeData: function () {
            var count = this.model.get('taskoutputs').size();
            var data = Marionette.ItemView.prototype.serializeData.apply(this, arguments);
            return _.extend(data, {taskoutputcount: count});
        }
    });

    var TaskProfileListView = OutputProfile.ListBaseView.extend({
        el: '#taskprofilelist-container',
        childView: TaskProfileListItemView,
        childViewContainer: 'tbody',
        template: 'taskprofilelistTemplate',
        ui: {
            selectAll: '#select-all',
            edit: '#taskprofile-edit-btn',
            copy: '#taskprofile-copy-btn',
            delete: '#taskprofile-delete-btn'
        },
        events: {
            'click #taskprofile-create-btn': 'createTaskProfile'
        },
        behaviors: {
            ListContainer: {}
        },
        createTaskProfile: function () {
            location.href = "task/new";
        },
        updateActionBar: function () {
            if (this.selectedItem.size() == 0) {
                this.ui.edit.parent().addClass("disable");
                this.ui.copy.parent().addClass("disable");
                this.ui.delete.parent().addClass("disable");
            } else if (this.selectedItem.size() == 1) {
                this.ui.edit.parent().removeClass("disable");
                this.ui.copy.parent().removeClass("disable");
                this.ui.delete.parent().removeClass("disable");
            } else {
                this.ui.edit.parent().addClass("disable");
                this.ui.copy.parent().addClass("disable");
                this.ui.delete.parent().removeClass("disable");
            }
        },
        initialize: function (options) {
            this.selectedItem = new TaskProfileCollection();
            OutputProfile.ListBaseView.prototype.initialize.apply(this, arguments);
        }
    });

    var TaskProfileStaticListView = Backbone.View.extend({
        el: '#taskprofilelist-container',
        events: {
            'click .select-one': 'selectionChange',
            'click tr': 'itemClick',
            'click #select-all': 'selectAllChange',
            'click #taskprofile-edit-btn': 'editTaskProfile',
            'click #taskprofile-copy-btn': 'copyTaskProfile',
            'click #taskprofile-delete-btn': 'deleteTaskProfile',
            'click #taskprofile-create-btn': 'createTaskProfile'
        },
        itemClick: function (event) {
            var $check = $(event.currentTarget).find(".select-one");
            if ($check.length > 0) {
                var id = $check.val();
                var prevSelected = $check.prop("checked");

                if (prevSelected) {
                    $check.prop("checked", false);
                } else {
                    $check.prop("checked", true);
                }
                this.itemSelectionChanged(!prevSelected, id);
            }

        },
        selectionChange: function (event) {
            var id = $(event.currentTarget).val();
            var selected = $(event.currentTarget).prop("checked");
            event.stopPropagation();
            this.itemSelectionChanged(selected, id);
        },
        itemSelectionChanged: function (selected, id) {
            if (selected) {
                if (!_.contains(this.selectedItem, id)) {
                    this.selectedItem.push(id);
                }
                var size = this.$table.find('input[name="id"]').length;
                if (this.selectedItem.length == size) {
                    this.$selectAll.prop("checked", true);
                }
            } else {
                var index = this.selectedItem.indexOf(id);
                if (index != -1) {
                    this.selectedItem.splice(index, 1);
                }
                this.$selectAll.prop("checked", false);
            }
            this.updateActionBar();
        },
        selectAllChange: function (event) {
            event.stopPropagation();
            var selected = $(event.currentTarget).prop("checked");
            var selectItem = this.selectedItem;
            if (selected) {
                this.$table.find('input[name="id"]').each(function () {
                    var id = $(this).val();
                    $(this).prop("checked", true);
                    selectItem.push(id);
                });
            } else {
                this.$table.find('input[name="id"]').each(function () {
                    $(this).prop("checked", false);
                });
                this.selectedItem = [];
            }
            this.updateActionBar();
        },
        createTaskProfile: function () {
            location.href = "task/new/0";
        },
        editTaskProfile: function () {
            location.href = "task/edit/" + this.selectedItem[0];
        },
        copyTaskProfile: function () {
            location.href = "task/copy/" + this.selectedItem[0];
        },
        deleteTaskProfile: function () {
            if (this.selectedItem.length > 0) {
                var _selectedItem = this.selectedItem;
                sv.prompt.showConfirm2('是否删除所选记录？', function () {
                    sv.ajax.doDelete('/profile/task/' + _selectedItem.join(','))
                        .always(function () {
                            location.reload();
                        });

                });
            }
        },
        updateActionBar: function () {
            if (this.selectedItem.length == 0) {
                this.$edit.parent().addClass("disable");
                this.$copy.parent().addClass("disable");
                this.$delete.parent().addClass("disable");
            } else if (this.selectedItem.length == 1) {
                this.$edit.parent().removeClass("disable");
                this.$copy.parent().removeClass("disable");
                this.$delete.parent().removeClass("disable");
            } else {
                this.$edit.parent().addClass("disable");
                this.$copy.parent().addClass("disable");
                this.$delete.parent().removeClass("disable");
            }
        },
        initialize: function () {

            var selectedItem = [];
            this.$edit = this.$el.find("#taskprofile-edit-btn");
            this.$copy = this.$el.find("#taskprofile-copy-btn");
            this.$delete = this.$el.find("#taskprofile-delete-btn");
            this.$selectAll = this.$el.find("#select-all");
            this.$table = this.$el.find("#taskprofilelist");
            this.$table.find('input[name="id"]').each(function () {
                var selected = $(this).prop("checked");
                var id = $(this).val();
                if (selected) {
                    selectedItem.push(id);
                }
            });
            this.selectedItem = selectedItem;
            this.updateActionBar();
        }
    });

    return {
        initItem: function () {
            var attrs = state.item || {};
            var model = new TaskProfileModel(attrs, {parse: true});
            var view = new TaskProfileMainLayoutView({model: model});
            view.render();
        },
        initList: function () {
            // var models = state.list || [];
            // var collection = new TaskProfileCollection(models, {parse: true});
            // var view = new TaskProfileListView({collection: collection});
            // view.render();

            var view = new TaskProfileStaticListView();
        }
    }
}(jQuery, _, Backbone, Backbone.Validation, TipsyValidationCallback, Marionette, OutputProfile, state, window.sv));

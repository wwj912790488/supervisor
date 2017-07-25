var JS_DIALOG_FRAME_TEMPLATE = "#DialogFrameTemplate";
var JS_DIALOG_CONTAINER = ".dialog-content";
var JS_PREVIEW_TEMPLATE = "#ProgramPreviewTmpl";
var JS_PLAYER_CONTAINER = ".TMPlayerContainer";
var JS_PREVIEW_PLAYER = "#ArcSoft_TMPlayer";

var PLAYER_MEDIA_TYPE_FILE = 0;
var PLAYER_MEDIA_TYPE_BD = 1;
var PLAYER_MEDIA_TYPE_DVD = 2;
var PLAYER_MEDIA_TYPE_UDP = 3;

var INPUT_TYPE_NETWORK = "Network";
var INPUT_TYPE_FILE = "LocalFile";

var PLAYBACK_TYPE_RECORD = 0;
var PLAYBACK_TYPE_CHANNEL = 1;
var Z_INDEX = 10001;

var STATE_CLOSED = 0;  // no media opened, or media closed.
var STATE_STOPPED = 1;  // playback is stopped.
var STATE_PAUSED = 2;  // playback is paused.
var STATE_RUNNING = 3;  // playback is running.
var STATE_INITING = 4;  // in process of opening media.


function TMPlayerMngr() {

    this.program = {programId: -1, videoId: -1, audioId: -1, subtitleId: -1};
    this.PlaybackType = -1;   //Record:0 , Channel:1

}

TMPlayerMngr.prototype = {

    /** public API **/
    initRecrodPlayback: function (id) {

        var _this = this;

        var dom = null;
        var domContent = null;
        var domPlayer = null;
        var localURI = null;
        var inputType = null;

        if (id == null) id = "";
        this.PlaybackType = PLAYBACK_TYPE_RECORD;

        var $preview = $("#DialogFrameTemplate");
        this.dom = $preview;

        this.Bind();

        $(".play-btn").click(function () {

            var id = $(this).parent().parent().find("#select-one").val();
            $.getJSON("getRecordInfo", {id: id, r: Math.random()}, function (contentdetectlog) {

                var inputInfo = {};
                inputInfo.type = _this.GetInputType();
                inputInfo.uri = contentdetectlog.videoFilePath;//recordPath;
                _this.SetMediaInfo(inputInfo);
                var offset = contentdetectlog.startOffset != null ? contentdetectlog.startOffset : 0;

                _this.Show();
                _this.Play();
                if (offset != 0) {
                    _this.SeekFromCurrent(offset);
                }

                $("#DialogFrameTemplate").modal({
                    showClose: false, clickClose: false
                });
            });
        });

        $("#tmplayer-close").click(function () {
            _this.Close();
        });

        return this.dom;
    },

    initChannelPlayback: function () {

        var _this = this;

        var dom = null;
        var domContent = null;
        var domPlayer = null;
        var localURI = null;
        var inputType = null;

        this.PlaybackType = PLAYBACK_TYPE_CHANNEL;

        var $preview = $("#DialogFrameTemplate");
        this.dom = $preview;
        this.Bind();
        $("#closePlay").on("click", function () {
            _this.Close();
        });
        //tb 关闭play
        $("#inc_tab").on("click", "a", function () {
            _this.Close();
        });
        //退出 关闭play
        $("#login_out").on("click", function () {
            _this.Close();
        });
        $("#accordion").on("click", "a", function () {
            this.PlaybackType = PLAYBACK_TYPE_CHANNEL;
            var ipAddr = $(this).find("#channelIp").val();
            var programid = $(this).find("#channelProgramId").val();
            var audioid = $(this).find("#channelAudioId").val();

            //  program = {programId: -1, videoId: -1, audioId: -1, subtitleId: -1};
            var inputInfo = {};
            var program = {programId: -1, videoId: -1, audioId: -1, subtitleId: -1};
            inputInfo.type = _this.GetInputType();
            inputInfo.uri = ipAddr;//recordPath;
            program.programId = programid;
            program.audioId = audioid;
            inputInfo.program = program;
            _this.SetMediaInfo(inputInfo);

            _this.Show();
            _this.Play();

        });

        $("#channel-info-content").on("click", ".play-btn", function () {

            this.PlaybackType = PLAYBACK_TYPE_RECORD;

            var id = $(this).parent().parent().find("#contentdetectlogid").val();
            $.getJSON("getRecordInfo", {id: id, r: Math.random()}, function (contentdetectlog) {

                var inputInfo = {};
                inputInfo.type = _this.GetInputType();
                inputInfo.uri = contentdetectlog.videoFilePath;//recordPath;
                _this.SetMediaInfo(inputInfo);
                var offset = contentdetectlog.startOffset != null ? contentdetectlog.startOffset : 0;

                _this.Show();
                _this.Play();
                if (offset != 0) {
                    _this.SeekFromCurrent(offset);
                }
            });
        });

        return this.dom;
    },

    GetInputType: function () {
        var mediaType;
        if (this.PlaybackType == PLAYBACK_TYPE_RECORD)
            mediaType = INPUT_TYPE_FILE;
        else
            mediaType = INPUT_TYPE_NETWORK;

        return mediaType;
    },

    InitTMPlayer: function () {
        if (this.domPlayer != null) return;
        var bi = uBrowserInfo();
        if (!bi.msie) {
            return;
        }

        LoadTMPlayer($(JS_PLAYER_CONTAINER, this.dom).get(0));
        this.domPlayer = $(JS_PREVIEW_PLAYER, this.dom).get(0);

        if (this.domPlayer.GetPlayState == undefined) {
            this.domPlayer = null;
            return;
        }
    },

    Show: function () {
        var $preview = $("#DialogFrameTemplate");
        $preview.show();

    },
    Play: function () {
        this.InitTMPlayer();
        var _url = URI2HttpURL(this.localURI);
        this.PlayURI(_url);
    },

    StopPreview: function () {
        if (!this.bDeviceStarted) return;

        if (this.inputType == INPUT_TYPE_SDI
            || this.inputType == INPUT_TYPE_CVBS
            || this.inputType == INPUT_TYPE_HDMI
            || this.inputType == INPUT_TYPE_ASI) {
            this.StopDevicePreview();
        }
        else if (uGetProtocol(this.localURI) == "udp") {
            this.StopUdpPreview();
        }
    },

    SeekFromCurrent: function (duration) {
        if (this.domPlayer == null) return;

        var state = this.domPlayer.GetPlayState();
        if (state == STATE_RUNNING) {
            this.domPlayer.Pause();
        } else if (state == STATE_PAUSED || state == STATE_STOPPED) {
        } else {
            return;
        }

        var accurate = 1;
        var flag = 1;
        var mode = accurate * 0x01000000 + flag;
        this.domPlayer.Seek(duration * 1000, mode);

        this.domPlayer.Play();
    },

    // SeekTime: function() {
    //     if(this.domPlayer == null) return;

    //     var timeText = $(JS_SEEK_TIME, this.dom).val();
    //     var o = uTimeText2Object(timeText);
    //     if(o == null) {
    //         alert(str_warning.needHmsm);
    //         return;
    //     }

    //     if(o.ms > this.domPlayer.GetMediaDuration()) {
    //         alert(str_warning.outOfDuration);
    //         return;
    //     }

    //     this.JumpByTimeText(timeText);
    // },

    //     /* timeText: 'h:m:s:ms' */
    // JumpByTimeText: function(timeText) {
    //     var o = uTimeText2Object(timeText);

    //     var accurate = 1;
    //     var flag = 1; 
    //     var mode = accurate * 0x01000000 + flag;
    //     this.domPlayer.Pause();
    //     this.domPlayer.Seek(o.ms, mode);
    // },


    SetMediaInfo: function (o) {
        this.ClearMediaInfo();

        this.localURI = o.uri;
        this.inputType = o.type;

        if (o.program != null) {
            this.program.programId = o.program.programId;
            this.program.videoId = o.program.videoId;
            this.program.audioId = o.program.audioId;
            this.program.subtitleId = o.program.subtitleId;
        }
    },

    ClearMediaInfo: function () {
        this.localURI = "";
        this.inputType = null;
        this.program.programId = -1;
        this.program.videoId = -1;
        this.program.audioId = -1;
        this.program.subtitleId = -1;

    },

    SelectTrack: function () {

        var p0 = parseInt(this.program.programId);
        if (isNaN(p0)) {
            p0 = -1;
        } else {
            //UDP use programId
            p0 = p0 + 0x10000000;
        }
        var p1 = parseInt(this.program.videoId);
        if (isNaN(p1)) p1 = -1;
        var p2 = parseInt(this.program.audioId);
        if (isNaN(p2)) p2 = -1;
        var p3 = parseInt(this.program.subtitleId);
        if (isNaN(p3)) p3 = -1;

        this.domPlayer.SelectTrackByID(p0, p1, p2, p3);

    },
    Player_GetMediaType: function (inputType, uri) {
        var mediaType = 0;
        inputType = inputType.toLowerCase();
        if (inputType == INPUT_TYPE_FILE.toLowerCase()) {
            mediaType = PLAYER_MEDIA_TYPE_FILE;
        }
        else if (inputType == INPUT_TYPE_NETWORK.toLowerCase()) {
            if (uri.match(/^udp:/i) != null
            ) {
                mediaType = PLAYER_MEDIA_TYPE_UDP;

            } else {
                mediaType = PLAYER_MEDIA_TYPE_FILE;
            }
        }
        else if (inputType == INPUT_TYPE_BD.toLowerCase()) {
            mediaType = PLAYER_MEDIA_TYPE_BD;
        }
        else if (inputType == INPUT_TYPE_DVD.toLowerCase()) {
            mediaType = PLAYER_MEDIA_TYPE_DVD;
        }
        mediaType = mediaType * 0x10000 + 3;
        return mediaType;
    },
    GetMediaType: function () {

        var mediaType = PLAYER_MEDIA_TYPE_FILE;
        if (this.PlaybackType == PLAYBACK_TYPE_CHANNEL)
            mediaType = PLAYER_MEDIA_TYPE_UDP;

        mediaType = mediaType * 0x10000 + 3;
        return mediaType;
    },

    PlayURI: function (httpURI) {
        //alert(this.inputType)
        if (this.domPlayer == null) return;
        if (httpURI == null) return;

        var mediaType = this.Player_GetMediaType(this.inputType, this.localURI);
        //var mediaType = this.GetMediaType();
        var ret = this.domPlayer.LoadMedia(httpURI, "", mediaType);
        //var test="http://localhost:80/tms.content?url=udp://239.1.1.1:1234"
        //var ret = this.domPlayer.LoadMedia(test, "", mediaType);

        if (ret != 0) return;
        if (this.localURI.match(/^udp:/i) != null) {
            if (this.PlaybackType == PLAYBACK_TYPE_CHANNEL)
                this.SelectTrack();
        }
        ret = this.domPlayer.Play();
        return ret;
    },

    RestorePlayer: function () {
        this.domPlayer.ChangeAspectRatio(0, 0, -100, 0);
    },

    Close: function () {
        //TMPlayer operate.
        if (this.domPlayer != null) {
            this.domPlayer.close();
            this.RestorePlayer();
        }
        //$(this.dom).hide();
    },

    Bind: function () {
        var context = this;
    },
};

/********************************************/
function URI2HttpURL(uri) {
    if (uri == null) return null;
    var httpURL = null;
    var protocol = uGetProtocol(uri);
    if (protocol == "http" || protocol == "rtsp" || protocol == "rtmp") {
        httpURL = uri;
    } else {
        var _requestURL;
        _requestURL = location.href.substring(0, location.href.lastIndexOf("/"));
        _requestURL = _requestURL.substring(0, _requestURL.lastIndexOf("/") + 1);
        var _action = "tms.content?";
        var _param = "url=" + (uri);
        httpURL = _requestURL + _action + _param;
    }
    return httpURL;
};

function uGetProtocol(uri) {
    if (uri == null) return null;
    var pos = uri.indexOf("://");
    var protocol = uri.substring(0, pos);
    protocol = protocol.toLowerCase();
    return protocol;
};

function uCheckBrowser() {
    var Sys = {};
    var ua = navigator.userAgent.toLowerCase();
    var s;
    (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
        (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
            (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
                (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
                    (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
    return Sys;
};

function uBrowserInfo() {
    var browser = {
            msie: false, firefox: false, opera: false, safari: false,
            chrome: false, netscape: false, appname: 'unknown', version: 0
        },
        userAgent = window.navigator.userAgent.toLowerCase();
    if (/(msie|firefox|opera|chrome|netscape)\D+(\d[\d.]*)/.test(userAgent)) {
        browser[RegExp.$1] = true;
        browser.appname = RegExp.$1;
        browser.version = RegExp.$2;
    } else if (/version\D+(\d[\d.]*).*safari/.test(userAgent)) { // safari
        browser.safari = true;
        browser.appname = 'safari';
        browser.version = RegExp.$2;
    } else if (/rv:(\d[\d.]*)/.test(userAgent)) {
        browser.msie = true;
        browser.appname = "msie";
        browser.version = RegExp.$1;
    }
    return browser;
};



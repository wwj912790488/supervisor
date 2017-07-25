
var JS_PROGRAM_PREVIEW_TRIGGER = ".ProgramPreviewTrigger";

var INPUT_TYPE_SDI		="SDI";
var INPUT_TYPE_AES_EBU	="AESEBU";
var INPUT_TYPE_ASI		="ASI";
var INPUT_TYPE_NETWORK	="Network";
var INPUT_TYPE_FILE		="LocalFile";
var INPUT_TYPE_BD		="BD";
var INPUT_TYPE_DVD		="DVD";
var INPUT_TYPE_COMBINATION		="COMBINATION";

function OnPageReady() {
	g_taskSupport = new TaskSupport();
	g_taskSupport.Create();
}

function LoadTMPlayer(dom) {
	dom.innerHTML = 
	'<object classid="clsid:b14dcdc6-dc3a-4e99-80b2-3169b06ef069"'+
	'codebase="tmplayer/TMPlayer.CAB#Version=2,0,0,72"'+
	'id="ArcSoft_TMPlayer"'+
	'width="520" height="390"'+
	'viewastext standby="Loading ArcSoft TotalMedia Player ...">'+
	'<param name="ApplicationType" value="0" />'+
	'<param name="PanelType" value="3" />'+
	'<param name="ResizeMode" value="7" />'+
	'<br/><br/><br/><br/><br/><br/>'+
	'<p style="font-size:12px">当前网页需要安装媒体播放器“ ArcSoft TotalMedia Player”。<br/>'+
	'如果你没有看到提示, 请确认系统和浏览器的安全权限。<br/></p>'+
	'<a href="'+ sv.urlPath.getRealPath("tmplayer/TMPSetup.exe")+'"><b style="color:green;font-size:13px">或者点击此处安装“ ArcSoft TotalMedia Player”。</b><br/>'+
	'<b style="color:green;font-size:13px">请保存安装文件至本地后,使用管理员权限安装。<b></a><br/>'+
	'<a href="#" onclick="location.reload()"><b style="font-size:13px">安装完成后，点此刷新本页面。</b></a><br/>'+
	'</object>';
}

function installTMPlayer() {
	var pluginDiv = document.createElement("div");
	pluginDiv.setAttribute("id", "installPlugin");
	pluginDiv.setAttribute("style", "display:none");
	document.body.insertBefore(pluginDiv, null);
	LoadTMPlayer(pluginDiv);
}

function TaskSupport() {

	this.dom = null;

	this.Create = function() {
		//installTMPlayer();
				
		var dom = null;
		var title = "";
		
		this.Init();
		
		return this.dom;
	};
	
	this.Init = function() {
		var context = this;
		this.Bind();
	};

	this.Bind = function() {
		var context = this;
		
		$(JS_PROGRAM_PREVIEW_TRIGGER, this.dom).click(function() {
			context.Preview();
		});
	};

	this.Preview = function() {

	};
}





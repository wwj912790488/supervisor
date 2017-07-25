<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/common/normalize.css"/>
    <utils:css path="/css/profile.css"/>
    <utils:js path="/js/plugins/backbone/underscore.js,/js/plugins/backbone/backbone.js,/js/plugins/backbone/backbone.marionette.js,/js/plugins/backbone/backbone-validation.js"/>

	<script type="text/javascript">
		window.state = {};
        window.state.op = '${op}';
        window.state.item = {};
		var profile = '${profile}';
		if (profile) {
			window.state.item = JSON.parse(profile);
		}
    </script>
    <utils:js path="/js/outputProfile.js"/>
    <script type="text/javascript">
        $(function () {
            OutputProfile.initItem();
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
    	<div class="content-wrapper" id="outputprofile-container">
    	<div id="outputprofile-title">
			<c:set var="opName">
				<c:choose>
					<c:when test="${op == 'copy'}">复制</c:when>
					<c:when test="${op == 'edit'}">编辑</c:when>
					<c:when test="${op == 'new'}">新建</c:when>
				</c:choose>
			</c:set>
			${opName}输出参数模板
		</div>
    	<div id="title-delimiter"></div>
    	<div class="line clearfix" id="outputprofile-basic"></div>
    	<div id="streamprofile">
    		<div id="streamprofile-title">流摘要信息</div>
    		<div id="streamporfile-content">
				<div id="outputprofile-summary"></div>
	    		<div id="videostreamprofile">
		    		
	    		</div>
	    		<div id="audiostreamprofile">
		    		<div class="stream-title-line clearfix">
		    			<div class="line-block stream-title">
		    				音频
		    			</div>
		    			<div id="audioprofiles-add-btn">
						<div class="dialog-btn">
				                <a>
				                    <span class="btn-left"></span>
				                    <span class="btn-middle">
				                        <span class="btn-text">新增音频</span>
				                    </span>
				                    <span class="btn-right"></span>
				                </a>
				            </div>
				           </div>
					</div>
					<div id="audiostreamprofilelist">
					</div>
		    	</div>
		    </div>
    	</div>
    	<div id="outputprofile-submit">
    		<div id="outputprofile-back-btn" class="dialog-btn">
                <a>
                    <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">返回</span>
                    </span>
                    <span class="btn-right"></span>
                </a>
            </div>
			<div id="outputprofile-save-btn" class="dialog-btn">
                <a>
                    <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">保存</span>
                    </span>
                    <span class="btn-right"></span>
                </a>
            </div>            
    	</div>
    </div>
    <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script type="text/x-handlebars-template" id="basicTemplate">
	<div class="line-block2">
	<span>名称： </span>
    <input type="text" name="name" value="{{name}}"/>
    </div>
    <div class="line-block2">
    <span>描述： </span>
    <textarea name="description" style="width: 400px; height: 50px; resize: none;">{{description}}</textarea>
    <input type="hidden" name="id" value="{{id}}"/>
    </div>
</script>
<script type="text/x-handlebars-template" id="novideoprofileTemplate">
		<div class="clearfix">
			<div class="line-block stream-title">
		    	视频
		    </div>
			<div id="videoprofiles-add-btn">
				<div class="dialog-btn">
	                <a>
	                    <span class="btn-left"></span>
	                    <span class="btn-middle">
	                        <span class="btn-text">新增视频</span>
	                    </span>
	                    <span class="btn-right"></span>
	                </a>
	            </div>
			</div>	
		</div>
</script>
<script type="text/x-handlebars-template" id="summaryprofileTemplate">				
	<span >{{#if videoprofile}}
				{{#if videoprofile.passthrough}}Pass Through{{else}}{{videoprofile.videocodec}} {{videoprofile.videowidth}} x {{videoprofile.videoheight}}@ {{videoprofile.videoratecontrol}} {{videoprofile.videobitrate}}Kbps 
				{{/if}}
				{{#if audioprofile}} |			 
				{{#if audioprofile.passthrough}}Pass Through {{else}}{{audioprofile.audiocodec}} {{audioprofile.audiochannel}}Channel(s) {{audioprofile.audiosamplerate}}KHz {{audioprofile.audiobitrate}}Kbps 
				{{/if}} 
				{{else}}
				{{/if}}
			{{else}}
				{{#if audioprofile}}
					{{#if audioprofile.passthrough}}Pass Through {{else}}{{audioprofile.audiocodec}} {{audioprofile.audiochannel}}Channel(s) {{audioprofile.audiosamplerate}}KHz {{audioprofile.audiobitrate}}Kbps 
					{{/if}} 
				{{else}}
					空流
				{{/if}}
			{{/if}}
	</span>
</script>

<script type="text/x-handlebars-template" id="mpeg4videoprofileTemplate">
		
			<div class="videoprofile">
				<div class="stream-title-line clearfix">
	    			<div class="line-block stream-title">视频</div>
					<span style="font-size:15px;margin-left:10px;">Pass Through</span>
					<input type="checkbox" name="videopass" {{#if videopassthrough}}checked="checked"{{/if}}></input>
	    			<input type="hidden" name="index" value="{{index}}"/>
	    			<div class="line-block-right">
	    			<%--<div class="videoprofile-remove-btn"></div>		                --%>
		            </div>
	    		</div>
	    		{{#unless videopassthrough}}
	    		<div class="section">
	    			<div class="section-trigger">
	    				<div class="sectiontitle">编码参数
	    			</div>
	    			</div>
	    			<div class="sectioncontent">
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
		    					<span>编码格式： </span>
		    					<select name="videocodec">
		    						{{#select videocodec}}
		    						<option value="MPEG4">MPEG4</option>
		    						<option value="H264">H264</option>
		    						{{/select}}
		    					</select>
		    				</div>
		    				<div class="line-block" style="width:230px;">
		    					<span>编码档次： </span>
		    					<select name="videoprofile">
		    						{{#select videocodecprofile}}
		    						<option value="Simple">Simple</option>
		    						{{/select}}
		    					</select>
		    				</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:275px;">
		    					<span>分辨率： </span>
		    					<input type="text" name="videowidth" value="{{videowidth}}"></input>
		    					<span>x</span>
		    					<input type="text" name="videoheight" value="{{videoheight}}"></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>显示宽高比： </span>
	    					{{#if videosourcePAR}}
	    					<select name="videoPAR">
	    						<option value="source" selected="selected">跟随源</option>
	    						<option value="custom">自定义</option>
	    						<option value="16:9">16 : 9</option>
	    						<option value="4：3">4 : 3</option>
	    						<option value="40:33">40 : 33</option>
	    						<option value="16:11">16 : 11</option>
	    					</select>
	    					{{else}}
	    					<select name="videoPAR">
	    						{{#select videoPAR}}
	    						<option value="source">跟随源</option>
	    						<option value="custom">自定义</option>
	    						<option value="16:9">16 : 9</option>
	    						<option value="4：3">4 : 3</option>
	    						<option value="40:33">40 : 33</option>
	    						<option value="16:11">16 : 11</option>
	    						{{/select}}
	    					</select>
	    					{{/if}}
	    					</div>
	    					<div class="line-block" style="width:160px;">
	    					<input type="text" name="videoPARX" {{#if videosourcePAR}}disabled{{else}}value="{{videoPARX}}"{{/if}}></input>
	    					<span>/</span>
	    					<input type="text" name="videoPARY" {{#if videosourcePAR}}disabled{{else}}value="{{videoPARY}}"{{/if}}></input>
	    					</div>
	    					<div class="line-block" style="width:300px;">
	    					<span>宽高变换模式： </span>
	    					<select name="videosmartborder">
	    						{{#select videosmartborder}}
	    						<option value="1">智能黑边</option>
	    						<option value="2">自动裁剪</option>
	    						<option value="0">线性拉伸</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>帧率： </span>
	    					{{#if videosourceframerate}}
	    					<select name="videoframerate">
	    						<option value="source" selected="selected">跟随源</option>
	    						<option value="custom">自定义</option>
	    						<option value="10:1">10.0</option>
	    						<option value="15:1">15.0</option>
	    						<option value="24000:1001">23.976</option>
	    						<option value="24:1">24.0</option>
	    						<option value="25:1">25.0</option>
	    						<option value="30000:1001">29.97</option>
	    						<option value="30:1">30.0</option>
	    						<option value="50:1">50.0</option>
	    						<option value="60000:1001">59.94</option>
	    						<option value="60:1">60.0</option>
	    					</select>
	    					{{else}}
	    					<select name="videoframerate">
	    						{{#select videoframerate}}
		    					<option value="source">跟随源</option>
		    					<option value="custom">自定义</option>
		    					<option value="10:1">10.0</option>
		    					<option value="15:1">15.0</option>
		    					<option value="24000:1001">23.976</option>
		    					<option value="24:1">24.0</option>
		    					<option value="25:1">25.0</option>
		    					<option value="30000:1001">29.97</option>
		    					<option value="30:1">30.0</option>
		    					<option value="50:1">50.0</option>
		    					<option value="60000:1001">59.94</option>
		    					<option value="60:1">60.0</option>
		    					{{/select}}
		    				</select>
		    				{{/if}}
		    				</div>
		    				<div class="line-block" style="width:160px;">
	    					<input type="text" name="videoframerateX" {{#if videosourceframerate}}disabled{{else}}value="{{videoframerateX}}"{{/if}}></input>
	    					<span>/</span>
	    					<input type="text" name="videoframerateY" {{#if videosourceframerate}}disabled{{else}}value="{{videoframerateY}}"{{/if}}></input>
	    					</div>
	    					<div class="line-block" style="width:202px;display: none;">
	    					<span>插帧补帧： </span>
	    					<input type="checkbox" name="videoframerateconversionmode" {{#if videoframerateconversionmode}}checked="checked"{{/if}}></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>码率控制： </span>
	    					<select name="videoratecontrol">
	    						{{#select videoratecontrol}}
	    						<option value="ABR">ABR</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>平均码率： </span>
	    					<input type="text" name="videobitrate" value="{{videobitrate}}"></input>
	    					<span>Kbps</span>
	    					</div>
	    					<div class="line-block" style="width:212px;">
	    					<span>GOP大小： </span>
	    					<input type="text" name="videoGOPsize" value="{{videogopsize}}"></input>
	    					<span>帧</span>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:133px;">
	    					<span>场景检测： </span>
	    					<input type="checkbox" name="videoSCD" {{#if videoSCD}}checked="checked"{{/if}}></input>
	    					</div>
	    				</div>
	    			</div>
	    		</div>
	    		<div class="section section-collapse">
	    			<div class="section-trigger">
	    			<div class="sectiontitle">图像处理</div>
	    			</div>
	    			<div class="sectioncontent">
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>去交错： </span>
	    					<select name="videodeinterlace">
	    						{{#select videodeinterlace}}
	    						<option value="0">关</option>
	    						<option value="1">开</option>
	    						<option value="2">自动</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>去交错算法： </span>
	    					<select name="videodeinterlacealg">
	    						{{#select videodeinterlacealg}}
	    						<option value="2">质量优先</option>
	    						<option value="3">速度优先</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>调整尺寸算法： </span>
	    					<select name="videoresizealg">
	    						{{#select videoresizealg}}
	    						<option value="3">质量优先</option>
	    						<option value="1">速度优先</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>去噪： </span>
	    					<select name="videodenoise">
	    						{{#select videodenoise}}
	    						<option value="0">0</option>
	    						<option value="1">1</option>
	    						<option value="2">2</option>
	    						<option value="3">3</option>
	    						<option value="4">4</option>
	    						<option value="5">5</option>
	    						<option value="6">6</option>
	    						<option value="7">7</option>
	    						<option value="8">8</option>
	    						<option value="9">9</option>
	    						<option value="10">10</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:134px;">
	    					<span>De-block： </span>
	    					<input type="checkbox" name="videodeblock" {{#if videodeblock}}checked="checked"{{/if}}></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>Sharpen： </span>
	    					<select name="videosharpen">
	    						{{#select videosharpen}}
	    						<option value="0">0</option>
	    						<option value="1">1</option>
	    						<option value="2">2</option>
	    						<option value="3">3</option>
	    						<option value="4">4</option>
	    						<option value="5">5</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>Anti-Alias： </span>
	    					<input type="checkbox" name="videoantialias" {{#if videoantialias}}checked="checked"{{/if}}></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:186px;">
	    					<span>亮度： </span>
	    					<input type="text" name="videobright" value="{{videobright}}"></input>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>对比度： </span>
	    					<input type="text" name="videocontrast" value="{{videocontrast}}"></input>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>饱和度： </span>
	    					<input type="text" name="videosaturation" value="{{videosaturation}}"></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:186px;"
	    					<span>色调： </span>
	    					<input type="text" name="videohue" value="{{videohue}}"></input>
	    					</div>
	    					<div class="line-block" style="width:275px;">
	    					<span>De-light： </span>
	    					<select name="videodelight">
	    						{{#select videodelight}}
	    						<option value="0">0</option>
	    						<option value="1">1</option>
	    						<option value="2">2</option>
	    						<option value="3">3</option>
	    						<option value="4">4</option>
	    						<option value="5">5</option>
	    						<option value="6">6</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    			</div>
	    		</div>
	    		{{/unless}}
	    	</div>
</script>
<script type="text/x-handlebars-template" id="h264videoprofileTemplate">
		
			<div class="videoprofile">
				<div class="stream-title-line clearfix">
	    			<div class="line-block stream-title">视频</div>
					<span style="font-size:15px;margin-left:10px;">Pass Through</span>
					<input type="checkbox" name="videopass" {{#if videopassthrough}}checked="checked"{{/if}}></input>
	    			<input type="hidden" name="index" value="{{index}}"></input>
	    			<div class="line-block-right">
	    				<%--<div class="videoprofile-remove-btn"></div>--%>
		            </div>
	    		</div>
	    		{{#unless videopassthrough}}
	    		<div class={{#if codecsectionvisible}}"section"{{else}}"section section-collapse"{{/if}}>
	    			<div class="section-trigger codecsection">
	    			<div class="sectiontitle">编码参数</div>
	    			</div>
	    			<div class="sectioncontent">
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>编码格式： </span>
	    					<select name="videocodec">
	    						{{#select videocodec}}
	    						<option value="H264">H264</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>编码档次： </span>
	    					<select name="videoprofile">
	    						{{#select videocodecprofile}}
	    						<option value="High">High</option>
	    						<option value="Main">Main</option>
	    						<option value="Baseline">Baseline</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>编码级别： </span>
	    					<select name="videocodeclevel">
	    						{{#select videocodeclevel}}
	    						<option value="-1">auto</option>
	    						<option value="10">1.0</option>
	    						<option value="11">1.1</option>
	    						<option value="12">1.2</option>
	    						<option value="13">1.3</option>
	    						<option value="20">2.0</option>
	    						<option value="21">2.1</option>
	    						<option value="22">2.2</option>
	    						<option value="30">3.0</option>
	    						<option value="31">3.1</option>
	    						<option value="32">3.2</option>
	    						<option value="40">4.0</option>
	    						<option value="41">4.1</option>
	    						<option value="42">4.2</option>
	    						<option value="50">5.0</option>
	    						<option value="51">5.1</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>帧场模式： </span>
	    					{{#ifCond videocodecprofile "==" "Baseline"}}
	    						<select name="videointerlace">
	    							<option value="0" selected="selected">帧</option>
	    						</select>
	    					{{else}}
		    					<select name="videointerlace">
		    						{{#select videointerlace}}
		    						<option value="-1">跟随源</option>
		    						<option value="0">帧</option>
		    						<option value="2">场AUTO</option>
		    						<option value="3">MBAFF</option>
		    						<option value="4">PAFF</option>
		    						{{/select}}
		    					</select>
	    					{{/ifCond}}	   
	    					</div>
	    					<div class="line-block" style="width:230px;"> 					
	    					<span>场优先： </span>
	    					<select name="videotopfieldfirst" {{#ifCond videointerlace "==" "-1"}}disabled{{/ifCond}} {{#ifCond videointerlace "==" "0"}}disabled{{/ifCond}}>
	    						{{#select videotopfieldfirst}}
	    						<option value="-1">跟随源</option>
	    						<option value="0">底场优先</option>
	    						<option value="1">顶场优先</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:275px;">
	    					<span>分辨率： </span>
	    					<input type="text" name="videowidth" value="{{videowidth}}"></input>
	    					<span>x</span>
	    					<input type="text" name="videoheight" value="{{videoheight}}"></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>显示宽高比： </span>
	    					{{#if videosourcePAR}}
	    					<select name="videoPAR">
	    						<option value="source" selected="selected">跟随源</option>
	    						<option value="custom">自定义</option>
	    						<option value="16:9">16 : 9</option>
	    						<option value="4:3">4 : 3</option>
	    						<option value="40:33">40 : 33</option>
	    						<option value="16:11">16 : 11</option>
	    					</select>
	    					{{else}}
	    					<select name="videoPAR">
	    						{{#select videoPAR}}
	    						<option value="source">跟随源</option>
	    						<option value="custom">自定义</option>
	    						<option value="16:9">16 : 9</option>
	    						<option value="4:3">4 : 3</option>
	    						<option value="40:33">40 : 33</option>
	    						<option value="16:11">16 : 11</option>
	    						{{/select}}
	    					</select>
	    					{{/if}}
	    					</div>
	    					<div class="line-block" style="width:160px;">
	    					<input type="text" name="videoPARX" {{#if videosourcePAR}}disabled{{else}}value="{{videoPARX}}"{{/if}}></input>
	    					<span>/</span>
	    					<input type="text" name="videoPARY" {{#if videosourcePAR}}disabled{{else}}value="{{videoPARY}}"{{/if}}></input>
	    					</div>
	    					<div class="line-block" style="width:300px;">
	    					<span>宽高变换模式： </span>
	    					<select name="videosmartborder">
	    						{{#select videosmartborder}}
	    						<option value="1">智能黑边</option>
	    						<option value="2">自动裁剪</option>
	    						<option value="0">线性拉伸</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>帧率： </span>
	    					{{#if videosourceframerate}}
	    					<select name="videoframerate">
	    						<option value="source" selected="selected">跟随源</option>
	    						<option value="custom">自定义</option>
	    						<option value="10:1">10.0</option>
	    						<option value="15:1">15.0</option>
	    						<option value="24000:1001">23.976</option>
	    						<option value="24:1">24.0</option>
	    						<option value="25:1">25.0</option>
	    						<option value="30000:1001">29.97</option>
	    						<option value="30:1">30.0</option>
	    						<option value="50:1">50.0</option>
	    						<option value="60000:1001">59.94</option>
	    						<option value="60:1">60.0</option>
	    					</select>
	    					{{else}}
	    					<select name="videoframerate">
	    						{{#select videoframerate}}
		    					<option value="source">跟随源</option>
		    					<option value="custom">自定义</option>
		    					<option value="10:1">10.0</option>
		    					<option value="15:1">15.0</option>
		    					<option value="24000:1001">23.976</option>
		    					<option value="24:1">24.0</option>
		    					<option value="25:1">25.0</option>
		    					<option value="30000:1001">29.97</option>
		    					<option value="30:1">30.0</option>
		    					<option value="50:1">50.0</option>
		    					<option value="60000:1001">59.94</option>
		    					<option value="60:1">60.0</option>
		    					{{/select}}
		    				</select>
		    				{{/if}}
		    				</div>
		    				<div class="line-block" style="width:160px;">
	    					<input type="text" name="videoframerateX" {{#if videosourceframerate}}disabled{{else}}value="{{videoframerateX}}"{{/if}}></input>
	    					<span>/</span>
	    					<input type="text" name="videoframerateY" {{#if videosourceframerate}}disabled{{else}}value="{{videoframerateY}}"{{/if}}></input>
	    					</div>
	    					<div class="line-block" style="width:202px;display: none;">
	    					<span>插帧补帧： </span>
	    					<input type="checkbox" name="videoframerateconversionmode" {{#if videoframerateconversionmode}}checked="checked"{{/if}}></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>码率控制： </span>
	    					<select name="videoratecontrol">
	    						{{#select videoratecontrol}}
	    						<option value="VBR">VBR</option>
	    						<option value="CBR">CBR</option>
	    						<option value="ABR">ABR</option>
	    						<option value="CQ">CQ</option>
	    						<option value="CRF">CRF</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>平均码率： </span>
	    					<input type="text" name="videobitrate" value="{{videobitrate}}" {{#ifCond videoratecontrol "==" "CQ"}}disabled{{/ifCond}} {{#ifCond videoratecontrol "==" "CRF"}}disabled{{/ifCond}}></input>
	    					<span>Kbps</span>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>最高码率： </span>
	    					<input type="text" name="videomaxbitrate" value="{{videomaxbitrate}}" {{#ifCond videoratecontrol "==" "CBR"}}disabled{{/ifCond}} value="{{videomaxbitrate}}" {{#ifCond videoratecontrol "==" "ABR"}}disabled{{/ifCond}} {{#ifCond videoratecontrol "==" "CQ"}}disabled{{/ifCond}} {{#ifCond videoratecontrol "==" "CRF"}}disabled{{/ifCond}}></input>
	    					<span>Kbps</span>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:133px; display: none;">
	    					<span>Two pass： </span>
	    					<input type="checkbox" name="videotwopass" {{#if videotwopass}}checked="checked"{{/if}}></input>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>质量级别： </span>
	    					<select name="videoqualityleveldisp">
	    						{{#select videoqualityleveldisp}}
	    						<option value="-1">0</option>
	    						<option value="0">1</option>
	    						<option value="1">2</option>
	    						<option value="2">3</option>
	    						<option value="3">4</option>
	    						<option value="4">5</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:350px;">
	    					<span>当编码策略选择"自定义"时，质量级别生效</span>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:240px;">
	    					<span>缓冲区大小： </span>
	    					<input type="text" name="videobuffersize" value="{{videobuffersize}}" {{#ifCond videoratecontrol "==" "ABR"}}disabled{{/ifCond}} {{#ifCond videoratecontrol "==" "CQ"}}disabled{{/ifCond}} {{#ifCond videoratecontrol "==" "CRF"}}disabled{{/ifCond}}></input>
	    					<span>Kbytes</span>
	    					</div>
	    					<div class="line-block" style="width:215px;">
	    					<span>初始填充： </span>
	    					<input type="text" name="videobufferfill" value="{{videobufferfill}}" {{#ifCond videoratecontrol "==" "ABR"}}disabled{{/ifCond}} {{#ifCond videoratecontrol "==" "CQ"}}disabled{{/ifCond}} {{#ifCond videoratecontrol "==" "CRF"}}disabled{{/ifCond}}></input>
	    					<span>毫秒</span>
	    					</div>
	    					<div class="line-block" style="width:192px;">
	    					<span>量化： </span>
	    					<input type="text" name="videoquantizer" value="{{videoquantizer}}" {{#ifCond videoratecontrol "==" "VBR"}}disabled{{/ifCond}} {{#ifCond videoratecontrol "==" "CBR"}}disabled{{/ifCond}} {{#ifCond videoratecontrol "==" "ABR"}}disabled{{/ifCond}}></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:211px;">
	    					<span>GOP大小： </span>
	    					<input type="text" name="videoGOPsize" value="{{videogopsize}}"></input>
	    					<span>帧</span>
	    					</div>
	    					<div class="line-block" style="width:205px;">
	    					<span>B帧数量： </span>
	    					<input type="text" name="videobframe" {{#ifCond videocodecprofile "==" "Baseline"}}disabled{{/ifCond}} value="{{videobframe}}"></input>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>参考帧： </span>
	    					<input type="text" name="videoreferenceframe" value="{{videoreferenceframe}}"></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:131px;">
	    					<span>CABAC： </span>
	    					<input type="checkbox" name="videoCABAC" {{#ifCond videocodecprofile "==" "Baseline"}}disabled{{/ifCond}} {{#if videoCABAC}}checked="checked"{{/if}}></input>
	    					</div>
	    					<div class="line-block" style="width:232px;">
	    					<span>8x8帧内预测： </span>
	    					<input type="checkbox" name="videointraprediction" {{#ifCond videocodecprofile "!=" "High"}}disabled{{/ifCond}} {{#if videointraprediction}}checked="checked"{{/if}}></input>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>8x8变换： </span>
	    					<input type="checkbox" name="videotransform" {{#ifCond videocodecprofile "!=" "High"}}disabled{{/ifCond}} {{#if videotransform}}checked="checked"{{/if}}></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:131px;">
	    					<span>场景检测： </span>
	    					<input type="checkbox" name="videoSCD" {{#if videoSCD}}checked="checked"{{/if}}></input>
	    					</div>
	    				</div>
	    			</div>
	    		</div>
	    		<div class={{#if ipsectionvisible}}"section"{{else}}"section section-collapse"{{/if}}>
	    			<div class="section-trigger ipsection">
	    			<div class="sectiontitle">图像处理</div>
	    			</div>
	    			<div class="sectioncontent">
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>去交错： </span>
	    					<select name="videodeinterlace">
	    						{{#select videodeinterlace}}
	    						<option value="0">关</option>
	    						<option value="1">开</option>
	    						<option value="2">自动</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>去交错算法： </span>
	    					<select name="videodeinterlacealg">
	    						{{#select videodeinterlacealg}}
								<option value="1">最优速度(单频道转码专用)</option>
	    						<option value="2">质量优先</option>
	    						<option value="3">速度优先</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>调整尺寸算法： </span>
	    					<select name="videoresizealg">
	    						{{#select videoresizealg}}
	    						<option value="3">质量优先</option>
	    						<option value="1">速度优先</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>去噪： </span>
	    					<select name="videodenoise">
	    						{{#select videodenoise}}
	    						<option value="0">0</option>
	    						<option value="1">1</option>
	    						<option value="2">2</option>
	    						<option value="3">3</option>
	    						<option value="4">4</option>
	    						<option value="5">5</option>
	    						<option value="6">6</option>
	    						<option value="7">7</option>
	    						<option value="8">8</option>
	    						<option value="9">9</option>
	    						<option value="10">10</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:134px;">
	    					<span>De-block： </span>
	    					<input type="checkbox" name="videodeblock" {{#if videodeblock}}checked="checked"{{/if}}></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>Sharpen： </span>
	    					<select name="videosharpen">
	    						{{#select videosharpen}}
	    						<option value="0">0</option>
	    						<option value="1">1</option>
	    						<option value="2">2</option>
	    						<option value="3">3</option>
	    						<option value="4">4</option>
	    						<option value="5">5</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>Anti-Alias： </span>
	    					<input type="checkbox" name="videoantialias" {{#if videoantialias}}checked="checked"{{/if}}></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:186px;">
	    					<span>亮度： </span>
	    					<input type="text" name="videobright" value="{{videobright}}"></input>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>对比度： </span>
	    					<input type="text" name="videocontrast" value="{{videocontrast}}"></input>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>饱和度： </span>
	    					<input type="text" name="videosaturation" value="{{videosaturation}}"></input>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:186px;"
	    					<span>色调： </span>
	    					<input type="text" name="videohue" value="{{videohue}}"></input>
	    					</div>
	    					<div class="line-block" style="width:275px;">
	    					<span>De-light： </span>
	    					<select name="videodelight">
	    						{{#select videodelight}}
	    						<option value="0">0</option>
	    						<option value="1">1</option>
	    						<option value="2">2</option>
	    						<option value="3">3</option>
	    						<option value="4">4</option>
	    						<option value="5">5</option>
	    						<option value="6">6</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    			</div>
	    		</div>
	    		{{/unless}}
	    	</div>
</script>
<script type="text/x-handlebars-template" id="aacaudioprofileTemplate">
	    		<div class="audioprofile">    		
	    		<div class="section">
	    			<div class="section-trigger clearfix">
		    			<div class="line-block">
		    				<div class="sectiontitle">音频{{index}}</div>
		    			</div>
		    			<div class="line-block">
		    				<span style="font-size:15px;margin-left:10px;">Pass Through</span>
		    				<input type="checkbox" name="audiopass" {{#if audiopassthrough}}checked="checked"{{/if}}></input>
		    				<input type="hidden" name="index" value="{{index}}"></input>
		    				
		    			</div>
		    			<div class="line-block-right">
				        	<div class="audioprofile-remove-btn"></div>     
				        </div>
	    			</div>
	    			{{#unless audiopassthrough}}
	    			<div class="sectioncontent">
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>编码格式： </span>
	    					<select name="audiocodec">
	    						{{#select audiocodec}}
	    						<option value="AAC">AAC</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>编码档次： </span>
	    					<select name="audiocodecprofile">
	    						{{#select audiocodecprofile}}
	    						<option value="LC">LC</option>
	    						<option value="MPEG2LC">MPEG2LC</option>
	    						<option value="HEV1">HEV1</option>
	    						<option value="HEV2">HEV2</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>声道： </span>
	    					<select name="audiochannel">
	    						{{#select audiochannel}}
								<option value="1">Mono</option>
								<option value="2">Stereo</option>
								<option value="6">5.1</option>
								{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>采样率： </span>
	    					<select name="audiosamplerate">
	    						{{#select audiosamplerate}}
	    						<option value="8000">8.0</option>
	    						<option value="22050">22.05</option>
	    						<option value="24000">24.0</option>
	    						<option value="32000">32.0</option>
	    						<option value="44100">44.1</option>
	    						<option value="48000">48.0</option>
	    						{{/select}}
	       					</select>
	       					</div>
	       					<div class="line-block" style="width:230px;">
	       					<span>码率： </span>
	       					<input type="text" name="audiobitrate" value="{{audiobitrate}}"></input>
	       					<span>Kbps</span>
	       					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>音量模式： </span>
	    					<select name="audiovolumemode">
	    						{{#select audiovolumemode}}
	    						<option value="0">跟随源</option>
	    						<option value="1">音量增益</option>
	    						<option value="2">音量平衡</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>增益： </span>
	    					<select name="audioboostlevel" {{#ifCond audiovolumemode
                        "!=" "1"}}disabled{{/ifCond}}>
	    						{{#select audioboostlevel}}
	    						<option value="0">0</option>
	    						<option value="1">1</option>
	    						<option value="2">2</option>
	    						<option value="3">3</option>
	    						<option value="4">4</option>
	    						<option value="5">5</option>
	    						<option value="6">6</option>
	    						<option value="7">7</option>
	    						<option value="8">8</option>
	    						<option value="9">9</option>
	    						<option value="10">10</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:230px;">
	    					<span>平衡级别： </span>
	    					<select name="audiobalancelevel" {{#ifCond audiovolumemode
                        "!=" "2"}}disabled{{/ifCond}}>
	    						{{#select audiobalancelevel}}
	    						<option value="0">低</option>
	    						<option value="5">中</option>
	    						<option value="10">高</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    					<div class="line-block" style="width:215px;">
	    					<span>音量： </span>
	    					<input type="text" name="audiobalancedb" {{#ifCond audiovolumemode
                        "!=" "2"}}disabled{{/ifCond}} value="{{audiobalancedb}}"></input>
	    					<span>db</span>
	    					</div>
	    				</div>
	    				<div class="line clearfix">
	    					<div class="line-block" style="width:230px;">
	    					<span>声道处理： </span>
	    					<select name="audiochannelprocessing">
	    						{{#select audiochannelprocessing}}
	    						<option value="None">无</option>
	    						<option value="Left">只保留源片的左声道</option>
	    						<option value="Right">只保留源片的右声道</option>
	    						<option value="Mix">混合源片所有声道</option>
	    						{{/select}}
	    					</select>
	    					</div>
	    				</div>
	    			</div>
	    			{{/unless}}
	    		</div>
	    		</div>
</script>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/common/normalize.css"/>
    <utils:css path="/css/task.css, /css/profile.css"/>
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
            TaskProfile.initItem();
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
    	<div class="content-wrapper" id="taskprofile-container">
    	<div id="global-validation-message"></div>
    	<div id="taskprofile-title">
			<c:set var="opName">
				<c:choose>
					<c:when test="${op == 'copy'}">复制</c:when>
					<c:when test="${op == 'edit'}">编辑</c:when>
					<c:when test="${op == 'new'}">新建</c:when>
				</c:choose>
			</c:set>
			${opName}任务模板
		</div>
    	<div id="taskprofile-content">
	    	<div id="taskprofile-basic">
	    		
	    	</div>
	    	<div id="taskprofile-input">
	    		
	    	</div>

	    	<div id="taskprofile-outputprofile">
	    		<div class="component-title">
		    		输出流参数设定
		    		<div id="outputprofile-add-btn">
			    		<div  class="dialog-btn">
					        <a>
					            <span class="btn-left"></span>
					            <span class="btn-middle">
					                <span class="btn-text">新增输出流</span>
					            </span>
					            <span class="btn-right"></span>
					        </a>
					    </div>	
					</div>
					<div id="outputprofile-import-btn">
			    		<div  class="dialog-btn">
					        <a>
					            <span class="btn-left"></span>
					            <span class="btn-middle">
					                <span class="btn-text">导入输出流</span>
					            </span>
					            <span class="btn-right"></span>
					        </a>
					    </div>						
					</div>
		    	</div>
				
			    <div id="outputprofilelist">
			    </div>   		
	    	</div>

	    	<div id="taskprofile-output">
	    		
	    	</div>
	    	
	    </div>
	    <div id="taskprofile-submit">
	    		<div id="taskprofile-back-btn" class="dialog-btn">
	                <a>
	                    <span class="btn-left"></span>
	                    <span class="btn-middle">
	                        <span class="btn-text">返回</span>
	                    </span>
	                    <span class="btn-right"></span>
	                </a>
	            </div>
				<div id="taskprofile-save-btn" class="dialog-btn">
	                <a>
	                    <span class="btn-left"></span>
	                    <span class="btn-middle">
	                        <span class="btn-text">保存</span>
	                    </span>
	                    <span class="btn-right"></span>
	                </a>
	            </div>            
	    </div>
	    <div id="taskprofile-importlist">
	    </div>
    </div>
    <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script type="text/x-handlebars-template" id="importItemTemplate">
	<div class="import-item">
		<input type="checkbox" name="import-item-select"/>
		<span>{{name}}</span>
	</div>
</script>
<script type="text/x-handlebars-template" id="validationTemplate">
	{{#each errors}}
	<div>{{this}}</div>
	{{/each}}
</script>
<script type="text/x-handlebars-template" id="importListTemplate">
		<div id="importList-title">导入流模板</div>
		<div id="importlist-container">
		</div>
		<div id="importlist-submit">
				<div id="importlist-cancel-btn" class="dialog-btn">
	                <a>
	                    <span class="btn-left"></span>
	                    <span class="btn-middle">
	                        <span class="btn-text">取消</span>
	                    </span>
	                    <span class="btn-right"></span>
	                </a>
	            </div>
	    		<div id="importlist-select-btn" class="dialog-btn">
	                <a>
	                    <span class="btn-left"></span>
	                    <span class="btn-middle">
	                        <span class="btn-text">确定</span>
	                    </span>
	                    <span class="btn-right"></span>
	                </a>
	            </div>
   
	    </div>
</script>
<script type="text/x-handlebars-template" id="basicTemplate">
	<div class="line clearfix">
		<div class="line-block" style="width: 315px;">
		<span>名称： </span>
    	<input type="text" name="taskname" value="{{taskname}}"></input>
    	</div>
    </div>
    <div class="line clearfix">
    	<div class="line-block" style="width: 315px;">
    	<span>描述： </span>
    	<input type="text" name="taskdescription" value="{{taskdescription}}"></input>
    	<input type="hidden" name="profileid" value="{{id}}"></input>
    	</div>
    </div>
    <div class="line clearfix">
    	<div class="line-block" style="width: 195px;">
    	<span>编码策略： </span>
    	<select name="taskencodingoption">
    		{{#select taskencodingoption}}
    		<option value="BestQuality">最高质量</option>
    		<option value="GoodQuality">高质量</option>
    		<option value="Balance">均衡</option>
    		<option value="Fast">快速</option>
    		<option value="Fastest">最快速</option>
    		<option value="Custom">自定义</option>
    		{{/select}}
    	</select>
    	</div>
    	<div class="line-block" style="width: 230px;">
    	<span>优先级： </span>
    	<select name="taskpriority">
    		{{#select taskpriority}}
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
		{{#if enableGpu}}
    	<div class="line-block" style="width: 240px;">
    	<span>GPU数量： </span>
    	<select name="taskgpucores">
    		{{#select taskgpucores}}
    		<option value="1">1</option>
    		<option value="2">2</option>
    		<option value="3">3</option>
    		<option value="4">4</option>
    		<option value="8">8</option>
    		{{/select}}
    	</select>
    	</div>
		{{/if}}
    </div>
</script>
<script type="text/x-handlebars-template" id="taskinputTemplate">
	<div class="component-title">
		输入设定
	</div>
	<div id="taskinput-content" class="line clearfix">
		<div class="line-block" style="width:195px; display:none">
		<span>输入画面： </span>
		<select name="taskinputrowcolumn">
			{{#select taskinputrowcolumn}}
			<option value="6X6">6X6</option>
			<option value="5X5">5X5</option>
			<option value="4X5">4X5</option>
			<option value="4X4">4X4</option>
			<option value="3X4">3X4</option>
			<option value="3X3">3X3</option>
			<option value="2X2">2X2</option>
			<option value="1X1">1</option>
			{{/select}}
		</select>
		</div>
		<div class="line-block" style = "width: 200px;">
		<span>允许Program ID变化：</span>
		<input type="checkbox" name="taskinputallowprogramidchange" id="taskinputallowprogramidchange"
			{{#if allowProgramIdChange}}checked="checked"{{/if}}/>
		</div>
	</div>

	
</script>
<script type="text/x-handlebars-template" id="taskoutputprofilesaveTemplate">
	<div id="saveoutputprofile-title">另存为流模板</div>
	<div class="clearfix">
		<div class="line-block" style="width:260px;">
		<span>名称： </span>
	    <input type="text" name="name" value=""/>
	    </div>	
	</div>
	<div class="clearfix" style="line-height: 50px;">
		<div class="line-block " style="width:596px;">
		<span>描述： </span>
    	<textarea name="description" style="width:400px; height:50px; resize:none; vertical-align:middle;"></textarea>
		</div>
	</div>
	<div id="saveoutputprofile-submit">
				<div id="saveoutputprofile-cancel-btn" class="dialog-btn">
	                <a>
	                    <span class="btn-left"></span>
	                    <span class="btn-middle">
	                        <span class="btn-text">取消</span>
	                    </span>
	                    <span class="btn-right"></span>
	                </a>
	            </div>
	    		<div id="saveoutputprofile-ok-btn" class="dialog-btn">
	                <a>
	                    <span class="btn-left"></span>
	                    <span class="btn-middle">
	                        <span class="btn-text">确定</span>
	                    </span>
	                    <span class="btn-right"></span>
	                </a>
	            </div>
	</div>
</script>
<script type="text/x-handlebars-template" id="taskoutputprofileTemplate">
	<div class="section section-collapse">
		<div class="section-trigger">
			<div class="sectiontitle clearfix">
				<div class="outputprofile-summary line-block">
				</div>
				<div class="line-block-right">
				<div class="outputprofile-remove-btn">
	            </div>			
	            </div>
			</div>
		</div>
		<div class="saveoutputmodal-container"></div>
		<div class="sectioncontent">
			
            <div class="outputprofile-content">
            	<div class="clearfix">
	            	<div class="outputprofile-save-btn">
		            <div class="dialog-btn">
		                <a>
		                    <span class="btn-left"></span>
		                    <span class="btn-middle">
		                        <span class="btn-text">另存为流模板</span>
		                    </span>
		                    <span class="btn-right"></span>
		                </a>
		            </div>
		            </div>
		        </div>
            	<div class="outputprofile-video">
	    		
    			</div>
    			<div>
	    			<div class="stream-title-line clearfix">
		    			<div class="line-block stream-title">
		    				音频
		    			</div>
		    			<div class="audioprofiles-add-btn">
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
					<div class="outputprofile-audio">
					</div>
	    		</div>
            </div>
		</div>
	</div>
</script>
<script type="text/x-handlebars-template" id="taskoutputtabitemTemplate">
	<div class="tab-content">
		<span>{{index}}</span>
		<div class="outputtabitem-remove-btn" style="display:inline-block;"></div>	            
	</div>
</script>
<script type="text/x-handlebars-template" id="taskoutputtabsTemplate">
	<div id="outputtabs-width">
		<div id="outputtabs">
		</div>
	</div>
	<div class="outputtabs-btn-container">
        <div id="outputtabs-next-btn"></div>
    </div>
   	<div class="outputtabs-btn-container">
        <div id="outputtabs-prev-btn"></div>
    </div>
</script>
<script type="text/x-handlebars-template" id="taskoutputTemplate">
	<div id="taskoutput-header">
		<div class="component-title">
			输出设定
			<div id="outputtabs-add-btn">
				<div  class="dialog-btn">
			        <a>
			            <span class="btn-left"></span>
			            <span class="btn-middle">
			                <span class="btn-text">新增输出设定</span>
			            </span>
			            <span class="btn-right"></span>
			        </a>
			    </div>
			</div>
			<div id="outputtabs-copy-btn">
		   	<div  class="dialog-btn">
		        <a>
		            <span class="btn-left"></span>
		            <span class="btn-middle">
		                <span class="btn-text">复制输出设定</span>
		            </span>
		            <span class="btn-right"></span>
		        </a>
		    </div>
		    </div>
		</div>
	</div>
	<div id="outputtabs-container">
	</div>
	<div id="output-current">
	</div>
</script>
<script type="text/x-handlebars-template" id="filearchiveoutputTemplate">
	<div class="line">
		<span>输出类型： </span>
		<select name="outputtype">
			{{#select outputtype}}
			<option value="FileArchive">FileArchive</option>
			<option value="UdpStreaming">Udp</option>
			<option value="FlashStreaming">Flash</option>
			{{/select}}
		</select>
		<span>容器格式： </span>
		<select name="outputcontainer">
			{{#select outputtype}}
			<option value="TS">MPEG-2 Transport Stream</option>
			<option value="MP4">MPEG-4 Container</option>
			{{/select}}
		</select>
	</div>
		{{#ifCond linkedprofile "==" "-1"}}
			<div>
				Stream Setting
			   	<div id="output-stream-add-btn" class="dialog-btn">
			        <a>
			            <span class="btn-left"></span>
			            <span class="btn-middle">
			                <span class="btn-text">新增输出流</span>
			            </span>
			            <span class="btn-right"></span>
			        </a>
			    </div>
			</div>
		{{else}}
			<div>
				Stream Setting
			</div>
			<div>
				<select name="linkedprofile">
				{{#select linkedprofile}}
				{{#each outputprofiles}}
					<option value="{{value}}">流参数-{{name}}</option>
				{{/each}}
				{{/select}}
				</select>
				<div id="output-stream-remove-btn" class="dialog-btn">
			        <a>
			            <span class="btn-left"></span>
			            <span class="btn-middle">
			                <span class="btn-text">删除输出流</span>
			            </span>
			            <span class="btn-right"></span>
			        </a>
			    </div>
			</div>
		{{/ifCond}}
</script>

<script type="text/x-handlebars-template" id="flashstreamingoutputTemplate">
	<div class="line clearfix">
		<div class="line-block" style="width:510px;">
		<span>描述： </span>
		<input type="text" name="outputdescription" value="{{outputdescription}}" style="width:400px;"></input>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:220px;">
		<span>输出类型： </span>
		<select name="outputtype">
			{{#select outputtype}}
			<option value="UdpStreaming">Udp</option>
			<option value="FlashStreaming">Flash</option>
			{{/select}}
		</select>
		</div>
		<div class="line-block" style="width:265px;">
		<span>容器格式： </span>
		<select name="outputcontainer">
			{{#select outputcontainer}}
			<option value="RTMP">RTMP</option>
			{{/select}}
		</select>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:220px;">
		<span>输出位置： </span>
		<select type="text" name="outputDest" value="{{outputDest}}">
			{{#select outputDest}}
			<option value="0">Screen</option>
			<option value="1">Mobile</option>
			{{/select}}
		</select>
		</div>
	</div>
		{{#ifCond linkedprofile "==" "-1"}}
			<div id="linkedprofile">
				输出流参数设定
				<div id="output-stream-add-btn">
			   	<div class="dialog-btn">
			        <a>
			            <span class="btn-left"></span>
			            <span class="btn-middle">
			                <span class="btn-text">新增输出流</span>
			            </span>
			            <span class="btn-right"></span>
			        </a>
			    </div>
			    </div>
			</div>
		{{else}}
			<div id="linkedprofile">
				输出流参数设定
			</div>
			<div class="line clearfix" style="padding:10px 10px;">
				<div class="line-block">
				<select name="linkedprofile">
				{{#select linkedprofile}}
				{{#each outputprofiles}}
					<option value="{{value}}">流参数-{{name}}</option>
				{{/each}}
				{{/select}}
				</select>
				</div>
				<div class="line-block-right" style="display: none;">
	    			<div id="output-stream-remove-btn"></div>		                
		        </div>
			</div>
		{{/ifCond}}
	</div>
</script>
<script type="text/x-handlebars-template" id="udpstreamingoutputTemplate">
	<div class="line clearfix">
		<div class="line-block" style="width:510px;">
		<span>描述： </span>
		<input type="text" name="outputdescription" value="{{outputdescription}}" style="width:400px;"></input>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:220px;">
		<span>输出类型： </span>
		<select name="outputtype">
			{{#select outputtype}}
			<option value="UdpStreaming">Udp</option>
			<option value="FlashStreaming">Flash</option>
			{{/select}}
		</select>
		</div>
		<div class="line-block" style="width:265px;">
		<span>容器格式： </span>
		<select name="outputcontainer">
			{{#select outputcontainer}}
			<option value="UDPOverTS">TSOverUDP</option>
			{{/select}}
		</select>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:220px;">
		<span>输出位置： </span>
		<select type="text" name="outputDest" value="{{outputDest}}">
			{{#select outputDest}}
			<option value="0">Screen</option>
			<option value="1">Mobile</option>
			{{/select}}
		</select>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:176px;">
		<span>缓存： </span>
		<input type="text" name="outputbuffersize" value="{{outputbuffersize}}"></input>
		</div>
		<div class="line-block" style="width:265px;">
		<span>TTL： </span>
		<input type="text" name="outputTTL" value="{{outputTTL}}"></input>
		</div>
		<div class="line-block" style="width:300px;">
		<span>IGMP source IP： </span>
		<input type="text" name="outputIGMP" value="{{outputIGMP}}"></input>
		</div>
	</div>
	{{#with outputtsoption}}
	<div class="outputoption">
		TS高级选项
	<div class="line clearfix">
		<div class="line-block" style="width:265px;">
		<span>Service name： </span>
		<input type="text" name="tsservicename" value="{{tsservicename}}"></input>
		</div>
		<div class="line-block" style="width:343px;">
		<span>PMT PID： </span>
		<input type="text" name="tspmtpid" value="{{tspmtpid}}"></input>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:265px;">
		<span>Service provider： </span>
		<input type="text" name="tsserviceprovider" value="{{tsserviceprovider}}"></input>
		</div>
		<div class="line-block" style="width:343px;">
		<span>Video PID： </span>
		<input type="text" name="tsvideopid" value="{{tsvideopid}}"></input>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:265px;">
		<span>Service ID： </span>
		<input type="text" name="tsserviceid" value="{{tsserviceid}}"></input>
		</div>
		<div class="line-block" style="width:343px;">
		<span>Start Audio PID： </span>
		<input type="text" name="tsaudiopid" value="{{tsaudiopid}}"></input>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:308px;">
		<span>总码率： </span>
		<input type="text" name="tstotalbitrate" value="{{tstotalbitrate}}"></input>
		<span>kbps</span>
		</div>
		<div class="line-block" style="width:300px;">
		<span>PCR PID： </span>
		<input type="text" name="tspcrpid" value="{{tspcrpid}}"></input>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:265px;">
		<span>Network ID： </span>
		<input type="text" name="tsnetworkid" value="{{tsnetworkid}}"></input>
		</div>
		<div class="line-block" style="width:343px;">
		<span>Transport ID： </span>
		<input type="text" name="tstransportid" value="{{tstransportid}}"></input>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:158px;">
		<span>插入TOT&TDT： </span>
		<input type="checkbox" name="tsinserttottdt" value="{{tsinserttottdt}}"></input>
		</div>
		<div class="line-block" style="width:482px;">
		<span>TOT&TDT周期： </span>
		<input type="text" name="tstottdtperiod" value="{{tstottdtperiod}}"></input>
		<span>ms</span>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:297px;">
		<span>PCR周期： </span>
		<input type="text" name="tspcrperiod" value="{{tspcrperiod}}"></input>
		<span>ms</span>
		</div>
		<div class="line-block" style="width:343px;">
		<span>PAT周期： </span>
		<input type="text" name="tspatperiod" value="{{tspatperiod}}"></input>
		<span>ms</span>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:297px;">
		<span>SDT周期： </span>
		<input type="text" name="tssdtperiod" value="{{tssdtperiod}}"></input>
		<span>ms</span>
		</div>
	</div>
	<div class="line clearfix">
		<div class="line-block" style="width:265px;">
		<span>Private stream PID： </span>
		<input type="text" name="tsprivatemetadatapid" value="{{tsprivatemetadatapid}}"></input>
		</div>
		<div class="line-block" style="width:343px;">
		<span>Private stream type： </span>
		<input type="text" name="tsprivatemetadatatype" value="{{tsprivatemetadatatype}}"></input>
		</div>
	</div>
	{{/with}}
		{{#ifCond linkedprofile "==" "-1"}}
			<div id="linkedprofile">
				输出流参数设定
				<div id="output-stream-add-btn">
			   	<div class="dialog-btn">
			        <a>
			            <span class="btn-left"></span>
			            <span class="btn-middle">
			                <span class="btn-text">新增输出流</span>
			            </span>
			            <span class="btn-right"></span>
			        </a>
			    </div>
			    </div>
			</div>
		{{else}}
			<div id="linkedprofile">
				输出流参数设定
			</div>
			<div class="line clearfix" style="padding:10px 10px;">
				<div class="line-block">
				<select name="linkedprofile">
				{{#select linkedprofile}}
				{{#each outputprofiles}}
					<option value="{{value}}">流参数-{{name}}</option>
				{{/each}}
				{{/select}}
				</select>
				</div>
				<div class="line-block-right" style="display: none;">
	    			<div id="output-stream-remove-btn"></div>		                
		        </div>
			</div>
		{{/ifCond}}
	</div>
</script>
<script type="text/x-handlebars-template" id="novideoprofileTemplate">
			<div>
				<div id="videoprofiles-add-btn" class="dialog-btn">
	                <a>
	                    <span class="btn-left"></span>
	                    <span class="btn-middle">
	                        <span class="btn-text">新增视频</span>
	                    </span>
	                    <span class="btn-right"></span>
	                </a>
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
	    			<input type="hidden" name="index" value="{{index}}"></input>
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
	    		<div class="section">
	    			<div class="section-trigger section-collapse">
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
	    			<input type="hidden" name="index" value="{{index}}"/>
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
	    						<option value="MPEG4">MPEG4</option>
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
	    					<select name="videoqualityleveldisp" {{#ifCond taskencodingoption "!=" "Custom"}}disabled{{/ifCond}}>
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

							<c:if test="${supportmosaic}">
								<span style="font-size:15px;margin-left:10px;">混音</span>
								<input type="checkbox" name="mixaudio" {{#if audiomix}}checked="checked"{{/if}}></input>
							</c:if>
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
								<option value="MP2">MP2</option>
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
<script type="text/x-handlebars-template" id="aacaudioprofileTemplate_MP2">
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
					<c:if test="${supportmosaic}">
						<span style="font-size:15px;margin-left:10px;">混音</span>
						<input type="checkbox" name="mixaudio" {{#if audiomix}}checked="checked"{{/if}}></input>
					</c:if>
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
							<option value="MP2">MP2</option>
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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    <utils:css path="/css/channelList.css"/>
    <utils:js path="/js/plugins/nicescroll/jquery.nicescroll.min.js,/js/channelList.js,/js/My97DatePicker/WdatePicker.js"/>
    <script type="text/javascript">
        $(function () {
            ChannelManager.init();
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">
        <div id="add-channel-dialog" class="modal" style="display:none;"></div>
        <div id="loading-dialog" class="modal" style="display:none;width: 350px;"></div>
        <div id="add-channel-list-dialog" class="modal" style="display:none;width: 500px;">
            <div class="dialog-caption">添加频道分组</div>
            <div class="dialog-content"></div>
            <div class="dialog-btns">
                <div id="add-channel-list-cancel-btn" class="dialog-btn">
                    <a>
                        <span class="btn-left"></span>
					<span class="btn-middle">
						<span class="btn-text">取消</span>
					</span>
                        <span class="btn-right"></span>
                    </a>
                </div>
                <div id="add-channel-list-ok-btn" class="dialog-btn">
                    <a>
                        <span class="btn-left"></span>
					<span class="btn-middle">
						<span class="btn-text">确定</span>
					</span>
                        <span class="btn-right"></span>
                    </a>
                </div>
            </div>
        </div>
        <div id="move-channel-dialog" class="modal" style="display:none;width: 500px;">
            <div class="dialog-caption">移动频道</div>
            <div class="dialog-content"></div>
            <div class="dialog-btns">
                <div id="move-channel-cancel-btn" class="dialog-btn">
                    <a>
                        <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">取消</span>
                    </span>
                        <span class="btn-right"></span>
                    </a>
                </div>
                <div id="move-channel-ok-btn" class="dialog-btn">
                    <a>
                        <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">确定</span>
                    </span>
                        <span class="btn-right"></span>
                    </a>
                </div>
            </div>

        </div>

        <div class="content-wrapper clearfix content-right-column">
            <div class="channel-lists sidebar">
                <p class="title">
                    <span class="title-content">频道分组列表</span>
                    <a class="add-btn" id="group-add-btn" href="#add-channel-list-dialog"></a>
                </p>
                <ul id="channel-lists-nav">
                </ul>
            </div>
            <div class="list content">
                <div class="head clearfix">
                    <div class="title">
                        <span class="title-icon"></span>
                        <span class="title-name">未分组</span>
                    </div>
                    <div class="add-channel">
                        <div class="action-btn">
                            <a href="#add-channel-dialog" id="add-channel-btn">
                                <span class="btn-left"></span>
							<span class="btn-middle">
								<span class="btn-text">添加频道</span>
								<span class="btn-icon"></span>
							</span>
                                <span class="btn-right"></span>
                            </a>
                        </div>
                    </div>
                </div>
                <div class="action-bar">
                    <div>
                        <div class="action-bar-item edit" id="edit-channel-btn">修改</div>
                    </div>
                    <div>
                        <div class="action-bar-item delete" id="delete-channel-btn">删除</div>
                    </div>
                    <div>
                        <div class="action-bar-item move" id="move-channel-btn">移动</div>
                    </div>
                    <c:if test="${supportmosaic==false}">
                    <div>
                        <div class="action-bar-item start" id="start-channel-btn">启动</div>
                    </div>
                    <div>
                        <div class="action-bar-item stop" id="stop-channel-btn">停止</div>
                    </div>
                    </c:if>
                    <div>
                        <label id="channel-search-label">搜索</label>
                        <input type="text" id="channel-search-input"></input>
                    </div>
                </div>
                <div class="tab-content" id="channel-list-content">
                    <table id="channels-table">
                        <thead>
                        <tr>
                            <th width="40px" class="right-align">
                                <input type="checkbox" class="select-all">
                            </th>
                            <th width="100px">ID</th>
                            <th width="180px">频道名称</th>
                            <!--<th>流地址</th>-->
                            <c:if test="${!supportmosaic}">
                            <th>状态</th>
                            </c:if>
                            <th>频道信息</th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <div class="push"></div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script type="text/x-handlebars-template" id="groupTemplate">
    <li data-id="-1">
        <a>
            <span class="nav-icon"></span>
            <span class="nav-title">未分组</span>
        </a>
    </li>
    {{#each groups}}
    <li data-id="{{id}}">
        <a>
            <span class="nav-icon"></span>
            <span class="nav-title">{{name}}</span>
            <span class="remove-btn"></span>
        </a>
    </li>
    {{/each}}
</script>
<script type="text/x-handlebars-template" id="channelsTemplate">
    {{#each channels}}
    <tr class="channel-item">
        <td class="right-align">
            <input type="checkbox" class="select-one" name="channel-ids" value="{{id}}">
        </td>
        <td class="channel-item-name">{{id}}</td>
        <td class="channel-item-name">{{name}}</td>
        <!--<td title="{{address}}">{{{stripString address 25 '...'}}}</td>-->
        <c:if test="${!supportmosaic}">
        <td class="channel-status"></td>
        </c:if>
        <td title="{{address}}" class="channel-item-name">
            <div>{{{stripString address 50 '...'}}}
                <div class="detail">
                    <div class="detail-status"></div>
                </div>
            </div>
        </td>
    </tr>
    {{/each}}

    <tr id="image-samples-info" style="display:none">
        <td colspan="6">

            <div class="Content TableItemText">
                <div id="inputinfo" class="Row">
                    <div class="ThumbCol">
                        <img>
                    </div>
                    <div class="InputDetail">
                        <div id="iSrcName" class="Item">
                            <span class="Lbl">频道名称:</span>
                            <span class="Val"></span>
                        </div>
                        <div id="iSrcUrl" class="Item">
                            <span class="Lbl">源地址:</span>
                            <span class="Val"></span>
                        </div>
                        <div id="iSrcContainer" class="Item">
                            <span class="Lbl">类型:</span>
                            <span class="Val"></span>
                        </div>
                        <div id="iSrcMediaInfoContainer" class="Item">
                            <span class="Lbl">媒体信息:</span><br>
					<span id="MediaInfo" class="Val">
						<div id="videoMediaInfo">
                            <span>视频编码:</span><span id="VCodec" style="margin-left: 5px;"></span><br>
                            <div style="display:none;">
                            <span>视频码率:</span><span id="VBitrate" style="margin-left: 5px;"></span><br>
                            </div>
                            <span>视频帧率:</span><span id="VFramerate" style="margin-left: 5px;"></span><span
                                id="VFramerateLbl">fps</span><br>
                            <span>视频分辨率:</span><span id="VResolution" style="margin-left: 5px;"></span><br>
                            <span>视频宽高比:</span><span id="VAspectRadio" style="margin-left: 5px;"></span><br>
                        </div>
						<div id="audioMediaInfo">
                            <div style="display:none;">
                            <span>音频语言:</span><span id="ALanguage" style="margin-left: 5px;"></span><br>
                            </div>
                            <span>音频编码:</span><span id="ACodec" style="margin-left: 5px;"></span><br>
                            <div style="display:none;">
                            <span>音频码率:</span><span id="ABitrate" style="margin-left: 5px;"></span><br>
                            </div>
                            <span>音频采样率:</span><span id="ASamplerate" style="margin-left: 5px;"></span><br>
                            <span>音频声道:</span><span id="AChannels" style="margin-left: 5px;"></span><span
                                id="AChannelsLbl"></span><br>
                            <span>音频位深:</span><span id="ABitDepth" style="margin-left: 5px;"></span><br>
                        </div>
					</span>
                            <div id="screen-position-name" class="Item" style="margin-left: 15px">
                                <span class="Lbl">屏幕墙列表:</span><br>
                                <span class="Val" style="margin-left: 35px"></span>
                            </div>

                            <div id="iOutUrl" class="Item">
                                <span class="Lbl">输出地址:</span>
                                <span class="Val"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </td>
    </tr>
</script>
<script type="text/x-handlebars-template" id="addGroupTemplate">
    <div style="text-align:center;margin:20px 0px;">
        <form id="groupForm">
            <span>分组名称：</span>
            <input type="text" id="groupName" name="groupName" required/>
        </form>
    </div>
</script>
<script type="text/x-handlebars-template" id="moveChannelTemplate">
    <div style="text-align:center;margin:20px 0px;">
        <form id="moveForm">
            <select style="width:150px;" name="moveToChannel" id="moveToChannel" required>
                {{#each groups}}
                <option value="{{id}}">{{name}}</option>
                {{/each}}
            </select>
        </form>
    </div>
</script>
<script type="text/x-handlebars-template" id="addChannelTemplate">
    <div class="dialog-caption">{{#if id}}修改{{else}}添加{{/if}}频道</div>
    <div class="dialog-content">

        <c:choose>
        <c:when test="${supportmosaic}">
        <form id="channel-form" style="height: 230px; overflow-y: auto;">
            </c:when>
            <c:otherwise>
            <form id="channel-form" style="height: 500px; overflow-y: auto;">
                </c:otherwise>
                </c:choose>

            <div class="basic">
                <input type="hidden" name="id" value="{{id}}">

                <div class="line">
                    <span style="width:100px;">频道名称：</span>
                    <input type="text" style="width:160px;" name="name" id="name" value="{{name}}" required
                           maxlength=12/>
                    <span style="width:100px;">IP流协议：</span>
                    <select style="width:160px;" name="protocol" id="protocol" required>
                        <option value="">请选择</option>
                        <option value="udp" {{#ifCond protocol "==" "udp"}}selected{{/ifCond}}>UDP</option>
                        <option value="http" {{#ifCond protocol "==" "http"}}selected{{/ifCond}}>HTTP</option>
                        <option value="rtsp" {{#ifCond protocol "==" "rtsp"}}selected{{/ifCond}}>RTSP</option>
                        <option value="rtmp" {{#ifCond protocol "==" "rtmp"}}selected{{/ifCond}}>RTMP</option>
                        <option value="rtp" {{#ifCond protocol "==" "rtp"}}selected{{/ifCond}}>RTP</option>
                        <option value="sdi" {{#ifCond protocol "==" "sdi"}}selected{{/ifCond}}>SDI</option>
                    </select>
                </div>
                <div class="line" id="sdp-upload" style="display:none;">
                    <span style="width:100px;">sdp文件：</span>
                    <input type="file" name="sdp" id="sdp">
                    <input type="submit" name="sdp-submit" id="sdp-submit" value="上传sdp文件">
                    <span id="sdp-error-message"></span>
                </div>
                <div class="line" id="sdi-div" style="display:none;">
                    <%--<span style="width:100px;">设备：</span>
                    <select name="sdiServer" id="SDIServer" style="width:160px;">
                        127.0.0.1
                    </select>--%>
                    <span style="width:100px;">端口：</span>
                    <select name="port"  id="port" style="width:160px;">
                    </select>
                        <span style="color: red">(注意:端口占用播放或者无端口会取不到频道和音轨)</span>
                </div>

                <div class="line">
                    <span style="width:100px;">URL地址：</span>
                    <input type="text" style="width:438px;" name="ip" id="ip" value="{{ip}}" />
                </div>

                <div class="line">
                    <span style="width:100px;">频道：</span>
                    <select style="width:160px;" name="programId" id="programId" required>
                    </select>
                    <span style="width:100px;">音轨：</span>
                    <select style="width:160px;" name="audioId" id="audioId" required>
                    </select>
                    <input type="hidden" id="defaultProgramId" value="{{pid}}">
                    <input type="hidden" id="defaultAudioId" value="{{audioId}}">
                </div>
                <div class="line" id="media-loading-div" style="margin-left: 115px;display: none;">读取媒体信息中<span
                        id="media-loading"></span></div>
                <div class="line">
                    <span style="width: 100px">频道标签：</span>
                    <div id="channel-tag-list">
                    <button type="button" id="add-tag-btn">添加标签</button>
                    {{#each tags}}
                        <div class="channel-tag">
                            <input type="text" class="channel-tag-name" name="channel-tag-name" value="{{name}}"/>
                            <span class="del-tag-btn">&times;</span>
                        </div>
                    {{/each}}
                    </div>

                    <c:if test="${supportmosaic}">
                        <div style="display: inline;" id="original_channal_id">
                            <span style="width:100px;">外部频道ID：</span>
                            <input type="text" style="ime-mode: disabled;width:270px;" name="origchannelid" id="origchannelid" required value="{{origchannelid}}"
                                   onkeyup="this.value=this.value.replace(/\D/g,'')"  onafterpaste="this.value=this.value.replace(/\D/g,'')" maxlength=33/>
                        </div>
                    </c:if>

                </div>
    <c:if test="${!supportmosaic}">
                <div class="line">
                    <%--<span style="width: 100px;">储存时间：</span>--%>
                    <%--<input type="text" style="width:30px;" name="maxPersistDays" id="maxPersistDays" value="{{maxPersistDays}}" disabled/>--%>
                    <%--<span>天</span>--%>
                    <span style="width: 100px;">录制模式：</span>
                    <input type="checkbox" name="disableRecord" id="disableRecord" {{#if disableRecord}}checked="checked" {{/if}}/>
                    <span>不录制</span>

                    <input type="checkbox" name="enableRecord" id="enableRecord" value="1" {{#if
                           enableRecord}}checked="checked" {{/if}}/>
                    <span>循环录制</span>

                    <input type="checkbox" name="enableTriggerRecord" id="enableTriggerRecord" value="1" {{#if enableTriggerRecord}}checked="checked" {{/if}} />
                    <span>告警触发录制</span>
                    <%--<span>储存格式：</span>
                    <select name="recordFormat" disabled>
                        <option value="0">mp4</option>
                        <option value="1" selected>hls</option>
                    </select>--%>
                    <input type="hidden" name="recordFormat" value="1"/>



                    <%--
                    <span>信号检测：</span>
                    <input type="checkbox" name="enableSignalDetect" disabled id="enableSignalDetect" value="1" {{#if
                           enableSignalDetect}}checked="checked" {{/if}} />--%>

                </div>
    </c:if>
            </div>
           <c:if test="${supportmosaic==false}">
            <div class="mobile">

                <div class="section-switcher">
                    <input type="checkbox" name="isSupportMobile" id="isSupportMobile" value="1" {{#if
                               isSupportMobile}}checked="checked" {{/if}}/>
                    <span>支持移动设备</span>
                </div>

                <div class="line" style="display:none">
                    <span style="width:100px;">标清视频码率：</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[0].videoBitrate"
                           id="mobile-sd-videoBitrate" value="{{mobileConfigs.[0].videoBitrate}}"/>
                    <span>kpbs</span>
                    <span style="width:85px;">音频码率：</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[0].audioBitrate"
                           id="mobile-sd-audioBitrate" value="{{mobileConfigs.[0].audioBitrate}}"/>
                    <span>kpbs</span>
                    <span style="width:75px;">分辨率：</span>
                    <span>宽</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[0].width" id="mobile-sd-width"
                           value="{{mobileConfigs.[0].width}}"/>
                    <span>高</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[0].height" id="mobile-sd-height"
                           value="{{mobileConfigs.[0].height}}"/>
                    <input type="hidden" name="mobileConfigs[0].type" id="mobile-sd-type" value="0"/>
                </div>
                <div class="line">
                    <span style="width:100px;">视频码率：</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[1].videoBitrate"
                           id="mobile-hd-videoBitrate" value="{{mobileConfigs.[1].videoBitrate}}"/>
                    <span>kpbs</span>
                    <span style="width:85px;">音频码率：</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[1].audioBitrate"
                           id="mobile-hd-audioBitrate" value="{{mobileConfigs.[1].audioBitrate}}"/>
                    <span>kpbs</span>
                    <span style="width:75px;">分辨率：</span>
                    <span>宽</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[1].width" id="mobile-hd-width"
                           value="{{mobileConfigs.[1].width}}"/>
                    <span>高</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[1].height" id="mobile-hd-height"
                           value="{{mobileConfigs.[1].height}}"/>
                    <input type="hidden" name="mobileConfigs[1].type" id="mobile-hd-type" value="1"/>
                </div>
		<div class="line">
                    <div style="margin-left: 40px">
                        <input type="checkbox" name="mobileConfigs[1].deinterlace" id="mobile-hd-deinterlace" value="1" {{#if
                               mobileConfigs.[1].deinterlace}}checked="checked" {{/if}}/>
                        <span>反交错</span>
                    </div>
                </div>
                <div class="line">
                    <div style="margin-left: 40px">
                        <pre></pre><pre></pre>
                        <span >注意：当视频编码格式为H.264时，设置分辨率为0，以透传的方式处理；</span>
                        <pre></pre>
                        <span>如果本频道需要支持移动设备访问，请设置合适的分辨率和码率。</span>
                    </div>
                </div>

            </div>
           </c:if>

           <c:if test="${supportmosaic==false}">
            <div class="signal-analysis">
                <div class="section-switcher">
                    <input type="checkbox" name="enableSignalDetectByType" id="enableSignalDetectByType" value="1" {{#if enableSignalDetectByType}}checked="checked" {{/if}} />
                    <span>信源检测</span>
                </div>

                <div class="line">
                    <span style="width:200px;">信源异常持续告警间隔时间：</span>
                    <input type="text" style="width:55px;" id="notifyInterval" name="signalDetectByTypeConfig.notifyInterval"
                           value="{{signalDetectByTypeConfig.notifyInterval}}"/>
                    <span>秒</span>
                </div>
                <div class="line">
                    <input type="checkbox" style="margin-left:50px;" id="warningSignalBrokenEnabled" name="signalDetectByTypeConfig.enableWarningSignalBroken"
                     value="1" {{#if signalDetectByTypeConfig.enableWarningSignalBroken}} checked="checked" {{/if}}/>
                    <span style="width:100px;">信源中断：</span>
                    <input type="text" style="width:55px;" id="warningSignalBrokenTimeout" name="signalDetectByTypeConfig.warningSignalBrokenTimeout"
                           value="{{#if signalDetectByTypeConfig.enableWarningSignalBroken}}{{signalDetectByTypeConfig.warningSignalBrokenTimeout}}{{/if}}"
                           {{#if signalDetectByTypeConfig.enableWarningSignalBroken}}enabled{{else}}disabled{{/if}}  />
                    <span>毫秒</span>

                    <input type="checkbox" style="margin-left:80px;" id="warningProgidLossEnabled" name="signalDetectByTypeConfig.enableWarningProgidLoss"
                     value="1" {{#if signalDetectByTypeConfig.enableWarningProgidLoss}} checked="checked" {{/if}}/>
                    <span style="width:110px;">Program ID丢失：</span>
                    <input type="text" style="width:55px;" id="warningProgidLossTimeout" name="signalDetectByTypeConfig.warningProgidLossTimeout"
                           value="{{#if signalDetectByTypeConfig.enableWarningProgidLoss}}{{signalDetectByTypeConfig.warningProgidLossTimeout}}{{/if}}"
                           {{#if signalDetectByTypeConfig.enableWarningProgidLoss}}enabled{{else}}disabled{{/if}}/>
                    <span>毫秒</span>

                </div>
                <div class="line">

                    <input type="checkbox" style="margin-left:50px;" id="warningVideoLossEnabled" name="signalDetectByTypeConfig.enableWarningVideoLoss"
                     value="1" {{#if signalDetectByTypeConfig.enableWarningVideoLoss}} checked="checked" {{/if}}/>
                    <span style="width:100px;">视频流丢失：</span>
                    <input type="text" style="width:55px;" id="warningVideoLossTimeout" name="signalDetectByTypeConfig.warningVideoLossTimeout"
                           value="{{#if signalDetectByTypeConfig.enableWarningVideoLoss}}{{signalDetectByTypeConfig.warningVideoLossTimeout}}{{/if}}"
                           {{#if signalDetectByTypeConfig.enableWarningVideoLoss}}enabled{{else}}disabled{{/if}}  />
                    <span>毫秒</span>

                    <input type="checkbox" style="margin-left:80px;" id="warningAudioLossEnabled" name="signalDetectByTypeConfig.enableWarningAudioLoss"
                     value="1" {{#if signalDetectByTypeConfig.enableWarningAudioLoss}} checked="checked" {{/if}}/>
                    <span style="width:110px;">音频流丢失：</span>
                    <input type="text" style="width:55px;" id="warningAudioLossTimeout" name="signalDetectByTypeConfig.warningAudioLossTimeout"
                           value="{{#if signalDetectByTypeConfig.enableWarningAudioLoss}}{{signalDetectByTypeConfig.warningAudioLossTimeout}}{{/if}}"
                           {{#if signalDetectByTypeConfig.enableWarningAudioLoss}}enabled{{else}}disabled{{/if}}  />
                    <span>毫秒</span>

                </div>
                <div class="line">
                    <input type="checkbox" style="margin-left:50px;" id="warningCcErrorEnabled" name="signalDetectByTypeConfig.enableWarningCcError"
                     value="1" {{#if signalDetectByTypeConfig.enableWarningCcError}} checked="checked" {{/if}}/>
                    <span style="width:100px;">连续计数器错误：</span>
                    <input type="text" style="width:55px;" id="warningCcErrorTimeout" name="signalDetectByTypeConfig.warningCcErrorTimeout"
                           value="{{#if signalDetectByTypeConfig.enableWarningCcError}}{{signalDetectByTypeConfig.warningCcErrorTimeout}}{{/if}}"
                           {{#if signalDetectByTypeConfig.enableWarningCcError}}enabled{{else}}disabled{{/if}} />
                    <span>毫秒内达到</span>
                    <input type="text" style="width:55px;" id="warningCcErrorCount" name="signalDetectByTypeConfig.warningCcErrorCount"
                           value="{{#if signalDetectByTypeConfig.enableWarningCcError}}{{signalDetectByTypeConfig.warningCcErrorCount}}{{/if}}"
                           {{#if signalDetectByTypeConfig.enableWarningCcError}}enabled{{else}}disabled{{/if}}  />
                    <span>个</span>
                </div>
            </div>
           </c:if>

            <%--<div class="signal-analysis">
                <p>信号检测</p>

                <div class="line">
                    <span style="width:100px;">一级错误：</span>
                    <input type="checkbox" name="signalDetectConfig.enableLevel1Error" disabled value="1" {{#if
                           signalDetectConfig.enableLevel1Error}}checked="checked" {{/if}}/>
                    <span style="width:175px;">二级错误：</span>
                    <input type="checkbox" name="signalDetectConfig.enableLevel2Error" disabled value="1" {{#if
                           signalDetectConfig.enableLevel2Error}}checked="checked" {{/if}}/>
                    <span style="width:175px;">三级错误：</span>
                    <input type="checkbox" name="signalDetectConfig.enableLevel3Error" disabled value="1" {{#if
                           signalDetectConfig.enableLevel3Error}}checked="checked" {{/if}}/>
                </div>
            </div>--%>
           <c:if test="${supportmosaic==false}">
            <div class="content-analysis">

                <div class="section-switcher">
                    <input type="checkbox" name="enableContentDetect" id="enableContentDetect" value="1" {{#if enableContentDetect}}checked="checked" {{/if}}/>
                    <span>内容检测</span>
                </div>

                <div class="line">
                    <span style="width:100px;">黑场：</span>
                    <input type="text" style="width:55px;" id="blackSeconds" name="contentDetectConfig.blackSeconds"
                           value="{{contentDetectConfig.blackSeconds}}"/>
                    <span>秒</span>
                    <span style="width:100px;">静音：</span>
                    <input type="text" style="width:55px;" id="silenceSeconds" name="contentDetectConfig.silenceSeconds"
                           value="{{contentDetectConfig.silenceSeconds}}"/>
                    <span>秒</span>
					<span style="width:100px;">静音门限设置：</span>
                    <input type="text" style="width:55px;" id="silenceThreshold" name="contentDetectConfig.silenceThreshold"
                           value="{{contentDetectConfig.silenceThreshold}}"/>
                    <span>db</span>
				</div>
				<div class="line">
					<span style="width:100px;">静帧：</span>
                    <input type="text" style="width:55px;" id="noFrameSeconds" name="contentDetectConfig.noFrameSeconds"
                           value="{{contentDetectConfig.noFrameSeconds}}"/>
                    <span>秒</span>
                    <span style="width:100px;">音量过高：</span>
                    <input type="text" style="width:55px;" id="loudVolumeSeconds" name="contentDetectConfig.loudVolumeSeconds"
                           value="{{contentDetectConfig.loudVolumeSeconds}}"/>
                    <span>秒</span>
                    <span style="width:100px;">音量门限设置：</span>
                    <input type="text" style="width:55px;" id="loudVolumeThreshold" name="contentDetectConfig.loudVolumeThreshold"
                           value="{{contentDetectConfig.loudVolumeThreshold}}"/>
                    <span>db</span>

                </div>
                <div class="line">
					<span style="width:100px; margin-left:198px;">音量过低：</span>
                    <input type="text" style="width:55px;" id="lowVolumeSeconds" name="contentDetectConfig.lowVolumeSeconds"
                           value="{{contentDetectConfig.lowVolumeSeconds}}"/>
                    <span>秒</span>
					<span style="width:100px;">音量门限设置：</span>
                    <input type="text" style="width:55px;" id="lowVolumeThreshold" name="contentDetectConfig.lowVolumeThreshold"
                           value="{{contentDetectConfig.lowVolumeThreshold}}"/>
                    <span>db</span>
                </div>
            </div>

                    <div class="alarmTime">
                        <div class="section-switcher">
                            <span>不告警时间段设置</span>
                        </div>
                        <div class="line">
                            <input type="checkbox" style="margin-left:130px;" id="enableTime1"  name="channelAlarmTime.enableTime1"
                                   {{#if channelAlarmTime.enableTime1}} checked="checked" value="true" {{/if}} >
                            <span>不告警时间段1：</span>
                            <input class="Wdate"
                                   style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="channelAlarmTime.alarmStartTime1" id="alarmStartTime1" value="{{#if channelAlarmTime.enableTime1}}{{channelAlarmTime.alarmStartTime1}}{{/if}}"
                                   onFocus="WdatePicker({dateFmt:'HH:mm:ss',maxDate:alarmEndTime1.value})" >
                            -&nbsp;
                            <input class="Wdate"
                                   style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="channelAlarmTime.alarmEndTime1" id="alarmEndTime1" value="{{#if channelAlarmTime.enableTime1}}{{channelAlarmTime.alarmEndTime1}}{{/if}}"
                                   onFocus="WdatePicker({dateFmt:'HH:mm:ss',minDate:alarmStartTime1.value})">

                        </div>

                        <div class="line">
                            <input type="checkbox" style="margin-left:130px;" id="enableTime2"  name="channelAlarmTime.enableTime2"
                                   {{#if channelAlarmTime.enableTime2}} checked="checked" value="true" {{/if}} >
                            <span>不告警时间段2：</span>
                            <input class="Wdate"
                                   style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="channelAlarmTime.alarmStartTime2" id="alarmStartTime2" value="{{#if channelAlarmTime.enableTime2}}{{channelAlarmTime.alarmStartTime2}}{{/if}}"
                                   onFocus="WdatePicker({dateFmt:'HH:mm:ss',maxDate:alarmEndTime2.value})" >
                            -&nbsp;
                            <input class="Wdate"
                                   style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="channelAlarmTime.alarmEndTime2" id="alarmEndTime2" value="{{#if channelAlarmTime.enableTime2}}{{channelAlarmTime.alarmEndTime2}}{{/if}}"
                                   onFocus="WdatePicker({dateFmt:'HH:mm:ss',minDate:alarmStartTime2.value})">

                        </div>

                        <div class="line">
                            <input type="checkbox" style="margin-left:130px;" id="enableTime3"  name="channelAlarmTime.enableTime3"
                                   {{#if channelAlarmTime.enableTime3}} checked="checked" value="true" {{/if}} >
                            <span>不告警时间段3：</span>
                            <input class="Wdate"
                                   style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="channelAlarmTime.alarmStartTime3" id="alarmStartTime3" value="{{#if channelAlarmTime.enableTime3}}{{channelAlarmTime.alarmStartTime3}}{{/if}}"
                                   onFocus="WdatePicker({dateFmt:'HH:mm:ss',maxDate:alarmStartTime3.value})" >
                            -&nbsp;
                            <input class="Wdate"
                                   style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="channelAlarmTime.alarmEndTime3" id="alarmEndTime3" value="{{#if channelAlarmTime.enableTime3}}{{channelAlarmTime.alarmEndTime3}}{{/if}}"
                                   onFocus="WdatePicker({dateFmt:'HH:mm:ss',minDate:alarmEndTime3.value})">
                        </div>

                        <div class="line">
                            <input type="checkbox" style="margin-left:130px;" id="enableTime4"  name="channelAlarmTime.enableTime4"
                                   {{#if channelAlarmTime.enableTime4}} checked="checked" value="true" {{/if}} >
                            <span>不告警时间段4：</span>
                            <input class="Wdate"
                                   style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="channelAlarmTime.alarmStartTime4" id="alarmStartTime4" value="{{#if channelAlarmTime.enableTime4}}{{channelAlarmTime.alarmStartTime4}}{{/if}}"
                                   onFocus="WdatePicker({dateFmt:'HH:mm:ss',maxDate:alarmEndTime4.value})" >
                            -&nbsp;
                            <input class="Wdate"
                                   style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="channelAlarmTime.alarmEndTime4" id="alarmEndTime4" value="{{#if channelAlarmTime.enableTime4}}{{channelAlarmTime.alarmEndTime4}}{{/if}}"
                                   onFocus="WdatePicker({dateFmt:'HH:mm:ss',minDate:alarmStartTime4.value})">
                        </div>

                        <div class="line">
                            <input type="checkbox" style="margin-left:130px;" id="enableTime5"  name="channelAlarmTime.enableTime5"
                                   {{#if channelAlarmTime.enableTime5}} checked="checked" value="true" {{/if}} >
                            <span>不告警时间段5：</span>
                            <input class="Wdate"
                                   style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="channelAlarmTime.alarmStartTime5" id="alarmStartTime5" value="{{#if channelAlarmTime.enableTime5}}{{channelAlarmTime.alarmStartTime5}}{{/if}}"
                                   onFocus="WdatePicker({dateFmt:'HH:mm:ss',maxDate:alarmEndTime5.value})" >
                            -&nbsp;
                            <input class="Wdate"
                                   style="width:140px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="channelAlarmTime.alarmEndTime5" id="alarmEndTime5" value="{{#if channelAlarmTime.enableTime5}}{{channelAlarmTime.alarmEndTime5}}{{/if}}"
                                   onFocus="WdatePicker({dateFmt:'HH:mm:ss',minDate:alarmStartTime5.value})">
                        </div>
                    </div>
           </c:if>
        </form>
    </div>

    <div class="dialog-btns">
        <div id="add-channel-cancel-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
					<span class="btn-middle">
						<span class="btn-text">取消</span>
					</span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="add-channel-ok-btn" class="dialog-btn">
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
<script type="text/x-handlebars-template" id="editChannelsTemplate">
    <div class="dialog-caption">修改频道</div>
    <div class="dialog-content">
        <c:choose>
        <c:when test="${supportmosaic}">
        <form id="edit-channels-form" style="height: 200px; overflow-y: auto;">
            </c:when>
            <c:otherwise>
            <form id="edit-channels-form" style="height: 500px; overflow-y: auto;">
                </c:otherwise>
                </c:choose>
            <div class="basic">

                <div class="line">
                    <span style="width: 100px">频道标签：</span>
                    <div id="channel-tag-list">
                    <button type="button" id="add-tag-btn">添加标签</button>
                    </div>

                </div>
                <c:if test="${!supportmosaic}">
                <div class="line">

                    <span style="width: 100px;">录制模式：</span>
                    <input type="checkbox" name="disableRecord" id="disableRecord" checked="checked"/>
                    <span>不录制</span>

                    <input type="checkbox" name="enableRecord" id="enableRecord" value="1"/>
                    <span>循环录制</span>

                    <input type="checkbox" name="enableTriggerRecord" id="enableTriggerRecord" value="1" />
                    <span>告警触发录制</span>

                </div>
                </c:if>
            </div>
<c:if test="${!supportmosaic}">
            <%--<div class="mobile">
                <div class="section-switcher">
                    <input type="checkbox" name="isSupportMobile" id="isSupportMobile" value="1"/>
                    <span>支持移动设备</span>
                </div>

                <div class="line" style="display:none">
                    <span style="width:100px;">标清视频码率：</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[0].videoBitrate"
                           id="mobile-sd-videoBitrate" disabled/>
                    <span>kpbs</span>
                    <span style="width:85px;">音频码率：</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[0].audioBitrate"
                           id="mobile-sd-audioBitrate" disabled/>
                    <span>kpbs</span>
                    <span style="width:75px;">分辨率：</span>
                    <span>宽</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[0].width" id="mobile-sd-width" disabled/>
                    <span>高</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[0].height" id="mobile-sd-height" disabled/>
                    <input type="hidden" name="mobileConfigs[0].type" id="mobile-sd-type" value="0" disabled/>
                </div>
                <div class="line">
                    <span style="width:100px;">视频码率：</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[1].videoBitrate"
                           id="mobile-hd-videoBitrate" disabled/>
                    <span>kpbs</span>
                    <span style="width:85px;">音频码率：</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[1].audioBitrate"
                           id="mobile-hd-audioBitrate" disabled/>
                    <span>kpbs</span>
                    <span style="width:75px;">分辨率：</span>
                    <span>宽</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[1].width" id="mobile-hd-width" disabled/>
                    <span>高</span>
                    <input type="text" style="width:55px;" name="mobileConfigs[1].height" id="mobile-hd-height" disabled/>
                    <input type="hidden" name="mobileConfigs[1].type" id="mobile-hd-type" value="1" disabled/>
                </div>
                <div class="line">
                    <div style="margin-left: 40px">
                        <input type="checkbox" name="mobileConfigs[1].deinterlace" id="mobile-hd-deinterlace" value="1" disabled/>
                        <span>反交错</span>
                    </div>
                </div>
            </div>--%>

            <div class="signal-analysis">
                <div class="section-switcher">
                    <input type="checkbox" name="enableSignalDetectByType" id="enableSignalDetectByType" value="1"/>
                    <span>信源检测</span>
                </div>

                <div class="line">
                    <span style="width:200px;">信源异常持续告警间隔时间：</span>
                    <input type="text" style="width:55px;" id="notifyInterval" name="signalDetectByTypeConfig.notifyInterval" disabled/>
                    <span>秒</span>
                </div>
                <div class="line">
                    <input type="checkbox" style="margin-left:50px;" id="warningSignalBrokenEnabled" name="signalDetectByTypeConfig.enableWarningSignalBroken" value="1" disabled/>
                    <span style="width:100px;">信源中断：</span>
                    <input type="text" style="width:55px;" id="warningSignalBrokenTimeout" name="signalDetectByTypeConfig.warningSignalBrokenTimeout" disabled/>
                    <span>毫秒</span>

                    <input type="checkbox" style="margin-left:80px;" id="warningProgidLossEnabled" name="signalDetectByTypeConfig.enableWarningProgidLoss" value="1" disabled/>
                    <span style="width:110px;">Program ID丢失：</span>
                    <input type="text" style="width:55px;" id="warningProgidLossTimeout" name="signalDetectByTypeConfig.warningProgidLossTimeout" disabled/>
                    <span>毫秒</span>

                </div>
                <div class="line">

                    <input type="checkbox" style="margin-left:50px;" id="warningVideoLossEnabled" name="signalDetectByTypeConfig.enableWarningVideoLoss" value="1" disabled/>
                    <span style="width:100px;">视频流丢失：</span>
                    <input type="text" style="width:55px;" id="warningVideoLossTimeout" name="signalDetectByTypeConfig.warningVideoLossTimeout" disabled/>
                    <span>毫秒</span>

                    <input type="checkbox" style="margin-left:80px;" id="warningAudioLossEnabled" name="signalDetectByTypeConfig.enableWarningAudioLoss" value="1" disabled/>
                    <span style="width:110px;">音频流丢失：</span>
                    <input type="text" style="width:55px;" id="warningAudioLossTimeout" name="signalDetectByTypeConfig.warningAudioLossTimeout" disabled/>
                    <span>毫秒</span>

                </div>
                <div class="line">
                    <input type="checkbox" style="margin-left:50px;" id="warningCcErrorEnabled" name="signalDetectByTypeConfig.enableWarningCcError" value="1" disabled/>
                    <span style="width:100px;">连续计数器错误：</span>
                    <input type="text" style="width:55px;" id="warningCcErrorTimeout" name="signalDetectByTypeConfig.warningCcErrorTimeout" disabled/>
                    <span>毫秒内达到</span>
                    <input type="text" style="width:55px;" id="warningCcErrorCount" name="signalDetectByTypeConfig.warningCcErrorCount" disabled/>
                    <span>个</span>
                </div>
            </div>

            <div class="content-analysis">
                <div class="section-switcher">
                    <input type="checkbox" name="enableContentDetect" id="enableContentDetect" value="1"/>
                    <span>内容检测</span>
                </div>

                <div class="line">
                    <span style="width:100px;">黑场：</span>
                    <input type="text" style="width:55px;" id="blackSeconds" name="contentDetectConfig.blackSeconds" disabled/>
                    <span>秒</span>
                    <span style="width:100px;">静音：</span>
                    <input type="text" style="width:55px;" id="silenceSeconds" name="contentDetectConfig.silenceSeconds" disabled/>
                    <span>秒</span>
                    <span style="width:100px;">静音门限设置：</span>
                    <input type="text" style="width:55px;" id="silenceThreshold" name="contentDetectConfig.silenceThreshold" disabled/>
                    <span>db</span>
                </div>
                <div class="line">
                    <span style="width:100px;">静帧：</span>
                    <input type="text" style="width:55px;" id="noFrameSeconds" name="contentDetectConfig.noFrameSeconds" disabled/>
                    <span>秒</span>
                    <span style="width:100px;">音量过高：</span>
                    <input type="text" style="width:55px;" id="loudVolumeSeconds" name="contentDetectConfig.loudVolumeSeconds" disabled/>
                    <span>秒</span>
                    <span style="width:100px;">音量门限设置：</span>
                    <input type="text" style="width:55px;" id="loudVolumeThreshold" name="contentDetectConfig.loudVolumeThreshold" disabled/>
                    <span>db</span>

                </div>
                <div class="line">
                    <span style="width:100px;margin-left:198px;">音量过低：</span>
                    <input type="text" style="width:55px;" id="lowVolumeSeconds" name="contentDetectConfig.lowVolumeSeconds" disabled/>
                    <span>秒</span>
                    <span style="width:100px;">音量门限设置：</span>
                    <input type="text" style="width:55px;" id="lowVolumeThreshold" name="contentDetectConfig.lowVolumeThreshold" disabled/>
                    <span>db</span>
                </div>

            </div>
    </c:if>
        </form>
    </div>
    <div class="dialog-btns">
        <div id="edit-channels-cancel-btn" class="dialog-btn">
            <a>
                <span class="btn-left"></span>
                    <span class="btn-middle">
                        <span class="btn-text">取消</span>
                    </span>
                <span class="btn-right"></span>
            </a>
        </div>
        <div id="edit-channels-ok-btn" class="dialog-btn">
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
<script type="text/x-handlebars-template" id="loadingTemplate">
    <div class="dialog-caption">提示</div>
    <div class="dialog-content">
        <div style="height: 60px;">
            <div style="margin-left: 30%;padding-top: 20px;">
                <img src="<c:url value="/images/spinner.gif"/> ">
                <span style="position: absolute;margin-left: 5px;">频道保存中，请稍等...</span>
            </div>
        </div>
    </div>
</script>

<script type="text/x-handlebars-template" id="loadingTemplate2">
    <div class="dialog-caption">提示</div>
    <div class="dialog-content">
        <div style="height: 60px;">
            <div style="margin-left: 30%;padding-top: 20px;">
                <img src="<c:url value="/images/spinner.gif"/> ">
                <span style="position: absolute;margin-left: 5px;">操作处理中，请稍等...</span>
            </div>
        </div>
    </div>
</script>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="helper" uri="/WEB-INF/jsphelper/helper.tld" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="../common/common.jsp" %>
    <utils:css path="/css/loglist.css,/css/file/userManage.css,/js/plugins/mulselect/multiple-select.css"/>
    <utils:js path="/js/plugins/jquery.extend.js,/js/logList.js,/js/My97DatePicker/WdatePicker.js,
	/js/common/CommonDefine.js,/js/TMPlayerMngr.js,/js/Task.js,/js/plugins/mulselect/multiple-select.js"/>
    <script type="text/javascript">
        $(function () {
            $("select[name='types']").multipleSelect({
                selectAllText: "全选",
                allSelected: "全选"
            });

            $("select[name='groups']").multipleSelect({
                selectAllText: "全选",
                allSelected: "全选"
            });

            /* $("select[name='channelList']").multipleSelect({
             selectAllText: "全选",
             allSelected: "全选"
             });*/

            var cdView = new sv.ContentDetectLogView();
            cdView.render();
            $.pagination({"form": "search-form"});
            var TMPlayerMngrobj = new TMPlayerMngr();
            TMPlayerMngrobj.initRecrodPlayback();
        });
    </script>
</head>
<body onload="OnPageReady()">
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div id="packageadd" class="modal" style="display:none;width: 500px">
        <div class="dialog-caption" style="text-align: left;font-weight: bold;">告警设置
        </div>
        <div class="Package">
            <div id="dialog-install">
            </div>
        </div>
    </div>

    <div class="container">
        <div class="logType">
            <div id="logtab" class="tabbable" style=" background-color:#244d75; height:43px;">
                <ul>
                    <c:if test="${supportmosaic==false}">
                        <li id="logtab-tb1" class="active"><a data-toggle="tab"
                                                              href="<c:url value="/log/cd"/>">内容报警日志</a>
                        </li>
                    </c:if>
                    <li id="logtab-tb2"><a data-toggle="tab" href="<c:url value="/log/s"/>">服务器日志</a></li>
                    <li id="logtab-tb3"><a data-toggle="tab" href="<c:url value="/log/op"/>">操作日志</a></li>
                    <c:if test="${supportmosaic==false}">
                        <li id="logtab-tb1"><a data-toggle="tab" href="<c:url value="/log/chart"/>">内容报警报表</a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
        <div class="log-list content-wrapper">
            <div class="searchbox">
                <form id="search-form" name="search-form" method="post" action="<c:url value="/log/cd"/>">
                    <input type="hidden" name="export">
                    <input type="hidden" name="delete">
                        <span style="margin-left:5px;">查找条件：
                            <input class="Wdate"
                                   style=" width:150px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="startTime" value="${q.startTimeAsString}"
                                   onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:endTime.value})" >
                            &nbsp; 至 &nbsp;
                            <input class="Wdate"
                                   style="width:150px;height:26px;box-shadow: 0px 1px 4px 0px rgba(168, 168, 168, 0.6) inset;-moz-border-radius:3px;border-radius:3px;"
                                   type="text" name="endTime" value="${q.endTimeAsString}"
                                   onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:startTime.value})">

                        </span>
                        <span style="margin-left:5px;">报警类型：
                            <select name="types" multiple="multiple" class="status_sel">
                                <option value="0" <c:if test="${helper:containsInteger(q.types,0)}">selected</c:if>>黑场
                                </option>
                                <option value="32" <c:if test="${helper:containsInteger(q.types,32)}">selected</c:if>>
                                    静音
                                </option>
                                <option value="33" <c:if test="${helper:containsInteger(q.types,33)}">selected</c:if>>
                                    低音
                                </option>
                                <option value="34" <c:if test="${helper:containsInteger(q.types,34)}">selected</c:if>>
                                    高音
                                </option>
                                <option value="2" <c:if test="${helper:containsInteger(q.types,2)}">selected</c:if>>静帧
                                </option>
                                <option value="27" <c:if test="${helper:containsInteger(q.types,27)}">selected</c:if>>
                                    CC错误
                                </option>
                                <option value="28" <c:if test="${helper:containsInteger(q.types,28)}">selected</c:if>>
                                    Audio丢失
                                </option>
                                <option value="29" <c:if test="${helper:containsInteger(q.types,29)}">selected</c:if>>
                                    Video丢失
                                </option>
                                <option value="30" <c:if test="${helper:containsInteger(q.types,30)}">selected</c:if>>
                                    信源中断
                                </option>

                            </select>
                        </span>
                        <span style="margin-left: 5px;">频道组：
                            <select name="groups" multiple="multiple" class="status_sel">
                                <option value="-1" <c:if test="${helper:containsInteger(q.groups,-1)}">selected</c:if>>
                                    未分组
                                </option>
                                <c:forEach items="${groups}" var="group" varStatus="status">
                                    <option value="${group.id}"
                                            <c:if test="${helper:containsInteger(q.groups,group.id)}">selected</c:if>>${group.name}</option>
                                </c:forEach>
                            </select>
                        </span>

                    <%--<span style="margin-left: 5px;">频道：
                        <select name="channelList" multiple="multiple" class="status_sel">
                            <option value="-1" <c:if test="${helper:containsInteger(channelList,-1)}">selected</c:if>>未分组</option>
                            <c:forEach items="${channelList}" var="channel" varStatus="status">
                                <option value="${channel.id}" <c:if test="${helper:containsInteger(channelList,channel.id)}">selected</c:if>>${channel.name}</option>
                            </c:forEach>
                        </select>
                    </span>--%>

                    <input class="keyword" type="text" id="channelName" name="channelName" placeholder="频道名称搜索"
                           autocomplete="off" value="${q.channelName}" style="margin-left:5px;"/>

                    <div id="search-btn" class="dialog-btn" style="position:relative; top:10px;margin-left:5px;">
                        <a>
                            <span class="btn-left"></span>
                                <span class="btn-middle">
                                    <span class="btn-text">搜索</span>
                                </span>
                            <span class="btn-right"></span>
                        </a>
                    </div>
                    <div id="delete-filtered-btn" class="dialog-btn"
                         style="position:relative; top:10px;margin-left:5px;">
                        <a>
                            <span class="btn-left"></span>
                        <span class="btn-middle">
                            <span class="btn-text">删除</span>
                        </span>
                            <span class="btn-right"></span>
                        </a>
                    </div>

                </form>
            </div>
            <div>
                <div class="action-bar disable">
                    <div class="action-bar-item export" id="export-btn">导出</div>
                    <div class="action-bar-item delete" id="delete-btn">删除</div>
                    <div id="sss" class="action-bar-item setting"
                         style="float: right;margin-right: 30px;cursor: pointer">设置
                    </div>
                </div>
                <table style="table-layout:fixed;word-wrap:break-word;word-break:break-all" class="log-list">
                    <thead>
                    <tr>
                        <th width="5%"><input type="checkbox" class="select-all"/></th>
                        <th width="8%">任务编号</th>
                        <th width="10%;">频道名称</th>
                        <th width="10%;">报警类型</th>
                        <th width="17%;">开始时间</th>
                        <th width="17%;">结束时间</th>
                        <th width="8%;">告警记录</th>
                        <th width="8%;">导出视频</th>
                        <th width="17%;">确认时间</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${pager.getContent()}" var="contentdetectlog" varStatus="status">
                        <tr class="tasks">
                            <td>
                                <input type="checkbox" class="select-one" id="select-one"
                                       name="contentDetectLog-id" value="${contentdetectlog.id}"/>
                            </td>
                            <td>${contentdetectlog.taskId}</td>
                            <td style="WORD-WRAP: break-word"
                                width="150px;">${contentdetectlog.channelName}</td>
                            <td>
                                    <span>
                                        <c:if test="${contentdetectlog.type==0}">黑场</c:if>
                                        <c:if test="${contentdetectlog.type==31}">流中断</c:if>
                                        <c:if test="${contentdetectlog.type==32}">
                                            <c:if test="${contentdetectlog.soundTrack==0}">静音-左声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==1}">静音-右声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==2}">静音-中置声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==3}">静音-低音声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==4}">静音-左后置声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==5}">静音-右后置声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==6}">静音-左环绕声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==7}">静音-右环绕声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==255}">静音</c:if>
                                        </c:if>
                                        <c:if test="${contentdetectlog.type==33}">低音</c:if>
                                        <c:if test="${contentdetectlog.type==34}">高音</c:if>
                                        <c:if test="${contentdetectlog.type==37}">
                                            <c:if test="${contentdetectlog.soundTrack==0}">爆音-左声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==1}">爆音-右声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==2}">爆音-中置声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==3}">爆音-低音声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==4}">爆音-左后置声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==5}">爆音-右后置声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==6}">爆音-左环绕声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==7}">爆音-右环绕声道</c:if>
                                            <c:if test="${contentdetectlog.soundTrack==255}">爆音</c:if>
                                        </c:if>
                                        <c:if test="${contentdetectlog.type==2}">静帧</c:if>
                                        <c:if test="${contentdetectlog.type==1}">绿场</c:if>
                                        <c:if test="${contentdetectlog.type==27}">CC错误</c:if>
                                        <c:if test="${contentdetectlog.type==28}">Audio丢失</c:if>
                                        <c:if test="${contentdetectlog.type==29}">Video丢失</c:if>
                                        <c:if test="${contentdetectlog.type==30}">信源中断</c:if>
                                    </span>
                            </td>
                            <td><fmt:setLocale value="zh_cn"/>
                                <fmt:formatDate value="${contentdetectlog.startTimeAsDate}" type="both"
                                                pattern="yyyy-MM-dd HH:mm:ss.SSS"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${contentdetectlog.endTime!=0}">
                                        <fmt:setLocale value="zh_cn"/>
                                        <fmt:formatDate value="${contentdetectlog.endTimeAsDate}" type="both"
                                                        pattern="yyyy-MM-dd HH:mm:ss.SSS"/>
                                    </c:when>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${contentdetectlog.type != 31}">
                                    <c:if test="${not empty contentdetectlog.videoFilePath}">
                                        <div class="play-btn ProgramPreviewTrigger">
                                            <a>
                                                <span class="playbtn"></span>
                                            </a>
                                        </div>
                                    </c:if>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${not empty contentdetectlog.videoFilePath}">
                                    <div class="export-btn" data-url="${contentdetectlog.videoFilePath}">
                                        <a>
                                            <span>导出</span>
                                        </a>
                                    </div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${not empty contentdetectlog.confirmdate}">
                                    <fmt:formatDate value="${contentdetectlog.confirmdate}"
                                                    pattern="yyyy-MM-dd HH:mm:ss"/>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <jsp:include page="/WEB-INF/views/common/pager.jsp"/>

        <script type="text/x-handlebars-template" id="addPackageTemplate">
            <form id="add-package-form">
                <table width="60%" align="center" class="tblcol2">
                    <tbody>


                    <tr style="height:28px">
                        <td>指定保存告警历史记录的天数(0表示不自动删除)</td>
                    </tr>
                    <tr style="height:20px">
                        <td>
                            <span style="font-size: 15px" >保留时间:</span>
                            <input type="text" id="deleteBeforeDays" name="deleteBeforeDays" style="width:30px">天

                        </td>
                    </tr>

                    </tbody>
                </table>

                <div class="dialog-btns-center">
                    <div id="add-package-ok-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span
                            class="btn-middle"> <span
                            class="btn-text">确定</span> </span> <span class="btn-right"></span> </a></div>

                    <div id="add-package-cancel-btn" class="dialog-btn"><a> <span class="btn-left"></span> <span
                            class="btn-middle"> <span class="btn-text">取消</span> </span> <span class="btn-right"></span>
                    </a></div>
                </div>
            </form>

        </script>

        <div>
            <div id="DialogFrameTemplate" class="modal" style="width: 520px;display: none;">
                <div class="TMPlayer">
                    <div class="dialog-caption" style="text-align: left;font-weight: bold;">播放器
                    </div>
                    <div class="dialog-content">
                        <div id="ProgramPreviewTmpl" style="display: block">
                            <table id="PreviewDlgBody">
                                <tr>
                                    <td style="width: 520px;height:390px;">
                                        <div class="TMPlayerContainer" style="width: 100%; height:100%">
                                            <object classid="clsid:b14dcdc6-dc3a-4e99-80b2-3169b06ef069"
                                                    codebase="<c:url value="/tmplayer/TMPlayer.CAB#Version=2,0,0,72"/>"
                                                    id="ArcSoft_TMPlayer"
                                                    width="520" height="390" viewastext
                                                    standby="Loading ArcSoft TotalMedia Player ...">
                                                <param name="ApplicationType" value="0"/>
                                                <param name="PanelType" value="3"/>
                                                <param name="ResizeMode" value="7"/>
                                                <br/><br/>

                                                <p style="font-size:12px">
                                                    当前网页需要安装媒体播放器“ ArcSoft TotalMedia Player”。<br/>
                                                    如果你没有看到提示, 请确认系统和浏览器的安全权限。<br/>
                                                </p>
                                                <a href="<c:url value="/tmplayer/TMPSetup.exe"/> ">
                                                    <b style="color:green;font-size:13px">或者点击此处安装“ ArcSoft TotalMedia
                                                        Player”。</b><br/>
                                                    <b style="color:green;font-size:13px">请保存安装文件至本地后,使用管理员权限安装。</b>
                                                </a><br/>
                                                <a href="#" onclick="location.reload()">
                                                    <b style="font-size:13px">安装完成后，点此刷新本页面。</b>
                                                </a><br/>
                                            </object>
                                        </div>
                                        <!-- <div class="LineSpacing"></div> -->
                                    </td>
                                    <td>
                                        <div id="tmplayer-close" class="ButtonTrigger"
                                             style="position: fixed; left: 50%; margin-top: -220px; margin-left: 210px; z-index: 2;">
                                            <a href="#" rel="modal:close"><span
                                                    style="color: white;font-size: inherit;">关闭</span></a></div>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>

package com.arcsoft.supervisor.web;

import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.service.log.SystemLogService;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A filter implementation to do security validate.
 *
 * @author zw.
 */
public class OperationLogFilter implements Filter {

    private SystemLogService systemLogService;
    private String strHost;
    private String strDevice = "/device";
    private String stringHost="/host";
    private String strScreen = "/screen";
    private String strChannel = "/channel";
    private String strUser = "/user";
    private String strMsg = "/msg";
    private String strLog = "/log";
    private String strLogin = "/login";
    private String strTask = "/task";
    private String strPackage="/package";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        strHost = filterConfig.getServletContext().getContextPath();
        systemLogService = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext()).getBean(SystemLogService.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//      HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String uri = httpServletRequest.getRequestURI();
        Date datetime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strdatetime = sdf.format(datetime);
        User user = (User) httpServletRequest.getSession().getAttribute(SupervisorDefs.Constants.LOGIN_USER_INFO);
        String strUserName = (user == null) ? "Unlogin" : user.getUserName();

        int funcType = 0;
        String operationInfo = "";

        uri = uri.substring(strHost.length());
        if (uri.startsWith(strDevice)) {
            funcType = 1;
            operationInfo = getDeviveMngrOperationInfo(uri);
        } else if (uri.startsWith(stringHost)) {
            funcType = 1;
            operationInfo = getHostInfo(uri);
            //operationInfo ="集群系统初始化保存";
        }else if (uri.startsWith(strScreen)) {
            funcType = 2;
            operationInfo = getScreenMngrOperationInfo(uri);
        } else if (uri.startsWith(strChannel)) {
            funcType = 3;
            operationInfo = getChannelMngrOperationInfo(uri);
        } else if (uri.startsWith(strUser)) {
            funcType = 4;
            operationInfo = getUserMngrOperationInfo(uri);
        } else if (uri.startsWith(strLogin)) {
            funcType = 4;
            operationInfo = getLoginMngrOperationInfo(uri);
        } else if (uri.startsWith(strMsg)) {
            funcType = 5;
            operationInfo = getMessageMngrOperationInfo(uri);
        } else if (uri.startsWith(strLog)) {
            funcType = 6;
            operationInfo = getLogMngrOperationInfo(uri);
        } else if (uri.startsWith(strTask)) {
            funcType = 2;
            operationInfo = getTaskOperationInfo(uri);
        }else if (uri.startsWith(strPackage)) {
            funcType = 9;
            operationInfo = getPackage(uri);
        }else if (uri.startsWith("/addops_app")) {
            funcType = 9;
            operationInfo ="注册ops";
        }

        if (!operationInfo.isEmpty())
            systemLogService.add(strdatetime, strUserName, funcType, operationInfo, "成功");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
    private  String getHostInfo(String uri){
        String output = "";
        String op = uri.substring(stringHost.length());
        if (op.startsWith("/init"))
            output = "集群系统初始化保存";
        else if (op.startsWith("/restart"))
            output = "集群重启";
        else if (op.startsWith("/shutdown"))
            output = "集群关机";
       return output;
    }

    private String getPackage(String uri){
        String output = "";
        String op = uri.substring(strPackage.length());
        if (op.startsWith("/deleteOps"))
            output = "删除OPS";
        else if (op.startsWith("/uploadPackage"))
            output = "上传安装包";
        else if (op.startsWith("/deletePackage"))
            output = "删除安装包";
        else
            output = "";

        return output;

    }

    private String getDeviveMngrOperationInfo(String uri) {
        String output = "";
        String op = uri.substring(strDevice.length());
        if (op.startsWith("/index"))
            output = "";//"查看设备管理页面";
        else if (op.startsWith("/get"))
            output = "";//"获取设备信息";
        else if (op.startsWith("/check"))
            output = "";//"检验设备信息";
        else if (op.startsWith("/join"))
            output = "添加设备";
        else if (op.startsWith("/update"))
            output = "更新设备信息";
        else if (op.startsWith("/delete"))
            output = "删除设备";
        else if (op.startsWith("/scan"))
            output = "";//"查找设备";
        else if (op.startsWith("/getStatus"))
            output = "";
        else if (op.startsWith("/gets"))
            output = "";//"获取设备";
        else if (op.startsWith("/al"))
            output = "";
        else if (op.startsWith("/init"))
            output = "系统初始化保存";
        else if (op.startsWith("/tasks"))
            output = "";
        else
            output = op;
        return output;
    }

    private String getScreenMngrOperationInfo(String uri) {
        String output = "";
        String op = uri.substring(strScreen.length());
        if (op.startsWith("/index"))
            output = "";
        else if (op.startsWith("/screen"))
            output = "";//"显示屏幕";
        else if (op.startsWith("/updateSchema"))
            output = "更新画面布局";
        else if (op.startsWith("/updateWallPositionOps"))
            output = "更新OPS墙位置";
        else if (op.startsWith("/screenPosition"))
            output = "";//"获取屏幕位置";
        else if (op.startsWith("/updateScreenPosition"))
            output = "更新屏幕位置";
        else if (op.startsWith("/activeSchema"))
            output = "激活画面布局";
        else if (op.startsWith("/recognize"))
            output = "";//"识别屏幕";
        else if (op.startsWith("/channels"))
            output = "";
        else if (op.startsWith("/updateWall"))
            output = "更新墙信息";
        else if (op.startsWith("/addWall"))
            output = "增加屏幕墙";
        else if (op.startsWith("/opsServers"))
            output = "";
        else if (op.startsWith("/walls"))
            output = "";//"查看画面管理页面";
        else if (op.startsWith("/saveWall"))
            output = "保存当前屏幕墙";
        else if (op.startsWith("/removeWall"))
            output = "移除屏幕墙";
        else if (op.startsWith("/sdiOutputs"))
            output = "";
        /*else if (op.startsWith("/templates"))
            output = "获取画面模板位置";*/
        else if (op.startsWith("/updateTaskProfile"))
            output = "更新画面任务模板";
        /*else if (op.startsWith("/activeChannels"))
            output = "获取所有活动频道";*/
        else if (op.startsWith("/allScreenByWallId"))
            output = "";
        else if (op.startsWith("/allScreen"))
            output = "";
        else if (op.startsWith("/control"))
            output = "";
        else if (op.startsWith("/updateOutput"))
            output = "更新画面输出地址";
        else if (op.startsWith("/getScreenName"))
            output = "";
        else if (op.startsWith("/bindOps"))
            output = "绑定OPS";
        else if (op.startsWith("/unbindOps "))
            output = "解绑OPS";
        else
            output = "";

        return output;
    }

    private String getChannelMngrOperationInfo(String uri) {
        String output = "";
        String op = uri.substring(strChannel.length());
        if (op.startsWith("/index"))
            output = "";
        else if (op.startsWith("/groups"))
            output = "";//"查看频道管理页面";
        else if (op.startsWith("/channels"))
            output = "";
        else if (op.startsWith("/frame"))
            output = "";
        else if (op.startsWith("/unGroupedChannels"))
            output = "";//"列举未分组频道";
        else if (op.startsWith("/removeGroup"))
            output = "移除分组信息";
        else if (op.startsWith("/saveGroup"))
            output = "保存分组信息";
        else if (op.startsWith("/save"))
            output = "保存频道信息";
        else if (op.startsWith("/get"))
            output = "";//"加载频道信息";
        else if (op.startsWith("/delete"))
            output = "删除频道";
        else if (op.startsWith("/move"))
            output = "移动频道";
        else if (op.startsWith("/mediainfo"))
            output = "";
        else if (op.startsWith("list_app"))    //rtsp channellist_app
            output = "RTSP请求 channellist_app";
        else if (op.startsWith("_app"))    //rtsp channellist_app
            output = "RTSP请求 channel_app";
        else if (op.startsWith("out_app"))    //rtsp channellist_app
            output = "RTSP请求 channelout_app";
        else if (op.startsWith("/channelInfo"))
            output = "";
        else if (op.startsWith("/start"))
            output = "频道任务启动";
        else if (op.startsWith("/stop"))
            output = "频道任务停止";
        else
            output = "";

        return output;
    }

    private String getUserMngrOperationInfo(String uri) {
        String output = "";
        String op = uri.substring(strUser.length());
        if (op.startsWith("/index"))
            output = "";
        else if (op.startsWith("/users"))
            output = "";//"查看用户管理页面";
        else if (op.startsWith("/addUser"))
            output = "新增用户";
        else if (op.startsWith("/delete"))
            output = "删除用户";
        else if (op.startsWith("/editUserPswd"))
            output = "修改用户密码";
        else if (op.startsWith("/editUserRole"))
            output = "修改用户权限";
        else if (op.startsWith("/resetUserPswd"))
            output = "重置密码";
        else if (op.startsWith("/chgPswd"))
            output = "";
        else
            output = "";
        return output;
    }

    private String getLoginMngrOperationInfo(String uri) {
        String output = "";
        String op = uri.substring(strLogin.length());
        if (op.startsWith("/index"))
            output = "";
        else if (op.startsWith("/sign_in"))
            output = "";//"用户登录";
        else if (op.startsWith("/out"))
            output = "用户登出";
        else if (op.startsWith("/register"))
            output = "新用户注册";
        else
            output = op;

        return output;
    }

    private String getMessageMngrOperationInfo(String uri) {
        String output = "";
        String op = uri.substring(strMsg.length());
        if (op.startsWith("/index"))
            output = "";//"查看消息发送日志";
        else if (op.startsWith("/postMessage"))
            output = "发送消息";
        else if (op.startsWith("/delete"))
            output = "删除消息";
        else if (op.startsWith("/clear"))
            output = "清除消息";
        else
            output = op;

        return output;
    }

    private String getLogMngrOperationInfo(String uri) {
        String output = "";
        String op = uri.substring(strLog.length());
        if (op.startsWith("/cd"))
            output = "";//"查看内容报警日志";
        else if (op.startsWith("/s"))
            output = "";//"查看服务器日志";
        else if (op.startsWith("/op"))
            output = "";
        else if (op.startsWith("/deleteContentDetectLogs"))
            output = "删除内容检测日志";
        else if (op.startsWith("/deleteServiceLogs"))
            output = "删除服务器日志";
        else if (op.startsWith("/deleteSystemLogs"))
            output = "删除操作日志";
        else if (op.startsWith("/searchContentDetectLogs"))
            output = "";//"查询内容检测日志";
        else if (op.startsWith("/searchServiceLogs"))
            output = "";//"查询服务器日志";
        else if (op.startsWith("/searchSystemLogs"))
            output = "";
        else if (op.startsWith("/getRecordInfo"))
            output = "";
        else if (op.startsWith("/chart"))
            output = "";
        else if (op.startsWith("/baiduchart"))
            output = "";
        else
            output = "";

        return output;
    }

    private String getTaskOperationInfo(String uri) {
        String output = "";
        String op = uri.substring(strTask.length());
        String cmdStart = "/screen/start";
        String cmdStop = "/screen/stop";
        if (op.startsWith(cmdStart))
            output = "播放任务启动";
        else if (op.startsWith(cmdStop))
            output = "播放任务停止";
        //else
        //	output = op;

        return output;
    }
}

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="/WEB-INF/tags/profiles.tld" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="../common/common.jsp" %>
    <utils:css path="/css/warning.css"/>
    <utils:js path="/js/warning.js"/>
    <script type="text/javascript">
        $(function () {
            var pushView = new sv.EmailView();
            pushView.render();
        });
    </script>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#selectAlltypes").click(function () {
                var selectOne = document.getElementsByClassName("mycheck");
                for (var i = 0; i < selectOne.length; i++) {
                    selectOne[i].checked = this.checked;

                }
            })

            $("#btnSendTestMail").click(function () {
                $.ajax({
                    url: "/warning/sendTestMail",
                    type: "POST",
                    dataType: "json",
                    data: $("#voice-form").serialize(),
                    success: function (data) {
                        if (data.code != 0) {
                            alert("邮件发送失败，请检查配置参数！")
                        }

                    }
                });
            })
        });
    </script>
</head>
<body>
<div class="maindiv">
    <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    <div class="container">

        <%--  <c:set var="navIndex" value="1"/>
          <jsp:include page="nav-config.jsp">
              <jsp:param name="activeIndex" value="${navIndex}"/>
          </jsp:include>--%>

        <div class="secondary-nav">
            <ul>
                <li>
                    <a data-toggle="tab" href="/warning/report">告警上报</a>
                </li>
                <li class="active">
                    <a data-toggle="tab" href="/warning/email">邮件告警</a>
                </li>
                <p:profiles hasProfiles="${_profile_sms}">
                    <li>
                        <a data-toggle="tab" href="/warning/sms">短信告警</a>
                    </li>
                </p:profiles>
                <p:profiles hasProfiles="${_profile_voice}">
                    <li>
                        <a data-toggle="tab" href="/warning/push">声音报警</a>
                    </li>
                </p:profiles>
            </ul>
        </div>

        <div id="voice-content" class="warning-content">
            <form id="voice-form" class="form">
                <table class="tblcol2">
                    <tbody>
                    <tr style="height:30px">
                        <td class="warning-table-tile">SMTP主机:</td>
                        <td style="padding-right: 0px;"><input type="text" id="host" value="${cfg.mailServerHost}"
                                                               required="required" name="mailServerHost"
                                                               style="min-width: 200px;"></td>
                    </tr>
                    <tr style="height:30px">
                        <td class="warning-table-tile">SMTP端口:</td>
                        <td style="padding-right: 0px;"><input type="text" id="port" value="${cfg.mailServerPort}"
                                                               required="required" name="mailServerPort">
                            <span id="error_smtpPort" style="height:28px;color:red"></span>
                            <span>
                                    <input type="checkbox" <c:choose> <c:when test="${cfg.choosessl}"> checked="checked" </c:when> </c:choose>
                                           id="choosessl" name="choosessl" style="vertical-align: middle">

                            启用SSL
                            </span>

                        </td>


                    </tr>

                    <tr style="height:30px">
                        <td class="warning-table-tile">发件人:</td>
                        <td style="padding-right: 0px;"><input type="text" id="userName" value="${cfg.userName}"
                                                               name="userName" style="width: 200px;" tip-gravity="w">
                        </td>
                    </tr>
                    <tr style="height:30px">
                        <td class="warning-table-tile">密码:</td>
                        <td style="padding-right: 0px;"><input type="password" value="${cfg.password}" id="password"
                                                               required="required" name="password"
                                                               style="width: 200px;"><span style="color: red">(163邮箱应在设置中心开启客户端授权码，填写此密码)</span>
                        </td>
                    </tr>
                    <tr style="height:30px">
                        <td class="warning-table-tile">收件人:</td>
                        <td style="padding-right: 0px;"><input type="text" id="toAddress" value="${cfg.toAddress}"
                                                               required="required" placeholder="xxx@xx.com"
                                                               name="toAddress" style="min-width: 300px;">
                            <input type="button" id="btnSendTestMail" value="测试发送">
                            <br/>(多个收件人之间用分号隔开)
                        </td>

                    </tr>
                    </tbody>
                </table>

                <div class="selectItem" style="display: block;">
                    <ul>
                        <li class="ms-select-all"><label class="TypeClass"><input type="checkbox" id="selectAlltypes" name="selectAlltypes" style="vertical-align: middle"> [全选]</label>
                        </li>
                        <li class="selected"><label class="TypeClass">
                            <input type="checkbox" name="typeBlack" class="mycheck"
                            <c:if test="${cfg.typeBlack == 0}"> checked="checked"   </c:if> value="0">黑场</label>
                        </li>
                        <li class="selected"><label class="TypeClass">
                            <input type="checkbox" name="typeMute" class="mycheck"
                            <c:if test="${cfg.typeMute == 32}"> checked="checked"   </c:if> value="32">静音</label>
                        </li>
                        <li class="selected"><label class="TypeClass">
                            <input type="checkbox" name="typeBass" class="mycheck"
                            <c:if test="${cfg.typeBass == 33}"> checked="checked"   </c:if> value="33">低音</label>
                        </li>
                        <li class="selected"><label class="TypeClass">
                            <input type="checkbox" name="typePitch" class="mycheck"
                            <c:if test="${cfg.typePitch == 34}"> checked="checked"   </c:if> value="34">高音</label>
                        </li>
                        <li class="selected"><label class="TypeClass">
                            <input type="checkbox" name="typeStatic" class="mycheck"
                            <c:if test="${cfg.typeStatic == 2}"> checked="checked"   </c:if> value="2">静帧</label>
                        </li>
                        <li class="selected"><label class="TypeClass">
                            <input type="checkbox" name="typeCc" class="mycheck"
                            <c:if test="${cfg.typeCc == 27}"> checked="checked"   </c:if> value="27">CC错误</label>
                        </li>
                        <li class="selected"><label class="TypeClass">
                            <input type="checkbox" name="typeAudio" class="mycheck"
                            <c:if test="${cfg.typeAudio == 28}"> checked="checked"   </c:if> value="28">Audio丢失</label>
                        </li>
                        <li class="selected"><label class="TypeClass">
                            <input type="checkbox" name="typeVideo" class="mycheck"
                            <c:if test="${cfg.typeVideo == 29}"> checked="checked"   </c:if> value="29">Video丢失</label>
                        </li>
                        <li class="selected"><label class="TypeClass">
                            <input type="checkbox" name="typeSignal" class="mycheck"
                            <c:if test="${cfg.typeSignal == 30}"> checked="checked"   </c:if> value="30">信源中断</label>
                        </li>
                    </ul>
                </div>

                <div class="text-ct">
                    <div id="save-btn" class="dialog-btn">
                        <a>
                            <span class="btn-left"></span>
                                <span class="btn-middle">
                                    <span class="btn-text">保存</span>
                                </span>
                            <span class="btn-right"></span>
                        </a>
                    </div>
                </div>
            </form>
        </div>
        <div class="push"></div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<div id="loading-dialog" class="modal" style="display:none;width: 350px;"></div>
<script type="text/x-handlebars-template" id="opLoadingTemplate">
    <div class="dialog-caption">&nbsp;</div>
    <div class="dialog-content">
        <div style="height: 60px;">
            <div style="margin-left: 30%;padding-top: 20px;">
                <img src="<c:url value="/images/spinner.gif"/> ">
                <span style="position: absolute;margin-left: 5px;">{{message}}</span>
            </div>
        </div>
    </div>
</script>

</body>
</html>

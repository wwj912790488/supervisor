<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="utils" uri="/WEB-INF/tags/utils.tld" %>
<!DOCTYPE html>
<html>
<head>
    <title>Supervisor</title>
	<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.ico">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <utils:css path="/css/common/common.css,/css/login.css,
    /js/plugins/tipsy/tipsy.css"/>
    <utils:js path="/js/jquery-1.11.1.min.js,
    /js/plugins/validate/jquery.validate.min.js,
    /js/plugins/validate/messages_zh.min.js,
    /js/plugins/tipsy/jquery.tipsy.js,
    /js/plugins/json/jquery.json.min.js,
    /js/plugins/jquery.md5.js,
    /js/plugins/backbone/underscore.js,
    /js/plugins/backbone/backbone.js,
    /js/plugins/backbone/backbone.marionette.js,
    /js/common/base.js,
    /js/login.js"/>
	<script type="text/javascript">
		$(function(){
			sv.urlPath.setContextPath('<c:url value="/"/>');
		});
	</script>
</head>
<body>
<div class="maindiv">
    <div class="container" onload="frm.username.focus()">
    	<div id="login-dialog">
			<div class="dialog-caption">登录</div>
			<form id="loginfrm" name="loginfrm" method="post" action="sign_in">
				<div class="line" style="text-align:center;margin:20px 0px;">
					<span style="width:50px;"> 用户名: </span> 
					<input style="width:150px;" class="textbox" type="text" name="userName" id="userName" value="" required>
				</div>
				<div class="line" style="text-align:center;margin:20px 0px;">
					<span style="width:50px;">密码: </span> 
					<input style="width:150px;" class="textbox" type="password" name="pwd" id="pwd" value="" required>
					<input type="hidden" name="password" id="password" value="">
				</div>
			</form>
			<div class="dialog-btns">
		        <div id="login-btn" class="dialog-btn">
		            <a>
		                <span class="btn-left"></span>
							<span class="btn-middle">
								<span class="btn-text">登录</span>
							</span>
		                <span class="btn-right"></span>
		            </a>
		        </div>
    		</div>
		</div>
    </div>
</div>
</body>
</html>

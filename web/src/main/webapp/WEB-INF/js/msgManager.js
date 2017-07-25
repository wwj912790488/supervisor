function messageManager() {

}

messageManager.prototype = {
	init: function() {
		var _this = this;
        sv.nav.active('tb3');

        $(".select-all").click(function () {
            $(".select-one").prop("checked", this.checked);
            _this.updateActionBar();
        });
        
        $("#message-list-table").on('click', ".select-one", function () {
            _this.updateActionBar();
        });
        
        $("#post-msg-btn").click(function() {
        	//_this.postMessage();
            $("#postMsg").click();
        });

        $("#clear-msg-btn").click(function(){
            _this.clearMessage();
        });
        
        $("#message-list-table").on('click', ".postAgain", function () {
			var data = $(this).children($("#message")).val();
			$("#message").val(data);
			//_this.postMessage();
            $("#postMsg").click();
        });

        $("#delete-msg-btn").click(function () {
            if(!$(".action-bar").hasClass("disable")) {
                sv.prompt.showConfirm2('是否删除所选记录？', function () {
                    _this.deleteMessage();
                });
            }
        });
        
/*        $("#message").on("keydown", function(event) {
    		if(event.which == 13) {
    			_this.postMessage();
    		}
    	});
*/
        
        this.updateActionBar();
	},
	updateActionBar: function () {
		$(".select-all").prop("checked",
                    $(".select-one[type='checkbox']:checked").length > 0 && $(".select-one[type='checkbox']:checked").length === $(".select-one[type='checkbox']").length);
        if ($(".select-one[type='checkbox']:checked").length > 0) {
            $(".action-bar").removeClass("disable");
        } else {
            $(".action-bar").addClass("disable");
        }
    },
    
/*    postMessage: function () {
    	var postMessageForm = $("#post-msg-form");
		var validator = postMessageForm.validate();
		if(postMessageForm.valid()) {
			var mydate = new Date();
			var dateTime = mydate.toLocaleString();
			$("#dateTime").val(dateTime);
	        var data = postMessageForm.serializeArray();
	        
	        $.post("postMessage", data, $.proxy(function () {
	        	location.reload();
	        	this.updateActionBar();
	        	$("#message").val("");
	        }, this)); 
		}      
    }, 
*/    
    deleteMessage: function () {
        var $messageObjs = $("input[name='message-id']:checked");
        if ($messageObjs.length > 0) {
            var params = [];
            for (var i = 0, len = $messageObjs.length; i < len; i++) {
                var message = {};
                message.id = $($messageObjs.get(i)).val();
                params[i] = message;
            }
            $.post("delete", {ids: $.toJSON(params)}, $.proxy(function () {
            	location.href = "index"
            }, this));
        }
    },
    clearMessage : function(){
        $("#loading-dialog").render("loadingTemplate").modal({
            showClose : false,
            clickClose : false
        });
        $.post("clear", function(json){
            sv.prompt.succeed2("消息清除" + (json.code == 0 ? "成功" : "失败"));
            $.modal.close();
        });
    }
};


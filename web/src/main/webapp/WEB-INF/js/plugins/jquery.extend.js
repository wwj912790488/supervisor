(function($){
	$.fn.extend({
		/**
		 * add effect like css hover
		 * @param color - string. color value
		 */
		addHover : function(color){
			this.off("mouseenter mouseleave").hover(function(){
				$(this).css("background-color", color);
			},function(){
				$(this).css("background-color", "");
			});
		}
	});
	$.extend({
		/**
		 * pagination
		 * 
		 * @param params - json object
		 * 		  common params:
		 * 				type - 0 or 1, default is 1. 0 will use normal request submit(post), 1 will use ajax submit.
		 * 				data - all of data of submit to server
		 * 		  use in ajax:
		 * 				success - callback function
		 * 		  use in normal form submit:
		 * 				form - form id. if this value is exists then method and data value will be ignored and type value will set to 0.
		 * 				method - get or post. if data is specify then data will append to url.(Only support get for now)
		 * 
		 * Usage:
		 * 		ajax:
		 * 				$.pagination({
		 *				   "data" : {"pager.keySearch" : $("#key_search").val()},
		 *				   "success" : function(data){
		 *					 $("#security_content").empty().append(data);
		 *				   }
		 *			    });
		 *      form:
		 *             $.pagination({"form" : "form"})
		 *      get:
		 *      	   $.pagination({"method" : "get"})
		 * 				
		 */
		pagination : function(params){
			var url = $("#pageUrl");
			var pageIndex = $("#pageIndex");
			params = $.extend({type : 1, data : {}}, params);
			if(params.form || params.method){ 
				params.type = 0;
			}
			init();
			function init(){
				$("#nav_pre").click(function() {
					if(!$(this).hasClass("nav_pre_disable")){
						params.data["page"] = parseInt(pageIndex.val()) - 1;
						doQuery();
					}
				});
				$("#nav_next").click(function() {
					if(!$(this).hasClass("nav_next_disable")){
						params.data["page"] = parseInt(pageIndex.val()) + 1;
						doQuery();
					}
				});
				$(".nav_page").click(function() {
					if($(this).attr("index") != pageIndex.val()){
						params.data["page"] = parseInt($(this).attr("index"));
						doQuery();
					}
				});
				
				//set hover effect
				$(".nav_page").hover(function(){
					if($(this).attr("index") != pageIndex.val()){
						$(this).addClass("nav_page_hover");
					}
				},function(){
					if($(this).attr("index") != pageIndex.val()){
						$(this).removeClass("nav_page_hover");
					}
				});
				
				appendQueryNode();
			}
			function doQuery(){
				if(params.type == 1){
					ajaxQuery();
				}else{
					normalQuery();
				}
			}
			
			function ajaxQuery(){
				$.ajax({
					url : url.val(),
					type : "post",
					data : params.data,
					success : function(data){
						params.success(data);
					}
				});
			}
			
			function normalQuery(){
				if(params.form){
					$("#pagerForm").find("#pageIndex").val(params.data["page"]);
					$("#pagerForm").submit();
				}else{//Don't have a from and want to use a get request 
					if(params.method){
						if(params.method == "get"){ //use get request
							var urlQueryString = $.param(params.data);
							var requestUrl = url.val() + "?" + urlQueryString;
							location.href = requestUrl;
						}
					}
				}
			}
			/**
			 * clone all of form's input to pagerForm
			 */
			function appendQueryNode(){
				var form = $("#" + params.form);
				var pagerForm = $("#pagerForm");
				$.each(form.find(":input"), function(idx, o){
					$(this).clone().appendTo(pagerForm);
				});
			}
		}

	});
})(jQuery);

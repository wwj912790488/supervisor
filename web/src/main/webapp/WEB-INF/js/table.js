function tableStyle(id){
	var Ptr = document.getElementById(id).getElementsByTagName("tr");
	for (i = 1; i < Ptr.length + 1; i++) {
		Ptr[i - 1].className = (i % 2 > 0) ? "t1"
				: "t2";
	}  
	for ( var i = 0; i < Ptr.length; i++) {
		Ptr[i].onmouseover = function() {
			this.tmpClass = this.className;
			this.className = "t3";
		};
		Ptr[i].onmouseout = function() {
			this.className = this.tmpClass;
		};
	}
}

function tableStyle2(id){
	var Ptr = document.getElementById(id).getElementsByTagName("tr");
	for (i = 1; i < Ptr.length + 1; i++) {
		Ptr[i - 1].className = (i % 2 > 0) ? "t2"
				: "t1";
	}  
	for ( var i = 0; i < Ptr.length; i++) {
		Ptr[i].onmouseover = function() {
			this.tmpClass = this.className;
			this.className = "t3";
		};
		Ptr[i].onmouseout = function() {
			this.className = this.tmpClass;
		};
	}
}

function tableStyleByTrClass(id,cls){
	var Ptr = $("#"+id).find("tr[class='"+cls+"']");
	Ptr.each(function(i){
		Ptr[i].className = (i % 2 > 0) ? "t1"
				: "t2";
	})
	Ptr.each(function(i){
		Ptr[i].onmouseover = function() {
			if(!$(Ptr[i]).find("input[type=checkbox]").attr("checked")){
				this.tmpClass = this.className;
				this.className = "t3";
			}
		};
		Ptr[i].onmouseout = function() {
			if(!$(Ptr[i]).find("input").attr("checked")){
				this.className = this.tmpClass;
			}
		};
		
		$(Ptr[i]).find("input[type=checkbox]").click(function(){
			if($(this).attr("checked")){
				Ptr[i].tmpClass1 = Ptr[i].tmpClass;
				Ptr[i].className = "t3";
			}else if(!$(this).attr("checked")){
				Ptr[i].className = Ptr[i].tmpClass1;
			}
		});
	})
}

function tableStyleByTrTag(id,tag){
	var Ptr = $("#"+id).find("tr");
	Ptr.each(function(i){
		Ptr[i].className = (i % 2 > 0) ? "t1" : "t2";
	})
	
	
	Ptr.each(function(i){
		Ptr[i].onmouseover = function() {
			if(!$(Ptr[i]).find("input[type=checkbox]").attr("checked")){
				this.className = "t3";
			}
		};
		Ptr[i].onmouseout = function() {
			if(!$(Ptr[i]).find("input[type=checkbox]").attr("checked")){
				this.className = (i % 2 > 0) ? "t1" : "t2";
			}
		};
		
		$(Ptr[i]).find("input[type=checkbox]").click(function(){
			if($(this).attr("checked")){
				Ptr[i].className = "t3";
			}else if(!$(this).attr("checked")){
				Ptr[i].className = (i % 2 > 0) ? "t1" : "t2";
			}
		});
	})
}
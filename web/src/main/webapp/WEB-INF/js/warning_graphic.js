$(function() {
    $("#inc_tab")
        .find("li")
        .removeClass("active")
        .end()
        .find("#tb9")
        .addClass("active");

    $.pagination({form : "channel-select-form"});

    $("#search-btn")
    .on("click", function() {
    	$("#channel-select-form").submit();
    })
    
});

var WarningGraphic = (function($, _, Backbone, sv){
	//var canvas = oCanvas.create({ canvas: "#channel" });

	var WarningBlock = function(canvas, x, width, height, fill, zIndex, message) {
		this.x = x;
		this.width = width;
		this.height = height;
		this.fill = fill;
		this.message = message;
		this.block = canvas.display.rectangle({
			x: this.x,
			y: 0,
			width: this.width,
			height: this.height,
			zIndex: zIndex,
			fill: this.fill
		});
		this.bubbleMessage = canvas.display.text({
			x: 0,
			y: -15,
			align: "center",
			font: "12px",
			text: this.message,
			fill: "#000"
		});
		this.block.bind('mouseenter', _.bind(function() {
			this.block.addChild(this.bubbleMessage);
		}, this));
		this.block.bind('mouseleave', _.bind(function() {
			this.block.removeChild(this.bubbleMessage);
		}, this));
	};

	var ChannelBlock = function(canvas, x, y, width, height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.data = [];
		this.fill = {
			28: "#FF4444",
			29: "#FF4444",
			30: "#FF4444",
			31: "#FF4444"
		};
		this.message = {
		"0": "黑场",
        "32": "静音",
        "33": "低音",
        "34": "高音",
        "37": "爆音",
        "2": "静帧",
        "27": "CC错误",
        "28": "audio丢失",
        "29": "video丢失",
        "30": "信源中断"
		}
		this.base = canvas.display.rectangle({
			x : this.x,
			y : this.y,
			width : this.width,
			height : this.height,
			fill : "#40B440"
		})
	};

	_.extend(ChannelBlock.prototype, {
		getFill: function(type) {
			var fill = this.fill[type];
			if(!fill) {
				fill = "#FF9C1C";
			}
			return fill;
		},
		getMessage: function(type) {
			var message = this.message[type];
			if(!message) {
				message = "warning";
			}
			return message;
		},
		setData: function(canvas, start, end, data) {
			//this.clearData();
			var that = this;
			var range = end - start;
			this.base.remove(false);
			_.each(data, function(item) {
				var zIndex = "front";
				if(item.endTime == 0) {
					item.endTime = end;
					zIndex = "back"
				}
				if(item.startTime < start) {
					item.startTime = start;
				}
				var originStart = item.startTime;
				var originRange = item.endTime - item.startTime;
				var scaleX = (originStart - start)*that.width/range;
				var scaleRange = originRange*that.width/range;
				if(scaleRange <  5) {
					scaleRange = 5;
				}
				var block = new WarningBlock(canvas, scaleX, scaleRange, that.height, that.getFill(item.type), zIndex, that.getMessage(item.type));
				//that.data.push(block);
				that.base.addChild(block.block, false);
			});
			this.base.add(false);
			canvas.redraw();
		},
		clearData: function() {
			_.each(this.data, function(item) {
				item.block.remove(false);
			});
			this.data = [];
		}
	});

    var WarningGraphicView = Backbone.View.extend({

    	update: function(start, end) {
    		var params = {};
    		params.channelIds = this.channelIds;
    		params.types = this.types;
    		if(start && end) {
    			params.startTime = start;
				params.endTime = end;
    		} else {
    			var now = Date.now();
    			params.startTime = now - 1000 * 60 * 60 * 24;
    			params.endTime = now;
    		}
    		
			var that = this;
    		sv.ajax.postJson("/warning/graphic/channels", params)
        		.fail(function(jqXhr){
                    
        		}).done(function(result) {
    				that.render(result);
    				that.renderTimeline(result.start, result.end);
        		}).always(function(){                
        		
        		});	
    	},

    	renderTimeline: function(start, end) {
    		if(this.timelineCanvas) {
    			this.timelineCanvas.destroy();
    		}
    		this.timelineCanvas = oCanvas.create({ canvas: "#channel-timeline"});

		    var timeblockproto = this.timelineCanvas.display.rectangle({
		    	x : 0,
		    	y : 0,
		    	width : 100,
		    	height : 30, 
		    	fill : "#aaa",
		    	stroke: "1px #000"
		    });

		    var timeblocktextproto = this.timelineCanvas.display.text({
		    	x : 8,
		    	y : 8,
		    	align: "center",
		    	font : "12px",
		    	text : "",
		    	fill : "#000"
		    });

		    var slice = (end - start)/10;

		    for(var i = 1; i <= 10; i++) {
		    	var date = moment(start + slice*i).format("MM-DD HH:mm:ss");
		    	var timeblocktext = timeblocktextproto.clone({
		    		text : date
		    	});
		    	var timeblock = timeblockproto.clone({
		    		x : (i-1) * 100
		    	});

		    	timeblock.addChild(timeblocktext);
		    	this.timelineCanvas.addChild(timeblock);
		    }
    	},

    	render: function(result) {
    		var that = this;
    		_.each(this.channelIds, function(channel) {
    			var canvas;
    			if(!that.channelCanvas[channel]) {
    				canvas = oCanvas.create({ canvas : "#channel-canvas-" + channel});
    				that.channelCanvas[channel] = canvas;
    			} else {
    				canvas = that.channelCanvas[channel];
    				canvas.destroy();
    				canvas = oCanvas.create({ canvas : "#channel-canvas-" + channel});
    				that.channelCanvas[channel] = canvas;
    			}
    			//canvas.reset();
    			
    			var channelBlock = new ChannelBlock(canvas, 0, 20, 1000, 30);
    			canvas.addChild(channelBlock.base, false);
    			var data = [];
    			if(result && result.dataset) {
    				data = result.dataset[channel];
    			} 
    			var start = 0, end = 1;
    			if(result && result.start) {
    				start = result.start;
    			}
    			if(result && result.end) {
    				end = result.end;
    			}
    			channelBlock.setData(canvas, start, end, data);
    		});
    	},

    	startRealTime: function() {
    		if(this.realTimeId == -1) {
    			this.realTimeId = setInterval(_.bind(this.update, this), 1000 * 10);
    		}
    	},
    	
    	initialize: function(options) {
    		this.types = options.types;
    		this.realTimeId = -1;
    		this.channelIds = [];
    		this.channelCanvas = {};
    		var that = this;
    		$(".channel-item").each(function() {
    			var id = $(this).data("id");
    			that.channelIds.push(id);
    		});
    		this.update();
    		//this.update("2015-08-02T08:00:00", "2015-08-04T08:00:00");
    		this.startRealTime();
    	}
    });

	return {
		init: function(types) {
			var view = new WarningGraphicView({types : types});
		}
	}
}(jQuery, _, Backbone, window.sv));


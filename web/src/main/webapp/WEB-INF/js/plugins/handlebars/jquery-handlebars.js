(function ($) {
    var _cache = {};
    $.fn.render = function (templateId, data) {
        this.html($.renderString(templateId, data));
        return this;
    };
    $.renderString = function (templateId, data) {
        if (_cache.hasOwnProperty(templateId)) {
            return _cache[templateId](data);
        } else {
            _cache[templateId] = Handlebars.compile($("#" + templateId).html());
            return _cache[templateId](data);
        }
    };
    Handlebars.registerHelper('ifCond', function (v1, operator, v2, options) {

        switch (operator) {
            case '==':
                return (v1 == v2) ? options.fn(this) : options.inverse(this);
            case '===':
                return (v1 === v2) ? options.fn(this) : options.inverse(this);
            case '!=':
                return (v1 != v2) ? options.fn(this) : options.inverse(this);
            case '<':
                return (v1 < v2) ? options.fn(this) : options.inverse(this);
            case '<=':
                return (v1 <= v2) ? options.fn(this) : options.inverse(this);
            case '>':
                return (v1 > v2) ? options.fn(this) : options.inverse(this);
            case '>=':
                return (v1 >= v2) ? options.fn(this) : options.inverse(this);
            case '&&':
                return (v1 && v2) ? options.fn(this) : options.inverse(this);
            case '||':
                return (v1 || v2) ? options.fn(this) : options.inverse(this);
            default:
                return options.inverse(this);
        }
    });
    Handlebars.registerHelper('ifIn', function (list, item, options) {
        return list && list.length > 0 && list.indexOf(item) > -1 ? options.fn(this) : options.inverse(this);
    });
    Handlebars.registerHelper('ifNotIn', function (list, item, options) {
        return list && list.length > 0 && list.indexOf(item) > -1 ? options.inverse(this) : options.fn(this);
    });
    Handlebars.registerHelper('select', function( value, options ) {
        var $el = $('<select />').html( options.fn(this) );
        var $value = $el.find('[value="' +  value + '"]');
        if($value.length == 0) {
            $value = $el.find('[value="custom"]');
        }
        if($value.length == 0) {
            $value = $el.children().eq(0);
        }
        $value.attr({'selected':'selected'});
        return $el.html();
    });
    
    Handlebars.registerHelper("datetime",function(value){
    	var datetime = new Date(value);
    	return datetime.toLocaleDateString() + " " +datetime.toLocaleTimeString() + "." + datetime.getMilliseconds();
    });

    Handlebars.registerHelper("ifNotEmpty",function(str, options){
        return str && str.length > 0 ? options.fn(this) : options.inverse(this);       
    });

}(jQuery));
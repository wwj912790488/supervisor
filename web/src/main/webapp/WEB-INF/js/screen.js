Backbone.Marionette.Renderer.render = function (template, data) {
    if (!template) {
        throw new Marionette.Error({
            name: 'TemplateNotFoundError',
            message: 'Cannot render the template since its false, null or undefined.'
        });
    }
    var renderer = Handlebars.compile($("#" + template).html());
    return renderer(data);
};

var ChannelList = (function ($, _, Backbone, Marionette, sv) {

    var Filter = function (options) {
        this.initialize.apply(this, arguments);
    };

    Filter.extend = Backbone.Model.extend;

    _.extend(Filter.prototype, Backbone.Events, {
        filter: function (model, options) {
            return false;
        },
        update: function () {
            this.trigger("filter:updated");
        }
    });

    var TargetAttributeValueFilter = Filter.extend({
        initialize: function (options) {
            this.targetAttribute = options.targetAttribute;
            this.filterString = options.filterString;
        },
        filter: function (model, options) {
            return model.get(this.targetAttribute).indexOf(this.filterString) != -1;
        },
        update: function (filterString) {
            this.filterString = filterString;
            this.trigger("filter:updated", this);
        }
    });

    var ChannelTagFilter = Filter.extend({
        initialize: function (options) {
            if (options) {
                this.filterTagString = options.filterTagString;
            }
        },
        filter: function (model, options) {
            var tags = model.get("tags");
            if (tags.length == 0 && !this.filterTagString) {
                return true;
            }
            return _.any(tags, function (tag) {
                return tag.name.indexOf(this.filterTagString) != -1
            }, this);
        },
        update: function (filterTagString) {
            this.filterTagString = filterTagString;
            this.trigger("filter:updated", this);
        }
    });

    var ItemContainedFilter = Filter.extend({
        initialize: function (options) {

        },
        filter: function (model, options) {
            if (!this.enabled) {
                return true;
            } else {
                var id = model.get("id");
                return this.items.indexOf(id) == -1;
            }

        },
        updateItems: function (items) {
            this.items = items;
            this.trigger("filter:updated", this);
        },
        updateEnabled: function (enabled) {
            this.enabled = enabled;
            this.trigger("filter:updated", this);
        }
    });

    var FilterableModel = Backbone.Model.extend({
        initialize: function (attributes, options) {
            if (options.filters && _.isArray(options.filters)) {
                this.filters = _.map(options.filters, function (filter) {
                    if (this._isFilter(filter)) {
                        return filter
                    } else if (_.isFunction(filter)) {
                        return new filter();
                    }
                });
                _.each(this.filters, this.register);
            } else {
                this.filters = [];
            }
        },
        _isFilter: function (filter) {
            return filter instanceof Filter;
        },
        register: function (filter) {
            this.filters.push(filter);
            this.listenTo(filter, "filter:updated", this.filterUpdated);
            this.filterUpdated();
        },
        filterUpdated: function () {
            this.trigger("filter:changed");
        },
        shouldBeShown: function () {
            var filtered = false;
            if (this.filters && _.isArray(this.filters)) {
                filtered = _.all(this.filters, function (filter) {
                    return filter.filter(this);
                }, this);
            }
            return filtered;
        }
    });

    var ChannelModel = FilterableModel.extend({});

    var ChannelCollection = Backbone.Collection.extend({
        model: ChannelModel
    });

    var ChannelListItemView = Marionette.ItemView.extend({
        template: 'channelItemTemplate',
        attributes: {
            'class': 'channel-item'
        },
        ui: {
            select: 'input[name="channel-item-select"]'
        },
        events: {
            'click @ui.select': 'onCheckboxClicked',
            'click': 'onItemClicked'
        },
        modelEvents: {
            "filter:changed": "filter"
        },
        onCheckboxClicked: function (event) {
            //if ($(event.currentTarget).parent().parent().parent().attr('id') == "multiplechannellist")

            var selected = $(event.currentTarget).prop("checked");
            if (selected) {
                this.triggerMethod("channelitem:selected");
            } else {
                this.triggerMethod("channelitem:unselected");
            }

            event.stopPropagation();
        },
        onItemClicked: function (event) {
            var selected = this.ui.select.prop("checked");
            if (selected) {
                this.ui.select.prop("checked", false);
                this.triggerMethod("channelitem:unselected");
            } else {
                this.ui.select.prop("checked", true);
                this.triggerMethod("channelitem:selected");
            }
        },
        unselected: function () {
            this.ui.select.prop("checked", false);
        },
        selected: function () {
            this.ui.select.prop("checked", true);
        },
        filter: function () {
            if (this.model.shouldBeShown()) {
                this.$el.show();
            } else {
                this.$el.hide();
            }
        }
    });

    var SingleSelectionChannelListView = Marionette.CompositeView.extend({
        childView: ChannelListItemView,
        childViewContainer: '#channellist-container',
        template: 'channelListTemplate',
        el: '#singlechannellist',
        ui: {
            select: '#channellist-select-btn',
            cancel: '#channellist-cancel-btn'
        },
        attributes: {
            'class': 'modal'
        },
        events: {
            'click @ui.select': 'channelSelected',
            'click @ui.cancel': 'closeModal',
            'keyup #channelList-name-filter': 'nameFilterValueChanged',
            'keyup #channelList-tag-filter': 'tagFilterValueChanged',
            'change #channelList-used-filter': 'usedFilterCheckedChanged'
        },
        childEvents: {
            'channelitem:selected': function (view) {
                this.children.each(function (child) {
                    if (view != child) {
                        child.unselected();
                    }
                });
                this.selectedItem = view.model;
            },
            'channelitem:unselected': function (view) {
                this.selectedItem = null;
            }
        },
        openModal: function (channelId, schemaId, row, column, groupIndex) {
            this.selectedItem = null;
            this.$el.find("#channelList-name-filter").val('');
            this.$el.find("#channelList-tag-filter").val('');
            this.nameFilter.update('');
            this.tagFilter.update('');
            this.schemaId = schemaId;
            this.row = row;
            this.column = column;
            this.groupIndex = groupIndex;
            if (channelId != -1) {
                this.selectedItem = this.collection.get(channelId);
            }

            var _this = this;

            sv.ajax.getJSON("/screen/activeChannels", function (channels) {
                var channelIds = _.map(channels, function (channel) {
                    return channel.id;
                });
                _this.usedFilter.updateItems(channelIds);
                _this.$el.modal({
                    showClose: false,
                    clickClose: false
                });

                _this.setSelection();
            });

        },
        closeModal: function () {
            $.modal.close();
        },
        channelSelected: function () {
            var channelId = -1;
            if (this.selectedItem) {
                channelId = this.selectedItem.get('id');
            }
            var screenPosition = {
                schemaId: this.schemaId, row: this.row, column: this.column, group: this.groupIndex,
                channelId: channelId
            };
            var that = this;
            sv.ajax.post("/screen/updateScreenPosition", screenPosition)
                .then(function (updated) {
                    that.closeModal();
                    Backbone.trigger("addchannel:finish", updated);

                })
                .fail(function (jqXhr) {
                    that.closeModal();
                    Backbone.trigger("addchannel:failed", jqXhr);

                });
        },
        setSelection: function () {

            this.children.each(function (child) {
                if (child.model == this.selectedItem) {
                    child.selected();

                    var top = child.$el.position().top;
                    this.$el.find('#channellist-container').scrollTop(top > 0 ? top : 0);

                } else {
                    child.unselected();
                }

            }, this);
        },
        nameFilterValueChanged: function (event) {
            var nameFilterValue = this.$el.find('#channelList-name-filter').val() || '';
            this.nameFilter.update(nameFilterValue);
        },
        tagFilterValueChanged: function (event) {
            var tagFilterValue = this.$el.find("#channelList-tag-filter").val() || '';
            this.tagFilter.update(tagFilterValue);
        },
        usedFilterCheckedChanged: function (event) {
            var checked = this.$el.find("#channelList-used-filter").prop("checked");
            this.usedFilter.updateEnabled(checked);
        },
        onRender: function () {
            this.$el.find("#channelList-title").text("添加频道");
        },
        initialize: function (options) {
            this.nameFilter = new TargetAttributeValueFilter({
                targetAttribute: "name",
                filterString: ''
            });
            this.tagFilter = new ChannelTagFilter();
            this.usedFilter = new ItemContainedFilter();
            this.collection.each(function (model) {
                model.register(this.nameFilter);
                model.register(this.tagFilter);
                model.register(this.usedFilter);
            }, this);
        }
    });

    var MultipleSelectionChannelListView = Marionette.CompositeView.extend({
        childView: ChannelListItemView,
        childViewContainer: '#channellist-container',
        template: 'channelListTemplate',
        el: '#multiplechannellist',
        ui: {
            select: '#channellist-select-btn',
            cancel: '#channellist-cancel-btn'
        },
        attributes: {
            'class': 'modal'
        },
        events: {
            'click @ui.select': 'channelSelected',
            'click @ui.cancel': 'closeModal',
            'keyup #channelList-name-filter': 'nameFilterValueChanged',
            'keyup #channelList-tag-filter': 'tagFilterValueChanged',
            'change #channelList-used-filter': 'usedFilterCheckedChanged'
        },
        childEvents: {
            'channelitem:selected': function (view) {
                this.selectedItem.push(view.model);
            },
            'channelitem:unselected': function (view) {
                var index = this.selectedItem.indexOf(view.model);
                if (index != -1) {
                    this.selectedItem.splice(index, 1);
                }
            }
        },
        openModal: function (schemaId, group) {
            this.schemaId = schemaId;
            this.group = group
            this.selectedItem = [];
            this.$el.find("#channelList-name-filter").val('');
            this.$el.find("#channelList-tag-filter").val('');
            this.nameFilter.update('');
            this.tagFilter.update('');

            var _this = this;

            sv.ajax.getJSON("/screen/activeChannels", function (channels) {
                var channelIds = _.map(channels, function (channel) {
                    return channel.id;
                });
                _this.usedFilter.updateItems(channelIds);
                _this.$el.modal({
                    showClose: false,
                    clickClose: false
                });

                _this.clearSelection();
            });

        },
        closeModal: function () {
            $.modal.close();
        },
        channelSelected: function () {
            if (this.selectedItem.length > 0) {
                var channels = _.map(this.selectedItem, function (model) {
                    return model.get('id');
                });
                var that = this;
                sv.ajax.post("/screen/updateScreenPositionBundle", {
                        "schemaId": that.schemaId,
                        "group": that.group,
                        "allowgroup": true,
                        "channels": $.toJSON(channels)
                    }
                ).then(function (schema) {
                        that.closeModal();
                        Backbone.trigger("addchannelbundle:finish", schema);
                    })
                    .fail(function (jqXhr) {
                        Backbone.trigger("addchannelbundle:failed", jqXhr);
                    });
            }
        },
        clearSelection: function () {
            this.children.each(function (child) {
                child.unselected();
            }, this);
        },
        nameFilterValueChanged: function (event) {
            var nameFilterValue = this.$el.find('#channelList-name-filter').val() || '';
            this.nameFilter.update(nameFilterValue);
        },
        tagFilterValueChanged: function (event) {
            var tagFilterValue = this.$el.find("#channelList-tag-filter").val() || '';
            this.tagFilter.update(tagFilterValue);
        },
        usedFilterCheckedChanged: function (event) {
            var checked = this.$el.find("#channelList-used-filter").prop("checked");
            this.usedFilter.updateEnabled(checked);
        },
        onRender: function () {
            this.$el.find("#channelList-title").text("批量添加频道");
        },
        initialize: function (options) {
            this.nameFilter = new TargetAttributeValueFilter({
                targetAttribute: "name",
                filterString: ''
            });
            this.tagFilter = new ChannelTagFilter();
            this.usedFilter = new ItemContainedFilter();
            this.collection.each(function (model) {
                model.register(this.nameFilter);
                model.register(this.tagFilter);
                model.register(this.usedFilter);
            }, this);
        }
    })

    return {
        ChannelCollection: ChannelCollection,
        SingleSelectionChannelListView: SingleSelectionChannelListView,
        MultipleSelectionChannelListView: MultipleSelectionChannelListView
    }

}(jQuery, _, Backbone, Marionette, window.sv));

var OpsList = (function ($, _, Backbone, Marionette, sv) {

    var OpsModel = Backbone.Model.extend({
        defaults: {
            'nameFilter': '',
            'usedFilter': false
        },
        shouldBeShown: function () {
            if (this.get('usedFilter') && this.get('wallPosition') != -1) {
                return false;
            }
            if (this.get('nameFilter') != '' && this.get('ip').indexOf(this.get('nameFilter')) == -1) {
                return false;
            }
            return true;
        }
    });

    var OpsCollection = Backbone.Collection.extend({
        url: function () {
            return sv.urlPath.getRealPath("/screen/opsServers");
        },
        model: OpsModel
    });

    var OpsListItemView = Marionette.ItemView.extend({
        template: 'opsItemTemplate',
        attributes: {
            'class': 'ops-item'
        },
        ui: {
            select: 'input[name="ops-item-select"]'
        },
        events: {
            'click @ui.select': 'onCheckboxClicked',
            'click': 'onItemClicked'
        },
        modelEvents: {
            "change nameFilter": "filter",
            "change usedFilter": "filter"
        },
        onCheckboxClicked: function (event) {
            var selected = $(event.currentTarget).prop("checked");
            if (selected) {
                this.triggerMethod("opsitem:selected");
            } else {
                this.triggerMethod("opsitem:unselected");
            }
            event.stopPropagation();
        },
        onItemClicked: function (event) {
            if (this.model.get('wallPosition') != -1) {
                return;
            }
            var selected = this.ui.select.prop("checked");
            if (selected) {
                this.ui.select.prop("checked", false);
                this.triggerMethod("opsitem:unselected");
            } else {
                this.ui.select.prop("checked", true);
                this.triggerMethod("opsitem:selected");
            }
        },
        unselected: function () {
            this.ui.select.prop("checked", false);
        },
        filter: function (model) {
            if (model.shouldBeShown()) {
                this.$el.show();
            } else {
                this.$el.hide();
            }
        }
    });

    var OpsListView = Marionette.CompositeView.extend({
        childView: OpsListItemView,
        childViewContainer: '#opslist-container',
        template: 'opsListTemplate',
        el: '#opslist',
        ui: {
            bind: '#opslist-bind-btn',
            cancel: '#opslist-cancel-btn'
        },
        attributes: {
            'class': 'modal',
            id: 'opslist-modal'
        },
        events: {
            'click @ui.bind': 'bindOps',
            'click @ui.cancel': 'closeModal',
            'click #opsList-used-filter': 'filterUsed',
            'keyup #opsList-name-filter': 'filterName'
        },
        childEvents: {
            'opsitem:selected': function (view) {
                this.children.each(function (child) {
                    if (view != child) {
                        child.unselected();
                    }
                });
                this.selectedItem = view.model;
            },
            'opsitem:unselected': function (view) {
                this.selectedItem = null;
            }
        },
        openModal: function (wallPosition) {
            this.wallPosition = wallPosition;
            this.selectedItem = null;
            $("#opsList-message").text("");
            this.clearSelection();
            var _this = this;
            this.collection.fetch({
                success: function (collection, response, options) {
                    _this.collection.invoke('set', 'nameFilter', '');
                    _this.collection.invoke('set', 'usedFilter', false);
                    _this.$el.modal({
                        showClose: false,
                        clickClose: false
                    })
                    _this.render();
                }
            });
        },
        closeModal: function () {
            $.modal.close();
        },
        clearSelection: function () {
            this.children.each(function (child) {
                child.unselected();
            });
        },
        bindOps: function () {
            var _this = this;
            if (this.selectedItem) {
                $.post("bindOps", {
                        "wallPosition": this.wallPosition,
                        "opsId": this.selectedItem.get('id')
                    }
                ).fail(function (jqXhr) {
                    Backbone.trigger('bindOps:fail');
                }).done(function (ops) {
                    _this.selectedItem.set('wallPosition', _this.wallPosition);
                    Backbone.trigger('bindOps:finish', ops.id, ops.ip);
                }).always(function () {

                });
                $.modal.close();
            } else {
                $("#opsList-message").text("未选择ops设备");
            }
        },
        filterName: function () {
            var nameFilter = $('#opsList-name-filter').val() || '';
            this.collection.invoke('set', 'nameFilter', nameFilter);

        },
        filterUsed: function () {
            var usedFilter = $('#opsList-used-filter').prop("checked");
            this.collection.invoke('set', 'usedFilter', usedFilter);
        },
        initialize: function () {
            this.selectedItem = null;
        }
    });

    var OpsList = function () {
        var models = [];
        this.collection = new OpsCollection(models);
    };

    OpsList.prototype = {
        bind: function (wallPosition) {
            if (!this.view) {
                this.view = new OpsListView({collection: this.collection});
            }
            this.view.render();
            this.view.openModal(wallPosition);
        },
        unbind: function (wallPosition) {
            this.wallPosition = +wallPosition;
            var _this = this;
            $.post("unbindOps", {
                    "wallPosition": this.wallPosition
                }
            ).fail(function (jqXhr) {
                Backbone.trigger('unbindOps:fail');
            }).done(function () {
                var model = _this.collection.find(function (model) {
                    return model.get('wallPosition') == _this.wallPosition;
                });
                if (model) {
                    model.set('wallPosition', -1);
                }
                Backbone.trigger('unbindOps:finish', "", "");
            }).always(function () {


            });
        }
    }

    return OpsList;
}(jQuery, _, Backbone, Marionette, window.sv));

var MessageStyle = (function ($, _, Backbone, Marionette, sv) {
    var MessageStyleModel = Backbone.Model.extend({});

    var MessageStylePreview = Marionette.ItemView.extend({
        template: 'messageStyleTemplate',
        el: '#message-style-modal',
        ui: {
            save: '#style-save-btn',
            saveDefault: '#style-save-default-btn',
            reset: '#style-reset-btn',
            cancel: '#style-cancel-btn'
        },
        events: {
            'click @ui.save': 'updateStyle',
            'click @ui.saveDefault': 'updateDefaultStyle',
            'click @ui.reset': 'resetStyle',
            'click @ui.cancel': 'closeModal',
            'change #style-font': 'fontChanged',
            'change #style-font-size': 'fontSizeChanged',
            'change #style-position-x': 'positionXChanged',
            'change #style-position-y': 'positionYChanged',
            'change #style-width': 'widthChanged',
            'change #style-height': 'heightChanged',
            'change #style-font-color': 'fontColorChanged',
            'change #style-font-color-alpha': 'alphaChanged'
        },
        attributes: {
            'class': 'modal'
        },
        serializeData: function (options) {
            var data = Marionette.ItemView.prototype.serializeData.call(this);
            if (data.color && _.isNumber(data.color)) {
                data.color = this.numberToHex(data.color);
            }
            if (options && options.preview) {
                data.size = data.size / 5;
                data.x = data.x / 5;
                data.y = data.y / 5;
                data.width = data.width / 5;
                data.height = data.height / 5;
                data.alpha = data.alpha / 100;
            }
            return data;
        },
        numberToHex: function (number) {
            var hexString = number.toString(16);
            var preZeroLength = 6 - hexString.length;
            var zeros = "";
            for (var i = 0; i < preZeroLength; i++) {
                zeros = zeros + '0';
            }
            return "#" + zeros + hexString;
        },
        openModal: function (screenId, model) {
            this.screenId = screenId;
            this.model = model;
            this.listenTo(this.model, 'change', this.render);
            this.$el.modal({
                showClose: false,
                clickClose: false
            });
            this.render();
        },
        updateStyle: function () {
            var data = {};
            data.screenId = this.screenId;
            data.style = this.model.attributes;
            sv.ajax.postJson("/screen/updateScreenStyle", data).then(function (style) {
                $.modal.close();
                Backbone.trigger("style:updated", style);
            });
        },
        resetStyle: function () {
            var data = {};
            data.screenId = this.screenId;
            data.style = this.model.attributes;
            var that = this;
            sv.ajax.postJson("/screen/resetScreenStyle", data).then(function (style) {
                that.model.set(style);
                Backbone.trigger("style:updated", style);
            });
        },
        updateDefaultStyle: function () {
            var data = {};
            data.screenId = this.screenId;
            data.style = this.model.attributes;
            sv.ajax.postJson("/screen/updateScreenDefaultStyle", data).then(function (style) {
                $.modal.close();
                Backbone.trigger("style:updated", style);
            });
        },
        closeModal: function () {
            this.stopListening(this.model);
            $.modal.close();
        },
        onRender: function () {
            this.colorPicker = $.farbtastic('#style-font-color-picker', '#style-font-color');
            this.colorPicker.updateDisplay = _.wrap(this.colorPicker.updateDisplay, function (func) {
                func();
                Backbone.trigger('color:picked', this.color);
            });

            if (!this.colorPicker.color && this.model) {
                this.colorPicker.setColor(this.numberToHex(this.model.get('color')));
            }
            var previewRenderer = Handlebars.compile($("#messageStylePreviewTemplate").html());
            var ifr = $("#style-preview-iframe").get(0);
            var ifrw = (ifr.contentWindow) ? ifr.contentWindow : (ifr.contentDocument.document) ? ifr.contentDocument.document : ifr.contentDocument;
            ifrw.document.open();
            ifrw.document.write(previewRenderer(this.serializeData({preview: true})));
            ifrw.document.close();
        },
        fontChanged: function (event) {
            this.model.set('font', $(event.currentTarget).val());
        },
        fontSizeChanged: function (event) {
            var size = $(event.currentTarget).val();
            if (size < 20) {
                size = 20;
            }
            if (size > 500) {
                size = 500;
            }
            $(event.currentTarget).val(size);
            this.model.set('size', size);
        },
        positionXChanged: function (event) {
            var x = $(event.currentTarget).val();
            if (x < 0) {
                x = 0;
            }
            if (x > 1920) {
                x = 1920;
            }
            $(event.currentTarget).val(x);
            this.model.set('x', x);
        },
        positionYChanged: function (event) {
            var y = $(event.currentTarget).val();
            if (y < 0) {
                y = 0;
            }
            if (y > 1080) {
                y = 1080;
            }
            $(event.currentTarget).val(y);
            this.model.set('y', y);
        },
        widthChanged: function (event) {
            var width = $(event.currentTarget).val();
            if (width < 0) {
                width = 0;
            }
            if (width > 1920) {
                width = 1920;
            }
            $(event.currentTarget).val(width);
            this.model.set('width', width);
        },
        heightChanged: function (event) {
            var height = $(event.currentTarget).val();
            if (height < 0) {
                height = 0;
            }
            if (height > 1080) {
                height = 1080;
            }
            $(event.currentTarget).val(height);
            this.model.set('height', height);
        },
        fontColorChanged: function () {
            $("#style-font-color").val(this.colorPicker.color);
        },
        alphaChanged: function (event) {
            this.model.set('alpha', $(event.currentTarget).val());
        },
        colorPicked: function (color) {
            var hex = "0x" + this.colorPicker.color.substring(1);
            var color = +hex;
            this.model.set('color', color);
        },
        initialize: function () {
            this.listenTo(Backbone, 'color:picked', this.colorPicked);
        }
    });

    return {
        MessageStyleModel: MessageStyleModel,
        MessageStylePreview: MessageStylePreview
    };


}(jQuery, _, Backbone, Marionette, window.sv));

var Screen = (function ($, _, Backbone, Marionette, sv) {
    var ScreenModel = Backbone.Model.extend({
        initialize: function (attributes, options) {
            this.wallId = options.wallId;
        },
        parse: function (resp, options) {
            if (resp.schemas) {
                var schemaCollection = new ScreenSchemaCollection();
                _.each(resp.schemas, function (attr) {
                    schemaCollection.add(new ScreenSchemaModel(attr));
                });
                resp.schemas = schemaCollection;
                this.currentSchema = schemaCollection.get(resp.active);
            }
            return resp;
        }
    });

    var ScreenSchemaModel = Backbone.Model.extend({});

    var ScreenSchemaCollection = Backbone.Collection.extend({
        model: ScreenSchemaModel
    });

    var LayoutTemplateModel = Backbone.Model.extend({});

    var LayoutTemplateCollection = Backbone.Collection.extend({
        model: LayoutTemplateModel
    });

    var LayoutTemplateListItemView = Marionette.ItemView.extend({
        template: 'layoutItemTemplate',
        attributes: function () {
            return {
                'class': 'layout-item',
                'id': 'layout-item' + this.model.get('id')
            }
        },
        events: {
            'click input[name="layout-item-select"]': 'onRadioClicked',
            'click': 'onItemClicked'
        },
        serializeData: function () {
            var data = Marionette.ItemView.prototype.serializeData.apply(this, arguments);
            if (data.positions) {
                var realPositions = [];
                var that = this;
                _.each(data.positions, function (position) {
                    var realPosition = {};
                    _.extend(realPosition, position);
                    realPosition.row = realPosition.row + 1;
                    realPosition.column = realPosition.column + 1;
                    realPositions.push(realPosition);
                })
                data.positions = realPositions
            }
            return data;
        },
        onRadioClicked: function (event) {
            event.stopPropagation();
        },
        onItemClicked: function (event) {
            var selected = this.$el.find("input[name='layout-item-select']").prop("checked");
            if (!selected) {
                this.$el.find("input[name='layout-item-select']").prop("checked", true);
            }
        },
        onRender: function () {
            var rowCount = this.model.get('rowCount'),
                columnCount = this.model.get('columnCount');

            var gridster = this.$el.find(".layout-position-grid").gridster({
                namespace: '#layout-item' + this.model.get('id'),
                widget_margins: [0, 0],
                widget_selector: ".layout-position-item",
                widget_base_dimensions: [120 / columnCount, 67 / rowCount]
            }).data('gridster');

            gridster.disable();
        }
    });

    var LayoutTemplateListView = Marionette.CompositeView.extend({
        childView: LayoutTemplateListItemView,
        childViewContainer: '#layoutlist-container',
        template: 'layoutListTemplate',
        el: '#layoutlist',
        ui: {
            select: '#layoutlist-select-btn',
            cancel: '#layoutlist-cancel-btn'
        },
        attributes: {
            'class': 'modal',
        },
        events: {
            'click @ui.select': 'layoutTemplateSelected',
            'click @ui.cancel': 'closeModal'
        },
        openModal: function (schemaId, layoutTemplate) {
            this.schemaId = schemaId;
            this.layoutTemplate = layoutTemplate
            this.$el.modal({
                showClose: false,
                clickClose: false
            });
            this.render();
        },
        onRender: function () {
            this.$el.find("input[name='layout-item-select']").prop('checked', false);
            if (this.layoutTemplate) {
                this.$el.find("input[name='layout-item-select'][value='" + this.layoutTemplate + "']").prop('checked', true);
            }

        },
        layoutTemplateSelected: function () {
            var selected = this.$el.find("input[name='layout-item-select']:checked").val();
            var that = this;
            if (selected) {
                sv.ajax.post("screen/updateSchemaTemplate", {
                    schemaId: this.schemaId,
                    template: selected
                }, function (schema) {
                    Backbone.trigger('currentSchema:updated', schema);
                    $.modal.close();
                });
            }
        },
        closeModal: function () {
            $.modal.close();
        }
    });

    var ScreenSchemaView = Marionette.ItemView.extend({
        template: 'screenActiveSchemaTemplate',
        events: {
            'change #schema-group-count': 'updateGroupCount',
            'change #schema-switch-time': 'updateSwitchTime',
            'click .schema-group-index': 'changeGroup',
            'click .position-item': 'positionClicked'
        },
        serializeData: function () {
            var data = Marionette.ItemView.prototype.serializeData.apply(this, arguments);
            if (this.currentGroup > data.groupCount) {
                this.currentGroup = 1;
            }
            if (data.positions) {
                var realPositions = [];
                var that = this;
                _.each(data.positions, function (position) {
                    if (position.group == that.currentGroup - 1) {
                        var realPosition = {};
                        _.extend(realPosition, position);
                        realPosition.row = realPosition.row + 1;
                        realPosition.column = realPosition.column + 1;
                        realPositions.push(realPosition);
                    }
                })
                data.positions = realPositions
            }
            return data;
        },
        onRender: function () {

            this.$el.find("#schema-group-index-container").empty();
            var status = $("#screen-status").val();
            if (status == 'RUNNING') {
                $("#schema-group-count").attr("disabled", true);
                $("#schema-switch-time").attr("disabled", true);
            }
            var groupCount = this.model.get('groupCount');
            if (groupCount > 1) {
                for (var g = 0; g < groupCount; g++) {
                    var index = g + 1;
                    if (this.currentGroup == index) {
                        this.$el.find("#schema-group-index-container").append($("<span class='schema-group-index active' data-index='" + index + "'>" + index + "</span>"));
                    } else {
                        this.$el.find("#schema-group-index-container").append($("<span class='schema-group-index' data-index='" + index + "'>" + index + "</span>"));
                    }
                }
            }

            var rowCount = this.model.get('row'),
                columnCount = this.model.get('column');

            var gridster = this.$el.find("#active-schema").gridster({
                namespace: "#active-schema",
                widget_margins: [0, 0],
                widget_selector: ".position-item",
                widget_base_dimensions: [480 / columnCount, 270 / rowCount]
            }).data('gridster');

            gridster.disable();

        },
        changeGroup: function (event) {
            this.currentGroup = $(event.currentTarget).data('index');
            this.render();
        },
        updateGroupCount: function (event) {
            var that = this;
            sv.ajax.post("/screen/updateSchemaGroup", {
                schemaId: that.model.get('id'),
                group: $(event.currentTarget).val()
            }, function (schema) {
                that.currentGroup = 1;
                that.model.set(schema);
            });

        },
        updateSwitchTime: function (event) {
            var that = this;
            sv.ajax.post("screen/updateSchemaSwitchTime", {
                schemaId: this.model.get('id'),
                switchTime: $(event.currentTarget).val()
            }, function (schema) {
                that.model.set(schema);
            });
        },
        positionClicked: function (event) {
            var row = $(event.currentTarget).data('row') - 1,
                column = $(event.currentTarget).data('col') - 1,
                channelId = $(event.currentTarget).data('channel'),
                schemaId = this.model.get('id');
            Backbone.trigger('position:clicked', channelId, schemaId, row, column, this.currentGroup - 1);
        },
        updateSchema: function (updated) {
            this.model.set(updated);
        },
        initialize: function () {
            this.currentGroup = 1;
            this.listenTo(this.model, 'change', this.render);
            this.listenTo(Backbone, 'addchannel:finish', this.updateSchema);
            this.listenTo(Backbone, 'addchannelbundle:finish', this.updateSchema);
        }
    });

    var ScreenSchemaTabItemView = Marionette.ItemView.extend({
        template: 'screenSchemaTabItemTemplate',
        tagName: 'li',
        events: {
            'click a': 'actived'
        },
        serializeData: function () {
            var data = Marionette.ItemView.prototype.serializeData.apply(this, arguments);
            if (!data.name) {
                data.name = "预设" + this.index
            }
            return data;
        },
        initialize: function (options) {
            this.active = options.active;
            this.index = options.index;
            this.listenTo(Backbone, 'active:changed', this.setActive);
        },
        onRender: function () {
            this.$el.find('input').hide();
            this.setActive(this.active);
        },
        actived: function () {
            this.triggerMethod('schema:actived', this.model);
        },
        setActive: function (active) {
            if (this.model == active) {
                this.$el.addClass('active');
            } else {
                this.$el.removeClass('active');
            }
        }
    })

    var ScreenSchemaTabsView = Marionette.CollectionView.extend({
        childView: ScreenSchemaTabItemView,
        tagName: 'ul',
        attributes: {
            'class': "tab-links clearfix"
        },
        childEvents: {
            'schema:actived': function (view, model) {
                this.screen.currentSchema = model;
                Backbone.trigger('active:changed', model);
            }
        },
        initialize: function (options) {
            this.screen = options.screen;
        },
        childViewOptions: function (model, index) {
            return {
                active: this.screen.currentSchema,
                index: index + 1
            }
        }
    });

    var ScreenLayoutView = Marionette.LayoutView.extend({
        el: "#tab-channel-setting",
        template: 'screenTemplate',
        regions: {
            current: '#current-schema',
            tabs: '#set-channel-tab'
        },
        codeMessage: {
            "1": "当前纪录不存在",
            "100": "系统处理出现错误",
            "103": "当前记录已被其他用户锁定",
            "104": "当前无可用的服务器",
            "105": "频道设置参数不存在或参数错误",
            "106": "屏幕墙位置不存在",
            "107": "OPS设备不存在",
            "113": "输出地址格式有误，正确的格式为udp://ip:port",
            "114": "该输出地址已经被使用，请重新输入",
            "115": "转码器无法启动任务，请检查参数设置",
            "7": "当前屏幕已被删除,请刷新页面重新操作",
            "108": "操作超时",
            "1001": "屏幕墙名称已存在",
            "1101": "当前屏幕墙正在运行，无法调换",
            "1102": "当前设置已更改，请刷新后再操作",
            "1103": "当前屏幕已被删除,请刷新页面重新操作",
            "1104": "正在运行的屏幕墙单元不能删除，请停止任务后再操作",
            "2101": "指定的频道不存在",
            "6003": "转码服务器未正确初始化，请重新初始化或添加",
            "7001": "RTSP发布url未配置",
            "7002": "RTSP存储目录未配置",
            "7003": "RTSP服务器地址未配置"
        },
        events: {
            'click #edit-screen-message-style': 'editMessageStyle',
            'click #set-channel-add-btn': 'addChannels',
            'click #set-channel-start-btn': 'startTask',
            'click #set-channel-stop-btn': 'stopTask',
            'change #taskProfileId': 'taskProfileChanged',
            'change #device': 'serverChanged',
            'change #gpuIndex': 'gpuIndexChanged',
            'change #wall-position-output-selector': 'outputTypeChanged',
            'click #device-binding': 'bindOps',
            'click #device-unbinding': 'unbindOps',
            'click #save-wall-position-output': 'saveOutput',
            'click #apply-screen-message': 'setScreenMessage',
            'click #select-layout-template': 'selectLayoutTemplate'

        },
        serializeData: function () {
            var data = Marionette.LayoutView.prototype.serializeData.apply(this, arguments);
            if (this.model.currentSchema) {
                data.template = this.model.currentSchema.get('template');
            }
            return data;
        },
        showActiveSchema: function () {
            this.showChildView('current', new ScreenSchemaView({model: this.model.currentSchema}));
        },
        onRender: function () {
            this.showChildView('current', new ScreenSchemaView({model: this.model.currentSchema}));
            this.showChildView('tabs', new ScreenSchemaTabsView({
                collection: this.model.get('schemas'),
                screen: this.model
            }))
            this.setTaskProfile();
            this.setDeviceList();
            this.setTaskStatus();
        },
        load: function (model) {
            if (this.model) {
                this.stopListening(this.model);
            }
            this.model = model;
            this.listenTo(this.model, "change:status", this.setTaskStatus);
            this.listenTo(this.model, "change:outputAddr", this.setOutput);
            this.listenTo(this.model, "change:outputAddr2", this.setOutput);
            this.startSyncStatus();
            this.render();
        },
        showLoading: function () {
            $("#loading-dialog").render("loadingTemplate").modal({
                showClose: false,
                clickClose: false
            });
        },
        editMessageStyle: function () {
            Backbone.trigger("edit-message-style:clicked", this.model.get('id'), this.model.get('style'));
        },
        addChannels: function () {
            Backbone.trigger("add-channels:clicked", this.model.currentSchema.get('id'), this.getChildView('current').currentGroup - 1);
        },
        getUsedTaskProfileId: function () {
            return sv.ajax.getJSON('/task/screen/usedTaskProfile/' + this.model.get('id'));
        },
        getTaskProfileItems: function () {
            return sv.ajax.getJSON('/profile/task/list');
        },
        getTask: function () {
            return sv.ajax.getJSON("/task/getScreenTask", {screenId: this.model.get('id')});
        },
        getAliveServer: function () {
            return sv.ajax.getJSON('/device/al/' + this.model.wallId);
        },
        setActiveSchema: function (screenId, schemaId) {
            return sv.ajax.post("/screen/activeSchema", {screenId: screenId, schemaId: schemaId});
        },
        setTaskProfile: function () {
            var $taskProfileContainer = this.$el.find("#task-profile-container"),
                that = this,
                renderData = {};

            this.getUsedTaskProfileId().then(function (data) {
                renderData.selectedTaskProfileId = data.r;
                return that.getTaskProfileItems();
            }).then(function (data) {
                renderData.taskProfiles = data;
            }).done(function () {
                $taskProfileContainer.render("taskProfileTemplate", renderData);


            });
        },
        setDeviceList: function () {
            var $deviceContainer = this.$el.find("#device-container"),
                that = this,
                renderData = {};

            this.getTask().then(function (data) {
                var task = data.r || {};
                renderData.selectedDeviceId = task['serverId'] || '';
                renderData.selectedGpuIndex = task['gpudIndex'];
                return that.getAliveServer();
            }).then(function (data) {
                renderData.devices = data.r;
            }).done(function () {
                $deviceContainer.render("deviceListTemplate", renderData);
                var status = $("#screen-status").val();
                if (status == 'RUNNING') {

                    $("#taskProfileId").attr("disabled", true);
                    $("#device").attr("disabled", true);
                    $("#gpuIndex").attr("disabled", true);
                    $("#schema-group-count").attr("disabled", true);
                    $("#schema-switch-time").attr("disabled", true);
                }
            });
        },
        setTaskStatus: function () {
            var status = this.model.get('status');
            $("#screen-status").val(status)
            if (status == 'RUNNING') {
                this.$el.find("#set-channel-start-btn").hide();
                this.$el.find("#set-channel-stop-btn").show();
                this.$el.find("#set-channel-disconnected-btn").hide();
                this.$el.find("#task-disconnected-message").hide();

                $("#select-layout-template").attr("disabled", true);


            } else if (status == 'DISCONNECTED') {
                this.$el.find("#set-channel-start-btn").hide();
                this.$el.find("#set-channel-stop-btn").hide();
                this.$el.find("#set-channel-disconnected-btn").show();
                this.$el.find("#task-disconnected-message").show();

            } else {
                this.$el.find("#set-channel-start-btn").show();
                this.$el.find("#set-channel-stop-btn").hide();
                this.$el.find("#set-channel-disconnected-btn").hide();
                this.$el.find("#task-disconnected-message").hide();

            }
        },
        setOutput: function () {
            this.$el.find("#output-stream-address").text(this.model.get("outputAddr") + " " + this.model.get("outputAddr2"));
        },
        setBinding: function (id, name) {
            if (!id) {
                this.$el.find("#binding-name").text("");
                this.$el.find("#device-unbinding").hide();
                this.$el.find("#device-binding").show();
            } else {
                this.$el.find("#binding-name").text(name);
                this.$el.find("#device-unbinding").show();
                this.$el.find("#device-binding").hide();
            }
            this.$el.find("#wall-position-output").val("");
        },
        setPid: function (pid) {
            this.$el.find("#screen-task-pid").text(pid);
        },
        setStyle: function (style) {
            this.model.set('style', style);
        },
        clearAndHideTaskProfileErrorContainer: function (errorContainer) {
            errorContainer || (errorContainer = this.$el.find('#error-container'));
            errorContainer.text("").hide();
        },
        setScreenMessage: function () {
            sv.ajax.post("screen/updateScreenMessage", {
                screenId: this.model.get('id'),
                message: this.$el.find("#screen-message-text").val()
            }, function () {
                $.toast({
                    text: "设置成功",
                    textAlign: 'center',
                    position: 'mid-center',
                    stack: false,
                    hideAfter: 1500,
                });
            });
        },
        startTask: function () {
            var $errorContainer = this.$el.find("#error-container"),
                $taskProfileContainer = this.$el.find("#task-profile-container"),
                taskProfileId = $taskProfileContainer.find("#taskProfileId").val(),
                $deviceContainer = this.$el.find("#device-container"),
                deviceId = $deviceContainer.find("#device").val(),
                GpuIndex = $deviceContainer.find("#gpuIndex").val(),
                output = this.$el.find("#wall-position-output-container").find("#wall-position-output").val();
            var selector = $("#wall-position-output-selector").val();  //1 ops 2 固定地址
            var that = this;
            var flag = true;
            if (selector == 1) {
                if ($("#binding-name").text() == "") {
                    $errorContainer.text("请选择输出方式").show();
                    return;
                }
            } else {
                if (output == "") {
                    $errorContainer.text("请选择输出方式").show();
                    return;
                } else {
                    $.ajax({
                        type: "post",
                        url: "/screen/updateOutput",
                        data: {"wallPosition": this.model.get('wallPosition'), "output": output},
                        async: false,
                        success: function (data) {
                            /*that.model.set('opsId', "");
                            that.model.set('opsIp', "");
                            that.$el.find("#binding-name").text("");
                            that.$el.find("#device-unbinding").hide();
                            that.$el.find("#device-binding").show();
                            $.toast({
                                text: "设置成功",
                                textAlign: 'center',
                                position: 'mid-center',
                                stack: false,
                                hideAfter: 1500,
                            });*/
                        }, error: function (jqXhr) {
                            sv.prompt.error({message: that.codeMessage[sv.ajax.getErrorCode(jqXhr)]});
                            that.$el.find("#wall-position-output").val(that.model.get('output'));
                            flag = false;
                        }

                    });
                    /* sv.ajax.post("/screen/updateOutput",async:false, {"wallPosition": this.model.get('wallPosition'), "output": output})
                     .then(function () {
                     that.model.set('opsId', "");
                     that.model.set('opsIp', "");
                     that.$el.find("#binding-name").text("");
                     that.$el.find("#device-unbinding").hide();
                     that.$el.find("#device-binding").show();
                     $.toast({
                     text: "设置成功",
                     textAlign: 'center',
                     position: 'mid-center',
                     stack: false,
                     hideAfter: 1500,
                     });
                     }).fail(function (jqXhr) {
                     sv.prompt.error({message: that.codeMessage[sv.ajax.getErrorCode(jqXhr)]});
                     that.$el.find("#wall-position-output").val(that.model.get('output'));
                     return;
                     });*/
                }
            }
            if (taskProfileId == -1) {
                $errorContainer.text("请选择任务模版").show();
                return;
            } else {
                this.clearAndHideTaskProfileErrorContainer($errorContainer);
            }
            if (flag) {
                var screenId = this.model.get('id');
                var asyncChain = this.setActiveSchema(screenId, this.model.currentSchema.get('id'));
                this.showLoading();
                asyncChain.then(function () {
                    return sv.ajax.post('/task/screen/start', {
                        screenId: screenId,
                        taskProfileId: taskProfileId,
                        serverId: deviceId,
                        gpuIndex: GpuIndex
                    });
                }).then(function (json) {
                    $.modal.close();
                    that.model.set('outputAddr', json.url);
                    that.model.set('status', "RUNNING");
                    if (json.mobile) {
                        that.model.set('outputAddr2', json.mobile);
                        sv.prompt.succeed4("启动成功,输出地址为：\n" + json.url + "\n" + json.mobile);
                    } else {
                        that.model.set('outputAddr2', "");
                        sv.prompt.succeed3("启动成功,输出地址为：" + json.url);
                    }
                    //button disable
                    $("#select-layout-template").attr("disabled", true);
                    $("#taskProfileId").attr("disabled", true);
                    $("#device").attr("disabled", true);
                    $("#gpuIndex").attr("disabled", true);
                    $("#schema-group-count").attr("disabled", true);
                    $("#schema-switch-time").attr("disabled", true);
                }).fail(function (jqXhr) {
                    $.modal.close();
                    that.model.set('status', "ERROR");
                    sv.prompt.error2(that.codeMessage[jqXhr['responseJSON'].code]);

                });
            }

        },
        stopTask: function () {
            var that = this;
            this.showLoading();
            sv.ajax.post('/task/screen/stop', {screenId: this.model.get('id')})
                .then(function () {
                    sv.prompt.succeed({message: "停止成功"});
                    that.model.set('outputAddr', "");
                    that.model.set('outputAddr2', "");
                    that.model.set('status', "STOP");
                    //button disable
                    $("#select-layout-template").attr("disabled", false);
                    $("#taskProfileId").attr("disabled", false);
                    $("#device").attr("disabled", false);
                    $("#gpuIndex").attr("disabled", false);
                    $("#schema-group-count").attr("disabled", false);
                    $("#schema-switch-time").attr("disabled", true);
                }).fail(function (jqXhr) {
                sv.prompt.error({message: that.codeMessage[sv.ajax.getErrorCode(jqXhr)]});
            }).always(function () {
                $.modal.close();
            });
        },
        startSyncStatus: function () {
            this.stopSyncStatus();
            if (this.model.get('id') > 0) {
                var that = this;
                this.statusSyncId = setInterval(function () {
                    if (that.model.get('id') > 0) {
                        sv.ajax.getJSON("/task/getScreenTask", {screenId: that.model.get('id')}, function (data) {
                            if (data && data.code == 0 && data.r && that.model.get('id') == data.r["referenceId"]) {
                                that.model.set("status", data.r.status);
                                that.setPid(data.r.pid);
                            }
                        });
                    } else {
                        that.stopSyncStatus();
                    }
                }, 2000);
            }
        },
        stopSyncStatus: function () {
            if (this.statusSyncId != -1) {
                clearInterval(this.statusSyncId);
                this.statusSyncId = -1;
            }
        },
        saveOutput: function () {
            var output = this.$el.find("#wall-position-output").val();
            var that = this;
            sv.ajax.post("/screen/updateOutput", {"wallPosition": this.model.get('wallPosition'), "output": output})
                .then(function () {
                    that.model.set('opsId', "");
                    that.model.set('opsIp', "");
                    that.$el.find("#binding-name").text("");
                    that.$el.find("#device-unbinding").hide();
                    that.$el.find("#device-binding").show();
                    $.toast({
                        text: "设置成功",
                        textAlign: 'center',
                        position: 'mid-center',
                        stack: false,
                        hideAfter: 1500,
                    });
                }).fail(function (jqXhr) {
                sv.prompt.error({message: that.codeMessage[sv.ajax.getErrorCode(jqXhr)]});
                that.$el.find("#wall-position-output").val(that.model.get('output'));
            });
        },
        taskProfileChanged: function (event) {
            if ($(event.currentTarget).val() != -1) {
                this.clearAndHideTaskProfileErrorContainer();
                var $deviceContainer = this.$el.find("#device-container"),
                    deviceId = $deviceContainer.find("#device").val(),
                    GpuIndex = $deviceContainer.find("#gpuIndex").val();
                /*  sv.ajax.post("screen/updateTaskProfile", {screenId: this.model.get('id'), taskProfileId: $(event.currentTarget).val(),serverId: deviceId}, function() {
                 });*/
                this.updateTaskParam(this.model.get('id'), $(event.currentTarget).val(), deviceId, GpuIndex);
            }
        },
        serverChanged: function (event) {
            var deviceId = $(event.currentTarget).val();
            var $taskProfileContainer = this.$el.find("#task-profile-container"),
                taskProfileId = $taskProfileContainer.find("#taskProfileId").val();
            var $deviceContainer = this.$el.find("#device-container"),
                GpuIndex = $deviceContainer.find("#gpuIndex").val();
            /*   sv.ajax.post("screen/updateTaskProfile", {screenId: this.model.get('id'), taskProfileId: taskProfileId,serverId: deviceId}, function() {
             });*/
            this.updateTaskParam(this.model.get('id'), taskProfileId, deviceId, GpuIndex);
        },
        gpuIndexChanged: function (event) {
            var GpuIndex = $(event.currentTarget).val();
            var $taskProfileContainer = this.$el.find("#task-profile-container"),
                taskProfileId = $taskProfileContainer.find("#taskProfileId").val();
            var $deviceContainer = this.$el.find("#device-container"),
                deviceId = $deviceContainer.find("#device").val();
            /*     sv.ajax.post("screen/updateTaskProfile", {screenId: this.model.get('id'), taskProfileId: taskProfileId,serverId: deviceId}, function() {
             });*/
            this.updateTaskParam(this.model.get('id'), taskProfileId, deviceId, GpuIndex);
        },
        outputTypeChanged: function (event) {
            var selected_output = $(event.currentTarget).val();
            if (selected_output == 1) {
                this.$el.find("#binding-container").show();
                this.$el.find("#wall-position-output-container").hide();
            } else if (selected_output == 2) {
                this.$el.find("#binding-container").hide();
                this.$el.find("#wall-position-output-container").show();
            }
        },
        templateChanged: function (event) {
            var that = this;
            sv.ajax.post("screen/updateSchemaTemplate", {
                schemaId: this.model.currentSchema.get('id'),
                template: $(event.currentTarget).val()
            }, function (schema) {
                that.model.currentSchema.set(schema);
            });
        },
        selectLayoutTemplate: function () {
            Backbone.trigger('selectLayoutTemplate:clicked', this.model.currentSchema.get('id'), this.model.currentSchema.get('template'));
        },
        bindOps: function () {
            Backbone.trigger('bindops:clicked', this.model.get('wallPosition'));
        },
        unbindOps: function () {
            Backbone.trigger('unbindops:clicked', this.model.get('wallPosition'));
        },
        updateCurrentSchema: function (schema) {
            this.model.currentSchema.set(schema);
        },
        updateTaskParam: function (screenId, taskProfileId, deviceId, gpuIndex) {
            sv.ajax.post("screen/updateTaskProfile", {
                screenId: screenId,
                taskProfileId: taskProfileId,
                serverId: deviceId,
                gpuIndex: gpuIndex
            }, function () {
            });
        },
        initialize: function (options) {
            this.listenTo(Backbone, 'bindOps:finish', this.setBinding);
            this.listenTo(Backbone, 'unbindOps:finish', this.setBinding);
            this.listenTo(Backbone, 'style:updated', this.setStyle);
            this.listenTo(Backbone, 'active:changed', this.showActiveSchema);
            this.listenTo(Backbone, 'currentSchema:updated', this.updateCurrentSchema);
        }
    });

    return {
        ScreenModel: ScreenModel,
        ScreenLayoutView: ScreenLayoutView,
        LayoutTemplateListView: LayoutTemplateListView,
        LayoutTemplateCollection: LayoutTemplateCollection
    };

}(jQuery, _, Backbone, Marionette, window.sv));

function ScreenManager(opsList) {
    this.opsList = opsList;
    this.screenId = -1;
    this.wallId = -1;
    this.opsIds = {};
    this.sdiIds = {};
    this.moved = false;
    this.codeMessage = {
        "1": "当前纪录不存在",
        "100": "系统处理出现错误",
        "103": "当前记录已被其他用户锁定",
        "104": "当前无可用的服务器",
        "105": "频道设置参数不存在或参数错误",
        "106": "屏幕墙位置不存在",
        "107": "OPS设备不存在",
        "113": "输出地址格式有误，正确的格式为udp://ip:port",
        "114": "该输出地址已经被使用，请重新输入",
        "115": "转码器无法启动任务，请检查参数设置",
        "7": "当前屏幕已被删除,请刷新页面重新操作",
        "108": "操作超时",
        "1001": "屏幕墙名称已存在",
        "1101": "当前屏幕墙正在运行，无法调换",
        "1102": "当前设置已更改，请刷新后再操作",
        "1103": "当前屏幕已被删除,请刷新页面重新操作",
        "1104": "正在运行的屏幕墙单元不能删除，请停止任务后再操作",
        "2101": "指定的频道不存在",
        "6003": "转码服务器未正确初始化，请重新初始化或添加",
        "7001": "RTSP发布url未配置",
        "7002": "RTSP存储目录未配置",
        "7003": "RTSP服务器地址未配置"
    };
    this.statusSyncId = -1;
}

ScreenManager.prototype = {
    init: function () {
        var _this = this;
        sv.nav.active("tb1");

        $("#screen-settings .body").hide();

        sv.ajax.getJSON("/screen/channels", function (channels) {
            var collection = new ChannelList.ChannelCollection(channels);
            _this.singleChannelListView = new ChannelList.SingleSelectionChannelListView({collection: collection});
            _this.singleChannelListView.render();
            var multipleCollection = new ChannelList.ChannelCollection(channels);
            _this.multipleChannelListView = new ChannelList.MultipleSelectionChannelListView({collection: multipleCollection});
            _this.multipleChannelListView.render();

        });

        sv.ajax.getJSON("/screen/templates", function (templates) {
            var collection = new Screen.LayoutTemplateCollection(templates);
            _this.layoutTemplateListView = new Screen.LayoutTemplateListView({collection: collection});
            _this.layoutTemplateListView.render();
        });

        _this.screenLayoutView = new Screen.ScreenLayoutView();

        sv.ajax.getJSON("/screen/opsServers", function (opsServers) {
            $("#device-list").render("opsTemplate", {"opsServers": opsServers});
            $("#tab-wall-setting").find("input:checkbox").on("change", $.proxy(function () {

            }, _this));
        });

        sv.ajax.getJSON("/screen/sdiOutputs", function (sdiOutputs) {
            $("#sdi-list").render("sdiTemplate", {"sdiOutputs": sdiOutputs});
        });

        $("#type").on("change", function () {
            if ($(this).val() == 1) {
                $("#sdi-list").hide();
                $("#device-list").show();
            } else if ($(this).val() == 2) {
                $("#sdi-list").show();
                $("#device-list").hide();
            }
        });

        $("#wall-add-btn").click(function (event) {
            event.preventDefault();
            _this.setWallSettings();
            $("#add-wall-dialog")
                .cleanTipsyOnModalClose()
                .modal({showClose: false, clickClose: false});
        })
        //修改组别名
        $("#saveScreenName").on("click", function () {
            var wallId = $("#wallId").val()
            var rowAndColumn = $("#screenName").val()
            var settingScreenName = $("#settingScreenName").val()
            var data = {
                "wallId": wallId, rowAndColumn: rowAndColumn, settingScreenName: settingScreenName
            }
            //data.push("screenId":screenId,screenName:screenName,settingScreenName:settingScreenName)
            sv.ajax.post("/screen/settingScreenName", data).then(function (id) {

                _this.reloadWalls(id);
            })
        })

        $("#add-wall-dialog").on("click", "#add-wall-cancel-btn", $.proxy(function () {
            if (_this.wallId != -1) {
                _this.setWallSettings($("#screen-wall-lists-nav li[data-id='" + _this.wallId + "']").data());
            }
            $.clearValidateError($("#wall-setting-form"));
            $.modal.close();
        }, this)).on("click", "#add-wall-ok-btn", $.proxy(function () {
            this.saveOrUpdateWall($("#wall_type").val());
        }, this)).on("keypress", $.proxy(function (event) {
            if (event.which == 13) {
                $("#add-wall-ok-btn").trigger('click');
                event.preventDefault();
            }
        }, this));

        this.reloadWalls();

        $("#screen-wall-lists-nav").niceScroll();

        $("#screen-wall-lists-nav").on("click", "li", function () {
            var top = ($(this).prevAll().length + 1) * $(this).outerHeight(false) - $("#screen-wall-lists-nav").height();
            if (top > $("#screen-wall-lists-nav").scrollTop()) {
                $("#screen-wall-lists-nav").scrollTop(top);
            }
            _this.screenLayoutView.stopSyncStatus();
            _this.setSelected($(this));
        }).on("click", ".remove-btn", function (event) {

            var id = $(this).closest("li").data("id");
            var flag = true;
            $.ajax({
                type: "GET",
                url: "allScreenByWallId",
                data: {"wallId": id},
                async: false,
                success: function (result) {
                    for (var i = 0, l = result.length; i < l; i++) {
                        if (result[i].status == "RUNNING") {
                            alert("正在运行的屏幕墙不能删除");
                            flag = false;
                        }
                    }

                }
            });

            if (flag) {
                event.stopPropagation();
                sv.prompt.showConfirm({
                    message: '确认删除该屏幕墙吗？', okFunc: function () {
                        _this.removeWall(id);
                    }
                });
            }
        }).on("click", ".edit-btn", function (event) {
            event.stopPropagation();
            _this.setWallSettings($(this).closest("li").data());
            $("#add-wall-dialog")
                .cleanTipsyOnModalClose()
                .modal({showClose: false, clickClose: false});
        });


        $("#rowCount").on("input", $.proxy(function () {
            $("#wall-setting-form").valid();
            if (!$("#rowCount").hasClass("error") && !$("#columnCount").hasClass("error")) {
                this.changeSmallGrid($("#rowCount").val(), $("#columnCount").val());
            }
        }, this));

        $("#columnCount").on("input", $.proxy(function () {
            $("#wall-setting-form").valid();
            if (!$("#rowCount").hasClass("error") && !$("#columnCount").hasClass("error")) {
                this.changeSmallGrid($("#rowCount").val(), $("#columnCount").val());
            }
        }, this));

        $("#position-screen-wall-recognize-btn").click(function () {
            _this.recognizeOps();
        });

        $('#set-wall-tab.tabs .tab-links a').on('click', function (e) {
            var $clicked = $(this);

            _this.tabClicked($clicked);

            e.preventDefault();
        });

        $('#set-channel-tab.tabs .tab-links a').on('click', function (e) {
            _this.schemaActiveHandle($(this));
            e.preventDefault();
        }).on('dblclick', function (e) {
            $(this).hide();
            $(this).siblings().filter("input").show().focus().val($(this).html());
            e.preventDefault();
        });

        $("#set-channel-tab.tabs .tab-links input").on("blur", function (e) {
            var $input = $(this);
            $(this).closest("form").validate();
            if ($(this).closest("form").valid()) {
                $(this).hide();
                $(this).siblings().filter("a").show();
                sv.ajax.post("/screen/updateSchemaName", {
                    schemaId: $(this).data("id"),
                    name: $(this).val()
                }, function () {
                    $input.siblings().filter("a").html($input.val());
                })
            }
            e.preventDefault();
        }).on("keypress", function (e) {
            if (e.which == 13) {
                $(this).blur();
                e.preventDefault();
            }
        }).hide();

        Backbone.listenTo(Backbone, 'addchannel:failed', function (jqXhr) {
            sv.prompt.error({message: _this.codeMessage[sv.ajax.getErrorCode(jqXhr)]});
        })
        Backbone.listenTo(Backbone, 'addchannelbundle:failed', function (jqXhr) {
            sv.prompt.error({message: _this.codeMessage[sv.ajax.getErrorCode(jqXhr)]});
        });
        Backbone.listenTo(Backbone, 'edit-message-style:clicked', function (screenId, style) {
            if (!_this.editStyleModal) {
                _this.editStyleModal = new MessageStyle.MessageStylePreview();
                _this.editStyleModal.render();
            }
            _this.editStyleModal.openModal(screenId, new MessageStyle.MessageStyleModel(style));
        })
        Backbone.listenTo(Backbone, 'add-channels:clicked', function (schemaId, group) {
            _this.multipleChannelListView.openModal(schemaId, group);
        })
        Backbone.listenTo(Backbone, 'bindops:clicked', function (wallPosition) {
            _this.opsList.bind(wallPosition);
        });

        Backbone.listenTo(Backbone, 'unbindops:clicked', function (wallPosition) {
            _this.opsList.unbind(wallPosition);
        });

        Backbone.listenTo(Backbone, 'position:clicked', function (channelId, schemaId, row, column, groupIndex) {
            _this.singleChannelListView.openModal(channelId, schemaId, row, column, groupIndex);
        });

        Backbone.listenTo(Backbone, 'selectLayoutTemplate:clicked', function (schemaId, layouttemplate) {
            _this.layoutTemplateListView.openModal(schemaId, layouttemplate);
        });

        $(".tabs .tab-links a[href='#tab-screen-setting']").trigger("click");
    },
    saveWall: function () {
        var _this = this;
        var wallName = $("#add-wall-dialog").find("#wallName").val();
        var wallForm = $("#wallForm");
        var validator = wallForm.validate({
            messages: {
                wallName: "请输入屏幕墙名称"
            }
        });
        if (wallForm.valid()) {
            sv.ajax.post("/screen/saveWall", {"name": wallName}).then(function (id) {
                _this.reloadWalls(id);
                $.modal.close();
            }).fail(function () {
                validator.showErrors({
                    "wallName": "该名称已存在"
                });
            });
        }
    },
    saveOrUpdateWall: function (obj) {
        var _this = this;
        if (_this.checkWallSettingChanged()) {
            var wallSettingForm = $("#wall-setting-form");
            if (wallSettingForm.valid()) {
                var data = wallSettingForm.serializeArray();
                var ops = [];
                $("input[name='opsServer']:checked").each(function () {
                    ops.push($(this).val());
                });
                var opsparam = {name: "opsIds", value: ops};
                data.push(opsparam);
                var sdi = [];
                $("input[name='sdiOutput']:checked").each(function () {
                    sdi.push($(this).val());
                });
                var sdiparam = {name: "sdiIds", value: sdi};
                data.push(sdiparam);
                if (obj == "add") {
                    sv.ajax.post("/screen/addWall", data).then(function (wall) {
                        $.modal.close();
                        _this.reloadWalls(wall.id);
                    }).fail(function (jqXhr) {
                        $("#wall-setting-form").validate().showErrors({
                            "name": _this.codeMessage[sv.ajax.getErrorCode(jqXhr)]
                        })
                    });
                } else if (obj == "update") {
                    sv.ajax.post("/screen/updateWall", data).then(function (wall) {
                        $.modal.close();
                        _this.reloadWalls(wall.id);
                    }).fail(function (jqXhr) {
                        $("#wall-setting-form").validate().showErrors({
                            "name": _this.codeMessage[sv.ajax.getErrorCode(jqXhr)]
                        })
                    });

                }
            }
        } else {
            $.modal.close();
        }
    },
    reloadWalls: function (desWallId) {
        var _this = this;
        sv.ajax.getJSON("/screen/walls", function (walls) {
            $("#screen-wall-lists-nav")
                .render("wallTemplate", {"walls": walls});
            $.each(walls, function (index, wall) {
                _this.opsIds[wall.id] = wall.opsIds;
                _this.sdiIds[wall.id] = wall.sdiIds;
            });
            var $walls = $("#screen-wall-lists-nav");
            if ($walls.children().length > 0) {
                var id = 0;
                if (_this.wallId == -1) {
                    id = $walls.children().eq(0).data("id");
                } else {
                    id = _this.wallId;
                }
                if (desWallId) {
                    $selected = $walls.find("li[data-id='" + desWallId + "']");
                } else {
                    $selected = $walls.find("li[data-id='" + id + "']");
                }
                $selected.trigger("click");
                $("#screen-settings .body").show();
            } else {
                $("#screen-setttings-title").html("");
                $("#screen-settings .body").hide();
            }
        });
    },
    removeWall: function (wallId) {
        var _this = this;
        sv.ajax.post("/screen/removeWall", {"id": wallId}).always(function () {
            _this.wallId = -1;
            _this.reloadWalls();
        });
    },
    setWallSettings: function (wall) {
        var _this = this;
        if (wall) {
            $("#add-wall-dialog .dialog-caption").html("修改屏幕墙");
            $("#version").val(wall.version);
            $("#id").val(wall.id);
            $("#name").val(wall.name);
            $("#type").val(wall.type).change();
            $("#rowCount").val(wall.rowcount);
            $("#columnCount").val(wall.columncount);
            $("#wall_type").val("update");

            _this.changeSmallGrid(wall.rowcount, wall.columncount);

            $("input[name='opsServer']").prop("checked", false);
            $.each(_this.opsIds[wall.id], function (index, opsId) {
                $("input[name='opsServer'][value='" + opsId + "']").prop("checked", true).prop("disabled", false);
            });
            $("#screen-wall-lists-nav li").filter("[data-id!='" + wall.id + "']").each(function () {
                $.each(_this.opsIds[$(this).data("id")], function (index, opsId) {
                    $("input[name='opsServer'][value='" + opsId + "']").prop("disabled", true);
                })
            });
            $("input[name='sdiOutput']").prop("checked", false);
            $.each(_this.sdiIds[wall.id], function (index, sdiId) {
                $("input[name='sdiOutput'][value='" + sdiId + "']").prop("checked", true).prop("disabled", false);
            });
            $("#screen-wall-lists-nav li").filter("[data-id!='" + wall.id + "']").each(function () {
                $.each(_this.sdiIds[$(this).data("id")], function (index, sdiId) {
                    $("input[name='sdiOutput'][value='" + sdiId + "']").prop("disabled", true);
                })
            });
        } else {
            $("#add-wall-dialog .dialog-caption").html("添加屏幕墙");
            $("#version").val("0");
            $("#id").val("-1");
            $("#name").val("");
            $("#type").val("1").change();
            $("#rowCount").val("1");
            $("#columnCount").val("1");
            $("#wall_type").val("add");

            _this.changeSmallGrid(1, 1);

            $("input[name='opsServer']").prop("checked", false);
            $("#screen-wall-lists-nav li").each(function () {
                $.each(_this.opsIds[$(this).data("id")], function (index, opsId) {
                    $("input[name='opsServer'][value='" + opsId + "']").prop("disabled", true);
                })
            });
            $("input[name='sdiOutput']").prop("checked", false);
            $("#screen-wall-lists-nav li").each(function () {
                $.each(_this.sdiIds[$(this).data("id")], function (index, sdiId) {
                    $("input[name='sdiOutput'][value='" + sdiId + "']").prop("disabled", true);
                })
            });
        }

        $("#wall-setting-form").validate({
            rules: {
                name: "required",
                rowCount: {
                    required: true,
                    digits: true,
                    max: 5,
                    min: 1
                },
                columnCount: {
                    required: true,
                    digits: true,
                    max: 10,
                    min: 1
                }
            },
            messages: {
                rowCount: {
                    max: "行数不能超过5",
                    min: "行数不能小于1"
                },
                columnCount: {
                    max: "列数不能超过10",
                    min: "列数不能小于1"
                }
            }
        });
    },

    loadWall: function (wall) {
        var _this = this;
        $("#screen-setttings-title").html(wall.name)

        _this.wallId = wall.id;
        _this.setWallSettings(wall);

        $("#tab-link-screen-setting").find("a").trigger("click");

        _this.resetWallPosition(wall);

    },
    changeSmallGrid: function (row, column) {
        var $smallgrid = $("#small-grid");

        $smallgrid.find("*").remove();
        for (var i = 0; i < row; i++)
            for (var j = 0; j < column; j++) {
                var index = i * column + j;
                $smallgrid.append($("<div class='gridcell'></div>"));
            }
        $smallgrid.width($("#small-grid .gridcell").outerWidth(true) * column);
    },
    resetWallPosition: function (wall) {
        var _this = this;
        var $grid = $("#grid"), $previewholder = $("<div class='previewholder'></div>"), row = wall.rowcount, column = wall.columncount;
        $grid.find("*").remove();
        $("#screenName").find("*").remove();
        $("#screenName").append('  <option value="-1" selected="selected">请选择</option>');

        $.ajax({
            type: "GET",
            url: "/screen/getScreenName",
            data: {"wallId": wall.id},
            async: false,
            success: function (result) {
                for (var i = 0; i < result.length; i++) {
                    var WallPosition = result[i];
                    $grid.append($("<div class='gridcell draggable' data-row='" + WallPosition.row + "' data-column='" + WallPosition.column + "'></div>").append("<span>" + WallPosition.wallName + "</span>"))
                    $("#screenName").append('<option value="' + WallPosition.row + WallPosition.column + '">' + WallPosition.wallName + '</option>')
                }

            }
        });

        /*for (var i = 0; i < row; i++)
         for (var j = 0; j < column; j++) {
         var index = i * column + j + 1;
         $grid.append($("<div class='gridcell draggable' data-row='" + i + "' data-column='" + j + "'></div>").append("<span>" + index + "</span>"))
         $("#screenName").append('<option value="'+i+j+'">'+index+'</option>')
         }*/
        $grid.width($("#grid .gridcell").outerWidth(true) * column);


        $("#grid .draggable").on("mousedown", function (e) {
            if (e.which == 1) {
                var $drag = $(this);
                var pox_y = $drag.offset().top - e.pageY,
                    pox_x = $drag.offset().left - e.pageX;

                $drag.addClass("dragged").offset({
                    top: pox_y + e.pageY,
                    left: pox_x + e.pageX
                }).after($previewholder);

                var $base = $previewholder;

                var downX = e.pageX;
                var downY = e.pageY;
                _this.moved = false;

                var mouseup = function (e) {
                    if (e.which == 1) {
                        if (!_this.moved) {
                            _this.loadScreen($("#tab-wall-setting #id").val(), $drag.data("row"), $drag.data("column"));
                            $("#tab-link-channel-setting").show().find("a").trigger("click");
                        }
                        _this.drop($drag, $previewholder);
                        $(".content-wrapper").off("mousemove").off("mouseup", mouseup).off("mouseleave", mouseup);
                    }
                    e.preventDefault();
                };

                $(".content-wrapper").on("mousemove", function (e) {
                    if (downX != e.pageX || downY != e.pageY) {
                        _this.moved = true;
                    }
                    var drag_y = pox_y + e.pageY,
                        drag_x = pox_x + e.pageX,
                        drag_w = $drag.width(),
                        drag_h = $drag.height(),
                        row_n,
                        column_n;

                    row_n = Math.floor((drag_y - $grid.offset().top) / drag_h);
                    row_n = row_n < 0 ? 0 : row_n;
                    row_n = row_n > row - 1 ? row - 1 : row_n;
                    column_n = Math.floor((drag_x - $grid.offset().left) / drag_w);
                    column_n = column_n < 0 ? 0 : column_n;
                    column_n = column_n > column - 1 ? column - 1 : column_n;

                    var index = row_n * column + column_n

                    var $cand = $drag.siblings().eq(index)
                    if (!$cand.is($previewholder)) {
                        var copy_base, copy_prev, copy_cand;
                        if (!$base.is($previewholder) && !$base.is($cand)) {
                            copy_base = $base.clone(true);
                            copy_prev = $previewholder.clone(true);
                            copy_cand = $cand.clone(true);
                            $previewholder.replaceWith(copy_base);
                            $cand.replaceWith(copy_prev);
                            $base.replaceWith(copy_cand);
                        } else {
                            copy_prev = $previewholder.clone(true);
                            copy_cand = $cand.clone(true);
                            $previewholder.replaceWith(copy_cand);
                            $cand.replaceWith(copy_prev);
                        }
                        $previewholder = copy_prev;
                        if ($base.is($cand)) {
                            $base = $previewholder;
                        } else {
                            $base = copy_cand;
                        }
                    }

                    $drag.offset({
                        top: drag_y,
                        left: drag_x
                    })

                }).on("mouseup", mouseup).on("mouseleave", mouseup)


            }
            e.preventDefault();
        }).on("contextmenu", function (e) {
            e.preventDefault();
        });
    },
    updateWallPositionOps: function () {
        var _this = this;
        if (_this.wallId != -1) {
            var seq = [];
            $("#grid .gridcell").each(function () {
                seq.push({row: $(this).data("row"), column: $(this).data("column")})
            });

            var data = {wallId: _this.wallId, opsIds: _this.opsIds[_this.wallId], positions: seq};
            sv.ajax.post("/screen/updateWallPositionOps", {"opsPositions": $.toJSON(data)})
                .fail(function (jqXhr) {
                    sv.prompt.error({message: _this.codeMessage[sv.ajax.getErrorCode(jqXhr)]});
                }).always(function () {
                _this.resetWallPosition($("#screen-wall-lists-nav ").find("li[data-id='" + _this.wallId + "']").data());
            });
        }
    },
    recognizeOps: function () {
        var _this = this;
        if (_this.wallId != -1) {
            sv.ajax.post("/screen/recognize", {wallId: _this.wallId}, function () {

            })
        }
    },
    setSelected: function ($li) {
        $li.siblings()
            .removeClass("selected")
            .end()
            .addClass("selected");
        this.loadWall($li.data())
    },
    loadScreen: function (wallId, row, column) {
        var _this = this,
            activeSchema = null;
        sv.ajax.getJSON("/screen/screen" + "?wallId=" + wallId + "&row=" + row + "&column=" + column, function (screen) {
            var model = new Screen.ScreenModel(screen, {wallId: wallId, parse: true});
            _this.screenLayoutView.load(model);
        });
    },
    showPid: function (pid) {
        $("#screen-task-pid").text(pid);
    },
    showOutputAddr: function (addr) {
        if (!addr) {
            $("#output-stream-address").text("");
        } else {
            $("#output-stream-address").text(addr);
        }
    },
    drop: function ($drag, $previewholder) {
        $drag.removeClass("dragged");
        $previewholder.after($drag).remove();
    },
    checkWallSettingChanged: function () {
        var _this = this;
        var wallId = $("#id").val();
        if (wallId == -1) {
            return true;
        } else {
            currentWall = $("#screen-wall-lists-nav ").find("li[data-id='" + wallId + "']").data();
            var changed = false;
            var name = $("#name").val();
            var type = $("#type").val();
            var rowCount = $("#rowCount").val();
            var columnCount = $("#columnCount").val();

            changed = name != currentWall.name
                || type != currentWall.type
                || rowCount != currentWall.rowcount
                || columnCount != currentWall.columncount;

            if (type == 1) {
                $("input[name='opsServer']:checked").each(function () {
                    changed = changed || ($.inArray($(this).val(), _this.opsIds[_this.wallId]) == -1);
                });
                $("input[name='opsServer']:unchecked").each(function () {
                    changed = changed || ($.inArray($(this).val(), _this.opsIds[_this.wallId]) != -1);
                });
            } else if (type == 2) {
                $("input[name='sdiOutput']:checked").each(function () {
                    changed = changed || ($.inArray(parseInt($(this).val()), _this.sdiIds[_this.wallId]) == -1);
                });
                $("input[name='sdiOutput']:unchecked").each(function () {
                    changed = changed || ($.inArray(parseInt($(this).val()), _this.sdiIds[_this.wallId]) != -1);
                })
            }
            return changed;
        }
    },
    tabClicked: function ($clicked) {
        var _this = this;
        var currentAttrValue = $clicked.attr('href');
        var $li = $clicked.closest('li');

        $('.tab-content ' + currentAttrValue).show().siblings().hide();

        $li.addClass('active').siblings().removeClass('active');

        if (!$li.is("#tab-link-channel-setting")) {
            $("#tab-link-channel-setting").hide();
            $("#screen-settings-debug-info").show();
        }

        if ($li.is("#tab-link-wall-setting") && _this.wallId != -1) {
            _this.setWallSettings($("#screen-wall-lists-nav li[data-id='" + _this.wallId + "']").data());
        }
    },

    setTaskProfile: function (amountRow, amountColumn) {
        var $taskProfileContainer = $('.tab-content #tab-channel-setting').find("#task-profile-container"),
            that = this,
            renderData = {},
            taskProfileIdEl = $taskProfileContainer.find('#taskProfileId');

        this.getUsedTaskProfileId().then(function (data) {
            renderData.selectedTaskProfileId = data.r;
            return that.getTaskProfileItems(amountRow, amountColumn);
        }).then(function (data) {
            renderData.taskProfiles = data;
        }).done(function () {
            $taskProfileContainer.render("taskProfileTemplate", renderData);
        });
    }
};

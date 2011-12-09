(function() {
  var TrackSearch;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };
  TrackSearch = (function() {
    function TrackSearch() {
      this.load = __bind(this.load, this);
      this.render = __bind(this.render, this);
      var self;
      self = this;
      this.focused = '';
      this.el = $('#track-search-results');
      this.template = _.template($('#track-result-template').html());
      $('#query').bind('ajax:success', this.render);
      $('body').bind('click', function(e) {
        if ($(e.srcElement).parents('form#search').length) {
          return true;
        }
        return self.el.hide();
      });
      $('#query').bind('focus', function(e) {
        return self.el.show();
      });
      $('#query').bind('keyup', this.load);
      this.el.find('li a').bind('click', function() {
        return self.el.hide();
      });
    }
    TrackSearch.prototype.render = function(e, tracks) {
      var html, self;
      self = this;
      html = '';
      _.each(tracks, function(track) {
        return html += self.template(track);
      });
      this.el.html(html);
      return this.el.show();
    };
    TrackSearch.prototype.load = function(e) {
      var $form, self, value;
      if (e.keyCode === 40) {
        return this.traverseList();
      }
      self = $(e.srcElement);
      $form = self.parent('form');
      value = self.val();
      if (!(value.length > 2)) {
        return this.reset();
      }
      return $.ajax({
        method: 'GET',
        url: $form.attr('action'),
        data: $form.serialize(),
        dataType: 'json',
        success: function(data, status, xhr) {
          return self.trigger('ajax:success', [data, status, xhr]);
        }
      });
    };
    TrackSearch.prototype.reset = function() {
      return this.el.html('');
    };
    TrackSearch.prototype.traverseList = function() {
      var self;
      if (!this.el.children().length) {
        return;
      }
      self = this;
      this.focused = this.el.find('li:first-child');
      this.el.bind('keydown', function(e) {
        if (e.keyCode !== 13) {
          return e.preventDefault();
        }
      });
      this.el.bind('keyup', function(e) {
        switch (e.keyCode) {
          case 40:
            self.focused = self.focused.next('li');
            return self.focused.find('a').focus();
          case 38:
            self.focused = self.focused.prev('li');
            if (self.focused.length === 1) {
              return self.focused.find('a').focus();
            } else {
              $('#query').focus();
              return self.el.unbind('keyup');
            }
        }
      });
      this.focused.find('a').focus();
      return false;
    };
    return TrackSearch;
  })();
  $(function() {
    return new TrackSearch;
  });
}).call(this);

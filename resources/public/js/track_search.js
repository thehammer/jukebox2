(function() {
  var TrackSearch;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };
  TrackSearch = (function() {
    function TrackSearch() {
      this.render = __bind(this.render, this);      this.el = $('#track-search-results');
      this.template = _.template($('#track-result-template').html());
      $('#query').bind('ajax:success', this.render);
      $('body').bind('click', function(e) {
        if ($(e.srcElement).parents('form#search').length) {
          return true;
        }
        return $('#track-search-results').hide();
      });
      $('#query').bind('focus', function(e) {
        return $('#track-search-results').show();
      });
      $('#query').bind('keyup', this.load);
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
      self = $(this);
      $form = self.parent('form');
      value = self.val();
      if (!(value.length > 2)) {
        return true;
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
    return TrackSearch;
  })();
  $(function() {
    return new TrackSearch;
  });
}).call(this);

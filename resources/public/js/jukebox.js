(function() {
  var Jukebox;
  Jukebox = (function() {
    function Jukebox() {
      _.templateSettings = {
        evaluate: /<%([\s\S]+?)%>/g,
        interpolate: /\{\{(.+?)\}\}/g
      };
      $('body').on('click', 'a[data-remote=true]', this.remote);
      $('body').on('submit', 'form[data-remote=true]', this.remote);
      $('.topbar').dropdown();
      Typekit.load();
    }
    Jukebox.prototype.remote = function(e) {
      var $el, data, method, methodOverride, url;
      e.preventDefault();
      data = {};
      $el = $(this);
      url = $el.attr('href') || $el.attr('action');
      if ($el.is('form')) {
        data = $el.serialize();
      }
      methodOverride = $el.attr('data-method');
      if ((methodOverride != null) && methodOverride !== 'GET') {
        method = 'POST';
        data['_method'] = $el.attr('data-method');
      } else {
        method = $el.attr('method') || 'GET';
      }
      return $.ajax({
        type: method,
        url: url,
        data: data,
        dataType: 'json',
        success: function(data, status, xhr) {
          return $el.trigger('ajax:success', [data, status, xhr]);
        }
      });
    };
    return Jukebox;
  })();
  $(function() {
    return new Jukebox;
  });
}).call(this);

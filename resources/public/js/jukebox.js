(function() {
  var Jukebox;
  Jukebox = (function() {
    function Jukebox() {
      _.templateSettings = {
        evaluate: /<%([\s\S]+?)%>/g,
        interpolate: /\{\{(.+?)\}\}/g
      };
      $('body').delegate('a[data-remote=true]', 'click', this.remote);
      $('.topbar').dropdown();
      Typekit.load();
    }
    Jukebox.prototype.remote = function(e) {
      var $el, method, url;
      e.preventDefault();
      $el = $(this);
      url = $el.attr('href');
      method = $el.data('method') || 'GET';
      return $.ajax({
        method: method,
        url: url,
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

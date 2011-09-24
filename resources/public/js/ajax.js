(function() {
  var Ajax;
  Ajax = (function() {
    function Ajax(settings) {
      this.settings = settings;
    }
    Ajax.prototype.upload = function(file) {
      var data, xhr;
      xhr = new XMLHttpRequest();
      xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
          if (xhr.status < 300) {
            return this.settings.element.trigger('ajax:success', [xhr, xhr.responseText]);
          } else {
            return this.settings.element.trigger('ajax:error', xhr);
          }
        }
      };
      xhr.open('POST', '/library/upload/cory');
      data = new FormData();
      data.append("file", file);
      return xhr.send(data);
    };
    return Ajax;
  })();
  window.Ajax = Ajax;
}).call(this);

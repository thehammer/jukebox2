(function() {
  var Uploader;
  Uploader = (function() {
    function Uploader(settings) {
      this.settings = settings;
    }
    Uploader.prototype.send = function(file, $element) {
      var data, xhr;
      xhr = new XMLHttpRequest();
      xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
          if (xhr.status < 300) {
            return $element.trigger('ajax:success', [xhr, xhr.responseText]);
          } else {
            return $element.trigger('ajax:error', xhr);
          }
        }
      };
      xhr.open('POST', '/library/upload/cory');
      data = new FormData();
      data.append("file", file);
      return xhr.send(data);
    };
    return Uploader;
  })();
  window.Uploader = Uploader;
}).call(this);

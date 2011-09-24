(function() {
  var Uploader;
  Uploader = (function() {
    function Uploader(settings) {
      this.settings = settings;
    }
    Uploader.prototype.send = function(file, $element) {
      var data, event, xhr, _i, _len, _ref;
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
      if (xhr.upload != null) {
        _ref = ["abort", "error", "load", "loadstart", "progress"];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          event = _ref[_i];
          xhr.upload.addEventListener(event, function(p) {
            return $element.trigger("upload:" + event, [xhr, p]);
          }, false);
        }
      }
      xhr.open('POST', '/library/upload');
      data = new FormData();
      data.append("file", file);
      return xhr.send(data);
    };
    return Uploader;
  })();
  window.Uploader = Uploader;
}).call(this);

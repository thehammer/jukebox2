(function() {
  var Files;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };
  Files = (function() {
    function Files() {
      this.render = __bind(this.render, this);      this.validFiles = $('body').attr('data-accept');
      this.notification = new FileNotification('#notifications', '#file-notification');
      this.uploader = new Uploader({
        method: 'POST',
        url: '/library/upload'
      });
      document.addEventListener("dragenter", this.stopActions, false);
      document.addEventListener("dragexit", this.stopActions, false);
      document.addEventListener("dragover", this.stopActions, false);
      document.addEventListener("drop", this.stopActions, false);
      document.addEventListener("drop", this.render, false);
    }
    Files.prototype.isAcceptable = function(type) {
      return new RegExp(this.validFiles, 'gi').test(type);
    };
    Files.prototype.render = function(evt) {
      var $element, file, _i, _len, _ref, _results;
      _ref = evt.dataTransfer.files;
      _results = [];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        file = _ref[_i];
        if (!this.isAcceptable(file.type)) {
          continue;
        }
        $element = this.notification.render({
          name: file.name,
          size: this.sizeInMb(file.size)
        });
        _results.push(this.uploader.send(file, $element));
      }
      return _results;
    };
    Files.prototype.sizeInMb = function(size) {
      return Math.round(parseInt(size) / 1048576);
    };
    Files.prototype.stopActions = function(evt) {
      evt.stopPropagation();
      return evt.preventDefault();
    };
    return Files;
  })();
  $(function() {
    return new Files;
  });
}).call(this);

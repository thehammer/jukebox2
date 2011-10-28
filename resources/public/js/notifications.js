(function() {
  var FileNotification, Notifications;
  var __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  Notifications = (function() {
    function Notifications(root) {
      this.root = $(root);
      this.view = "";
    }
    Notifications.prototype.render = function() {
      this.root.append(this.view);
      return this.view;
    };
    Notifications.prototype.remove = function() {
      var $el;
      $el = $(this);
      return setTimeout(function() {
        return $el.fadeOut();
      }, 1000);
    };
    return Notifications;
  })();
  FileNotification = (function() {
    __extends(FileNotification, Notifications);
    function FileNotification(root, template) {
      FileNotification.__super__.constructor.call(this, root);
      this.template = _.template($(template).html());
      this.root.delegate('li', 'ajax:success', this.success);
      this.root.delegate('li', 'ajax:error', this.error);
      this.root.delegate('li', 'upload:progress', this.progress);
      this.root.delegate('li', 'notification:remove', this.remove);
    }
    FileNotification.prototype.render = function(data) {
      this.view = $(this.template({
        file: data
      }));
      return FileNotification.__super__.render.apply(this, arguments);
    };
    FileNotification.prototype.success = function(e, xhr, data) {
      return $(this).addClass('success').removeClass('uploading').trigger('notification:remove');
    };
    FileNotification.prototype.error = function(e, xhr) {
      return $(this).addClass('error').removeClass('uploading').trigger('notification:remove');
    };
    FileNotification.prototype.progress = function(e, xhr, progress) {
      var percent;
      if (progress.lengthComputable) {
        percent = (progress.loaded / progress.total) * 100;
        return $('.progress-bar', this).css({
          width: "" + percent + "%"
        });
      }
    };
    return FileNotification;
  })();
  window.FileNotification = FileNotification;
}).call(this);

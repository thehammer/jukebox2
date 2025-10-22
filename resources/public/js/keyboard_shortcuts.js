(function() {
  var KeyboardShortcuts;
  KeyboardShortcuts = (function() {
    function KeyboardShortcuts() {
      $('input, textarea').on('keyup', function(e) {
        if (e.keyCode !== 27) {
          e.stopPropagation();
        }
        return true;
      });
      $('body').on('keyup', function(e) {
        switch (e.keyCode) {
          case 32:
            return $('a.btn.play, a.btn.pause').trigger('click');
          case 39:
          case 78:
            return $('a.btn.skip').trigger('click');
          case 82:
            return $('#random').trigger('click');
          case 83:
            return $('#query').focus();
          case 27:
            return $(e.srcElement).blur();
        }
      });
    }
    return KeyboardShortcuts;
  })();
  $(function() {
    return new KeyboardShortcuts;
  });
}).call(this);

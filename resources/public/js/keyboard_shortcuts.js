(function() {
  var KeyboardShortcuts;
  KeyboardShortcuts = (function() {
    function KeyboardShortcuts() {
      $(document).bind('keyup', function(e) {
        switch (e.keyCode) {
          case 32:
            return $('a.btn.play, a.btn.pause').trigger('click');
          case 39:
          case 78:
            return $('a.btn.skip').trigger('click');
          case 82:
            return $('#random').trigger('click');
        }
      });
    }
    return KeyboardShortcuts;
  })();
  $(function() {
    return new KeyboardShortcuts;
  });
}).call(this);

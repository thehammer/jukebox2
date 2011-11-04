class KeyboardShortcuts

  constructor: ->
    $(document).bind('keyup', (e) ->
      switch e.keyCode
        when 32
          $('a.btn.play, a.btn.pause').trigger('click')
        when 39, 78
          $('a.btn.skip').trigger('click')
        when 82
          $('#random').trigger('click')
    )




$ -> new KeyboardShortcuts

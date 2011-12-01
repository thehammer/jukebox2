class KeyboardShortcuts

  constructor: ->
    $('input, textarea').bind('keyup', (e) ->
      e.stopPropagation() unless e.keyCode is 27
      return true
    )
    $('body').bind('keyup', (e) ->

      switch e.keyCode
        when 32
          $('a.btn.play, a.btn.pause').trigger('click')
        when 39, 78
          $('a.btn.skip').trigger('click')
        when 82
          $('#random').trigger('click')
        when 83
          $('#query').focus()
        when 27
          $(e.srcElement).blur()
    )




$ -> new KeyboardShortcuts

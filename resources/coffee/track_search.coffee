class TrackSearch

  constructor: ->
    self = this
    @focused = ''
    @loggedIn = $('.logged-in').length > 0
    @el = $('#track-search-results')
    @template = _.template $('#track-result-template').html()

    $('#query').bind('ajax:success', @render)

    $('body').bind 'click', (e) ->
      return true if $(e.srcElement).parents('form#search').length
      self.el.hide()

    $('#query').bind 'focus', (e) ->
      self.el.show()

    $('#query').bind 'keyup', @load

    @el.find('li a').bind 'click', ->
      self.el.hide()

  render: (e, tracks) =>
    self = this
    html = ''

    _.each tracks, (track) ->
      track.canAdd = self.loggedIn
      console.dir(track)
      html += self.template track

    @el.html(html)
    @el.show()

  load: (e) =>
    return @traverseList() if e.keyCode is 40
    self = $(e.srcElement)
    $form = self.parent('form')
    value = self.val()

    return @reset() unless value.length > 2

    $.ajax
      method: 'GET'
      url: $form.attr('action')
      data: $form.serialize()
      dataType: 'json'
      success: (data, status, xhr) ->
        self.trigger 'ajax:success', [data, status, xhr]

  reset: ->
    @el.html('')

  traverseList: ->
    if !@loggedIn or @el.children().length == 0
      return

    self = this
    @focused = @el.find('li:first-child')

    @el.bind 'keydown', (e) ->
      e.preventDefault() unless e.keyCode is 13

    @el.bind 'keyup', (e) ->
      switch e.keyCode
        when 40
          self.focused = self.focused.next('li')
          self.focused.find('a').focus()
        when 38
          self.focused = self.focused.prev('li')
          if self.focused.length is 1
            self.focused.find('a').focus()
          else
            $('#query').focus()
            self.el.unbind('keyup')


    @focused.find('a').focus()
    return false


$ -> new TrackSearch

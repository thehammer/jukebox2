class TrackSearch

  constructor: ->
    self = this
    @focused = ''
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
    return unless @el.children().length
    self = this
    @focused = @el.find('li:first-child')

    @el.bind 'keydown', (e) ->
      e.preventDefault()

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

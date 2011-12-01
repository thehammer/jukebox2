class TrackSearch

  constructor: ->
    @el = $('#track-search-results')
    @template = _.template $('#track-result-template').html()

    $('#query').bind('ajax:success', @render)
    $('body').bind 'click', (e) ->
      return true if $(e.srcElement).parents('form#search').length
      $('#track-search-results').hide()
    $('#query').bind 'focus', (e) ->
      $('#track-search-results').show()
    $('#query').bind 'keyup', (e) ->
      self = $(this)
      $form = self.parent('form')
      value = self.val()

      return true unless value.length > 2

      $.ajax
        method: 'GET'
        url: $form.attr('action')
        data: $form.serialize()
        dataType: 'json'
        success: (data, status, xhr) ->
          self.trigger 'ajax:success', [data, status, xhr]

  render: (e, tracks) =>
    self = this
    html = ''

    _.each tracks, (track) ->
      html += self.template track

    @el.html(html)
    @el.show()

$ -> new TrackSearch

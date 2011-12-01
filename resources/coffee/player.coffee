class Player
  constructor: ->
    self = this
    @url = '/playlist/current-track'
    @trackTemplate = _.template $('#track-template').html()
    @playerTemplate = _.template $('#player-template').html()

    @album = $('#current_track .album-cover').data('album')

    $('body').delegate('#track', 'track.render', (e, data) ->
      self.render(data)
    )

    $('body').delegate('#track', 'track.refresh', ->
      self.refresh()
    )

    $('body').delegate('.controls', 'ajax:success', (e, data) ->
      self.render(data)
    )

    setInterval( ->
      $('#track').trigger('track.refresh')
    , 10000)

  refresh: ->
    $.get(@url, (data) ->
      $('#track').trigger('track.render', [data])
    , 'json')

  render: (data) ->
    track = $ @trackTemplate {track: data}
    controls = $ @playerTemplate {track: data}
    $('#player-controls').html(controls).trigger('track.tick', [data])
    if @album isnt data.album
      @album = data.album
      $('div.album-cover').attr('data-artist', data.artist).attr('data-album', data.album)
      $('#track').html(track)
      $('title').text("#{data.title} - #{data.artist} - jukebox2")
      $('.current').removeClass('current')
      $("img[title=#{data.owner}]").addClass('current')
      PlayerNotification.render
        url: ""
        title: "Now playing"
        body: "#{data.title} by #{data.artist}"
      $('#current_track').trigger('track.updated')

$ -> new Player



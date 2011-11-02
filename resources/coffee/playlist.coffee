class Playlist

  constructor: ->
    self = this
    @template = _.template $('#playlist-template').html()
    @url = '/playlist'

    $('body').delegate('.controls', 'ajax:success', ->
      self.load()
    )

    @playlistTimer = setInterval( ->
      self.load()
    , 5000)

  load: ->
    self = this
    $.getJSON(@url, (data) ->
      self.render(data)
    )

  render: (data) ->
    if data.length
      playlist = @template {tracks: data}
    else
      playlist = $('<li />', {'class': 'random'}).text('Choosing random tracks')

    $('#playlist').html(playlist)


$ -> new Playlist

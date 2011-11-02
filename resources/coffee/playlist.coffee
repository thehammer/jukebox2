class Playlist

  constructor: ->
    self = this
    @template = _.template $('#playlist-template').html()
    @url = '/playlist'

    @playlistTimer = setInterval( ->
      self.load()
    , 10000)

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

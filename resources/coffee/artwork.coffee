class Artwork

  constructor: ->
    @album = undefined
    @artist = undefined
    @rendered = false

    $('#current_track').delegate('.album-cover', 'artwork.render', @render)
    @load()
    $('#current_track').bind('track.updated', @load)

  load: ->
    $covers = $('.album-cover')
    $covers.each ->
      $cover = $(this)
      @album = $cover.attr('data-album')
      @artist = $cover.attr('data-artist')
      $.get('http://ws.audioscrobbler.com/2.0/', {"method": "album.getInfo", "api_key": "809bf298f1f11c57fbb680b1befdf476", "album": @album, "artist": @artist}, (data) ->
        $cover.trigger 'artwork.render', [data]
      )

  render: (e, info) ->
    size = $(this).attr('data-thumbnail')
    src = $(info).find("image[size=#{size}]").text()
    if src is ''
      src = '/img/no_art_lrg.png'
    if @rendered
      $('.thumbnail').attr('src', src)
    else
      wrap = $('<a />', {'href': '#'})
      img = $('<img>', {'class': 'thumbnail', 'src': src})
      wrap.append(img)
      $(this).append(wrap)

    @rendered = true

$ -> new Artwork

class Progress

  constructor: ->
    self = this
    $progress = $('#current_track .progress')
    @progress = parseInt $progress.data('current')
    @duration = parseInt $progress.data('duration')
    @playing = $('a.btn.pause').length > 0

    self.render()

    $('body').delegate('#player-controls', 'track.tick', (e, data) ->
      self.tick(data)
    )

    @songTimer = setInterval( ->
      self.tick()
    , 1000)


  tick: (track) ->
    if track?
      {@playing, @progress, @duration} = track
    else
      @progress += 1 if @playing
    @render()

  render: ->
    if @progress < @duration
      remaining = @duration - @progress
      minutes = Math.floor remaining / 60
      seconds = remaining % 60
      seconds = "0#{seconds}" if seconds < 10
      $('#player-controls .remaining').text("#{minutes}:#{seconds}")
    else
      $('#track').trigger('track.refresh')


$ -> new Progress

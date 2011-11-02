(function() {
  var Progress;
  Progress = (function() {
    function Progress() {
      var $progress, self;
      self = this;
      $progress = $('#current_track .progress');
      this.progress = parseInt($progress.data('current'));
      this.duration = parseInt($progress.data('duration'));
      this.playing = $('a.btn.pause').length > 0;
      self.render();
      $('body').delegate('#player-controls', 'track.tick', function(e, data) {
        return self.tick(data);
      });
      this.songTimer = setInterval(function() {
        return self.tick();
      }, 1000);
    }
    Progress.prototype.tick = function(track) {
      if (track != null) {
        this.playing = track.playing, this.progress = track.progress, this.duration = track.duration;
      } else {
        if (this.playing) {
          this.progress += 1;
        }
      }
      return this.render();
    };
    Progress.prototype.render = function() {
      var minutes, remaining, seconds;
      if (this.progress >= this.duration) {
        return;
      }
      remaining = this.duration - this.progress;
      minutes = Math.floor(remaining / 60);
      seconds = remaining % 60;
      if (seconds < 10) {
        seconds = "0" + seconds;
      }
      return $('#player-controls .remaining').text("" + minutes + ":" + seconds);
    };
    return Progress;
  })();
  $(function() {
    return new Progress;
  });
}).call(this);

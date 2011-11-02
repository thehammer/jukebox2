(function() {
  var Player;
  Player = (function() {
    function Player() {
      var self;
      self = this;
      this.url = '/playlist/current-track';
      this.trackTemplate = _.template($('#track-template').html());
      this.playerTemplate = _.template($('#player-template').html());
      this.album = $('#current_track .album-cover').data('album');
      $('body').delegate('#track', 'track.render', function(e, data) {
        return self.render(data);
      });
      $('body').delegate('#track', 'track.refresh', function() {
        return self.refresh();
      });
      $('body').delegate('.controls', 'ajax:success', function(e, data) {
        return self.render(data);
      });
      setInterval(function() {
        return $('#track').trigger('track.refresh');
      }, 10000);
    }
    Player.prototype.refresh = function() {
      return $.get(this.url, function(data) {
        return $('#track').trigger('track.render', [data]);
      }, 'json');
    };
    Player.prototype.render = function(data) {
      var controls, track;
      track = $(this.trackTemplate({
        track: data
      }));
      controls = $(this.playerTemplate({
        track: data
      }));
      $('#player-controls').html(controls).trigger('track.tick', [data]);
      if (this.album !== data.album) {
        this.album = data.album;
        $('div.album-cover').attr('data-artist', data.artist).attr('data-album', data.album);
        $('#track').html(track);
        return $('#current_track').trigger('track.updated');
      }
    };
    return Player;
  })();
  $(function() {
    return new Player;
  });
}).call(this);

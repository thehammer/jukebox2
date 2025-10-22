(function() {
  var Player;
  Player = (function() {
    function Player() {
      var self;
      self = this;
      this.url = '/playlist/current-track';
      this.trackTemplate = _.template($('#track-template').html());
      this.playerTemplate = _.template($('#player-template').html());
      this.title = $('#current_track .album-cover').data('title');
      $('body').on('track.render', '#track', function(e, data) {
        return self.render(data);
      });
      $('body').on('track.refresh', '#track', function() {
        return self.refresh();
      });
      $('body').on('ajax:success', '.controls', function(e, data) {
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
      if (this.title === data.title) {
        return;
      }
      this.title = data.title;
      $('div.album-cover').attr('data-artist', data.artist).attr('data-album', data.album).attr('data-title', data.title);
      $('#track').html(track);
      $('title').text("" + data.title + " - " + data.artist + " - jukebox2");
      $('.current').removeClass('current');
      $("img[title='" + data.owner + "']").addClass('current');
      PlayerNotification.render({
        body: "" + data.title + " by " + data.artist
      });
      return $('#current_track').trigger('track.updated');
    };
    return Player;
  })();
  $(function() {
    return new Player;
  });
}).call(this);

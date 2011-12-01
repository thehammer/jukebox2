(function() {
  var Playlist;
  Playlist = (function() {
    function Playlist() {
      var self;
      self = this;
      this.template = _.template($('#playlist-template').html());
      this.url = '/playlist';
      $('body').delegate('.controls', 'ajax:success', function() {
        return self.load();
      });
      $('.topbar').delegate('a.update-playlist', 'ajax:success', function(e, data) {
        return self.render(data);
      });
      this.playlistTimer = setInterval(function() {
        return self.load();
      }, 6000);
    }
    Playlist.prototype.load = function() {
      var self;
      self = this;
      return $.getJSON(this.url, function(data) {
        return self.render(data);
      });
    };
    Playlist.prototype.render = function(data) {
      var playlist;
      if (data.length) {
        playlist = this.template({
          tracks: data
        });
      } else {
        playlist = $('<li />', {
          'class': 'random'
        }).text('Choosing random tracks');
      }
      return $('#playlist').html(playlist);
    };
    return Playlist;
  })();
  $(function() {
    return new Playlist;
  });
}).call(this);

(function() {
  var Artwork;
  Artwork = (function() {
    function Artwork() {
      this.covers = $('.album-cover');
      this.covers.bind('ajax:success', this.render);
      this.load();
    }
    Artwork.prototype.load = function() {
      return this.covers.each(function() {
        var $cover, album, artist;
        $cover = $(this);
        album = $cover.attr('data-album');
        artist = $cover.attr('data-artist');
        return $.get('http://ws.audioscrobbler.com/2.0/', {
          "method": "album.getInfo",
          "api_key": "809bf298f1f11c57fbb680b1befdf476",
          "album": album,
          "artist": artist
        }, function(data) {
          return $cover.trigger('ajax:success', [data]);
        });
      });
    };
    Artwork.prototype.render = function(e, info) {
      var img, size, src, wrap;
      size = $(this).attr('data-thumbnail');
      src = $(info).find("image[size=" + size + "]").text();
      wrap = $('<a />', {
        'href': '#'
      });
      img = $('<img>', {
        'class': 'thumbnail',
        'src': src
      });
      wrap.append(img);
      return $(this).append(wrap);
    };
    return Artwork;
  })();
  $(function() {
    return new Artwork;
  });
}).call(this);

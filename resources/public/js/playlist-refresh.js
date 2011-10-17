function refreshPlaylist() {
  $.get('/playlist/current-track', function(html) {
    $('#current_track').html(html);
    new Artwork;
  });
}

$(function() {
  setInterval(refreshPlaylist, 10000);
});

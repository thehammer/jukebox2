function refreshPlaylist() {
  $.get('/playlist/current-track', function(html) {
    $('#current_track').html(html).trigger('track.updated');
  });
}

$(function() {
  setInterval(refreshPlaylist, 10000);
});

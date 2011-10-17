function refreshPlaylist() {
  $.get('/playlist/current-track', function(html) {
    $('#current_track').html(html);
  });
}

$(function() {
  setInterval(refreshPlaylist, 10000);
});

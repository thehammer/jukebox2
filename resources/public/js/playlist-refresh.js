function onRefreshSuccess(html, status, xhr) {
  $('#current_track_etag').val(xhr.getResponseHeader("E-Tag"));
  if (status == "success") {
    $('#current_track').html(html).trigger('track.updated');
    var title  = $('#current_track h1.title').text(),
        artist = $('#current_track p.artist').text();
    $('title').text(title + ' - ' + artist + ' - jukebox2');
  }
  $('#current_track .progress').data('current', xhr.getResponseHeader("X-Progress"));
  updateProgress();
}

function refreshPlaylist() {
  $.ajax({
    url: '/playlist/current-track',
    success: onRefreshSuccess,
    headers: { "If-None-Match": $("#current_track_etag").val() },
    dataType: "html"
  });
}

function isPlaying() {
  return $('#current_track .controls .pause').length > 0;
}

function tickProgress() {
  if (isPlaying()) {
    var progress = $('#current_track .progress');
    var current = parseInt(progress.data('current'));
    var duration = parseInt(progress.data('duration'));
    if (current < duration) {
      progress.data('current', current + 1);
      updateProgress();
    }
    else {
      refreshPlaylist();
    }
  }
}

function updateProgress() {
  var progress = $('#current_track .progress');
  var remaining = parseInt(progress.data('duration')) - parseInt(progress.data('current'));
  var minutes = Math.floor(remaining / 60.0).toString();
  var seconds = (remaining % 60).toString();
  seconds = seconds.length < 2 ? "0" + seconds : seconds
  $('#current_track .progress .remaining').html(minutes + ":" + seconds);
}

$(function() {
  $('#current_track .progress').data('current', $('#first_load_progress').val());
  updateProgress();
  setTimeout(refreshPlaylist, 500);
  setInterval(refreshPlaylist, 10000);
  setInterval(tickProgress, 1000);
});

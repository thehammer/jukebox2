function onRefreshSuccess(html, status, xhr) {
  console.log(status);
  console.log(xhr.getResponseHeader("E-Tag"));
  $('#current_track_etag').val(xhr.getResponseHeader("E-Tag"));
  if (status == "success") {
    $('#current_track').html(html).trigger('track.updated');
  }
}

function refreshPlaylist() {
  $.ajax({
    url: '/playlist/current-track',
    success: onRefreshSuccess,
    headers: { "If-None-Match": $("#current_track_etag").val() }
  });
}

$(function() {
  setInterval(refreshPlaylist, 10000);
});

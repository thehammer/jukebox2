$(function() {
  $("#uploader").pluploadQueue({
    runtimes : 'flash,html5',
    url : '/library/upload/tony',
    max_file_size : '20mb',
    unique_names : true,
    filters : [{title : "Music files", extensions : "mp3,m4a"}],
    flash_swf_url : '/plupload.flash.swf',
  });
});


$(function() {
  $("#uploader").pluploadQueue({
    runtimes : 'html5,flash',
    url : '/library/upload/tony',
    max_file_size : '20mb',
    unique_names : true,
    filters : [{title : "Music files", extensions : "mp3,m4a"}],
    flash_swf_url : '/plupload.flash.swf',
  });
});


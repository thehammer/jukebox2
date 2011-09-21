$(function() {
  $("#uploader").pluploadQueue({
    runtimes : 'flash,html5',
    url : '/library/upload/tony',
    max_file_size : '10mb',
//    chunk_size : '1mb',
    unique_names : true,
    filters : [{title : "Music files", extensions : "mp3,m4a"}],
    flash_swf_url : '/plupload.flash.swf',
  });

  // Client side form validation
  $('form').submit(function(e) {
        var uploader = $('#uploader').pluploadQueue();

        // Files in queue upload them first
        if (uploader.files.length > 0) {
            // When all files are uploaded submit form
            uploader.bind('StateChanged', function() {
                if (uploader.files.length === (uploader.total.uploaded + uploader.total.failed)) {
                    $('form')[0].submit();
                }
            });
                
            uploader.start();
        } else {
            alert('You must queue at least one file.');
        }

        return false;
    });
});


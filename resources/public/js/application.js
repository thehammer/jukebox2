$(function () {

  $('.topbar').dropdown();

  $('#notifications').delegate('li', 'upload:progress', function (e, xhr, p) {
    console.log([e, xhr, p]);
  });

  $('#notifications').delegate('li', 'ajax:success', function (e, xhr, data) {
    $(this).addClass('success').removeClass('uploading');
  });

});

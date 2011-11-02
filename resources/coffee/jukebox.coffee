class Jukebox
  constructor: ->
    _.templateSettings =
      evaluate: /<%([\s\S]+?)%>/g
      interpolate: /\{\{(.+?)\}\}/g

    $('body').delegate('a[data-remote=true]', 'click', @remote)
    $('.topbar').dropdown()
    Typekit.load()

  remote: (e) ->
    e.preventDefault()
    $el = $(this)
    url = $el.attr('href')
    method = $el.data('method') || 'GET'

    $.ajax
      method: method
      url: url
      dataType: 'json'
      success: (data, status, xhr) ->
        $el.trigger 'ajax:success', [data, status, xhr]

$ -> new Jukebox

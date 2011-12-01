class Jukebox
  constructor: ->
    _.templateSettings =
      evaluate: /<%([\s\S]+?)%>/g
      interpolate: /\{\{(.+?)\}\}/g

    $('body').delegate('a[data-remote=true]', 'click', @remote)
    $('body').delegate('form[data-remote=true]', 'submit', @remote)
    $('.topbar').dropdown()
    Typekit.load()

  remote: (e) ->
    e.preventDefault()
    $el = $(this)
    url = $el.attr('href') || $el.attr('action')
    data = $el.serialize() if $el.is 'form'
    method = $el.attr('method') || 'GET'

    $.ajax
      method: method
      url: url
      data: data || {}
      dataType: 'json'
      success: (data, status, xhr) ->
        $el.trigger 'ajax:success', [data, status, xhr]

$ -> new Jukebox

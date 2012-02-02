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
    data = {}
    $el = $(this)
    url = $el.attr('href') || $el.attr('action')
    data = $el.serialize() if $el.is 'form'
    methodOverride = $el.attr('data-method')

    if methodOverride? and methodOverride isnt 'GET'
      method = 'POST'
      data['_method'] = $el.attr('data-method')
    else
      method = $el.attr('method') || 'GET'

    $.ajax
      type: method
      url: url
      data: data
      dataType: 'json'
      success: (data, status, xhr) ->
        $el.trigger 'ajax:success', [data, status, xhr]

$ -> new Jukebox

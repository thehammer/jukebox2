class Notifications

  constructor: (root) ->
    @root = $(root)
    @view = ""

  render: ->
    @root.append @view
    @view

  remove: ->
    $el = $(this)
    setTimeout( ->
      $el.fadeOut()
    , 1000)

class FileNotification extends Notifications

  constructor: (root, template) ->
    super root
    @template = _.template $(template).html()

    @root.delegate 'li', 'ajax:success', @success
    @root.delegate 'li', 'ajax:error', @error
    @root.delegate 'li', 'upload:progress', @progress
    @root.delegate 'li', 'notification:remove', @remove

  render: (data) ->
    @view = $ @template {file: data}
    super

  success: (e, xhr, data) ->
    $(this).addClass('success').removeClass('uploading').trigger('notification:remove')

  error: (e, xhr) ->
    $(this).addClass('error').removeClass('uploading').trigger('notification:remove')

  progress: (e, xhr, progress) ->
    if progress.lengthComputable
      percent = (progress.loaded / progress.total) * 100
      $('.progress-bar', this).css({width: "#{percent}%"})

window.FileNotification = FileNotification

class PlayerNotification

  constructor: ->
    return @removeButton() unless @capable()
    @renderButton() unless @noPermission()
    @defaultOptions =
      url: ''
      title: 'Now Playing'
      body: ''
    $('#enable-notifications').bind('click', @askForPermission)

  render: (options) ->
    return false if !@capable() or @noPermission()

    {url, title, body} = $.extend @defaultOptions, options
    notification = window.webkitNotifications.createNotification(url, title, body)
    notification.show()
    setTimeout ->
      notification.cancel()
    , 5000

  renderButton: ->
    $('#enable-notifications').text('Notifications Enabled').unbind('click')

  removeButton: ->
    $('#enable-notifications').parent('li').remove()
    false

  capable: ->
    window.webkitNotifications?

  noPermission: ->
    window.webkitNotifications.checkPermission() > 0

  askForPermission: =>
    self = this
    window.webkitNotifications.requestPermission ->
      self.renderButton()


$ -> window.PlayerNotification = new PlayerNotification

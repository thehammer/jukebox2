class Notifications

  constructor: (root) ->
    @root = $(root)
    @view = ""


  render: ->
    @root.append @view
    @view


class FileNotification extends Notifications

  constructor: (root, template) ->
    super root
    @template = _.template $(template).html()

    @root.delegate 'li', 'ajax:success', @success
    @root.delegate 'li', 'ajax:error', @error
    @root.delegate 'li', 'upload:progress', @progress

  render: (data) ->
    @view = $ @template {file: data}
    super

  success: (e, xhr, data) ->
    $(this).addClass('success').removeClass('uploading')

  error: (e, xhr) ->
    $(this).addClass('error').removeClass('uploading')

  progress: (e, xhr, data) ->
    $('.progress-bar', this).css({width: "#{data}%"})

window.FileNotification = FileNotification

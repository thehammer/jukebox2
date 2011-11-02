class Files

  constructor: ->
    @loggedIn = $('.logged-in').length
    @validFiles = $('body').attr('data-accept')
    @notification = new FileNotification '#notifications', '#file-notification'
    @uploader = new Uploader
      method: 'POST'
      url: '/library/upload'

    document.addEventListener "dragenter", @stopActions, false
    document.addEventListener "dragexit", @stopActions, false
    document.addEventListener "dragover", @stopActions, false
    document.addEventListener "drop", @stopActions, false

    if @loggedIn
      document.addEventListener "drop", @render, false

  isAcceptable: (type) ->
    new RegExp(@validFiles, 'gi').test(type)

  render: (evt) =>
    for file in evt.dataTransfer.files
      $element = @notification.render
        name: file.name
        size: @sizeInMb(file.size)
      if @isAcceptable file.type
        @uploader.send file, $element
      else
        $element.trigger 'ajax:error', ["file type is invalid"]

  sizeInMb: (size) ->
    Math.round parseInt(size) / 1048576

  stopActions: (evt) ->
    evt.stopPropagation()
    evt.preventDefault()


$ -> new Files

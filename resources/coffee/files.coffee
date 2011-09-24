class Files

  constructor: (@selector) ->
    document.addEventListener "dragenter", @stopActions, false
    document.addEventListener "dragexit", @stopActions, false
    document.addEventListener "dragover", @stopActions, false
    document.addEventListener "drop", @render, false
    @ajax = new Ajax
      element: 'a_selector'

  isAcceptable: (type) ->

  render: (evt) =>
    $selector = $ @selector
    for file in evt.dataTransfer.files
      listItem = $('<li />').text("#{file.name} #{@sizeInMb(file.size)}mb")
      $selector.append listItem
      @ajax.upload file

  sizeInMb: (size) ->
    Math.round parseInt(size) / 1048576

  stopActions: (evt) ->
    evt.stopPropagation()
    evt.preventDefault()


$ new Files('#uploads')

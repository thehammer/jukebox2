class Files

  constructor: (@selector) ->
    @validFiles = new RegExp $('body').attr('data-accept'), 'gi'
    @uploader = new Uploader

    document.addEventListener "dragenter", @stopActions, false
    document.addEventListener "dragexit", @stopActions, false
    document.addEventListener "dragover", @stopActions, false
    document.addEventListener "drop", @render, false

  isAcceptable: (type) ->
    @validFiles.test type

  render: (evt) =>
    $selector = $ @selector
    for file in evt.dataTransfer.files
      continue unless @isAcceptable file.type
      listItem = $('<li />', {"class": "uploading"}).text("#{file.name} #{@sizeInMb(file.size)}mb")
      $selector.append listItem
      @uploader.send file, listItem

  sizeInMb: (size) ->
    Math.round parseInt(size) / 1048576

  stopActions: (evt) ->
    evt.stopPropagation()
    evt.preventDefault()


$ -> new Files('#notifications')

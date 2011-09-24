class Uploader

  constructor: (@settings) ->

  send: (file, $element) ->
    xhr = new XMLHttpRequest()

    xhr.onreadystatechange = ->
      if xhr.readyState is 4
        if xhr.status < 300
          $element.trigger 'ajax:success', [xhr, xhr.responseText]
        else
          $element.trigger 'ajax:error', xhr

    if xhr.upload?
      for event in ["abort", "error", "load", "loadstart", "progress"]
        xhr.upload.addEventListener(event, (p) ->
          $element.trigger "upload:#{event}", [xhr, p]
        , false)

    xhr.open 'POST', '/library/upload'

    data = new FormData()
    data.append "file", file
    xhr.send data

window.Uploader = Uploader

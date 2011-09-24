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

    xhr.open 'POST', '/library/upload/cory'

    data = new FormData()
    data.append "file", file
    xhr.send data

window.Uploader = Uploader

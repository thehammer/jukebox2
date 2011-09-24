class Ajax

  constructor: (@settings) ->

  upload: (file) ->
    xhr = new XMLHttpRequest()

    xhr.onreadystatechange = ->
      if xhr.readyState is 4
        if xhr.status < 300
          @settings.element.trigger 'ajax:success', [xhr, xhr.responseText]
        else
          @settings.element.trigger 'ajax:error', xhr

    xhr.open 'POST', '/library/upload/cory'

    data = new FormData()
    data.append "file", file
    xhr.send data

window.Ajax = Ajax

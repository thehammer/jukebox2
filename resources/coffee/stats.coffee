google.load 'visualization', '1.0', {'packages':['corechart']}
google.setOnLoadCallback ->
  $.getJSON('/stats/song-counts', (songCountData) ->
    data = new google.visualization.DataTable()
    data.addColumn 'string', 'User'
    data.addColumn 'number', 'Number of Songs'
    data.addRows(songCountData)

    options = {'title': 'Number of Songs', 'width':800, 'height':600}

    chart = new google.visualization.PieChart(document.getElementById('song-count-chart'))
    chart.draw data, options
  )

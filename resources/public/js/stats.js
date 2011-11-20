(function() {
  google.load('visualization', '1.0', {
    'packages': ['corechart']
  });
  google.setOnLoadCallback(function() {
    return $.getJSON('/stats/song-counts', function(songCountData) {
      var chart, data, options;
      data = new google.visualization.DataTable();
      data.addColumn('string', 'User');
      data.addColumn('number', 'Number of Songs');
      data.addRows(songCountData);
      options = {
        'title': 'Number of Songs',
        'width': 800,
        'height': 600
      };
      chart = new google.visualization.PieChart(document.getElementById('song-count-chart'));
      return chart.draw(data, options);
    });
  });
}).call(this);

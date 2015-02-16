$(document).ready(function () {
    var data = {};

    $("#myButton").click(function () {

        $.ajax({
            type: 'POST',

            url: '/submit/charts1b',

            data: JSON.stringify($('#ContactForm').serializeObject()),

            contentType: "application/json; charset=utf-8",

            success: function (response) {

            },

            complete: function (response) {
                var json = JSON.parse(response.responseText);

                if(isArray(json)) {
                    var conditionsJson = json[0];
                    var servicesJson = json[1];


                    //plotServices(servicesJson);
                    plotServices(servicesJson);
                    plotConditions(conditionsJson);
                } else {
                    alert('No data is available for the selected inputs.');
                }
            },

            error: function (xhr, status) {

            }
        });
    });

    function plotServices(servicesJson) {
        console.log(servicesJson);
        nv.addGraph(function() {
          var chart = nv.models.multiBarHorizontalChart()
              .x(function(d) { return d.label })
              .y(function(d) { return d.value })
              .margin({top: 30, right: 20, bottom: 50, left: 250})
              .showValues(true)
              .tooltips(false)
              .showControls(true);

          chart.yAxis
              .tickFormat(d3.format(',.2f'));

          d3.select('#service-chart svg')
              .datum(servicesJson)
            .transition().duration(500)
              .call(chart);

          nv.utils.windowResize(chart.update);

          return chart;
        });
    }

    function plotConditions(conditions) {
         var chart = nv.models.discreteBarChart()
           .x(function(d) { return d.label })
           .y(function(d) { return d.value })
           .staggerLabels(true)
           .tooltips(false)
           .showValues(true)

         d3.select('#condition-chart svg')
           .datum(conditions)
           .transition().duration(500)
           .call(chart)
           ;

         nv.utils.windowResize(chart.update);

         return chart;
    }

});
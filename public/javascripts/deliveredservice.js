$(document).ready(function(){
  console.log('Calling deliveredservice.js');

  var raceChart;
  var genderChart;
  var agegroupChart;
  var servicesChart;


  // load the data file
  d3.json("api/deliveredservice", function (patients) {

      // we'll need to display month names rather than 0-based index values
      var monthNames = [
          "January", "February", "March", "April", "May", "June",
          "July", "August", "September", "October", "November", "December"
      ];

      // we'll need to display day names rather than 0-based index values
      var dayNames = [
          "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
      ];

      // associate the charts with their html elements
      raceChart = dc.rowChart("#chart-race");
      genderChart = dc.pieChart("#chart-gender");
      agegroupChart = dc.pieChart("#chart-agegroup");
      servicesChart = dc.rowChart("#chart-delivered-services");

      // use cross filter to create the dimensions and grouping
      var ppr = crossfilter(patients);

      //Race Chart
      var raceDim = ppr.dimension(function (d) {
          return d.eth;
      });

      var countPerRace = raceDim.group().reduceCount();

      //Gender Chart
      var genderDim = ppr.dimension(function (d) {
          return d.gen;
      });

      var countPerGender = genderDim.group().reduceCount();

      //Age Chart
      //Clean up data for date
      patients.forEach(function (patient) {
          // remove the euro symbol and convert to a number
          if ((18 <= patient.age) && (patient.age < 24)) {
              patient.ageGroup = "18 - 24";
          } else if ((24 <= patient.age) && (patient.age < 34)) {
              patient.ageGroup = "24 - 34";
          } else if ((34 <= patient.age) && (patient.age < 44)) {
              patient.ageGroup = "34 - 44";
          } else if ((44 <= patient.age) && (patient.age < 54)) {
              patient.ageGroup = "44 - 54";
          } else if ((54 <= patient.age) && (patient.age < 64)) {
              patient.ageGroup = "54 - 64";
          } else if ((64 <= patient.age)) {
              patient.ageGroup = "older than 64";
          }

      });

      //Age group Chart
      var agegroupDim = ppr.dimension(function (d) {
          return d.ageGroup;
      });

      var countPerAgeGroup = agegroupDim.group().reduceCount();

      //Services Chart
      var servicesDim = ppr.dimension(function (d) {
          return d.ser
      });

      console.log("dim "+servicesDim);

      var servicesDimGroup = servicesDim.group().reduceCount();

      console.log("group" + servicesDimGroup);


      //Configure the charts
      //race
      raceChart
          .width(300)
          .height(250)
          .dimension(raceDim)
          .group(countPerRace)
          .colors(d3.scale.category10())
          .label(function (d) {
              return d.key;
          })
          .title(function (d) {
              return d.key + ' / ' + d.value;
          })
          .elasticX(true).xAxis().ticks(4);

      //gender
      genderChart
          .width(300)
          .height(250)
          .radius(100)
          .dimension(genderDim)
          .group(countPerGender)
          .colors(d3.scale.category10()).
          label(function (d) {
              return d.key;
          })
          .title(function (d) {
              return d.key + ' / ' + d.value;
          });

      //age group
      agegroupChart
          .width(300)
          .height(250)
          .radius(100)
          .innerRadius(50)
          .dimension(agegroupDim)
          .group(countPerAgeGroup)
          .colors(d3.scale.category10())
          .label(function (d) {
              return d.key;
          })
          .title(function (d) {
              return d.key + ' / ' + d.value;
          });

       //services
       servicesChart
           .width(300)
           .height(700)
           .dimension(servicesDim)
           .group(servicesDimGroup)
           .colors(d3.scale.category10())
           .label(function (d) {
               return d.key;
           })
           .title(function (d) {
               return d.key + ' / ' + d.value;
           })
           .elasticX(true).xAxis().ticks(4);



      // hit it!
      dc.renderAll();

  });

});


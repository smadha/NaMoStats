<span class="chart-container">


    <svg id="${chartid}" class="chart">
    </svg>
    <span id="menubox" class="col-lg-4">
        Candidate's activity on twitter:
    </span>
    <span id="desc"></span>
    <script>
        var margin = {top: 20, right: 80, bottom: 30, left: 50},
                width = 960 - margin.left - margin.right,
                height = 500 - margin.top - margin.bottom;
        var x = d3.time.scale()
                .range([0, width]);
        var y = d3.scale.linear()
                .range([height, 0]);
        var color = d3.scale.category10();
        var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom");
        var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left");
        var line = d3.svg.line()
                .interpolate("basis")
                .x(function(d) { return x(d.date); })
                .y(function(d) { return y(d.count); });
        var svg = d3.select("#${chartid}")
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        function drawChart(data){
            //clear previous state
            d3.select("g").selectAll("*").remove();
            // if (error) throw error;
            domains = data.map(function (v) {return v.name});
            color.domain(domains);
            //console.log(data)
            x.domain(d3.extent(data[0].values, function(d) { return d.date; }));
            y.domain([
                d3.min(data, function(c) { return d3.min(c.values, function(v) { return v.count; }); }),
                d3.max(data, function(c) { return d3.max(c.values, function(v) { return v.count; }); })
            ]);
            svg.append("g")
                    .attr("class", "x axis")
                    .attr("transform", "translate(0," + height + ")")
                    .call(xAxis);
            svg.append("g")
                    .attr("class", "y axis")
                    .call(yAxis)
                    .append("text")
                    .attr("transform", "rotate(-90)")
                    .attr("y", 6)
                    .attr("dy", ".71em")
                    .style("text-anchor", "end")
                    .text("Activeness ");
            var canditrend = svg.selectAll(".canditrend")
                    .data(data)
                    .enter().append("g")
                    .attr("class", "candiname");
            canditrend.append("path")
                    .attr("class", "line")
                    .attr("d", function(d) {return line(d.values);})
                    .style("stroke", function(d) { return color(d.name); });
            canditrend.append("text")
                    .datum(function(d) { return {name: d.name, value: d.values[d.values.length - 1]}; })
                    .attr("transform", function(d) { console.log(d); return "translate(" + x(d.value.date) + "," + y(d.value.count) + ")"; })
                    .attr("x", 3)
                    .attr("dy", ".35em")
                    .text(function(d) { return d.name; });
        }

        function onInputChange(){
            menu = d3.select("#candinameMenu")[0][0];
            if(!menu) {
                return console.error("No menu");
            }
            opts = menu.selectedOptions;
            candinames = [];
            for (i=0; i< opts.length; i++) {
                candinames.push(opts[i].value)
            }
            drawTrend(candinames)
        }


        function getDateFacets(query, id, callback){
            dateStartStr = "2015-06-01T00:00:00Z";
            dateEndStr = "NOW";
            url = "/rest/datefacets?q=" + query + "&gap=14DAYS";
            d3.json(url, function(err, data) {
                var arr = [];

                for (var i=0; i<data.length; i++){
                    var rec = data[i]
                    var obj = {};
                    obj['date'] = new Date(Date.parse(rec['date']));
                    obj['count'] =  rec['count'];
                    arr.push(obj);
                }
                callback(id, arr)
            });
        }

        function drawTrend(candidates){
            var stats = [];
            d3.select("#desc").text("");
            console.log("Canidates = "+ candidates);
            for (i=0; i < candidates.length; i++) {
                //counts[candidates[i]] = {values:{}};
                getDateFacets("userid:"+candidates[i], candidates[i], function(id, data) {
                            part = {};
                            part['name'] = id;
                            part['values'] = data;
                            stats.push(part);
                            if (stats.length == candidates.length) {
                                //all results are here
                                //console.log(stats)
                                drawChart(stats);
                                for (i=0; i < candidates.length; i++) {
                                    d3.select("#desc")
                                            .append("div").attr("padding",0).attr("margin",0)
                                            .attr("class","lineThick")
                                            .style("color",function(d) { return color(candidates[i]); })
                                            .html(candidates[i]+"&nbsp;&nbsp;&nbsp;&nbsp;――――――――――――――――――")
                                }
                            }
                        });
            }
        }

        candidates = ['SenSanders', 'HillaryClinton', 'realDonaldTrump',
            'tedcruz', 'marcorubio', 'JohnKasich', 'RealBenCarson']
        var select  = d3.select("#menubox")
                .append("select")
                .attr('multiple', 'true')
                .attr('id', 'candinameMenu')
                .on("change", onInputChange),
                options = select.selectAll('option').data(candidates); // Data join
        // Enter selection
        options.enter().append("option").text(function(d) { return d; });
        drawTrend(candidates);

    </script>
</span>

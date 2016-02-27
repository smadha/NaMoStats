<span class="chart" id="bubble-${fieldName}-container">
    <h1>${title}</h1>
    <svg id="bubble-${fieldName}" width="800" height="360">

    </svg>
    <script>
        var diameter = 800,
                format = d3.format(",d"),
                color = d3.scale.category20c();

        var bubble = d3.layout.pack()
                .sort(null)
                .size([diameter, diameter])
                .padding(2);

        var svg${fieldName} = d3.select("#bubble-${fieldName}")
                .attr("width", diameter)
                .attr("height", diameter)
                .attr("class", "bubble-${fieldName}");

        url = "./postFacets?field=${fieldName}&rows=50&userid=${userid}"
        d3.json(url, function(error, root) {
            if (error) throw error;
            //console.log(JSON.stringify(root))
            if (root.children.length == 0) {
                return svg${fieldName}.append('text')
                        .attr("y", "100")
                        .attr("x", "100")
                        .style("text-anchor", "middle")
                        .text("No results found.");
            }

            var node = svg${fieldName}.selectAll(".node-${fieldName}")
                    .data(bubble.nodes(root)
                    .filter(function(d) { return !d.children; }))
                    .enter().append("g")
                    .attr("class", "node")
                    .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

            node.append("title")
                    .text(function(d) { return d.name + ": " + format(d.value); });

            node.append("circle")
                    .attr("r", function(d) { return d.r; })
                    .style("fill", function(d) { return color(d.name); });

            node.append("text")
                    .attr("dy", ".3em")
                    .style("text-anchor", "middle")
                    .text(function(d) { return d.name.substring(0, d.r / 3); });
        });

        d3.select(self.frameElement).style("height", diameter + "px");

    </script>
</span>

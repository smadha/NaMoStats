
<div id="pieChart"></div>


<script>
$.ajax({
		'url' : "/rest/sentiments?userid=${params.userid}",
		'success' : function(dataApi) {
			var color = {"Negative": "#ff6666",
				"Positive": "#b3ffb3",
				"Neutral":  "#7575a3",
				"Mixed": "#ffff1a",
				"Very negative": "#ff0000",
				"Very positive": "#008000"}
			for(var i in dataApi){
				dataApi[i].color=color[dataApi[i].label]
			}

var pie = new d3pie("pieChart", {
	"header": {
		"title": {
			"text": "How are people reacting?",
			"fontSize": 24,
			"font": "open sans"
		},
		"subtitle": {
			"text": "A pie chart to show sentiments of people whille mentioning @${params.userid}.",
			"color": "#999999",
			"fontSize": 12,
			"font": "open sans"
		},
		"titleSubtitlePadding": 9
	},
	"footer": {
		"color": "#999999",
		"fontSize": 10,
		"font": "open sans",
		"location": "bottom-left"
	},
	"size": {
		"canvasWidth": 590,
		"pieOuterRadius": "90%"
	},
	"data": {
		"sortOrder": "value-desc",
		"content": dataApi
	},
	"labels": {
		"outer": {
			"pieDistance": 32
		},
		"inner": {
			"hideWhenLessThanPercentage": 3
		},
		"mainLabel": {
			"fontSize": 11
		},
		"percentage": {
			"color": "#ffffff",
			"decimalPlaces": 0
		},
		"value": {
			"color": "#adadad",
			"fontSize": 11
		},
		"lines": {
			"enabled": true
		},
		"truncation": {
			"enabled": true
		}
	},
	"effects": {
		"pullOutSegmentOnClick": {
			"effect": "linear",
			"speed": 400,
			"size": 8
		}
	},
	"misc": {
		"gradient": {
			"enabled": true,
			"percentage": 100
		}
	}
 });
}
});
</script>
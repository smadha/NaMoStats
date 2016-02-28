  <div id="googleMap" style="width: 500px; height: 500px;"></div>

<script>
	function initialize() {
		var mapProp = {
			center : new google.maps.LatLng(39.879034,-101.762123),
			zoom : 1,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		};
		var map = new google.maps.Map(document.getElementById("googleMap"), mapProp);
		$.ajax({
		'url' : "/rest/boundingbox",
		'success' : function(data) {
      		var boxCoords = data.boxes

			// Construct the polygon.
			var boxes = new google.maps.Polygon({
				paths : boxCoords,
				strokeColor : '#FF0000',
				strokeOpacity : 0.8,
				strokeWeight : 2,
				fillColor : '#FF0000',
				fillOpacity : 0.35
			});
			boxes.setMap(map);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.error('Issue while calling API - ' + url + ' - ' + textStatus + ' - ' + errorThrown);
		}
		});
	
		
	}

	function loadScript() {
		var script = document.createElement("script");
		script.type = "text/javascript";
		script.src = "http://maps.googleapis.com/maps/api/js?key=&sensor=false&callback=initialize";
		document.body.appendChild(script);
	}

	window.onload = loadScript;
</script>


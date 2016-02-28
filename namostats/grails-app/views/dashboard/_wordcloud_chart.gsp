<div id="example" style="width: 550px; height: 350px;"></div>

<script type="text/javascript">
$(function() {
	
	$.ajax({
		'url' : "http://localhost:8080/rest/toptags",
		'success' : function(data) {
		
      	var word_array = []
		for (var i in data){
			var obj = {}
			obj['text']=data[i].name
			obj['weight']=data[i].count
			obj['link']="https://twitter.com/search?q="+data[i].name
			word_array.push(obj)
		}


        $("#example").jQCloud(word_array);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.error('Issue while calling API - ' + url + ' - ' + textStatus + ' - ' + errorThrown);
		}
	});
      
      });
    </script>
<%--
  Created by IntelliJ IDEA.
  User: tg
  Date: 11/26/15
  Time: 11:54 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Dashboard</title>
  <asset:javascript src="application.js"/>
  <asset:stylesheet src="application.css"/>
  <link rel="stylesheet" type="text/css" href="/assets/jqcloud.css" />
  
  
  <script type="text/javascript" defer="defer">
	window.twttr = (function(d, s, id) {
		var js, fjs = d.getElementsByTagName(s)[0], t = window.twttr || {};
		if (d.getElementById(id))
			return t;
		js = d.createElement(s);
		js.id = id;
		js.src = "https://platform.twitter.com/widgets.js";
		fjs.parentNode.insertBefore(js, fjs);

		t._e = [];
		t.ready = function(f) {
			t._e.push(f);
		};

		return t;
	}(document, "script", "twitter-wjs"));
	
	window.setTimeout(function() {
				twttr.widgets.load()
			}, 5000)
</script>
</head>

<body>
<div class="container-fluid">
<H3 class="twitter-jargons">Candidates</H3>
    <div class="content">
      <div class="row seven-cols">
        
         <g:each in="${solrService.getCandidates()}" var="c">

      <!-- Ele start -->
        <div class="profile profile-${c.party} col-lg-1 col-md-3 col-sm-4 col-xs-6 wrapper">
          <div class="row">
            <img src="${c.profileimgurl}"
            
              class="profile-image">
          </div>
          <div class="row margin-2">
            <h3>${c.username}</h3>
            ${c.content}
          </div>
          <div class="row bottom">
            <div class="col-md-5">
              <span class="twitter-jargons">Tweets</span><br />
              <b>${c.statusescount}</b>
            </div>
            <div class="col-md-7">
              <span class="twitter-jargons">Followers</span><br />
              <b>${c.followerscount}</b>
            </div>

          </div>
        </div>
        <!-- LOOP ele -->
        
        </g:each>
      </div>
        
        
      <!-- Twitter Handles -->
      <div class="row">
      <div class="col-md-6">
      	<a class="twitter-timeline" href="https://twitter.com/hashtag/gopdebate" data-widget-id="703942429817221120">#gopdebate Tweets</a>
		<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
	  </div>	
	  <div class="col-md-6">
		<a class="twitter-timeline" href="https://twitter.com/hashtag/Democrats" data-widget-id="703943259240865792">#Democrats Tweets</a>
		<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
	  </div>
  	  </div>
        
      <div class="row">
          <div class="col-md-12">
            <g:render template="timeline_chart"
                  model="${[chartid:'main-time-chart', 'title':'Temporal range']}"/>
        </div>
      </div>
      <!-- Word Cloud -->  
      <div class="row">
          <div class="col-md-12">
            <g:render template="wordcloud_chart"
                  model="${[chartid:'main-wordcloud-chart', 'title':'Word Cloud']}"/>
        </div>
      </div>
            
      
    </div>
  </div>
    

</body>
</html>
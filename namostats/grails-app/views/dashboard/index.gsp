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
</head>

<body>
<div class="container-fluid">
<H3 class="twitter-jargons">Candidates</H3>
    <div class="content">
      <div class="row">
        
         <g:each in="${solrService.getCandidates()}" var="c">
        
        
    	
      <!-- Ele start --!>
        <div class="profile profile-democrat col-lg-1">
          <div class="row">
            <img src="${c.profileimgurl}"
            
              class="profile-image">
          </div>
          <div class="row margin-2">
            <h3>${c.username}</h3>
            ${c.content}
          </div>
          <div class="row">
            <div class="col-md-6">
              <span class="twitter-jargons">Tweets</span><br />
              <b>${c.statusescount}</b>
            </div>
            <div class="col-md-6">
              <span class="twitter-jargons">Followers</span><br />
              <b>${c.followerscount}</b>
            </div>

          </div>
        </div>
        <!-- LOOP ele -->
        
        </g:each>
        
        <div class="row">
        	<div class="col-md-12">
        		<g:render template="timeline_chart"
            		  model="${[chartid:'main-time-chart', 'title':'Temporal range']}"/>
    		</div>
        </div>
        
      </div>
    </div>
  </div>
    

</body>
</html>

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
        <div class="row seven-cols">

            <g:each in="${solrService.getCandidates()}" var="c">

                <!-- Ele start --!>
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
                <h3>Temporal Trend</h3>
                <g:render template="temporal_multiline" model="${[chartid:'multiline-time-chart', 'title':'Temporal Trend']}">
                </g:render>
            </div>
        </div>
    </div>
</div>

</body>
</html>
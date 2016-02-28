<%-- Created by IntelliJ IDEA. User: tg Date: 2/28/16 Time: 7:22 AM --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    </head>
<body>
<asset:javascript src="application.js" />
<asset:stylesheet src="application.css" />
<link rel="stylesheet" type="text/css" href="/assets/jqcloud.css" />
<div class="container-fluid">

  <div class="row margin-2">
    <div class="profile profile-${c.party} col-lg-3">
      <div class="row">
        <img src="${c.profileimgurl}" class="profile-image-ind">
      </div>
      <div class="row margin-2">
        <h3>${c.username}</h3>
        <h4>@${c.userid}</h4>
        ${c.content}
      </div>

      <div class="row bottom">
        <div class="col-md-5">
          <span class="twitter-jargons">Tweets</span><br /> <b class="number">${formatNumber(number:c.statusescount,
            locale: Locale.ENGLISH, format: '###,##0')}</b>
        </div>
        <div class="col-md-7">
          <span class="twitter-jargons">Followers</span><br /> <b class="number">${formatNumber(number:c.followerscount,
            locale: Locale.ENGLISH, format: '###,##0')}</b>
        </div>
      </div>
    </div>

  </div>
	<!-- Word Cloud -->
        <div class="row">
            <div class="col-md-6">
            <h1><a name="Trends"><small>What's being talked about?<small></a></h1>
                <g:render template="wordcloud_chart"
                          model="${[chartid:'main-wordcloud-chart', 'title':'Word Cloud']}"/>
            </div>
            <div class="col-md-6">
            <!-- Map -->
            <h1><small>Where are Tweets coming from?<small></h1>
            <div>
                <g:render template="google_map_chart"
                          model="${[chartid:'main-map-chart', 'title':'World map']}"/>
                          </div>
            </div>
        </div>
</div>
</body>
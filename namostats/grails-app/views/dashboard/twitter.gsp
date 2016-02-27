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
    <title>Twitter</title>
    <asset:javascript src="application.js"/>
</head>
<body>
<h2>Tweets analysis</h2>
<g:form  class="form-inline" action="twitter">
    <div class="form-group">
        <label for="userid">Twitter UserName</label>
        <input required="true" type="text" class="form-control" name="userid"
               id="userid" placeholder="@thammegowda" value="${userid?:'@thammegowda'}">
    </div>
    <g:actionSubmit value="Show Analytics" action="twitter"/>
    <g:actionSubmit value="Fetch Tweets (slow)" action="indexTwitterTimeline"/>
</g:form>
<hr>

<g:if test="${userid}">

    <div class="row">
        <div class="col-md-12">
        <g:render template="timeline_chart"
              model="${[userid: userid, chartid:'main-time-chart', 'title':'Activity of ' + userid]}"/>
    </div>
    <hr/>

    <div class="row">
        <div class="col-md-6">
            <g:render template="bubblechart"
                      model="${[fieldName:'connections', userid: userid,
                                'title': userid + '\'s Interactions']}"/>
        </div>

        <div class="col-md-6">
            <g:render template="bubblechart"
                      model="${[fieldName:'tags', userid:userid,
                                'title':'Hash Tags used by ' + userid]}"/>
        </div>

        <div class="col-md-6">
            <g:render template="bubblechart"
                      model="${[fieldName:'urlhosts',userid: userid,
                                'title':'Domains of links shared by ' + userid]}"/>
        </div>
        <div class="col-md-6">
            <g:render template="bubblechart"
                      model="${[fieldName:'lang', userid: userid,
                                'title': userid + '\'s languages']}"/>
        </div>
    </div>
</g:if>

</body>
</html>
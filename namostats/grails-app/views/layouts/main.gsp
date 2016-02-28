<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><g:layoutTitle default="Stats"/></title>
    <asset:stylesheet src="application.css"/>
    <g:layoutHead/>
</head>
<body>
<div class="container-fluid">
	<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="/dashboard#">Election 2016</a>
            </div>
			<div id="navbar" class="navbar-collapse collapse">
          	<ul class="nav navbar-nav">
            	<li class="active"><a class="navbar-brand" href="/dashboard#Candidates">Candidates</a></li>
            	<li><a class="navbar-brand" href="/dashboard#Live">Live</a></li>
            	<li><a class="navbar-brand" href="/dashboard#Temporal">Temporal</a></li>
            	<li><a class="navbar-brand" href="/dashboard#Trends">Trends</a></li>
          	</ul>
        	</div>
        </div><!-- /.container-fluid -->
    </nav>
    <div class="content" style="margin-top: 23px;">
        <g:layoutBody/>
    </div>
</div>
<asset:javascript src="application.js"/>
</body>
</html>

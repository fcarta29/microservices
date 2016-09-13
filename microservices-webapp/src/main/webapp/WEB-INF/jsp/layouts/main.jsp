<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<html lang="en">
<head>
    <tiles:insertAttribute name="header"/>
    <title><tiles:getAsString name="title"/></title>
    <tiles:insertAttribute name="style"/>
    
	<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->    
</head>
	<body role="document" onload="disconnect()">
	    <noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being enabled. Please enable Javascript and reload this page!</h2></noscript>
	    
	    <tiles:insertAttribute name="menu" />
	    
	    <tiles:insertAttribute name="body" />
	    
	    <tiles:insertAttribute name="footer" />
	    
	    <script src="/js/sockjs-0.3.4.js"></script>
	    <script src="/js/stomp.js"></script>
	    <script src="/js/jquery-2.1.4.min.js"></script>
	    <script src="/js/jquery-ui.min.js"></script>
	    <script src="/bootstrap/js/bootstrap.min.js"></script>
	    <tiles:insertAttribute name="script" />
	</body>
</html>

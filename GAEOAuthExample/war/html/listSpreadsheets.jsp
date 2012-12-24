<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.zazarie.domain.AppUser" %>
<%@ page import="com.zazarie.shared.Constant" %>
<%@ page import="java.net.URL" %>
<%@ page import="com.google.gdata.client.docs.DocsService" %>
<%@ page import="com.google.gdata.data.docs.*" %>


<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>Google Spreadsheet Listing</title>
	<meta name="generator" content="BBEdit 10.1" />
</head>
<body>
<h4>Spreadsheet Documents</h4>

<%
	AppUser appUser = (AppUser) request.getSession().getAttribute(Constant.AUTH_USER);
	
	URL feedUrl = new URL(Constant.GOOGLE_SPREADSHEET_FEED);
	
	DocsService client = (DocsService) request.getSession().getAttribute(Constant.DOC_SESSION_ID);
	
    DocumentListFeed resultFeed = client.getFeed(feedUrl, DocumentListFeed.class);
%>
<ul>
<%
    for (DocumentListEntry entry : resultFeed.getEntries()) {
        %>
        <li><% out.println(entry.getTitle().getPlainText()); %></li>
    	<%
    }
%>    
</ul>

</body>
</html>

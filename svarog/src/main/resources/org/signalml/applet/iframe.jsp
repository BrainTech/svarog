<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<div>

<table><tr><td style="border: thin solid #C0C0C0;">
<applet alt="No applets" code="org.signalml.applet.SvarogApplet" archive="lib/castor-1.1.1.jar,lib/janino.jar,lib/jcommon-1.0.12.jar,lib/jfreechart-1.0.8.jar,lib/spring-oxm-1.0.3.jar,lib/spring-ws-core-1.0.3.jar,lib/spring-ws-core-tiger-1.0.3.jar,lib/spring-xml-1.0.3.jar,lib/xercesImpl.jar,lib/xstream-1.2.2.jar,lib/commons-cli-1.1.jar,lib/commons-logging.jar,lib/log4j-1.2.14.jar,lib/spring-beans.jar,lib/spring-context.jar,lib/spring-core.jar,signalml-signed.jar"  width="960" height="700">
  <param name="userName" value="<%= request.getParameter("userName") %>" />
  <param name="loginTime" value="<%= request.getParameter("loginTime") %>" />
  <param name="token" value="<%= request.getParameter("token") %>" />
</applet>
</td></tr></table>

</div>

</body>
</html>
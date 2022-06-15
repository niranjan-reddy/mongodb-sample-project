<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%-- <%@taglib uri="java.sun.com/jsp/jstl/core" prefix="c"%> --%>

<%@page import="java.io.*" %>
<%@page import="java.net.*" %>
<%@page import="org.json.JSONObject" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>India COVID19 Distribution Table</title>
</head>
<body>





<%--
   String recv;
   String recvbuff = null;
   URL jsonpage = new URL("http://localhost:8080/india-live-report");
   URLConnection urlcon = jsonpage.openConnection();
   BufferedReader buffread = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

   while ((recv = buffread.readLine()) != null)
    recvbuff += recv;
   buffread.close();

   out.println(recvbuff);
--%>



<%-- <% 
JSONObject inp = new JSONObject(request.getParameter("http://localhost:8080/india-live-report"));

//for(int i=0;i<inp.length();i++)
//{
%>
    <%=inp.getString("payload-INDIA.total-cases")%>
<%
//}
%> --%>



<%-- <c:import var="dataJson" url="http://localhost:8080/india-live-report"/>
<json:parse json="${dataJson}" var="parsedJSON" />

<c:out value="${parsedJSON.node[1].payload-INDIA}" /> --%>

</body>
</html>
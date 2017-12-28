<%@ page import="org.inspirecenter.amazechallenge.data.Parameter" %>
<%@ page import="java.util.List" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %><%--
  User: Nearchos
  Date: 31-Oct-17
  Time: 9:27 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>aMazeChallenge - Parameters</title>
</head>
<body>
    <h1>Parameters</h1>

    <table border="1">
        <tr>
            <td><b>ID</b></td>
            <td><b>Name</b></td>
            <td><b>Value</b></td>
            <td></td>
        </tr>
<%
    final String error = request.getParameter("error");

    final List<Parameter> allParameters = ObjectifyService.ofy()
            .load()
            .type(Parameter.class)
            .list();

    for(final Parameter parameter : allParameters) {
%>
        <tr>
            <td><%=parameter.id%></td>
            <td><%=parameter.name%></td>
            <td><%=parameter.value%></td>
            <td><form action="/admin/delete-parameter" method="post"><input type="hidden" name="id" value="<%=parameter.id%>"/><input type="submit" value="Delete"></form></td>
        </tr>
<%
    }
%>
    </table>

    <p style="color: red;"><%=(error != null && !error.isEmpty()) ? error : ""%></p>
    <h2>Add parameter</h2>
    <form action="/admin/add-parameter" method="post">
        <p><span>Name: <label><input type="text" name="name"/></label></span></p>
        <p><span>Value: <label><input type="text" name="value"/></label></span></p>
        <p><input type="submit"></p>
    </form>

</body>
</html>

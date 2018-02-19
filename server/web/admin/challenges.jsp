<%@ page import="java.util.List" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ page import="org.inspirecenter.amazechallenge.model.Challenge" %><%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 02-Nov-17
  Time: 10:41 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>aMazeChallenge - Challenges</title>
</head>
<body>
    <h1>aMazeChallenge - Challenges</h1>

    <table border="1">
        <tr>
            <td>ID</td>
            <td>Name</td>
            <td>Description</td>
            <td>Grid</td>
        </tr>
    <%
        final String error = request.getParameter("error");

        final List<Challenge> allChallenges = ObjectifyService.ofy()
                .load()
                .type(Challenge.class)
                .list();

        for(final Challenge challenge : allChallenges) {
    %>
        <tr>
            <td><%=challenge.getId()%></td>
            <td><%=challenge.getName()%></td>
            <td><%=challenge.getDescription()%></td>
            <td><%=challenge.getGrid()%></td>
        </tr>
    <%
        }
    %>
    </table>

    <hr/>

    <p style="color: red;"><%=(error != null && !error.isEmpty()) ? error : ""%></p>

    <form action="/admin/add-challenge" method="post" id="add-challenge-form">
        <p><label>Name: <input type="text" name="name" /></label></p>
        <p><label>Description: <textarea rows="10" cols="80" name="description" form="add-challenge-form"></textarea></label></p>
        <p><label>Grid | Width: <input type="number" name="width" /></label><label>Height: <input type="number" name="height" /></label></p>
        <p><label>Starting position | Row: <input type="number" name="startingPositionY" /></label><label>Col: <input type="number" name="startingPositionX" /></label></p>
        <p><label>Target position | Row: <input type="number" name="targetPositionY" /></label><label>Col: <input type="number" name="targetPositionX" /></label></p>
        <p><label>Grid as Hex: <textarea rows="4" cols="80" name="grid" form="add-challenge-form"></textarea></label></p>
        <input type="submit" value="Add Challenge">
    </form>
</body>
</html>

<%@ page session="false" %>
<html>
    <head>
        <title>Formulaire Sprint 11</title>
    </head>
    <body>
        <h2>Formulaire Sprint 11</h2>
        <form action="${pageContext.request.contextPath}/sprint11/session" method="POST">
            <label>Key :</label>
            <input type="text" name="key">
            <br>
            <label>Value :</label>
            <input type="text" name="value">
            <br>
            <button type="submit">Send</button>
        </form>
    </body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Session List</title>
</head>
<body>
    <h2>Session actuelle</h2>
    <%
        Object sessionObj = request.getAttribute("session");
        if (sessionObj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) sessionObj;
            if (map.isEmpty()) {
    %>
                <p>Aucune donnée en session.</p>
    <%
            } else {
    %>
                <p>Nombre d'attributs : <%= map.size() %></p>
                <ul>
                <%
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                %>
                    <li><%= entry.getKey() %> : <%= entry.getValue() %></li>
                <%
                    }
                %>
                </ul>
    <%
            }
        } else {
    %>
        <p>Aucune session trouvée.</p>
    <%
        }
    %>
</body>
</html>

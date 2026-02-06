<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Résultat formulaire</title>
</head>
<body>
<h2>Résultat du formulaire</h2>
<p>Nom (parametre) : ${nom}</p>
<p>Prenom: ${prenom}</p>
<p>Âge: ${age}</p>

<h3>Détail de la Map (liste)</h3>
<%
    Object listeObj = request.getAttribute("liste");
    if (listeObj != null && listeObj instanceof java.util.Map) {
        java.util.Map map = (java.util.Map) listeObj;
%>
    <ul>
<%
        for (Object keyObj : map.keySet()) {
            String key = keyObj.toString();
            Object value = map.get(key);
            if ("langues".equals(key) && value != null && value.getClass().isArray()) {
                String[] langues = (String[]) value;
%>
        <li>
            <strong><%= key %> :</strong>
            <ul>
<%
                for (String l : langues) {
%>
                <li><%= l %></li>
<%
                }
%>
            </ul>
        </li>
<%
            } else {
%>
        <li><strong><%= key %> :</strong> <%= value %></li>
<%
            }
        }
%>
    </ul>
<%
    }
%>
</body>
</html>
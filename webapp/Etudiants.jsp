<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Liste des étudiants</title>
</head>
<body>
<h1>Informations étudiant</h1>
<p>Nom : ${nom}</p>
<p>Age : ${age}</p>

<c:if test="${not empty liste}">
    <h2>Données reçues (liste)</h2>
    <ul>
        <c:forEach var="entry" items="${liste}">
            <li>${entry.key} = ${entry.value}</li>
        </c:forEach>
    </ul>
</c:if>
</body>
</html>

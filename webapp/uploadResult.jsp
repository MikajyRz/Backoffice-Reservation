<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<html>
<head>
    <title>Résultat upload</title>
</head>
<body>
<h1>Upload terminé</h1>
<p>Nom de l'étudiant : ${nom}</p>

<h2>Fichiers reçus</h2>
<c:if test="${empty files}">
    <p>Aucun fichier reçu.</p>
</c:if>
<c:if test="${not empty files}">
    <ul>
        <c:forEach var="entry" items="${files}">
            <li>${entry.key} (taille : ${fn:length(entry.value)} octets)</li>
        </c:forEach>
    </ul>
</c:if>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Résultat Etudiant (Object Binding)</title>
</head>
<body>
<h2>Résultat Etudiant (Object Binding Complexe)</h2>
<c:if test="${not empty etudiant}">
    <p>Nom : ${etudiant.nom}</p>
    <p>Prénom : ${etudiant.prenom}</p>
    <p>Âge : ${etudiant.age}</p>
    <p>Departement ID : ${etudiant.departement.id}</p>
    <p>Departement Nom : ${etudiant.departement.nom}</p>

    <h3>Matières (List)</h3>
    <c:forEach items="${etudiant.matieres}" var="m">
        <p>Code : ${m.code}, Coefficient : ${m.coefficient}, Note : ${m.note.value}</p>
    </c:forEach>
</c:if>
</body>
</html>

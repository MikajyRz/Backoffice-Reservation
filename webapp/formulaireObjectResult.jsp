<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Résultat formulaire (Object Binding)</title>
</head>
<body>
<h2>Résultat du formulaire (Object Binding)</h2>
<p>Nom : ${user.nom}</p>
<p>Prénom : ${user.prenom}</p>
<p>Âge : ${user.age}</p>
<p>Ville : ${user.adresse.ville}</p>
</body>
</html>

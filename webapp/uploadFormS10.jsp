<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Formulaire Upload (sprint10)</title>
</head>
<body>
<h1>Formulaire Upload (sprint10)</h1>
<form method="post" action="${pageContext.request.contextPath}/upload" enctype="multipart/form-data">
    <h3>Infos Etudiant</h3>
    <input name="e.nom" placeholder="Nom"><br>
    <input name="e.prenom" placeholder="Prenom"><br>
    <input name="e.age" placeholder="Age" type="number"><br>

    <h3>Fichiers à uploader</h3>
    <input type="file" name="fichier1"><br>
    <input type="file" name="fichier2"><br>

    <button type="submit">Envoyer (upload)</button>
</form>
</body>
</html>

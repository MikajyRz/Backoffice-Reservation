<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Formulaire Utilisateur</title>
</head>
<body>
    <h2>Formulaire Utilisateur</h2>
    <form action="${pageContext.request.contextPath}/users/result" method="post">
        <label for="nom">Nom :</label>
        <input type="text" id="nom" name="nom" required><br><br>
        <label for="nom">Prenom :</label>
        <input type="text" id="prenom" name="prenom" required><br><br>
        <label for="age">Age :</label>
        <input type="number" id="age" name="age" required><br><br>
        <input type="submit" value="Envoyer">
    </form>

    <hr/>

    <h2>Formulaire Utilisateur (test Map)</h2>
    <form action="${pageContext.request.contextPath}/submitMap" method="post">
        <label for="nom2">Nom :</label>
        <input type="text" id="nom2" name="nom" required><br><br>

        <label for="prenom2">Prenom :</label>
        <input type="text" id="prenom2" name="prenom" required><br><br>

        <label for="age2">Age :</label>
        <input type="number" id="age2" name="age" required><br><br>

        <p>Langues parlées :</p>
        <input type="checkbox" id="francais" name="langues" value="francais">
        <label for="francais">Français</label><br>
        <input type="checkbox" id="anglais" name="langues" value="anglais">
        <label for="anglais">Anglais</label><br>
        <input type="checkbox" id="espagnol" name="langues" value="espagnol">
        <label for="espagnol">Espagnol</label><br><br>

        <input type="submit" value="Envoyer (test Map)">
    </form>

    <hr/>

    <h2>Formulaire Utilisateur (object binding)</h2>
    <form action="${pageContext.request.contextPath}/users/resultObject" method="post">
        <label for="userNom">Nom :</label>
        <input type="text" id="userNom" name="user.nom" required><br><br>

        <label for="userPrenom">Prenom :</label>
        <input type="text" id="userPrenom" name="user.prenom" required><br><br>

        <label for="userAge">Age :</label>
        <input type="number" id="userAge" name="user.age" required><br><br>

        <label for="userVille">Ville :</label>
        <input type="text" id="userVille" name="user.adresse.ville" required><br><br>

        <input type="submit" value="Envoyer (object binding)">
    </form>
</body>
</html>

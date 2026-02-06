<html>
<head>
    <title>Formulaire Etudiant</title>
</head>
<body>
    <h2>Formulaire Etudiant (Object Binding Complexe)</h2>
    <form method="post" action="${pageContext.request.contextPath}/test/bind">

        <h3>Infos Etudiant</h3>
        <input name="e.nom" placeholder="Nom"><br>
        <input name="e.prenom" placeholder="Prenom"><br>
        <input name="e.age" placeholder="Age"><br>

        <h3>Departement</h3>
        <input name="e.departement.id" placeholder="ID Dept"><br>
        <input name="e.departement.nom" placeholder="Nom Dept"><br>

        <h3>Matiere 1</h3>
        <input name="e.matieres[0].code" placeholder="Code matiere">
        <input name="e.matieres[0].coefficient" placeholder="Coefficient">
        <input name="e.matieres[0].note.value" placeholder="Note matiere 1">

        <h3>Matiere 2</h3>
        <input name="e.matieres[1].code" placeholder="Code matiere">
        <input name="e.matieres[1].coefficient" placeholder="Coefficient">
        <input name="e.matieres[1].note.value" placeholder="Note matiere 2">

        <button type="submit">Tester Binder</button>
    </form>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Nouvelle réservation</title>
</head>
<body>
<h2>Créer une réservation</h2>

<form method="post" action="create">
    <div>
        <label>Id client (4 chiffres)</label>
        <input type="text" name="id_client" maxlength="4" minlength="4" required />
    </div>
    <div>
        <label>Nombre passager</label>
        <input type="number" name="nombre_passager" min="1" required />
    </div>
    <div>
        <label>Date/heure arrivée</label>
        <input type="datetime-local" name="date_heure_arrive" required />
    </div>
    <div>
        <label>Id hotel</label>
        <input type="number" name="id_hotel" min="1" required />
    </div>

    <button type="submit">Enregistrer</button>
</form>

<p>
    <a href="../">Retour</a>
</p>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Nouvelle réservation | Backoffice</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body>

<div class="form-card">
    <h2>Créer une réservation</h2>

    <form method="post" action="create">
        <div class="form-group">
            <label for="id_client">Id client (4 chiffres)</label>
            <input type="text" id="id_client" name="id_client" maxlength="4" minlength="4" placeholder="Ex: 1234" required />
        </div>
        
        <div class="form-group">
            <label for="nombre_passager">Nombre passager</label>
            <input type="number" id="nombre_passager" name="nombre_passager" min="1" placeholder="Ex: 2" required />
        </div>
        
        <div class="form-group">
            <label for="date_heure_arrive">Date/heure d'arrivée</label>
            <input type="datetime-local" id="date_heure_arrive" name="date_heure_arrive" required />
        </div>
        
        <div class="form-group">
            <label for="id_hotel">Id hôtel</label>
            <input type="number" id="id_hotel" name="id_hotel" min="1" placeholder="Ex: 10" required />
        </div>

        <button type="submit">Enregistrer la réservation</button>
    </form>

    <a href="${pageContext.request.contextPath}/" class="back-link">← Retour au tableau de bord</a>
</div>

</body>
</html>

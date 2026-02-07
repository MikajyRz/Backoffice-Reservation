<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Réservation enregistrée</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/booking.css" />
</head>
<body>

<%@ include file="fragments/navbar.jspf" %>

<div class="container">
    <div class="header">
        <h2>Réservation enregistrée</h2>
    </div>

    <div class="card">
        <div class="muted">Détails</div>
        <div style="margin-top: 10px; display: grid; gap: 6px;">
            <div><strong>id_client:</strong> ${id_client}</div>
            <div><strong>nombre_passager:</strong> ${nombre_passager}</div>
            <div><strong>date_heure_arrive:</strong> ${date_heure_arrive}</div>
            <div><strong>id_hotel:</strong> ${id_hotel}</div>
        </div>

        <div class="form-actions">
            <a class="link-btn" href="form">Créer une autre réservation</a>
            <a class="link-btn" href="${pageContext.request.contextPath}/">Retour accueil</a>
        </div>
    </div>
</div>
</body>
</html>

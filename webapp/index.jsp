<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Backoffice Reservations</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/booking.css" />
</head>
<body>

<%@ include file="fragments/navbar.jspf" %>

<div class="container">
    <div class="header">
        <h2>Accueil</h2>
    </div>

    <div class="card">
        <div>
            Gérez les réservations via le formulaire Backoffice.
        </div>
        <div class="form-actions">
            <a class="link-btn" href="${pageContext.request.contextPath}/reservation/form">Créer une réservation</a>
        </div>
    </div>
</div>

</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Planification | Backoffice</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/booking.css" />
</head>
<body>

<%@ include file="fragments/navbar.jspf" %>

<div class="container">
    <div class="header">
        <h2>Planifier les trajets</h2>
    </div>

    <div class="card">
        <form method="post" action="plan-date">
            <div class="form-grid">
                <div>
                    <label for="date">Date</label>
                    <input type="date" id="date" name="date" required />
                </div>
            </div>

            <div class="form-actions">
                <button class="btn" type="submit">Planifier</button>
                <a class="link-btn" href="${pageContext.request.contextPath}/">Retour</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>

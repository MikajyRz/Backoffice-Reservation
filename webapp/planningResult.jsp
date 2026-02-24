<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Résultats Planification | Backoffice</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/booking.css" />
</head>
<body>

<%@ include file="fragments/navbar.jspf" %>

<div class="container">
    <div class="header">
        <h2>Résultats de la planification</h2>
    </div>

    <c:if test="${not empty result}">
        <div class="card">
            <h3>Trajets assignés</h3>
            <table>
                <thead>
                    <tr>
                        <th>Véhicule</th>
                        <th>Réservation</th>
                        <th>Lieu</th>
                        <th>Départ</th>
                        <th>Arrivée</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="trip" items="${result.assigned}">
                        <tr>
                            <td>${trip.vehicule}</td>
                            <td>${trip.reservationId}</td>
                            <td>${trip.lieu}</td>
                            <td>${trip.dateDepart}</td>
                            <td>${trip.dateArrivee}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${not empty result.unassigned}">
            <div class="card">
                <h3>Réservations non assignées</h3>
                <ul>
                    <c:forEach var="id" items="${result.unassigned}">
                        <li>Réservation ${id}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
    </c:if>

    <div class="form-actions">
        <a class="link-btn" href="${pageContext.request.contextPath}/planning">Nouvelle planification</a>
        <a class="link-btn" href="${pageContext.request.contextPath}/">Retour</a>
    </div>
</div>

</body>
</html>

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
            <p>Voici la liste des réservations qui ont pu être assignées à un véhicule.</p>
            <table>
                <thead>
                    <tr>
                        <th>Véhicule</th>
                        <th>Détails Véhicule</th>
                        <th>Réservation</th>
                        <th>Passagers</th>
                        <th>Lieu</th>
                        <th>Départ</th>
                        <th>Arrivée</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="trip" items="${result.assigned}">
                        <tr>
                            <td><strong>${trip.vehicule}</strong></td>
                            <td>
                                <span class="badge badge-info">${trip.vehiculeDetails.type_carburant}</span>
                                <span class="badge">${trip.vehiculeDetails.nb_place} places</span>
                            </td>
                            <td>#${trip.reservationId}</td>
                            <td>${trip.nbPassagers}</td>
                            <td>${trip.lieu}</td>
                            <td>${trip.dateDepart}</td>
                            <td>${trip.dateArrivee}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${not empty result.unusedVehicles}">
            <div class="card">
                <h3>Véhicules non utilisés</h3>
                <p>Ces véhicules étaient disponibles mais n'ont pas été nécessaires ou ne correspondaient pas aux critères.</p>
                <table>
                    <thead>
                        <tr>
                            <th>Immatriculation</th>
                            <th>Carburant</th>
                            <th>Capacité</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="v" items="${result.unusedVehicles}">
                            <tr>
                                <td>${v.immatricule}</td>
                                <td>${v.type_carburant}</td>
                                <td>${v.nb_place} places</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>

        <c:if test="${not empty result.unassigned}">
            <div class="card">
                <h3>⚠️ Réservations non assignées</h3>
                <p class="error-text">Aucun véhicule disponible n'a pu satisfaire ces demandes.</p>
                <table>
                    <thead>
                        <tr>
                            <th>Réservation</th>
                            <th>Client</th>
                            <th>Passagers</th>
                            <th>Lieu</th>
                            <th>Date & Heure</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="res" items="${result.unassigned}">
                            <tr>
                                <td>#${res.id}</td>
                                <td>${res.id_client}</td>
                                <td>${res.nombre_passager}</td>
                                <td>${res.lieu_nom}</td>
                                <td>${res.date_heure_arrive}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
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

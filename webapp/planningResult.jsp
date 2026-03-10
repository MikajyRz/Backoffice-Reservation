<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Résultats Planification | Backoffice</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/booking.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
        /* Style inspiré de Booking.com */
        body {
            background-color: #f5f7fa;
            color: #262626;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }
        
        .header {
            margin-bottom: 24px;
        }

        .card {
            background: #fff;
            border: 1px solid #e7e7e7;
            border-radius: 8px; /* Coins légèrement arrondis */
            padding: 20px;
            margin-bottom: 24px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.04); /* Ombre très légère */
        }

        .card h3 {
            font-size: 18px;
            font-weight: 700;
            margin-bottom: 16px;
            color: #262626;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        /* Tableau épuré */
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }

        thead th {
            background-color: #f2f2f2; /* Gris très clair pour l'en-tête */
            color: #4a4a4a;
            font-weight: 600;
            text-align: left;
            padding: 12px 16px;
            border-bottom: 1px solid #e7e7e7;
            text-transform: uppercase;
            font-size: 12px;
            letter-spacing: 0.5px;
        }

        tbody td {
            padding: 14px 16px;
            border-bottom: 1px solid #e7e7e7; /* Bordure fine entre les lignes */
            vertical-align: middle;
            color: #262626;
        }

        tbody tr:last-child td {
            border-bottom: none;
        }

        tbody tr:hover {
            background-color: #f9f9f9; /* Effet hover subtil */
        }

        /* Badges plus discrets */
        .badge {
            display: inline-block;
            padding: 4px 8px;
            font-size: 12px;
            font-weight: 600;
            border-radius: 4px; /* Coins carrés arrondis */
            line-height: 1;
        }

        .badge-info {
            background-color: #e6f7ff;
            color: #0057b8; /* Bleu Booking */
            border: 1px solid #bae7ff;
        }

        .badge-success {
            background-color: #f6ffed;
            color: #389e0d;
            border: 1px solid #b7eb8f;
        }
        
        .badge-danger {
            background-color: #fff1f0;
            color: #cf1322;
            border: 1px solid #ffa39e;
        }

        .link-btn {
            background-color: #0071c2; /* Bleu Booking */
            color: white;
            font-weight: 600;
            border-radius: 4px;
            padding: 10px 20px;
            text-decoration: none;
            transition: background-color 0.2s;
            display: inline-block;
            margin-right: 10px;
        }

        .link-btn:hover {
            background-color: #005999;
        }

        .error-text {
            color: #d93025;
            font-weight: 500;
        }
    </style>
</head>
<body>

<%@ include file="fragments/navbar.jspf" %>

<div class="container">
    <div class="header">
        <h2><i class="fas fa-calendar-check" style="color: #0071c2;"></i> Résultats de la planification</h2>
    </div>

    <c:if test="${not empty result}">
        <div class="card">
            <h3><i class="fas fa-check-circle" style="color: #28a745;"></i> Trajets assignés</h3>
            <p>Voici la liste des réservations qui ont pu être assignées à un véhicule.</p>
            <table>
                <thead>
                    <tr>
                        <th style="width: 50px;"># ID</th> <!-- Nouvelle colonne ID -->
                        <th>Véhicule</th>
                        <th>Détails Véhicule</th>
                        <th>Réservation</th>
                        <th>Passagers</th>
                        <th>Lieu</th>
                        <th>Départ</th>
                        <th>Arrivée</th>
                        <th>Retour Aéroport</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="trip" items="${result.assigned}">
                        <tr>
                            <td><strong>${trip.vehiculeDetails.id}</strong></td>
                            <td><strong style="color: #0071c2;">${trip.vehicule}</strong></td>
                            <td>
                                <span class="badge badge-info">${trip.vehiculeDetails.type_carburant}</span>
                                <span class="badge badge-success">${trip.vehiculeDetails.nb_place} places</span>
                            </td>
                            <td>#${trip.reservationId}</td>
                            <td>${trip.nbPassagers}</td>
                            <td>${trip.lieu}</td>
                            <td>${trip.dateDepart}</td>
                            <td>${trip.dateArrivee}</td>
                            <td>${trip.dateRetourAeroport}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${not empty result.unassigned}">
            <div class="card">
                <h3 style="color: #d93025;"><i class="fas fa-exclamation-circle"></i> Réservations non assignées</h3>
                <p class="error-text">Aucun véhicule disponible n'a pu satisfaire ces demandes.</p>
                <table>
                    <thead>
                        <tr>
                            <th>Réservation</th>
                            <th>Client</th>
                            <th>Passagers</th>
                            <th>Lieu</th>
                            <th>Date & Heure</th>
                            <th>Statut</th>
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
                                <td><span class="badge badge-danger">Non Assigné</span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>

        <c:if test="${not empty result.unusedVehicles}">
            <div class="card">
                <h3><i class="fas fa-warehouse" style="color: #6b7280;"></i> Véhicules non utilisés</h3>
                <p>Ces véhicules étaient disponibles mais n'ont pas été nécessaires ou ne correspondaient pas aux critères.</p>
                <table>
                    <thead>
                        <tr>
                            <th style="width: 50px;"># ID</th>
                            <th>Immatriculation</th>
                            <th>Carburant</th>
                            <th>Capacité</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="v" items="${result.unusedVehicles}">
                            <tr>
                                <td>${v.id}</td>
                                <td>${v.immatricule}</td>
                                <td>${v.type_carburant}</td>
                                <td>${v.nb_place} places</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </c:if>

    <div class="form-actions" style="margin-top: 30px; text-align: center;">
        <a class="link-btn" href="${pageContext.request.contextPath}/reservation/planning">Nouvelle planification</a>
        <a class="link-btn" style="background-color: #6b7280;" href="${pageContext.request.contextPath}/">Retour</a>
    </div>
</div>

</body>
</html>

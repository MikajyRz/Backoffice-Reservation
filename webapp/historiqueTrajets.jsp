<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Historique des trajets | Backoffice</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/booking.css" />
    <style>
        .table-wrap {
            width: 100%;
            overflow-x: auto;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
            min-width: 980px;
        }
        thead th {
            background-color: #f2f2f2;
            color: #4a4a4a;
            font-weight: 600;
            text-align: left;
            padding: 14px 16px;
            border-bottom: 1px solid #e7e7e7;
            white-space: nowrap;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        tbody td {
            padding: 14px 16px;
            border-bottom: 1px solid #e7e7e7;
            vertical-align: middle;
            color: #262626;
        }
        tbody tr:last-child td {
            border-bottom: none;
        }
        tbody tr:hover {
            background-color: #f9f9f9;
        }
        .cell-mono {
            font-variant-numeric: tabular-nums;
            white-space: nowrap;
        }
        .cell-stops {
            color: #111827;
            line-height: 1.25rem;
        }
        .badge {
            display: inline-block;
            padding: 4px 8px;
            font-size: 12px;
            font-weight: 600;
            border-radius: 6px;
            line-height: 1;
        }
        .badge-info {
            background-color: #e6f7ff;
            color: #0057b8;
            border: 1px solid #bae7ff;
        }
        .badge-success {
            background-color: #f6ffed;
            color: #389e0d;
            border: 1px solid #b7eb8f;
        }
        .stops-list {
            margin: 0;
            padding: 0;
            list-style: none;
            display: grid;
            gap: 6px;
        }
        .stop-item {
            display: grid;
            grid-template-columns: 34px 1fr auto;
            gap: 10px;
            align-items: center;
            padding: 6px 8px;
            border: 1px solid #e7e7e7;
            border-radius: 8px;
            background: #ffffff;
        }
        .stop-ordre {
            width: 34px;
            text-align: center;
            font-weight: 800;
            color: #0f172a;
        }
        .stop-lieu {
            font-weight: 700;
            color: #111827;
        }
        .stop-arrivee {
            font-variant-numeric: tabular-nums;
            white-space: nowrap;
            color: #374151;
            font-weight: 700;
        }
    </style>
</head>
<body>

<%@ include file="fragments/navbar.jspf" %>

<div class="container">
    <div class="header">
        <h2>Historique des trajets</h2>
    </div>

    <div class="card">
        <form method="get" action="historique-trajets">
            <div class="form-grid">
                <div>
                    <label for="date">Jour</label>
                    <input type="date" id="date" name="date" value="${selectedDate}" required />
                </div>
            </div>

            <div class="form-actions">
                <button class="btn" type="submit">Filtrer</button>
                <a class="link-btn" href="${pageContext.request.contextPath}/">Retour</a>
            </div>
        </form>
    </div>

    <c:if test="${not empty trajets}">
        <div class="card">
            <h3>Trajets</h3>
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>Tournée</th>
                            <th>Véhicule</th>
                            <th>Réservations</th>
                            <th>Fenêtre</th>
                            <th>Départ effectif</th>
                            <th>Retour Aéroport</th>
                            <th>Passagers</th>
                            <th>Stops</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="t" items="${trajets}">
                            <tr>
                                <td class="cell-mono"><span class="badge badge-info">#${t.tourneeId}</span></td>
                                <td><span class="badge badge-info">${t.vehicule}</span></td>
                                <td>
                                    <c:if test="${not empty t.reservationIds}">
                                        <c:forEach var="rid" items="${t.reservationIds}">
                                            <span class="badge badge-info">#${rid}</span>
                                        </c:forEach>
                                    </c:if>
                                </td>
                                <td class="cell-mono">${t.windowStart} - ${t.windowEnd}</td>
                                <td class="cell-mono">${t.departEffectif}</td>
                                <td class="cell-mono">${t.retourAeroport}</td>
                                <td class="cell-mono"><span class="badge badge-success">${t.nbPassagersTotal}</span></td>
                                <td class="cell-stops">
                                    <c:if test="${not empty t.stops}">
                                        <ul class="stops-list">
                                            <c:forEach var="s" items="${t.stops}">
                                                <li class="stop-item">
                                                    <div class="stop-ordre">${s.ordre}</div>
                                                    <div class="stop-lieu">${s.lieu}</div>
                                                    <div class="stop-arrivee">${s.arrivee}</div>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <c:if test="${empty trajets}">
        <div class="card">
            <div>Aucun trajet pour ce jour.</div>
        </div>
    </c:if>
</div>

</body>
</html>

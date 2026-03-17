<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="test.java.VoitureRow" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Voitures</title>
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
            min-width: 760px;
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

        .badge {
            display: inline-block;
            padding: 4px 8px;
            font-size: 12px;
            font-weight: 600;
            border-radius: 6px;
            line-height: 1;
            white-space: nowrap;
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

        .badge-danger {
            background-color: #fff1f0;
            color: #cf1322;
            border: 1px solid #ffa39e;
        }

        .actions {
            display: inline-flex;
            gap: 10px;
            align-items: center;
            flex-wrap: wrap;
        }

        .btn-danger {
            background: #d93025;
            color: #fff;
        }
        .btn-danger:hover {
            filter: brightness(0.95);
        }
    </style>
</head>
<body>

<%@ include file="fragments/navbar.jspf" %>

<div class="container">
    <div class="header">
        <h2>Voitures</h2>
    </div>

    <div class="card">
        <div class="form-actions" style="margin-bottom: 12px;">
            <a class="link-btn" href="${pageContext.request.contextPath}/voiture/form">Créer une voiture</a>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Immatricule</th>
                    <th>Carburant</th>
                    <th>Places</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <%
                    Object vObj = request.getAttribute("voitures");
                    List<VoitureRow> voitures = vObj != null ? (List<VoitureRow>) vObj : new ArrayList<>();
                    for (VoitureRow v : voitures) {
                %>
                <tr>
                    <td><%= v.getId() %></td>
                    <td><%= v.getImmatricule() %></td>
                    <td><span class="badge badge-info"><%= v.getType_carburant() %></span></td>
                    <td><span class="badge badge-success"><%= v.getNb_place() %> places</span></td>
                    <td>
                        <div class="actions">
                            <a class="link-btn" href="${pageContext.request.contextPath}/voiture/form?id=<%= v.getId() %>">Éditer</a>
                            <form method="post" action="${pageContext.request.contextPath}/voiture/delete" style="display:inline;">
                                <input type="hidden" name="id" value="<%= v.getId() %>" />
                                <button class="btn btn-danger" type="submit">Supprimer</button>
                            </form>
                        </div>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>

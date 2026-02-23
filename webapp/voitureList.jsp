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
                    <td><%= v.getType_carburant() %></td>
                    <td><%= v.getNb_place() %></td>
                    <td>
                        <a class="link-btn" href="${pageContext.request.contextPath}/voiture/form?id=<%= v.getId() %>">Éditer</a>
                        <form method="post" action="${pageContext.request.contextPath}/voiture/delete" style="display:inline;">
                            <input type="hidden" name="id" value="<%= v.getId() %>" />
                            <button class="btn" type="submit">Supprimer</button>
                        </form>
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

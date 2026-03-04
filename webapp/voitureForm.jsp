<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="test.java.VoitureRow" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Voiture</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/booking.css" />
</head>
<body>

<%@ include file="fragments/navbar.jspf" %>

<%
    String error = (String) request.getAttribute("error");
    VoitureRow v = (VoitureRow) request.getAttribute("voiture");
    // On considère en mode édition SEULEMENT si l'ID est > 0
    boolean edit = (v != null && v.getId() > 0);
%>

<div class="container">
    <div class="header">
        <h2><%= edit ? "Éditer une voiture" : "Créer une voiture" %></h2>
    </div>

    <% if (error != null) { %>
        <div style="color: red; margin-bottom: 15px; padding: 10px; background-color: #ffe6e6; border: 1px solid #ffcccc; border-radius: 4px;">
            <%= error %>
        </div>
    <% } %>

    <div class="card">
        <form method="post" action="${pageContext.request.contextPath}<%= edit ? "/voiture/update" : "/voiture/create" %>">
            <% if (edit) { %>
                <input type="hidden" name="id" value="<%= v.getId() %>" />
            <% } %>

            <div class="field">
                <label>Immatricule</label>
                <input type="text" name="immatricule" value="<%= (v != null) ? v.getImmatricule() : "" %>" required />
            </div>

            <div class="field">
                <label>Type carburant</label>
                <select name="type_carburant" required>
                    <option value="E" <%= (v != null && "E".equals(v.getType_carburant())) ? "selected" : "" %>>Essence (E)</option>
                    <option value="D" <%= (v != null && "D".equals(v.getType_carburant())) ? "selected" : "" %>>Diesel (D)</option>
                    <option value="El" <%= (v != null && "El".equals(v.getType_carburant())) ? "selected" : "" %>>Electrique (El)</option>
                    <option value="H" <%= (v != null && "H".equals(v.getType_carburant())) ? "selected" : "" %>>Hybride (H)</option>
                </select>
            </div>

            <div class="field">
                <label>Nombre de places</label>
                <input type="number" min="1" name="nb_place" value="<%= (v != null) ? v.getNb_place() : 4 %>" required />
            </div>

            <div class="form-actions">
                <button class="btn" type="submit"><%= edit ? "Mettre à jour" : "Créer" %></button>
                <a class="link-btn" href="${pageContext.request.contextPath}/voiture">Retour</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>

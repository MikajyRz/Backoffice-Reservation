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
    VoitureRow v = (VoitureRow) request.getAttribute("voiture");
    boolean edit = v != null;
%>

<div class="container">
    <div class="header">
        <h2><%= edit ? "Éditer une voiture" : "Créer une voiture" %></h2>
    </div>

    <div class="card">
        <form method="post" action="${pageContext.request.contextPath}<%= edit ? "/voiture/update" : "/voiture/create" %>">
            <% if (edit) { %>
                <input type="hidden" name="id" value="<%= v.getId() %>" />
            <% } %>

            <div class="field">
                <label>Immatricule</label>
                <input type="text" name="immatricule" value="<%= edit ? v.getImmatricule() : "" %>" required />
            </div>

            <div class="field">
                <label>Type carburant</label>
                <select name="type_carburant" required>
                    <option value="E" <%= edit && "E".equals(v.getType_carburant()) ? "selected" : "" %>>Essence (E)</option>
                    <option value="D" <%= edit && "D".equals(v.getType_carburant()) ? "selected" : "" %>>Diesel (D)</option>
                    <option value="El" <%= edit && "El".equals(v.getType_carburant()) ? "selected" : "" %>>Electrique (El)</option>
                    <option value="H" <%= edit && "H".equals(v.getType_carburant()) ? "selected" : "" %>>Hybride (H)</option>
                </select>
            </div>

            <div class="field">
                <label>Nombre de places</label>
                <input type="number" min="1" name="nb_place" value="<%= edit ? v.getNb_place() : 4 %>" required />
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

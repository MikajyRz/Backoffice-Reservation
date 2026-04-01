<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="test.java.VoitureRow" %>
<%@ page import="test.java.VoitureController.VoitureDisponibiliteRow" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Disponibilité des voitures</title>
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
            min-width: 860px;
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

        .alert {
            border: 1px solid var(--border);
            border-radius: 12px;
            padding: 12px 14px;
            margin-bottom: 12px;
            font-weight: 700;
        }
        .alert-error {
            background: #fff5f5;
            border-color: rgba(220,53,69,0.25);
            color: #b02a37;
        }
        .alert-success {
            background: #f0fff4;
            border-color: rgba(25,135,84,0.25);
            color: #146c43;
        }

        select,
        input[type="time"] {
            width: 100%;
            background: white;
            border: 1px solid var(--border);
            border-radius: 10px;
            padding: 10px 12px;
            color: var(--text);
            outline: none;
        }
        select:focus,
        input[type="time"]:focus {
            border-color: rgba(0, 87, 184, 0.55);
            box-shadow: 0 0 0 3px rgba(0, 87, 184, 0.12);
        }

        .badge-success {
            background-color: #198754;
            color: white;
        }

        .badge-light {
            background-color: #e9ecef;
            color: #212529;
        }
    </style>
</head>
<body>
<jsp:include page="fragments/navbar.jspf" />

<div class="container">
    <div class="header">
        <h2>Disponibilité des voitures</h2>
        <div class="muted">Définir l'heure à partir de laquelle une voiture peut être utilisée sur une date donnée.</div>
    </div>

    <div class="card">
        <%
            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");
            if (error != null && !error.trim().isEmpty()) {
        %>
        <div class="alert alert-error"><%= error %></div>
        <%
            }
            if (success != null && !success.trim().isEmpty()) {
        %>
        <div class="alert alert-success"><%= success %></div>
        <%
            }
        %>

        <%
            Object vObj = request.getAttribute("voitures");
            List<VoitureRow> voitures = vObj != null ? (List<VoitureRow>) vObj : new ArrayList<>();

            String jour = (String) request.getAttribute("jour");
            if (jour == null) jour = "";
        %>

        <form method="get" action="${pageContext.request.contextPath}/voiture/disponibilite">
            <div class="form-grid">
                <div>
                    <label for="jour">Jour</label>
                    <input type="date" id="jour" name="jour" value="<%= jour %>" />
                </div>
                <div class="form-actions" style="align-items: end; margin-top: 0;">
                    <button class="btn" type="submit">Filtrer</button>
                    <a class="link-btn" href="${pageContext.request.contextPath}/voiture/disponibilite">Réinitialiser</a>
                </div>
            </div>
        </form>

        <hr style="border: none; border-top: 1px solid var(--border); margin: 14px 0;" />

        <form method="post" action="${pageContext.request.contextPath}/voiture/disponibilite/save">
            <div class="form-grid">
                <div>
                    <label for="id_voiture">Voiture</label>
                    <select id="id_voiture" name="id_voiture" required>
                        <option value="">-- choisir --</option>
                        <%
                            for (VoitureRow v : voitures) {
                        %>
                        <option value="<%= v.getId() %>">#<%= v.getId() %> - <%= v.getImmatricule() %> (<%= v.getNb_place() %> places)</option>
                        <%
                            }
                        %>
                    </select>
                </div>
                <div>
                    <label for="jour_save">Jour</label>
                    <input type="date" id="jour_save" name="jour" value="<%= jour %>" required />
                </div>
                <div>
                    <label for="heure_dispo">Heure de disponibilité</label>
                    <input type="time" id="heure_dispo" name="heure_dispo" value="00:00" required />
                </div>
                <div class="form-actions" style="align-items: end; margin-top: 0;">
                    <button class="btn" type="submit">Enregistrer</button>
                </div>
            </div>
        </form>

        <div class="muted">Astuce: si aucune ligne n'existe pour un véhicule/jour, il est considéré disponible dès 00:00 (comportement par défaut).</div>
    </div>

    <div class="card" style="margin-top: 14px;">
        <div class="header" style="margin-top: 0;">
            <h2 style="font-size: 18px;">Disponibilités enregistrées</h2>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Jour</th>
                    <th>Voiture</th>
                    <th>Heure dispo</th>
                </tr>
                </thead>
                <tbody>
                <%
                    Object dObj = request.getAttribute("disponibilites");
                    List<VoitureDisponibiliteRow> disponibilites = dObj != null ? (List<VoitureDisponibiliteRow>) dObj : new ArrayList<>();

                    if (disponibilites.isEmpty()) {
                %>
                <tr>
                    <td colspan="3" class="muted">Aucune disponibilité enregistrée pour ce filtre.</td>
                </tr>
                <%
                    } else {
                        for (VoitureDisponibiliteRow r : disponibilites) {
                %>
                <tr>
                    <td><span class="badge badge-light"><%= r.getJour() %></span></td>
                    <td><span class="badge badge-info"><%= r.getImmatricule() %></span></td>
                    <td><span class="badge badge-success"><%= r.getHeureDispo() %></span></td>
                </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>

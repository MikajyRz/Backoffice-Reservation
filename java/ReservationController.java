package test.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.annotations.Api;
import com.annotations.ControllerAnnotation;
import com.annotations.GetMapping;
import com.annotations.Param;
import com.annotations.PostMapping;
import com.classes.ModelView;
import com.utils.DbUtil;

@ControllerAnnotation("/reservation")
public class ReservationController {

    @GetMapping("/reservation/form")
    public ModelView form() {
        ModelView mv = new ModelView();
        mv.setView("reservationForm.jsp");
        return mv;
    }

    @PostMapping("/reservation/create")
    public ModelView create(
            @Param("id_client") String idClient,
            @Param("nombre_passager") int nombrePassager,
            @Param("date_heure_arrive") String dateHeureArrive,
            @Param("id_lieu") int idLieu) {

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_lieu) VALUES (?, ?, ?, ?)")) {

            ps.setString(1, idClient);
            ps.setInt(2, nombrePassager);

            LocalDateTime ldt = LocalDateTime.parse(dateHeureArrive);
            ps.setTimestamp(3, Timestamp.valueOf(ldt));

            ps.setInt(4, idLieu);

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ModelView mv = new ModelView();
        mv.setView("reservationSuccess.jsp");
        mv.addData("id_client", idClient);
        mv.addData("nombre_passager", nombrePassager);
        mv.addData("date_heure_arrive", dateHeureArrive);
        mv.addData("id_lieu", idLieu);
        return mv;
    }

    @Api
    @PostMapping("/api/reservations")
    public String apiCreate(
            @Param("id_client") String idClient,
            @Param("nombre_passager") int nombrePassager,
            @Param("date_heure_arrive") String dateHeureArrive,
            @Param("id_lieu") int idLieu) {

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_lieu) VALUES (?, ?, ?, ?)")) {

            ps.setString(1, idClient);
            ps.setInt(2, nombrePassager);

            LocalDateTime ldt = LocalDateTime.parse(dateHeureArrive);
            ps.setTimestamp(3, Timestamp.valueOf(ldt));

            ps.setInt(4, idLieu);

            ps.executeUpdate();
            return "OK";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Api
    @GetMapping("/api/reservations")
    public List<ReservationRow> apiList() {
        List<ReservationRow> rows = new ArrayList<>();

        String sql = "SELECT r.id, r.id_client, r.nombre_passager, r.date_heure_arrive, r.id_lieu, l.libelle AS lieu_nom "
                + "FROM reservation r JOIN lieu l ON l.id = r.id_lieu "
                + "ORDER BY r.id DESC";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String idClient = rs.getString("id_client");
                int nb = rs.getInt("nombre_passager");
                Timestamp ts = rs.getTimestamp("date_heure_arrive");
                int idLieu = rs.getInt("id_lieu");
                String lieuNom = rs.getString("lieu_nom");

                rows.add(new ReservationRow(
                        id,
                        idClient,
                        nb,
                        ts != null ? ts.toLocalDateTime().toString() : null,
                        idLieu,
                        lieuNom
                ));
            }

            return rows;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Api
    @PostMapping("/api/plan-date")
    public Map<String, Object> planDate(@Param("date") String dateStr) {
        // Étape 1.1 : Parser la date
        LocalDate date = LocalDate.parse(dateStr);

        // Étape 1.2 : Récupérer réservations de la date sans véhicule, groupées par lieu avec total passagers
        Map<Integer, List<ReservationRow>> reservationsByLieu = getReservationsByLieuForDate(date);

        // Étape 1.3 : Récupérer véhicules disponibles
        List<VoitureRow> vehicles = listAllVehicles();

        // Étape 1.4 : Assigner véhicules par lieu
        List<Map<String, Object>> assigned = new ArrayList<>();
        List<ReservationRow> unassigned = new ArrayList<>();
        List<VoitureRow> availableVehicles = new ArrayList<>(vehicles);

        // On récupère d'abord les véhicules déjà assignés pour cette date (pour l'affichage complet)
        // Mais attention : ici on planifie les NON assignés. 
        // Si on veut afficher TOUT, il faut fusionner après.

        try (Connection con = DbUtil.getConnection()) {
            con.setAutoCommit(false);
            try {
                for (Map.Entry<Integer, List<ReservationRow>> entry : reservationsByLieu.entrySet()) {
                    int lieuId = entry.getKey();
                    List<ReservationRow> resList = entry.getValue();
                    List<ReservationRow> pending = new ArrayList<>(resList);

                    while (!pending.isEmpty() && !availableVehicles.isEmpty()) {
                        int needed = pending.stream().mapToInt(ReservationRow::getNombre_passager).sum();

                        // 1. Essayer de trouver un véhicule pour TOUT le monde
                        VoitureRow bestVehicle = findBestVehicle(availableVehicles, needed);

                        // 2. Si pas trouvé, prendre le plus grand disponible pour en prendre une partie
                        if (bestVehicle == null) {
                             bestVehicle = availableVehicles.stream()
                                    .max(Comparator.comparingInt(VoitureRow::getNb_place))
                                    .orElse(null);
                        }

                        if (bestVehicle == null) break; // Plus de véhicules disponibles

                        // 3. Remplir ce véhicule (Greedy)
                        List<ReservationRow> assignedToThis = new ArrayList<>();
                        int currentLoad = 0;
                        int capacity = bestVehicle.getNb_place();

                        Iterator<ReservationRow> it = pending.iterator();
                        while (it.hasNext()) {
                            ReservationRow res = it.next();
                            if (currentLoad + res.getNombre_passager() <= capacity) {
                                assignedToThis.add(res);
                                currentLoad += res.getNombre_passager();
                                it.remove();
                            }
                        }

                        if (assignedToThis.isEmpty()) {
                            break; 
                        }

                        // Assigner et calculer horaires pour ce lot
                        availableVehicles.remove(bestVehicle);

                        for (ReservationRow res : assignedToThis) {
                            Map<String, Object> trip = new HashMap<>();
                            trip.put("vehicule", bestVehicle.getImmatricule());
                            trip.put("vehiculeDetails", bestVehicle); // Ajout détails véhicule
                            trip.put("reservationId", res.getId());
                            trip.put("lieu", res.getLieu_nom());
                            trip.put("nbPassagers", res.getNombre_passager()); // Ajout nb passagers
                            trip.put("dateDepart", res.getDate_heure_arrive());
                            String arrivee = calculateArrival(res, bestVehicle, lieuId);
                            trip.put("dateArrivee", arrivee);
                            assigned.add(trip);

                            // Mettre à jour DB
                            try (PreparedStatement ps = con.prepareStatement(
                                    "UPDATE reservation SET id_voiture = ? WHERE id = ?")) {
                                ps.setInt(1, bestVehicle.getId());
                                ps.setInt(2, res.getId());
                                ps.executeUpdate();
                            }
                        }
                    }

                    // Ce qui reste dans pending n'a pas été assigné
                    if (!pending.isEmpty()) {
                        unassigned.addAll(pending);
                    }
                }
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Ajouter les véhicules non utilisés à la réponse pour info
        Map<String, Object> result = new HashMap<>();
        result.put("assigned", assigned);
        result.put("unassigned", unassigned);
        result.put("unusedVehicles", availableVehicles); // Véhicules restants sans mission
        return result;
    }

    private Map<Integer, List<ReservationRow>> getReservationsByLieuForDate(LocalDate date) {
        Map<Integer, List<ReservationRow>> map = new HashMap<>();
        String sql = "SELECT r.id, r.id_client, r.nombre_passager, r.date_heure_arrive, r.id_lieu, l.libelle AS lieu_nom "
                + "FROM reservation r JOIN lieu l ON l.id = r.id_lieu "
                + "WHERE DATE(r.date_heure_arrive) = ? AND r.id_voiture IS NULL "
                + "ORDER BY r.id_lieu, r.id";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String idClient = rs.getString("id_client");
                    int nb = rs.getInt("nombre_passager");
                    String dateHeure = rs.getTimestamp("date_heure_arrive").toLocalDateTime().toString();
                    int idLieu = rs.getInt("id_lieu");
                    String lieuNom = rs.getString("lieu_nom");

                    ReservationRow res = new ReservationRow(id, idClient, nb, dateHeure, idLieu, lieuNom);
                    map.computeIfAbsent(idLieu, k -> new ArrayList<>()).add(res);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    private static List<VoitureRow> listAllVehicles() {
        List<VoitureRow> rows = new ArrayList<>();
        String sql = "SELECT id, immatricule, type_carburant, nb_place FROM voiture ORDER BY id DESC";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new VoitureRow(
                        rs.getInt("id"),
                        rs.getString("immatricule"),
                        rs.getString("type_carburant"),
                        rs.getInt("nb_place")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return rows;
    }

    private static VoitureRow findBestVehicle(List<VoitureRow> available, int totalPassengers) {
        System.out.println("[Planification] Recherche véhicule pour " + totalPassengers + " passagers.");
        
        // On filtre d'abord les véhicules qui ont assez de places
        List<VoitureRow> candidates = available.stream()
                .filter(v -> v.getNb_place() >= totalPassengers)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            System.out.println("[Planification] AUCUN véhicule n'a une capacité >= " + totalPassengers);
            return null;
        }

        // Tri selon les règles :
        // 1. Capacité la plus proche (différence minimale)
        // 2. Priorité au Diesel ('D') si capacité égale
        // 3. ID (pour le random déterministe)
        VoitureRow best = candidates.stream()
                .min(Comparator.comparingInt((VoitureRow v) -> Math.abs(v.getNb_place() - totalPassengers))
                        .thenComparing((VoitureRow v) -> {
                            String carb = v.getType_carburant() != null ? v.getType_carburant().trim() : "";
                            return "D".equalsIgnoreCase(carb) ? 1 : 0;
                        }, Comparator.reverseOrder())
                        .thenComparingInt(VoitureRow::getId))
                .orElse(null);

        if (best != null) {
            System.out.println("[Planification] Véhicule choisi : " + best.getImmatricule() + " (" + best.getNb_place() + " places, Type: " + best.getType_carburant() + ")");
        }
        return best;
    }

    private static String calculateArrival(ReservationRow res, VoitureRow vehicle, int lieuId) {
        int distance = getDistance(lieuId, 1); // Vers l'aéroport (Lieu 1)
        double vitesse = getVitesse();
        int attente = getAttente();
        
        LocalDateTime depart = LocalDateTime.parse(res.getDate_heure_arrive());
        
        // Calcul du trajet en minutes : (distance / vitesse) * 60
        double trajetMinutesDouble = (distance > 0) ? ((double) distance / vitesse) * 60 : 0;
        long trajetMinutes = Math.round(trajetMinutesDouble);
        
        LocalDateTime arrivee = depart.plusMinutes(trajetMinutes).plusMinutes(attente);
        
        // Formatage simple pour l'affichage
        return arrivee.toString().replace("T", " ");
    }

    private static int getDistance(int from, int to) {
        String sql = "SELECT kilometer FROM distance WHERE from_lieu = ? AND to_lieu = ?";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, from);
            ps.setInt(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private static double getVitesse() {
        String sql = "SELECT vitesse_moyenne FROM parametre";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 60.0; // default
    }

    private static int getAttente() {
        String sql = "SELECT temps_attente FROM parametre";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 30; // default
    }

    @GetMapping("/reservation/planning")
    public ModelView planning() {
        ModelView mv = new ModelView();
        mv.setView("planning.jsp");
        return mv;
    }

    @PostMapping("/reservation/plan-date")
    public ModelView planDateForm(@Param("date") String dateStr) {
        Map<String, Object> result = planDate(dateStr);
        ModelView mv = new ModelView();
        mv.setView("planningResult.jsp");
        mv.addData("result", result);
        return mv;
    }
}

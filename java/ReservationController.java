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
    @GetMapping("/api/plan-date")
    public Map<String, Object> getPlanDate(@Param("date") String dateStr) {
        // Étape 1.1 : Parser la date
        LocalDate date = LocalDate.parse(dateStr);

        // Étape 1.2 : Récupérer les véhicules
        List<VoitureRow> vehicles = listAllVehicles();

        // Étape 1.3 : Récupérer les réservations DÉJÀ assignées
        List<Map<String, Object>> assigned = getAssignedReservationsForDate(date, vehicles);
        
        // Étape 1.4 : Récupérer les réservations NON assignées (pour affichage)
        List<ReservationRow> unassigned = getUnassignedReservationsForDate(date);

        // Étape 1.5 : Calculer les véhicules non utilisés
        List<VoitureRow> unusedVehicles = new ArrayList<>(vehicles);
        if (!assigned.isEmpty()) {
            for (Map<String, Object> trip : assigned) {
                VoitureRow vDetails = (VoitureRow) trip.get("vehiculeDetails");
                if (vDetails != null) {
                    unusedVehicles.removeIf(v -> v.getId() == vDetails.getId());
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("assigned", assigned);
        result.put("unassigned", unassigned);
        result.put("unusedVehicles", unusedVehicles);
        
        return result;
    }

    @Api
    @PostMapping("/api/plan-date")
    public Map<String, Object> planDate(@Param("date") String dateStr) {
        // Étape 1.1 : Parser la date
        LocalDate date = LocalDate.parse(dateStr);

        // Étape 1.2 : Récupérer TOUTES les réservations non assignées pour cette date (triées par ID)
        List<ReservationRow> pendingReservations = getUnassignedReservationsForDate(date);

        // Étape 1.3 : Récupérer véhicules disponibles
        List<VoitureRow> vehicles = listAllVehicles();
        List<VoitureRow> availableVehicles = new ArrayList<>(vehicles);

        // Init lists
        List<Map<String, Object>> assigned = new ArrayList<>();
        List<ReservationRow> unassigned = new ArrayList<>();

        // -- NOUVELLE LOGIQUE : Récupérer les réservations DÉJÀ assignées pour cette date --
        List<Map<String, Object>> alreadyAssigned = getAssignedReservationsForDate(date, vehicles);
        if (!alreadyAssigned.isEmpty()) {
            assigned.addAll(alreadyAssigned);
            
            // Retirer les véhicules déjà utilisés de la liste des disponibles
            for (Map<String, Object> trip : alreadyAssigned) {
                VoitureRow vDetails = (VoitureRow) trip.get("vehiculeDetails");
                if (vDetails != null) {
                    availableVehicles.removeIf(v -> v.getId() == vDetails.getId());
                }
            }
        }
        // ----------------------------------------------------------------------------------

        try (Connection con = DbUtil.getConnection()) {
            con.setAutoCommit(false);
            try {
                // Étape 2 : Traiter les réservations une par une (ordre ID croissant)
                for (ReservationRow res : pendingReservations) {
                    // Chercher le meilleur véhicule pour CETTE réservation
                    VoitureRow bestVehicle = findBestVehicle(availableVehicles, res.getNombre_passager());

                    if (bestVehicle != null) {
                        // Véhicule trouvé -> Assigner
                        availableVehicles.remove(bestVehicle);

                        Map<String, Object> trip = new HashMap<>();
                        trip.put("vehicule", bestVehicle.getImmatricule());
                        trip.put("vehiculeDetails", bestVehicle); // Ajout détails véhicule
                        trip.put("reservationId", res.getId());
                        trip.put("clientId", res.getId_client()); // Ajout ID Client
                        trip.put("lieu", res.getLieu_nom());
                        trip.put("nbPassagers", res.getNombre_passager()); // Ajout nb passagers
                        trip.put("dateDepart", res.getDate_heure_arrive());
                        String arrivee = calculateArrival(res, bestVehicle, res.getId_lieu());
                        trip.put("dateArrivee", arrivee);
                        assigned.add(trip);

                        // Mettre à jour DB
                        try (PreparedStatement ps = con.prepareStatement(
                                "UPDATE reservation SET id_voiture = ? WHERE id = ?")) {
                            ps.setInt(1, bestVehicle.getId());
                            ps.setInt(2, res.getId());
                            ps.executeUpdate();
                        }
                    } else {
                        // Pas de véhicule disponible -> Non assigné
                        unassigned.add(res);
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
        result.put("unusedVehicles", availableVehicles);
        return result;
    }

    private List<Map<String, Object>> getAssignedReservationsForDate(LocalDate date, List<VoitureRow> allVehicles) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT r.id, r.id_client, r.nombre_passager, r.date_heure_arrive, r.id_lieu, l.libelle AS lieu_nom, r.id_voiture "
                + "FROM reservation r JOIN lieu l ON l.id = r.id_lieu "
                + "WHERE DATE(r.date_heure_arrive) = ? AND r.id_voiture IS NOT NULL "
                + "ORDER BY r.id_voiture, r.id";

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
                    int idVoiture = rs.getInt("id_voiture");

                    // Retrouver le véhicule correspondant
                    VoitureRow vehicle = allVehicles.stream()
                            .filter(v -> v.getId() == idVoiture)
                            .findFirst()
                            .orElse(null);

                    if (vehicle != null) {
                        ReservationRow res = new ReservationRow(id, idClient, nb, dateHeure, idLieu, lieuNom);
                        Map<String, Object> trip = new HashMap<>();
                        trip.put("vehicule", vehicle.getImmatricule());
                        trip.put("vehiculeDetails", vehicle);
                        trip.put("reservationId", res.getId());
                        trip.put("clientId", res.getId_client());
                        trip.put("lieu", res.getLieu_nom());
                        trip.put("nbPassagers", res.getNombre_passager());
                        trip.put("dateDepart", res.getDate_heure_arrive());
                        String arrivee = calculateArrival(res, vehicle, idLieu);
                        trip.put("dateArrivee", arrivee);
                        result.add(trip);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private List<ReservationRow> getUnassignedReservationsForDate(LocalDate date) {
        List<ReservationRow> list = new ArrayList<>();
        String sql = "SELECT r.id, r.id_client, r.nombre_passager, r.date_heure_arrive, r.id_lieu, l.libelle AS lieu_nom "
                + "FROM reservation r JOIN lieu l ON l.id = r.id_lieu "
                + "WHERE DATE(r.date_heure_arrive) = ? AND r.id_voiture IS NULL "
                + "ORDER BY r.id ASC"; // Ordre de création (FIFO)

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

                    list.add(new ReservationRow(id, idClient, nb, dateHeure, idLieu, lieuNom));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
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
                            return "D".equalsIgnoreCase(carb) ? 0 : 1; // 0 (Diesel) vient avant 1 (Autre)
                        })
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

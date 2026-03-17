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
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
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

    private static LocalDateTime computeEffectiveDepart(LocalDateTime windowStart, List<ReservationRow> reservationsInTrip) {
        if (reservationsInTrip == null || reservationsInTrip.isEmpty()) return windowStart;

        boolean hasLaterReservation = false;
        for (ReservationRow r : reservationsInTrip) {
            if (r == null || r.getDate_heure_arrive() == null) continue;
            LocalDateTime t = LocalDateTime.parse(r.getDate_heure_arrive());
            if (t.isAfter(windowStart)) {
                hasLaterReservation = true;
                break;
            }
        }

        if (hasLaterReservation) {
            return windowStart.plusMinutes(getAttente());
        }
        return windowStart;
    }

    private static class ReservationWindow {
        final LocalDateTime windowStart;
        final List<ReservationRow> reservations;

        ReservationWindow(LocalDateTime windowStart, List<ReservationRow> reservations) {
            this.windowStart = windowStart;
            this.reservations = reservations;
        }
    }

    private static List<ReservationWindow> buildPendingWindows(List<ReservationRow> pending) {
        if (pending == null || pending.isEmpty()) return Collections.emptyList();

        int attente = getAttente();
        List<ReservationRow> sorted = new ArrayList<>(pending);
        sorted.sort(Comparator
                .comparing((ReservationRow r) -> LocalDateTime.parse(r.getDate_heure_arrive()))
                .thenComparingInt(ReservationRow::getId));

        List<ReservationWindow> windows = new ArrayList<>();
        int i = 0;
        while (i < sorted.size()) {
            ReservationRow first = sorted.get(i);
            LocalDateTime start = LocalDateTime.parse(first.getDate_heure_arrive());
            LocalDateTime endInclusive = start.plusMinutes(attente);

            List<ReservationRow> group = new ArrayList<>();
            group.add(first);
            i++;
            while (i < sorted.size()) {
                ReservationRow r = sorted.get(i);
                LocalDateTime t = LocalDateTime.parse(r.getDate_heure_arrive());
                if (!t.isAfter(endInclusive)) {
                    group.add(r);
                    i++;
                } else {
                    break;
                }
            }

            windows.add(new ReservationWindow(start, group));
        }
        return windows;
    }

    private static class VehicleTrip {
        final LocalDateTime windowStart;
        final List<ReservationRow> reservations;

        VehicleTrip(LocalDateTime windowStart, List<ReservationRow> reservations) {
            this.windowStart = windowStart;
            this.reservations = reservations;
        }
    }

    private static List<VehicleTrip> buildVehicleTripsFromAssignedRows(List<ReservationRow> assignedForVehicle) {
        if (assignedForVehicle == null || assignedForVehicle.isEmpty()) return Collections.emptyList();

        int attente = getAttente();
        List<ReservationRow> sorted = new ArrayList<>(assignedForVehicle);
        sorted.sort(Comparator
                .comparing((ReservationRow r) -> LocalDateTime.parse(r.getDate_heure_arrive()))
                .thenComparingInt(ReservationRow::getId));

        List<VehicleTrip> trips = new ArrayList<>();
        int i = 0;
        while (i < sorted.size()) {
            ReservationRow first = sorted.get(i);
            LocalDateTime start = LocalDateTime.parse(first.getDate_heure_arrive());
            LocalDateTime endInclusive = start.plusMinutes(attente);

            List<ReservationRow> group = new ArrayList<>();
            group.add(first);
            i++;
            while (i < sorted.size()) {
                ReservationRow r = sorted.get(i);
                LocalDateTime t = LocalDateTime.parse(r.getDate_heure_arrive());
                if (!t.isAfter(endInclusive)) {
                    group.add(r);
                    i++;
                } else {
                    break;
                }
            }
            trips.add(new VehicleTrip(start, group));
        }
        return trips;
    }

    private static class AssignedWindow {
        final LocalDateTime windowStart;
        final List<AssignedReservation> rows;

        AssignedWindow(LocalDateTime windowStart, List<AssignedReservation> rows) {
            this.windowStart = windowStart;
            this.rows = rows;
        }
    }

    private static List<AssignedWindow> buildAssignedWindows(List<AssignedReservation> assignedRows) {
        if (assignedRows == null || assignedRows.isEmpty()) return Collections.emptyList();

        int attente = getAttente();
        List<AssignedReservation> sorted = new ArrayList<>(assignedRows);
        sorted.sort(Comparator
                .comparing((AssignedReservation r) -> r.dateTime)
                .thenComparingInt(r -> r.id));

        List<AssignedWindow> windows = new ArrayList<>();
        int i = 0;
        while (i < sorted.size()) {
            AssignedReservation first = sorted.get(i);
            LocalDateTime start = first.dateTime;
            LocalDateTime endInclusive = start.plusMinutes(attente);

            List<AssignedReservation> group = new ArrayList<>();
            group.add(first);
            i++;
            while (i < sorted.size()) {
                AssignedReservation r = sorted.get(i);
                if (!r.dateTime.isAfter(endInclusive)) {
                    group.add(r);
                    i++;
                } else {
                    break;
                }
            }

            windows.add(new AssignedWindow(start, group));
        }
        return windows;
    }

    private static LocalDateTime computeEffectiveDepartFromAssignedRows(LocalDateTime windowStart, List<AssignedReservation> assignedRowsInWindow) {
        if (assignedRowsInWindow == null || assignedRowsInWindow.isEmpty()) return windowStart;

        boolean hasLaterReservation = false;
        for (AssignedReservation r : assignedRowsInWindow) {
            if (r == null || r.dateTime == null) continue;
            if (r.dateTime.isAfter(windowStart)) {
                hasLaterReservation = true;
                break;
            }
        }
        if (hasLaterReservation) return windowStart.plusMinutes(getAttente());
        return windowStart;
    }

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

        // Étape 1.2 : Récupérer toutes les réservations non assignées pour cette date
        List<ReservationRow> pendingReservations = getUnassignedReservationsForDate(date);

        // Étape 1.3 : Récupérer véhicules disponibles (réutilisables selon créneau)
        List<VoitureRow> vehicles = listAllVehicles();

        // Init lists
        List<Map<String, Object>> assigned = new ArrayList<>();
        List<ReservationRow> unassigned = new ArrayList<>();

        // 1.4 : Récupérer les réservations déjà assignées pour cette date (elles restent en base)
        List<AssignedReservation> alreadyAssigned = getAssignedReservationRowsForDate(date);

        // 1.5 : Indexer les véhicules par id
        Map<Integer, VoitureRow> vehicleById = vehicles.stream()
                .collect(Collectors.toMap(VoitureRow::getId, v -> v));

        // 1.6 : Construire l'état des "bins" déjà existants par créneau (date+heure) et véhicule
        // Note: on conserve l'ancien stockage "par heure" pour ne pas casser la persistance.
        Map<LocalDateTime, Map<Integer, VehicleBin>> binsBySlot = new LinkedHashMap<>();
        for (AssignedReservation ar : alreadyAssigned) {
            VoitureRow v = vehicleById.get(ar.idVoiture);
            if (v == null) continue;
            VehicleBin bin = binsBySlot
                    .computeIfAbsent(ar.dateTime, k -> new LinkedHashMap<>())
                    .computeIfAbsent(ar.idVoiture, k -> new VehicleBin(v));
            bin.add(ar.toReservationRow());
        }

        // Disponibilité par véhicule : heure à laquelle il est de retour à l'aéroport (donc réutilisable)
        Map<Integer, LocalDateTime> vehicleAvailableAt = new HashMap<>();
        for (VoitureRow v : vehicles) {
            vehicleAvailableAt.put(v.getId(), LocalDateTime.MIN);
        }

        // Initialiser la disponibilité à partir des réservations déjà assignées en base
        // (on prend le retour à l'aéroport comme fin de mission)
        // IMPORTANT: toutes les tournées de la même fenêtre partent au même departEffectif.
        if (!alreadyAssigned.isEmpty()) {
            List<AssignedWindow> windows = buildAssignedWindows(alreadyAssigned);
            for (AssignedWindow w : windows) {
                LocalDateTime departEffectif = computeEffectiveDepartFromAssignedRows(w.windowStart, w.rows);

                Map<Integer, List<ReservationRow>> perVehicle = new HashMap<>();
                for (AssignedReservation ar : w.rows) {
                    if (!vehicleById.containsKey(ar.idVoiture)) continue;
                    perVehicle.computeIfAbsent(ar.idVoiture, k -> new ArrayList<>()).add(ar.toReservationRow());
                }

                List<Integer> vehIds = new ArrayList<>(perVehicle.keySet());
                vehIds.sort(Comparator.naturalOrder());
                for (Integer vehId : vehIds) {
                    List<ReservationRow> ordered = new ArrayList<>(perVehicle.getOrDefault(vehId, Collections.emptyList()));
                    ordered.sort(Comparator
                            .comparingInt((ReservationRow r) -> distanceFromAirportKm(r.getId_lieu()))
                            .thenComparing(ReservationRow::getLieu_nom, Comparator.nullsLast(String::compareToIgnoreCase))
                            .thenComparingInt(ReservationRow::getId));

                    LocalDateTime returnAt = computeReturnToAirportDateTime(departEffectif, ordered);
                    LocalDateTime current = vehicleAvailableAt.getOrDefault(vehId, LocalDateTime.MIN);
                    if (returnAt.isAfter(current)) vehicleAvailableAt.put(vehId, returnAt);
                }
            }
        }

        // 1.7 : Construire des fenêtres d'attente [t0, t0+attente] pour les réservations non assignées
        List<ReservationWindow> pendingWindows = buildPendingWindows(pendingReservations);

        // 2 : Pour chaque créneau, appliquer la planification
        try (Connection con = DbUtil.getConnection()) {
            con.setAutoCommit(false);
            try {
                for (ReservationWindow w : pendingWindows) {
                    LocalDateTime slot = w.windowStart;
                    List<ReservationRow> slotReservations = new ArrayList<>(w.reservations);

                    // Départ effectif commun à tous les véhicules de cette fenêtre
                    LocalDateTime departEffectifFenetre = computeEffectiveDepart(slot, w.reservations);

                    // Priorité : ordre décroissant du nombre de personnes
                    slotReservations.sort(Comparator.comparingInt(ReservationRow::getNombre_passager).reversed()
                            .thenComparingInt(ReservationRow::getId));

                    Map<Integer, VehicleBin> slotBins = binsBySlot.computeIfAbsent(slot, k -> new LinkedHashMap<>());
                    Set<Integer> usedVehicleIdsInSlot = new HashSet<>(slotBins.keySet());

                    // Interdire l'utilisation des véhicules dont la disponibilité est après ce créneau
                    for (Map.Entry<Integer, LocalDateTime> av : vehicleAvailableAt.entrySet()) {
                        if (av.getValue() != null && av.getValue().isAfter(slot)) {
                            usedVehicleIdsInSlot.add(av.getKey());
                        }
                    }

                    // NB : un même véhicule peut être réutilisé sur un autre créneau => pas de liste globale "available"
                    for (int i = 0; i < slotReservations.size(); i++) {
                        ReservationRow res = slotReservations.get(i);
                        VehicleBin bestFitExisting = findBestFitExistingBin(slotBins.values(), res.getNombre_passager());
                        if (bestFitExisting != null) {
                            bestFitExisting.add(res);
                            continue;
                        }

                        List<Integer> remainingPassengers = new ArrayList<>();
                        for (int j = i + 1; j < slotReservations.size(); j++) {
                            remainingPassengers.add(slotReservations.get(j).getNombre_passager());
                        }

                        VoitureRow newVehicle = findVehicleForNewBin(vehicles, usedVehicleIdsInSlot, res.getNombre_passager(), remainingPassengers);
                        if (newVehicle == null) {
                            unassigned.add(res);
                            continue;
                        }

                        usedVehicleIdsInSlot.add(newVehicle.getId());
                        VehicleBin bin = new VehicleBin(newVehicle);
                        bin.add(res);
                        slotBins.put(newVehicle.getId(), bin);

                        // Remplissage immédiat : on parcourt les réservations restantes du créneau
                        // et on ajoute celles qui rentrent dans ce véhicule, avant d'ouvrir un autre véhicule.
                        for (int j = i + 1; j < slotReservations.size(); ) {
                            ReservationRow candidate = slotReservations.get(j);
                            if (bin.remainingCapacity() >= candidate.getNombre_passager()) {
                                bin.add(candidate);
                                slotReservations.remove(j);
                            } else {
                                j++;
                            }
                        }
                    }

                    // Mettre à jour la disponibilité des véhicules utilisés dans ce créneau
                    for (VehicleBin b : slotBins.values()) {
                        List<ReservationRow> ordered = new ArrayList<>(b.reservations);
                        ordered.sort(Comparator
                                .comparingInt((ReservationRow r) -> distanceFromAirportKm(r.getId_lieu()))
                                .thenComparing(ReservationRow::getLieu_nom, Comparator.nullsLast(String::compareToIgnoreCase))
                                .thenComparingInt(ReservationRow::getId));
                        LocalDateTime returnAt = computeReturnToAirportDateTime(departEffectifFenetre, ordered);
                        LocalDateTime current = vehicleAvailableAt.getOrDefault(b.vehicle.getId(), LocalDateTime.MIN);
                        if (returnAt.isAfter(current)) vehicleAvailableAt.put(b.vehicle.getId(), returnAt);
                    }

                    // Persist pour ce créneau : mise à jour id_voiture
                    for (VehicleBin bin : slotBins.values()) {
                        for (ReservationRow r : bin.reservations) {
                            if (r.getId() <= 0) continue;
                            try (PreparedStatement ps = con.prepareStatement(
                                    "UPDATE reservation SET id_voiture = ? WHERE id = ?")) {
                                ps.setInt(1, bin.vehicle.getId());
                                ps.setInt(2, r.getId());
                                ps.executeUpdate();
                            }
                        }
                    }
                }

                con.commit();
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // 3 : Construire la réponse (assigned/unassigned/unusedVehicles)
        // On relit les réservations assignées depuis la base, et on les ordonne par règles de desserte
        assigned.addAll(getAssignedReservationsForDate(date, vehicles));

        // "unusedVehicles" = véhicules jamais utilisés sur la date (tous créneaux confondus)
        Set<Integer> usedVehicleIdsForDay = new HashSet<>();
        for (Map<String, Object> trip : assigned) {
            VoitureRow v = (VoitureRow) trip.get("vehiculeDetails");
            if (v != null) usedVehicleIdsForDay.add(v.getId());
        }
        List<VoitureRow> unusedVehicles = vehicles.stream()
                .filter(v -> !usedVehicleIdsForDay.contains(v.getId()))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("assigned", assigned);
        result.put("unassigned", unassigned);
        result.put("unusedVehicles", unusedVehicles);
        return result;
    }

    private List<Map<String, Object>> getAssignedReservationsForDate(LocalDate date, List<VoitureRow> allVehicles) {
        Map<Integer, VoitureRow> vehicleById = allVehicles.stream()
                .collect(Collectors.toMap(VoitureRow::getId, v -> v));

        List<AssignedReservation> assignedRows = getAssignedReservationRowsForDate(date);

        // Reconstruire les fenêtres globalement (tous véhicules), puis afficher un dateDepart commun
        // à tous les bins de la fenêtre.
        List<Map<String, Object>> result = new ArrayList<>();

        List<AssignedWindow> windows = buildAssignedWindows(assignedRows);
        for (AssignedWindow w : windows) {
            LocalDateTime departEffectifFenetre = computeEffectiveDepartFromAssignedRows(w.windowStart, w.rows);

            Map<Integer, List<ReservationRow>> perVehicle = new HashMap<>();
            for (AssignedReservation ar : w.rows) {
                if (!vehicleById.containsKey(ar.idVoiture)) continue;
                perVehicle.computeIfAbsent(ar.idVoiture, k -> new ArrayList<>()).add(ar.toReservationRow());
            }

            List<Integer> vehicleIds = new ArrayList<>(perVehicle.keySet());
            vehicleIds.sort(Comparator.naturalOrder());
            for (Integer vehicleId : vehicleIds) {
                VoitureRow vehicle = vehicleById.get(vehicleId);
                if (vehicle == null) continue;

                List<ReservationRow> reservations = new ArrayList<>(perVehicle.getOrDefault(vehicleId, Collections.emptyList()));
                reservations.sort(Comparator
                        .comparingInt((ReservationRow r) -> distanceFromAirportKm(r.getId_lieu()))
                        .thenComparing(ReservationRow::getLieu_nom, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparingInt(ReservationRow::getId));

                Map<Integer, String> arrivalByReservationId = computeSequentialArrivals(departEffectifFenetre, reservations);
                String retourAeroport = computeReturnToAirport(departEffectifFenetre, reservations);

                int ordre = 1;
                for (ReservationRow res : reservations) {
                    Map<String, Object> trip = new HashMap<>();
                    trip.put("vehicule", vehicle.getImmatricule());
                    trip.put("vehiculeDetails", vehicle);
                    trip.put("reservationId", res.getId());
                    trip.put("clientId", res.getId_client());
                    trip.put("lieu", res.getLieu_nom());
                    trip.put("nbPassagers", res.getNombre_passager());
                    trip.put("dateDepart", departEffectifFenetre.toString().replace("T", " "));
                    trip.put("dateArrivee", arrivalByReservationId.get(res.getId()));
                    trip.put("dateRetourAeroport", retourAeroport);
                    trip.put("ordreDesserte", ordre);
                    result.add(trip);
                    ordre++;
                }
            }
        }

        // ordre global stable: départ effectif asc, puis véhicule id, puis ordre desserte
        result.sort(Comparator
                .comparing((Map<String, Object> m) -> (String) m.get("dateDepart"))
                .thenComparing(m -> ((VoitureRow) m.get("vehiculeDetails")).getId())
                .thenComparing(m -> (Integer) m.get("ordreDesserte"))
                .thenComparing(m -> (Integer) m.get("reservationId")));

        return result;
    }

    private List<AssignedReservation> getAssignedReservationRowsForDate(LocalDate date) {
        List<AssignedReservation> list = new ArrayList<>();
        String sql = "SELECT r.id, r.id_client, r.nombre_passager, r.date_heure_arrive, r.id_lieu, l.libelle AS lieu_nom, r.id_voiture "
                + "FROM reservation r JOIN lieu l ON l.id = r.id_lieu "
                + "WHERE DATE(r.date_heure_arrive) = ? AND r.id_voiture IS NOT NULL";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String idClient = rs.getString("id_client");
                    int nb = rs.getInt("nombre_passager");
                    LocalDateTime dt = rs.getTimestamp("date_heure_arrive").toLocalDateTime();
                    int idLieu = rs.getInt("id_lieu");
                    String lieuNom = rs.getString("lieu_nom");
                    int idVoiture = rs.getInt("id_voiture");
                    list.add(new AssignedReservation(id, idClient, nb, dt, idLieu, lieuNom, idVoiture));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
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

    private static VehicleBin findBestFitExistingBin(java.util.Collection<VehicleBin> bins, int passengers) {
        VehicleBin best = null;
        int bestRemainingAfter = Integer.MAX_VALUE;
        for (VehicleBin bin : bins) {
            int remaining = bin.remainingCapacity();
            if (remaining >= passengers) {
                int after = remaining - passengers;
                if (after < bestRemainingAfter) {
                    bestRemainingAfter = after;
                    best = bin;
                }
            }
        }
        return best;
    }

    private static VoitureRow findVehicleForNewBin(List<VoitureRow> allVehicles, Set<Integer> usedVehicleIdsInSlot, int passengers, List<Integer> remainingPassengers) {
        List<VoitureRow> candidates = allVehicles.stream()
                .filter(v -> !usedVehicleIdsInSlot.contains(v.getId()))
                .filter(v -> v.getNb_place() >= passengers)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) return null;

        System.out.println("[Planning] Candidats (passagers=" + passengers + "):");
        for (VoitureRow v : candidates) {
            System.out.println("  - id=" + v.getId() + " immat=" + v.getImmatricule() + " places=" + v.getNb_place() + " carb=" + v.getType_carburant());
        }

        // Heuristique pour réduire le nombre de véhicules:
        // si possible, choisir un véhicule qui laisse assez de place pour au moins une réservation restante du même créneau.
        List<VoitureRow> mergeFriendly = candidates.stream()
                .filter(v -> {
                    int remaining = v.getNb_place() - passengers;
                    for (Integer p : remainingPassengers) {
                        if (p != null && p > 0 && remaining >= p) return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        List<VoitureRow> pool = mergeFriendly.isEmpty() ? candidates : mergeFriendly;

        VoitureRow chosen = pool.stream()
                .min(Comparator.comparingInt(VoitureRow::getNb_place)
                        .thenComparing((VoitureRow v) -> {
                            String carb = v.getType_carburant() != null ? v.getType_carburant().trim() : "";
                            return "D".equalsIgnoreCase(carb) ? 0 : 1;
                        })
                        .thenComparingInt(VoitureRow::getId))
                .orElse(null);

        if (chosen != null) {
            System.out.println("[Planning] Choisi: id=" + chosen.getId() + " immat=" + chosen.getImmatricule() + " places=" + chosen.getNb_place() + " carb=" + chosen.getType_carburant());
        }
        return chosen;
    }

    private static Map<Integer, String> computeSequentialArrivals(LocalDateTime depart, List<ReservationRow> orderedStops) {
        Map<Integer, String> result = new HashMap<>();
        LocalDateTime t = depart;
        double vitesse = getVitesse();

        int prevLieu = 1; // Aéroport
        for (ReservationRow stop : orderedStops) {
            int dist = getDistanceSmart(prevLieu, stop.getId_lieu());
            long minutes = Math.round(((double) dist / vitesse) * 60);
            t = t.plusMinutes(minutes);
            result.put(stop.getId(), t.toString().replace("T", " "));
            prevLieu = stop.getId_lieu();
        }
        return result;
    }

    private static String computeReturnToAirport(LocalDateTime depart, List<ReservationRow> orderedStops) {
        return computeReturnToAirportDateTime(depart, orderedStops).toString().replace("T", " ");
    }

    private static LocalDateTime computeReturnToAirportDateTime(LocalDateTime depart, List<ReservationRow> orderedStops) {
        LocalDateTime t = depart;
        double vitesse = getVitesse();

        int prevLieu = 1;
        for (ReservationRow stop : orderedStops) {
            int dist = getDistanceSmart(prevLieu, stop.getId_lieu());
            long minutes = Math.round(((double) dist / vitesse) * 60);
            t = t.plusMinutes(minutes);
            prevLieu = stop.getId_lieu();
        }

        int distBack = getDistanceSmart(prevLieu, 1);
        long minutesBack = Math.round(((double) distBack / vitesse) * 60);
        t = t.plusMinutes(minutesBack);
        return t;
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

    private static int getDistanceSmart(int from, int to) {
        int d = getDistance(from, to);
        if (d > 0) return d;
        return getDistance(to, from);
    }

    private static int distanceFromAirportKm(int lieuId) {
        return getDistanceSmart(1, lieuId);
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

    private static class AssignedReservation {
        final int id;
        final String idClient;
        final int nb;
        final LocalDateTime dateTime;
        final int idLieu;
        final String lieuNom;
        final int idVoiture;

        AssignedReservation(int id, String idClient, int nb, LocalDateTime dateTime, int idLieu, String lieuNom, int idVoiture) {
            this.id = id;
            this.idClient = idClient;
            this.nb = nb;
            this.dateTime = dateTime;
            this.idLieu = idLieu;
            this.lieuNom = lieuNom;
            this.idVoiture = idVoiture;
        }

        ReservationRow toReservationRow() {
            return new ReservationRow(id, idClient, nb, dateTime.toString(), idLieu, lieuNom);
        }
    }

    private static class VehicleBin {
        final VoitureRow vehicle;
        final List<ReservationRow> reservations = new ArrayList<>();

        VehicleBin(VoitureRow vehicle) {
            this.vehicle = vehicle;
        }

        void add(ReservationRow r) {
            reservations.add(r);
        }

        int remainingCapacity() {
            int used = 0;
            for (ReservationRow r : reservations) used += r.getNombre_passager();
            return vehicle.getNb_place() - used;
        }
    }
}

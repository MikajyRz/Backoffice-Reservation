package test.java;

import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.annotations.Api;
import com.annotations.ControllerAnnotation;
import com.annotations.GetMapping;
import com.annotations.Param;
import com.annotations.PostMapping;
import com.classes.ModelView;
import com.utils.DbUtil;

@ControllerAnnotation("/voiture")
public class VoitureController {

    @GetMapping("/voiture")
    public ModelView listPage() {
        ModelView mv = new ModelView();
        mv.setView("voitureList.jsp");
        mv.addData("voitures", listAll());
        return mv;
    }

    @GetMapping("/voiture/disponibilite")
    public ModelView disponibilitePage(@Param("jour") String jour) {
        ModelView mv = new ModelView();
        mv.setView("voitureDisponibilite.jsp");
        mv.addData("voitures", listAll());
        mv.addData("disponibilites", listDisponibilites(jour));
        mv.addData("jour", jour);
        return mv;
    }

    @PostMapping("/voiture/disponibilite/save")
    public ModelView saveDisponibilite(
            @Param("id_voiture") Integer idVoiture,
            @Param("jour") String jour,
            @Param("heure_dispo") String heureDispo) {

        if (idVoiture == null || jour == null || jour.trim().isEmpty() || heureDispo == null || heureDispo.trim().isEmpty()) {
            ModelView mv = new ModelView();
            mv.setView("voitureDisponibilite.jsp");
            mv.addData("error", "Veuillez sélectionner une voiture, un jour et une heure.");
            mv.addData("voitures", listAll());
            mv.addData("disponibilites", listDisponibilites(jour));
            mv.addData("jour", jour);
            return mv;
        }

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO voiture_disponibilite(id_voiture, jour, heure_dispo) VALUES (?, ?, ?) " +
                             "ON CONFLICT (id_voiture, jour) DO UPDATE SET heure_dispo = EXCLUDED.heure_dispo")) {

            LocalDate d = LocalDate.parse(jour.trim());
            LocalTime t = LocalTime.parse(heureDispo.trim());

            ps.setInt(1, idVoiture);
            ps.setDate(2, Date.valueOf(d));
            ps.setTime(3, Time.valueOf(t));
            ps.executeUpdate();
        } catch (Exception e) {
            ModelView mv = new ModelView();
            mv.setView("voitureDisponibilite.jsp");
            mv.addData("error", "Erreur lors de l'enregistrement : " + e.getMessage());
            mv.addData("voitures", listAll());
            mv.addData("disponibilites", listDisponibilites(jour));
            mv.addData("jour", jour);
            return mv;
        }

        ModelView mv = new ModelView();
        mv.setView("voitureDisponibilite.jsp");
        mv.addData("success", "Disponibilité enregistrée.");
        mv.addData("voitures", listAll());
        mv.addData("disponibilites", listDisponibilites(jour));
        mv.addData("jour", jour);
        return mv;
    }

    @GetMapping("/voiture/form")
    public ModelView form(@Param("id") Integer id) {
        ModelView mv = new ModelView();
        mv.setView("voitureForm.jsp");

        if (id != null) {
            mv.addData("voiture", findById(id));
        }
        return mv;
    }

    @PostMapping("/voiture/create")
    public ModelView create(
            @Param("immatricule") String immatricule,
            @Param("type_carburant") String typeCarburant,
            @Param("nb_place") int nbPlace) {

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO voiture(immatricule, type_carburant, nb_place) VALUES (?, ?, ?)")) {

            ps.setString(1, immatricule);
            ps.setString(2, typeCarburant);
            ps.setInt(3, nbPlace);
            ps.executeUpdate();
        } catch (Exception e) {
            // En cas d'erreur, on retourne au formulaire avec le message
            ModelView mv = new ModelView();
            mv.setView("voitureForm.jsp");
            mv.addData("error", "Erreur lors de la création : " + e.getMessage());
            // On remet les valeurs saisies
            mv.addData("voiture", new VoitureRow(0, immatricule, typeCarburant, nbPlace));
            return mv;
        }

        ModelView mv = new ModelView();
        mv.setView("voitureList.jsp");
        mv.addData("voitures", listAll());
        return mv;
    }

    @PostMapping("/voiture/update")
    public ModelView update(
            @Param("id") int id,
            @Param("immatricule") String immatricule,
            @Param("type_carburant") String typeCarburant,
            @Param("nb_place") int nbPlace) {

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE voiture SET immatricule = ?, type_carburant = ?, nb_place = ? WHERE id = ?")) {

            ps.setString(1, immatricule);
            ps.setString(2, typeCarburant);
            ps.setInt(3, nbPlace);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ModelView mv = new ModelView();
        mv.setView("voitureList.jsp");
        mv.addData("voitures", listAll());
        return mv;
    }

    @PostMapping("/voiture/delete")
    public ModelView delete(@Param("id") int id) {
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM voiture WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ModelView mv = new ModelView();
        mv.setView("voitureList.jsp");
        mv.addData("voitures", listAll());
        return mv;
    }

    @Api
    @GetMapping("/api/voitures")
    public List<VoitureRow> apiList() {
        return listAll();
    }

    @Api
    @PostMapping("/api/voitures")
    public String apiCreate(
            @Param("immatricule") String immatricule,
            @Param("type_carburant") String typeCarburant,
            @Param("nb_place") int nbPlace) {

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO voiture(immatricule, type_carburant, nb_place) VALUES (?, ?, ?)")) {

            ps.setString(1, immatricule);
            ps.setString(2, typeCarburant);
            ps.setInt(3, nbPlace);
            ps.executeUpdate();
            return "OK";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Api
    @PostMapping("/api/voitures/update")
    public String apiUpdate(
            @Param("id") int id,
            @Param("immatricule") String immatricule,
            @Param("type_carburant") String typeCarburant,
            @Param("nb_place") int nbPlace) {

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE voiture SET immatricule = ?, type_carburant = ?, nb_place = ? WHERE id = ?")) {

            ps.setString(1, immatricule);
            ps.setString(2, typeCarburant);
            ps.setInt(3, nbPlace);
            ps.setInt(4, id);
            ps.executeUpdate();
            return "OK";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Api
    @PostMapping("/api/voitures/delete")
    public String apiDelete(@Param("id") int id) {
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM voiture WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return "OK";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<VoitureRow> listAll() {
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

    private static VoitureRow findById(int id) {
        String sql = "SELECT id, immatricule, type_carburant, nb_place FROM voiture WHERE id = ?";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new VoitureRow(
                            rs.getInt("id"),
                            rs.getString("immatricule"),
                            rs.getString("type_carburant"),
                            rs.getInt("nb_place")
                    );
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class VoitureDisponibiliteRow {
        private int id;
        private int idVoiture;
        private String immatricule;
        private String jour;
        private String heureDispo;

        public VoitureDisponibiliteRow(int id, int idVoiture, String immatricule, String jour, String heureDispo) {
            this.id = id;
            this.idVoiture = idVoiture;
            this.immatricule = immatricule;
            this.jour = jour;
            this.heureDispo = heureDispo;
        }

        public int getId() {
            return id;
        }

        public int getIdVoiture() {
            return idVoiture;
        }

        public String getImmatricule() {
            return immatricule;
        }

        public String getJour() {
            return jour;
        }

        public String getHeureDispo() {
            return heureDispo;
        }
    }

    private static List<VoitureDisponibiliteRow> listDisponibilites(String jourFilter) {
        List<VoitureDisponibiliteRow> rows = new ArrayList<>();
        boolean hasFilter = jourFilter != null && !jourFilter.trim().isEmpty();
        String sql =
                "SELECT vd.id, v.id AS id_voiture, v.immatricule, vd.jour, vd.heure_dispo " +
                        "FROM voiture_disponibilite vd " +
                        "JOIN voiture v ON v.id = vd.id_voiture " +
                        (hasFilter ? "WHERE vd.jour = ? " : "") +
                        "ORDER BY vd.jour DESC, v.immatricule ASC";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (hasFilter) {
                ps.setDate(1, Date.valueOf(LocalDate.parse(jourFilter.trim())));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date d = rs.getDate("jour");
                    Time t = rs.getTime("heure_dispo");
                    rows.add(new VoitureDisponibiliteRow(
                            rs.getInt("id"),
                            rs.getInt("id_voiture"),
                            rs.getString("immatricule"),
                            d != null ? d.toString() : null,
                            t != null ? t.toLocalTime().toString() : null
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return rows;
    }
}

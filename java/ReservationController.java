package test.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}

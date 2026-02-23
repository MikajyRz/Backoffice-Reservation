package test.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            throw new RuntimeException(e);
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
}

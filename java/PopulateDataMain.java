package test.java;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.utils.DbUtil;

public class PopulateDataMain {

    public static void main(String[] args) {
        try (Connection con = DbUtil.getConnection()) {

            // Insert parametre
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO parametre(vitesse_moyenne, temps_attente) VALUES (?, ?)")) {
                ps.setInt(1, 60); // 60 km/h
                ps.setInt(2, 30); // 30 minutes
                ps.executeUpdate();
            }

            // Insert lieux
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO lieu(id, code, libelle) VALUES (?, ?, ?)")) {
                // Aeroport
                ps.setInt(1, 1);
                ps.setString(2, "TNR");
                ps.setString(3, "Ivato Aeroport");
                ps.executeUpdate();

                // Hotels
                ps.setInt(1, 2);
                ps.setString(2, "HOT1");
                ps.setString(3, "Hotel Carlton");
                ps.executeUpdate();

                ps.setInt(1, 3);
                ps.setString(2, "HOT2");
                ps.setString(3, "Hotel Lokanga");
                ps.executeUpdate();

                ps.setInt(1, 4);
                ps.setString(2, "HOT3");
                ps.setString(3, "Hotel Ibis");
                ps.executeUpdate();

                ps.setInt(1, 5);
                ps.setString(2, "HOT4");
                ps.setString(3, "Hotel Lokanga");
                ps.executeUpdate();
            }

            // Insert distances
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES (?, ?, ?)")) {
                ps.setInt(1, 2); // HOT1 to TNR
                ps.setInt(2, 1);
                ps.setInt(3, 13);
                ps.executeUpdate();

                ps.setInt(1, 3); // HOT2 to TNR
                ps.setInt(2, 1);
                ps.setInt(3, 15);
                ps.executeUpdate();

                ps.setInt(1, 4); // HOT3 to TNR
                ps.setInt(2, 1);
                ps.setInt(3, 10);
                ps.executeUpdate();

                ps.setInt(1, 5); // HOT4 to TNR
                ps.setInt(2, 1);
                ps.setInt(3, 12);
                ps.executeUpdate();
            }

            System.out.println("Données insérées avec succès dans parametre, lieu, et distance.");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

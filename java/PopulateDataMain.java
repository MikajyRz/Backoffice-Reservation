package test.java;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.utils.DbUtil;

public class PopulateDataMain {

    public static void main(String[] args) {
        try (Connection con = DbUtil.getConnection()) {

            System.out.println("Début du nettoyage complet de la base de données...");

            // Nettoyage complet (DELETE uniquement)
            try (java.sql.Statement stmt = con.createStatement()) {
                // Ordre inverse des dépendances pour éviter les erreurs de clés étrangères
                stmt.executeUpdate("DELETE FROM distance");
                stmt.executeUpdate("DELETE FROM reservation");
                stmt.executeUpdate("DELETE FROM api_token");
                stmt.executeUpdate("DELETE FROM voiture");
                stmt.executeUpdate("DELETE FROM lieu");
                stmt.executeUpdate("DELETE FROM parametre");
                System.out.println("Toutes les tables ont été vidées avec succès.");
            }

            System.out.println("La base de données est maintenant vide et prête pour une nouvelle insertion.");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

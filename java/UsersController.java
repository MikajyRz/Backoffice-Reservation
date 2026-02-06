package test.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import com.annotations.ControllerAnnotation;
import com.annotations.GetMapping;
import com.annotations.HandleUrl;
import com.annotations.Param;
import com.annotations.PostMapping;
import com.classes.ModelView;
import com.utils.DbUtil;

// Contrôleur de test pour les URLs dynamiques avec paramètre de chemin
@ControllerAnnotation("/user")
public class UsersController {

    // Affiche un message simple pour tester le retour de String
    @HandleUrl("/home")
    public String home() {
        return "Bienvenue sur la page utilisateur !";
    }

    // Affiche le formulaire utilisateur 
    @GetMapping("/users/formulaire")
    public ModelView userForm() {
        ModelView mv = new ModelView();
        mv.setView("formulaire.jsp");
        return mv;
    }

    // Traite le résultat du formulaire
    @PostMapping("/users/result")
    public ModelView userFormResult(@Param("nom") String nom, String prenom, int age) {

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO users(nom, prenom, age) VALUES (?, ?, ?)")) {
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setInt(3, age);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ModelView mv = new ModelView();
        mv.setView("formulaireResult.jsp");
        mv.addData("nom", nom);
        mv.addData("prenom", prenom);
        mv.addData("age", age);
        return mv;
    }

    @PostMapping("/users/resultObject")
    public ModelView userFormResultObject(User user) {
        ModelView mv = new ModelView();
        mv.setView("formulaireObjectResult.jsp");
        mv.addData("user", user);
        return mv;
    }

    // Gère les URLs de type /user/{id} et affiche les détails d'un utilisateur
    @HandleUrl("/user/{id}")
    public ModelView showUser(int id) {

        ModelView mv = new ModelView();
        mv.setView("userDetails.jsp");
        mv.addData("value", id);
        return mv;
    }

    // Gère les URLs de type /user/{id}/{nom} avec deux paramètres de chemin
    @HandleUrl("/user/{id}/{nom}")
    public ModelView showUserWithName(int id, String nom) {

        ModelView mv = new ModelView();
        mv.setView("userDetails.jsp");
        mv.addData("value", id);
        mv.addData("nom", nom);
        return mv;
    }


}
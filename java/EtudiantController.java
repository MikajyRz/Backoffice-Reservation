package test.java;

import java.io.IOException;
import java.util.Map;

import com.annotations.ControllerAnnotation;
import com.annotations.GetMapping;
import com.annotations.PostMapping;
import com.annotations.HandleUrl;
import com.annotations.Api;
import com.annotations.Param;
import com.annotations.Session;
import com.classes.ModelView;
import com.exceptions.BadRequestException;
import com.utils.FileStorage;

@ControllerAnnotation("EtudiantController")
public class EtudiantController {

    public EtudiantController() {
    }

    // ==========================
    // EXEMPLES DE ROUTES SIMPLES
    // ==========================

    @HandleUrl("/home")
    public String home() {
        return "Bienvenue !";
    }

    @HandleUrl("/test")
    public int test() {
        return 100;
    }

    @HandleUrl("/testVoid")
    public void testVoid() {
    }

    @HandleUrl("/session/set")
    public ModelView sessionSet(@Session Map<String, Object> session,
                                @Param("key") String key,
                                @Param("value") String value) {
        session.put(key, value);
        ModelView mv = new ModelView("SessionTest.jsp");
        mv.addData("action", "set");
        mv.addData("key", key);
        mv.addData("value", value);
        return mv;
    }

    @HandleUrl("/session/get")
    public ModelView sessionGet(@Session Map<String, Object> session,
                                @Param("key") String key) {
        Object value = session.get(key);
        ModelView mv = new ModelView("SessionTest.jsp");
        mv.addData("action", "get");
        mv.addData("key", key);
        mv.addData("value", value);
        return mv;
    }

    // ==========================
    // EXEMPLES MVC AVEC FORMULAIRES
    // ==========================

    @HandleUrl("/etudiant")
    public ModelView getEtudiant() {
        ModelView mv = new ModelView("Etudiants.jsp");
        mv.addData("nom", "ANDERSON");
        mv.addData("age", 20);
        return mv;
    }

    @GetMapping("/etudiants/form8")
    public ModelView formEtudiant() {
        ModelView mv = new ModelView();
        mv.setView("Formulaire8.jsp");
        return mv;
    }

    @PostMapping("/etudiants/form")
    public ModelView submitEtudiant(@Param("nom") String n, String prenom, int age) {
        ModelView mv = new ModelView("Etudiants.jsp");
        mv.addData("nom", n);
        mv.addData("prenom", prenom);
        mv.addData("age", age);
        return mv;
    }

    @PostMapping("/etudiants/liste")
    public ModelView listeEtudiants(Map<String, Object> data, String nom) {
        ModelView mv = new ModelView("Etudiants.jsp");
        mv.addData("liste", data);
        mv.addData("nom", nom);
        return mv;
    }

    // ==========================
    // BINDING AVANCÉ D'OBJET Etudiant
    // ==========================

    @PostMapping("/test/bind")
    public ModelView testBind(Etudiant e) {
        ModelView mv = new ModelView();
        mv.setView("EtudiantResult.jsp");
        mv.addData("etudiant", e);
        return mv;
    }

    @PostMapping("/test/bind2")
    public ModelView testBind2(Etudiant e) {
        ModelView mv = new ModelView("EtudiantResult.jsp");
        mv.addData("etudiant", e);
        return mv;
    }

    @GetMapping("/sprint11/form")
    public ModelView formS11() {
        ModelView mv = new ModelView("FormulaireSp11.jsp");
        return mv;
    }

    @PostMapping("/sprint11/session")
    public ModelView sessionTest(@Session Map<String, Object> session,
                                 @Param("key") String key,
                                 @Param("value") String value) {
        session.put(key, value);
        ModelView mv = new ModelView("Etudiants.jsp");
        mv.addData("nom", "SESSION");
        mv.addData("liste", session);
        return mv;
    }

    @GetMapping("/sprint11/session")
    public ModelView sessionList(@Session Map<String, Object> session) {
        ModelView mv = new ModelView("SessionList.jsp");
        mv.addData("session", session);
        return mv;
    }

    // ==========================
    // UPLOAD DE FICHIERS
    // ==========================

    @PostMapping("/upload")
    public ModelView upload(Etudiant e, Map<String, byte[]> files) {
        for (var entry : files.entrySet()) {
            String filename = entry.getKey();
            byte[] data = entry.getValue();
            try {
                FileStorage.save(data, filename);
            } catch (IOException exp) {
                throw new RuntimeException(exp);
            }
        }

        ModelView mv = new ModelView("uploadResult.jsp");
        mv.addData("nom", e.getNom());
        mv.addData("files", files);
        return mv;
    }

    // ==========================
    // EXEMPLES D'API JSON
    // ==========================

    @Api
    @HandleUrl("/api/hello")
    public String helloApi() {
        return "Bonjour API";
    }

    @Api
    @HandleUrl("/api/etudiant")
    public ModelView apiEtudiant() {
        ModelView mv = new ModelView();
        mv.addData("nom", "RAZAFIMANJATO");
        mv.addData("prenom", "Mikajy");
        mv.addData("age", 21);
        return mv;
    }

    @Api
    @HandleUrl("/api/error400")
    public void error400() {
        throw new BadRequestException("Parametre invalide");
    }
}

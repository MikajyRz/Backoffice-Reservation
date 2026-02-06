package test.java;

import java.util.List;
import java.util.Map;

public class Etudiant {

    private String nom;
    private String prenom;
    private int age;

    private Departement departement;
    private List<Matiere> matieres;
    private Note[] notes;
    private Map<String, Note> resultats;

    public Etudiant() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public List<Matiere> getMatieres() {
        return matieres;
    }

    public void setMatieres(List<Matiere> matieres) {
        this.matieres = matieres;
    }

    public Note[] getNotes() {
        return notes;
    }

    public void setNotes(Note[] notes) {
        this.notes = notes;
    }

    public Map<String, Note> getResultats() {
        return resultats;
    }

    public void setResultats(Map<String, Note> resultats) {
        this.resultats = resultats;
    }
}

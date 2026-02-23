package test.java;

public class VoitureRow {
    private int id;
    private String immatricule;
    private String type_carburant;
    private int nb_place;

    public VoitureRow() {
    }

    public VoitureRow(int id, String immatricule, String type_carburant, int nb_place) {
        this.id = id;
        this.immatricule = immatricule;
        this.type_carburant = type_carburant;
        this.nb_place = nb_place;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImmatricule() {
        return immatricule;
    }

    public void setImmatricule(String immatricule) {
        this.immatricule = immatricule;
    }

    public String getType_carburant() {
        return type_carburant;
    }

    public void setType_carburant(String type_carburant) {
        this.type_carburant = type_carburant;
    }

    public int getNb_place() {
        return nb_place;
    }

    public void setNb_place(int nb_place) {
        this.nb_place = nb_place;
    }
}

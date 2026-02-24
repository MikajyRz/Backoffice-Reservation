package test.java;

public class ReservationRow {
    private int id;
    private String id_client;
    private int nombre_passager;
    private String date_heure_arrive;
    private int id_lieu;
    private String lieu_nom;

    public ReservationRow() {
    }

    public ReservationRow(int id, String id_client, int nombre_passager, String date_heure_arrive, int id_lieu, String lieu_nom) {
        this.id = id;
        this.id_client = id_client;
        this.nombre_passager = nombre_passager;
        this.date_heure_arrive = date_heure_arrive;
        this.id_lieu = id_lieu;
        this.lieu_nom = lieu_nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId_client() {
        return id_client;
    }

    public void setId_client(String id_client) {
        this.id_client = id_client;
    }

    public int getNombre_passager() {
        return nombre_passager;
    }

    public void setNombre_passager(int nombre_passager) {
        this.nombre_passager = nombre_passager;
    }

    public String getDate_heure_arrive() {
        return date_heure_arrive;
    }

    public void setDate_heure_arrive(String date_heure_arrive) {
        this.date_heure_arrive = date_heure_arrive;
    }

    public int getId_lieu() {
        return id_lieu;
    }

    public void setId_lieu(int id_lieu) {
        this.id_lieu = id_lieu;
    }

    public String getLieu_nom() {
        return lieu_nom;
    }

    public void setLieu_nom(String lieu_nom) {
        this.lieu_nom = lieu_nom;
    }
}

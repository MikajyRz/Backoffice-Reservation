package com.classes;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String view;
    private Map<String, Object> data;

    public ModelView() {
        this.data = new HashMap<String, Object>();
    }

    public ModelView(String view) {
        this.view = view;
        this.data = new HashMap<String, Object>();
    }

    public ModelView(Map<String, Object> data) {
        this.data = data;
    }

    public ModelView(String view, Map<String, Object> data) {
        this.view = view;
        this.data = data;
    }

    // Retourne le chemin de la vue (JSP ou autre ressource) à afficher
    public String getView() {
        return view;
    }

    // Définit le chemin de la vue (JSP ou autre ressource) à afficher
    public void setView(String view) {
        this.view = view;
    }

    // Retourne la map des données à transmettre à la vue
    public Map<String, Object> getData() {
        return data;
    }

    // Remplace la map des données à transmettre à la vue
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    // Ajoute une donnée (clé/valeur) au modèle à envoyer vers la vue
    public void addData(String key, Object value) {
        if (this.data != null) {
            this.data.put(key, value);
        }
    }
}

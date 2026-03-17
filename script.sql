-- Script de réinitialisation de la base de données
-- Supprime les tables existantes
DROP TABLE IF EXISTS distance CASCADE;
DROP TABLE IF EXISTS tournee_stop CASCADE;
DROP TABLE IF EXISTS tournee CASCADE;
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS api_token CASCADE;
DROP TABLE IF EXISTS parametre CASCADE;
DROP TABLE IF EXISTS lieu CASCADE;
DROP TABLE IF EXISTS voiture CASCADE;

-- Recréation des tables
CREATE TABLE lieu (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE voiture (
    id SERIAL PRIMARY KEY,
    immatricule VARCHAR(20) NOT NULL UNIQUE,
    type_carburant VARCHAR(2) NOT NULL,
    nb_place INTEGER NOT NULL CHECK (nb_place > 0),
    CONSTRAINT ck_voiture_type_carburant CHECK (type_carburant IN ('E', 'D', 'El', 'H'))
);

CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    id_client CHAR(4) NOT NULL,
    nombre_passager INTEGER NOT NULL CHECK (nombre_passager > 0),
    date_heure_arrive TIMESTAMP NOT NULL,
    id_lieu INTEGER NOT NULL,
    id_tournee INTEGER,
    CONSTRAINT fk_reservation_lieu FOREIGN KEY (id_lieu) REFERENCES lieu(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE tournee (
    id SERIAL PRIMARY KEY,
    id_voiture INTEGER NOT NULL,
    window_start TIMESTAMP NOT NULL,
    window_end TIMESTAMP NOT NULL,
    depart_effectif TIMESTAMP NOT NULL,
    retour_aeroport TIMESTAMP NOT NULL,
    nb_passagers_total INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_tournee_voiture FOREIGN KEY (id_voiture) REFERENCES voiture(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE tournee_stop (
    id SERIAL PRIMARY KEY,
    id_tournee INTEGER NOT NULL,
    ordre INTEGER NOT NULL CHECK (ordre > 0),
    id_lieu INTEGER NOT NULL,
    heure_arrivee TIMESTAMP NOT NULL,
    CONSTRAINT fk_tournee_stop_tournee FOREIGN KEY (id_tournee) REFERENCES tournee(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_tournee_stop_lieu FOREIGN KEY (id_lieu) REFERENCES lieu(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

ALTER TABLE reservation
    ADD CONSTRAINT fk_reservation_tournee
    FOREIGN KEY (id_tournee) REFERENCES tournee(id) ON UPDATE CASCADE ON DELETE SET NULL;

CREATE INDEX idx_reservation_date_heure_arrive ON reservation(date_heure_arrive);
CREATE INDEX idx_reservation_tournee ON reservation(id_tournee);
CREATE INDEX idx_tournee_voiture_start ON tournee(id_voiture, window_start);
CREATE INDEX idx_tournee_window_start ON tournee(window_start);
CREATE INDEX idx_tournee_stop_tournee_ordre ON tournee_stop(id_tournee, ordre);

CREATE TABLE api_token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(128) NOT NULL UNIQUE,
    date_expiration TIMESTAMP NOT NULL
);

CREATE TABLE parametre (
    vitesse_moyenne INTEGER NOT NULL,  
    temps_attente INTEGER NOT NULL     
);

CREATE TABLE distance (
    id SERIAL PRIMARY KEY,
    from_lieu INTEGER NOT NULL,
    to_lieu INTEGER NOT NULL,
    kilometer INTEGER NOT NULL,
    CONSTRAINT fk_distance_from FOREIGN KEY (from_lieu) REFERENCES lieu(id),
    CONSTRAINT fk_distance_to FOREIGN KEY (to_lieu) REFERENCES lieu(id)
);

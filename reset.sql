-- Script de réinitialisation de la base de données
-- Supprime les tables existantes
DROP TABLE IF EXISTS distance CASCADE;
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
    id_voiture INTEGER,
    CONSTRAINT fk_reservation_lieu FOREIGN KEY (id_lieu) REFERENCES lieu(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_voiture FOREIGN KEY (id_voiture) REFERENCES voiture(id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE api_token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(128) NOT NULL UNIQUE,
    date_expiration TIMESTAMP NOT NULL
);

CREATE TABLE parametre (
    vitesse_moyenne INTEGER NOT NULL,  -- km/h
    temps_attente INTEGER NOT NULL     -- minutes
);

CREATE TABLE distance (
    id SERIAL PRIMARY KEY,
    from_lieu INTEGER NOT NULL,
    to_lieu INTEGER NOT NULL,
    kilometer INTEGER NOT NULL,
    CONSTRAINT fk_distance_from FOREIGN KEY (from_lieu) REFERENCES lieu(id),
    CONSTRAINT fk_distance_to FOREIGN KEY (to_lieu) REFERENCES lieu(id)
);

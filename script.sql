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

-- Création des lieux avec IDs connus
INSERT INTO lieu(id, code, libelle) VALUES (1, 'TNR', 'Ivato Aeroport');
INSERT INTO lieu(id, code, libelle) VALUES (2, 'HOT1', 'Hotel Carlton');
INSERT INTO lieu(id, code, libelle) VALUES (3, 'HOT2', 'Hotel Lokanga');
INSERT INTO lieu(id, code, libelle) VALUES (4, 'HOT3', 'Hotel Ibis');
INSERT INTO lieu(id, code, libelle) VALUES (5, 'HOT4', 'Hotel Lokanga');

-- Distances
INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES (5, 1, 12);

-- Voitures
INSERT INTO voiture(immatricule, type_carburant, nb_place) VALUES 
('1234TAA', 'E', 5),
('4567TAB', 'D', 4),
('8901TAC', 'El', 5),
('2222TAD', 'H', 7);

-- Réservations
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_lieu) VALUES
('4631', 11, '2026-02-05 00:01:00', 4),
('4394', 1, '2026-02-05 23:55:00', 4),
('8054', 2, '2026-02-09 10:17:00', 2),
('1432', 4, '2026-02-01 15:25:00', 3),
('7861', 4, '2026-01-28 07:11:00', 2),
('3308', 5, '2026-01-28 07:45:00', 2),
('4484', 13, '2026-02-28 08:45:00', 3),
('9687', 8, '2026-02-28 13:00:00', 3),
('6302', 7, '2026-02-15 13:00:00', 2),
('8640', 1, '2026-02-18 22:55:00', 5);




-- Ce script permet de resynchroniser les séquences (AUTO_INCREMENT) de PostgreSQL
-- avec les IDs maximums réellement présents dans les tables.
-- Cela corrige l'erreur "Duplicate key value violates unique constraint" (Erreur 500).

-- 1. Table LIEU
SELECT setval(pg_get_serial_sequence('lieu', 'id'), COALESCE((SELECT MAX(id) FROM lieu), 0) + 1, false);

-- 2. Table VOITURE
SELECT setval(pg_get_serial_sequence('voiture', 'id'), COALESCE((SELECT MAX(id) FROM voiture), 0) + 1, false);

-- 3. Table RESERVATION
SELECT setval(pg_get_serial_sequence('reservation', 'id'), COALESCE((SELECT MAX(id) FROM reservation), 0) + 1, false);

-- 4. Table DISTANCE
SELECT setval(pg_get_serial_sequence('distance', 'id'), COALESCE((SELECT MAX(id) FROM distance), 0) + 1, false);

-- 5. Table API_TOKEN
SELECT setval(pg_get_serial_sequence('api_token', 'id'), COALESCE((SELECT MAX(id) FROM api_token), 0) + 1, false);

CREATE TABLE hotel (
    id_hotel SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);


CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,

    id_client CHAR(4) NOT NULL,  

    nombre_passager INTEGER NOT NULL
        CHECK (nombre_passager > 0),

    date_heure_arrive TIMESTAMP NOT NULL,

    id_hotel INTEGER NOT NULL,

    CONSTRAINT fk_reservation_hotel
        FOREIGN KEY (id_hotel)
        REFERENCES hotel(id_hotel)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE api_token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(128) NOT NULL UNIQUE,
    date_expiration TIMESTAMP NOT NULL
);

CREATE TABLE voiture (
    id SERIAL PRIMARY KEY,
    immatricule VARCHAR(20) NOT NULL UNIQUE,
    type_carburant VARCHAR(2) NOT NULL,
    nb_place INTEGER NOT NULL CHECK (nb_place > 0),
    CONSTRAINT ck_voiture_type_carburant CHECK (type_carburant IN ('E', 'D', 'El', 'H'))
);

INSERT INTO hotel(nom) VALUES ('Colbert');
INSERT INTO hotel(nom) VALUES ('Novotel');
INSERT INTO hotel(nom) VALUES ('Ibis');
INSERT INTO hotel(nom) VALUES ('Lokanga');

INSERT INTO voiture(immatricule, type_carburant, nb_place) VALUES ('1234TAA', 'E', 5);
INSERT INTO voiture(immatricule, type_carburant, nb_place) VALUES ('4567TAB', 'D', 4);
INSERT INTO voiture(immatricule, type_carburant, nb_place) VALUES ('8901TAC', 'El', 5);
INSERT INTO voiture(immatricule, type_carburant, nb_place) VALUES ('2222TAD', 'H', 7);

INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_hotel) VALUES
('4631', 11, '2026-02-05 00:01:00', 3);
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_hotel) VALUES
('4394', 1, '2026-02-05 23:55:00', 3);
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_hotel) VALUES
('8054', 2, '2026-02-09 10:17:00', 1);
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_hotel) VALUES
('1432', 4, '2026-02-01 15:25:00', 2);
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_hotel) VALUES
('7861', 4, '2026-01-28 07:11:00', 1);
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_hotel) VALUES
('3308', 5, '2026-01-28 07:45:00', 1);
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_hotel) VALUES
('4484', 13, '2026-02-28 08:45:00', 2);
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_hotel) VALUES
('9687', 8, '2026-02-28 13:00:00', 2);
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_hotel) VALUES
('6302', 7, '2026-02-15 13:00:00', 1);
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_hotel) VALUES
('8640', 1, '2026-02-18 22:55:00', 4);


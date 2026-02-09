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

INSERT INTO hotel(nom) VALUES ('Colbert');
INSERT INTO hotel(nom) VALUES ('Novotel');
INSERT INTO hotel(nom) VALUES ('Ibis');
INSERT INTO hotel(nom) VALUES ('Lokanga');

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


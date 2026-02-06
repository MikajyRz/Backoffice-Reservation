CREATE TABLE hotel (
    id_hotel SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);


CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,

    id_client CHAR(4) NOT NULL,  -- 4 chiffres exactement

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

INSERT INTO hotel(nom) VALUES ('Hotel Test');

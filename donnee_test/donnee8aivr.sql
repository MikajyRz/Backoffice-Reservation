
DELETE FROM tournee_reservation;
DELETE FROM tournee_stop;
DELETE FROM tournee;
UPDATE reservation SET id_tournee = NULL;
DELETE FROM reservation;
DELETE FROM voiture_disponibilite;
DELETE FROM distance;
DELETE FROM parametre;
DELETE FROM voiture;
DELETE FROM lieu;

INSERT INTO parametre(vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
    (1, 'AER', 'Aeroport'),
    (2, 'H1', 'Hotel 1 zone'),
    (3, 'H2', 'hotel 2 zone');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
    (1, 2, 90),
    (2, 1, 90),
    (1, 3, 65),
    (3, 1, 65),
    (2, 3, 10),
    (3, 2, 10);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
    (1, 'V-001', 'D', 10),
    (2, 'V-002', 'D', 8),
    (3, 'V-003', 'E', 8),
    (4, 'V-004', 'E', 12);

INSERT INTO voiture_disponibilite(id_voiture, jour, heure_dispo) VALUES
    (1, '2026-04-02', '00:00:00'),
    (2, '2026-04-02', '08:00:00'),
    (3, '2026-04-02', '08:00:00'),
    (4, '2026-04-02', '09:00:00');

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_tournee) VALUES
    (1, 'C001', 20, '2026-04-02 06:00:00', 2, NULL),
    (2, 'C002', 6, '2026-04-02 08:15:00', 2, NULL),
    (3, 'C003', 10, '2026-04-02 09:00:00', 2, NULL),
    (4, 'C004', 6, '2026-04-02 09:10:00', 3, NULL);

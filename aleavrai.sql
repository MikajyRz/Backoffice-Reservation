
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

INSERT INTO parametre(vitesse_moyenne, temps_attente) VALUES (50, 30);

INSERT INTO lieu(id, code, libelle) VALUES
    (1, 'AER', 'Aeroport'),
    (2, 'H1', 'Hotel 1'),
    (3, 'H2', 'hotel 2');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
    (1, 2, 90),
    (2, 1, 90),
    (1, 3, 35),
    (3, 1, 35),
    (2, 3, 60),
    (3, 2, 60);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
    (1, 'V-001', 'D', 5),
    (2, 'V-002', 'E', 5),
    (3, 'V-003', 'D', 12),
    (4, 'V-004', 'D', 9),
    (5, 'V-005', 'E', 12);

INSERT INTO voiture_disponibilite(id_voiture, jour, heure_dispo) VALUES
    (1, '2026-03-19', '09:00:00'),
    (2, '2026-03-19', '09:00:00'),
    (3, '2026-03-19', '08:00:00'),
    (4, '2026-03-19', '09:00:00'),
    (5, '2026-03-19', '13:00:00');

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_tournee) VALUES
    (1, 'C001', 7, '2026-03-19 09:00:00', 2, NULL),
    (2, 'C002', 20, '2026-03-19 08:00:00', 3, NULL),
    (3, 'C003', 3, '2026-03-19 09:10:00', 2, NULL),
    (4, 'C004', 10, '2026-03-19 09:15:00', 2, NULL),
    (5, 'C005', 5, '2026-03-19 09:20:00', 2, NULL),
    (6, 'C006', 12, '2026-03-19 13:30:00', 2, NULL);




-- Résultats de la planification
-- Trajets assignés
-- Voici la liste des réservations qui ont pu être assignées à un véhicule.

-- # ID	Véhicule	Détails Véhicule	Réservation	Passagers	Lieu	Créneau	Départ	Arrivée	Retour Aéroport
-- Créneau: 2026-03-19 08:00 - 2026-03-19 08:30
-- 3	V-003	D 12 places	#2	12	hotel 2	2026-03-19 08:00	2026-03-19 08:00	2026-03-19 08:42	2026-03-19 09:24
-- Créneau: 2026-03-19 09:00 - 2026-03-19 09:30
-- 4	V-004	D 9 places	#2	8	hotel 2	2026-03-19 09:00	2026-03-19 09:10	2026-03-19 09:52	2026-03-19 12:52
-- 4	V-004	D 9 places	#3	1	Hotel 1	2026-03-19 09:00	2026-03-19 09:10	2026-03-19 11:04	2026-03-19 12:52
-- 1	V-001	D 5 places	#3	2	Hotel 1	2026-03-19 09:00	2026-03-19 09:20	2026-03-19 11:08	2026-03-19 12:56
-- 1	V-001	D 5 places	#5	3	Hotel 1	2026-03-19 09:00	2026-03-19 09:20	2026-03-19 11:08	2026-03-19 12:56
-- 2	V-002	E 5 places	#1	3	Hotel 1	2026-03-19 09:00	2026-03-19 09:20	2026-03-19 11:08	2026-03-19 12:56
-- 2	V-002	E 5 places	#5	2	Hotel 1	2026-03-19 09:00	2026-03-19 09:20	2026-03-19 11:08	2026-03-19 12:56
-- 3	V-003	D 12 places	#1	4	Hotel 1	2026-03-19 09:00	2026-03-19 09:24	2026-03-19 11:12	2026-03-19 13:00
-- 3	V-003	D 12 places	#4	8	Hotel 1	2026-03-19 09:00	2026-03-19 09:24	2026-03-19 11:12	2026-03-19 13:00
-- Créneau: 2026-03-19 12:52 - 2026-03-19 13:22
-- 4	V-004	D 9 places	#4	2	Hotel 1	2026-03-19 12:52	2026-03-19 12:52	2026-03-19 14:40	2026-03-19 16:28
-- Créneau: 2026-03-19 13:30 - 2026-03-19 14:00
-- 5	V-005	E 12 places	#6	12	Hotel 1	2026-03-19 13:30	2026-03-19 13:30	2026-03-19 15:18	2026-03-19 17:06
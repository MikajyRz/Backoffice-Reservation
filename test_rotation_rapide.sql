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

-- Paramètres de base
-- Vitesse moyenne 60km/h (1km = 1 minute)
-- Temps d'attente du créneau: 30 minutes
INSERT INTO parametre(vitesse_moyenne, temps_attente) VALUES (60, 30);

-- Lieux
INSERT INTO lieu(id, code, libelle) VALUES
    (1, 'AER', 'Aeroport'),
    (2, 'P1', 'Proche (10km)'),
    (3, 'L1', 'Loin (45km)');

-- Distances depuis l'aéroport (AER=1)
-- Aller-retour P1 = 20 minutes
-- Aller-retour L1 = 90 minutes (1h30)
INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
    (1, 2, 10),
    (2, 1, 10),
    (1, 3, 45),
    (3, 1, 45),
    (2, 3, 35),
    (3, 2, 35);

-- Véhicules
-- V1 : Petit van (8 places) - Dispo à 08:00
-- V2 : Berline (4 places) - Dispo à 08:00
-- V3 : Grand van (12 places) - Dispo plus tard (09:00)
INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
    (1, 'VAN-8', 'D', 8),
    (2, 'BER-4', 'E', 4),
    (3, 'VAN-12', 'D', 12);

INSERT INTO voiture_disponibilite(id_voiture, jour, heure_dispo) VALUES
    (1, '2026-04-01', '08:00:00'),
    (2, '2026-04-01', '08:00:00'),
    (3, '2026-04-01', '09:00:00');

-- Réservations
-- SCÉNARIO DE TEST
-- Créneau 1 : 08:00 à 08:30
-- On a 10 personnes pour un lieu très proche (P1). V1(8pl) et V2(4pl) sont dispos.
-- Logiquement, l'algorithme va utiliser V1 pour 8 personnes. 
-- Reste: 2 personnes en attente.
-- V1 fait l'A/R en 20 min et revient à 08:20.
-- 
-- Créneau 2 : 08:25 (dans la foulée, presque collé)
-- Un nouveau groupe de 6 personnes arrive pour L1.
-- À 08:25, V1 (qui est revenu à 08:20) est dispo.
-- V1 doit PRENDRE EN PRIORITÉ les 2 restes de 08:00 (P1), PUIS les 6 nouveaux (L1).
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_tournee) VALUES
    -- Groupe 1 (Créneau 1)
    (1, 'C1', 10, '2026-04-01 08:00:00', 2, NULL),
    
    -- Groupe 2 (Créneau 2)
    (2, 'C2', 6, '2026-04-01 08:25:00', 3, NULL);


-- Résultats de la planification
-- Trajets assignés
-- Voici la liste des réservations qui ont pu être assignées à un véhicule.

-- # ID	Véhicule	Détails Véhicule	Réservation	Passagers	Lieu	Créneau	Départ	Arrivée	Retour Aéroport
-- Créneau: 2026-04-01 08:00 - 2026-04-01 08:30
-- 1	VAN-8	D 8 places	#1	8	Proche (10km)	2026-04-01 08:00	2026-04-01 08:00	2026-04-01 08:10	2026-04-01 08:20
-- 1	VAN-8	D 8 places	#2	4	Loin (45km)	2026-04-01 08:00	2026-04-01 08:25	2026-04-01 09:10	2026-04-01 09:55
-- 2	BER-4	E 4 places	#1	2	Proche (10km)	2026-04-01 08:00	2026-04-01 08:25	2026-04-01 08:35	2026-04-01 09:55
-- 2	BER-4	E 4 places	#2	2	Loin (45km)	2026-04-01 08:00	2026-04-01 08:25	2026-04-01 09:10	2026-04-01 09:55
-- Véhicules non utilisés
-- Ces véhicules étaient disponibles mais n'ont pas été nécessaires ou ne correspondaient pas aux critères.

-- # ID	Immatriculation	Carburant	Capacité
-- 3	VAN-12	D	12 places
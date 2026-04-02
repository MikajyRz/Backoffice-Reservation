-- NETTOYAGE COMPLET
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

-- 1. CONFIGURATION DE BASE
-- Vitesse : 60km/h (1km = 1min)
-- Attente : 30 minutes
INSERT INTO parametre(vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
    (1, 'AER', 'Aeroport'),
    (2, 'H-PROCHE', 'Hotel Proche (10km)'),
    (3, 'H-LOIN', 'Hotel Loin (50km)');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
    (1, 2, 10), (2, 1, 10), -- AER <-> PROCHE = 10 min aller
    (1, 3, 50), (3, 1, 50), -- AER <-> LOIN = 50 min aller
    (2, 3, 40), (3, 2, 40);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
    (1, 'VAN-12', 'D', 12),
    (2, 'POTIT-5', 'E', 5);

INSERT INTO voiture_disponibilite(id_voiture, jour, heure_dispo) VALUES
    (1, '2026-05-01', '08:00:00'),
    (2, '2026-05-01', '08:00:00');

--------------------------------------------------------------------------------
-- SCÉNARIO 1 : SYNERGIE ET POOLING (Matin)
-- Objectif : Vérifier que des petites réservations proches sont regroupées.
--------------------------------------------------------------------------------
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu) VALUES
    (1, 'CA', 3, '2026-05-01 08:00:00', 2), -- 08:00 -> Proche
    (2, 'CB', 4, '2026-05-01 08:15:00', 2), -- 08:15 -> Proche
    (3, 'CC', 2, '2026-05-01 08:20:00', 2); -- 08:20 -> Proche

-- RÉSULTAT ATTENDU : 
-- Une seule tournée pour VAN-12 à 08:20 (heure du dernier arrivé).
-- Total passagers : 3+4+2 = 9/12 places occupées.
-- Retour prévu à l'aéroport : 08:20 + 10min (aller) + 10min (retour) = 08:40.

--------------------------------------------------------------------------------
-- SCÉNARIO 2 : RÉACTIVITÉ ET RELIQUATS (Après-midi)
-- Objectif : Vérifier que les restes partent dès qu'une voiture rentre, sans attendre le prochain groupe.
--------------------------------------------------------------------------------
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu) VALUES
    -- Groupe de 15 personnes à 10h00 (va être splitté car VAN-12 est le max)
    (4, 'G15', 15, '2026-05-01 10:00:00', 3), 
    
    -- Un groupe qui arrive beaucoup plus tard (14h00)
    (5, 'TARD', 10, '2026-05-01 14:00:00', 2);

-- RÉSULTAT ATTENDU :
-- 1. À 10h00 : VAN-12 prend 12 personnes. Reste 3 personnes pour GROUPE-15.
-- 2. VAN-12 fait AER -> LOIN (50min) -> AER (50min). Retour à 11h40.
-- 3. GRÂCE À NOTRE FIX : Le système doit créer un trajet à 11h40 pour les 3 restants.
-- 4. Ils ne doivent PAS attendre le groupe de 14h00.



-- Résultats de la planification
-- Trajets assignés
-- Voici la liste des réservations qui ont pu être assignées à un véhicule.

-- # ID	Véhicule	Détails Véhicule	Réservation	Passagers	Lieu	Créneau	Départ	Arrivée	Retour Aéroport
-- Créneau: 2026-05-01 08:00 - 2026-05-01 08:30
-- 1	VAN-12	D 12 places	#1	3	Hotel Proche (10km)	2026-05-01 08:00	2026-05-01 08:20	2026-05-01 08:30	2026-05-01 08:40
-- 1	VAN-12	D 12 places	#2	4	Hotel Proche (10km)	2026-05-01 08:00	2026-05-01 08:20	2026-05-01 08:30	2026-05-01 08:40
-- 1	VAN-12	D 12 places	#3	2	Hotel Proche (10km)	2026-05-01 08:00	2026-05-01 08:20	2026-05-01 08:30	2026-05-01 08:40
-- Créneau: 2026-05-01 10:00 - 2026-05-01 10:30
-- 1	VAN-12	D 12 places	#4	10	Hotel Loin (50km)	2026-05-01 10:00	2026-05-01 10:00	2026-05-01 10:50	2026-05-01 11:40
-- 2	POTIT-5	E 5 places	#4	5	Hotel Loin (50km)	2026-05-01 10:00	2026-05-01 10:00	2026-05-01 10:50	2026-05-01 11:40
-- Créneau: 2026-05-01 14:00 - 2026-05-01 14:30
-- 1	VAN-12	D 12 places	#5	10	Hotel Proche (10km)	2026-05-01 14:00	2026-05-01 14:00	2026-05-01 14:10	2026-05-01 14:20
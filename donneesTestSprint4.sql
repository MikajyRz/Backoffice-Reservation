-- ======================================================================
-- SPRINT 4 - JEUX DE DONNEES DE TEST (1 CAS = 1 CONTRAINTE)
-- BackofficeReservation - Planification véhicules
--
-- Utilisation:
-- - Choisir UN CAS ci-dessous.
-- - Exécuter son bloc SQL (il contient son nettoyage + inserts).
-- - Aller sur: http://localhost:8080/BackofficeReservation/reservation/planning
-- - Entrer la date indiquée dans le CAS, puis cliquer sur Planifier.
-- - Comparer avec les résultats attendus (en bas de chaque CAS).
--
-- Prérequis:
-- - Le lieu id=1 doit être l'aéroport (dans le code).
-- - Le calcul des distances utilise table distance (from_lieu/to_lieu).
-- - La vitesse + attente viennent de table parametre.
-- ======================================================================

-- Helper (à copier-coller si besoin après planification)
-- SELECT r.id, r.id_client, r.nombre_passager, r.date_heure_arrive, l.libelle AS lieu, r.id_voiture, v.immatricule, v.nb_place, v.type_carburant
-- FROM reservation r
-- JOIN lieu l ON l.id = r.id_lieu
-- LEFT JOIN voiture v ON v.id = r.id_voiture
-- WHERE DATE(r.date_heure_arrive) = 'YYYY-MM-DD'
-- ORDER BY r.date_heure_arrive ASC, r.id ASC;


-- ======================================================================
-- CAS 1 - Mutualisation (même date+heure) + minimisation nb véhicules
-- Date test: 2026-03-20
-- Attendu: 2 réservations sont mutualisées dans un véhicule (5+2=7)
-- ======================================================================
-- Nettoyage
DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'A', 'Hotel Alpha'),
(3, 'B', 'Hotel Beta');

-- Distances (2 <-> 1 =10km, 3 <-> 1 =20km, 2 <-> 3 =15km)
INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10),
(1,3,20),(3,1,20),
(2,3,15),(3,2,15);

-- Véhicules
INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'V1','E',4),
(2,'V2','El',5),
(3,'V3','H',6),
(4,'V4','D',7);

-- Réservations même créneau 15:00
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(101,'C101',5,'2026-03-20 15:00:00',3,NULL),
(102,'C102',2,'2026-03-20 15:00:00',2,NULL),
(103,'C103',2,'2026-03-20 15:00:00',3,NULL);

-- Résultats attendus (après Planifier sur 2026-03-20)
-- - (101, 102) -> V4 (7 places)
-- - (103)      -> V1 (4 places)
-- - Nb véhicules à 15:00 = 2


-- ======================================================================
-- CAS 2 - Priorité de traitement (tri décroissant nb personnes)
-- Date test: 2026-03-21
-- Attendu: la réservation 6p est placée en premier, ce qui influence la création des bins.
-- ======================================================================
-- Nettoyage
DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'A', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10);

-- Véhicules: 6 et 7 pour observer l'effet
INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(10,'V6','E',6),
(11,'V7','D',7);

-- Même créneau: 6p, 1p, 1p
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(201,'C201',1,'2026-03-21 10:00:00',2,NULL),
(202,'C202',6,'2026-03-21 10:00:00',2,NULL),
(203,'C203',1,'2026-03-21 10:00:00',2,NULL);

-- Résultats attendus
-- - La 6p (#202) doit ouvrir un bin en premier.
-- - Avec V6 (6 places): #202 prend V6, puis #201/#203 ne peuvent pas se mettre dans V6 (reste 0) => un 2ème véhicule nécessaire.
-- - Donc: #202 -> V6 ; (#201 + #203) -> V7


-- ======================================================================
-- CAS 3 - Réutilisation d'un véhicule sur un autre créneau (même date, heure différente)
-- Date test: 2026-03-22
-- Attendu: un même véhicule peut être utilisé à 09:00 puis à 10:00.
-- ======================================================================
-- Nettoyage
DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'A', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'V4','D',7);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(301,'C301',7,'2026-03-22 09:00:00',2,NULL),
(302,'C302',7,'2026-03-22 10:00:00',2,NULL);

-- Résultats attendus
-- - #301 -> V4 à 09:00
-- - #302 -> non assignée (règle: 1 seul créneau par véhicule sur la journée)


-- ======================================================================
-- CAS 4 - Ordre de desserte: distance croissante, puis alphabétique si égalité
-- Date test: 2026-03-23
-- Attendu: à même distance, Beta avant Gamma (B < G)
-- ======================================================================
-- Nettoyage
DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'ALP', 'Hotel Alpha'),
(3, 'BET', 'Hotel Beta'),
(4, 'GAM', 'Hotel Gamma');

-- Alpha 10km, Beta 20km, Gamma 20km
INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10),
(1,3,20),(3,1,20),
(1,4,20),(4,1,20),
(2,3,15),(3,2,15),
(2,4,15),(4,2,15),
(3,4,5),(4,3,5);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'V7','D',7);

-- On force mutualisation dans un seul véhicule
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(401,'C401',2,'2026-03-23 12:00:00',2,NULL),
(402,'C402',2,'2026-03-23 12:00:00',4,NULL),
(403,'C403',2,'2026-03-23 12:00:00',3,NULL);

-- Résultats attendus (ordre affiché dans le planning)
-- - Ordre 1: Alpha (10km)
-- - Ordre 2: Beta  (20km)
-- - Ordre 3: Gamma (20km)  (car Beta < Gamma)


-- ======================================================================
-- CAS 5 - Intégrité réservation: un groupe reste dans un seul véhicule (pas de split)
-- Date test: 2026-03-24
-- Attendu: une réservation 8p reste non assignée si aucun véhicule >= 8.
-- ======================================================================
-- Nettoyage
DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'A', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'V4','D',7),
(2,'V5','E',5);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(501,'C501',8,'2026-03-24 14:00:00',2,NULL),
(502,'C502',5,'2026-03-24 14:00:00',2,NULL);

-- Résultats attendus
-- - #501 (8p) -> non assignée
-- - #502 (5p) -> V5 (5 places)


-- ======================================================================
-- CAS 6 - Persistance: toutes les réservations planifiées sont enregistrées (id_voiture non null)
-- Date test: 2026-03-25
-- Attendu: après planification, id_voiture != NULL pour toutes les réservations assignables.
-- ======================================================================
-- Nettoyage
DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'A', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'V4','D',7),
(2,'V3','H',6);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(601,'C601',6,'2026-03-25 08:00:00',2,NULL),
(602,'C602',1,'2026-03-25 08:00:00',2,NULL);

-- Résultats attendus
-- - #601 + #602 -> V4 (7 places) (mutualisation)
-- - Requête de contrôle (après planifier):
--   SELECT COUNT(*) FROM reservation WHERE DATE(date_heure_arrive)='2026-03-25' AND id_voiture IS NULL;  => 0


-- ======================================================================
-- CAS 7 - Tie-break carburant UNIQUEMENT si égalité de capacité
-- Date test: 2026-03-26
-- Attendu: entre 2 véhicules de même capacité, Diesel est choisi.
-- ======================================================================
-- Nettoyage
DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'A', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10);

-- Deux véhicules 5 places, l'un Diesel
INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(10,'V5-E','E',5),
(11,'V5-D','D',5);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(701,'C701',5,'2026-03-26 09:00:00',2,NULL);

-- Résultats attendus
-- - #701 -> V5-D (car même capacité 5, Diesel prioritaire)


-- ======================================================================
-- CAS 8 - Règle JOURNÉE: 1 seul créneau par véhicule (y compris si déjà assigné en base)
-- Date test: 2026-03-27
-- But: vérifier qu'un véhicule déjà utilisé à 09:00 ne peut pas être repris à 11:00.
-- ======================================================================
-- Nettoyage
DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'A', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'V7-A','D',7),
(2,'V7-B','E',7);

-- Déjà assigné en base: véhicule V7-A utilisé à 09:00
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(801,'C801',7,'2026-03-27 09:00:00',2,1);

-- À planifier: créneau 11:00 (même journée)
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(802,'C802',7,'2026-03-27 11:00:00',2,NULL);

-- Résultats attendus
-- - #801 reste sur V7-A (déjà assigné)
-- - #802 -> V7-B (V7-A doit être considéré indisponible sur le reste de la journée)


-- ======================================================================
-- IMPORTANT
-- Ce fichier contient plusieurs CAS successifs. Exécute UN SEUL CAS à la fois.
-- Si tu exécutes tout le fichier d'un coup, seul le dernier CAS restera en base.
-- ======================================================================

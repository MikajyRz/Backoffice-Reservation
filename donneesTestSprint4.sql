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
-- SCENARIO UNIQUE COMPLET (données plus complexes) - Assignation + ordre + disponibilité
-- Date test: 2026-04-05
-- Couvre:
-- - Mutualisation (même date+heure)
-- - Remplissage immédiat après ouverture d'un véhicule (greedy)
-- - Tie-break carburant si même capacité (Diesel prioritaire)
-- - Ordre de desserte: distance à l'aéroport puis alphabétique à égalité
-- - Réutilisation véhicule si retour à l'aéroport <= créneau suivant
-- - Réservation non assignable (trop de passagers)
--
-- Hypothèses temps:
-- - vitesse_moyenne=60 km/h => 1 km = 1 min
-- - le temps d'attente n'est pas inclus dans les calculs de trajet
-- ======================================================================

-- Nettoyage
DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

-- Lieux (id=1 = aéroport)
INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'ALP', 'Hotel Alpha'),
(3, 'BET', 'Hotel Beta'),
(4, 'GAM', 'Hotel Gamma'),
(5, 'DEL', 'Hotel Delta');

-- Distances depuis l'aéroport
-- Alpha 8km, Beta 12km, Gamma 12km (égalité => tri alpha: Beta avant Gamma), Delta 25km
-- + distances inter-lieux pour rendre les retours non triviaux
INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,8),(2,1,8),
(1,3,12),(3,1,12),
(1,4,12),(4,1,12),
(1,5,25),(5,1,25),
(2,3,6),(3,2,6),
(2,4,7),(4,2,7),
(2,5,18),(5,2,18),
(3,4,4),(4,3,4),
(3,5,16),(5,3,16),
(4,5,15),(5,4,15);

-- Véhicules
-- 2 véhicules de 10 places (E vs D) pour tester tie-break Diesel uniquement à capacité égale
INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'V18-D','D',18),
(2,'V10-E','E',10),
(3,'V10-D','D',10),
(4,'V7-D','D',7),
(5,'V5-E','E',5);

-- ======================================================================
-- Réservations sur la même date (2026-04-05), multi-créneaux
-- ======================================================================

-- CRENEAU 09:00
-- But:
-- - ouvrir V18-D avec 13p puis le remplir immédiatement avec 3p et 1p
-- - ouvrir ensuite un 10 places et, à capacité égale, Diesel est choisi (V10-D)
-- - mutualiser 6p+2p dans V10-D
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(901,'R901',13,'2026-04-05 09:00:00',5,NULL),
(902,'R902', 6,'2026-04-05 09:00:00',2,NULL),
(903,'R903', 3,'2026-04-05 09:00:00',3,NULL),
(904,'R904', 1,'2026-04-05 09:00:00',4,NULL),
(905,'R905', 2,'2026-04-05 09:00:00',2,NULL);

-- CRENEAU 09:15
-- But:
-- - V10-D revient à 09:16 => il n'est pas dispo à 09:15
-- - on force la création d'un bin sur V7-D, puis remplissage avec 1p
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(906,'R906', 5,'2026-04-05 09:15:00',2,NULL),
(907,'R907', 1,'2026-04-05 09:15:00',3,NULL);

-- CRENEAU 09:30
-- But:
-- - V10-D est de nouveau disponible (retour 09:16 <= 09:30) => réutilisation
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(908,'R908', 6,'2026-04-05 09:30:00',2,NULL);

-- CRENEAU 10:00
-- But:
-- - une réservation non assignable (25p)
-- - bin packing: ouvrir V18-D pour 9p puis le remplir avec 7p puis 2p (reste 0)
-- - la dernière 2p part dans le plus petit véhicule possible (V5-E)
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(909,'R909',25,'2026-04-05 10:00:00',2,NULL),
(910,'R910', 9,'2026-04-05 10:00:00',5,NULL),
(911,'R911', 7,'2026-04-05 10:00:00',2,NULL),
(912,'R912', 2,'2026-04-05 10:00:00',3,NULL),
(913,'R913', 2,'2026-04-05 10:00:00',4,NULL);


-- ======================================================================
-- Résultats attendus (après Planifier sur 2026-04-05)
-- ======================================================================
-- AFFECTATIONS ATTENDUES (id_reservation -> véhicule)
-- - 09:00
--   - #901 (13) + #903 (3) + #904 (1) -> V18-D (id_voiture=1)
--   - #902 (6) + #905 (2)           -> V10-D (id_voiture=3)  (tie-break vs V10-E)
-- - 09:15
--   - #906 (5) + #907 (1)           -> V7-D  (id_voiture=4)  (V10-D indispo car retour 09:16)
-- - 09:30
--   - #908 (6)                      -> V10-D (id_voiture=3)  (réutilisation)
-- - 10:00
--   - #909 (25)                     -> non assignée (id_voiture NULL)
--   - #910 (9) + #911 (7) + #912 (2)-> V18-D (id_voiture=1)
--   - #913 (2)                      -> V5-E  (id_voiture=5)
--
-- ORDRE DE DESSERTE (à vérifier dans l'affichage du planning)
-- Règle: distance à l'aéroport croissante, puis alphabétique si égalité.
-- Ex: pour le créneau 09:00 dans V18-D: Beta (12) avant Gamma (12).
--
-- DISPONIBILITE (retour à l'aéroport, sans temps d'attente)
-- V10-D sur 09:00 avec 2 arrêts à Alpha:
-- - Aéroport->Alpha: 8 min ; Alpha->Aéroport: 8 min => retour 09:16
-- donc V10-D indispo à 09:15, mais dispo à 09:30.
--
-- QUERIES DE VERIFICATION (à exécuter après planification)
-- 1) Voir toutes les lignes + véhicules
-- SELECT r.id, r.id_client, r.nombre_passager, r.date_heure_arrive, l.libelle AS lieu,
--        r.id_voiture, v.immatricule, v.nb_place, v.type_carburant
-- FROM reservation r
-- JOIN lieu l ON l.id = r.id_lieu
-- LEFT JOIN voiture v ON v.id = r.id_voiture
-- WHERE DATE(r.date_heure_arrive) = '2026-04-05'
-- ORDER BY r.date_heure_arrive ASC, r.id ASC;
--
-- 2) Contrôle persistance: seul #909 doit rester NULL
-- SELECT COUNT(*) AS nb_null
-- FROM reservation
-- WHERE DATE(date_heure_arrive)='2026-04-05'
--   AND id_voiture IS NULL;
-- Attendu: 1
--
-- 3) Contrôle par créneau
-- SELECT date_heure_arrive, id_voiture, COUNT(*) AS nb_res, SUM(nombre_passager) AS pax
-- FROM reservation
-- WHERE DATE(date_heure_arrive)='2026-04-05'
-- GROUP BY date_heure_arrive, id_voiture
-- ORDER BY date_heure_arrive, id_voiture;

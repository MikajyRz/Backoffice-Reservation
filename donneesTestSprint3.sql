-- Nettoyage complet des données de test
DELETE FROM distance;
DELETE FROM reservation;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

-- 1. Insertion des Paramètres (Vitesse, Temps attente)
INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

-- Création des lieux avec IDs connus
INSERT INTO lieu(id, code, libelle) VALUES (1, 'TNR', 'Ivato Aeroport');
INSERT INTO lieu(id, code, libelle) VALUES (2, 'HOT1', 'Hotel Carlton');
INSERT INTO lieu(id, code, libelle) VALUES (3, 'HOT2', 'Hotel Lokanga');
INSERT INTO lieu(id, code, libelle) VALUES (4, 'HOT3', 'Hotel Ibis');
INSERT INTO lieu(id, code, libelle) VALUES (5, 'HOT4', 'Hotel Lokanga');

-- 3. Insertion des Distances
-- Distance entre Hotel Carlton (2) et Aeroport (1) = 15 km
INSERT INTO distance (from_lieu, to_lieu, kilometer) VALUES (2, 1, 15);

-- 4. Insertion des Voitures (V1, V2, V3)
-- V1 : 4 places, Essence
INSERT INTO voiture (immatricule, nb_place, type_carburant) VALUES ('V1-ESSENCE', 4, 'E');
-- V2 : 8 places, Diesel
INSERT INTO voiture (immatricule, nb_place, type_carburant) VALUES ('V2-DIESEL', 8, 'D');
-- V3 : 5 places, Diesel
INSERT INTO voiture (immatricule, nb_place, type_carburant) VALUES ('V3-DIESEL', 5, 'D');


-- ==========================================
-- TEST 1 : DATE 29/04/2026
-- Scénario : R1(2p) -> R2(6p) -> R3(4p)
-- ==========================================

-- R1 : 2 passagers, 08:00
INSERT INTO reservation (id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) 
VALUES ('CR1', 2, '2026-04-29 08:00:00', 2, NULL);

-- R2 : 6 passagers, 09:00
INSERT INTO reservation (id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) 
VALUES ('CR2', 6, '2026-04-29 09:00:00', 2, NULL);

-- R3 : 4 passagers, 08:00
INSERT INTO reservation (id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) 
VALUES ('CR3', 4, '2026-04-29 08:00:00', 2, NULL);


-- ==========================================
-- TEST 2 : DATE 30/04/2026 (Lendemain)
-- Scénario : Vérifier que les véhicules sont de nouveau dispo
-- R4(8p) -> R5(4p)
-- ==========================================

-- R4 : 8 passagers (Besoin du grand véhicule V2)
INSERT INTO reservation (id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) 
VALUES ('CR4', 8, '2026-04-30 10:00:00', 2, NULL);

-- R5 : 4 passagers (Besoin exact de V1)
INSERT INTO reservation (id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) 
VALUES ('CR5', 4, '2026-04-30 11:00:00', 2, NULL);


-- Instructions pour le testeur :
-- ---------------------------------------------------
-- CAS 1 : Date 29/04/2026
-- 1. Allez sur la page de planification, entrez "2026-04-29".
-- 2. Vérifiez :
--    - R1 (2p) -> V1 (4pl Essence) [Capacité la plus proche]
--    - R2 (6p) -> V2 (8pl Diesel)  [Seul capable]
--    - R3 (4p) -> V3 (5pl Diesel)  [Dernier dispo]

-- CAS 2 : Date 30/04/2026
-- 1. Allez sur la page de planification, entrez "2026-04-30".
-- 2. Vérifiez :
--    - R4 (8p) -> V2 (8pl Diesel)  [V2 est de nouveau libre !]
--    - R5 (4p) -> V1 (4pl Essence) [V1 est de nouveau libre !]

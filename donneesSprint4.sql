DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'ALP', 'Hotel Alpha'),
(3, 'BET', 'Hotel Beta');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10),
(1,3,20),(3,1,20),
(2,3,12),(3,2,12);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'V6','E',6),
(2,'V5-E','E',5),
(3,'V5-D','D',5);

INSERT INTO api_token(token, date_expiration)
VALUES ('tok_valid_123', NOW() + INTERVAL '7 days');

INSERT INTO api_token(token, date_expiration)
VALUES ('tok_expired_456', NOW() - INTERVAL '2 days');

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(1001,'C1001',5,'2026-04-10 09:00:00',2,NULL),
(1002,'C1002',1,'2026-04-10 09:10:00',2,NULL);


DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'ALP', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'V6','E',6),
(2,'V6-2','D',6);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(2001,'C2001',5,'2026-04-11 09:00:00',2,NULL),
(2002,'C2002',1,'2026-04-11 09:40:00',2,NULL);


DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'ALP', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'V6','E',6);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(3001,'C3001',5,'2026-04-12 09:00:00',2,NULL),
(3002,'C3002',1,'2026-04-12 09:30:00',2,NULL);


DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'ALP', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,10),(2,1,10);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(10,'V5-E','E',5),
(11,'V5-D','D',5),
(12,'V7','E',7);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(4001,'C4001',4,'2026-04-13 09:00:00',2,NULL),
(4002,'C4002',1,'2026-04-13 09:05:00',2,NULL),
(4003,'C4003',5,'2026-04-13 09:20:00',2,NULL),
(4004,'C4004',2,'2026-04-13 09:25:00',2,NULL);

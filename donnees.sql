DELETE FROM reservation;
DELETE FROM tournee_stop;
DELETE FROM tournee;
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

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu)
VALUES
(1001,'C1001',5,'2026-04-10 09:00:00',2),
(1002,'C1002',1,'2026-04-10 09:10:00',2);


DELETE FROM reservation;
DELETE FROM tournee_stop;
DELETE FROM tournee;
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

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu)
VALUES
(2001,'C2001',5,'2026-04-11 09:00:00',2),
(2002,'C2002',1,'2026-04-11 09:40:00',2);


DELETE FROM reservation;
DELETE FROM tournee_stop;
DELETE FROM tournee;
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

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu)
VALUES
(3001,'C3001',5,'2026-04-12 09:00:00',2),
(3002,'C3002',1,'2026-04-12 09:30:00',2);


DELETE FROM reservation;
DELETE FROM tournee_stop;
DELETE FROM tournee;
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

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu)
VALUES
(4001,'C4001',4,'2026-04-13 09:00:00',2),
(4002,'C4002',1,'2026-04-13 09:05:00',2),
(4003,'C4003',5,'2026-04-13 09:20:00',2),
(4004,'C4004',2,'2026-04-13 09:25:00',2);


DELETE FROM reservation;
DELETE FROM tournee_stop;
DELETE FROM tournee;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'ALP', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,1),(2,1,1);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(21,'V1','E',6),
(22,'V2','E',6),
(23,'V3','E',6);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu)
VALUES
(5001,'C5001',6,'2026-04-14 09:00:00',2),
(5002,'C5002',6,'2026-04-14 10:00:00',2),
(5003,'C5003',6,'2026-04-14 11:00:00',2);


DELETE FROM reservation;
DELETE FROM tournee_stop;
DELETE FROM tournee;
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

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,8),(2,1,8),
(1,3,12),(3,1,12),
(1,4,18),(4,1,18),
(2,3,7),(3,2,7),
(3,4,9),(4,3,9),
(2,4,11),(4,2,11);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(31,'V8','E',8),
(32,'V5-D','D',5);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu)
VALUES
(6001,'C6001',4,'2026-04-15 09:00:00',2),
(6002,'C6002',2,'2026-04-15 09:10:00',3),
(6003,'C6003',1,'2026-04-15 09:20:00',4);


DELETE FROM reservation;
DELETE FROM tournee_stop;
DELETE FROM tournee;
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
(41,'V7','E',7);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu)
VALUES
(6101,'C6101',2,'2026-04-16 09:00:00',2),
(6102,'C6102',3,'2026-04-16 09:05:00',2),
(6103,'C6103',1,'2026-04-16 09:10:00',3);


DELETE FROM reservation;
DELETE FROM tournee_stop;
DELETE FROM tournee;
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
(51,'V6','E',6),
(52,'V5','D',5);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu)
VALUES
(6201,'C6201',8,'2026-04-17 09:00:00',2);


DELETE FROM reservation;
DELETE FROM tournee_stop;
DELETE FROM tournee;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (60, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'ALP', 'Hotel Alpha');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,5),(2,1,5);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(61,'V6','E',6);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu)
VALUES
(6301,'C6301',5,'2026-04-18 09:00:00',2),
(6302,'C6302',5,'2026-04-18 09:40:00',2);

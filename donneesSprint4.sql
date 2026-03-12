DELETE FROM reservation;
DELETE FROM distance;
DELETE FROM voiture;
DELETE FROM lieu;
DELETE FROM parametre;

INSERT INTO parametre (vitesse_moyenne, temps_attente) VALUES (50, 30);

INSERT INTO lieu(id, code, libelle) VALUES
(1, 'TNR', 'Ivato Aeroport'),
(2, 'ALP', 'Hotel1');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
(1,2,50);

INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
(1,'Vehicule1','D',12),
(2,'Vehicule2','E',5),
(3,'Vehicule3','D',5),
(4,'Vehicule4','E',12);

INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu, id_voiture) VALUES
(001,'C001',7,'2026-03-12 09:00:00',2,NULL),
(002,'C002', 11,'2026-03-12 09:00:00',2,NULL),
(003,'C003', 3,'2026-03-12 09:00:00',2,NULL),
(004,'C004', 1,'2026-03-12 09:00:00',2,NULL),
(005,'C005', 2,'2026-03-12 09:00:00',2,NULL),
(006,'C006', 20,'2026-03-12 09:00:00',2,NULL);

INSERT INTO api_token(token, date_expiration) 
VALUES ('tok_valid_123', NOW() + INTERVAL '7 days');

INSERT INTO api_token(token, date_expiration) 
VALUES ('tok_expired_456', NOW() - INTERVAL '2 days');

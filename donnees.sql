-- Création des lieux avec IDs connus
INSERT INTO lieu(id, code, libelle) VALUES (1, 'TNR', 'Ivato Aeroport');
INSERT INTO lieu(id, code, libelle) VALUES (2, 'HOT1', 'Hotel Carlton');
INSERT INTO lieu(id, code, libelle) VALUES (3, 'HOT2', 'Hotel Lokanga');
INSERT INTO lieu(id, code, libelle) VALUES (4, 'HOT3', 'Hotel Ibis');
INSERT INTO lieu(id, code, libelle) VALUES (5, 'HOT4', 'Hotel Lokanga');

-- Distances
INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES (5, 1, 12);

-- Voitures
INSERT INTO voiture(immatricule, type_carburant, nb_place) VALUES 
('1234TAA', 'E', 5),
('4567TAB', 'D', 4),
('8901TAC', 'El', 5),
('2222TAD', 'H', 7);

-- Réservations
INSERT INTO reservation(id_client, nombre_passager, date_heure_arrive, id_lieu) VALUES
('4631', 11, '2026-02-05 00:01:00', 4),
('4394', 1, '2026-02-05 23:55:00', 4),
('8054', 2, '2026-02-09 10:17:00', 2),
('1432', 4, '2026-02-01 15:25:00', 3),
('7861', 4, '2026-01-28 07:11:00', 2),
('3308', 5, '2026-01-28 07:45:00', 2),
('4484', 13, '2026-02-28 08:45:00', 3),
('9687', 8, '2026-02-28 13:00:00', 3),
('6302', 7, '2026-02-15 13:00:00', 2),
('8640', 1, '2026-02-18 22:55:00', 5);



-- 1. Table LIEU
SELECT setval(pg_get_serial_sequence('lieu', 'id'), COALESCE((SELECT MAX(id) FROM lieu), 0) + 1, false);

-- 2. Table VOITURE
SELECT setval(pg_get_serial_sequence('voiture', 'id'), COALESCE((SELECT MAX(id) FROM voiture), 0) + 1, false);

-- 3. Table RESERVATION
SELECT setval(pg_get_serial_sequence('reservation', 'id'), COALESCE((SELECT MAX(id) FROM reservation), 0) + 1, false);

-- 4. Table DISTANCE
SELECT setval(pg_get_serial_sequence('distance', 'id'), COALESCE((SELECT MAX(id) FROM distance), 0) + 1, false);

-- 5. Table API_TOKEN
SELECT setval(pg_get_serial_sequence('api_token', 'id'), COALESCE((SELECT MAX(id) FROM api_token), 0) + 1, false);

INSERT INTO api_token(token, date_expiration) 
VALUES ('tok_valid_123', NOW() + INTERVAL '7 days');

INSERT INTO api_token(token, date_expiration) 
VALUES ('tok_expired_456', NOW() - INTERVAL '2 days');
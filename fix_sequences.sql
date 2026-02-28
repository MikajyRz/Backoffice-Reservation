-- Ce script permet de resynchroniser les séquences (AUTO_INCREMENT) de PostgreSQL
-- avec les IDs maximums réellement présents dans les tables.
-- Cela corrige l'erreur "Duplicate key value violates unique constraint" (Erreur 500).

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

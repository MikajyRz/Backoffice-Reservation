-- NETTOYAGE
DELETE FROM tournee_reservation; DELETE FROM tournee_stop; DELETE FROM tournee;
UPDATE reservation SET id_tournee = NULL;
DELETE FROM reservation; DELETE FROM voiture_disponibilite;
DELETE FROM distance; DELETE FROM parametre; DELETE FROM voiture; DELETE FROM lieu;

-- CONFIGURATION
-- Vitesse 50km/h (1km = 1.2 min)
INSERT INTO parametre(vitesse_moyenne, temps_attente) VALUES (50, 30);

INSERT INTO lieu(id, code, libelle) VALUES
    (1, 'AER', 'Aeroport'),
    (2, 'H-NORD', 'Hotel Nord (15km)'),
    (3, 'PLAGE', 'La Plage (35km)'),
    (4, 'MONTAGNE', 'La Montagne (100km)');

INSERT INTO distance(from_lieu, to_lieu, kilometer) VALUES
    (1, 2, 15), (2, 1, 15), -- AER <-> NORD = 18 min
    (1, 3, 35), (3, 1, 35), -- AER <-> PLAGE = 42 min
    (1, 4, 100), (4, 1, 100), -- AER <-> MONTAGNE = 120 min (2h)
    (2, 3, 20), (3, 2, 20), -- NORD <-> PLAGE = 24 min
    (2, 4, 85), (3, 4, 65);

-- FLOTTE MIXTE
INSERT INTO voiture(id, immatricule, type_carburant, nb_place) VALUES
    (1, 'BUS-15', 'D', 15),
    (2, 'VAN-9', 'D', 9),
    (3, 'E-CAR-5', 'E', 5);

INSERT INTO voiture_disponibilite(id_voiture, jour, heure_dispo) VALUES
    (1, '2026-06-01', '08:00:00'),
    (2, '2026-06-01', '08:00:00'),
    (3, '2026-06-01', '08:00:00');

-- RÉSERVATIONS (La stratégie de Pooling va être testée ici)
INSERT INTO reservation(id, id_client, nombre_passager, date_heure_arrive, id_lieu) VALUES
    -- VAGUE 1 (08h00 - 08h30) : 19 personnes au total
    (1, 'GRA', 10, '2026-06-01 08:00:00', 3), -- 10p pour PLAGE
    (2, 'SOL1', 2, '2026-06-01 08:10:00', 2),    -- 2p pour NORD
    (3, 'SOL2', 3, '2026-06-01 08:15:00', 3),    -- 3p pour PLAGE
    (4, 'GRB', 4, '2026-06-01 08:20:00', 2),  -- 4p pour NORD

    -- VAGUE 2 (Arrivée massive pour un lieu lointain)
    (5, 'EXP', 20, '2026-06-01 11:30:00', 4); -- 20p pour MONTAGNE (2h de route !)

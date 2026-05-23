# Backoffice Reservation

Application web Java/JSP de gestion de reservations de transport, orientee back-office. Le projet permet de gerer les vehicules, les disponibilites, les reservations clients et la planification automatique des tournees en fonction des passagers, des lieux, des distances et des contraintes de capacite.

## Fonctionnalites

- Gestion des voitures : creation, modification, suppression et consultation.
- Gestion des disponibilites par vehicule, jour et heure.
- Creation et consultation des reservations clients.
- Planification des reservations par date.
- Affectation des reservations aux vehicules selon les places disponibles.
- Generation de tournees avec ordre des arrets, horaires d'arrivee et retour aeroport.
- Historique des trajets planifies.
- Endpoints API pour les voitures, les reservations et la planification.

## Stack technique

- Java 17
- JSP / JSTL
- Jakarta Servlet 5
- PostgreSQL
- Tomcat 10
- Framework MVC Java personnalise avec annotations (`@ControllerAnnotation`, `@GetMapping`, `@PostMapping`, `@Api`)

## Structure du projet

```text
.
|-- java/                  # Controleurs et modeles Java
|-- webapp/                # Pages JSP, CSS, WEB-INF et librairies
|-- script.sql             # Schema de base de donnees
|-- donnees.sql            # Donnees de test
|-- framework.properties   # Configuration application et base de donnees
|-- script.bat             # Compilation, packaging WAR et deploiement Tomcat
`-- build/                 # WAR genere
```

## Base de donnees

Le projet utilise PostgreSQL avec la base :

```properties
db.url=jdbc:postgresql://localhost:5432/backoffice_reservation
db.user=postgres
db.password=postgres
```

Le schema principal est disponible dans `script.sql`. Il contient notamment les tables :

- `voiture`
- `voiture_disponibilite`
- `reservation`
- `tournee`
- `tournee_stop`
- `tournee_reservation`
- `lieu`
- `distance`
- `parametre`
- `api_token`

## Installation

1. Creer la base PostgreSQL :

```sql
CREATE DATABASE backoffice_reservation;
```

2. Executer le schema :

```bash
psql -U postgres -d backoffice_reservation -f script.sql
```

3. Ajouter les donnees de test si necessaire :

```bash
psql -U postgres -d backoffice_reservation -f donnees.sql
```

4. Verifier la configuration dans `framework.properties`.

5. Compiler et deployer l'application :

```bat
script.bat
```

Par defaut, le script genere `build/BackofficeReservation.war` et le deploie dans Tomcat.

## Acces application

Apres deploiement, l'application est disponible sur :

```text
http://localhost:8080/BackofficeReservation/
```

## Pages principales

- `/voiture` : liste des voitures.
- `/voiture/form` : formulaire de voiture.
- `/voiture/disponibilite` : disponibilites des voitures.
- `/reservation/form` : creation de reservation.
- `/reservation/planning` : planification des reservations.
- `/reservation/historique-trajets` : historique des tournees.

## API principales

- `GET /api/voitures`
- `POST /api/voitures`
- `POST /api/voitures/update`
- `POST /api/voitures/delete`
- `GET /api/reservations`
- `POST /api/reservations`
- `GET /api/plan-date`
- `POST /api/plan-date`

## Objectif du projet

Ce back-office a ete concu pour automatiser la gestion des reservations de transport et optimiser l'affectation des vehicules. Il centralise les donnees de reservation, les contraintes de disponibilite et les parametres de trajet afin de produire une planification exploitable par les administrateurs.

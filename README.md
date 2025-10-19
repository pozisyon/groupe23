# L'Arbre des 32 cartes – Projet "studio-grade" (v2)
- **core/** : moteur du jeu (Java)
- **api/** : API Spring Boot (REST + JSON), renvoie un plateau 4×8
- **frontend/** : React (Vite) qui affiche le plateau
- **docker/** : docker-compose + Nginx + Postgres

## Démarrage rapide
```bash
mvn -q clean install
cd api && mvn spring-boot:run
cd ../frontend && npm install && npm run dev
```
Puis ouvre http://localhost:5173

## Endpoints
- `POST /api/game/start`
- `GET  /api/game/state`

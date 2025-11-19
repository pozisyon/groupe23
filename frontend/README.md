# Arbre32 Frontend (React + Vite)

Frontend complet pour le projet **Arbre32** (jeu de stratégie) en React + TypeScript.

## ⚙️ Prérequis

- Node.js 18+ recommandé
- Backend Spring Boot Arbre32 déjà lancé sur `http://localhost:8080`

## 🚀 Installation

```bash
npm install
npm run dev
```

Par défaut, Vite démarre sur `http://localhost:5173`.

## 🔗 Configuration de l'API

Par défaut, le frontend pointe sur :

- `http://localhost:8080` pour l'API REST
- `http://localhost:8080/ws` pour le WebSocket SockJS

Tu peux surcharger ces valeurs avec un fichier `.env` :

```bash
VITE_API_BASE_URL=http://localhost:8080
VITE_WS_URL=http://localhost:8080/ws
```

## 🧱 Structure

- `src/App.tsx` : routes principales
- `src/pages/*` : pages (`Login`, `Register`, `Lobby`, `Game`)
- `src/api/*` : appels axios vers le backend
- `src/store/*` : stores Zustand (auth, game)
- `src/websocket/socket.ts` : client STOMP/SockJS
- `src/components/*` : composants partagés

Tu peux maintenant brancher la vraie logique d'affichage du plateau Arbre32 dans `GamePage.tsx`.
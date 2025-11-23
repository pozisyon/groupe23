#!/usr/bin/env bash

# ============================================================
# Script de test complet backend Arbre32
# - Login
# - Création de game
# - Lecture état
# - Join
# - Play avec carte jouable
# - Chat
# - Liste des parties ouvertes
# Nécessite : curl, jq
# ============================================================

set -euo pipefail

BASE_URL="http://localhost:8080"

# ⚠️ À ADAPTER si besoin
EMAIL="newuser@mail.com"
PASSWORD="1234"

echo "🔐 Connexion..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")

echo "Réponse login : $LOGIN_RESPONSE"

TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.accessToken')

if [[ "$TOKEN" == "null" || -z "$TOKEN" ]]; then
  echo "❌ Impossible de récupérer le token. Vérifie ton email/mot de passe."
  exit 1
fi

echo "✅ Token récupéré."

AUTH_HEADER="Authorization: Bearer $TOKEN"

echo
echo "🎮 Création de partie (mode 32 cartes)..."

CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/game/create" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{"players":["J1","J2"],"mode":32}')

echo "Réponse création : $CREATE_RESPONSE"

GAME_ID=$(echo "$CREATE_RESPONSE" | jq -r '.gameId')

if [[ "$GAME_ID" == "null" || -z "$GAME_ID" ]]; then
  echo "❌ Impossible de récupérer l'ID de la partie."
  exit 1
fi

echo "✅ GameID : $GAME_ID"

echo
echo "🧩 État initial..."
STATE=$(curl -s "$BASE_URL/api/game/$GAME_ID/state" -H "$AUTH_HEADER")
echo "$STATE" | jq

TURN_PLAYER=$(echo "$STATE" | jq -r '.turnPlayer')
echo
echo "Joueur courant (turnPlayer) : $TURN_PLAYER"

echo
echo "👥 Test join (mode public, pas d'auth obligatoire côté serveur)..."

JOIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/game/$GAME_ID/join" -H "$AUTH_HEADER")
echo "Réponse join : $JOIN_RESPONSE"

echo
echo "📜 Parties ouvertes..."
OPEN_RESPONSE=$(curl -s "$BASE_URL/api/lobby/open")
echo "$OPEN_RESPONSE" | jq

echo
echo "🃏 Sélection d'une carte jouable..."

CARD_ID=$(echo "$STATE" | jq -r '
  .board[][] | select(.playable == true) | .id
' | head -n 1)

if [[ -z "$CARD_ID" ]]; then
  echo "❌ Aucune carte jouable trouvée dans l'état initial."
  exit 1
fi

echo "Carte jouable sélectionnée : $CARD_ID"

echo
echo "🃏 Test jouer la carte..."

PLAY_RESPONSE=$(curl -s -X POST "$BASE_URL/api/game/$GAME_ID/play" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d "{\"cardId\":\"$CARD_ID\"}")

echo "Réponse play :"
echo "$PLAY_RESPONSE" | jq || echo "$PLAY_RESPONSE"

echo
echo "💬 Test chat (HTTP)..."

CHAT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/chat/send" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d "{\"gameId\":\"$GAME_ID\",\"from\":\"$EMAIL\",\"message\":\"Bonjour depuis le script de test !\"}")

echo "Réponse chat : $CHAT_RESPONSE"

echo
echo "✅ Tous les tests HTTP sont terminés."

#!/bin/bash

API="http://localhost:8080"

echo ""
echo "==============================="
echo " 🔐 1. Connexion utilisateur"
echo "==============================="

LOGIN_RESPONSE=$(curl -s -X POST "$API/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"newuser@mail.com","password":"1234"}')

echo "Réponse login : $LOGIN_RESPONSE"

TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.accessToken')

if [ "$TOKEN" = "null" ]; then
  echo "❌ Échec login"
  exit 1
fi

echo "✅ Token : $TOKEN"


echo ""
echo "==============================="
echo " 🎮 2. Création d'une nouvelle partie"
echo "==============================="

CREATE_RESPONSE=$(curl -s -X POST "$API/api/game/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"players":["J1","J2"], "mode":32}')

echo "Réponse création : $CREATE_RESPONSE"

GAME_ID=$(echo "$CREATE_RESPONSE" | jq -r '.gameId')

if [ "$GAME_ID" = "null" ]; then
  echo "❌ Échec de création de partie"
  exit 1
fi

echo "✅ GameID : $GAME_ID"


echo ""
echo "==============================="
echo " 🧩 3. État initial de la partie"
echo "==============================="

curl -s "$API/api/game/$GAME_ID/state" \
  -H "Authorization: Bearer $TOKEN" | jq .


echo ""
echo "==============================="
echo " 🤖 4. Simulation complète du jeu"
echo "==============================="

SIMULATION_RESPONSE=$(curl -s -X POST "$API/api/game/$GAME_ID/simulate" \
  -H "Authorization: Bearer $TOKEN")

echo "$SIMULATION_RESPONSE" | jq .


echo ""
echo "==============================="
echo " 🎉 Fin de la simulation"
echo "==============================="

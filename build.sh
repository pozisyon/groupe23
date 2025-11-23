#!/bin/bash

echo "🟦 Build React..."
cd frontend
npm install
npm run build

echo "🟩 Nettoyage du dossier static Spring..."
rm -rf ../api/src/main/resources/static/*

echo "🟩 Copie du build React → Spring Boot..."
cp -r dist/* ../api/src/main/resources/static/

echo "🟧 Build Spring Boot..."
cd ../api
mvn clean package

echo "✔️ Build complet terminé !"

#!/bin/bash

echo "[INFO] Avvio del server UNO multiplayer..."

# Compilazione del progetto
echo "[INFO] Compilazione Maven in corso..."
mvn clean package
BUILD_STATUS=$?

if [ $BUILD_STATUS -ne 0 ]; then
  echo "[ERROR] La compilazione Maven è fallita. Interruzione."
  exit 1
fi

# Verifica che il JAR esista
JAR_PATH="target/app.jar"
if [ ! -f "$JAR_PATH" ]; then
  echo "[ERROR] JAR non trovato: $JAR_PATH"
  exit 2
fi

# Avvio del server
echo "[INFO] Avvio del server con java -jar..."
java -jar "$JAR_PATH"
RUN_STATUS=$?

if [ $RUN_STATUS -ne 0 ]; then
  echo "[ERROR] Il server si è arrestato con codice $RUN_STATUS"
  exit $RUN_STATUS
fi

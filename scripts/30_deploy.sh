#!/usr/bin/env bash
# Compila a aplicacao e faz o deploy no Web App via Azure CLI (az webapp deploy).
# Pre-requisito: source scripts/00_env.sh
set -euo pipefail

: "${RG:?Execute antes: source scripts/00_env.sh}"
JAR=target/dimdim-webapp.jar

echo "[1/4] Compilando (Maven)..."
mvn clean package -DskipTests -B -q
test -f "$JAR" || { echo "ERRO: $JAR nao foi gerado"; exit 1; }
ls -lh "$JAR"

echo "[2/4] Deploy no Web App ${WEBAPP} (az webapp deploy)..."
az webapp deploy \
  --resource-group "$RG" \
  --name "$WEBAPP" \
  --src-path "$JAR" \
  --type jar \
  --output none

HOST=$(az webapp show --resource-group "$RG" --name "$WEBAPP" --query defaultHostName --output tsv)
URL="https://${HOST}"

echo "[3/4] Aguardando a aplicacao subir (ate ~7 minutos)..."
CODE=000
for i in $(seq 1 30); do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" --max-time 20 "${URL}/actuator/health" || true)
  echo "  tentativa ${i}/30: HTTP ${CODE}"
  [ "$CODE" = "200" ] && break
  sleep 15
done

if [ "$CODE" != "200" ]; then
  echo "ERRO: a aplicacao nao respondeu 200 em /actuator/health."
  echo "Veja o log com: az webapp log tail --resource-group $RG --name $WEBAPP"
  exit 1
fi

echo "[4/4] Health check (inclui a conexao com o Azure SQL):"
curl -s "${URL}/actuator/health"
echo
echo "OK. URL publica: ${URL}"

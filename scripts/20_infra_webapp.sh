#!/usr/bin/env bash
# Cria App Service Plan, Web App (Java 17), Log Analytics e Application Insights
# e configura as variaveis de ambiente do Web App.
# Pre-requisito: source scripts/00_env.sh
# Opcional: PLAN_SKU=B1 bash scripts/20_infra_webapp.sh   (padrao: F1)
set -euo pipefail

: "${RG:?Execute antes: source scripts/00_env.sh}"
: "${SQL_PASS:?Execute antes: source scripts/00_env.sh}"

PLAN_SKU="${PLAN_SKU:-F1}"
RUNTIME="${RUNTIME:-JAVA|17-java17}"

echo "[0/7] Validando runtime ${RUNTIME}..."
RUNTIMES=$(az webapp list-runtimes --os-type linux --output tsv)
if ! grep -q "^${RUNTIME}[[:space:]]" <<< "$RUNTIMES"; then
  echo "ERRO: runtime ${RUNTIME} nao disponivel. Runtimes Java encontrados:"
  grep -i java <<< "$RUNTIMES" || true
  exit 1
fi

echo "[1/7] Instalando extensao application-insights..."
az extension add --name application-insights --yes --only-show-errors

echo "[2/7] Criando App Service Plan ${PLAN} (SKU ${PLAN_SKU}, Linux)..."
az appservice plan create \
  --name "$PLAN" \
  --resource-group "$RG" \
  --location "${WEB_LOCATION:-$LOCATION}" \
  --sku "$PLAN_SKU" \
  --is-linux \
  --query "{plano:name, sku:sku.name, regiao:location}" \
  --output table

echo "[3/7] Criando Web App ${WEBAPP} (${RUNTIME})..."
az webapp create \
  --name "$WEBAPP" \
  --resource-group "$RG" \
  --plan "$PLAN" \
  --runtime "$RUNTIME" \
  --query "{webapp:name, estado:state, host:defaultHostName}" \
  --output table

echo "[4/7] Criando Log Analytics ${LAW} e Application Insights ${APPI}..."
az monitor log-analytics workspace create \
  --resource-group "$RG" \
  --workspace-name "$LAW" \
  --location "${WEB_LOCATION:-$LOCATION}" \
  --query "{workspace:name, regiao:location}" \
  --output table

LAW_ID=$(az monitor log-analytics workspace show \
  --resource-group "$RG" --workspace-name "$LAW" --query id --output tsv)

az monitor app-insights component create \
  --app "$APPI" \
  --location "${WEB_LOCATION:-$LOCATION}" \
  --resource-group "$RG" \
  --workspace "$LAW_ID" \
  --kind web \
  --application-type web \
  --query "{appinsights:name, regiao:location, tipo:kind}" \
  --output table

APPI_CS=$(az monitor app-insights component show \
  --app "$APPI" --resource-group "$RG" --query connectionString --output tsv)

echo "[5/7] Configurando variaveis de ambiente do Web App (valores nao sao exibidos)..."
az webapp config appsettings set \
  --resource-group "$RG" \
  --name "$WEBAPP" \
  --output none \
  --settings \
  "SPRING_DATASOURCE_URL=jdbc:sqlserver://${SQL_FQDN}:1433;database=${SQL_DB};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;" \
  "SPRING_DATASOURCE_USERNAME=${SQL_ADMIN}" \
  "SPRING_DATASOURCE_PASSWORD=${SQL_PASS}" \
  "APPLICATIONINSIGHTS_CONNECTION_STRING=${APPI_CS}" \
  "ApplicationInsightsAgent_EXTENSION_VERSION=~3" \
  "WEBSITES_PORT=8080" \
  "SERVER_PORT=8080" \
  "WEBSITES_CONTAINER_START_TIME_LIMIT=600"

echo "[6/7] Forcando HTTPS e habilitando logs do container..."
az webapp update --resource-group "$RG" --name "$WEBAPP" --https-only true --output none
az webapp log config --resource-group "$RG" --name "$WEBAPP" --docker-container-logging filesystem --output none

echo "[7/7] Conferindo..."
echo "Variaveis configuradas (somente os nomes):"
az webapp config appsettings list --resource-group "$RG" --name "$WEBAPP" --query "[].name" --output tsv
echo "OK. URL do Web App: https://$(az webapp show --resource-group "$RG" --name "$WEBAPP" --query defaultHostName --output tsv)"

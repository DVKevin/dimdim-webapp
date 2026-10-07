#!/usr/bin/env bash
# Cria o Resource Group e o Azure SQL Database (PaaS) via Azure CLI.
# Pre-requisito: source scripts/00_env.sh
set -euo pipefail

: "${RG:?Execute antes: source scripts/00_env.sh}"
: "${SQL_PASS:?Execute antes: source scripts/00_env.sh}"

echo "[1/5] Criando Resource Group ${RG} em ${LOCATION}..."
az group create --name "$RG" --location "$LOCATION" --output table

echo "[2/5] Criando servidor Azure SQL ${SQL_SERVER}..."
az sql server create \
  --name "$SQL_SERVER" \
  --resource-group "$RG" \
  --location "$LOCATION" \
  --admin-user "$SQL_ADMIN" \
  --admin-password "$SQL_PASS" \
  --output table

echo "[3/5] Criando banco ${SQL_DB} (SKU Basic)..."
az sql db create \
  --resource-group "$RG" \
  --server "$SQL_SERVER" \
  --name "$SQL_DB" \
  --service-objective Basic \
  --backup-storage-redundancy Local \
  --output table

echo "[4/5] Liberando acesso dos servicos Azure (Web App) ao servidor SQL..."
az sql server firewall-rule create \
  --resource-group "$RG" \
  --server "$SQL_SERVER" \
  --name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0 \
  --output table

echo "[5/5] Conferindo o banco criado..."
az sql db show \
  --resource-group "$RG" \
  --server "$SQL_SERVER" \
  --name "$SQL_DB" \
  --query "{banco:name, sku:currentSku.name, status:status}" \
  --output table

echo "OK. Servidor: ${SQL_FQDN}"

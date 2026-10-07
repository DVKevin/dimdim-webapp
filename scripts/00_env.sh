#!/usr/bin/env bash
# Uso: source scripts/00_env.sh
# Carrega as variaveis do projeto DimDim. A senha do SQL e digitada na sessao
# (nunca fica salva em arquivo nem no repositorio).

export RM=563454
export LOCATION=chilecentral
export RG="rg-dimdim-rm${RM}"
export SQL_SERVER="sqldimdimrm${RM}"
export SQL_DB=dimdimdb
export SQL_ADMIN=dimdimadmin
export SQL_FQDN="${SQL_SERVER}.database.windows.net"
export PLAN="plan-dimdim-rm${RM}"
export WEBAPP="webapp-dimdim-rm${RM}"
export LAW="law-dimdim-rm${RM}"
export APPI="appi-dimdim-rm${RM}"

if [ -z "${SQL_PASS:-}" ]; then
  read -r -s -p "Senha do admin do SQL (min. 12 caracteres, maiuscula, minuscula, numero e _ ): " SQL_PASS
  echo
  if [ "${#SQL_PASS}" -lt 12 ] || ! [[ "$SQL_PASS" =~ [A-Z] ]] || ! [[ "$SQL_PASS" =~ [a-z] ]] || ! [[ "$SQL_PASS" =~ [0-9] ]]; then
    echo "ERRO: senha fraca. Use 12+ caracteres com maiuscula, minuscula e numero."
    unset SQL_PASS
    return 1
  fi
  export SQL_PASS
fi

echo "Variaveis carregadas: RG=$RG | SQL=$SQL_SERVER | WEBAPP=$WEBAPP | regiao=$LOCATION"

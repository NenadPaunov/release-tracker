#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    SELECT 'CREATE DATABASE keycloak'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak')\gexec
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "keycloak" <<-EOSQL
    GRANT ALL PRIVILEGES ON SCHEMA public TO "$POSTGRES_USER";
    ALTER SCHEMA public OWNER TO "$POSTGRES_USER";
EOSQL

echo "Keycloak database check completed and privileges granted!"
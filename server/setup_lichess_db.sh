#!/bin/bash

# Define real values
DB_NAME="chess_evaluations"
DB_USER="chess_evaluations_user"
DB_PASS="chess_evaluations_password"
TABLE_NAME="lichess_evaluations"

echo "🛠 Running setup as OS user: $USER"

# Create user (role)
psql -d postgres <<EOF
DO \$\$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '${DB_USER}') THEN
      RAISE NOTICE 'Creating role ${DB_USER}...';
      CREATE ROLE ${DB_USER} WITH LOGIN PASSWORD '${DB_PASS}';
   ELSE
      RAISE NOTICE 'Role ${DB_USER} already exists.';
   END IF;
END
\$\$;
EOF

# Create database if not exists
if ! psql -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname = '${DB_NAME}'" | grep -q 1; then
  echo "📦 Creating database '${DB_NAME}'..."
  createdb -O ${DB_USER} ${DB_NAME}
else
  echo "✅ Database '${DB_NAME}' already exists."
fi

# Create table
echo "📐 Creating table '${TABLE_NAME}'..."
PGPASSWORD="${DB_PASS}" psql -U "${DB_USER}" -d "${DB_NAME}" <<EOF
CREATE TABLE IF NOT EXISTS ${TABLE_NAME} (
    fen TEXT PRIMARY KEY,
    score INTEGER
);
EOF

echo "✅ All done."

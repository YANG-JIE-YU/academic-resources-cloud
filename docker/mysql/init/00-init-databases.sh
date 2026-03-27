#!/bin/sh
set -eu

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<'EOSQL'
CREATE DATABASE IF NOT EXISTS academic_cloud_user_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS academic_cloud_paper_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS academic_cloud_ai_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EOSQL

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" academic_cloud_user_db < /docker-entrypoint-initdb.d/sql/academic_cloud_user_db_init.sql
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" academic_cloud_paper_db < /docker-entrypoint-initdb.d/sql/academic_cloud_paper_db_init.sql
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" academic_cloud_ai_db < /docker-entrypoint-initdb.d/sql/academic_cloud_ai_db_init.sql

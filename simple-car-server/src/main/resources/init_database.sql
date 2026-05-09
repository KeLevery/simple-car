-- Simple Car database initialization entrypoint.
-- Run this file from the MySQL client while the current directory is
-- simple-car-server/src/main/resources:
--
--   mysql -uroot -p simple_car < init_database.sql
--
-- The order below keeps table creation before seed data and groups feature
-- extension data after the core demo account and vehicle data.

SOURCE schema.sql;
SOURCE admin_auth.sql;
SOURCE user_settings.sql;
SOURCE data.sql;
SOURCE add_features.sql;
SOURCE new_tables.sql;

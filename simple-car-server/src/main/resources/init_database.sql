-- Simple Car database initialization entrypoint.
-- Run this file from the MySQL client while the current directory is
-- simple-car-server/src/main/resources:
--
--   mysql -uroot -p simple_car < init_database.sql
--
-- The order below keeps table creation before seed data and groups feature
-- extension data after the core demo account and vehicle data.

SOURCE schema.sql;
SOURCE user_settings.sql;
SOURCE data.sql;
SOURCE add_features.sql;
SOURCE new_tables.sql;

-- create_notice_table.sql is intentionally not sourced here because
-- schema.sql already creates the notice table used by the application.

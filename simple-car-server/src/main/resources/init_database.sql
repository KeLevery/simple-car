-- Simple Car database initialization entrypoint.
-- WARNING: rebuild_database.sql drops and recreates the simple_car database.
-- Run this file from simple-car-server/src/main/resources:
--
--   mysql -uroot -p < init_database.sql

SOURCE rebuild_database.sql;

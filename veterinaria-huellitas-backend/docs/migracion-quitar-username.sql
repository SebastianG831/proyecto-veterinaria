-- ============================================================================
-- MIGRACIÓN: el login ahora es con correo y la tabla usuarios ya no tiene "username".
-- Solo necesitas esto si ya tenías la base de datos "huellitasdb" creada con la versión anterior.
-- (Hibernate con ddl-auto=update agrega tablas y columnas nuevas, pero NO borra las viejas;
--  como "username" era obligatorio, los registros nuevos fallarían si no la quitas.)
-- Elige UNA de las dos opciones.
-- ============================================================================

-- OPCIÓN A (la más simple, pierdes los datos de prueba):
-- Borra la base. Al iniciar el backend se crea de nuevo y data.sql vuelve a cargar
-- roles, administrador y servicios de ejemplo.
-- DROP DATABASE huellitasdb;

-- OPCIÓN B (conserva los usuarios que ya registraste):
USE huellitasdb;
ALTER TABLE usuarios DROP COLUMN username;

-- Si el DROP COLUMN da error por un índice único, mira cómo se llama con:
--   SHOW INDEX FROM usuarios;
-- y elimínalo con:  ALTER TABLE usuarios DROP INDEX <nombre_del_indice>;  (luego repite el DROP COLUMN)

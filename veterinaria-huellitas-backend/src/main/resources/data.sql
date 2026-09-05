-- Roles del sistema Clinica Veterinaria Huellitas
-- ADMIN   -> staff/personal de la veterinaria
-- CLIENTE -> dueño/a de mascota que reserva citas
INSERT INTO roles (nombre) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ADMIN');
INSERT INTO roles (nombre) SELECT 'CLIENTE' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'CLIENTE');

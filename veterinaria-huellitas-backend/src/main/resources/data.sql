-- Roles del sistema Clinica Veterinaria Huellitas
-- ADMIN   -> staff/personal de la veterinaria
-- CLIENTE -> dueño/a de mascota que reserva citas
INSERT INTO roles (nombre) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ADMIN');
INSERT INTO roles (nombre) SELECT 'CLIENTE' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'CLIENTE');

-- Usuario ADMIN inicial (credenciales en documentación interna del equipo)
INSERT INTO usuarios (username, password, email, nombre_completo, rol_id)
SELECT 'admin', '$2b$10$2Gc/PIFB9mfU7vMpH0GsjOD6PbrQUhtc6yod9QxBKZ4PCBIHK0PSW', 'admin@huellitas.com', 'Administrador Huellitas', r.id
FROM roles r
WHERE r.nombre = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin');
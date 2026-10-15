-- Roles del sistema Clinica Veterinaria Huellitas
-- ADMIN   -> staff/personal de la veterinaria
-- CLIENTE -> dueño/a de mascota que reserva citas
INSERT INTO roles (nombre) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ADMIN');
INSERT INTO roles (nombre) SELECT 'CLIENTE' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'CLIENTE');

-- Usuario ADMIN inicial (credenciales en documentación interna del equipo)
-- Inicia sesión con su correo: admin@huellitas.com
INSERT INTO usuarios (password, email, nombre_completo, rol_id)
SELECT '$2b$10$2Gc/PIFB9mfU7vMpH0GsjOD6PbrQUhtc6yod9QxBKZ4PCBIHK0PSW', 'admin@huellitas.com', 'Administrador Huellitas', r.id
FROM roles r
WHERE r.nombre = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@huellitas.com');

-- Servicios de ejemplo para poder probar las reservas (puedes editarlos o eliminarlos desde la API)
INSERT INTO servicios (nombre, descripcion, precio, duracion_minutos, activo)
SELECT 'Consulta general', 'Revisión general de la salud de la mascota', 50.00, 30, true
WHERE NOT EXISTS (SELECT 1 FROM servicios WHERE nombre = 'Consulta general');

INSERT INTO servicios (nombre, descripcion, precio, duracion_minutos, activo)
SELECT 'Vacunación', 'Aplicación de vacunas según el calendario de la mascota', 60.00, 20, true
WHERE NOT EXISTS (SELECT 1 FROM servicios WHERE nombre = 'Vacunación');

INSERT INTO servicios (nombre, descripcion, precio, duracion_minutos, activo)
SELECT 'Baño y corte', 'Baño medicado y corte de pelo', 70.00, 60, true
WHERE NOT EXISTS (SELECT 1 FROM servicios WHERE nombre = 'Baño y corte');

INSERT INTO servicios (nombre, descripcion, precio, duracion_minutos, activo)
SELECT 'Desparasitación', 'Tratamiento antiparasitario interno y externo', 40.00, 20, true
WHERE NOT EXISTS (SELECT 1 FROM servicios WHERE nombre = 'Desparasitación');

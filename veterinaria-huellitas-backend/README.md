# Veterinaria Huellitas — Backend (API REST)

Backend del proyecto integrador **Plataforma Web de Gestión de Reservas y Servicios**, adaptado a la
Clínica Veterinaria Huellitas. Hecho con Spring Boot 3.4, Spring Data JPA, Spring Security + JWT y MySQL.

## Cómo ejecutarlo

Requisitos: Java 17 o superior y MySQL 8 encendido en el puerto 3306.

1. Revisa en `src/main/resources/application.properties` el usuario y la contraseña de tu MySQL.
2. Desde esta carpeta ejecuta:
   - Windows: `.\mvnw spring-boot:run`
   - Linux/Mac: `./mvnw spring-boot:run`
3. La API queda en `http://localhost:8085`. La base `huellitasdb` se crea sola y `data.sql` carga
   los roles, el administrador inicial y cuatro servicios de ejemplo.

**Administrador inicial:** correo `admin@huellitas.com` (la contraseña está en la documentación interna del equipo).

> Si ya tenías la base de datos de la versión anterior (con `username`), sigue los pasos de
> `docs/migracion-quitar-username.sql` antes de iniciar el backend.

## Arquitectura por capas

```
controller   Reciben las peticiones HTTP y delegan (no tienen lógica de negocio)
service      Lógica de negocio y reglas (AuthService, ServicioService, ReservaService, UsuarioService)
repository   Acceso a datos con Spring Data JPA (consultas derivadas y JPQL con @Query)
model        Entidades JPA: Usuario, Rol, Servicio, Reserva, EstadoReserva
dto          Objetos de entrada y salida de la API (la contraseña nunca sale en una respuesta)
security     JWT (JwtUtils, JwtAuthFilter), UserDetails y SecurityConfig
exception    ApiException y GlobalExceptionHandler (todos los errores salen como { "mensaje": "..." })
config/util  Reloj con zona America/Lima, horario de atención y utilidad de correos
```

La inyección de dependencias se hace **por constructor** en controllers y services.

## Cambios de esta entrega (parte 2)

1. **Inicio de sesión con correo.** Se eliminó el `username`: el correo es único y es el identificador del login.
2. **Reservas sin validación del administrador.** La reserva nace `CONFIRMADA`; el sistema la valida solo
   (ver reglas abajo). El administrador solo supervisa.
3. CRUD de **servicios**, gestión de **reservas** y gestión de **usuarios** (panel del administrador).

## Reglas de negocio de las reservas

- La reserva se **confirma automáticamente**; no requiere aprobación del administrador.
- El servicio debe existir y estar activo.
- La fecha y hora deben ser futuras (hora de Lima).
- Horario de atención: **lunes a sábado, de 09:00 a 21:00** (domingo cerrado). El servicio completo
  debe terminar antes del cierre (hora de fin = hora de inicio + duración del servicio).
  El horario se cambia en `application.properties` (`huellitas.horario.apertura` / `cierre`).
- No puede cruzarse con otra reserva `CONFIRMADA` del mismo servicio.
- El cliente cancela solo sus reservas y antes de que comiencen; el administrador puede cancelar cualquiera.
- Estados: `CONFIRMADA` → `CANCELADA` o `ATENDIDA`.
- "Eliminar" un servicio lo **desactiva** (baja lógica) para no perder el historial de reservas.

## Endpoints

Todos devuelven JSON. Excepto `/api/auth/**`, requieren el header `Authorization: Bearer <token>`.

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| POST | `/api/auth/registro` | público | Registra un cliente (nombreCompleto, email, password) |
| POST | `/api/auth/login` | público | Login con email y password; devuelve el token JWT |
| GET | `/api/servicios` | ADMIN, CLIENTE | Lista los servicios activos |
| GET | `/api/servicios/{id}` | ADMIN, CLIENTE | Detalle de un servicio |
| GET | `/api/servicios/buscar?nombre=` | ADMIN, CLIENTE | Busca por nombre (JPQL) |
| POST | `/api/servicios` | ADMIN | Crea un servicio |
| PUT | `/api/servicios/{id}` | ADMIN | Edita un servicio |
| DELETE | `/api/servicios/{id}` | ADMIN | Desactiva un servicio |
| POST | `/api/reservas` | CLIENTE | Crea una reserva (queda confirmada) |
| GET | `/api/reservas/mis-reservas` | CLIENTE | Historial del cliente |
| PUT | `/api/reservas/{id}/cancelar` | CLIENTE, ADMIN | Cancela una reserva |
| GET | `/api/admin/reservas?estado=` | ADMIN | Todas las reservas (filtro opcional por estado) |
| PUT | `/api/admin/reservas/{id}/atendida` | ADMIN | Marca una reserva como atendida |
| GET | `/api/admin/usuarios` | ADMIN | Lista los usuarios |
| GET | `/api/admin/usuarios/{id}` | ADMIN | Detalle de un usuario |
| PUT | `/api/admin/usuarios/{id}` | ADMIN | Edita nombre, correo, rol y contraseña (opcionales) |
| DELETE | `/api/admin/usuarios/{id}` | ADMIN | Elimina un usuario sin reservas |

Ejemplo para crear una reserva (`POST /api/reservas`):

```json
{
  "servicioId": 1,
  "fechaHora": "2026-10-20T10:00:00",
  "nombreMascota": "Firulais",
  "observaciones": "Viene con cartilla de vacunas"
}
```

Códigos de error: `400` datos o regla de horario inválidos, `401` sin token o credenciales incorrectas,
`403` sin permisos, `404` no existe, `409` conflicto (correo repetido, horario ocupado).

## Pruebas con Postman

Importa `docs/Huellitas.postman_collection.json`. Ejecuta primero las carpetas en orden
(**1. Autenticación** guarda los tokens solos). La carpeta de reservas calcula una fecha futura válida.

## Consultas JPQL (`@Query`)

- `ServicioRepository.buscarPorNombre`: búsqueda de servicios activos por nombre, sin importar mayúsculas.
- `ReservaRepository.historialPorUsuario`, `listarTodas`, `listarPorEstado`: historial y panel del administrador.
- `ReservaRepository.contarCruces`: detecta si una reserva se cruza con otra del mismo servicio.

## Limitaciones conocidas

- Si dos clientes reservan exactamente al mismo tiempo, podrían pasar ambas validaciones; para producción
  habría que agregar un bloqueo o una restricción en la base de datos.
- Los reportes del panel administrativo y el despliegue en la nube quedan para las siguientes entregas.

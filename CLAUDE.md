# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Siempre responde en español.

## Comandos

### Build y ejecución
```bash
./mvnw clean install          # Compilar y empaquetar
./mvnw spring-boot:run        # Ejecutar la aplicación
./mvnw clean package -DskipTests  # Empaquetar sin tests
```

### Tests
```bash
./mvnw test                   # Ejecutar todos los tests (83 tests)
./mvnw test -Dtest=AuthControllerTest        # Ejecutar una clase de test específica
./mvnw test -Dtest=AuthControllerTest#login_ValidRequest_200  # Ejecutar un test específico
```

### Docker
```bash
docker-compose up --build     # Construir y levantar todos los servicios
docker-compose down           # Detener y eliminar contenedores
```

La app corre en `http://localhost:8080`. Swagger UI: `http://localhost:8080/swagger-ui/index.html`.

## Variables de entorno

El proyecto usa `spring-dotenv` para cargar variables desde un archivo `.env` en la raíz. Las variables requeridas son:

```
JWT_SECRET_KEY=
JWT_EXPIRATION=
JWT_REFRESH_TOKEN_EXPIRATION=
SPRING_SECURITY_USER_NAME=
SPRING_SECURITY_USER_PASSWORD=
MYSQL_ROOT_PASSWORD=
```

Con Docker, `MYSQL_DATABASE` se puede sobrescribir (por defecto `bancodb`). Sin Docker, la BD se conecta en `localhost:3306/bancodb`.

## Arquitectura

### Flujo de capas
`Controller → Service → Repository → Entity`

Los DTOs separan la capa HTTP de las entidades JPA. Hay DTOs de Request (`dto/Request/`) y de Response (`dto/Response/`). El `GlobalExceptionHandler` centraliza toda la gestión de errores, mapeando cada excepción de dominio a su código HTTP correspondiente.

### Modelo de dominio y relaciones clave
- **Cliente** ↔ **Usuario**: relación `OneToOne` bidireccional. `Usuario` tiene la FK `cliente_id`. Un usuario puede existir sin cliente, pero el registro de cliente-usuario los crea a ambos en un solo paso (`/auth/registrar-cliente-usuario`).
- **Cliente** → **Cuentas**: `OneToMany` con `CascadeType.ALL`. Un cliente puede tener múltiples cuentas.
- **Cuenta** usa `@Version` para *optimistic locking* en operaciones concurrentes de saldo.
- **Token**: cada JWT emitido se persiste en la tabla `tokens`. Al hacer login o refresh se revocan todos los tokens anteriores del usuario (`expired=true`, `revoked=true`). El logout también revoca el token activo.

### Seguridad
- `JwtAuthenticationFilter` intercepta cada request antes de `UsernamePasswordAuthenticationFilter`, valida el Bearer Token contra la tabla de tokens (no revocado, no expirado) y setea el `SecurityContext`.
- Los endpoints públicos son: `/api/v1/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`.
- Control de acceso por rol usando `@PreAuthorize` en los controllers con `@EnableMethodSecurity`. Los roles son `ADMIN` y `CLIENTE`.
- El logout está implementado como handler de Spring Security en `/api/v1/auth/logout` (método POST).

### Tests
Los tests son de tipo `@WebMvcTest` + `@MockitoBean` (tests de slice de controller) y tests de servicio con Mockito puro. No hay tests de integración contra base de datos real; todos usan mocks.

### Enums de dominio
- `RolUsuario`: `ADMIN`, `CLIENTE`
- `TipoCuenta`: `AHORRO`, `CORRIENTE`
- `TipoTransaccion`: `DEPOSITO`, `RETIRO`, `TRANSFERENCIA`
- `EstadoTransaccion`: `COMPLETADA`, `PENDIENTE`, `FALLIDA`

### Validaciones de negocio
- El monto mínimo para cualquier operación es `100.00`.
- El DNI y el email tienen excepciones dedicadas (`DniInvalido`, `EmailInvalido`) validadas en `ClienteService`.
- Las transferencias entre la misma cuenta están bloqueadas en `TransaccionService`.

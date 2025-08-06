# 🏦 SpringBank - Motor Bancario Simplificado

**SpringBank** es un sistema backend desarrollado con **Java y Spring Boot** que simula la gestión de una entidad financiera. Está pensado como motor base para una app bancaria real, implementando seguridad robusta, entidades bien definidas y una arquitectura RESTful moderna.

---

## Características Principales

✅ **Autenticación segura con JWT**  
✅ **Protección de endpoints con Spring Security**  
✅ **Arquitectura RESTful**  
✅ **Swagger UI para documentación**  
✅ **Logout funcional que revoca Tokens**  
✅ **Autorización por roles (ADMIN / CLIENTE)**

---

## 🛠️ Tecnologías Utilizadas

| Tecnología       | Descripción                                   |
|------------------|-----------------------------------------------|
| ☕ Java 17        | Lenguaje principal                           |
| 🌱 Spring Boot 3.5.3 | Framework principal para el backend       |
| 🛡️ Spring Security | Protección de endpoints con JWT              |
| 🔄 Maven          | Gestión de dependencias                      |
| 💾 JPA / Hibernate| Acceso de datos relacional ORM               |
| 🗄️ MySQL         | Motor de base de datos utilizada              |
| 📄 Swagger  | Documentación de endpoints                         |
| 🧪 JUnit / Mockito| Testing                                      |
| 🧰 Git / GitHub   | Control de versiones                         |
| 🐳 Docker / Docker Compose | Contenerización y orquestación de servicios |

---
## 🔐 Seguridad con JWT

- Se requiere autenticación con token para consumir todos los endpoints (excepto los públicos de `/auth`).
- Los tokens son generados al hacer login y enviados en el header `Authorization: Bearer <token>`.
- El logout revoca el token, marcándolo como expirado y revocado en la base de datos.

---

## 📚 Documentación de la API

Accedé a la documentación Swagger en:  
`http://localhost:8080/swagger-ui.html`  
o  
`http://localhost:8080/swagger-ui/index.html`

⚠️ Requiere que el backend esté en ejecución.
---
## 📌 Endpoints de la API

### 🛡️ Admin (Solo rol ADMIN)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET    | `/api/v1/admin/usuarios` | Obtener todos los usuarios |
| GET    | `/api/v1/admin/usuarios/username/{username}` | Obtener usuario por su username |
| GET    | `/api/v1/admin/transacciones` | Obtener todas las transacciones |
| GET    | `/api/v1/admin/cuentas` | Obtener todas las cuentas |
| GET    | `/api/v1/admin/cuentas/{numeroCuenta}` | Obtener cuenta por su número de cuenta |
| GET    | `/api/v1/admin/clientes` | Obtener todos los clientes |
| GET    | `/api/v1/admin/clientes/dni/{dni}` | Obtener cliente por DNI |

---

### 💸 Transacciones
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST   | `/api/v1/transacciones/transferir` | Crear una nueva transferencia |
| POST   | `/api/v1/transacciones/retirar` | Realizar un retiro |
| POST   | `/api/v1/transacciones/depositar` | Realizar un depósito |

---

### 🏦 Cuentas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST   | `/api/v1/cuentas` | Crear una cuenta |
| GET    | `/api/v1/cuentas/{numeroCuenta}/transacciones` | Ver transacciones de una cuenta |
| GET    | `/api/v1/cuentas/{numeroCuenta}/saldo` | Ver saldo de una cuenta |

---

### 👤 Clientes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST   | `/api/v1/clientes` | Crear cliente |
| GET    | `/api/v1/clientes/{id}` | Traer cliente por ID |
| GET    | `/api/v1/clientes/all` | Traer todos los clientes |

---

### 🔑 Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST   | `/api/v1/auth/registrar-usuario` | Registrar usuario |
| POST   | `/api/v1/auth/registrar-cliente-usuario` | Registrar cliente con usuario asociado |
| POST   | `/api/v1/auth/refresh` | Refrescar token JWT |
| POST   | `/api/v1/auth/login` | Iniciar sesión y obtener token JWT |

---

### 👥 Usuarios
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET    | `/api/v1/usuarios/{username}` | Traer usuario por username |
| GET    | `/api/v1/usuarios/all` | Traer todos los usuarios |


## 📌 Estado del proyecto

- 🔄 Versión 1.0 con autenticación y autorización completas.
- 🧪 Autenticación funcional y segura.
- 🔒 Autorización por roles.
- ⚙️ Preparado para escalar con nuevas funcionalidades (transferencias, auditorías, notificaciones, etc.).
- 🔧 Código limpio y documentado.
- 🧪 Cuenta con 83 tests unitarios que cubren funcionalidades clave.

---

## 💡 Cómo correr el proyecto

### Opción 1: Con Maven
1. Cloná el repositorio:
   ```bash
   git clone https://github.com/Tomadin/SpringBank.git
   cd SpringBank
   ```
2. Configurá tu base de datos en src/main/resources/application.properties:
   ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/springbank
   
    spring.datasource.username=tu_usuario
   
    spring.datasource.password=tu_password
   ```
3. Ejecutá con Maven:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
---
### Opción 2: Con Docker
1. Cloná el repositorio
2. Ejecutar:
   
```bash
docker-compose up --build

```

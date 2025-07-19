# 🏦 SpringBank - Motor Bancario Simplificado

**SpringBank** es un sistema backend desarrollado con **Java y Spring Boot** que simula la gestión de una entidad financiera. Está pensado como motor base para una app bancaria real, implementando seguridad robusta, entidades bien definidas y una arquitectura RESTful moderna.

---

## Caracteristicas Principales

✅ **Autenticación segura con JWT**  
✅ **Protección de endpoints con Spring Security**  
✅ **Arquitectura RESTful profesional (DTOs, Services, Repositorios, Controladores)**  
✅ **Swagger UI para documentación**  
✅ **Logout funcional que revoca Tokens**  
🔜 **Autorización por roles (ADMIN / CLIENTE)** *(Proxima rama)*

---

## 🛠️ Tecnologias Utilizadas

| Tecnología       | Descripción                                   |
|------------------|-----------------------------------------------|
| ☕ Java 17        | Lenguaje Principal                           |
| 🌱 Spring Boot 3.5.3 | Framework principal para el backend       |
| 🛡️ Spring Security | Protección de endpoints con JWT              |
| 🔄 Maven          | Gestión de dependencias                      |
| 💾 JPA / Hibernate| Acceso de datos relacional ORM               |
| 🗄️ MySQL         | Motor de base de datos utilizada              |
| 📄 Swagger  | Documentación de endpoints                         |
| 🧪 JUnit / Mockito| Testing                                      |
| 🧰 Git / GitHub   | Control de versiones                         |

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

---

## 📌 Estado del proyecto

- 🔄 En desarrollo activo.
- 🧪 Autenticación funcional y segura.
- 🔒 En preparación autorización por roles.
- ⚙️ Preparado para escalar con nuevas funcionalidades (transferencias, auditorías, notificaciones, etc.) (Proximamente).

---

## 💡 Cómo correr el proyecto

1. Cloná el repositorio:
   ```bash
   git clone https://github.com/Tomadin/SpringBank.git
   cd SpringBank
2. Configurá tu base de datos en src/main/resources/application.properties:

    spring.datasource.url=jdbc:mysql://localhost:3306/springbank
   
    spring.datasource.username=tu_usuario
   
    spring.datasource.password=tu_password
   

4. Ejecutá con Maven:
./mvnw clean install
./mvnw spring-boot:run


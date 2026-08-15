# Proyecto AGLO

AGLO es una aplicación web Spring Boot para gestionar actividades, reservaciones, usuarios, guías y los catálogos operativos de la plataforma.

## Tecnología

- Java 17 y Maven.
- Spring Boot, Spring MVC, Spring Security, Spring Data JPA y Validation.
- Thymeleaf, Bootstrap, Font Awesome, jQuery y WebJars.
- MySQL y Firebase Storage.

## Requisitos

- JDK 17.
- MySQL local o accesible desde la aplicación.
- Las credenciales de conexión y Firebase revisadas en `src/main/resources/application.properties`.

El proyecto incluye Maven Wrapper, por lo que no requiere una instalación global de Maven después de clonar el repositorio.

## Base de datos

`sql/DB.sql` es el bootstrap canónico de AGLO y recrea la base de datos. Ejecútelo únicamente contra el ambiente local que desea reinicializar:

```bash
mysql -u root -p < sql/DB.sql
```

Las cuentas de desarrollo del script usan hashes BCrypt compatibles con el inicio de sesión. No sustituya esos hashes por contraseñas en texto plano.

## Ejecución local

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Abra <http://localhost:8080>.

## Seguridad y roles

- Las rutas públicas están declaradas explícitamente en `SecurityConfig`.
- `/admin/**` requiere `ADMIN` y `/guia/**` requiere `GUIA`.
- Las reservaciones y su historial requieren `CLIENTE`; la confirmación permite `CLIENTE` o `ADMIN` y el controlador verifica la propiedad de la reservación.
- Cualquier ruta no clasificada se deniega por defecto.
- `fide_ruta_tb` sigue siendo un catálogo administrativo: editarlo no cambia las reglas de seguridad en tiempo de ejecución.
- Los roles de sistema `ADMIN`, `GUIA` y `CLIENTE` no se pueden renombrar ni eliminar. Los roles adicionales no conceden acceso privilegiado por sí solos.

## Contenedor

La imagen ejecuta `mvn clean verify` durante su construcción y expone el puerto 8080:

```bash
docker build -t aglo .
docker run --rm -p 8080:8080 aglo
```

La imagen necesita acceso a la misma configuración de MySQL y Firebase que el entorno local.

## Credenciales Firebase

La configuración existente de Firebase se mantiene sin cambios para no alterar los entornos actuales. La clave actualmente versionada debe rotarse y externalizarse antes de desplegar fuera de desarrollo; esa migración se gestiona como una tarea separada.

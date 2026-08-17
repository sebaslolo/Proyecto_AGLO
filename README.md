# Proyecto AGLO

AGLO es una aplicación web desarrollada con Spring Boot para gestionar actividades, reservaciones, usuarios, guías, voluntariados y los catálogos operativos de la plataforma.

El sistema implementa autenticación y autorización basada en roles, persistencia de datos en MySQL, almacenamiento de archivos mediante Firebase Storage, envío de correos electrónicos y despliegue en la nube.

## Repositorio y despliegue

- **Repositorio GitHub:** [Proyecto_AGLO](https://github.com/sebaslolo/Proyecto_AGLO)
- **Aplicación desplegada:** [AGLO en Render](https://proyecto-aglo-qxbq.onrender.com/)

## Tecnología

- Java 17 y Maven.
- Spring Boot.
- Spring MVC.
- Spring Security.
- Spring Data JPA.
- Validation.
- Thymeleaf.
- Bootstrap.
- Font Awesome.
- jQuery.
- WebJars.
- MySQL.
- Firebase Storage.
- Aiven Cloud para la base de datos de producción.
- Render para el despliegue de la aplicación.
- Spring Mail y SendGrid SMTP para el envío de correos electrónicos.
- Docker para la construcción y ejecución del proyecto.

## Requisitos

Para ejecutar el proyecto localmente se requiere:

- JDK 17 o superior.
- Maven.
- MySQL local o acceso a una instancia MySQL remota.
- Configuración de las variables de entorno requeridas.
- Credenciales de Firebase disponibles para el proyecto.

## Variables de entorno

La aplicación utiliza variables de entorno para evitar almacenar credenciales sensibles directamente en el código fuente.

### Base de datos

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

### Correo electrónico

- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `MAIL_FROM`

En desarrollo puede utilizarse Gmail SMTP.

En el ambiente desplegado en Render se utiliza SendGrid mediante SMTP.

Las credenciales reales no deben almacenarse directamente en el repositorio.

## Base de datos

`sql/DB.sql` es el script principal de creación y carga de datos de AGLO.

Este archivo permite reconstruir una base de datos local:

```bash
mysql -u root -p < sql/DB.sql
```

Para el ambiente de Aiven se utiliza el archivo:

`sql/DB_AIVEN.sql`

Este script contiene la estructura y los datos necesarios sin intentar crear nuevamente la base de datos administrada por Aiven.

Las cuentas incluidas en los scripts utilizan hashes BCrypt compatibles con Spring Security.

Las contraseñas no se almacenan en texto plano dentro de la base de datos.

## Ejecución local

Con Maven instalado:

```bash
mvn clean verify
mvn spring-boot:run
```

La aplicación estará disponible de forma predeterminada en:

`http://localhost:8080`

El puerto también puede configurarse mediante la variable de entorno `PORT`.

## Despliegue

La aplicación se encuentra desplegada utilizando los siguientes servicios:

- **Aplicación web:** Render.
- **Base de datos:** Aiven MySQL.
- **Correo electrónico:** SendGrid SMTP.
- **Almacenamiento de archivos:** Firebase Storage.

Render construye y ejecuta la aplicación mediante el `Dockerfile` incluido en el repositorio.

## Módulos principales

AGLO incluye, entre otros, los siguientes módulos:

- Autenticación y registro de usuarios.
- Recuperación de contraseña mediante correo electrónico.
- Administración de usuarios.
- Administración de roles.
- Gestión de actividades.
- Catálogo de actividades.
- Reservaciones.
- Historial de reservaciones.
- Administración de guías.
- Agenda de guías.
- Gestión de voluntariados.
- Inscripción de voluntarios.
- Retroalimentación de actividades de voluntariado.
- Gestión de tortugas.
- Gestión de nidos.
- Gestión de nacimientos.
- Gestión de avistamientos.
- Gestión de monitoreos.
- Gestión de herramientas.
- Gestión de préstamos.
- Internacionalización de la interfaz.
- Almacenamiento de archivos mediante Firebase Storage.

## Seguridad y roles

El sistema utiliza Spring Security y BCrypt para la autenticación y protección de contraseñas.

Los principales roles implementados son:

- `ADMIN`
- `GUIA`
- `CLIENTE`

Entre las principales reglas de seguridad se encuentran:

- Las rutas públicas están declaradas explícitamente en `SecurityConfig`.
- `/admin/**` requiere el rol `ADMIN`.
- `/guia/**` requiere el rol `GUIA`.
- Las reservaciones y su historial requieren un usuario con los permisos correspondientes.
- Las reglas de acceso se validan mediante Spring Security y la lógica de los controladores.
- Cualquier ruta no clasificada se deniega por defecto.
- Los roles de sistema `ADMIN`, `GUIA` y `CLIENTE` no se pueden renombrar ni eliminar.
- Los roles adicionales no conceden acceso privilegiado automáticamente.
- Las contraseñas se almacenan mediante BCrypt.

## Usuarios de prueba

Para facilitar la evaluación del sistema se incluyen las siguientes cuentas de demostración.

### CLIENTE

- **Usuario:** `enrique1967`
- **Contraseña:** `k7xfYQuXB8`

Este usuario cuenta con reservaciones y datos de voluntariado previamente preparados para demostrar funcionalidades como historial de reservaciones y retroalimentación.

### GUIA

- **Usuario:** `juan.garcia`
- **Contraseña:** `J7@mP2x!`

Este usuario cuenta con una actividad asignada para demostrar las funcionalidades correspondientes al rol de guía.

### ADMIN

- **Usuario:** `tatiana.molina`
- **Contraseña:** `T6@qX4k$`

Este usuario permite acceder a las funcionalidades administrativas del sistema.

Estas credenciales corresponden únicamente a usuarios de demostración del proyecto académico.

## Correo electrónico

AGLO utiliza Spring Mail mediante `JavaMailSender`.

Entre las funcionalidades que utilizan correo electrónico se encuentran:

- Recuperación de contraseña.
- Notificaciones relacionadas con reservaciones.

Para el despliegue en Render se utiliza SendGrid como servidor SMTP.

Las credenciales SMTP se configuran mediante variables de entorno.

## Contenedor

El proyecto incluye un `Dockerfile` para construir y ejecutar la aplicación.

```bash
docker build -t aglo .
docker run --rm -p 8080:8080 aglo
```

En ambientes de producción el puerto es determinado mediante la variable de entorno `PORT`.

## Firebase Storage

Firebase Storage se utiliza para el almacenamiento de archivos e imágenes requeridos por la aplicación.

La configuración actual de Firebase se mantiene para conservar el funcionamiento de los ambientes desarrollados durante el proyecto.

La credencial actualmente versionada debe rotarse y externalizarse antes de utilizar el sistema en un ambiente productivo real. Esta migración se considera una tarea de seguridad posterior al proyecto académico.

## Diagrama de base de datos

El modelo EER de AGLO fue generado automáticamente mediante MySQL Workbench a partir de la estructura final de la base de datos.

- [Diagrama EER de la base de datos](docs/base-datos/DiagramaBaseDeDatos_ProyectoAGLO.pdf)
- [Modelo editable de MySQL Workbench](docs/base-datos/ModeloBaseDeDatos_ProyectoAGLO.mwb)
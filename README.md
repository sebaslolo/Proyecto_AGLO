# Proyecto AGLO

## Descripcion

Proyecto AGLO es una aplicacion web desarrollada con Spring Boot para la gestion de actividades, reservaciones, usuarios, guias y modulos informativos relacionados con la plataforma AGLO.

El sistema integra vistas publicas para consulta de actividades y reservaciones, asi como un panel administrativo para el mantenimiento de catalogos y datos principales del proyecto.

## Tecnologias principales

- Java 17 como version de compilacion.
- Spring Boot con Spring MVC.
- Thymeleaf para la generacion de vistas.
- Spring Data JPA y Spring Validation.
- MySQL como motor de base de datos.
- Maven como gestor de dependencias y construccion.
- Bootstrap, Font Awesome, jQuery y WebJars para recursos de interfaz.
- Firebase Storage para manejo de archivos e imagenes.

## Requisitos de ejecucion

Antes de ejecutar el proyecto, se requiere contar con:

- JDK 17 o compatible.
- Maven instalado.
- MySQL disponible en ambiente local.
- Base de datos `AGLO` creada a partir del script `sql/DB.sql`.
- Configuracion de conexion revisada en `src/main/resources/application.properties`.
- Archivo JSON de credenciales de Firebase disponible en `src/main/resources/firebase/`, segun la ruta configurada en `application.properties`.

## Ejecucion local

1. Clonar o descargar el repositorio.
2. Ingresar a la carpeta del proyecto:

   ```bash
   cd Proyecto_AGLO
   ```

3. Crear la base de datos ejecutando el script:

   ```bash
   mysql -u root -p < sql/DB.sql
   ```

4. Revisar en `src/main/resources/application.properties` los valores de conexion a MySQL, credenciales, puerto de ejecucion y configuracion de Firebase.
5. Ejecutar la aplicacion:

   ```bash
   mvn spring-boot:run
   ```

6. Abrir la aplicacion en el navegador:

   ```text
   http://localhost:8080
   ```

## Modulos y rutas destacadas

- Inicio, inicio de sesion, registro y recuperacion de contrasena.
- Catalogo de actividades y vista de detalle.
- Reservaciones, confirmacion de reserva y consulta de mis reservaciones.
- Panel administrativo.
- Gestion de usuarios, roles, rutas, actividades, tipos de actividad, guias, asignacion de guias y estados.
- Agenda para guias.
- Vistas informativas para voluntariados, avistamientos, herramientas y retroalimentacion.

## Estado del avance

- El proyecto esta estructurado como una aplicacion Spring Boot MVC con capas de `controller`, `service`, `repository`, `domain` y `templates`.
- La base de datos principal esta definida mediante el script `sql/DB.sql`.
- Existe autenticacion por sesion e interceptor para proteger rutas administrativas, de guia y de reservaciones de usuario.
- El panel administrativo y los CRUDs principales se encuentran implementados.
- El catalogo, las reservaciones y la agenda de guia ya cuentan con controladores y vistas.
- La internacionalizacion esta configurada mediante archivos de mensajes en varios idiomas.
- Actualmente no se detecta una carpeta `src/test`, por lo que las pruebas automatizadas quedan pendientes.
- La documentacion de avance disponible corresponde al archivo `PR-STEM-01-Avance 2_Grupo1.pdf`.


DROP DATABASE IF EXISTS AGLO;

CREATE database AGLO
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

use AGLO;

create table fide_estado_tb (
    id_estado  int not null auto_increment,
    nombre_estado varchar(50),
    fecha_creacion timestamp default CURRENT_TIMESTAMP,
    fecha_modificacion timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    primary key(id_estado),
    unique (nombre_estado),
    index ndx_nombre_estado (nombre_estado )
) ENGINE = InnoDB;

create table fide_usuario_tb (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username varchar(30) NOT NULL UNIQUE,
  password varchar(512) NOT NULL,
  nombre VARCHAR(20) NOT NULL,
  apellido_paterno VARCHAR(30) NOT NULL,
  apellido_materno VARCHAR(30) NULL,
  correo VARCHAR(75) NULL UNIQUE,
  telefono VARCHAR(25) NULL,
  ruta_imagen varchar(1024),
  id_estado int not null,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_usuario`),
  foreign key (id_estado) references fide_estado_tb(id_estado),
  CHECK (correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  index ndx_username (username))
  ENGINE = InnoDB;

create table fide_rol_tb (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol varchar(20) unique,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  primary key (id_rol))
  ENGINE = InnoDB;

create table fide_usuario_rol_tb (
  id_usuario int not null,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario,id_rol),
  foreign key fk_usuarioRol_usuario (id_usuario) references fide_usuario_tb(id_usuario),
  foreign key fk_usuarioRol_rol (id_rol) references fide_rol_tb(id_rol))
  ENGINE = InnoDB;

CREATE TABLE fide_ruta_tb (
    id_ruta INT AUTO_INCREMENT NOT NULL,
    ruta VARCHAR(255) NOT NULL,
    id_rol INT NULL,
    requiere_rol boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    check (id_rol IS NOT NULL OR requiere_rol = FALSE),
    PRIMARY KEY (id_ruta),
    FOREIGN KEY (id_rol) REFERENCES fide_rol_tb(id_rol))
    ENGINE = InnoDB;

create table fide_reservacion_tb(
    id_reservacion  int not null auto_increment,
    id_usuario  int not null,
    id_estado  int not null,
    fecha_reservacion datetime,
    monto_total decimal(10,2),
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key(id_reservacion),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_fecha_reservacion (fecha_reservacion))
    ENGINE = InnoDB;


create table fide_tipo_actividad_tb(
    id_tipo_actividad int not null auto_increment,
    nombre_tipo_actividad varchar(100),
    descripcion_tipo_actividad varchar(255),
    precio_base decimal(10,2),
    duracion_estimada varchar(50),
    imagen_tipo_actividad varchar(500),
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key(id_tipo_actividad),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    unique (nombre_tipo_actividad),
    index ndx_nombre_tipo_actividad (nombre_tipo_actividad)
)   ENGINE = InnoDB;

create table fide_actividad_tb(
    id_actividad int not null auto_increment,
    id_tipo_actividad int not null,
    nombre_actividad varchar(100),
    fecha_hora_inicio datetime,
    fecha_hora_fin datetime,
    cupo_maximo int,
    precio_actual decimal(10,2),
    imagen_actividad varchar(500),
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key(id_actividad),
    foreign key (id_tipo_actividad) references fide_tipo_actividad_tb(id_tipo_actividad),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_nombre_actividad (nombre_actividad)
)   ENGINE = InnoDB;

create table fide_actividad_detalle_tb(
    id_reservacion int not null,
    id_actividad int not null,
    cantidad_personas int,
    precio_unitario decimal(10,2),
    subtotal decimal(10,2),
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key(id_reservacion, id_actividad),
    foreign key (id_reservacion) references fide_reservacion_tb(id_reservacion),
    foreign key (id_actividad) references fide_actividad_tb(id_actividad),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_id_actividad (id_actividad),
    index ndx_id_reservacion (id_reservacion)
) ENGINE = InnoDB;

create table fide_guia_tb(
    id_guia int not null auto_increment,
    id_usuario int not null,
    fecha_ingreso date,
    disponibilidad boolean,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key(id_guia),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    unique (id_usuario),
    index ndx_id_usuario (id_usuario)
)   ENGINE = InnoDB;

create table fide_actividad_guia_tb(
    id_actividad int not null,
    id_guia int not null,
    fecha_asignacion datetime,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key(id_actividad, id_guia),
    foreign key (id_actividad) references fide_actividad_tb(id_actividad),
    foreign key (id_guia) references fide_guia_tb(id_guia),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_id_actividad (id_actividad),
    index ndx_id_guia (id_guia)
) ENGINE = InnoDB;

create table fide_voluntario_tb(
    id_voluntario int not null auto_increment,
    id_usuario int not null,
    fecha_ingreso date not null,
    disponibilidad varchar(100),
    horas_acumuladas decimal(10,2) not null default 0,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_voluntario),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    unique (id_usuario),
    index ndx_voluntario_estado (id_estado)
) ENGINE = InnoDB;

create table fide_inscripcion_voluntariado_tb(
    id_inscripcion_voluntariado int not null auto_increment,
    id_voluntario int not null,
    id_actividad int not null,
    fecha_inscripcion datetime default current_timestamp,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_inscripcion_voluntariado),
    foreign key (id_voluntario) references fide_voluntario_tb(id_voluntario),
    foreign key (id_actividad) references fide_actividad_tb(id_actividad),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    unique (id_voluntario, id_actividad),
    index ndx_inscripcion_voluntariado_actividad (id_actividad),
    index ndx_inscripcion_voluntariado_estado (id_estado)
) ENGINE = InnoDB;

create table fide_asistencia_voluntariado_tb(
    id_asistencia_voluntariado int not null auto_increment,
    id_inscripcion_voluntariado int not null,
    asistencia boolean not null default false,
    hora_entrada time null,
    hora_salida time null,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_asistencia_voluntariado),
    foreign key (id_inscripcion_voluntariado) references fide_inscripcion_voluntariado_tb(id_inscripcion_voluntariado),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    unique (id_inscripcion_voluntariado),
    check (hora_salida is null or hora_entrada is null or hora_salida >= hora_entrada),
    index ndx_asistencia_voluntariado_estado (id_estado)
) ENGINE = InnoDB;

create table fide_horas_voluntariado_tb(
    id_horas_voluntariado int not null auto_increment,
    id_voluntario int not null,
    id_actividad int not null,
    fecha date not null,
    cantidad_horas decimal(5,2) not null,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_horas_voluntariado),
    foreign key (id_voluntario) references fide_voluntario_tb(id_voluntario),
    foreign key (id_actividad) references fide_actividad_tb(id_actividad),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    unique (id_voluntario, id_actividad, fecha),
    check (cantidad_horas > 0),
    index ndx_horas_voluntariado_actividad (id_actividad),
    index ndx_horas_voluntariado_estado (id_estado)
) ENGINE = InnoDB;

-- Inscripciones usadas por la entidad Voluntariado vigente. Se mantiene
-- separada de las tablas históricas de perfiles y asistencias de voluntarios.
create table fide_voluntariado_tb(
    id_voluntariado int not null auto_increment,
    id_usuario int not null,
    id_actividad int not null,
    herramientas_utilizadas varchar(500),
    fecha_inscripcion datetime not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_voluntariado),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_actividad) references fide_actividad_tb(id_actividad),
    unique (id_usuario, id_actividad),
    index ndx_voluntariado_actividad (id_actividad)
) ENGINE = InnoDB;

create table fide_monitoreo_tb(
    id_monitoreo int not null auto_increment,
    id_guia int not null,
    fecha_monitoreo datetime,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_monitoreo),
    foreign key (id_guia) references fide_guia_tb(id_guia),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_id_guia (id_guia),
    index ndx_fecha_monitoreo (fecha_monitoreo)
)   ENGINE = InnoDB;

create table fide_tortuga_tb(
    etiqueta_tortuga varchar(50) primary key,
    especie varchar(100) not null,
    sexo varchar(10) not null,
    fecha_registro datetime,
    observaciones varchar(255),
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_etiqueta_tortuga (etiqueta_tortuga),
    index ndx_especie (especie)
)ENGINE = InnoDB;

create table fide_avistamiento_tb(
    id_avistamiento int primary key auto_increment,
    etiqueta_tortuga varchar(50) not null,
    comportamiento varchar(100) not null,
    ubicacion varchar(100) not null,
    fecha_avistamiento datetime,
    observaciones varchar(255),
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    foreign key (etiqueta_tortuga) references fide_tortuga_tb(etiqueta_tortuga),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_etiqueta_tortuga (etiqueta_tortuga),
    index ndx_id_estado (id_estado)
)ENGINE = InnoDB;

create table fide_nido_tb(
    id_nido int primary key auto_increment,
    etiqueta_tortuga varchar(50) not null,
    ubicacion varchar(100),
    fecha_anidacion datetime,
    cantidad_huevos int,
    profundidad_nido int,
    observaciones varchar(255),
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    foreign key (etiqueta_tortuga) references fide_tortuga_tb(etiqueta_tortuga),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_etiqueta_tortuga (etiqueta_tortuga),
    index ndx_id_estado (id_estado) 
)ENGINE = InnoDB;

create table fide_nacimiento_tb(
    id_nacimiento int primary key auto_increment,
    id_nido int not null,
    fecha_eclosion datetime,
    crias_vivas int,
    crias_muertas int,
    crias_infertiles int,
    observaciones varchar(255),
    id_estado int not null,
    foreign key (id_nido) references fide_nido_tb(id_nido),
    foreign key (id_estado) references fide_estado_tb(id_estado)
)ENGINE = InnoDB;


create table fide_herramientas_tb(
    id_herramienta int primary key auto_increment,
    nombre_herramienta varchar(100),
    descripcion varchar(255),
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_nombre_herramienta (nombre_herramienta),
    index ndx_id_estado (id_estado)
)ENGINE = InnoDB;

create table fide_prestamo_tb(
    id_prestamo int primary key auto_increment,
    id_herramienta int not null,
    id_usuario int not null,
    fecha_prestamo date,
    fecha_devolucion date,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    foreign key (id_herramienta) references fide_herramientas_tb(id_herramienta),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_id_herramienta (id_herramienta),
    index ndx_id_usuario (id_usuario),
    index ndx_id_estado (id_estado)
)ENGINE = InnoDB;


create table fide_tipo_solicitud_tb(
    id_tipo_solicitud int not null auto_increment,
    nombre varchar(100) not null,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_tipo_solicitud),
    unique (nombre),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_tipo_solicitud_estado (id_estado)
) ENGINE = InnoDB;

create table fide_tipo_respuesta_tb(
    id_tipo_respuesta int not null auto_increment,
    nombre varchar(100) not null,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_tipo_respuesta),
    unique (nombre),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_tipo_respuesta_estado (id_estado)
) ENGINE = InnoDB;

create table fide_pregunta_tb(
    id_pregunta int not null auto_increment,
    pregunta varchar(500) not null,
    id_tipo_respuesta int not null,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_pregunta),
    foreign key (id_tipo_respuesta) references fide_tipo_respuesta_tb(id_tipo_respuesta),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_pregunta_tipo_respuesta (id_tipo_respuesta),
    index ndx_pregunta_estado (id_estado)
) ENGINE = InnoDB;

create table fide_solicitud_tb(
    id_solicitud int not null auto_increment,
    id_usuario int not null,
    id_actividad int not null,
    id_tipo_solicitud int not null,
    id_estado int not null,
    fecha_solicitud datetime default current_timestamp,
    fecha_envio datetime null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_solicitud),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_actividad) references fide_actividad_tb(id_actividad),
    foreign key (id_tipo_solicitud) references fide_tipo_solicitud_tb(id_tipo_solicitud),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    check (fecha_envio is null or fecha_envio >= fecha_solicitud),
    index ndx_solicitud_usuario (id_usuario),
    index ndx_solicitud_actividad (id_actividad),
    index ndx_solicitud_tipo (id_tipo_solicitud),
    index ndx_solicitud_estado (id_estado)
) ENGINE = InnoDB;

create table fide_respuesta_tb(
    id_respuesta int not null auto_increment,
    id_solicitud int not null,
    id_pregunta int not null,
    respuesta text not null,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_respuesta),
    unique (id_solicitud, id_pregunta),
    foreign key (id_solicitud) references fide_solicitud_tb(id_solicitud),
    foreign key (id_pregunta) references fide_pregunta_tb(id_pregunta),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_respuesta_pregunta (id_pregunta),
    index ndx_respuesta_estado (id_estado)
) ENGINE = InnoDB;


INSERT INTO fide_estado_tb (nombre_estado) VALUES
('Activo'),
('Inactivo'),
('Pendiente'),
('Confirmada'),
('Cancelada'),
('Completado'),
('Disponible'),
('En Uso'),
('Agotado');

INSERT INTO fide_tipo_solicitud_tb (nombre, id_estado) VALUES
('Retroalimentacion', 1);

INSERT INTO fide_tipo_respuesta_tb (nombre, id_estado) VALUES
('Calificacion de 1 a 5', 1),
('Comentario libre', 1);

INSERT INTO fide_pregunta_tb (pregunta, id_tipo_respuesta, id_estado) VALUES
('Califique su experiencia en el voluntariado del 1 al 5.', 1, 1),
('Comparta sus comentarios sobre la experiencia.', 2, 1);

INSERT INTO fide_rol_tb (rol) VALUES
('ADMIN'),
('GUIA'),
('CLIENTE');

INSERT INTO fide_ruta_tb (ruta, id_rol, requiere_rol) VALUES
('/admin/**', 1, TRUE),
('/guia/**', 2, TRUE),
('/reservaciones/nueva', 3, TRUE),
('/reservaciones/guardar', 3, TRUE),
('/reservaciones/confirmacion/**', 3, TRUE),
('/mis-reservaciones', 3, TRUE),
('/mis-reservaciones/**', 3, TRUE),
('/', NULL, FALSE),
('/inicio', NULL, FALSE),
('/login', NULL, FALSE),
('/registro/**', NULL, FALSE),
('/forgot-password', NULL, FALSE),
('/catalogo/**', NULL, FALSE),
('/avistamientos/**', NULL, FALSE),
('/herramientas/**', NULL, FALSE),
('/retroalimentacion/**', NULL, FALSE),
('/voluntariados/**', NULL, FALSE),
('/css/**', NULL, FALSE),
('/js/**', NULL, FALSE),
('/img/**', NULL, FALSE),
('/webjars/**', NULL, FALSE),
('/logout', NULL, FALSE);

INSERT INTO fide_usuario_tb
(username, password, nombre, apellido_paterno, apellido_materno, correo, telefono, id_estado)
VALUES
('juan.garcia',      '$2a$10$j2AUm7e/emJJEIcZWnfUnejf9BLwJ8s4g23uateiO9SaZJURLdNUq', 'Juan',      'Garcia',      'Lopez',      'juan.garcia@gmail.com',      '612345678', 1),
('maria.martinez',   '$2a$10$Ex/LQrSMKp3aW7mgiHpN.e9O14iAnIDe3wLw3YgM4Ud1v2JFO3K82', 'Maria',     'Martinez',    'Gomez',      'maria.martinez@gmail.com',   '622345679', 2),
('carlos.lopez',     '$2a$10$E4tF5VBca2M9oGb1H.dJdenZHe9SLDBJXpgyNTGOEbGUsjLlFTs8O', 'Carlos',    'Lopez',       'Perez',      'carlos.lopez@gmail.com',     '632345680', 1),
('ana.rodriguez',    '$2a$10$h5071Pmv7uYf5xtobEoVTeQAqLZkajdAM7errNtPk.y3xoA2FIP5e', 'Ana',       'Rodriguez',   'Fernandez',  'ana.rodriguez@gmail.com',    '642345681', 2),
('luis.fernandez',   '$2a$10$HMSAsoG8VNZkC8QqYvkK..PKNLah1B0dduOYW24DA8C001FKWQE6m', 'Luis',      'Fernandez',   'Gonzalez',   'luis.fernandez@gmail.com',   '652345682', 1),
('elena.gonzalez',   '$2a$10$Khp8MWm8xnf9qxsUVhWDoOVRiI8ZOo5o3TtN8P4xfSR4QmePKZAjm', 'Elena',     'Gonzalez',    'Sanchez',    'elena.gonzalez@gmail.com',   '662345683', 2),
('pedro.perez',      '$2a$10$YUH6dzGmV4jTDJ95m6KQQuoBCVTOC6MyPkjHeIGKw2UlEB7u2CtEe', 'Pedro',     'Perez',       'Ruiz',       'pedro.perez@gmail.com',      '672345684', 1),
('sofia.sanchez',    '$2a$10$r3qxVt6UTKbU89zcmslXAelmu7rza4rBV9smZSV3CAfs07ywld9La', 'Sofia',     'Sanchez',     'Ramirez',    'sofia.sanchez@gmail.com',    '682345685', 2),
('miguel.ruiz',      '$2a$10$jw9lttS3jtP/kc2Npsyw6O1VV1pc0k7A9wbp.RxYpavOMRSzvK3SO', 'Miguel',    'Ruiz',        'Torres',     'miguel.ruiz@gmail.com',      '692345686', 1),
('isabel.ramirez',   '$2a$10$PnxM6nBt14TFGvly6fB7J.3cMUbDCOCcj444ogg4r6OzGsopWc/bm', 'Isabel',    'Ramirez',     'Flores',     'isabel.ramirez@gmail.com',   '702345687', 2),
('jose.torres',      '$2a$10$PkXz4F8m8nM/8pPemcREz.wExXuzJ2y8NG3Wm2V0XIp8.gy2wos82', 'Jose',      'Torres',      'Rivera',     'jose.torres@gmail.com',      '712345688', 1),
('lucia.flores',     '$2a$10$LeaiYgELOhGFeo7pwqYPAuWx2hRJ8stp0EcVb97VjorWfR6B5gaqa', 'Lucia',     'Flores',      'Morales',    'lucia.flores@gmail.com',     '722345689', 2),
('antonio.rivera',   '$2a$10$i73B/WmmCVQPRlJHge0xYOcT.WICXObcdoNX9sO4ug3.ERGNPXdlu', 'Antonio',   'Rivera',      'Ortiz',      'antonio.rivera@gmail.com',   '732345690', 1),
('david.solis',      '$2a$10$r/y7q0X4iFj9/iX21hHU1.x9euPuHWr8bknEz0j8fyXlhvF40Q7fW', 'David',     'Solis',       'Castro',     'david.solis@gmail.com',      '742345691', 2),
('nicole.vargas',    '$2a$10$4lY8Le2jocxjgjhqPe/2vuYs/y2wat7xj6we6Z5DGhYW42h0nJ5se', 'Nicole',    'Vargas',      'Jimenez',    'nicole.vargas@gmail.com',    '752345692', 1),
('bianca.solano',    '$2a$10$3Vj1bPAkYbok5KprHQ1HO.pS4YQsKPKfsTH0JXemcYBnZERt2aufe', 'Bianca',    'Solano',      'Mora',       'bianca.solano@gmail.com',    '762345693', 2),
('sebastian.castro', '$2a$10$ahu/ffkGfAj/LB18OUHfHuFVMuv76/JMPfFXuVQ046inJYyBqTeku', 'Sebastian', 'Castro',      'Rojas',      'sebastian.castro@gmail.com', '772345694', 1),
('andres.mora',      '$2a$10$6j8F/W6L0eRx8WFppRKK0O3uT7ngooUj6disFhhvslbXRJ8opyXZ2', 'Andres',    'Mora',        'Vega',       'andres.mora@gmail.com',      '782345695', 2),
('paula.vega',       '$2a$10$UpJYv/tsQRYhfMjH9TwSleReOohwUqToQGG4VN33NSjZaZzC0mFGq', 'Paula',     'Vega',        'Salas',      'paula.vega@gmail.com',       '792345696', 1),
('kevin.salas',      '$2a$10$VTG7.0ORTTzUVobXIllUgewYVnwXRfI7iyQQEO02u3buLbRB0943e', 'Kevin',     'Salas',       'Leon',       'kevin.salas@gmail.com',      '802345697', 2),
('laura.leon',       '$2a$10$UXKhxy2BTE87FuIGKXpiye5wO1FAfI2E4whP9PB1zJ/XkPwqorhJ6', 'Laura',     'Leon',        'Acosta',     'laura.leon@gmail.com',       '812345698', 1),
('jorge.acosta',     '$2a$10$P7Bm9RweANC.EjzgBrwGk.YVYo4vPTMhw/6dhe9gNXrY2wseQBfim', 'Jorge',     'Acosta',      'Herrera',    'jorge.acosta@gmail.com',     '822345699', 2),
('karla.herrera',    '$2a$10$/dbYumJASSwudlDx150iheEsTP.kFXEpKzqBtLRBnoiwPCrD0YVXW', 'Karla',     'Herrera',     'Campos',     'karla.herrera@gmail.com',    '832345700', 1),
('diego.campos',     '$2a$10$LSIsNx9QwLIYFRmRc0nW0eVS3Cb0QI0ityWB1QD7vcswXJ.KXtxKO', 'Diego',     'Campos',      'Navarro',    'diego.campos@gmail.com',     '842345701', 2),
('adriana.navarro',  '$2a$10$wKnczs12ymaxkMF3TPGE6ucCP5hs2oNYqiD0N/Cb3ZLQxtTjtePKi', 'Adriana',   'Navarro',     'Mendez',     'adriana.navarro@gmail.com',  '852345702', 1),
('roberto.mendez',   '$2a$10$JH.9/MAnkmGbiT54Eb2BVudsSATPAaehF.HLGwJYKC4HbPzZS2dOi', 'Roberto',   'Mendez',      'Aguilar',    'roberto.mendez@gmail.com',   '862345703', 2),
('daniela.aguilar',  '$2a$10$To2n0vN25Lgtd9QvQls6vuQkVUbDzoL.Mun2k87ewUooP6nGLtoF6', 'Daniela',   'Aguilar',     'Cordero',    'daniela.aguilar@gmail.com',  '872345704', 1),
('ricardo.cordero',  '$2a$10$evYg2YkIOF2ZFvM2osWqNe6OXUXdHCCh0U9/ffMlQC1qhg05xvi6S', 'Ricardo',   'Cordero',     'Araya',      'ricardo.cordero@gmail.com',  '882345705', 2),
('gabriela.araya',   '$2a$10$Q40T/8AxQsG4Y28cCO9UfuFIXgETIt7wZvS7eABhQTjH/bdYbnGA.', 'Gabriela',  'Araya',       'Monge',      'gabriela.araya@gmail.com',   '892345706', 1),
('oscar.monge',      '$2a$10$fYYZ/NbFmRkCz07fkZnNLOm6r/p9NZeOVBs3V0mdBpHu3AVzC8mIG', 'Oscar',     'Monge',       'Quesada',    'oscar.monge@gmail.com',      '902345707', 2),
('valeria.quesada',  '$2a$10$Z9cborwec4tGIIucjbJIbefOX.20qZi76SsrZzLPJbDOeHP15Y.Oi', 'Valeria',   'Quesada',     'Alfaro',     'valeria.quesada@gmail.com',  '912345708', 1),
('fernando.alfaro',  '$2a$10$iqPpBoO6akKJtJbEnig2lOGho0cHiyFPtd53.bzf0myfQb.v93Roa', 'Fernando',  'Alfaro',      'Esquivel',   'fernando.alfaro@gmail.com',  '922345709', 2),
('camila.esquivel',  '$2a$10$riNIzEtMMNU2LbkcB5XPt.qo0ZR9bhvrlmhQQaPd3A3azaKuF645W', 'Camila',    'Esquivel',    'Blanco',     'camila.esquivel@gmail.com',  '932345710', 1),
('eduardo.blanco',   '$2a$10$Jn7BF4ixueb6qwvEqD0bP.BcAkQibNO53fPfiTf87VdLiLJKzMiKK', 'Eduardo',   'Blanco',      'Chaves',     'eduardo.blanco@gmail.com',   '942345711', 2),
('patricia.chaves',  '$2a$10$b6/KLzcXe97JhckTyX6J4uWTRwm/NAGggjGfWeHYwRjpwTg8D7fbG', 'Patricia',  'Chaves',      'Nunez',      'patricia.chaves@gmail.com',  '952345712', 1),
('sergio.nunez',     '$2a$10$g8M0Pe3pvMJhLanguPNUUOFJQqA9qS6f/7ugE5Kcp2yXJw3GC.fN.', 'Sergio',    'Nunez',       'Porras',     'sergio.nunez@gmail.com',     '962345713', 2),
('monica.porras',    '$2a$10$i0QR4un3yi7nVZAVbnJJg./kCHol9c1tyDk6AlNRAbcdAYynjdPmW', 'Monica',    'Porras',      'Soto',       'monica.porras@gmail.com',    '972345714', 1),
('cristian.soto',    '$2a$10$iKI0XKfbdZgK.Gw4/Vow8eOsSPKndCLl8ZQlFWe5xo2bwviXfiqRm', 'Cristian',  'Soto',        'Valverde',   'cristian.soto@gmail.com',    '982345715', 2),
('alicia.valverde',  '$2a$10$yWGdJODiRU/WY3H7kPs2Kuok8cCQmHGAa2IvOTaeXuYw0S3ptokBq', 'Alicia',    'Valverde',    'Rojas',      'alicia.valverde@gmail.com',  '992345716', 1),
('esteban.rojas',    '$2a$10$M9bfU8wCkZl2cWAssbUSL.PRxAFl218l68E8fxeVV5jQmu00280KS', 'Esteban',   'Rojas',       'Jimenez',    'esteban.rojas@gmail.com',    '602345717', 2),
('veronica.jimenez', '$2a$10$i7Pi4xnx5mCcpTedpgs8YeXCUW4d5f.baYriVpA2ArhYyusJhwZ96', 'Veronica',  'Jimenez',     'Solis',      'veronica.jimenez@gmail.com', '612345718', 1),
('adrian.arias',     '$2a$10$g6szeJqs71e067Zr.8uSNueKhhkzNv9AaNqEYouJy4k5eRqOiWdja', 'Adrian',    'Arias',       'Vargas',     'adrian.arias@gmail.com',     '622345719', 2),
('melissa.diaz',     '$2a$10$Fkwi9Jft7rw3EhZ.QH/xXOggUKPF.0moOSVq0nltv2krxLUDTeK8G', 'Melissa',   'Diaz',        'Rojas',      'melissa.diaz@gmail.com',     '632345720', 1),
('hector.ortega',    '$2a$10$WSQrPtN8mRlf3.Ac7QtxTOvIleetQ9COMvy4qLb5.SiuNDsgNJA.W', 'Hector',    'Ortega',      'Lopez',      'hector.ortega@gmail.com',    '642345721', 2),
('ximena.marin',     '$2a$10$8wBJ.ENnCTFVSZjFacrgIO86BMPk6zzAAWdgoDCxAW5yn6nX9VPTO', 'Ximena',    'Marin',       'Campos',     'ximena.marin@gmail.com',     '652345722', 1),
('fabian.guerrero',  '$2a$10$kXXWbRyA.AJhDFqHMHtq2urhtCm4e0Lv4byEI64cJ8N3fiymAA07i', 'Fabian',    'Guerrero',    'Soto',       'fabian.guerrero@gmail.com',  '662345723', 2),
('carolina.reyes',   '$2a$10$bjZ0JoHoR70TaxJkaA3E.uJ39IP2JUVa.ZZs78PE5x..L58QKapg2', 'Carolina',  'Reyes',       'Perez',      'carolina.reyes@gmail.com',   '672345724', 1),
('manuel.espinoza',  '$2a$10$zbvmkkoP8l4XWhQF2tQbjOE/cpKbaEFK33LL.kC9gsvn.mkF7iZf6', 'Manuel',    'Espinoza',    'Vega',       'manuel.espinoza@gmail.com',  '682345725', 2),
('tatiana.molina',   '$2a$10$QMpzr1Sd3979R0ycB5SlXecQn5MWG20CUy7lUnxNOzKtsDYIQ1W3m', 'Tatiana',   'Molina',      'Castillo',   'tatiana.molina@gmail.com',   '692345726', 1);

INSERT INTO fide_usuario_rol_tb (id_usuario, id_rol) VALUES
(1, 2),
(2, 2),
(3, 2),
(4, 2),
(5, 2),
(6, 2),
(7, 2),
(8, 2),
(9, 2),
(10, 2),
(11, 2),
(12, 2),
(13, 3),
(14, 3),
(15, 3),
(16, 3),
(17, 3),
(18, 3),
(19, 3),
(20, 3),
(21, 3),
(22, 3),
(23, 3),
(24, 3),
(25, 3),
(26, 3),
(27, 3),
(28, 3),
(29, 3),
(30, 3),
(31, 3),
(32, 3),
(33, 3),
(34, 3),
(35, 3),
(36, 3),
(37, 3),
(38, 3),
(39, 3),
(40, 3),
(41, 3),
(42, 3),
(43, 3),
(44, 3),
(45, 3),
(46, 3),
(47, 3),
(48, 3),
(49, 1);

INSERT INTO fide_tipo_actividad_tb
(nombre_tipo_actividad, descripcion_tipo_actividad, precio_base, duracion_estimada, imagen_tipo_actividad, id_estado)
VALUES
('Liberacion de tortugas', 'Acompanamiento guiado durante liberaciones controladas en Playa Ostional.', 12000.00, '2 horas', 'https://vivemar.com.mx/wp-content/uploads/2022/01/Vivemar_Liberacion-%E2%80%93-monitoreo-nocturno-new-3.jpg', 1),
('Senderismo costero', 'Recorrido interpretativo por senderos y miradores cercanos a la comunidad.', 18000.00, '3 horas', 'https://wpapi.larepublica.net/wp-content/uploads/2018/09/20180926141859.trekking.jpg', 1),
('Avistamiento nocturno', 'Observacion responsable de fauna y actividad de anidacion con guia local.', 20000.00, '2.5 horas', 'https://www.ucr.ac.cr/medios/fotos/2021/nin%CC%83os-y-tortugas-lora6125434704b16.jpeg', 1),
('Voluntariado ambiental', 'Jornada comunitaria de limpieza, educacion ambiental y conservacion.', 5000.00, '4 horas', 'https://juventud.gob.do/wp-content/uploads/2023/03/Playita-con-bolsa-scaled.jpg', 1);

INSERT INTO fide_actividad_tb
(id_tipo_actividad, nombre_actividad, fecha_hora_inicio, fecha_hora_fin, cupo_maximo, precio_actual, imagen_actividad, id_estado)
VALUES
(1, 'Liberacion familiar de tortugas', '2026-08-15 16:30:00', '2026-08-15 18:30:00', 24, 12000.00, 'https://vivemar.com.mx/wp-content/uploads/2022/01/Vivemar_Liberacion-%E2%80%93-monitoreo-nocturno-new-3.jpg', 1),
(2, 'Sendero Mirador Ostional', '2026-08-18 07:00:00', '2026-08-18 10:00:00', 18, 18000.00, 'https://wpapi.larepublica.net/wp-content/uploads/2018/09/20180926141859.trekking.jpg', 1),
(3, 'Tour nocturno de anidacion', '2026-08-21 19:00:00', '2026-08-21 21:30:00', 16, 20000.00, 'https://www.ucr.ac.cr/medios/fotos/2021/nin%CC%83os-y-tortugas-lora6125434704b16.jpeg', 1),
(4, 'Limpieza comunitaria de playa', '2026-08-24 08:00:00', '2026-08-24 12:00:00', 30, 5000.00, 'https://juventud.gob.do/wp-content/uploads/2023/03/Playita-con-bolsa-scaled.jpg', 1),
(1, 'Charla y liberacion al atardecer', '2026-08-28 16:00:00', '2026-08-28 18:00:00', 20, 14000.00, 'https://d16ice5q223i7e.cloudfront.net/wp-content/uploads/2026/03/24034625/2021-08-Simbiosis-Ostional-InfoFotos-mqcphoto-019.jpg', 1);

INSERT INTO fide_guia_tb (id_usuario, fecha_ingreso, disponibilidad, id_estado) VALUES
(1, '2023-01-15', TRUE, 1),
(2, '2023-02-20', FALSE, 1),
(3, '2023-03-05', TRUE, 1),
(4, '2023-04-10', TRUE, 1),
(5, '2023-05-25', FALSE, 1),
(6, '2023-06-30', TRUE, 1),
(7, '2023-07-15', FALSE, 1),
(8, '2023-08-20', TRUE, 1),
(9, '2023-09-05', TRUE, 1),
(10, '2023-10-10', FALSE, 1),
(11, '2023-11-15', TRUE, 1),
(12, '2023-12-20', FALSE, 1);

INSERT INTO fide_actividad_guia_tb (id_actividad, id_guia, fecha_asignacion, id_estado) VALUES
(1, 1, '2026-07-01 08:00:00', 1),
(1, 2, '2026-07-01 08:00:00', 1),
(2, 3, '2026-07-01 08:00:00', 1),
(3, 4, '2026-07-01 08:00:00', 1),
(4, 5, '2026-07-01 08:00:00', 1),
(5, 6, '2026-07-01 08:00:00', 1);

-- 4) fide_monitoreo_tb
INSERT INTO fide_monitoreo_tb (id_monitoreo, id_guia, fecha_monitoreo, id_estado) VALUES
(1, 1, '2026-07-01 18:00:00', 6),
(2, 2, '2026-07-02 19:00:00', 6),
(3, 3, '2026-07-03 20:00:00', 6),
(4, 4, '2026-07-04 21:00:00', 6),
(5, 5, '2026-07-05 22:00:00', 6),
(6, 6, '2026-07-06 18:00:00', 6),
(7, 7, '2026-07-07 19:00:00', 6),
(8, 8, '2026-07-08 20:00:00', 6),
(9, 9, '2026-07-09 21:00:00', 6),
(10, 10, '2026-07-10 22:00:00', 6),
(11, 11, '2026-07-11 18:00:00', 6),
(12, 12, '2026-07-12 19:00:00', 6),
(13, 1, '2026-07-13 20:00:00', 6),
(14, 2, '2026-07-14 21:00:00', 6),
(15, 3, '2026-07-15 22:00:00', 6),
(16, 4, '2026-07-16 18:00:00', 1),
(17, 5, '2026-07-17 19:00:00', 1),
(18, 6, '2026-07-18 20:00:00', 1),
(19, 7, '2026-07-19 21:00:00', 1),
(20, 8, '2026-07-20 22:00:00', 1),
(21, 9, '2026-07-21 18:00:00', 1),
(22, 10, '2026-07-22 19:00:00', 1),
(23, 11, '2026-07-23 20:00:00', 1),
(24, 12, '2026-07-24 21:00:00', 1),
(25, 1, '2026-07-25 22:00:00', 1);

-- 5) fide_tortuga_tb
INSERT INTO fide_tortuga_tb (etiqueta_tortuga, especie, sexo, fecha_registro, observaciones, id_estado) VALUES
('OST-001', 'Lora', 'Hembra', '2025-01-04 20:07:00', 'Buen estado general', 1),
('OST-002', 'Lora', 'Hembra', '2025-01-07 21:14:00', 'Sin lesiones visibles', 1),
('OST-003', 'Verde', 'Hembra', '2025-02-10 22:21:00', 'Marca natural identificable en el caparazon', 1),
('OST-004', 'Lora', 'Hembra', '2025-02-13 23:28:00', 'Etiqueta revisada durante monitoreo', 1),
('OST-005', 'Carey', 'Hembra', '2025-03-16 19:35:00', 'Registro fotografico disponible', 1),
('OST-006', 'Lora', 'Hembra', '2025-03-19 20:42:00', 'Buen estado general', 1),
('OST-007', 'Lora', 'Hembra', '2025-04-22 21:49:00', 'Sin lesiones visibles', 1),
('OST-008', 'Verde', 'Hembra', '2025-04-25 22:56:00', 'Marca natural identificable en el caparazon', 1),
('OST-009', 'Lora', 'Hembra', '2025-05-01 23:03:00', 'Etiqueta revisada durante monitoreo', 1),
('OST-010', 'Carey', 'Hembra', '2025-05-04 19:10:00', 'Registro fotografico disponible', 1),
('OST-011', 'Lora', 'Hembra', '2025-06-07 20:17:00', 'Buen estado general', 1),
('OST-012', 'Lora', 'Hembra', '2025-06-10 21:24:00', 'Sin lesiones visibles', 1),
('OST-013', 'Verde', 'Hembra', '2025-07-13 22:31:00', 'Marca natural identificable en el caparazon', 1),
('OST-014', 'Lora', 'Hembra', '2025-07-16 23:38:00', 'Etiqueta revisada durante monitoreo', 1),
('OST-015', 'Carey', 'Hembra', '2025-08-19 19:45:00', 'Registro fotografico disponible', 1),
('OST-016', 'Lora', 'Hembra', '2025-08-22 20:52:00', 'Buen estado general', 1),
('OST-017', 'Lora', 'Hembra', '2025-01-25 21:59:00', 'Sin lesiones visibles', 1),
('OST-018', 'Verde', 'Hembra', '2025-01-01 22:06:00', 'Marca natural identificable en el caparazon', 1),
('OST-019', 'Lora', 'Hembra', '2025-02-04 23:13:00', 'Etiqueta revisada durante monitoreo', 1),
('OST-020', 'Carey', 'Hembra', '2025-02-07 19:20:00', 'Registro fotografico disponible', 1),
('OST-021', 'Lora', 'Hembra', '2025-03-10 20:27:00', 'Buen estado general', 1),
('OST-022', 'Lora', 'Hembra', '2025-03-13 21:34:00', 'Sin lesiones visibles', 1),
('OST-023', 'Verde', 'Hembra', '2025-04-16 22:41:00', 'Marca natural identificable en el caparazon', 1),
('OST-024', 'Lora', 'Hembra', '2025-04-19 23:48:00', 'Etiqueta revisada durante monitoreo', 1),
('OST-025', 'Carey', 'Hembra', '2025-05-22 19:55:00', 'Registro fotografico disponible', 1);

-- 6) fide_avistamiento_tb
INSERT INTO fide_avistamiento_tb (id_avistamiento, etiqueta_tortuga, comportamiento, ubicacion, fecha_avistamiento, observaciones, id_estado) VALUES
(1, 'OST-001', 'Anidacion', 'Sector Norte', '2026-08-01 19:07:00', 'Avistamiento controlado #1 durante recorrido de monitoreo', 1),
(2, 'OST-002', 'Retorno al mar', 'Sector Central', '2026-08-02 20:14:00', 'Avistamiento controlado #2 durante recorrido de monitoreo', 1),
(3, 'OST-003', 'Excavacion de nido', 'Sector Sur', '2026-08-03 21:21:00', 'Avistamiento controlado #3 durante recorrido de monitoreo', 1),
(4, 'OST-004', 'Desplazamiento', 'Boca del Rio', '2026-08-04 22:28:00', 'Avistamiento controlado #4 durante recorrido de monitoreo', 1),
(5, 'OST-005', 'Descanso previo a anidacion', 'Sector Rocoso', '2026-08-05 23:35:00', 'Avistamiento controlado #5 durante recorrido de monitoreo', 1),
(6, 'OST-006', 'Anidacion', 'Sector Norte', '2026-08-06 19:42:00', 'Avistamiento controlado #6 durante recorrido de monitoreo', 1),
(7, 'OST-007', 'Retorno al mar', 'Sector Central', '2026-08-07 20:49:00', 'Avistamiento controlado #7 durante recorrido de monitoreo', 1),
(8, 'OST-008', 'Excavacion de nido', 'Sector Sur', '2026-08-08 21:56:00', 'Avistamiento controlado #8 durante recorrido de monitoreo', 1),
(9, 'OST-009', 'Desplazamiento', 'Boca del Rio', '2026-08-09 22:03:00', 'Avistamiento controlado #9 durante recorrido de monitoreo', 1),
(10, 'OST-010', 'Descanso previo a anidacion', 'Sector Rocoso', '2026-08-10 23:10:00', 'Avistamiento controlado #10 durante recorrido de monitoreo', 1),
(11, 'OST-011', 'Anidacion', 'Sector Norte', '2026-08-11 19:17:00', 'Avistamiento controlado #11 durante recorrido de monitoreo', 1),
(12, 'OST-012', 'Retorno al mar', 'Sector Central', '2026-08-12 20:24:00', 'Avistamiento controlado #12 durante recorrido de monitoreo', 1),
(13, 'OST-013', 'Excavacion de nido', 'Sector Sur', '2026-08-13 21:31:00', 'Avistamiento controlado #13 durante recorrido de monitoreo', 1),
(14, 'OST-014', 'Desplazamiento', 'Boca del Rio', '2026-08-14 22:38:00', 'Avistamiento controlado #14 durante recorrido de monitoreo', 1),
(15, 'OST-015', 'Descanso previo a anidacion', 'Sector Rocoso', '2026-08-15 23:45:00', 'Avistamiento controlado #15 durante recorrido de monitoreo', 1),
(16, 'OST-016', 'Anidacion', 'Sector Norte', '2026-08-01 19:52:00', 'Avistamiento controlado #16 durante recorrido de monitoreo', 1),
(17, 'OST-017', 'Retorno al mar', 'Sector Central', '2026-08-02 20:59:00', 'Avistamiento controlado #17 durante recorrido de monitoreo', 1),
(18, 'OST-018', 'Excavacion de nido', 'Sector Sur', '2026-08-03 21:06:00', 'Avistamiento controlado #18 durante recorrido de monitoreo', 1),
(19, 'OST-019', 'Desplazamiento', 'Boca del Rio', '2026-08-04 22:13:00', 'Avistamiento controlado #19 durante recorrido de monitoreo', 1),
(20, 'OST-020', 'Descanso previo a anidacion', 'Sector Rocoso', '2026-08-05 23:20:00', 'Avistamiento controlado #20 durante recorrido de monitoreo', 1),
(21, 'OST-021', 'Anidacion', 'Sector Norte', '2026-08-06 19:27:00', 'Avistamiento controlado #21 durante recorrido de monitoreo', 1),
(22, 'OST-022', 'Retorno al mar', 'Sector Central', '2026-08-07 20:34:00', 'Avistamiento controlado #22 durante recorrido de monitoreo', 1),
(23, 'OST-023', 'Excavacion de nido', 'Sector Sur', '2026-08-08 21:41:00', 'Avistamiento controlado #23 durante recorrido de monitoreo', 1),
(24, 'OST-024', 'Desplazamiento', 'Boca del Rio', '2026-08-09 22:48:00', 'Avistamiento controlado #24 durante recorrido de monitoreo', 1),
(25, 'OST-025', 'Descanso previo a anidacion', 'Sector Rocoso', '2026-08-10 23:55:00', 'Avistamiento controlado #25 durante recorrido de monitoreo', 1);

-- 7) fide_nido_tb
INSERT INTO fide_nido_tb (id_nido, etiqueta_tortuga, ubicacion, fecha_anidacion, cantidad_huevos, profundidad_nido, observaciones, id_estado) VALUES
(1, 'OST-001', 'Sector Norte', '2026-08-01 20:15:00', 85, 41, 'Nido #1 delimitado y señalizado para seguimiento', 1),
(2, 'OST-002', 'Sector Central', '2026-08-02 21:15:00', 92, 44, 'Nido #2 delimitado y señalizado para seguimiento', 1),
(3, 'OST-003', 'Sector Sur', '2026-08-03 22:15:00', 99, 47, 'Nido #3 delimitado y señalizado para seguimiento', 1),
(4, 'OST-004', 'Boca del Rio', '2026-08-04 23:15:00', 106, 50, 'Nido #4 delimitado y señalizado para seguimiento', 1),
(5, 'OST-005', 'Sector Rocoso', '2026-08-05 20:15:00', 113, 53, 'Nido #5 delimitado y señalizado para seguimiento', 1),
(6, 'OST-006', 'Sector Norte', '2026-08-06 21:15:00', 120, 56, 'Nido #6 delimitado y señalizado para seguimiento', 1),
(7, 'OST-007', 'Sector Central', '2026-08-07 22:15:00', 82, 59, 'Nido #7 delimitado y señalizado para seguimiento', 1),
(8, 'OST-008', 'Sector Sur', '2026-08-08 23:15:00', 89, 38, 'Nido #8 delimitado y señalizado para seguimiento', 1),
(9, 'OST-009', 'Boca del Rio', '2026-08-09 20:15:00', 96, 41, 'Nido #9 delimitado y señalizado para seguimiento', 1),
(10, 'OST-010', 'Sector Rocoso', '2026-08-10 21:15:00', 103, 44, 'Nido #10 delimitado y señalizado para seguimiento', 1),
(11, 'OST-011', 'Sector Norte', '2026-08-11 22:15:00', 110, 47, 'Nido #11 delimitado y señalizado para seguimiento', 1),
(12, 'OST-012', 'Sector Central', '2026-08-12 23:15:00', 117, 50, 'Nido #12 delimitado y señalizado para seguimiento', 1),
(13, 'OST-013', 'Sector Sur', '2026-08-13 20:15:00', 79, 53, 'Nido #13 delimitado y señalizado para seguimiento', 1),
(14, 'OST-014', 'Boca del Rio', '2026-08-14 21:15:00', 86, 56, 'Nido #14 delimitado y señalizado para seguimiento', 1),
(15, 'OST-015', 'Sector Rocoso', '2026-08-15 22:15:00', 93, 59, 'Nido #15 delimitado y señalizado para seguimiento', 1),
(16, 'OST-016', 'Sector Norte', '2026-08-01 23:15:00', 100, 38, 'Nido #16 delimitado y señalizado para seguimiento', 1),
(17, 'OST-017', 'Sector Central', '2026-08-02 20:15:00', 107, 41, 'Nido #17 delimitado y señalizado para seguimiento', 1),
(18, 'OST-018', 'Sector Sur', '2026-08-03 21:15:00', 114, 44, 'Nido #18 delimitado y señalizado para seguimiento', 1),
(19, 'OST-019', 'Boca del Rio', '2026-08-04 22:15:00', 121, 47, 'Nido #19 delimitado y señalizado para seguimiento', 1),
(20, 'OST-020', 'Sector Rocoso', '2026-08-05 23:15:00', 83, 50, 'Nido #20 delimitado y señalizado para seguimiento', 1),
(21, 'OST-021', 'Sector Norte', '2026-08-06 20:15:00', 90, 53, 'Nido #21 delimitado y señalizado para seguimiento', 1),
(22, 'OST-022', 'Sector Central', '2026-08-07 21:15:00', 97, 56, 'Nido #22 delimitado y señalizado para seguimiento', 1),
(23, 'OST-023', 'Sector Sur', '2026-08-08 22:15:00', 104, 59, 'Nido #23 delimitado y señalizado para seguimiento', 1),
(24, 'OST-024', 'Boca del Rio', '2026-08-09 23:15:00', 111, 38, 'Nido #24 delimitado y señalizado para seguimiento', 1),
(25, 'OST-025', 'Sector Rocoso', '2026-08-10 20:15:00', 118, 41, 'Nido #25 delimitado y señalizado para seguimiento', 1);

-- 8) fide_nacimiento_tb
INSERT INTO fide_nacimiento_tb (id_nacimiento, id_nido, fecha_eclosion, crias_vivas, crias_muertas, crias_infertiles, observaciones, id_estado) VALUES
(1, 1, '2026-10-05 05:03:00', 65, 2, 3, 'Eclosion del nido #1; conteo realizado por el equipo de monitoreo', 1),
(2, 2, '2026-10-06 05:06:00', 70, 4, 6, 'Eclosion del nido #2; conteo realizado por el equipo de monitoreo', 1),
(3, 3, '2026-10-07 05:09:00', 75, 6, 9, 'Eclosion del nido #3; conteo realizado por el equipo de monitoreo', 1),
(4, 4, '2026-10-08 05:12:00', 80, 1, 2, 'Eclosion del nido #4; conteo realizado por el equipo de monitoreo', 1),
(5, 5, '2026-10-09 05:15:00', 85, 3, 5, 'Eclosion del nido #5; conteo realizado por el equipo de monitoreo', 1),
(6, 6, '2026-10-10 05:18:00', 90, 5, 8, 'Eclosion del nido #6; conteo realizado por el equipo de monitoreo', 1),
(7, 7, '2026-10-11 05:21:00', 95, 0, 1, 'Eclosion del nido #7; conteo realizado por el equipo de monitoreo', 1),
(8, 8, '2026-10-12 05:24:00', 62, 2, 4, 'Eclosion del nido #8; conteo realizado por el equipo de monitoreo', 1),
(9, 9, '2026-10-13 05:27:00', 67, 4, 7, 'Eclosion del nido #9; conteo realizado por el equipo de monitoreo', 1),
(10, 10, '2026-10-14 05:30:00', 72, 6, 0, 'Eclosion del nido #10; conteo realizado por el equipo de monitoreo', 1),
(11, 11, '2026-10-15 05:33:00', 77, 1, 3, 'Eclosion del nido #11; conteo realizado por el equipo de monitoreo', 1),
(12, 12, '2026-10-16 05:36:00', 82, 3, 6, 'Eclosion del nido #12; conteo realizado por el equipo de monitoreo', 1),
(13, 13, '2026-10-17 05:39:00', 87, 5, 9, 'Eclosion del nido #13; conteo realizado por el equipo de monitoreo', 1),
(14, 14, '2026-10-18 05:42:00', 92, 0, 2, 'Eclosion del nido #14; conteo realizado por el equipo de monitoreo', 1),
(15, 15, '2026-10-19 05:45:00', 97, 2, 5, 'Eclosion del nido #15; conteo realizado por el equipo de monitoreo', 1),
(16, 16, '2026-10-20 05:48:00', 64, 4, 8, 'Eclosion del nido #16; conteo realizado por el equipo de monitoreo', 1),
(17, 17, '2026-10-21 05:51:00', 69, 6, 1, 'Eclosion del nido #17; conteo realizado por el equipo de monitoreo', 1),
(18, 18, '2026-10-22 05:54:00', 74, 1, 4, 'Eclosion del nido #18; conteo realizado por el equipo de monitoreo', 1),
(19, 19, '2026-10-23 05:57:00', 79, 3, 7, 'Eclosion del nido #19; conteo realizado por el equipo de monitoreo', 1),
(20, 20, '2026-10-24 05:00:00', 84, 5, 0, 'Eclosion del nido #20; conteo realizado por el equipo de monitoreo', 1),
(21, 21, '2026-10-05 05:03:00', 89, 0, 3, 'Eclosion del nido #21; conteo realizado por el equipo de monitoreo', 1),
(22, 22, '2026-10-06 05:06:00', 94, 2, 6, 'Eclosion del nido #22; conteo realizado por el equipo de monitoreo', 1),
(23, 23, '2026-10-07 05:09:00', 61, 4, 9, 'Eclosion del nido #23; conteo realizado por el equipo de monitoreo', 1),
(24, 24, '2026-10-08 05:12:00', 66, 6, 2, 'Eclosion del nido #24; conteo realizado por el equipo de monitoreo', 1),
(25, 25, '2026-10-09 05:15:00', 71, 1, 5, 'Eclosion del nido #25; conteo realizado por el equipo de monitoreo', 1);

-- 9) fide_herramientas_tb
INSERT INTO fide_herramientas_tb (id_herramienta, nombre_herramienta, descripcion, id_estado) VALUES
(1, 'Linterna frontal', 'Equipo para recorridos y trabajo de campo', 7),
(2, 'Linterna de mano', 'Equipo para monitoreo y seguridad', 7),
(3, 'Chaleco reflectivo', 'Equipo de apoyo para actividades comunitarias', 7),
(4, 'Guantes de nitrilo', 'Equipo para registro y seguimiento', 8),
(5, 'Guantes de trabajo', 'Equipo disponible para cuadrillas de conservacion', 7),
(6, 'Pala pequeña', 'Equipo para recorridos y trabajo de campo', 7),
(7, 'Rastrillo', 'Equipo para monitoreo y seguridad', 7),
(8, 'Pinza recolectora', 'Equipo de apoyo para actividades comunitarias', 8),
(9, 'Bolsa reutilizable', 'Equipo para registro y seguimiento', 7),
(10, 'Cinta metrica', 'Equipo disponible para cuadrillas de conservacion', 7),
(11, 'GPS portatil', 'Equipo para recorridos y trabajo de campo', 7),
(12, 'Radio comunicador', 'Equipo para monitoreo y seguridad', 8),
(13, 'Botiquin', 'Equipo de apoyo para actividades comunitarias', 7),
(14, 'Conos de seguridad', 'Equipo para registro y seguimiento', 7),
(15, 'Estacas de marcacion', 'Equipo disponible para cuadrillas de conservacion', 7),
(16, 'Cuerda de delimitacion', 'Equipo para recorridos y trabajo de campo', 8),
(17, 'Libreta impermeable', 'Equipo para monitoreo y seguridad', 7),
(18, 'Termometro ambiental', 'Equipo de apoyo para actividades comunitarias', 7),
(19, 'Camara fotografica', 'Equipo para registro y seguimiento', 7),
(20, 'Binoculares', 'Equipo disponible para cuadrillas de conservacion', 8),
(21, 'Mochila de campo', 'Equipo para recorridos y trabajo de campo', 7),
(22, 'Recipiente plastico', 'Equipo para monitoreo y seguridad', 7),
(23, 'Lampara roja', 'Equipo de apoyo para actividades comunitarias', 7),
(24, 'Impermeable', 'Equipo para registro y seguimiento', 8),
(25, 'Silbato de emergencia', 'Equipo disponible para cuadrillas de conservacion', 9);

-- 10) fide_prestamo_tb
INSERT INTO fide_prestamo_tb (id_prestamo, id_herramienta, id_usuario, fecha_prestamo, fecha_devolucion, id_estado) VALUES
(1, 1, 1, '2026-08-01', '2026-08-03', 6),
(2, 2, 2, '2026-08-02', '2026-08-04', 6),
(3, 3, 3, '2026-08-03', '2026-08-05', 6),
(4, 4, 4, '2026-08-04', NULL, 8),
(5, 5, 5, '2026-08-05', '2026-08-07', 6),
(6, 6, 6, '2026-08-06', '2026-08-08', 6),
(7, 7, 7, '2026-08-07', '2026-08-09', 6),
(8, 8, 8, '2026-08-08', NULL, 8),
(9, 9, 9, '2026-08-09', '2026-08-11', 6),
(10, 10, 10, '2026-08-10', '2026-08-12', 6),
(11, 11, 11, '2026-08-11', '2026-08-13', 6),
(12, 12, 12, '2026-08-12', NULL, 8),
(13, 13, 1, '2026-08-13', '2026-08-15', 6),
(14, 14, 2, '2026-08-14', '2026-08-16', 6),
(15, 15, 3, '2026-08-15', '2026-08-17', 6),
(16, 16, 4, '2026-08-16', NULL, 8),
(17, 17, 5, '2026-08-17', '2026-08-19', 6),
(18, 18, 6, '2026-08-18', '2026-08-20', 6),
(19, 19, 7, '2026-08-19', '2026-08-21', 6),
(20, 20, 8, '2026-08-20', NULL, 8),
(21, 21, 9, '2026-08-01', '2026-08-03', 6),
(22, 22, 10, '2026-08-02', '2026-08-04', 6),
(23, 23, 11, '2026-08-03', '2026-08-05', 6),
(24, 24, 12, '2026-08-04', NULL, 8),
(25, 25, 1, '2026-08-05', '2026-08-07', 6);



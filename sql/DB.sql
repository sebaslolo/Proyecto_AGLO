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

create table fide_voluntariado_tb(
    id_voluntariado_registro int not null auto_increment,
    id_usuario int not null,
    id_estado int not null,
    id_actividad int, -- Referencia opcional a fide_actividad_tb si aplica
    fecha_ingreso date,
    disponibilidad varchar(100),
    horas_totales_acumuladas int default 0,
    fecha_inscripcion datetime,
    asistencia_confirmada boolean default false,
    hora_entrada datetime,
    hora_salida datetime,
    cantidad_horas_sesion decimal(4,2),
    fecha_sesion date,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_voluntariado_registro),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_voluntariado_usuario (id_usuario),
    index ndx_voluntariado_estado (id_estado)
) ENGINE = InnoDB;

    create table fide_monitoreo_tortugas_tb(
    id_monitoreo_registro int not null auto_increment,
    id_usuario int not null, -- Investigador o guía que realiza el reporte
    id_estado int not null,
    etiqueta_tortuga varchar(50) not null,
    especie_tortuga varchar(100),
    sexo_tortuga varchar(50),
    fecha_registro_tortuga datetime,
    observaciones_tortuga text,
    tipo_marca varchar(100),
    fecha_colocacion_marca datetime,
    fecha_retiro_marca datetime,
    ubicacion_marca_cuerpo varchar(150),
    fecha_monitoreo datetime,
    nombre_ubicacion_playa varchar(150),
    latitud decimal(10, 8),
    longitud decimal(11, 8),
    fecha_avistamiento datetime,
    lesiones_observadas text,
    id_nido_codigo varchar(50),
    fecha_anidacion datetime,
    numero_huevos int,
    profundidad_nido_cm decimal(5,2),
    observaciones_nido text,
    fecha_hora_eclosion datetime,
    crias_vivas int default 0,
    crias_muertas int default 0,
    crias_infertiles int default 0,
    observaciones_nacimiento text,
    condicion_corporal varchar(100),
    fecha_medicion datetime,
    largo_curvo_caparazon decimal(5,2),
    ancho_curvo_caparazon decimal(5,2),
    largo_recto_caparazon decimal(5,2),
    peso_kg decimal(5,2),
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_monitoreo_registro),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_tortuga_etiqueta (etiqueta_tortuga),
    index ndx_monitoreo_usuario (id_usuario)
) ENGINE = InnoDB;


create table fide_inventario_tb(
    id_inventario_registro int not null auto_increment,
    id_estado int not null,
    id_producto_codigo varchar(50),
    nombre_producto varchar(100) not null,
    descripcion_producto text,
    precio_venta decimal(10,2) default 0.00,
    categoria_producto varchar(100),
    tipo_producto varchar(100),
    stock_actual int default 0,
    tipo_movimiento varchar(50),
    cantidad_movimiento int,
    motivo_movimiento varchar(255),
    fecha_movimiento datetime,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_inventario_registro),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_inventario_producto (nombre_producto),
    index ndx_inventario_estado (id_estado)
) ENGINE = InnoDB;


create table fide_herramientas_tb(
    id_herramienta_registro int not null auto_increment,
    id_usuario int, 
    id_estado int not null, 
    nombre_herramienta varchar(100) not null,
    descripcion_herramienta text,
    codigo_patrimonio_placa varchar(50),
    categoria_herramienta varchar(100),
    fecha_prestamo datetime,
    fecha_devolucion_prevista datetime,
    fecha_devolucion_real datetime,
    observaciones_entrega text,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_herramienta_registro),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_herramienta_codigo (codigo_patrimonio_placa),
    index ndx_herramienta_usuario (id_usuario)
) ENGINE = InnoDB;


create table fide_formulario_tb(
    id_formulario_registro int not null auto_increment,
    id_usuario int not null, 
    id_actividad int, 
    id_estado int not null,
    id_solicitud_codigo varchar(50),
    tipo_solicitud_formulario varchar(100),
    fecha_solicitud datetime default current_timestamp,
    id_pregunta_ref int,
    texto_pregunta text,
    tipo_respuesta_esperada varchar(50),
    valor_respuesta_llenada text,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_formulario_registro),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_formulario_usuario (id_usuario),
    index ndx_formulario_estado (id_estado)
) ENGINE = InnoDB;

INSERT INTO fide_estado_tb (nombre_estado) VALUES
('Activo'),
('Inactivo'),
('Pendiente'),
('Confirmada'),
('Cancelada'),
('Completado'),
('Disponible');

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
('juan.garcia',      'J7@mP2x!', 'Juan',      'Garcia',      'Lopez',      'juan.garcia@gmail.com',      '612345678', 1),
('maria.martinez',   'M#8kL5q$', 'Maria',     'Martinez',    'Gomez',      'maria.martinez@gmail.com',   '622345679', 2),
('carlos.lopez',     'C9&vR1t*', 'Carlos',    'Lopez',       'Perez',      'carlos.lopez@gmail.com',     '632345680', 1),
('ana.rodriguez',    'A4!nW7z%', 'Ana',       'Rodriguez',   'Fernandez',  'ana.rodriguez@gmail.com',    '642345681', 2),
('luis.fernandez',   'L2@xH8m&', 'Luis',      'Fernandez',   'Gonzalez',   'luis.fernandez@gmail.com',   '652345682', 1),
('elena.gonzalez',   'E6$pT3k#', 'Elena',     'Gonzalez',    'Sanchez',    'elena.gonzalez@gmail.com',   '662345683', 2),
('pedro.perez',      'P1%yN9c!', 'Pedro',     'Perez',       'Ruiz',       'pedro.perez@gmail.com',      '672345684', 1),
('sofia.sanchez',    'S5&wQ2j$', 'Sofia',     'Sanchez',     'Ramirez',    'sofia.sanchez@gmail.com',    '682345685', 2),
('miguel.ruiz',      'M8!dF4v@', 'Miguel',    'Ruiz',        'Torres',     'miguel.ruiz@gmail.com',      '692345686', 1),
('isabel.ramirez',   'I3#uX7n%', 'Isabel',    'Ramirez',     'Flores',     'isabel.ramirez@gmail.com',   '702345687', 2),
('jose.torres',      'J9*tB5r&', 'Jose',      'Torres',      'Rivera',     'jose.torres@gmail.com',      '712345688', 1),
('lucia.flores',     'L4@gC8p!', 'Lucia',     'Flores',      'Morales',    'lucia.flores@gmail.com',     '722345689', 2),
('antonio.rivera',   'A7$hZ1m*', 'Antonio',   'Rivera',      'Ortiz',      'antonio.rivera@gmail.com',   '732345690', 1),
('david.solis',      'D8@qL2w!', 'David',     'Solis',       'Castro',     'david.solis@gmail.com',      '742345691', 2),
('nicole.vargas',    'N4#zX8m$', 'Nicole',    'Vargas',      'Jimenez',    'nicole.vargas@gmail.com',    '752345692', 1),
('bianca.solano',    'B7%rP3k&', 'Bianca',    'Solano',      'Mora',       'bianca.solano@gmail.com',    '762345693', 2),
('sebastian.castro', 'S2!nV6t@', 'Sebastian', 'Castro',      'Rojas',      'sebastian.castro@gmail.com', '772345694', 1),
('andres.mora',      'A9&gW4p#', 'Andres',    'Mora',        'Vega',       'andres.mora@gmail.com',      '782345695', 2),
('paula.vega',       'P3@dF8x!', 'Paula',     'Vega',        'Salas',      'paula.vega@gmail.com',       '792345696', 1),
('kevin.salas',      'K6#hJ1q$', 'Kevin',     'Salas',       'Leon',       'kevin.salas@gmail.com',      '802345697', 2),
('laura.leon',       'L5%yM9w*', 'Laura',     'Leon',        'Acosta',     'laura.leon@gmail.com',       '812345698', 1),
('jorge.acosta',     'J2@uT7k&', 'Jorge',     'Acosta',      'Herrera',    'jorge.acosta@gmail.com',     '822345699', 2),
('karla.herrera',    'K8!bR3n#', 'Karla',     'Herrera',     'Campos',     'karla.herrera@gmail.com',    '832345700', 1),
('diego.campos',     'D4$pX6m%', 'Diego',     'Campos',      'Navarro',    'diego.campos@gmail.com',     '842345701', 2),
('adriana.navarro',  'A1&vL8t!', 'Adriana',   'Navarro',     'Mendez',     'adriana.navarro@gmail.com',  '852345702', 1),
('roberto.mendez',   'R9@gQ5c$', 'Roberto',   'Mendez',      'Aguilar',    'roberto.mendez@gmail.com',   '862345703', 2),
('daniela.aguilar',  'D7#kH2z*', 'Daniela',   'Aguilar',     'Cordero',    'daniela.aguilar@gmail.com',  '872345704', 1),
('ricardo.cordero',  'R5!mN4x&', 'Ricardo',   'Cordero',     'Araya',      'ricardo.cordero@gmail.com',  '882345705', 2),
('gabriela.araya',   'G3%pW7j@', 'Gabriela',  'Araya',       'Monge',      'gabriela.araya@gmail.com',   '892345706', 1),
('oscar.monge',      'O6$tY1q#', 'Oscar',     'Monge',       'Quesada',    'oscar.monge@gmail.com',      '902345707', 2),
('valeria.quesada',  'V8@rC5m!', 'Valeria',   'Quesada',     'Alfaro',     'valeria.quesada@gmail.com',  '912345708', 1),
('fernando.alfaro',  'F2#nD9x$', 'Fernando',  'Alfaro',      'Esquivel',   'fernando.alfaro@gmail.com',  '922345709', 2),
('camila.esquivel',  'C4&wH6k%', 'Camila',    'Esquivel',    'Blanco',     'camila.esquivel@gmail.com',  '932345710', 1),
('eduardo.blanco',   'E1!vP8t&', 'Eduardo',   'Blanco',      'Chaves',     'eduardo.blanco@gmail.com',   '942345711', 2),
('patricia.chaves',  'P9@xJ3m*', 'Patricia',  'Chaves',      'Nunez',      'patricia.chaves@gmail.com',  '952345712', 1),
('sergio.nunez',     'S7#qL5c!', 'Sergio',    'Nunez',       'Porras',     'sergio.nunez@gmail.com',     '962345713', 2),
('monica.porras',    'M5%zR2w$', 'Monica',    'Porras',      'Soto',       'monica.porras@gmail.com',    '972345714', 1),
('cristian.soto',    'C8&tF4k@', 'Cristian',  'Soto',        'Valverde',   'cristian.soto@gmail.com',    '982345715', 2),
('alicia.valverde',  'A6!hN1p#', 'Alicia',    'Valverde',    'Rojas',      'alicia.valverde@gmail.com',  '992345716', 1),
('esteban.rojas',    'E3@dM7x%', 'Esteban',   'Rojas',       'Jimenez',    'esteban.rojas@gmail.com',    '602345717', 2),
('veronica.jimenez', 'V4&yK9q!', 'Veronica',  'Jimenez',     'Solis',      'veronica.jimenez@gmail.com', '612345718', 1),
('adrian.arias',     'A8#uP2m$', 'Adrian',    'Arias',       'Vargas',     'adrian.arias@gmail.com',     '622345719', 2),
('melissa.diaz',     'M1%gT6w*', 'Melissa',   'Diaz',        'Rojas',      'melissa.diaz@gmail.com',     '632345720', 1),
('hector.ortega',    'H9@bL5n&', 'Hector',    'Ortega',      'Lopez',      'hector.ortega@gmail.com',    '642345721', 2),
('ximena.marin',     'X2!kC8p#', 'Ximena',    'Marin',       'Campos',     'ximena.marin@gmail.com',     '652345722', 1),
('fabian.guerrero',  'F7$rW3x%', 'Fabian',    'Guerrero',    'Soto',       'fabian.guerrero@gmail.com',  '662345723', 2),
('carolina.reyes',   'C5&nJ1t@', 'Carolina',  'Reyes',       'Perez',      'carolina.reyes@gmail.com',   '672345724', 1),
('manuel.espinoza',  'M3#vH9m!', 'Manuel',    'Espinoza',    'Vega',       'manuel.espinoza@gmail.com',  '682345725', 2),
('tatiana.molina',   'T6@qX4k$', 'Tatiana',   'Molina',      'Castillo',   'tatiana.molina@gmail.com',   '692345726', 1);

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

INSERT INTO fide_voluntariado_tb (id_usuario, id_estado, fecha_ingreso, disponibilidad) VALUES
(13, 1, '2023-01-15', 'Lunes a Viernes'),
(14, 1, '2023-02-20', 'Fines de Semana'),
(15, 1, '2023-03-05', 'Lunes a Viernes'),
(16, 1, '2023-04-10', 'Fines de Semana'),
(17, 1, '2023-05-25', 'Lunes a Viernes'),
(18, 1, '2023-06-30', 'Fines de Semana'),
(19, 1, '2023-07-15', 'Lunes a Viernes'),
(20, 1, '2023-08-20', 'Fines de Semana'),
(21, 1, '2023-09-05', 'Lunes a Viernes'),
(22, 1, '2023-10-10', 'Fines de Semana'),
(23, 1, '2023-11-15', 'Lunes a Viernes'),
(24, 1, '2023-12-20', 'Fines de Semana');

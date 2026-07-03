CREATE database AGLO
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

use AGLO;

CREATE TABLE fide_estado_tb (

    id_estado  int not null AUTO_INCREMENT,
    nombre_estado varchar(50),
    fecha_creacion timestamp default CURRENT_TIMESTAMP,
    fecha_modificacion timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_estado),
    UNIQUE (nombre_estado),
    INDEX ndx_nombre_estado (nombre_estado )
) ENGINE = InnoDB;

CREATE TABLE fide_tipo_usuario_tb (

    id_tipo_usuario  int not null AUTO_INCREMENT,
    nombre_tipo_usuario varchar(50),
    fecha_creacion timestamp default CURRENT_TIMESTAMP,
    fecha_modificacion timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_tipo_usuario),
    UNIQUE (nombre_tipo_usuario),
    INDEX ndx_nombre_tipo_usuario (nombre_tipo_usuario)
) ENGINE = InnoDB;


create table fide_usuario_tb( 
    id_usuario  int not null auto_increment,
    identificacion varchar(20),
    nombre varchar(50),
    apellido_paterno varchar(50),
    apellido_materno varchar(50),
    id_tipo_usuario  int not null,
    id_estado  int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_usuario),
    foreign key (id_tipo_usuario) references fide_tipo_usuario_tb(id_tipo_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    unique (identificacion),
    index ndx_identificacion (identificacion))
    ENGINE = InnoDB;

create table fide_telefono_tb(
    id_telefono  int not null auto_increment,
    id_usuario  int not null,
    id_estado  int not null,
    numero_telefono varchar(20),
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_telefono),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    unique (numero_telefono),
    index ndx_numero_telefono (numero_telefono))
    ENGINE = InnoDB;

create table fide_cuenta_tb(
    id_cuenta  int not null auto_increment,
    id_usuario  int not null,
    id_estado  int not null,
    correo_electronico varchar(100)  ,
    contrasena_hash varchar(255)  ,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_cuenta),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    unique (correo_electronico),
    index ndx_correo_electronico (correo_electronico))
    ENGINE = InnoDB;


create table fide_reservacion_tb(
    id_reservacion  int not null auto_increment,
    id_usuario  int not null,
    id_estado  int not null,
    fecha_reservacion datetime,
    monto_total decimal(10,2),
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_reservacion),
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
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_tipo_actividad),
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
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_actividad),
    foreign key (id_tipo_actividad) references fide_tipo_actividad_tb(id_tipo_actividad),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_nombre_actividad (nombre_actividad)
)   ENGINE = InnoDB;

create table fide_actividad_guia_tb(
    id_actividad int not null,
    id_usuario int not null,
    id_estado int not null,
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_actividad, id_usuario),
    foreign key (id_actividad) references fide_actividad_tb(id_actividad),
    foreign key (id_usuario) references fide_usuario_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado),
    index ndx_actividad_guia_usuario (id_usuario),
    index ndx_actividad_guia_estado (id_estado),
    index ndx_actividad_guia_actividad (id_actividad)
) ENGINE = InnoDB;

create table fide_actividad_detalle_tb(
    id_reservacion int not null,
    id_actividad int not null,
    cantidad int default 1,
    precio_unitario decimal(10,2),
    fecha_creacion timestamp default current_timestamp,
    fecha_modificacion timestamp default current_timestamp on update current_timestamp,
    primary key (id_reservacion, id_actividad),
    foreign key (id_reservacion) references fide_reservacion_tb(id_reservacion),
    foreign key (id_actividad) references fide_actividad_tb(id_actividad),
    index ndx_id_actividad (id_actividad)
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


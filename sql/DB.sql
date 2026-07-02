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

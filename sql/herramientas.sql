create table fide_herramientas_tb(
    id_herramienta int primary key auto_increment,
    nombre_herramienta varchar(100),
    descripcion varchar(255),
    id_estado int not null,
    foreign key (id_estado) references fide_estado_tb(id_estado)
)ENGINE = InnoDB;

create table fide_prestamo_tb(
    id_prestamo int primary key auto_increment,
    id_herramienta int not null,
    id_usuario int not null,
    fecha_prestamo date,
    fecha_devolucion date,
    id_estado int not null,
    foreign key (id_herramienta) references fide_herramientas_tb(id_herramienta),
    foreign key (id_usuario) references fide_usuarios_tb(id_usuario),
    foreign key (id_estado) references fide_estado_tb(id_estado)
)ENGINE = InnoDB;


create table fide_tortuga_tb(
    etiqueta_tortuga varchar(50) primary key auto_increment,
    especie varchar(100),
    sexo varchar(10),
    fecha_registro datetime,
    observaciones varchar(255),
    id_estado int not null,
    foreign key (id_estado) references fide_estado_tb(id_estado)
)ENGINE = InnoDB;

create table fide_avistamiento_tb(
    id_avistamiento int primary key auto_increment,
    etiqueta_tortuga varchar(50) not null,
    comportamiento varchar(100),
    ubicacion varchar(100),
    fecha_avistamiento datetime,
    observaciones varchar(255),
    id_estado int not null,
    foreign key (etiqueta_tortuga) references fide_tortuga_tb(etiqueta_tortuga),
    foreign key (id_estado) references fide_estado_tb(id_estado)
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
    foreign key (etiqueta_tortuga) references fide_tortuga_tb(etiqueta_tortuga),
    foreign key (id_estado) references fide_estado_tb(id_estado)
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
    foreign key (id_estado) references fide_estado_tb(id_estado),
)ENGINE = InnoDB;


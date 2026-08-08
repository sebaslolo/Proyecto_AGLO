CREATE TABLE fide_tortuga_tb (
    etiqueta_tortuga VARCHAR(50) PRIMARY KEY,
    especie VARCHAR(100),
    sexo VARCHAR(10),
    fecha_registro DATETIME,
    observaciones VARCHAR(255),
    id_monitoreo INT NOT NULL,
    id_estado INT NOT NULL,
    FOREIGN KEY (id_monitoreo) REFERENCES fide_monitoreo_tb(id_monitoreo),
    FOREIGN KEY (id_estado) REFERENCES fide_estado_tb(id_estado),
    INDEX ndx_id_monitoreo_tortuga (id_monitoreo)
) ENGINE = InnoDB;

CREATE TABLE fide_avistamiento_tb (
    id_avistamiento INT PRIMARY KEY AUTO_INCREMENT,
    etiqueta_tortuga VARCHAR(50) NOT NULL,
    comportamiento VARCHAR(100),
    ubicacion VARCHAR(100),
    fecha_avistamiento DATETIME,
    observaciones VARCHAR(255),
    id_monitoreo INT NOT NULL,
    id_estado INT NOT NULL,
    FOREIGN KEY (etiqueta_tortuga) REFERENCES fide_tortuga_tb(etiqueta_tortuga),
    FOREIGN KEY (id_monitoreo) REFERENCES fide_monitoreo_tb(id_monitoreo),
    FOREIGN KEY (id_estado) REFERENCES fide_estado_tb(id_estado),
    INDEX ndx_id_monitoreo_avistamiento (id_monitoreo)
) ENGINE = InnoDB;

CREATE TABLE fide_nido_tb (
    id_nido INT PRIMARY KEY AUTO_INCREMENT,
    etiqueta_tortuga VARCHAR(50) NOT NULL,
    ubicacion VARCHAR(100),
    fecha_anidacion DATETIME,
    cantidad_huevos INT,
    profundidad_nido INT,
    observaciones VARCHAR(255),
    id_monitoreo INT NOT NULL,
    id_estado INT NOT NULL,
    FOREIGN KEY (etiqueta_tortuga) REFERENCES fide_tortuga_tb(etiqueta_tortuga),
    FOREIGN KEY (id_monitoreo) REFERENCES fide_monitoreo_tb(id_monitoreo),
    FOREIGN KEY (id_estado) REFERENCES fide_estado_tb(id_estado),
    INDEX ndx_id_monitoreo_nido (id_monitoreo)
) ENGINE = InnoDB;

CREATE TABLE fide_nacimiento_tb (
    id_nacimiento INT PRIMARY KEY AUTO_INCREMENT,
    id_nido INT NOT NULL,
    fecha_eclosion DATETIME,
    crias_vivas INT,
    crias_muertas INT,
    crias_infertiles INT,
    observaciones VARCHAR(255),
    id_monitoreo INT NOT NULL,
    id_estado INT NOT NULL,
    FOREIGN KEY (id_nido) REFERENCES fide_nido_tb(id_nido),
    FOREIGN KEY (id_monitoreo) REFERENCES fide_monitoreo_tb(id_monitoreo),
    FOREIGN KEY (id_estado) REFERENCES fide_estado_tb(id_estado),
    INDEX ndx_id_monitoreo_nacimiento (id_monitoreo)
) ENGINE = InnoDB;
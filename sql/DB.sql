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

INSERT INTO fide_estado_tb (nombre_estado) VALUES 
('Activo'),          
('Inactivo'),        
('Pendiente'),       
('Confirmada'),      
('Cancelada'),            
('Completado'),      
('Disponible');



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
    fecha_ingreso datetime,
    disponibilidad varchar(100),
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

INSERT INTO fide_inventario_tb (id_estado, id_producto_codigo, nombre_producto, descripcion_producto, precio_venta, categoria_producto, tipo_producto, stock_actual, tipo_movimiento, cantidad_movimiento, motivo_movimiento, fecha_movimiento) VALUES
(1, 'ART-001', 'Tortuga de Madera Pequeña', 'Artesanía tallada a mano por artesanos de Ostional', 15.00, 'Artesanías', 'Producto Venta', 25, 'Entrada', 25, 'Recepción de lote artesanal', NOW()),
(1, 'ART-002', 'Llavero de Resina Tortuguita', 'Llavero conmemorativo de la arribada', 5.00, 'Artesanías', 'Producto Venta', 100, 'Entrada', 100, 'Ingreso stock tienda', NOW()),
(1, 'ART-003', 'Camiseta Oficial AGLO M', 'Camiseta de algodón con logo de la asociación talla M', 20.00, 'Textil', 'Producto Venta', 30, 'Entrada', 30, 'Abastecimiento de uniformes/tienda', NOW()),
(1, 'ART-004', 'Camiseta Oficial AGLO L', 'Camiseta de algodón con logo de la asociación talla L', 20.00, 'Textil', 'Producto Venta', 30, 'Entrada', 30, 'Abastecimiento de uniformes/tienda', NOW()),
(1, 'ART-005', 'Gorra Bordada', 'Gorra con protección solar ajustable', 12.50, 'Textil', 'Producto Venta', 40, 'Entrada', 40, 'Compra a proveedor local', NOW()),
(1, 'ART-008', 'Bolso de Manta Ecológico', 'Bolso reutilizable para caminatas en la playa', 10.00, 'Textil', 'Producto Venta', 50, 'Entrada', 50, 'Ingreso stock tienda', NOW()),
(1, 'ART-009', 'Pulsera de Tortuga', 'Pulsera de hilo hecha por familias locales', 4.00, 'Artesanías', 'Producto Venta', 150, 'Entrada', 150, 'Recepción lote artesanal', NOW()),
(1, 'MAT-012', 'Malla de Protección', 'Malla plástica para proteger nidos contra depredadores', 0.00, 'Conservación', 'Insumo Interno', 60, 'Entrada', 60, 'Compra operativa', NOW()),
(1, 'MAT-013', 'Guantes de Látex Caja', 'Caja de 100 guantes para manipulación segura', 0.00, 'Protección', 'Insumo Interno', 15, 'Entrada', 15, 'Abastecimiento voluntariado', NOW()),
(1, 'MAT-014', 'Bolsas para Conteo Lora', 'Bolsas especiales para recolección de muestras biológicas', 0.00, 'Investigación', 'Insumo Interno', 500, 'Entrada', 500, 'Suministros biológicos', NOW()),
(1, 'MAT-015', 'Foco de Luz Roja LED', 'Linterna de luz roja que no afecta a las tortugas', 18.00, 'Monitoreo', 'Insumo Interno', 25, 'Entrada', 25, 'Compra de equipos guía', NOW()),
(1, 'MAT-016', 'Baterías AAA Recargables', 'Paquete de 4 pilas para linternas de monitoreo', 0.00, 'Energía', 'Insumo Interno', 20, 'Entrada', 20, 'Compra insumos', NOW()),
(1, 'MAT-017', 'Libreta de Campo Impermeable', 'Libreta Rite in the Rain para apuntes bajo la lluvia', 0.00, 'Monitoreo', 'Insumo Interno', 30, 'Entrada', 30, 'Dotación para guías', NOW()),
(1, 'MAT-018', 'Cinta Métrica de Fibra 5m', 'Cinta métrica para medir el caparazón de la tortuga', 0.00, 'Investigación', 'Insumo Interno', 12, 'Entrada', 12, 'Adquisición científica', NOW()),
(1, 'MAT-019', 'Líquido Desinfectante Galón', 'Para limpieza de calzado y herramientas post-monitoreo', 0.00, 'Limpieza', 'Insumo Interno', 8, 'Entrada', 8, 'Mantenimiento de estación', NOW()),



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



INSERT INTO fide_herramientas_tb (id_usuario, id_estado, nombre_herramienta, descripcion_herramienta, codigo_patrimonio_placa, categoria_herramienta, fecha_prestamo, fecha_devolucion_prevista, observaciones_entrega) VALUES
(1, 15, 'Cinta Métrica Ergonómica', 'Cinta flexible para medición de caparazones', 'AGLO-HER-001', 'Investigación', NOW(), '2026-07-06 04:00:00', 'Entregado en estuche plástico'),
(1, 15, 'Linterna Luz Roja Nocturna', 'Linterna frontal con filtro rojo regulable', 'AGLO-HER-002', 'Monitoreo', NOW(), '2026-07-06 04:00:00', 'Carga de batería completa'),
(1, 15, 'Linterna Luz Roja Nocturna', 'Linterna frontal con filtro rojo regulable', 'AGLO-HER-003', 'Monitoreo', NOW(), '2026-07-06 04:00:00', 'Detalle de desgaste en correa'),
(1, 15, 'GPS Portátil', 'Dispositivo de geolocalización para nidos de tortuga', 'AGLO-HER-006', 'Investigación', NOW(), '2026-07-06 06:00:00', 'Pantalla con protector, pilas cargadas'),
(1, 15, 'Pala de Punta Plástica', 'Pala ligera para remoción cuidadosa de arena', 'AGLO-HER-007', 'Voluntariado', NOW(), '2026-07-05 12:00:00', 'Limpia'),
(1, 15, 'Rastrillo para Limpieza', 'Rastrillo metálico para remover microplásticos de la playa', 'AGLO-HER-009', 'Voluntariado', NOW(), '2026-07-05 12:00:00', 'Dientes completos'),
(1, 15, 'Kit de Primeros Auxilios', 'Botiquín portátil de marcha para patrullas nocturnas', 'AGLO-HER-013', 'Seguridad', NOW(), '2026-07-06 06:00:00', 'Completo con insumos vigentes'),
(1, 15, 'Megáfono Portátil 20W', 'Megáfono para manejo e instrucciones de grupos de turistas', 'AGLO-HER-014', 'Turismo', NOW(), '2026-07-05 22:00:00', 'Probado el sonido'),
(1, 15, 'Termómetro de Arena', 'Termómetro de sonda larga para medir temperatura del nido', 'AGLO-HER-016', 'Investigación', NOW(), '2026-07-06 04:00:00', 'Sonda desinfectada'),
(1, 15, 'Radio Transmisor', 'Radio VHF para comunicación entre cuadrillas de guías', 'AGLO-HER-020', 'Seguridad', NOW(), '2026-07-06 06:00:00', 'Antena flexible nueva'),
(1, 15, 'Cámara Fotográfica de Campo', 'Cámara compacta a prueba de agua para registro de anomalías', 'AGLO-HER-021', 'Investigación', NOW(), '2026-07-06 05:00:00', 'Tarjeta SD limpia 32GB'),
(1, 15, 'Malla Metálica de Muestra', 'Estructura cilíndrica piloto para nidos de control', 'AGLO-HER-022', 'Conservación', NOW(), '2026-07-06 02:00:00', 'Ligeramente oxidada'),
(1, 15, 'Foco Luz Blanca de Emergencia', 'Reflector potente únicamente para emergencias o rescates', 'AGLO-HER-024', 'Seguridad', '2026-07-05 18:00:00', '2026-07-06 06:00:00', 'Prohibido usar frente a tortugas desovando'),
(1, 15, 'Tablilla con Clip de Campo', 'Soporte acrílico para hojas de conteo de voluntarios', 'AGLO-HER-025', 'Monitoreo', NOW(), '2026-07-05 23:00:00', 'Lápiz de grafito atado');
(1, 15, 'Balanza Digital', 'Balanza portátil para pesar muestras o nidos colapsados', 'AGLO-HER-026', 'Investigación', NOW(), '2026-07-06 04:00:00', 'Capacidad hasta 20kg')


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

INSERT INTO fide_formulario_tb (id_usuario, id_actividad, id_estado, id_solicitud_codigo, tipo_solicitud_formulario, id_pregunta_ref, texto_pregunta, tipo_respuesta_esperada, valor_respuesta_llenada) VALUES
(1, 101, 1, 'FORM-2026-01', 'Retroalimentación Voluntariado', 1, 'Califique la experiencia general del 1 al 5', 'Número', '5'),
(1, 101, 1, 'FORM-2026-02', 'Inscripción Voluntariado', 4, '¿Tiene experiencia previa en manejo de tortugas?', 'Booleano', 'No'),
(1, 102, 1, 'FORM-2026-03', 'Reporte Avistamiento', 7, 'Especie de tortuga identificada', 'Opciones', 'Tortuga Lora (Lepidochelys olivacea)')(1, 102, 1, 'FORM-2026-03', 'Reporte Avistamiento', 9, 'Comportamiento predominante observado', 'Texto corto', 'Desove masivo fluido'),
(1, 103, 1, 'FORM-2026-05', 'Control Herramientas', 11, '¿Alguna herramienta presentó fallas?', 'Booleano', 'Sí'),(1, 102, 1, 'FORM-2026-07', 'Reporte Avistamiento', 8, 'Estimado de tortugas en el sector asignado', 'Número', '2'),
(1, 101, 1, 'FORM-2026-08', 'Retroalimentación Voluntariado', 1, 'Califique la experiencia general del 1 al 5', 'Número', '5'),



-- =====================================================================
-- KINALAPP - SCRIPT DE BASE DE DATOS (MySQL Workbench)
-- =====================================================================
-- Compatible con el proyecto Spring Boot KinalApp.
-- Crea la base de datos, tablas y datos iniciales (productos y clientes).
-- El usuario administrador se crea AUTOMATICAMENTE al arrancar el
-- backend mediante AdminSeeder.java (no es necesario insertarlo aqui).
--
-- Credenciales de admin (auto-creadas por la aplicacion):
--   Usuario:    admin
--   Contrasena: admin12345
--
-- Para ejecutar:
--   1. Abrir MySQL Workbench.
--   2. Conectar a tu instancia local (localhost:3306).
--   3. Abrir este archivo y ejecutar TODO (Ctrl + Shift + Enter).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS dbClientess_in5am
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE dbClientess_in5am;

-- ---------------------------------------------------------------------
-- Tabla: usuarios
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    codigo_usuario BIGINT NOT NULL AUTO_INCREMENT,
    username       VARCHAR(50)  NOT NULL,
    password       VARCHAR(255) NOT NULL,
    email          VARCHAR(120) NOT NULL,
    rol            VARCHAR(30)  NOT NULL,
    estado         BIGINT       NOT NULL DEFAULT 1,
    PRIMARY KEY (codigo_usuario),
    UNIQUE KEY uk_usuarios_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Tabla: clientes
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clientes (
    dpi_cliente      VARCHAR(20)  NOT NULL,
    nombre_cliente   VARCHAR(80)  NOT NULL,
    apellido_cliente VARCHAR(80)  NOT NULL,
    direccion        VARCHAR(200) NULL,
    estado           INT          NOT NULL DEFAULT 1,
    foto_url         VARCHAR(255) NULL,
    PRIMARY KEY (dpi_cliente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Tabla: productos
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS productos (
    codigo_producto INT NOT NULL AUTO_INCREMENT,
    nombre_producto VARCHAR(120) NOT NULL,
    precio          DOUBLE       NOT NULL DEFAULT 0,
    stock           INT          NOT NULL DEFAULT 0,
    estado          BIGINT       NOT NULL DEFAULT 1,
    PRIMARY KEY (codigo_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Tabla: ventas
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ventas (
    codigo_venta   BIGINT NOT NULL AUTO_INCREMENT,
    fecha_venta    DATE   NULL,
    estado         BIGINT NOT NULL DEFAULT 1,
    dpi_cliente    VARCHAR(20) NULL,
    codigo_usuario BIGINT NULL,
    PRIMARY KEY (codigo_venta),
    KEY fk_ventas_cliente (dpi_cliente),
    KEY fk_ventas_usuario (codigo_usuario),
    CONSTRAINT fk_ventas_cliente FOREIGN KEY (dpi_cliente)
        REFERENCES clientes (dpi_cliente)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_ventas_usuario FOREIGN KEY (codigo_usuario)
        REFERENCES usuarios (codigo_usuario)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Tabla: detalles_ventas
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS detalles_ventas (
    codigo_detalle  INT NOT NULL AUTO_INCREMENT,
    codigo_venta    BIGINT NULL,
    codigo_producto INT NULL,
    cantidad        INT NOT NULL DEFAULT 1,
    precio_unitario DOUBLE NOT NULL DEFAULT 0,
    estado          BIGINT NOT NULL DEFAULT 1,
    PRIMARY KEY (codigo_detalle),
    KEY fk_det_venta (codigo_venta),
    KEY fk_det_producto (codigo_producto),
    CONSTRAINT fk_det_venta FOREIGN KEY (codigo_venta)
        REFERENCES ventas (codigo_venta)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_det_producto FOREIGN KEY (codigo_producto)
        REFERENCES productos (codigo_producto)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- DATOS INICIALES: PRODUCTOS
-- =====================================================================
-- Catalogo base disponible para clientes y empleados.
-- Los clientes (rol CLIENTE) pueden ver estos productos y comprarlos.
-- =====================================================================
INSERT INTO productos (nombre_producto, precio, stock, estado) VALUES
('Laptop HP 240 G9 Intel i5',         5499.00, 12, 1),
('Laptop Lenovo IdeaPad 3 Ryzen 5',   4799.00, 10, 1),
('Monitor LG 24" Full HD',            1299.00, 25, 1),
('Monitor Samsung 27" Curvo',         1899.00, 14, 1),
('Teclado mecanico Redragon Kumara',   349.00, 40, 1),
('Mouse logitech G203 Lightsync',      219.00, 60, 1),
('Audifonos HyperX Cloud Stinger',     499.00, 30, 1),
('Audifonos Bluetooth JBL Tune 510',   389.00, 22, 1),
('Webcam Logitech C920 HD Pro',        729.00, 18, 1),
('Impresora Epson L3250 EcoTank',     1599.00, 11, 1),
('Disco SSD Kingston 480GB',           379.00, 35, 1),
('Disco SSD NVMe Crucial 1TB',         789.00, 20, 1),
('Memoria RAM Kingston Fury 16GB',     499.00, 28, 1),
('Memoria USB Sandisk 64GB',            89.00, 80, 1),
('Router TP-Link Archer C6 AC1200',    349.00, 19, 1),
('Switch TP-Link 8 Puertos Gigabit',   299.00, 16, 1),
('Cargador USB-C 65W GaN',             259.00, 24, 1),
('Mochila para laptop 15.6"',          189.00, 33, 1),
('Tablet Samsung Galaxy Tab A9',      1499.00,  9, 1),
('Smartphone Xiaomi Redmi Note 13',   2199.00,  7, 1),
('Bocina Bluetooth JBL Go 3',          299.00, 26, 1),
('Silla gamer Cougar Armor Air',      2499.00,  5, 1),
('Cable HDMI 2.0 1.5m',                 49.00, 90, 1),
('Adaptador HDMI a VGA',                79.00, 45, 1),
('Power Bank 20000mAh USB-C',          329.00, 17, 1);

-- =====================================================================
-- DATOS INICIALES: CLIENTES DE EJEMPLO
-- =====================================================================
INSERT INTO clientes (dpi_cliente, nombre_cliente, apellido_cliente, direccion, estado) VALUES
('2998123450101', 'Ludwing',  'Vasquez',  'Zona 5, Guatemala',   1),
('3014987650108', 'Maria',    'Hernandez','Zona 11, Mixco',      1),
('1855001230109', 'Carlos',   'Lopez',    'Villa Nueva',         1),
('2734556780101', 'Andrea',   'Garcia',   'San Miguel Petapa',   1),
('1922344560110', 'Jose',     'Morales',  'Antigua Guatemala',   1);

-- =====================================================================
-- VERIFICACION
-- =====================================================================
SELECT 'usuarios'         AS tabla, COUNT(*) AS total FROM usuarios
UNION ALL SELECT 'clientes',         COUNT(*) FROM clientes
UNION ALL SELECT 'productos',        COUNT(*) FROM productos
UNION ALL SELECT 'ventas',           COUNT(*) FROM ventas
UNION ALL SELECT 'detalles_ventas',  COUNT(*) FROM detalles_ventas;

-- ==========================================
-- 🔹 ALTAVISTADB - Sistema de Reservas (SQL Server)
--    Cumple: PK / UNIQUE / DEFAULT / CHECK
--    10 registros por tabla, inserts coherentes, updates y selects
-- ==========================================

USE master;
GO

-- 1) Eliminar base si existe (modo seguro)
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'AltavistaDB')
BEGIN
    ALTER DATABASE AltavistaDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE AltavistaDB;
END
GO

-- 2) Crear base
CREATE DATABASE AltavistaDB;
GO

-- 3) Usar la base
USE AltavistaDB;
GO

-- ====================================================
-- TABLA: customer (maestra)
-- - campos: mínimo 6
-- - UNIQUE: email
-- - DEFAULT: registered_at, updated_at, is_active
-- - CHECK: client_type IN ('V','R','N')
-- ====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'customer')
BEGIN
    CREATE TABLE customer (
        customer_id INT IDENTITY(1,1) PRIMARY KEY,
        first_name NVARCHAR(100) NOT NULL,
        last_name NVARCHAR(100) NOT NULL,
        phone NVARCHAR(20),
        email NVARCHAR(100) UNIQUE,

        -- Auditoría CRUD estándar
        created_at DATETIME DEFAULT GETDATE(),   -- Antes: registered_at
        updated_at DATETIME DEFAULT GETDATE(),
        deleted_at DATETIME NULL,
        restored_at DATETIME NULL,

        -- Otros campos
        preferences NVARCHAR(MAX),
        is_active BIT DEFAULT 1,                 -- Puedes mantenerlo
        is_deleted BIT DEFAULT 0,                -- ESTÁNDAR BACKEND
        client_type CHAR(1) NOT NULL CHECK (client_type IN ('V','R','N'))
    );
END
GO

-- ====================================================
-- TABLA: table_spot (maestra)
-- - UNIQUE table_number
-- - CHECK capacity > 0
-- ====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'table_spot')
BEGIN
    CREATE TABLE table_spot (
        table_id INT IDENTITY(1,1) PRIMARY KEY,
        table_number INT NOT NULL UNIQUE,
        location NVARCHAR(50) NOT NULL,
        capacity INT NOT NULL CHECK (capacity > 0),
        is_available BIT DEFAULT 1,
        notes NVARCHAR(MAX),
        status NVARCHAR(50) DEFAULT 'Disponible',
        last_clean DATE,
        cleaning_time TIME,
        layout_details NVARCHAR(MAX)
    );
END
GO

-- ====================================================
-- TABLA: product (maestra)
-- - CHECK price > 0
-- - DEFAULT is_available
-- ====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'product')
BEGIN
    CREATE TABLE product (
        product_id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        description NVARCHAR(MAX),
        price DECIMAL(10,2) NOT NULL CHECK (price > 0),
        category CHAR(3),
        is_available BIT DEFAULT 1,
        image_url NVARCHAR(255),
        launch_date DATE,
        prep_time TIME,
        is_featured BIT DEFAULT 0,
        nutritional_info NVARCHAR(MAX)
    );
END
GO

-- ====================================================
-- TABLA: reservation (transaccional)
-- - FK a customer, table_spot
-- ====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'reservation')
BEGIN
    CREATE TABLE reservation (
        reservation_id INT IDENTITY(1,1) PRIMARY KEY,
        reservation_date DATE NOT NULL,
        reservation_time TIME NOT NULL,
        guests_count INT NOT NULL CHECK (guests_count > 0),
        status NVARCHAR(50) DEFAULT 'Pendiente',
        customer_customer_id INT NOT NULL,
        table_spot_table_id INT NOT NULL,
        is_active BIT DEFAULT 1,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        deleted_at DATETIME NULL,
        restored_at DATETIME NULL,
        CONSTRAINT FK_reservation_customer FOREIGN KEY (customer_customer_id) REFERENCES customer(customer_id),
        CONSTRAINT FK_reservation_table FOREIGN KEY (table_spot_table_id) REFERENCES table_spot(table_id)
    );
END
GO

-- ====================================================
-- TABLA: [order] (transaccional)
-- - FK a reservation
-- ====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'order')
BEGIN
    CREATE TABLE [order] (
        order_id INT IDENTITY(1,1) PRIMARY KEY,
        order_date DATETIME DEFAULT GETDATE(),
        total_amount DECIMAL(10,2) DEFAULT 0.00 CHECK (total_amount >= 0),
        reservation_reservation_id INT NULL,
        payment_method NVARCHAR(50),
        tip_amount DECIMAL(10,2) DEFAULT 0.00 CHECK (tip_amount >= 0),
        CONSTRAINT FK_order_reservation FOREIGN KEY (reservation_reservation_id) REFERENCES reservation(reservation_id)
    );
END
GO

-- ====================================================
-- TABLA: order_detail (transaccional)
-- - FK a order y product
-- ====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'order_detail')
BEGIN
    CREATE TABLE order_detail (
        detail_id INT IDENTITY(1,1) PRIMARY KEY,
        quantity INT NOT NULL CHECK (quantity > 0),
        order_order_id INT NOT NULL,
        product_product_id INT NOT NULL,
        price_at_purchase DECIMAL(10,2) NOT NULL CHECK (price_at_purchase >= 0),
        CONSTRAINT FK_orderdetail_order FOREIGN KEY (order_order_id) REFERENCES [order](order_id),
        CONSTRAINT FK_orderdetail_product FOREIGN KEY (product_product_id) REFERENCES product(product_id)
    );
END
GO

-- ====================================================
-- TABLA: admin_user
-- - Esquema explícito dbo para evitar errores en Spring Boot
-- ====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'admin_user' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.admin_user (
        user_id INT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(50) UNIQUE NOT NULL,
        password NVARCHAR(255) NOT NULL, -- Hashear luego
        is_active BIT DEFAULT 1,
        created_at DATETIME DEFAULT GETDATE()
    );

    INSERT INTO dbo.admin_user (username, password)
    VALUES 
    ('nayeli', 'nayeliha123'),
    ('yancarlos', 'yancarlos123');
END
GO

SELECT TABLE_SCHEMA, TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_NAME = 'admin_user';
GO
-- ====================================================
-- INSERTS: 10 registros por tabla (maestras y transaccionales)
-- Datos coherentes para Altavista Rooftop
-- ====================================================

-- 10 CUSTOMERS (client_type: 'V' VIP, 'R' Regular, 'N' Nuevo)
INSERT INTO customer (first_name, last_name, phone, email, preferences, client_type)
VALUES
(N'Lucía', N'Fernández', '987654321', 'lucia@altavista.com', N'{"favorite_dish":"Ceviche"}', 'V'),
(N'Carlos', N'Ramírez', '912345678', 'carlos@altavista.com', N'{"drink":"Pisco Sour"}', 'R'),
(N'Mariana', N'Torres', '945672189', 'mariana@altavista.com', N'{"seat":"Terraza"}', 'N'),
(N'Andrés', N'Salazar', '956789123', 'andres@altavista.com', N'{"allergies":"Nueces"}', 'R'),
(N'Paula', N'García', '978654312', 'paula@altavista.com', N'{"event":"Cena Romántica"}', 'V'),
(N'Luis', N'Martínez', '999555333', 'luis@altavista.com', N'{"music":"Jazz"}', 'R'),
(N'Valeria', N'Rojas', '988223344', 'valeria@altavista.com', N'{"table_pref":"Ventana"}', 'N'),
(N'Eduardo', N'Reyes', '933556677', 'eduardo@altavista.com', N'{"likes":"Eventos"}', 'R'),
(N'Ana', N'Lozano', '977334455', 'ana@altavista.com', N'{"menu":"Vegetariano"}', 'N'),
(N'Sofía', N'Mendoza', '955667788', 'sofia@altavista.com', N'{"seat":"Barra"}', 'R');
GO

-- 10 TABLE_SPOT
INSERT INTO table_spot (table_number, location, capacity, is_available, notes, status, last_clean, cleaning_time, layout_details)
VALUES
(1, N'Terraza Norte', 4, 1, N'Vista panorámica', N'Disponible', '2025-10-20', '10:00:00', N'{"shape":"round"}'),
(2, N'Terraza Sur', 2, 1, N'Zona romántica', N'Disponible', '2025-10-20', '10:05:00', N'{"shape":"square"}'),
(3, N'Interior Lounge', 6, 1, N'Cerca del bar', N'Ocupada', '2025-10-19', '09:50:00', N'{"shape":"rect"}'),
(4, N'VIP Zona A', 8, 0, N'Zona privada', N'Reservada', '2025-10-18', '11:00:00', N'{"shape":"oval"}'),
(5, N'Balcón Este', 4, 1, N'Vista al río', N'Disponible', '2025-10-20', '10:10:00', N'{"shape":"round"}'),
(6, N'Balcón Oeste', 2, 1, N'Espacio acogedor', N'Disponible', '2025-10-20', '10:15:00', N'{"shape":"square"}'),
(7, N'Salón Central', 10, 1, N'Ideal para eventos', N'Disponible', '2025-10-18', '12:00:00', N'{"shape":"rect"}'),
(8, N'Zona Lounge', 6, 0, N'Área reservada', N'Mantenimiento', '2025-10-17', '12:30:00', N'{"shape":"oval"}'),
(9, N'Terraza Alta', 4, 1, N'Vista a la ciudad', N'Disponible', '2025-10-19', '10:20:00', N'{"shape":"round"}'),
(10, N'Rooftop Bar', 2, 1, N'Bar exclusivo', N'Disponible', '2025-10-20', '10:25:00', N'{"shape":"square"}');
GO

-- 10 PRODUCTS
INSERT INTO product (name, description, price, category, is_available, image_url, launch_date, prep_time, is_featured, nutritional_info)
VALUES
(N'Cocktail Sunset', N'Cóctel frutal de la casa', 22.50, 'BEB', 1, N'/img/cocktail.jpg', '2025-01-05', '00:05:00', 1, N'{"alcohol":true}'),
(N'Tiradito Altavista', N'Tiradito de pescado fresco', 36.00, 'ENT', 1, N'/img/tiradito.jpg', '2025-02-10', '00:10:00', 1, N'{"protein":30}'),
(N'Risotto de Langostinos', N'Risotto cremoso con langostinos', 49.90, 'PRI', 1, N'/img/risotto.jpg', '2025-03-02', '00:20:00', 1, N'{"calories":550}'),
(N'Lomo Fino', N'Corte selecto en salsa de vino', 58.00, 'PRI', 1, N'/img/lomo.jpg', '2025-03-12', '00:25:00', 1, N'{"protein":40}'),
(N'Postre Luna', N'Cheesecake artesanal', 24.00, 'POS', 1, N'/img/cheesecake.jpg', '2025-04-20', '00:08:00', 0, N'{"sugar":20}'),
(N'Ceviche Rooftop', N'Clásico ceviche gourmet', 38.00, 'ENT', 1, N'/img/ceviche.jpg', '2025-02-28', '00:12:00', 1, N'{"protein":35}'),
(N'Mocktail Garden', N'Bebida sin alcohol', 18.00, 'BEB', 1, N'/img/mocktail.jpg', '2025-01-20', '00:05:00', 0, N'{"alcohol":false}'),
(N'Tabla Altavista', N'Piqueos y quesos artesanales', 46.00, 'ENT', 1, N'/img/tabla.jpg', '2025-04-01', '00:15:00', 1, N'{"protein":25}'),
(N'Tarta de Chocolate', N'Tarta húmeda de cacao', 22.00, 'POS', 1, N'/img/tarta.jpg', '2025-04-15', '00:10:00', 0, N'{"sugar":30}'),
(N'Sour de la Casa', N'Pisco sour con maracuyá', 26.00, 'BEB', 1, N'/img/sour.jpg', '2025-02-14', '00:06:00', 1, N'{"alcohol":true}');
GO

-- 10 RESERVATIONS (coherentes con customer_id y table_spot table_id)
INSERT INTO reservation (reservation_date, reservation_time, guests_count, status, customer_customer_id, table_spot_table_id)
VALUES
('2025-10-26','19:00',4,'Confirmada',1,1),
('2025-10-26','20:00',2,'Pendiente',2,2),
('2025-10-26','21:00',6,'Confirmada',3,3),
('2025-10-27','19:30',8,'Cancelada',4,4),
('2025-10-27','18:00',4,'Confirmada',5,5),
('2025-10-27','21:30',2,'Pendiente',6,6),
('2025-10-28','20:00',10,'Confirmada',7,7),
('2025-10-28','19:45',6,'En Espera',8,8),
('2025-10-28','18:30',4,'Confirmada',9,9),
('2025-10-29','22:00',2,'Confirmada',10,10);
GO

-- 10 ORDERS (coherentes con reservation_id)
INSERT INTO [order] (order_date, total_amount, reservation_reservation_id, payment_method, tip_amount)
VALUES
('2025-10-26 20:30', 110.50, 1, N'Tarjeta', 10.00),
('2025-10-26 21:15', 72.00, 2, N'Efectivo', 8.00),
('2025-10-26 22:00', 155.00, 3, N'Tarjeta', 15.00),
('2025-10-27 20:10', 0.00, 4, N'N/A', 0.00),
('2025-10-27 19:00', 95.50, 5, N'Yape', 9.50),
('2025-10-27 22:15', 60.00, 6, N'Efectivo', 5.00),
('2025-10-28 21:00', 210.00, 7, N'Tarjeta', 20.00),
('2025-10-28 19:50', 130.00, 8, N'Plin', 12.00),
('2025-10-28 20:10', 90.00, 9, N'Efectivo', 8.00),
('2025-10-29 22:30', 78.00, 10, N'Tarjeta', 7.00);
GO

-- 10 ORDER_DETAILS (coherentes con order_id y product_id)
INSERT INTO order_detail (quantity, order_order_id, product_product_id, price_at_purchase)
VALUES
(2,1,1,22.50),
(1,1,2,36.00),
(1,2,10,26.00),
(3,3,3,49.90),
(2,5,5,24.00),
(2,6,7,18.00),
(1,7,4,58.00),
(1,8,8,46.00),
(2,9,9,22.00),
(2,10,6,38.00);
GO

-- ====================================================
-- EJEMPLOS DE ACTUALIZACIÓN (maestras y transaccionales)
--  - Update en tabla maestra (product)
--  - Update en transaccional (reservation status)
--  - Transacción: crear order + order_detail y actualizar total
--  - Eliminado lógico (customer.is_active = 0) y restaurado
-- ====================================================

-- 1) Actualizar precios (maestra)
UPDATE product
SET price = price + 2.00
WHERE product_id IN (1,2,3);
GO

-- 2) Actualizar estado de una reserva (transaccional)
UPDATE reservation
SET status = 'Confirmada'
WHERE reservation_id = 2;
GO

-- 3) Transacción ejemplo: insertar orden + detalle y recalcular total (transaccional)
BEGIN TRAN;
    DECLARE @newOrderId INT;
    INSERT INTO [order] (order_date, total_amount, reservation_reservation_id, payment_method, tip_amount)
    VALUES (GETDATE(), 0.00, 2, N'Tarjeta', 5.00);
    SET @newOrderId = SCOPE_IDENTITY();

    INSERT INTO order_detail (quantity, order_order_id, product_product_id, price_at_purchase)
    VALUES (1, @newOrderId, 2, (SELECT price FROM product WHERE product_id = 2));

    -- Actualizar total_amount sumando order_details
    UPDATE [order]
    SET total_amount = (SELECT SUM(quantity * price_at_purchase) FROM order_detail WHERE order_order_id = @newOrderId)
    WHERE order_id = @newOrderId;
COMMIT TRAN;
GO

-- 4) Eliminado lógico (maestra) y restaurado
-- Marcar customer_id = 3 como inactivo (eliminado lógico)
UPDATE customer SET is_active = 0 WHERE customer_id = 3;
-- Restaurar: poner en activo
UPDATE customer SET is_active = 1 WHERE customer_id = 3;
GO

-- ====================================================
-- LISTADOS / CONSULTAS RELEVANTES (maestras y transaccionales)
-- ====================================================

-- 1) Listar customers (maestra)
SELECT * FROM customer;
GO

-- 2) Listar mesas (maestra)
SELECT * FROM table_spot;
GO

-- 3) Listar productos (maestra)
SELECT * FROM product;
GO

-- 4) Listar reservas con datos de cliente y mesa (join)
SELECT r.reservation_id, r.reservation_date, r.reservation_time, r.guests_count, r.status,
       c.customer_id, c.first_name, c.last_name, c.email,
       t.table_id, t.table_number, t.location
FROM reservation r
JOIN customer c ON r.customer_customer_id = c.customer_id
JOIN table_spot t ON r.table_spot_table_id = t.table_id
ORDER BY r.reservation_date, r.reservation_time;
GO

-- 5) Listar órdenes con detalles y total calculado por orden
SELECT o.order_id, o.order_date, o.payment_method, o.tip_amount, o.total_amount,
       r.reservation_id, c.first_name + ' ' + c.last_name AS customer_name
FROM [order] o
LEFT JOIN reservation r ON o.reservation_reservation_id = r.reservation_id
LEFT JOIN customer c ON r.customer_customer_id = c.customer_id;
GO

-- 6) Listar order_detail con producto
SELECT od.detail_id, od.order_order_id, od.product_product_id, p.name, od.quantity, od.price_at_purchase,
       od.quantity * od.price_at_purchase AS line_total
FROM order_detail od
JOIN product p ON od.product_product_id = p.product_id;
GO

-- 7) Total vendido por método de pago (agrupado)
SELECT o.payment_method, SUM(o.total_amount) AS total_vendido, COUNT(o.order_id) AS num_ordenes
FROM [order] o
GROUP BY o.payment_method;
GO

-- 8) Reservas por estado (conteo)
SELECT status, COUNT(*) AS cantidad
FROM reservation
GROUP BY status;
GO

-- ====================================================
-- FIN: Script completo
-- ====================================================
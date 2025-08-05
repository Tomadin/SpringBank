-- === CLIENTES ===
INSERT INTO clientes (id, nombre, apellido, dni, email) VALUES
(1, 'Juan', 'Castillo', '10000001', 'juan.castillo@mail.com'),
(2, 'Ana', 'Valdez', '10000002', 'ana.valdez@mail.com'),
(3, 'Carlos', 'Moreno', '10000003', 'carlos.moreno@mail.com'),
(4, 'Lucía', 'Martínez', '10000004', 'lucia.martinez@mail.com'),
(5, 'Pedro', 'Fernández', '10000005', 'pedro.fernandez@mail.com'),
(6, 'Sofía', 'Suárez', '10000006', 'sofia.suarez@mail.com'),
(7, 'Mariano', 'Torres', '10000007', 'mariano.torres@mail.com'),
(8, 'Valentina', 'Rivas', '10000008', 'valentina.rivas@mail.com'),
(9, 'Tomás', 'Alvarez', '10000009', 'tomas.alvarez@mail.com'),
(10, 'Martina', 'Vega', '10000010', 'martina.vega@mail.com');

-- === USUARIOS ===
INSERT INTO usuarios (id, username, password, rol, cliente_id) VALUES
(1, 'cliente', 'cliente', 'CLIENTE', 1),
(2, 'tyagod', '123123', 'CLIENTE', 2),
(3, 'quesito81', '123123', 'CLIENTE', 3),
(4, 'lmartinez', '123', 'CLIENTE', 4),
(5, 'pfernandez', '123', 'CLIENTE', 5),
(6, 'ssuarez', '123', 'CLIENTE', 6),
(7, 'mtorres', '123', 'CLIENTE', 7),
(8, 'vrivas', '123', 'CLIENTE', 8),
(9, 'talvarez', '123', 'CLIENTE', 9),
(10, 'mvega', '123', 'CLIENTE', 10),
(11, 'admin.brian', '123123', 'ADMIN', NULL),
(12, 'admin', 'admin', 'ADMIN', NULL);

-- === CUENTAS ===
INSERT INTO cuentas (id, fecha_apertura, numero_cuenta, saldo, tipo_cuenta, version, cliente_id) VALUES
(1, NOW(), 10000001, 10000.00, 'AHORRO', 1, 1),
(2, NOW(), 10000002, 20000.00, 'CORRIENTE', 1, 2),
(3, NOW(), 30000003, 30000.00, 'AHORRO', 1, 3),
(4, NOW(), 40000004, 40000.00, 'CORRIENTE', 1, 4),
(5, NOW(), 50000005, 50000.00, 'AHORRO', 1, 5),
(6, NOW(), 60000006, 60000.00, 'CORRIENTE', 1, 6),
(7, NOW(), 70000007, 70000.00, 'AHORRO', 1, 7),
(8, NOW(), 80000008, 80000.00, 'CORRIENTE', 1, 8),
(9, NOW(), 90000009, 90000.00, 'AHORRO', 1, 9),
(10, NOW(), 100000010, 100000.00, 'CORRIENTE', 1, 10);

-- === TRANSACCIONES (2 por cuenta) ===
INSERT INTO transacciones (id, descripcion, estado, fecha, monto, tipo, cuenta_origen_id, cuenta_destino_id) VALUES
-- cuenta 1
(1, 'Depósito inicial', 'COMPLETADA', NOW(), 10000.00, 'DEPOSITO', NULL, 1),
(2, 'Transferencia a cuenta 2', 'COMPLETADA', NOW(), 1000.00, 'TRANSFERENCIA', 1, 2),
-- cuenta 2
(3, 'Depósito inicial', 'COMPLETADA', NOW(), 20000.00, 'DEPOSITO', NULL, 2),
(4, 'Transferencia a cuenta 3', 'COMPLETADA', NOW(), 2000.00, 'TRANSFERENCIA', 2, 3),
-- cuenta 3
(5, 'Depósito inicial', 'COMPLETADA', NOW(), 30000.00, 'DEPOSITO', NULL, 3),
(6, 'Transferencia a cuenta 4', 'COMPLETADA', NOW(), 3000.00, 'TRANSFERENCIA', 3, 4),
-- cuenta 4
(7, 'Depósito inicial', 'COMPLETADA', NOW(), 40000.00, 'DEPOSITO', NULL, 4),
(8, 'Transferencia a cuenta 5', 'COMPLETADA', NOW(), 4000.00, 'TRANSFERENCIA', 4, 5),
-- cuenta 5
(9, 'Depósito inicial', 'COMPLETADA', NOW(), 50000.00, 'DEPOSITO', NULL, 5),
(10, 'Transferencia a cuenta 6', 'COMPLETADA', NOW(), 5000.00, 'TRANSFERENCIA', 5, 6),
-- cuenta 6
(11, 'Depósito inicial', 'COMPLETADA', NOW(), 60000.00, 'DEPOSITO', NULL, 6),
(12, 'Transferencia a cuenta 7', 'COMPLETADA', NOW(), 6000.00, 'TRANSFERENCIA', 6, 7),
-- cuenta 7
(13, 'Depósito inicial', 'COMPLETADA', NOW(), 70000.00, 'DEPOSITO', NULL, 7),
(14, 'Transferencia a cuenta 8', 'COMPLETADA', NOW(), 7000.00, 'TRANSFERENCIA', 7, 8),
-- cuenta 8
(15, 'Depósito inicial', 'COMPLETADA', NOW(), 80000.00, 'DEPOSITO', NULL, 8),
(16, 'Transferencia a cuenta 9', 'COMPLETADA', NOW(), 8000.00, 'TRANSFERENCIA', 8, 9),
-- cuenta 9
(17, 'Depósito inicial', 'COMPLETADA', NOW(), 90000.00, 'DEPOSITO', NULL, 9),
(18, 'Transferencia a cuenta 10', 'COMPLETADA', NOW(), 9000.00, 'TRANSFERENCIA', 9, 10),
-- cuenta 10
(19, 'Depósito inicial', 'COMPLETADA', NOW(), 100000.00, 'DEPOSITO', NULL, 10),
(20, 'Transferencia a cuenta 1', 'COMPLETADA', NOW(), 10000.00, 'TRANSFERENCIA', 10, 1);

-- === TOKENS ===
INSERT INTO tokens (id, token, tipo_token, expired, revoked, usuario_id) VALUES
(1, 'token_admin1', 'BEARER', 0, 0, 1),
(2, 'token_admin2', 'BEARER', 0, 0, 2),
(3, 'token_admin3', 'BEARER', 0, 0, 3),
(4, 'token_admin4', 'BEARER', 0, 0, 4),
(5, 'token_admin5', 'BEARER', 0, 0, 5),
(6, 'token_cliente1', 'BEARER', 0, 0, 6),
(7, 'token_cliente2', 'BEARER', 0, 0, 7),
(8, 'token_cliente3', 'BEARER', 0, 0, 8),
(9, 'token_cliente4', 'BEARER', 0, 0, 9),
(10, 'token_cliente5', 'BEARER', 0, 0, 10);

-- TOKENS SEQ (si usás H2 o alguna secuencia manual)
INSERT INTO tokens_seq (next_val) VALUES (1001);
INSERT INTO proveedor (proveedor_id, nombre, email, telefono, estado) VALUES (1, 'Proveedor A', 'proveedorA@ejemplo.com', '+5491112345678', true);
INSERT INTO proveedor (proveedor_id, nombre, email, telefono, estado) VALUES (2, 'Proveedor B', 'proveedorB@ejemplo.com', '+5491112345679', true);
INSERT INTO proveedor (proveedor_id, nombre, email, telefono, estado) VALUES (3, 'Proveedor C', 'proveedorC@ejemplo.com', '+5491112345680', true);

INSERT INTO producto(codigo,nombre,descripcion,precio,cantidad,imagen,estado,proveedor_id)VALUES("PROD003","Mouse","mod gamer",100.0,5,"https://firebasestorage.googleapis.com/v0/b/tp8poo2024.firebasestorage.app/o/imagesProducto%2Fproducto.webp?alt=media&token=df9db268-695f-4a94-a455-06426e67ff52","Disponible",1);
INSERT INTO producto(codigo,nombre,descripcion,precio,cantidad,imagen,estado,proveedor_id)VALUES("PROD002","Teclado","Inalambrico ",500.0,45,"https://firebasestorage.googleapis.com/v0/b/tp8poo2024.firebasestorage.app/o/imagesProducto%2Fproducto.webp?alt=media&token=df9db268-695f-4a94-a455-06426e67ff52","Disponible",1);

INSERT INTO cupon (id, FECHA_EXPIRACION, PORCENTAJE_DESCUENTO)VALUES (1, '2024-12-31', 15.0);

INSERT INTO cliente (id, tipo_cliente, nombre, apellido, email, celular, foto, estado, created, updated, CUPON_ID)VALUES (1, 'ESTANDAR', 'Fabricio', 'Bazan', 'fabri982010@hotmail.com', '1234567890', 'https://firebasestorage.googleapis.com/v0/b/tp8poo2024.firebasestorage.app/o/avatars%2Fdefault.webp?alt=media&token=8a77e93a-cea1-4a1a-bfb0-36c0e8bc433d', 'ACTIVO', NOW(), NOW(), 1);

INSERT INTO cliente (id, tipo_cliente, nombre, apellido, email, celular, foto, estado, created, updated, porcentaje_descuento)VALUES (2, 'PREMIUM', 'Maria', 'Magdalena', 'lamary@hotmail.com', '123456791', 'https://firebasestorage.googleapis.com/v0/b/tp8poo2024.firebasestorage.app/o/avatars%2Fdefault.webp?alt=media&token=8a77e93a-cea1-4a1a-bfb0-36c0e8bc433d', 'ACTIVO', NOW(), NOW(), 10.0);
INSERT INTO venta (cliente_id, producto_id, forma_pago, fechayhora, precio_producto) VALUES (2, 1, 'DEBITO', '2024-09-20 14:45:00', 120.75);

INSERT INTO venta (cliente_id, producto_id, forma_pago, fechayhora, precio_producto) VALUES (1, 2, 'TRANSFERENCIA', '2024-10-05 09:00:00', 90.00);

INSERT INTO venta (cliente_id, producto_id, forma_pago, fechayhora, precio_producto) VALUES (2, 2, 'CREDITO', '2024-10-10 16:15:00', 110.25);

INSERT INTO venta (cliente_id, producto_id, forma_pago, fechayhora, precio_producto) VALUES (1, 1, 'DEBITO', '2024-10-12 11:30:00', 95.50);

INSERT INTO venta (cliente_id, producto_id, forma_pago, fechayhora, precio_producto) VALUES (2, 1, 'TRANSFERENCIA', '2024-10-20 15:20:00', 105.00);

INSERT INTO venta (cliente_id, producto_id, forma_pago, fechayhora, precio_producto) VALUES (1, 2, 'CREDITO', '2024-10-25 10:00:00', 115.75);

INSERT INTO venta (cliente_id, producto_id, forma_pago, fechayhora, precio_producto) VALUES (2, 2, 'DEBITO', '2024-10-30 12:45:00', 125.00);
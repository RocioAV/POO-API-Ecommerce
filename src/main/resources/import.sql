INSERT INTO proveedor(nombre,email,telefono,estado) VALUES("coca cola","coca@gmail.com","38885156489",true);
INSERT INTO proveedor(nombre,email,telefono,estado) VALUES("Pepsi","pepsi@gmail.com","388858524785",true);

INSERT INTO producto(codigo,nombre,descripcion,precio,cantidad,imagen,estado,proveedor_id)VALUES("PROD1","PROD 1","DESCRIPCION 1",100.0,5,"URL","Disponible",1);
INSERT INTO producto(codigo,nombre,descripcion,precio,cantidad,imagen,estado,proveedor_id)VALUES("PROD2","PRODUCTO 2","DESCRIPCION 2",500.0,45,"URL","Disponible",1);

INSERT INTO cupon(porcentaje_descuento,fecha_expiracion) VALUES(10.0,"2024-12-30");
INSERT INTO cupon(porcentaje_descuento,fecha_expiracion) VALUES(10.0,"2025-01-30");

INSERT INTO cliente(nombre, apellido,email,celular,foto,estado,tipo_cliente,cupon_id,created,updated) VALUES("Fabricio","Bazan","fabri@gmail.com","3885456123","url","ACTIVO","ESTANDAR",1,now(),now());
INSERT INTO cliente(nombre, apellido,email,celular,foto,estado,tipo_cliente,porcentaje_descuento,created,updated) VALUES("Alejandro","Valle","ale@gmail.com","3885852123","url","ACTIVO","PREMIUM",50,now(),now());
CREATE TABLE IF NOT EXISTS ventas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha DATETIME NOT NULL,
    total DOUBLE NOT NULL,
    cliente_id BIGINT NULL,
    PRIMARY KEY (id),
    KEY idx_ventas_cliente (cliente_id),
    CONSTRAINT fk_ventas_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS detalle_venta (
    id BIGINT NOT NULL AUTO_INCREMENT,
    venta_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    PRIMARY KEY (id),
    KEY idx_detalle_venta_venta (venta_id),
    KEY idx_detalle_venta_producto (producto_id),
    CONSTRAINT fk_detalle_venta_venta
        FOREIGN KEY (venta_id) REFERENCES ventas(id),
    CONSTRAINT fk_detalle_venta_producto
        FOREIGN KEY (producto_id) REFERENCES productos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

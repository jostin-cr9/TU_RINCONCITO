CREATE TABLE IF NOT EXISTS movimientos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    producto_id BIGINT NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    cantidad INT NOT NULL,
    fecha DATE NOT NULL,
    motivo VARCHAR(200) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_movimientos_producto
        FOREIGN KEY (producto_id) REFERENCES productos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

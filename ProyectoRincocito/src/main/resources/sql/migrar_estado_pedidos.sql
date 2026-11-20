-- Ejecutar con la base tu_rinconcito seleccionada. Se puede ejecutar más de una vez.
SET @estado_existe = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'ventas'
      AND COLUMN_NAME = 'estado'
);

SET @sql_estado = IF(
    @estado_existe = 0,
    'ALTER TABLE ventas ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT ''RECIBIDO''',
    'SELECT ''La columna ventas.estado ya existe'''
);

PREPARE sentencia_estado FROM @sql_estado;
EXECUTE sentencia_estado;
DEALLOCATE PREPARE sentencia_estado;

SET @indice_estado_existe = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'ventas'
      AND INDEX_NAME = 'idx_ventas_estado_fecha'
);

SET @sql_indice_estado = IF(
    @indice_estado_existe = 0,
    'CREATE INDEX idx_ventas_estado_fecha ON ventas (estado, fecha)',
    'SELECT ''El índice de pedidos ya existe'''
);

PREPARE sentencia_indice_estado FROM @sql_indice_estado;
EXECUTE sentencia_indice_estado;
DEALLOCATE PREPARE sentencia_indice_estado;

package com.ProyectoRincocito.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ProyectoRincocito.Modelo.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    boolean existsByProducto_Id(Long productoId);
}

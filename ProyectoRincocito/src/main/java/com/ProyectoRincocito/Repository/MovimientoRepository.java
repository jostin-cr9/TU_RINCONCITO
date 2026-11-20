package com.ProyectoRincocito.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ProyectoRincocito.Modelo.Movimiento;
import java.time.LocalDate;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    boolean existsByProducto_Id(Long productoId);
    long countByFechaAndTipo(LocalDate fecha, String tipo);
    List<Movimiento> findTop5ByOrderByFechaDesc();
}

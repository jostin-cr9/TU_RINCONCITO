package com.ProyectoRincocito.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ProyectoRincocito.Modelo.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    boolean existsByProducto_Id(Long productoId);
}

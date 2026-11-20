package com.ProyectoRincocito.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import com.ProyectoRincocito.Modelo.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    @EntityGraph(attributePaths = {"detalles", "detalles.producto"})
    List<Venta> findAllByCliente_IdOrderByFechaDesc(Long clienteId);
}

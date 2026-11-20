package com.ProyectoRincocito.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDateTime;
import com.ProyectoRincocito.Modelo.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    @Query("select coalesce(sum(v.total), 0.0) from Venta v where v.fecha >= :inicio and v.fecha < :fin")
    Double sumarVentasEntre(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @EntityGraph(attributePaths = {"detalles", "detalles.producto"})
    List<Venta> findAllByCliente_IdOrderByFechaDesc(Long clienteId);
}

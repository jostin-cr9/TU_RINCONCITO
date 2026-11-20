package com.ProyectoRincocito.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;
import com.ProyectoRincocito.Modelo.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    long countByStockLessThan(int stock);
    List<Producto> findTop10ByStockLessThanOrderByStockAsc(int stock);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);
}

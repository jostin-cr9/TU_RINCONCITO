package com.ProyectoRincocito.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ProyectoRincocito.Modelo.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}

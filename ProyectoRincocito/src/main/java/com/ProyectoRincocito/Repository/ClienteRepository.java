package com.ProyectoRincocito.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.ProyectoRincocito.Modelo.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByDocumento(String documento);
    boolean existsByCorreoIgnoreCase(String correo);
    Optional<Cliente> findByCorreoIgnoreCase(String correo);
}

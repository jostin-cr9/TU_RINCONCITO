package com.ProyectoRincocito.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ProyectoRincocito.Modelo.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
}

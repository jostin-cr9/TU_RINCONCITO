package com.ProyectoRincocito.Repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ProyectoRincocito.Modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);

}

package com.ProyectoRincocito.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ProyectoRincocito.Modelo.Usuario;
import com.ProyectoRincocito.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminBootstrap(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.username:}") String username,
            @Value("${app.bootstrap-admin.password:}") String password) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean usuarioConfigurado = StringUtils.hasText(username);
        boolean claveConfigurada = StringUtils.hasText(password);
        if (!usuarioConfigurado && !claveConfigurada) {
            return;
        }
        if (!usuarioConfigurado || !claveConfigurada) {
            throw new IllegalStateException("Configura APP_BOOTSTRAP_ADMIN_USERNAME y APP_BOOTSTRAP_ADMIN_PASSWORD juntas.");
        }
        if (password.length() < 12) {
            throw new IllegalStateException("La contraseña inicial del administrador debe tener al menos 12 caracteres.");
        }

        if (usuarioRepository.existsByUsernameIgnoreCase(username.trim())) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setUsername(username.trim());
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRol("ADMIN");
        admin.setActivo(true);
        usuarioRepository.save(admin);
    }
}

package com.ProyectoRincocito.Config;

import java.util.Locale;

import com.ProyectoRincocito.Repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/registro-cliente", "/img/**")
                .permitAll()
                .requestMatchers("/pedidos/**").hasRole("CLIENTE")
                // Solo el admin y el empleado pueden entrar a la intranet
                .anyRequest().hasAnyRole("ADMIN", "EMPLEADO")
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("usuario")
                .passwordParameter("clave")
                .successHandler((request, response, authentication) -> {
                    boolean esEmpleado = authentication.getAuthorities().stream()
                        .anyMatch(autoridad ->
                            autoridad.getAuthority().equals("ROLE_ADMIN")
                            || autoridad.getAuthority().equals("ROLE_EMPLEADO"));

                    String destino = esEmpleado ? "/dashboard" : "/";
                    response.sendRedirect(request.getContextPath() + destino);
                })
                .failureHandler((request, response, exception) -> {
                    String destino = "tienda".equals(request.getParameter("origen"))
                        ? "/?loginError=true"
                        : "/login?error=true";
                    response.sendRedirect(request.getContextPath() + destino);
                })
                .permitAll()
            )
            
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return nombreUsuario -> usuarioRepository.findByUsernameIgnoreCase(nombreUsuario.trim())
            .map(usuario -> {
                String rol = usuario.getRol();

                String autoridad = (rol == null || rol.isBlank())
                    ? "ROLE_USER"
                    : (rol.toUpperCase(Locale.ROOT).startsWith("ROLE_")
                        ? rol.toUpperCase(Locale.ROOT)
                        : "ROLE_" + rol.toUpperCase(Locale.ROOT));

                return User.withUsername(usuario.getUsername())
                    .password(usuario.getPassword())
                    .authorities(new SimpleGrantedAuthority(autoridad))
                    .disabled(!usuario.isActivo())
                    .build();
            })
            .orElseThrow(() ->
                new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario));
    }
}

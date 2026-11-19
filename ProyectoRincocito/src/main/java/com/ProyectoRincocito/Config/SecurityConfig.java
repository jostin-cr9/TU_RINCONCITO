package com.ProyectoRincocito.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.ProyectoRincocito.Repository.UsuarioRepository;

import java.util.Locale;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/login", "/registro-cliente",
                    "/img/**", "/css/**", "/js/**", "/webjars/**", "/favicon.ico"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("usuario")
                .passwordParameter("clave")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> usuarioRepository.findByUsername(username)
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
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}

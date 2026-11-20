package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoRincocito.Repository.UsuarioRepository;

@Controller
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios";
    }

    @PostMapping("/usuarios/vendedor")
    public String crearVendedor(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmarPassword,
            RedirectAttributes redirectAttributes) {
        String usuario = username.trim().toLowerCase(java.util.Locale.ROOT);
        if (usuario.isBlank() || !usuario.contains("@")) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Escribe un correo válido para el vendedor.");
        } else if (password.length() < 12) {
            redirectAttributes.addFlashAttribute("errorUsuario", "La contraseña debe tener al menos 12 caracteres.");
        } else if (!password.equals(confirmarPassword)) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Las contraseñas no coinciden.");
        } else if (usuarioRepository.existsByUsernameIgnoreCase(usuario)) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Ya existe una cuenta con ese correo.");
        } else {
            com.ProyectoRincocito.Modelo.Usuario vendedor = new com.ProyectoRincocito.Modelo.Usuario();
            vendedor.setUsername(usuario);
            vendedor.setPassword(passwordEncoder.encode(password));
            vendedor.setRol("VENDEDOR");
            vendedor.setActivo(true);
            usuarioRepository.save(vendedor);
            redirectAttributes.addFlashAttribute("mensajeUsuario", "Cuenta de vendedor creada correctamente.");
        }
        return "redirect:/usuarios";
    }
}

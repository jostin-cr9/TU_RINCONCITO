package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsuarioController {
    @GetMapping("/usuarios")
    public String usuarios() { return "usuarios"; }
}

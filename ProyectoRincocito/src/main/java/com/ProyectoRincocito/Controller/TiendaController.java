package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TiendaController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String modulo, Model model) {
        model.addAttribute("modulo", modulo);
        return "login";
    }

@PostMapping("/login")
public String procesarLogin(
        @RequestParam String usuario,
        @RequestParam String clave,
        Model model) {


    if (usuario.equals("admin") && clave.equals("admin123")) {
        return "redirect:/dashboard";
    }

    model.addAttribute("error", "Usuario o contraseña incorrectos");
    model.addAttribute("modulo", "Intranet");

    return "login";
}

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/productos")
    public String productos() {
        return "productos";
    }

    @GetMapping("/inventario")
    public String inventario() {
        return "inventario";
    }

    @GetMapping("/movimientos")
    public String movimientos() {
        return "movimientos";
    }

    @GetMapping("/ventas")
    public String ventas() {
        return "ventas";
    }

    @GetMapping("/reportes")
    public String reportes() {
        return "reportes";          
    }    

    @GetMapping("/usuarios")
    public String usuarios() {
        return "usuarios";
    }
    
    @GetMapping({"/clientes"})
    public String clientes() {
        return "cliente";
    }

    @GetMapping("/proveedores")
    public String proveedores() {
        return "proveedores";
    }

}


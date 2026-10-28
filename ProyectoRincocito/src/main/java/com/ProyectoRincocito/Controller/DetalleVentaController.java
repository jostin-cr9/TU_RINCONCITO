package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ProyectoRincocito.Repository.DetalleVentaRepository;

@Controller
public class DetalleVentaController {
    private final DetalleVentaRepository repository;

    public DetalleVentaController(DetalleVentaRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/detalles-venta/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/ventas";
    }
}

package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ProyectoRincocito.Modelo.Proveedor;
import com.ProyectoRincocito.Repository.ProveedorRepository;

@Controller
public class ProveedorController {
    private final ProveedorRepository repository;

    public ProveedorController(ProveedorRepository repository) { this.repository = repository; }

    @GetMapping("/proveedores")
    public String listar(Model model) {
        model.addAttribute("proveedores", repository.findAll());
        return "proveedores";
    }

    @PostMapping("/proveedores/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor) {
        repository.save(proveedor);
        return "redirect:/proveedores";
    }

    @PostMapping("/proveedores/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/proveedores";
    }
}

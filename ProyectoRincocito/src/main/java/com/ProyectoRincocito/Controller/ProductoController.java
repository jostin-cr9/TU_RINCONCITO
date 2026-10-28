package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ProyectoRincocito.Modelo.Producto;
import com.ProyectoRincocito.Repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class ProductoController {
    private final ProductoRepository repository;

    public ProductoController(ProductoRepository repository) { this.repository = repository; }

    @GetMapping("/productos")
    public String listar(Model model) {
        model.addAttribute("productos", repository.findAll());
        return "productos";
    }

    @PostMapping("/productos/guardar")
    public String guardar( @Valid @ModelAttribute("producto") Producto producto,
            BindingResult resultado,
            Model model) {

        if (resultado.hasErrors()) {
            model.addAttribute("productos", repository.findAll());
            model.addAttribute("mostrarFormulario", true);
            return "productos";
        }
        repository.save(producto);
        return "redirect:/productos";
    }

    @PostMapping("/productos/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/productos";
    }
}

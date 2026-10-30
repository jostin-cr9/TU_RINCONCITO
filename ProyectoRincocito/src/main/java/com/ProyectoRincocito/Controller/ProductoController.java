package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ProyectoRincocito.Modelo.Producto;
import com.ProyectoRincocito.Repository.DetalleVentaRepository;
import com.ProyectoRincocito.Repository.MovimientoRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;

import jakarta.validation.Valid;

@Controller
public class ProductoController {
    private final ProductoRepository repository;
    private final MovimientoRepository movimientoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public ProductoController(ProductoRepository repository,
            MovimientoRepository movimientoRepository,
            DetalleVentaRepository detalleVentaRepository) {
        this.repository = repository;
        this.movimientoRepository = movimientoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @GetMapping("/productos")
    public String listar(Model model) {
        model.addAttribute("productos", repository.findAll());
        model.addAttribute("producto", new Producto());
        return "productos";
    }

    @GetMapping("/productos/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("productos", repository.findAll());
        model.addAttribute("producto", repository.findById(id).orElseThrow());
        model.addAttribute("mostrarFormulario", true);
        return "productos";
    }

    @PostMapping("/productos/guardar")
    public String guardar(
            @Valid @ModelAttribute("producto") Producto producto,
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
        if (movimientoRepository.existsByProducto_Id(id) || detalleVentaRepository.existsByProducto_Id(id)) {
            return "redirect:/productos?error=movimientos";
        }
        repository.deleteById(id);
        return "redirect:/productos";
    }
}

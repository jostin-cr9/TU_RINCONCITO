package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ProyectoRincocito.Repository.ProductoRepository;

@Controller
public class InventarioController {
    private final ProductoRepository productoRepository;

    public InventarioController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/inventario")
    public String inventario(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "inventario";
    }

    @GetMapping("/movimientos")
    public String movimientos(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "movimientos";
    }
}

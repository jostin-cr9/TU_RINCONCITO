package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ProyectoRincocito.Repository.DetalleVentaRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;
import com.ProyectoRincocito.Repository.VentaRepository;

@Controller
public class ReporteController {
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleRepository;
    private final ProductoRepository productoRepository;

    public ReporteController(VentaRepository ventaRepository, DetalleVentaRepository detalleRepository,
            ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("ventas", ventaRepository.findAll());
        model.addAttribute("detallesVenta", detalleRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        return "reportes";
    }
}

package com.ProyectoRincocito.Controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoRincocito.Modelo.DetalleVenta;
import com.ProyectoRincocito.Modelo.Producto;
import com.ProyectoRincocito.Modelo.Venta;
import com.ProyectoRincocito.Repository.ClienteRepository;
import com.ProyectoRincocito.Repository.DetalleVentaRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;
import com.ProyectoRincocito.Repository.VentaRepository;

@Controller
public class VentaController {
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public VentaController(VentaRepository ventaRepository, DetalleVentaRepository detalleRepository,
            ClienteRepository clienteRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleRepository = detalleRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping("/ventas")
    public String listar(Model model) {
        model.addAttribute("ventas", ventaRepository.findAll());
        model.addAttribute("detallesVenta", detalleRepository.findAll());
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        return "ventas";
    }

    @Transactional
    @PostMapping("/ventas/guardar")
    public String guardar(@RequestParam(required = false) Long clienteId,
            @RequestParam Long productoId, @RequestParam Integer cantidad,
            @RequestParam LocalDate fecha) {
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        if (cantidad < 1 || producto.getStock() < cantidad) return "redirect:/ventas?error=stock";

        Venta venta = new Venta();
        venta.setFecha(fecha.atStartOfDay());
        venta.setCliente(clienteId == null ? null : clienteRepository.findById(clienteId).orElseThrow());
        venta.setTotal(producto.getPrecio() * cantidad);
        venta = ventaRepository.save(venta);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setSubtotal(venta.getTotal());
        detalleRepository.save(detalle);

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
        return "redirect:/ventas";
    }

}

package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ProyectoRincocito.Modelo.DetalleVenta;
import com.ProyectoRincocito.Modelo.Producto;
import com.ProyectoRincocito.Modelo.Venta;
import com.ProyectoRincocito.Repository.DetalleVentaRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;
import com.ProyectoRincocito.Repository.VentaRepository;

@Controller
public class DetalleVentaController {
    private final DetalleVentaRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    public DetalleVentaController(DetalleVentaRepository detalleRepository,
            ProductoRepository productoRepository,
            VentaRepository ventaRepository) {
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional
    @PostMapping("/detalles-venta/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        DetalleVenta detalle = detalleRepository.findById(id).orElse(null);
        if (detalle == null) {
            return "redirect:/ventas";
        }

        Producto producto = detalle.getProducto();
        producto.setStock(producto.getStock() + detalle.getCantidad());
        productoRepository.save(producto);

        Venta venta = detalle.getVenta();
        double totalActualizado = venta.getDetalles().stream()
                .filter(item -> !item.getId().equals(id))
                .mapToDouble(DetalleVenta::getSubtotal)
                .sum();
        venta.setTotal(totalActualizado);

        detalleRepository.delete(detalle);
        ventaRepository.save(venta);
        return "redirect:/ventas";
    }
}

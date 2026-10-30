package com.ProyectoRincocito.Controller;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ProyectoRincocito.Modelo.DetalleVenta;
import com.ProyectoRincocito.Modelo.Movimiento;
import com.ProyectoRincocito.Modelo.Producto;
import com.ProyectoRincocito.Modelo.Venta;
import com.ProyectoRincocito.Repository.DetalleVentaRepository;
import com.ProyectoRincocito.Repository.MovimientoRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;
import com.ProyectoRincocito.Repository.VentaRepository;

@Controller
public class DetalleVentaController {
    private final DetalleVentaRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final MovimientoRepository movimientoRepository;

    public DetalleVentaController(DetalleVentaRepository detalleRepository,
            ProductoRepository productoRepository,
            VentaRepository ventaRepository,
            MovimientoRepository movimientoRepository) {
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.movimientoRepository = movimientoRepository;
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
        Movimiento devolucion = new Movimiento();
        devolucion.setProducto(producto);
        devolucion.setTipo("Entrada");
        devolucion.setCantidad(detalle.getCantidad());
        devolucion.setFecha(LocalDate.now(ZoneId.of("America/Bogota")));
        devolucion.setMotivo("Devolución por eliminación del detalle de venta #" + venta.getId());
        movimientoRepository.save(devolucion);

        double totalActualizado = venta.getDetalles().stream()
                .filter(item -> !item.getId().equals(id))
                .mapToDouble(DetalleVenta::getSubtotal)
                .sum();
        venta.setTotal(totalActualizado);

        venta.getDetalles().removeIf(item -> item.getId().equals(id));
        ventaRepository.save(venta);
        return "redirect:/ventas";
    }
}

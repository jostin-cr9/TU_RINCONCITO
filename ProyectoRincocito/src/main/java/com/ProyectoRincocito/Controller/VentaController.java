package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ProyectoRincocito.Modelo.DetalleVenta;
import com.ProyectoRincocito.Modelo.Movimiento;
import com.ProyectoRincocito.Modelo.Producto;
import com.ProyectoRincocito.Modelo.Venta;
import com.ProyectoRincocito.Modelo.VentaFormulario;
import com.ProyectoRincocito.Repository.ClienteRepository;
import com.ProyectoRincocito.Repository.DetalleVentaRepository;
import com.ProyectoRincocito.Repository.MovimientoRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;
import com.ProyectoRincocito.Repository.VentaRepository;

import jakarta.validation.Valid;

@Controller
public class VentaController {
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    public VentaController(VentaRepository ventaRepository,
            DetalleVentaRepository detalleRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            MovimientoRepository movimientoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleRepository = detalleRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @GetMapping("/ventas")
    public String listar(Model model) {
        cargarDatos(model);
        model.addAttribute("ventaFormulario", new VentaFormulario());
        return "ventas";
    }

    @Transactional
    @PostMapping("/ventas/guardar")
    public String guardar(
            @Valid @ModelAttribute("ventaFormulario") VentaFormulario formulario,
            BindingResult resultado,
            Model model) {

        if (!resultado.hasErrors()) {
            Producto producto = productoRepository.findById(formulario.getProductoId()).orElse(null);
            if (producto == null) {
                resultado.rejectValue("productoId", "producto.inexistente", "El producto seleccionado no existe");
            } else if (producto.getStock() < formulario.getCantidad()) {
                resultado.rejectValue("cantidad", "stock.insuficiente", "No hay stock suficiente para esa cantidad");
            }

            if (formulario.getClienteId() != null
                    && !clienteRepository.existsById(formulario.getClienteId())) {
                resultado.rejectValue("clienteId", "cliente.inexistente", "El cliente seleccionado no existe");
            }
        }

        if (resultado.hasErrors()) {
            cargarDatos(model);
            model.addAttribute("mostrarFormulario", true);
            return "ventas";
        }

        Producto producto = productoRepository.findById(formulario.getProductoId()).orElseThrow();
        Venta venta = new Venta();
        venta.setFecha(formulario.getFecha().atStartOfDay());
        if (formulario.getClienteId() != null) {
            venta.setCliente(clienteRepository.findById(formulario.getClienteId()).orElseThrow());
        }
        venta.setTotal(producto.getPrecio() * formulario.getCantidad());
        venta = ventaRepository.save(venta);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(formulario.getCantidad());
        detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setSubtotal(venta.getTotal());
        detalleRepository.save(detalle);

        producto.setStock(producto.getStock() - formulario.getCantidad());
        productoRepository.save(producto);

        Movimiento movimiento = new Movimiento();
        movimiento.setProducto(producto);
        movimiento.setTipo("Salida");
        movimiento.setCantidad(formulario.getCantidad());
        movimiento.setFecha(formulario.getFecha());
        movimiento.setMotivo("Venta #" + venta.getId());
        movimientoRepository.save(movimiento);
        return "redirect:/ventas";
    }

    private void cargarDatos(Model model) {
        model.addAttribute("ventas", ventaRepository.findAll());
        model.addAttribute("detallesVenta", detalleRepository.findAll());
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
    }
}

package com.ProyectoRincocito.Controller;

import java.util.Comparator;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ProyectoRincocito.Modelo.Movimiento;
import com.ProyectoRincocito.Modelo.MovimientoFormulario;
import com.ProyectoRincocito.Modelo.Producto;
import com.ProyectoRincocito.Repository.MovimientoRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;

import jakarta.validation.Valid;

@Controller
public class InventarioController {
    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    public InventarioController(ProductoRepository productoRepository, MovimientoRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @GetMapping("/inventario")
    public String inventario(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("stockMinimo", 10);
        return "inventario";
    }

    @GetMapping("/movimientos")
    public String movimientos(Model model) {
        cargarDatosMovimiento(model);
        model.addAttribute("movimientoFormulario", new MovimientoFormulario());
        return "movimientos";
    }

    @Transactional
    @PostMapping("/movimientos/guardar")
    public String guardarMovimiento(
            @Valid @ModelAttribute("movimientoFormulario") MovimientoFormulario formulario,
            BindingResult resultado,
            Model model) {

        Producto producto = null;
        if (!resultado.hasErrors()) {
            producto = productoRepository.findById(formulario.getProductoId()).orElse(null);
            if (producto == null) {
                resultado.rejectValue("productoId", "producto.inexistente", "El producto seleccionado no existe");
            } else if (!formulario.getTipo().equals("Entrada") && !formulario.getTipo().equals("Salida")) {
                resultado.rejectValue("tipo", "movimiento.tipo.invalido", "El tipo debe ser Entrada o Salida");
            } else if (formulario.getTipo().equals("Salida")
                    && formulario.getCantidad() > producto.getStock()) {
                resultado.rejectValue("cantidad", "stock.insuficiente", "No hay stock suficiente para esa salida");
            }
        }

        if (resultado.hasErrors()) {
            cargarDatosMovimiento(model);
            model.addAttribute("mostrarFormulario", true);
            return "movimientos";
        }

        if (formulario.getTipo().equals("Entrada")) {
            producto.setStock(producto.getStock() + formulario.getCantidad());
        } else {
            producto.setStock(producto.getStock() - formulario.getCantidad());
        }
        productoRepository.save(producto);

        Movimiento movimiento = new Movimiento();
        movimiento.setProducto(producto);
        movimiento.setTipo(formulario.getTipo());
        movimiento.setCantidad(formulario.getCantidad());
        movimiento.setFecha(formulario.getFecha());
        movimiento.setMotivo(formulario.getMotivo().trim());
        movimientoRepository.save(movimiento);
        return "redirect:/movimientos";
    }

    private void cargarDatosMovimiento(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("movimientos", movimientoRepository.findAll().stream()
                .sorted(Comparator.comparing(Movimiento::getFecha).reversed())
                .toList());
    }
}

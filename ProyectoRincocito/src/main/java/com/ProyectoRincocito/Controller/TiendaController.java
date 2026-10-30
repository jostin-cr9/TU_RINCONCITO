package com.ProyectoRincocito.Controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ProyectoRincocito.Modelo.Movimiento;
import com.ProyectoRincocito.Modelo.Venta;
import com.ProyectoRincocito.Repository.MovimientoRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;
import com.ProyectoRincocito.Repository.VentaRepository;

@Controller
public class TiendaController {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final MovimientoRepository movimientoRepository;

    public TiendaController(ProductoRepository productoRepository,
            VentaRepository ventaRepository,
            MovimientoRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @GetMapping("/")
    public String inicio() { return "index"; }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String modulo, Model model) {
        model.addAttribute("modulo", modulo);
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario, @RequestParam String clave, Model model) {
        if (usuario.equals("admin") && clave.equals("admin123")) return "redirect:/dashboard";
        model.addAttribute("error", "Usuario o contraseña incorrectos");
        model.addAttribute("modulo", "Intranet");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Bogota"));
        var productos = productoRepository.findAll();
        var productosStockBajo = productos.stream()
                .filter(producto -> producto.getStock() < 10)
                .toList();
        double totalVentasHoy = ventaRepository.findAll().stream()
                .filter(venta -> venta.getFecha() != null && venta.getFecha().toLocalDate().equals(hoy))
                .mapToDouble(Venta::getTotal)
                .sum();
        var movimientos = movimientoRepository.findAll();
        long entradasHoy = movimientos.stream()
                .filter(movimiento -> movimiento.getFecha().equals(hoy)
                        && movimiento.getTipo().equals("Entrada"))
                .count();
        var movimientosRecientes = movimientos.stream()
                .sorted(Comparator.comparing(Movimiento::getFecha).reversed())
                .limit(5)
                .toList();

        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("productosStockBajo", productosStockBajo);
        model.addAttribute("cantidadStockBajo", productosStockBajo.size());
        model.addAttribute("totalVentasHoy", totalVentasHoy);
        model.addAttribute("entradasHoy", entradasHoy);
        model.addAttribute("movimientosRecientes", movimientosRecientes);
        return "dashboard";
    }
}

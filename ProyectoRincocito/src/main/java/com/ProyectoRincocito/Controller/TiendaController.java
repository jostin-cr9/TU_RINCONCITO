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
import com.ProyectoRincocito.Modelo.Cliente;
import com.ProyectoRincocito.Modelo.Venta;
import com.ProyectoRincocito.Repository.ClienteRepository;
import com.ProyectoRincocito.Repository.MovimientoRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;
import com.ProyectoRincocito.Repository.VentaRepository;

@Controller
public class TiendaController {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final MovimientoRepository movimientoRepository;
    private final ClienteRepository clienteRepository;

    public TiendaController(ProductoRepository productoRepository,
            VentaRepository ventaRepository,
            MovimientoRepository movimientoRepository,
            ClienteRepository clienteRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.movimientoRepository = movimientoRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/")
    public String inicio() { return "index"; }

    @PostMapping("/registro-cliente")
    public String registrarCliente(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String documento,
            @RequestParam(required = false) String telefono,
            @RequestParam String correo,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String confirmarPassword,
            @RequestParam(required = false) String aceptarTerminos,
            Model model) {
        String error = null;
        if (nombre.isBlank() || apellido.isBlank() || documento.isBlank() || correo.isBlank()) {
            error = "Completa todos los campos obligatorios.";
        } else if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            error = "Ingresa un correo electrónico válido.";
        } else if (password == null || password.length() < 8) {
            error = "La contraseña debe tener al menos 8 caracteres.";
        } else if (!password.equals(confirmarPassword)) {
            error = "Las contraseñas no coinciden.";
        } else if (aceptarTerminos == null) {
            error = "Debes aceptar los términos y condiciones.";
        } else if (clienteRepository.existsByDocumento(documento.trim())) {
            error = "Ya existe un cliente registrado con ese documento.";
        }

        if (error != null) {
            model.addAttribute("registroError", error);
            model.addAttribute("abrirRegistro", true);
            return "index";
        }

        Cliente cliente = new Cliente();
        cliente.setNombre((nombre.trim() + " " + apellido.trim()).trim());
        cliente.setDocumento(documento.trim());
        cliente.setTelefono(telefono == null ? null : telefono.trim());
        cliente.setCorreo(correo.trim());
        clienteRepository.save(cliente);
        return "redirect:/?registro=exitoso";
    }

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

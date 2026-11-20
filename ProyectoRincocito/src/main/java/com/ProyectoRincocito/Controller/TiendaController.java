package com.ProyectoRincocito.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.ProyectoRincocito.Modelo.Producto;

import com.ProyectoRincocito.Modelo.Movimiento;
import com.ProyectoRincocito.Modelo.Cliente;
import com.ProyectoRincocito.Modelo.DetalleVenta;
import com.ProyectoRincocito.Modelo.Venta;
import com.ProyectoRincocito.Repository.ClienteRepository;
import com.ProyectoRincocito.Repository.MovimientoRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;
import com.ProyectoRincocito.Repository.VentaRepository;

import java.util.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoRincocito.Modelo.Usuario;
import com.ProyectoRincocito.Repository.UsuarioRepository;

@Controller
public class TiendaController {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final MovimientoRepository movimientoRepository;
    private final ClienteRepository clienteRepository;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

       public TiendaController(
        ProductoRepository productoRepository,
        VentaRepository ventaRepository,
        MovimientoRepository movimientoRepository,
        ClienteRepository clienteRepository,
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder) {

    this.productoRepository = productoRepository;
    this.ventaRepository = ventaRepository;
    this.movimientoRepository = movimientoRepository;
    this.clienteRepository = clienteRepository;
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
}

    @GetMapping("/")
    public String inicio(Model model, Authentication authentication) {
        model.addAttribute("productosCatalogo", productoRepository.findAll());
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENTE"))) {
            clienteRepository.findByCorreoIgnoreCase(authentication.getName()).ifPresent(cliente -> {
                model.addAttribute("clienteActual", cliente);
                model.addAttribute("misVentas", ventaRepository.findAllByCliente_IdOrderByFechaDesc(cliente.getId()));
            });
        }
        return "index";
    }
    
    @GetMapping("/registro-cliente")
    public String formularioRegistro() {
        return "registro";
    }

@Transactional
@PostMapping("/registro-cliente")
public String registrarCliente(
        @RequestParam String nombre,
        @RequestParam String apellido,
        @RequestParam String documento,
        @RequestParam(required = false) String telefono,
        @RequestParam String correo,
        @RequestParam String password,
        @RequestParam String confirmarPassword,
        @RequestParam(required = false) String aceptarTerminos,
        Model model) {

    String correoNormalizado = correo.trim().toLowerCase(Locale.ROOT);
    String error = null;

    if (nombre.isBlank() || apellido.isBlank()
            || documento.isBlank() || correo.isBlank()) {
        error = "Completa todos los campos obligatorios.";
    } else if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
        error = "Ingresa un correo electrónico válido.";
    } else if (password.length() < 8) {
        error = "La contraseña debe tener al menos 8 caracteres.";
    } else if (!password.equals(confirmarPassword)) {
        error = "Las contraseñas no coinciden.";
    } else if (aceptarTerminos == null) {
        error = "Debes aceptar los términos y condiciones.";
    } else if (clienteRepository.existsByDocumento(documento.trim())) {
        error = "Ya existe un cliente con ese documento.";
    } else if (clienteRepository.existsByCorreoIgnoreCase(correoNormalizado)
            || usuarioRepository.existsByUsernameIgnoreCase(correoNormalizado)) {
        error = "Ya existe una cuenta con ese correo.";
    }

    if (error != null) {
        model.addAttribute("registroError", error);
        return "registro";
    }

    Cliente cliente = new Cliente();
    cliente.setNombre((nombre.trim() + " " + apellido.trim()).trim());
    cliente.setDocumento(documento.trim());
    cliente.setTelefono(telefono == null ? null : telefono.trim());
    cliente.setCorreo(correoNormalizado);
    clienteRepository.save(cliente);

    Usuario usuario = new Usuario();
    usuario.setUsername(correoNormalizado);
    usuario.setPassword(passwordEncoder.encode(password));
    usuario.setRol("CLIENTE");
    usuario.setActivo(true);
    usuarioRepository.save(usuario);

    return "redirect:/?registro=exitoso";
}

    @Transactional
    @PostMapping("/pedidos/crear")
    public String crearPedido(
            @RequestParam(required = false) List<Long> productoIds,
            @RequestParam(required = false) List<Integer> cantidades,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (productoIds == null || cantidades == null || productoIds.isEmpty()
                || productoIds.size() > 100
                || productoIds.size() != cantidades.size()) {
            redirectAttributes.addFlashAttribute("pedidoError", "Revisa los productos de tu carrito.");
            return "redirect:/#catalogo";
        }

        Cliente cliente = clienteRepository.findByCorreoIgnoreCase(authentication.getName()).orElse(null);
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("pedidoError", "No encontramos el perfil de cliente asociado a tu cuenta.");
            return "redirect:/";
        }

        Map<Long, Integer> cantidadesPorProducto = new TreeMap<>();
        for (int i = 0; i < productoIds.size(); i++) {
            Long productoId = productoIds.get(i);
            Integer cantidad = cantidades.get(i);
            if (productoId == null || cantidad == null || cantidad < 1 || cantidad > 100) {
                redirectAttributes.addFlashAttribute("pedidoError", "Revisa las cantidades de tu carrito.");
                return "redirect:/#catalogo";
            }
            cantidadesPorProducto.merge(productoId, cantidad, Integer::sum);
            if (cantidadesPorProducto.get(productoId) > 100) {
                redirectAttributes.addFlashAttribute("pedidoError", "La cantidad máxima por producto es 100.");
                return "redirect:/#catalogo";
            }
        }

        List<Producto> productosPedido = new ArrayList<>();
        double totalPedido = 0;
        for (Map.Entry<Long, Integer> item : cantidadesPorProducto.entrySet()) {
            Producto producto = productoRepository.findByIdForUpdate(item.getKey()).orElse(null);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("pedidoError", "Uno de los productos ya no está disponible.");
                return "redirect:/#catalogo";
            }
            if (producto.getStock() < item.getValue()) {
                redirectAttributes.addFlashAttribute("pedidoError", "Stock insuficiente para: " + producto.getNombre());
                return "redirect:/#catalogo";
            }
            productosPedido.add(producto);
            totalPedido += producto.getPrecio() * item.getValue();
        }

        Venta venta = new Venta();
        venta.setFecha(java.time.LocalDateTime.now(ZoneId.of("America/Bogota")));
        venta.setCliente(cliente);
        venta.setTotal(totalPedido);

        for (int i = 0; i < productosPedido.size(); i++) {
            Producto producto = productosPedido.get(i);
            int cantidad = cantidadesPorProducto.get(producto.getId());
            DetalleVenta detalle = new com.ProyectoRincocito.Modelo.DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio() * cantidad);
            venta.getDetalles().add(detalle);
        }

        venta = ventaRepository.save(venta);
        for (Producto producto : productosPedido) {
            int cantidad = cantidadesPorProducto.get(producto.getId());
            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);

            Movimiento movimiento = new Movimiento();
            movimiento.setProducto(producto);
            movimiento.setTipo("Salida");
            movimiento.setCantidad(cantidad);
            movimiento.setFecha(LocalDate.now(ZoneId.of("America/Bogota")));
            movimiento.setMotivo("Pedido web #" + venta.getId());
            movimientoRepository.save(movimiento);
        }

        redirectAttributes.addFlashAttribute("pedidoId", venta.getId());
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String modulo, Model model) {
        model.addAttribute("modulo", modulo);
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Bogota"));
        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = hoy.plusDays(1).atStartOfDay();
        var productosStockBajo = productoRepository.findTop10ByStockLessThanOrderByStockAsc(10);
        double totalVentasHoy = ventaRepository.sumarVentasEntre(inicioDia, finDia);
        long entradasHoy = movimientoRepository.countByFechaAndTipo(hoy, "Entrada");
        var movimientosRecientes = movimientoRepository.findTop5ByOrderByFechaDesc();

        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("productosStockBajo", productosStockBajo);
        model.addAttribute("cantidadStockBajo", productoRepository.countByStockLessThan(10));
        model.addAttribute("totalVentasHoy", totalVentasHoy);
        model.addAttribute("entradasHoy", entradasHoy);
        model.addAttribute("movimientosRecientes", movimientosRecientes);
        return "dashboard";
    }
}

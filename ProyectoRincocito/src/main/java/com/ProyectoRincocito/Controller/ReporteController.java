package com.ProyectoRincocito.Controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ProyectoRincocito.Modelo.Producto;
import com.ProyectoRincocito.Repository.DetalleVentaRepository;
import com.ProyectoRincocito.Repository.MovimientoRepository;
import com.ProyectoRincocito.Repository.ProductoRepository;
import com.ProyectoRincocito.Repository.VentaRepository;

@Controller
public class ReporteController {
    private static final int STOCK_MINIMO = 10;

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    public ReporteController(VentaRepository ventaRepository,
            DetalleVentaRepository detalleRepository,
            ProductoRepository productoRepository,
            MovimientoRepository movimientoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("ventas", ventaRepository.findAll());
        model.addAttribute("detallesVenta", detalleRepository.findAll());
        model.addAttribute("productos", productos);
        model.addAttribute("productosStockBajo", productos.stream()
                .filter(producto -> producto.getStock() < STOCK_MINIMO)
                .toList());
        model.addAttribute("movimientos", movimientoRepository.findAll());
        return "reportes";
    }

    @GetMapping("/reportes/inventario.csv")
    public ResponseEntity<byte[]> descargarInventario() {
        StringBuilder csv = new StringBuilder("ID,Nombre,Categoria,Precio,Stock,Estado\r\n");
        productoRepository.findAll().forEach(producto -> csv.append(producto.getId()).append(',')
                .append(celdaCsv(producto.getNombre())).append(',')
                .append(celdaCsv(producto.getCategoria())).append(',')
                .append(producto.getPrecio()).append(',')
                .append(producto.getStock()).append(',')
                .append(estadoStock(producto.getStock())).append("\r\n"));
        return archivoCsv("inventario.csv", csv.toString());
    }

    @GetMapping("/reportes/ventas.csv")
    public ResponseEntity<byte[]> descargarVentas() {
        StringBuilder csv = new StringBuilder("Venta,Fecha,Cliente,Producto,Cantidad,Precio unitario,Subtotal\r\n");
        detalleRepository.findAll().forEach(detalle -> csv.append(detalle.getVenta().getId()).append(',')
                .append(detalle.getVenta().getFecha()).append(',')
                .append(celdaCsv(detalle.getVenta().getCliente() == null
                        ? "Cliente general" : detalle.getVenta().getCliente().getNombre())).append(',')
                .append(celdaCsv(detalle.getProducto().getNombre())).append(',')
                .append(detalle.getCantidad()).append(',')
                .append(detalle.getPrecioUnitario()).append(',')
                .append(detalle.getSubtotal()).append("\r\n"));
        return archivoCsv("ventas.csv", csv.toString());
    }

    @GetMapping("/reportes/stock-bajo.csv")
    public ResponseEntity<byte[]> descargarStockBajo() {
        StringBuilder csv = new StringBuilder("ID,Nombre,Stock,Stock minimo,Estado\r\n");
        productoRepository.findAll().stream()
                .filter(producto -> producto.getStock() < STOCK_MINIMO)
                .forEach(producto -> csv.append(producto.getId()).append(',')
                        .append(celdaCsv(producto.getNombre())).append(',')
                        .append(producto.getStock()).append(',')
                        .append(STOCK_MINIMO).append(',')
                        .append(estadoStock(producto.getStock())).append("\r\n"));
        return archivoCsv("stock-bajo.csv", csv.toString());
    }

    @GetMapping("/reportes/movimientos.csv")
    public ResponseEntity<byte[]> descargarMovimientos() {
        StringBuilder csv = new StringBuilder("ID,Fecha,Producto,Tipo,Cantidad,Motivo\r\n");
        movimientoRepository.findAll().forEach(movimiento -> csv.append(movimiento.getId()).append(',')
                .append(movimiento.getFecha()).append(',')
                .append(celdaCsv(movimiento.getProducto().getNombre())).append(',')
                .append(movimiento.getTipo()).append(',')
                .append(movimiento.getCantidad()).append(',')
                .append(celdaCsv(movimiento.getMotivo())).append("\r\n"));
        return archivoCsv("movimientos.csv", csv.toString());
    }

    private String estadoStock(Integer stock) {
        if (stock == 0) return "Agotado";
        return stock < STOCK_MINIMO ? "Stock bajo" : "Disponible";
    }

    private String celdaCsv(String valor) {
        if (valor == null) return "\"\"";
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }

    private ResponseEntity<byte[]> archivoCsv(String nombre, String contenido) {
        byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        byte[] cuerpo = contenido.getBytes(StandardCharsets.UTF_8);
        byte[] datos = new byte[bom.length + cuerpo.length];
        System.arraycopy(bom, 0, datos, 0, bom.length);
        System.arraycopy(cuerpo, 0, datos, bom.length, cuerpo.length);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(datos);
    }
}

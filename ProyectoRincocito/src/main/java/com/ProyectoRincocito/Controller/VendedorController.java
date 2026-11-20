package com.ProyectoRincocito.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoRincocito.Modelo.Venta;
import com.ProyectoRincocito.Repository.VentaRepository;

@Controller
public class VendedorController {
    private final VentaRepository ventaRepository;

    public VendedorController(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @GetMapping("/vendedor/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("pedidos", ventaRepository.findByEstadoInOrderByFechaAsc(
                List.of("PENDIENTE", "ARMADO")));
        model.addAttribute("pedidosPendientes", ventaRepository.countByEstado("PENDIENTE"));
        model.addAttribute("pedidosArmados", ventaRepository.countByEstado("ARMADO"));
        return "vendedor-pedidos";
    }

    @Transactional
    @PostMapping("/vendedor/pedidos/{id}/armar")
    public String marcarArmado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Venta pedido = ventaRepository.findById(id).orElse(null);
        if (pedido == null || !"PENDIENTE".equals(pedido.getEstado())) {
            redirectAttributes.addFlashAttribute("errorVendedor",
                    "El pedido no existe o ya fue actualizado por otro vendedor.");
            return "redirect:/vendedor/pedidos";
        }

        pedido.setEstado("ARMADO");
        ventaRepository.save(pedido);
        redirectAttributes.addFlashAttribute("mensajeVendedor",
                "Pedido #" + pedido.getId() + " marcado como armado.");
        return "redirect:/vendedor/pedidos";
    }
}

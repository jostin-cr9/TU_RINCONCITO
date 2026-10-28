package com.ProyectoRincocito.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ProyectoRincocito.Modelo.Cliente;
import com.ProyectoRincocito.Repository.ClienteRepository;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class ClienteController {
    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/clientes")
    public String listar(Model model) {
        model.addAttribute("clientes", repository.findAll());
        return "cliente";
    }

    @PostMapping("/clientes/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente,
     BindingResult resultadoBindingResult, Model model) {
        if (resultadoBindingResult.hasErrors()) {
            model.addAttribute("clientes", repository.findAll());
            model.addAttribute("mostrarFormulario", true);
            return "cliente";
        }
        repository.save(cliente);
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/clientes";
    }
}

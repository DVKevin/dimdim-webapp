package br.com.fiap.dimdim.controller;

import br.com.fiap.dimdim.service.ClienteService;
import br.com.fiap.dimdim.service.ContaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ClienteService clienteService;
    private final ContaService contaService;

    public HomeController(ClienteService clienteService, ContaService contaService) {
        this.clienteService = clienteService;
        this.contaService = contaService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalClientes", clienteService.contar());
        model.addAttribute("totalContas", contaService.contar());
        return "index";
    }
}

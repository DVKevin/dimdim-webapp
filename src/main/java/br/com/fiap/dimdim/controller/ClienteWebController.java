package br.com.fiap.dimdim.controller;

import br.com.fiap.dimdim.dto.ClienteDTO;
import br.com.fiap.dimdim.exception.NaoEncontradoException;
import br.com.fiap.dimdim.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClienteWebController {

    private final ClienteService service;

    public ClienteWebController(ClienteService service) {
        this.service = service;
    }

    @GetMapping("/clientes")
    public String listar(@RequestParam(required = false) Long editar, Model model) {
        ClienteDTO edicao = null;
        if (editar != null) {
            try {
                edicao = service.buscar(editar);
            } catch (NaoEncontradoException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        model.addAttribute("clientes", service.listar());
        model.addAttribute("edicao", edicao);
        model.addAttribute("acao", edicao == null ? "/clientes" : "/clientes/" + edicao.id());
        return "clientes";
    }

    @PostMapping("/clientes")
    public String criar(@RequestParam String nome, @RequestParam String cpf,
                        @RequestParam String email, RedirectAttributes ra) {
        try {
            ClienteDTO criado = service.criar(new ClienteDTO(null, nome, cpf, email, null));
            ra.addFlashAttribute("msg", "Cliente #" + criado.id() + " criado com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
        }
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/{id}")
    public String atualizar(@PathVariable Long id, @RequestParam String nome, @RequestParam String cpf,
                            @RequestParam String email, RedirectAttributes ra) {
        try {
            service.atualizar(id, new ClienteDTO(id, nome, cpf, email, null));
            ra.addFlashAttribute("msg", "Cliente #" + id + " atualizado com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
        }
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("msg", "Cliente #" + id + " excluido com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
        }
        return "redirect:/clientes";
    }
}

package br.com.fiap.dimdim.controller;

import br.com.fiap.dimdim.dto.ContaDTO;
import br.com.fiap.dimdim.exception.NaoEncontradoException;
import br.com.fiap.dimdim.service.ClienteService;
import br.com.fiap.dimdim.service.ContaService;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContaWebController {

    private final ContaService service;
    private final ClienteService clienteService;

    public ContaWebController(ContaService service, ClienteService clienteService) {
        this.service = service;
        this.clienteService = clienteService;
    }

    @GetMapping("/contas")
    public String listar(@RequestParam(required = false) Long editar, Model model) {
        ContaDTO edicao = null;
        if (editar != null) {
            try {
                edicao = service.buscar(editar);
            } catch (NaoEncontradoException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        model.addAttribute("contas", service.listar());
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("edicao", edicao);
        model.addAttribute("acao", edicao == null ? "/contas" : "/contas/" + edicao.id());
        return "contas";
    }

    @PostMapping("/contas")
    public String criar(@RequestParam Long idCliente, @RequestParam String numeroConta,
                        @RequestParam String tipoConta, @RequestParam(required = false) BigDecimal saldo,
                        RedirectAttributes ra) {
        try {
            ContaDTO criada = service.criar(new ContaDTO(null, idCliente, null, numeroConta, tipoConta, saldo, null));
            ra.addFlashAttribute("msg", "Conta #" + criada.id() + " criada com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
        }
        return "redirect:/contas";
    }

    @PostMapping("/contas/{id}")
    public String atualizar(@PathVariable Long id, @RequestParam Long idCliente,
                            @RequestParam String numeroConta, @RequestParam String tipoConta,
                            @RequestParam(required = false) BigDecimal saldo, RedirectAttributes ra) {
        try {
            service.atualizar(id, new ContaDTO(id, idCliente, null, numeroConta, tipoConta, saldo, null));
            ra.addFlashAttribute("msg", "Conta #" + id + " atualizada com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
        }
        return "redirect:/contas";
    }

    @PostMapping("/contas/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("msg", "Conta #" + id + " excluida com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
        }
        return "redirect:/contas";
    }
}

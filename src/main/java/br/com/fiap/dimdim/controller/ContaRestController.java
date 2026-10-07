package br.com.fiap.dimdim.controller;

import br.com.fiap.dimdim.dto.ContaDTO;
import br.com.fiap.dimdim.service.ContaService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contas")
public class ContaRestController {

    private final ContaService service;

    public ContaRestController(ContaService service) {
        this.service = service;
    }

    @GetMapping
    public List<ContaDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ContaDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<ContaDTO> criar(@RequestBody ContaDTO dto) {
        ContaDTO criada = service.criar(dto);
        return ResponseEntity.created(URI.create("/api/contas/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    public ContaDTO atualizar(@PathVariable Long id, @RequestBody ContaDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

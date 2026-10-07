package br.com.fiap.dimdim.service;

import br.com.fiap.dimdim.dto.ClienteDTO;
import br.com.fiap.dimdim.exception.ConflitoException;
import br.com.fiap.dimdim.exception.NaoEncontradoException;
import br.com.fiap.dimdim.model.Cliente;
import br.com.fiap.dimdim.repository.ClienteRepository;
import br.com.fiap.dimdim.repository.ContaRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final ContaRepository contaRepository;

    public ClienteService(ClienteRepository repository, ContaRepository contaRepository) {
        this.repository = repository;
        this.contaRepository = contaRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> listar() {
        return repository.findAll(Sort.by("id")).stream().map(ClienteService::paraDTO).toList();
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscar(Long id) {
        return paraDTO(obter(id));
    }

    @Transactional(readOnly = true)
    public long contar() {
        return repository.count();
    }

    @Transactional
    public ClienteDTO criar(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        aplicar(cliente, dto);
        return paraDTO(repository.saveAndFlush(cliente));
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO dto) {
        Cliente cliente = obter(id);
        aplicar(cliente, dto);
        return paraDTO(repository.saveAndFlush(cliente));
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = obter(id);
        if (contaRepository.existsByClienteId(id)) {
            throw new ConflitoException("O cliente " + id + " possui contas vinculadas. Exclua as contas primeiro.");
        }
        repository.delete(cliente);
        repository.flush();
    }

    private Cliente obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Cliente " + id + " nao encontrado"));
    }

    private void aplicar(Cliente cliente, ClienteDTO dto) {
        cliente.setNome(Validacao.obrigatorio(dto.nome(), "nome", 100));
        cliente.setCpf(Validacao.obrigatorio(dto.cpf(), "cpf", 14));
        String email = Validacao.obrigatorio(dto.email(), "email", 120);
        if (!email.contains("@")) {
            throw new IllegalArgumentException("O campo 'email' e invalido");
        }
        cliente.setEmail(email);
    }

    private static ClienteDTO paraDTO(Cliente c) {
        return new ClienteDTO(c.getId(), c.getNome(), c.getCpf(), c.getEmail(), Validacao.data(c.getDtCadastro()));
    }
}

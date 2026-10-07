package br.com.fiap.dimdim.service;

import br.com.fiap.dimdim.dto.ContaDTO;
import br.com.fiap.dimdim.exception.NaoEncontradoException;
import br.com.fiap.dimdim.model.Cliente;
import br.com.fiap.dimdim.model.Conta;
import br.com.fiap.dimdim.repository.ClienteRepository;
import br.com.fiap.dimdim.repository.ContaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContaService {

    private final ContaRepository repository;
    private final ClienteRepository clienteRepository;

    public ContaService(ContaRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ContaDTO> listar() {
        return repository.findAll(Sort.by("id")).stream().map(ContaService::paraDTO).toList();
    }

    @Transactional(readOnly = true)
    public ContaDTO buscar(Long id) {
        return paraDTO(obter(id));
    }

    @Transactional(readOnly = true)
    public long contar() {
        return repository.count();
    }

    @Transactional
    public ContaDTO criar(ContaDTO dto) {
        Conta conta = new Conta();
        aplicar(conta, dto);
        return paraDTO(repository.saveAndFlush(conta));
    }

    @Transactional
    public ContaDTO atualizar(Long id, ContaDTO dto) {
        Conta conta = obter(id);
        aplicar(conta, dto);
        return paraDTO(repository.saveAndFlush(conta));
    }

    @Transactional
    public void excluir(Long id) {
        Conta conta = obter(id);
        repository.delete(conta);
        repository.flush();
    }

    private Conta obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Conta " + id + " nao encontrada"));
    }

    private void aplicar(Conta conta, ContaDTO dto) {
        if (dto.idCliente() == null) {
            throw new IllegalArgumentException("O campo 'idCliente' e obrigatorio");
        }
        Cliente cliente = clienteRepository.findById(dto.idCliente())
                .orElseThrow(() -> new NaoEncontradoException("Cliente " + dto.idCliente() + " nao encontrado"));
        conta.setCliente(cliente);
        conta.setNumeroConta(Validacao.obrigatorio(dto.numeroConta(), "numeroConta", 20));

        String tipo = Validacao.obrigatorio(dto.tipoConta(), "tipoConta", 20).toUpperCase();
        if (!tipo.equals("CORRENTE") && !tipo.equals("POUPANCA")) {
            throw new IllegalArgumentException("O campo 'tipoConta' deve ser CORRENTE ou POUPANCA");
        }
        conta.setTipoConta(tipo);

        BigDecimal saldo = dto.saldo() == null ? BigDecimal.ZERO : dto.saldo();
        if (saldo.signum() < 0) {
            throw new IllegalArgumentException("O campo 'saldo' nao pode ser negativo");
        }
        conta.setSaldo(saldo.setScale(2, RoundingMode.HALF_UP));
    }

    private static ContaDTO paraDTO(Conta c) {
        return new ContaDTO(
                c.getId(),
                c.getCliente().getId(),
                c.getCliente().getNome(),
                c.getNumeroConta(),
                c.getTipoConta(),
                c.getSaldo(),
                Validacao.data(c.getDtAbertura()));
    }
}

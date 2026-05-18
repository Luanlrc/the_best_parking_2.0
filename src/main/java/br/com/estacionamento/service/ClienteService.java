package br.com.estacionamento.service;

import br.com.estacionamento.dto.ClienteDTO;
import br.com.estacionamento.entity.Cliente;
import br.com.estacionamento.exception.ResourceNotFoundException;
import br.com.estacionamento.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    @Transactional(readOnly = true)
    public List<ClienteDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscar(Integer id) {
        return toDTO(buscarEntidade(id));
    }

    public Cliente buscarEntidade(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado: " + id));
    }

    @Transactional
    public ClienteDTO criar(ClienteDTO dto) {
        Cliente cliente = Cliente.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .telefonePrincipal(dto.telefonePrincipal())
                .build();
        return toDTO(repository.save(cliente));
    }

    @Transactional
    public ClienteDTO atualizar(Integer id, ClienteDTO dto) {
        Cliente cliente = buscarEntidade(id);
        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setTelefonePrincipal(dto.telefonePrincipal());
        return toDTO(repository.save(cliente));
    }

    @Transactional
    public void deletar(Integer id) {
        Cliente cliente = buscarEntidade(id);
        repository.delete(cliente);
    }

    private ClienteDTO toDTO(Cliente c) {
        return new ClienteDTO(c.getId(), c.getNome(), c.getCpf(), c.getTelefonePrincipal());
    }
}

package br.com.estacionamento.service;

import br.com.estacionamento.dto.VagaDTO;
import br.com.estacionamento.entity.Vaga;
import br.com.estacionamento.exception.ResourceNotFoundException;
import br.com.estacionamento.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VagaService {

    private final VagaRepository repository;

    @Transactional(readOnly = true)
    public List<VagaDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<VagaDTO> listarLivres() {
        return repository.findByStatus(Vaga.STATUS_LIVRE).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public VagaDTO buscar(Integer id) {
        return toDTO(buscarEntidade(id));
    }

    public Vaga buscarEntidade(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga nao encontrada: " + id));
    }

    @Transactional
    public VagaDTO criar(VagaDTO dto) {
        Vaga vaga = Vaga.builder()
                .numero(dto.numero())
                .status(dto.status() == null ? Vaga.STATUS_LIVRE : dto.status())
                .build();
        return toDTO(repository.save(vaga));
    }

    @Transactional
    public VagaDTO atualizar(Integer id, VagaDTO dto) {
        Vaga vaga = buscarEntidade(id);
        vaga.setNumero(dto.numero());
        if (dto.status() != null) {
            vaga.setStatus(dto.status());
        }
        return toDTO(repository.save(vaga));
    }

    @Transactional
    public void deletar(Integer id) {
        Vaga vaga = buscarEntidade(id);
        repository.delete(vaga);
    }

    private VagaDTO toDTO(Vaga v) {
        return new VagaDTO(v.getId(), v.getNumero(), v.getStatus());
    }
}

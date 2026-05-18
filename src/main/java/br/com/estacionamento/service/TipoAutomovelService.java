package br.com.estacionamento.service;

import br.com.estacionamento.dto.TipoAutomovelDTO;
import br.com.estacionamento.entity.TipoAutomovel;
import br.com.estacionamento.exception.ResourceNotFoundException;
import br.com.estacionamento.repository.TipoAutomovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoAutomovelService {

    private final TipoAutomovelRepository repository;

    @Transactional(readOnly = true)
    public List<TipoAutomovelDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public TipoAutomovelDTO buscar(Integer id) {
        return toDTO(buscarEntidade(id));
    }

    public TipoAutomovel buscarEntidade(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de automovel nao encontrado: " + id));
    }

    @Transactional
    public TipoAutomovelDTO criar(TipoAutomovelDTO dto) {
        TipoAutomovel tipo = TipoAutomovel.builder()
                .nome(dto.nome())
                .valorHora(dto.valorHora())
                .build();
        return toDTO(repository.save(tipo));
    }

    @Transactional
    public TipoAutomovelDTO atualizar(Integer id, TipoAutomovelDTO dto) {
        TipoAutomovel tipo = buscarEntidade(id);
        tipo.setNome(dto.nome());
        tipo.setValorHora(dto.valorHora());
        return toDTO(repository.save(tipo));
    }

    @Transactional
    public void deletar(Integer id) {
        TipoAutomovel tipo = buscarEntidade(id);
        repository.delete(tipo);
    }

    private TipoAutomovelDTO toDTO(TipoAutomovel t) {
        return new TipoAutomovelDTO(t.getId(), t.getNome(), t.getValorHora());
    }
}

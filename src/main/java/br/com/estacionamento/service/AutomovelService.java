package br.com.estacionamento.service;

import br.com.estacionamento.dto.AutomovelDTO;
import br.com.estacionamento.entity.Automovel;
import br.com.estacionamento.entity.Cliente;
import br.com.estacionamento.entity.TipoAutomovel;
import br.com.estacionamento.exception.BusinessException;
import br.com.estacionamento.exception.ResourceNotFoundException;
import br.com.estacionamento.repository.AutomovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutomovelService {

    private final AutomovelRepository repository;
    private final ClienteService clienteService;
    private final TipoAutomovelService tipoAutomovelService;

    @Transactional(readOnly = true)
    public List<AutomovelDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public AutomovelDTO buscar(Integer id) {
        return toDTO(buscarEntidade(id));
    }

    public Automovel buscarEntidade(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Automovel nao encontrado: " + id));
    }

    public Automovel buscarPorPlaca(String placa) {
        return repository.findByPlaca(placa)
                .orElseThrow(() -> new ResourceNotFoundException("Automovel nao encontrado para placa: " + placa));
    }

    @Transactional
    public AutomovelDTO criar(AutomovelDTO dto) {
        if (repository.existsByPlaca(dto.placa())) {
            throw new BusinessException("Ja existe automovel cadastrado com a placa " + dto.placa());
        }
        Cliente cliente = dto.idCliente() == null ? null : clienteService.buscarEntidade(dto.idCliente());
        TipoAutomovel tipo = tipoAutomovelService.buscarEntidade(dto.idTipo());

        Automovel auto = Automovel.builder()
                .placa(dto.placa())
                .modelo(dto.modelo())
                .cor(dto.cor())
                .mensalista(Boolean.TRUE.equals(dto.mensalista()))
                .cliente(cliente)
                .tipo(tipo)
                .build();
        return toDTO(repository.save(auto));
    }

    @Transactional
    public AutomovelDTO atualizar(Integer id, AutomovelDTO dto) {
        Automovel auto = buscarEntidade(id);

        if (!auto.getPlaca().equals(dto.placa()) && repository.existsByPlaca(dto.placa())) {
            throw new BusinessException("Ja existe automovel cadastrado com a placa " + dto.placa());
        }

        Cliente cliente = dto.idCliente() == null ? null : clienteService.buscarEntidade(dto.idCliente());
        TipoAutomovel tipo = tipoAutomovelService.buscarEntidade(dto.idTipo());

        auto.setPlaca(dto.placa());
        auto.setModelo(dto.modelo());
        auto.setCor(dto.cor());
        auto.setMensalista(Boolean.TRUE.equals(dto.mensalista()));
        auto.setCliente(cliente);
        auto.setTipo(tipo);

        return toDTO(repository.save(auto));
    }

    @Transactional
    public void deletar(Integer id) {
        Automovel auto = buscarEntidade(id);
        repository.delete(auto);
    }

    private AutomovelDTO toDTO(Automovel a) {
        return new AutomovelDTO(
                a.getId(),
                a.getPlaca(),
                a.getModelo(),
                a.getCor(),
                a.getMensalista(),
                a.getCliente() == null ? null : a.getCliente().getId(),
                a.getTipo() == null ? null : a.getTipo().getId()
        );
    }
}

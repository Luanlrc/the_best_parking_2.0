package br.com.estacionamento.service;

import br.com.estacionamento.dto.CheckinRequest;
import br.com.estacionamento.dto.CheckoutRequest;
import br.com.estacionamento.dto.MovimentacaoDTO;
import br.com.estacionamento.entity.Automovel;
import br.com.estacionamento.entity.Movimentacao;
import br.com.estacionamento.entity.Vaga;
import br.com.estacionamento.exception.BusinessException;
import br.com.estacionamento.exception.ResourceNotFoundException;
import br.com.estacionamento.repository.MovimentacaoRepository;
import br.com.estacionamento.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoRepository repository;
    private final AutomovelService automovelService;
    private final VagaService vagaService;
    private final VagaRepository vagaRepository;

    @Transactional(readOnly = true)
    public List<MovimentacaoDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoDTO> listarAbertas() {
        return repository.findBySaidaIsNull().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public MovimentacaoDTO buscar(Integer id) {
        return toDTO(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimentacao nao encontrada: " + id)));
    }

    @Transactional
    public MovimentacaoDTO checkin(CheckinRequest request) {
        Automovel automovel = automovelService.buscarPorPlaca(request.placa());

        repository.findFirstByAutomovel_IdAndSaidaIsNullOrderByEntradaDesc(automovel.getId())
                .ifPresent(m -> {
                    throw new BusinessException("Automovel " + request.placa() + " ja esta no estacionamento (movimentacao " + m.getId() + ")");
                });

        Vaga vaga = vagaService.buscarEntidade(request.idVaga());
        if (!Vaga.STATUS_LIVRE.equalsIgnoreCase(vaga.getStatus())) {
            throw new BusinessException("Vaga " + vaga.getNumero() + " nao esta livre (status atual: " + vaga.getStatus() + ")");
        }

        vaga.setStatus(Vaga.STATUS_OCUPADA);
        vagaRepository.save(vaga);

        Movimentacao mov = Movimentacao.builder()
                .automovel(automovel)
                .vaga(vaga)
                .entrada(LocalDateTime.now())
                .build();

        return toDTO(repository.save(mov));
    }

    @Transactional
    public MovimentacaoDTO checkout(CheckoutRequest request) {
        Automovel automovel = automovelService.buscarPorPlaca(request.placa());

        Movimentacao mov = repository.findFirstByAutomovel_IdAndSaidaIsNullOrderByEntradaDesc(automovel.getId())
                .orElseThrow(() -> new BusinessException("Nao existe entrada aberta para a placa " + request.placa()));

        LocalDateTime saida = LocalDateTime.now();
        mov.setSaida(saida);
        mov.setValorCobrado(calcularValor(automovel, mov.getEntrada(), saida));

        Vaga vaga = mov.getVaga();
        vaga.setStatus(Vaga.STATUS_LIVRE);
        vagaRepository.save(vaga);

        return toDTO(repository.save(mov));
    }

    private BigDecimal calcularValor(Automovel automovel, LocalDateTime entrada, LocalDateTime saida) {
        if (Boolean.TRUE.equals(automovel.getMensalista())) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        long minutos = Duration.between(entrada, saida).toMinutes();
        if (minutos < 0) minutos = 0;
        long horasCobradas = (minutos + 59) / 60;
        if (horasCobradas == 0) horasCobradas = 1;

        BigDecimal valorHora = automovel.getTipo().getValorHora();
        return valorHora.multiply(BigDecimal.valueOf(horasCobradas)).setScale(2, RoundingMode.HALF_UP);
    }

    private MovimentacaoDTO toDTO(Movimentacao m) {
        return new MovimentacaoDTO(
                m.getId(),
                m.getEntrada(),
                m.getSaida(),
                m.getValorCobrado(),
                m.getAutomovel().getId(),
                m.getAutomovel().getPlaca(),
                m.getVaga().getId(),
                m.getVaga().getNumero()
        );
    }
}

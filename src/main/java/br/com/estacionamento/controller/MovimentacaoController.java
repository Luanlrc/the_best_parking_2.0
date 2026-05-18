package br.com.estacionamento.controller;

import br.com.estacionamento.dto.CheckinRequest;
import br.com.estacionamento.dto.CheckoutRequest;
import br.com.estacionamento.dto.MovimentacaoDTO;
import br.com.estacionamento.service.MovimentacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
@RequiredArgsConstructor
@Tag(name = "Movimentacoes (entrada/saida)")
public class MovimentacaoController {

    private final MovimentacaoService service;

    @GetMapping
    public List<MovimentacaoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/abertas")
    public List<MovimentacaoDTO> listarAbertas() {
        return service.listarAbertas();
    }

    @GetMapping("/{id}")
    public MovimentacaoDTO buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping("/checkin")
    public ResponseEntity<MovimentacaoDTO> checkin(@Valid @RequestBody CheckinRequest request) {
        MovimentacaoDTO mov = service.checkin(request);
        return ResponseEntity.created(URI.create("/api/movimentacoes/" + mov.id())).body(mov);
    }

    @PostMapping("/checkout")
    public MovimentacaoDTO checkout(@Valid @RequestBody CheckoutRequest request) {
        return service.checkout(request);
    }
}

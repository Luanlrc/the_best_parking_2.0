package br.com.estacionamento.controller;

import br.com.estacionamento.dto.TipoAutomovelDTO;
import br.com.estacionamento.service.TipoAutomovelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-automovel")
@RequiredArgsConstructor
@Tag(name = "Tipos de automovel")
public class TipoAutomovelController {

    private final TipoAutomovelService service;

    @GetMapping
    public List<TipoAutomovelDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public TipoAutomovelDTO buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<TipoAutomovelDTO> criar(@Valid @RequestBody TipoAutomovelDTO dto) {
        TipoAutomovelDTO criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/api/tipos-automovel/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public TipoAutomovelDTO atualizar(@PathVariable Integer id, @Valid @RequestBody TipoAutomovelDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

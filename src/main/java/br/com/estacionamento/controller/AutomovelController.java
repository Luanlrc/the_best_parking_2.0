package br.com.estacionamento.controller;

import br.com.estacionamento.dto.AutomovelDTO;
import br.com.estacionamento.service.AutomovelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/automoveis")
@RequiredArgsConstructor
@Tag(name = "Automoveis")
public class AutomovelController {

    private final AutomovelService service;

    @GetMapping
    public List<AutomovelDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public AutomovelDTO buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<AutomovelDTO> criar(@Valid @RequestBody AutomovelDTO dto) {
        AutomovelDTO criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/api/automoveis/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public AutomovelDTO atualizar(@PathVariable Integer id, @Valid @RequestBody AutomovelDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

package br.com.estacionamento.controller;

import br.com.estacionamento.dto.VagaDTO;
import br.com.estacionamento.service.VagaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vagas")
@RequiredArgsConstructor
@Tag(name = "Vagas")
public class VagaController {

    private final VagaService service;

    @GetMapping
    public List<VagaDTO> listar() {
        return service.listar();
    }

    @GetMapping("/livres")
    public List<VagaDTO> listarLivres() {
        return service.listarLivres();
    }

    @GetMapping("/{id}")
    public VagaDTO buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<VagaDTO> criar(@Valid @RequestBody VagaDTO dto) {
        VagaDTO criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/api/vagas/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public VagaDTO atualizar(@PathVariable Integer id, @Valid @RequestBody VagaDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

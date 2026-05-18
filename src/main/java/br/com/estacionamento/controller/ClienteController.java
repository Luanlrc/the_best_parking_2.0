package br.com.estacionamento.controller;

import br.com.estacionamento.dto.ClienteDTO;
import br.com.estacionamento.service.ClienteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes")
public class ClienteController {

    private final ClienteService service;

    @GetMapping
    public List<ClienteDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ClienteDTO buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> criar(@Valid @RequestBody ClienteDTO dto) {
        ClienteDTO criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/api/clientes/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public ClienteDTO atualizar(@PathVariable Integer id, @Valid @RequestBody ClienteDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

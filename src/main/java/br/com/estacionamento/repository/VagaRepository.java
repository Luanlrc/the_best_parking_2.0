package br.com.estacionamento.repository;

import br.com.estacionamento.entity.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Integer> {
    List<Vaga> findByStatus(String status);
}

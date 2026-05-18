package br.com.estacionamento.repository;

import br.com.estacionamento.entity.Automovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutomovelRepository extends JpaRepository<Automovel, Integer> {
    Optional<Automovel> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
}

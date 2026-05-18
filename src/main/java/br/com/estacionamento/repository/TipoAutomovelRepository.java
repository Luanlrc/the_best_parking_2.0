package br.com.estacionamento.repository;

import br.com.estacionamento.entity.TipoAutomovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoAutomovelRepository extends JpaRepository<TipoAutomovel, Integer> {
}

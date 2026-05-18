package br.com.estacionamento.repository;

import br.com.estacionamento.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {

    Optional<Movimentacao> findFirstByAutomovel_IdAndSaidaIsNullOrderByEntradaDesc(Integer idAutomovel);

    List<Movimentacao> findBySaidaIsNull();
}

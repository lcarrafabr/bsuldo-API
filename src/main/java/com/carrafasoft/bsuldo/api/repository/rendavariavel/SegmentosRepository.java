package com.carrafasoft.bsuldo.api.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.Segmentos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SegmentosRepository extends JpaRepository<Segmentos, Long> {


    @Query(nativeQuery = true,
    value = "select * from segmentos " +
            "where pessoa_id = :pessoaId " +
            "order by segmento_id desc ")
    List<Segmentos> findAllByPessoaId(Long pessoaId);

    @Query(nativeQuery = true,
    value = "select * from segmentos " +
            "where segmento_id = :codigo " +
            "and pessoa_id = :pessoaId ")
    Optional<Segmentos> findByIdAndPessoaId(Long codigo, Long pessoaId);
    @Query(nativeQuery = true,
            value = "select * from segmentos "
                    + "where nome_segmento LIKE %:nomeSegmento% " +
                    "and pessoa_id = :pessoaId ")
    public Segmentos buscaPorNomeSegmento(String nomeSegmento, Long pessoaId);

    @Query(nativeQuery = true,
            value = "select * from segmentos where status = 1 " +
                    "and pessoa_id = :pessoaId ")
    public List<Segmentos> buscaSegmentosAtivos(Long pessoaId);
}

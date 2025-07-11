package com.carrafasoft.bsuldo.api.v1.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Segmentos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Optional<Segmentos> findByCodigoSegmento(@Param("codigoSegmento") String codigoSegmento);

    @Query(nativeQuery = true,
    value = "select * from segmentos " +
            "where codigo_segmento = :codigo " +
            "and pessoa_id = :pessoaId ")
    Optional<Segmentos> findByCodigoSegmentoAndTokenId(@Param("codigo") String codigo,
                                            @Param("pessoaId") Long pessoaId);

    @Query(nativeQuery = true,
    value = "delete from segmentos " +
            "where codigo_segmento = :codigoSegmento ")
    void deleleteByCodigoSegmento(@Param("codigoSegmento") String codigoSegmento);
    @Query(nativeQuery = true,
            value = "select * from segmentos "
                    + "where nome_segmento LIKE %:nomeSegmento% " +
                    "and pessoa_id = :pessoaId ")
    public Segmentos buscaPorNomeSegmento(String nomeSegmento, Long pessoaId);

    @Query(nativeQuery = true,
            value = "select * from segmentos where status = 1 " +
                    "and pessoa_id = :pessoaId ")
    public List<Segmentos> buscaSegmentosAtivos(@Param("pessoaId") Long pessoaId);
}

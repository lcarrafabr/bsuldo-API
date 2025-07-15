package com.carrafasoft.bsuldo.api.v1.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ControleDividendos;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ControleDividendosRepository extends JpaRepository<ControleDividendos, Long> {

    List<ControleDividendos> findAll(Sort sort);

    @Query(nativeQuery = true,
    value = "select * from controle_dividendos " +
            "where pessoa_id = :pessoaId " +
            "order by controle_dividendos_id desc ")
    List<ControleDividendos>findAllByPessoaId(Long pessoaId);

    @Query(nativeQuery = true,
    value = "select COALESCE(SUM(valor_recebido), 0) as totalDivRecebido " +
            "from controle_dividendos " +
            "where tipo_div_recebimento_enum = 'RECEBIDO' " +
            "and valor_recebido > 0 " +
            "and pessoa_id = :pessoaId ")
    BigDecimal valorTotalDivRecebido(Long pessoaId);


    @Query(nativeQuery = true,
            value = "select coalesce(sum(valor_recebido), 0) as totalDivRecebido " +
                    "from controle_dividendos " +
                    "where tipo_div_recebimento_enum = 'RECEBIDO' " +
                    "and valor_recebido > 0 " +
                    "and div_utilizado = 0 " +
                    "and pessoa_id = :pessoaId ")
    BigDecimal valorTotalDivDisponivel(Long pessoaId);

    @Query(nativeQuery = true,
    value = "select * from controle_dividendos " +
            "where data_pagamento = CURRENT_DATE " +
            "order by pessoa_id ")
    List<ControleDividendos> verificaDividendosAReceber();

    @Query(nativeQuery = true,
            value = "select pessoa_id from controle_dividendos " +
                    "where data_pagamento = CURRENT_DATE " +
                    "group by pessoa_id " +
                    "order by pessoa_id ")
    List<Long> retornaListaDePessoasComDividendosAReceberNoDiaAtual();

    @Query(nativeQuery = true,
    value = "select * from controle_dividendos " +
            "where pessoa_id = :pessoaId " +
            "and codigo_controle_dividendo = :codigoControleDividendo ")
    Optional<ControleDividendos> findByCodigoControleDivAndTokenId(@Param("codigoControleDividendo") String codigoControleDividendo,
                                               @Param("pessoaId") Long pessoaId);

    @Query(nativeQuery = true,
    value = "delete from controle_dividendos " +
            "where codigo_controle_dividendo = :codigo")
    void deleteByCodigoControleDividendo(@Param("codigo") String codigo);

    @Query(nativeQuery = true,
    value = "select * from controle_dividendos " +
            "where codigo_controle_dividendo = :codigo ")
    Optional<ControleDividendos> buscaPorCodigoControleDividendo(@Param("codigo") String codigo);

//    @Modifying
//    @Transactional
//    @Query("UPDATE ControleDividendos p SET p.codigoControleDividendo = :uuid WHERE p.controleDividendoId = :controleDividendoId")
//    void atualizarCodigoProdutoRv(@Param("controleDividendoId") Long produtoId, @Param("uuid") String uuid);




}

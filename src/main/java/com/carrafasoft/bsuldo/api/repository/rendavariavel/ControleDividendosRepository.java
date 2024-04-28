package com.carrafasoft.bsuldo.api.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.ControleDividendos;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

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




}

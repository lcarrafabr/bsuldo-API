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
    value = "select sum(valor_recebido) as totalDivRecebido " +
            "from controle_dividendos " +
            "where tipo_div_recebimento_enum = 'RECEBIDO' " +
            "and valor_recebido > 0 ")// +
           // "and div_utilizado = 1 ")
    BigDecimal valorTotalDivRecebido();


    @Query(nativeQuery = true,
            value = "select coalesce(sum(valor_recebido), 0) as totalDivRecebido " +
                    "from controle_dividendos " +
                    "where tipo_div_recebimento_enum = 'RECEBIDO' " +
                    "and valor_recebido > 0 " +
                    "and div_utilizado = 0 ")
    BigDecimal valorTotalDivDisponivel();
}

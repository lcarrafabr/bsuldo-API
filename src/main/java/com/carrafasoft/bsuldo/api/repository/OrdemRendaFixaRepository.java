package com.carrafasoft.bsuldo.api.repository;

import com.carrafasoft.bsuldo.api.model.OrdemRendaFixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OrdemRendaFixaRepository extends JpaRepository<OrdemRendaFixa, Long> {

    @Query(nativeQuery = true,
            value = "select sum(valor_transacao) as total_aplicado " +
                    "from ordem_renda_fixa " +
                    "where tipo_ordem_renda_fixa_enum = 'APLICACAO'")
    BigDecimal listaTotalInvestido();

    @Query(nativeQuery = true,
            value = "select sum(valor_transacao) as total_aplicado " +
                    "from ordem_renda_fixa " +
                    "where tipo_ordem_renda_fixa_enum = 'RESGATE'")
    BigDecimal listaTotalResgatado();

    @Query(nativeQuery = true,
            value = "select sum(valor_transacao) as total_aplicado " +
                    "from ordem_renda_fixa ")
    BigDecimal listaTotalDisponivel();

    @Query(nativeQuery = true,
            value = "select sum(valor_transacao) - :valorResgate as total " +
                    "from ordem_renda_fixa " +
                    "where produto_renda_fixa_id = :produtoRendaFixaId ")
    BigDecimal verificaResgate(BigDecimal valorResgate, Long produtoRendaFixaId);
}

package com.carrafasoft.bsuldo.api.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.RelatorioBasico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemDeCompraRepository extends JpaRepository<OrdensDeCompra, Long> {

    @Query(nativeQuery = true,
            value = "select " +
                    "ordem_de_compra_id, " +
                    "data_execucao, " +
                    "data_transacao, " +
                    "preco_unitario_cota, " +
                    "sum(quantidade_cotas) as quantidade_cotas, " +
                    "tipo_produto_enum, " +
                    "tipo_ordem_renda_variavel_enum, " +
                    "avg(valor_investido) as valor_investido, " +
                    "pessoa_id, " +
                    "produto_id " +
                    "from ordens_de_compra " +
                    "group by produto_id ")
    List<OrdensDeCompra> listar();
}

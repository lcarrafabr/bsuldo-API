package com.carrafasoft.bsuldo.api.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.RelatorioBasico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemDeCompraRepository extends JpaRepository<OrdensDeCompra, Long> {

    @Query(nativeQuery = true,
    value = "select * from ordens_de_compra " +
            "where ordem_de_compra_id = :codigo " +
            "and pessoa_id = :pessoaId ")
    Optional<OrdensDeCompra> findByIdAndPessoaId(Long codigo, Long pessoaId);

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

    @Query(nativeQuery = true,
    value = "select oc.* " +
            "from ordens_de_compra oc " +
            "inner join produtos_renda_variavel p on p.produto_id = oc.produto_id " +
            "where p.ticker LIKE %:ticker% " +
            "and oc.pessoa_id = :pessoaId ")
    public List<OrdensDeCompra> buscaPorNomeProduto(String ticker, Long pessoaId);

    @Query(nativeQuery = true,
    value = "select count(p.ticker) as qtd " +
            "from ordens_de_compra oc " +
            "inner join produtos_renda_variavel p on p.produto_id = oc.produto_id " +
            "where p.ticker = :ticker " +
            "and oc.pessoa_id = :pessoaId ")
    Long findQtdAtivosAcompadosByTyckerAndPessoa(String ticker, Long pessoaId);

    @Query(nativeQuery = true,
            value = "select * from ordens_de_compra " +
                    "where pessoa_id = :pessoaId " +
                    "order by ordem_de_compra_id desc ")
    public List<OrdensDeCompra> findAllDesc(Long pessoaId);

    @Query(nativeQuery = true,
    value = "select distinct * " +
            "from ordens_de_compra " +
            "where pessoa_id = :pessoaId " +
            "group by produto_id ")
    List<OrdensDeCompra> buscaListaDeComprasAgrupadasPorProduto(Long pessoaId);

    @Query(nativeQuery = true,
    value = "select COALESCE(sum(valor_investido), 0) as valor from ordens_de_compra " +
            "where pessoa_id = :pessoaId ")
    BigDecimal valorTotalInvestidoRV(Long pessoaId);


}

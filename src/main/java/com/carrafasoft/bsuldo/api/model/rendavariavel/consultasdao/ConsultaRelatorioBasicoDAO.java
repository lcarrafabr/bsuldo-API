package com.carrafasoft.bsuldo.api.model.rendavariavel.consultasdao;

import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.RelatorioBasico;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class ConsultaRelatorioBasicoDAO {

    private EntityManager entityManager;

    public ConsultaRelatorioBasicoDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<RelatorioBasico> realizarConsultaNativa() {
        // Sua consulta SQL nativa aqui
        String sql = "select " +
                "o.tipo_produto_enum, " +
                "prv.ticker, " +
                "avg(preco_unitario_cota) as preco_unitario_cota, " +
                "sum(quantidade_cotas) as quantidade_cotas, " +
                "sum(valor_investido) as valor_investido, " +
                "avg(valor_investido) as media_investida " +
                "from ordens_de_compra o " +
                "inner join produtos_renda_variavel prv on prv.produto_id = o.produto_id " +
                "group by o.produto_id ";

        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Definir parâmetros, se houver
        //query.setParameter("parametro", valorDoParametro);

        // Executar a consulta
        List<RelatorioBasico> resultados = query.getResultList();

        return resultados;
    }
}

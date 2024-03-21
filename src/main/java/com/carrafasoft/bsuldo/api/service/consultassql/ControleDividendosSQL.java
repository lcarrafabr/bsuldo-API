package com.carrafasoft.bsuldo.api.service.consultassql;

import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.ControleDividendosCadastroCombobox;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.RelatorioBasico;
import org.hibernate.transform.Transformers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ControleDividendosSQL {

    @PersistenceContext
    private static EntityManager entityManager;

    public static List<ControleDividendosCadastroCombobox> buscaTickersParaCombobox() {
        // Sua consulta SQL nativa
        String sql = "select distinct o.produto_id, o.tipo_produto_enum, p.ticker " +
                "from ordens_de_compra o " +
                "inner join produtos_renda_variavel p on p.produto_id = o.produto_id " +
                "order by p.ticker ";

        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(ControleDividendosCadastroCombobox.class));

        // Executar a consulta
        List<ControleDividendosCadastroCombobox> resultados = query.getResultList();

        return resultados;
    }
}

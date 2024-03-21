package com.carrafasoft.bsuldo.api.service.rendavariavel.dto;

import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.MesesValorDTO;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.ValorDividendosRecebidosPorMesEAno;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.ValorPorAnoGridDTO;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardInvestimentosService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ValorDividendosRecebidosPorMesEAno> buscaTotalDividiendosPorMesEAno() {
        // Sua consulta SQL nativa
        String sql = "select data_referencia as dataReferencia, " +
                "sum(valor_recebido) as valorRecebido " +
                "from controle_dividendos " +
                "where tipo_div_recebimento_enum = 'RECEBIDO' " +
                "and year(data_referencia) = '2024' " +
                "group by data_referencia " +
                "order by data_referencia asc ";

        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados diretamente para a classe ValorDividendosRecebidosPorMesEAno
        query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        ValorDividendosRecebidosPorMesEAno resultado = new ValorDividendosRecebidosPorMesEAno();
                        for (int i = 0; i < aliases.length; i++) {
                            switch (aliases[i]) {
                                case "dataReferencia":
                                    // Converter java.sql.Date para java.time.LocalDate
                                    resultado.setDataReferencia(((java.sql.Date) tuple[i]).toLocalDate());
                                    break;
                                case "valorRecebido":
                                    resultado.setValorRecebido((BigDecimal) tuple[i]);
                                    break;
                                // Adicione mais cases conforme necessário para outras colunas
                            }
                        }
                        return resultado;
                    }

                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                });

        // Executar a consulta
        List<ValorDividendosRecebidosPorMesEAno> resultados = query.getResultList();

        return resultados;
    }

    public ValorPorAnoGridDTO buscaValorDividendosPorAno() {

        ValorPorAnoGridDTO valorRetorno = new ValorPorAnoGridDTO();
        List<ValorDividendosRecebidosPorMesEAno> divsRecebidos = buscaTotalDividiendosPorMesEAno();
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (int i = 1; i <= 12; i++) {

            for (int j = 0; j < divsRecebidos.size(); j++) {

                int mes = i;

                if (mes == divsRecebidos.get(j).getDataReferencia().getMonthValue()) {

                    BigDecimal valorRecebido = divsRecebidos.get(j).getValorRecebido();

                    switch (mes) {
                        case 1:
                            valorRetorno.setJan(valorRecebido);
                            break;
                        case 2:
                            valorRetorno.setFev(valorRecebido);
                            break;
                        case 3:
                            valorRetorno.setMar(valorRecebido);
                            break;
                        case 4:
                            valorRetorno.setAbr(valorRecebido);
                            break;
                        case 5:
                            valorRetorno.setMai(valorRecebido);
                            break;
                        case 6:
                            valorRetorno.setJun(valorRecebido);
                            break;
                        case 7:
                            valorRetorno.setJul(valorRecebido);
                            break;
                        case 8:
                            valorRetorno.setAgo(valorRecebido);
                            break;
                        case 9:
                            valorRetorno.setSet(valorRecebido);
                            break;
                        case 10:
                            valorRetorno.setOut(valorRecebido);
                            break;
                        case 11:
                            valorRetorno.setNov(valorRecebido);
                            break;
                        case 12:
                            valorRetorno.setDez(valorRecebido);
                            break;
                    }
                }
            }
        }


        for (ValorDividendosRecebidosPorMesEAno div : divsRecebidos) {
            if (div.getValorRecebido() != null) {
                valorTotal = valorTotal.add(div.getValorRecebido());
            }
        }

        valorRetorno.setTotal(valorTotal);

        return valorRetorno;
    }

}

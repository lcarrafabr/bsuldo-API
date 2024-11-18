package com.carrafasoft.bsuldo.api.service.rendavariavel.dto;

import com.carrafasoft.bsuldo.api.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.*;
import com.carrafasoft.bsuldo.api.model.reports.GridProventosRecebidosEFuturos;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PessoaService pessoaService;

    public List<ValorDividendosRecebidosPorMesEAno> buscaTotalDividiendosPorMesEAno(Long pessoaID) {
        // Sua consulta SQL nativa
        String sql = "select data_referencia as dataReferencia, " +
                "sum(valor_recebido) as valorRecebido " +
                "from controle_dividendos " +
                "where tipo_div_recebimento_enum = 'RECEBIDO' " +
                "and year(data_referencia) = '2024' " +
                "and pessoa_id = " + pessoaID + " " +
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

    public ValorPorAnoGridDTO buscaValorDividendosPorAno(String pessoaId) {

        ValorPorAnoGridDTO valorRetorno = new ValorPorAnoGridDTO();
        List<ValorDividendosRecebidosPorMesEAno> divsRecebidos = buscaTotalDividiendosPorMesEAno(pessoaService.recuperaIdPessoaByToken(pessoaId));
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

    public List<HistoricoProventosFuturos> buscaHistoricoProventosFuturos(String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

        String sql = "SELECT " +
                "c.tipo_ativo_enum, " +
                "pp.logo_url, " +
                "pp.ticker, " +
                "c.tipo_dividendo_enum, " +
                "MONTH(c.data_referencia) AS mes, " +
                "YEAR(c.data_referencia) AS ano, " +
                "c.data_com, " +
                "c.data_pagamento, " +
                "(SELECT SUM(o.quantidade_cotas) " +
                "FROM ordens_de_compra o " +
                "INNER JOIN produtos_renda_variavel p ON p.produto_id = o.produto_id " +
                "WHERE p.produto_id = c.produto_id " +
                "AND o.data_execucao <= c.data_com " +
                "AND o.pessoa_id = " + pessoaId + " " +
                ") as qtdCotas, " +
                "c.valor_por_cota, " +
                "( " +
                "SELECT SUM( " +
                "CASE " +
                "WHEN c.tipo_dividendo_enum = 'JCP' THEN o.quantidade_cotas * c.valor_por_cota * 0.85 " +
                "WHEN c.tipo_dividendo_enum = 'REND_TRIBUTADO' THEN o.quantidade_cotas * c.valor_por_cota * 0.80 " +
                "ELSE o.quantidade_cotas * c.valor_por_cota " +
                "END " +
                ") " +
                "FROM ordens_de_compra o " +
                "INNER JOIN produtos_renda_variavel p ON p.produto_id = o.produto_id " +
                "WHERE p.produto_id = c.produto_id " +
                "AND o.data_execucao <= c.data_com " +
                "AND o.pessoa_id = " + pessoaId + " " +
                ") AS dividendo_a_receber " +
                "FROM controle_dividendos c " +
                "INNER JOIN produtos_renda_variavel pp ON pp.produto_id = c.produto_id " +
                "WHERE c.pessoa_id = " + pessoaId + " " +
                //"AND c.valor_recebido = 0 " +
                //"and tipo_div_recebimento_enum = 'A_RECEBER' " +
                "and c.data_pagamento >= current_date() " +
                "ORDER BY c.data_pagamento ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        //query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(HistoricoProventosFuturos.class));

        // Configurar um transformador para mapear os resultados diretamente para a classe ValorDividendosRecebidosPorMesEAno
        query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        HistoricoProventosFuturos resultado = new HistoricoProventosFuturos();
                        for (int i = 0; i < aliases.length; i++) {
                            switch (aliases[i]) {
                                case "tipo_ativo_enum":
                                    // Converter java.sql.Date para java.time.LocalDate
                                    resultado.setTipo_ativo_enum((String) tuple[i]);
                                    break;
                                case "logo_url":
                                    // Converter java.sql.Date para java.time.LocalDate
                                    resultado.setLogo_url((String) tuple[i]);
                                    break;
                                case "ticker":
                                    // Converter java.sql.Date para java.time.LocalDate
                                    resultado.setTicker((String) tuple[i]);
                                    break;
                                case "tipo_dividendo_enum":
                                    // Converter java.sql.Date para java.time.LocalDate
                                    resultado.setTipo_dividendo_enum((String) tuple[i]);
                                    break;
                                case "mes":
                                    // Converter java.sql.Date para java.time.LocalDate
                                    resultado.setMes((int) tuple[i]);
                                    break;
                                case "ano":
                                    // Converter java.sql.Date para java.time.LocalDate
                                    resultado.setAno((int) tuple[i]);
                                    break;
                                case "data_com":
                                    resultado.setData_com(((java.sql.Date) tuple[i]).toLocalDate());
                                    break;
                                case "data_pagamento":
                                    resultado.setData_pagamento(((java.sql.Date) tuple[i]).toLocalDate());
                                    break;
                                case "qtdCotas":
                                    // Converter java.sql.Date para java.time.LocalDate
                                    resultado.setQtdCotas((BigDecimal) tuple[i]);
                                    break;
                                case "valor_por_cota":
                                    // Converter java.sql.Date para java.time.LocalDate
                                    resultado.setValor_por_cota((BigDecimal) tuple[i]);
                                    break;
                                case "dividendo_a_receber":
                                    // Converter java.sql.Date para java.time.LocalDate
                                    resultado.setDividendo_a_receber((BigDecimal) tuple[i]);
                                    break;
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
        List<HistoricoProventosFuturos> resultados = query.getResultList();

        return resultados;
    }



    public List<RelatorioPorSegmento> getRelatorioPorSegmento(String tokenId, String tipoProduto) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

        String sql = "SELECT  " +
                "s.nome_segmento as nomeSegmento, " +
                "SUM(o.valor_investido) AS valorInvestido, " +
                "ROUND(SUM(o.valor_investido) * 100.0 / ( " +
                "SELECT SUM(o2.valor_investido) " +
                "FROM ordens_de_compra o2 " +
                "WHERE o2.pessoa_id = " + pessoaId + " " +
                "AND o2.tipo_produto_enum = '" + tipoProduto + "' " +
                "), 2) AS percentual " +
                "FROM ordens_de_compra o " +
                "INNER JOIN produtos_renda_variavel p ON p.produto_id = o.produto_id " +
                "INNER JOIN segmentos s ON s.segmento_id = p.segmento_id " +
                "WHERE o.pessoa_id = " + pessoaId + " " +
                "AND o.tipo_produto_enum = '" + tipoProduto + "' " +
                "GROUP BY s.nome_segmento " +
                "ORDER BY percentual DESC ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioPorSegmento.class));

        // Executar a consulta
        List<RelatorioPorSegmento> resultados = query.getResultList();

        return resultados;
    }


    public List<RelatorioSetores> getRelatorioPorSetores(String tokenId, String tipoProduto) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

        String sql = "SELECT  " +
                "s.nome_setor as nomeSetor, " +
                "SUM(o.valor_investido) AS valorInvestido, " +
                "ROUND(SUM(o.valor_investido) * 100.0 / ( " +
                "SELECT SUM(o2.valor_investido) " +
                "FROM ordens_de_compra o2 " +
                "WHERE o2.pessoa_id = " + pessoaId + " " +
                "AND o2.tipo_produto_enum = '" + tipoProduto + "' " +
                "), 2) AS percentual " +
                "FROM ordens_de_compra o " +
                "INNER JOIN produtos_renda_variavel p ON p.produto_id = o.produto_id " +
                "INNER JOIN setores s ON s.setor_id = p.setor_id " +
                "WHERE o.pessoa_id = " + pessoaId + " " +
                "AND o.tipo_produto_enum = '" + tipoProduto + "' " +
                "GROUP BY s.nome_setor " +
                "ORDER BY percentual DESC ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioSetores.class));

        // Executar a consulta
        List<RelatorioSetores> resultados = query.getResultList();

        return resultados;
    }


    public List<GridProventosRecebidosEFuturos> getGridProventosRecebidosEFuturos(String tokenId, String tipoPesquisa) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);
        String tipoPesquisaRetorno = verificaTipoPesquisa(tipoPesquisa);

        String sql = "SELECT " +
                "ano, " +
                "SUM(jan) AS jan, " +
                "SUM(fev) AS fev, " +
                "SUM(mar) AS mar, " +
                "SUM(abr) AS abr, " +
                "SUM(mai) AS mai, " +
                "SUM(jun) AS jun, " +
                "SUM(jul) AS jul, " +
                "SUM(ago) AS ago, " +
                "SUM(setembro) AS setembro, " +
                "SUM(outubro) AS outubro, " +
                "SUM(nov) AS nov, " +
                "SUM(dez) AS dez, " +
                "SUM(jan + fev + mar + abr + mai + jun + jul + ago + setembro + outubro + nov + dez) AS total, " +
                "ROUND(SUM(jan + fev + mar + abr + mai + jun + jul + ago + setembro + outubro + nov + dez) / 12, 2) AS media " +

                "FROM ( " +
                "SELECT " +
                "EXTRACT(YEAR FROM data_pagamento) AS ano, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 1 THEN dividendo_a_receber ELSE 0 END) AS jan, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 2 THEN dividendo_a_receber ELSE 0 END) AS fev, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 3 THEN dividendo_a_receber ELSE 0 END) AS mar, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 4 THEN dividendo_a_receber ELSE 0 END) AS abr, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 5 THEN dividendo_a_receber ELSE 0 END) AS mai, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 6 THEN dividendo_a_receber ELSE 0 END) AS jun, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 7 THEN dividendo_a_receber ELSE 0 END) AS jul, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 8 THEN dividendo_a_receber ELSE 0 END) AS ago, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 9 THEN dividendo_a_receber ELSE 0 END) AS setembro, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 10 THEN dividendo_a_receber ELSE 0 END) AS outubro, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 11 THEN dividendo_a_receber ELSE 0 END) AS nov, " +
                "SUM(CASE WHEN EXTRACT(MONTH FROM data_pagamento) = 12 THEN dividendo_a_receber ELSE 0 END) AS dez " +

                "FROM ( " +
                "SELECT  " +
                "c.data_pagamento, " +
                "CASE  " +
                "WHEN c.tipo_dividendo_enum = 'JCP' THEN o.quantidade_cotas * c.valor_por_cota * 0.85  " +
                "ELSE o.quantidade_cotas * c.valor_por_cota  " +
                "END AS dividendo_a_receber " +
                "FROM controle_dividendos c " +
                "INNER JOIN produtos_renda_variavel pp ON pp.produto_id = c.produto_id " +
                "INNER JOIN ordens_de_compra o ON o.produto_id = c.produto_id " +
                "WHERE c.pessoa_id = " + pessoaId + " " +
                "AND o.data_execucao <= c.data_com " +
                "AND o.pessoa_id = " + pessoaId + " " +
                "AND tipo_div_recebimento_enum in (" + tipoPesquisaRetorno + ") " +
                ") AS subquery_internal " +
                "GROUP BY EXTRACT(YEAR FROM data_pagamento), EXTRACT(MONTH FROM data_pagamento) " +
                ") AS subquery " +
                "GROUP BY ANO " +
                "ORDER BY ANO ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(GridProventosRecebidosEFuturos.class));

        // Executar a consulta
        List<GridProventosRecebidosEFuturos> resultados = query.getResultList();

        return resultados;
    }


    /************************************** MÉTODOS PRIVADOS ***************************************************************/

    private String verificaTipoPesquisa(String tipoPesquisa) {

        String tipoPesquisaRetorno = "";

        switch (tipoPesquisa) {

            case "A_RECEBER":
                tipoPesquisaRetorno = "'A_RECEBER'";
                break;
            case "RECEBIDO":
                tipoPesquisaRetorno = "'RECEBIDO'";
                break;
            case ("AMBOS"):
                tipoPesquisaRetorno = "'A_RECEBER', 'RECEBIDO'";
                break;
            default: tipoPesquisaRetorno = "'A_RECEBER', 'RECEBIDO'";
        }

        return tipoPesquisaRetorno;
    }

}

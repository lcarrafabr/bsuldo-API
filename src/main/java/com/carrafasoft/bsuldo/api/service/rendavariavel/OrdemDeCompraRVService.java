package com.carrafasoft.bsuldo.api.service.rendavariavel;

import com.carrafasoft.bsuldo.api.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.enums.TipoOrdemRendaVariavelEnum;
import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.rendavariavel.ControleDividendos;
import com.carrafasoft.bsuldo.api.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.model.rendavariavel.consultasdao.ConsultaRelatorioBasicoDAO;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.RelatorioBasico;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.RelatorioCompletoRendaVariavel;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.RelatorioPercentualAcoesFiis;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.OrdemDeCompraRepository;
import com.carrafasoft.bsuldo.braviapi.modelo.PrecoAtualCota;
import com.carrafasoft.bsuldo.braviapi.service.ConsultarProdutoSimples;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrdemDeCompraRVService {

    //private static final Logger logger = LoggerFactory.getLogger(RelatorioCompletoRendaVariavel.class);

    @Autowired
    private OrdemDeCompraRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PersistenceContext
    private EntityManager entityManager;


    public PrecoAtualCota consultaPrecoAtualCota(String ticker, String apiToken) {
        PrecoAtualCota precoAtualCota = new PrecoAtualCota();
        try{
            precoAtualCota = ConsultarProdutoSimples.buscaPrecoAtualCota(ticker, apiToken);

            return precoAtualCota;
        } catch (Exception e) {

            precoAtualCota.setTicker(ticker);
            precoAtualCota.setValorAtualCota(BigDecimal.ZERO);

            return precoAtualCota;
        }
    }

    public OrdensDeCompra cadastrarOrdemDeCompraVenda(OrdensDeCompra ordemCompra, HttpServletResponse response) {

        BigDecimal resultadoValorInvestido = calculaValorInvestido(ordemCompra);
        ordemCompra.setValorInvestido(resultadoValorInvestido);

        OrdensDeCompra ordemDeCompraSalva = repository.save(ordemCompra);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, ordemCompra.getOrdemDeCompraId()));

        return ordemDeCompraSalva;
    }

    public OrdensDeCompra atualizarOrdemCompraVenda(Long codigo, OrdensDeCompra ordensDeCompra) {

        OrdensDeCompra ordemSalva = buscaPorId(codigo);
        BeanUtils.copyProperties(ordensDeCompra, ordemSalva, "ordemDeCompraId");

        return repository.save(ordemSalva);
    }

    //********************** relatórios **********************************************
    public List<RelatorioBasico> listarRelatorioBasico(List<OrdensDeCompra> list) {

        List<String> tickersList = new ArrayList<>();
        List<RelatorioBasico> relatorioBasicosList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            if(!tickersList.contains(list.get(i).getProdutoRendaVariavel().getTicker())) {
                tickersList.add(list.get(i).getProdutoRendaVariavel().getTicker());
            }
        }

        for (int i = 0; i < tickersList.size(); i++) {

            RelatorioBasico relatorioBasico = new RelatorioBasico();
            Long qtdCotas = 0L;
            TipoAtivoEnum tipoAtivoEnum = null;
            BigDecimal valorInvestido = BigDecimal.ZERO;
            BigDecimal precoUnitarioInvestido = BigDecimal.ZERO;
            BigDecimal media = BigDecimal.ZERO;

            for (int j = 0; j < list.size(); j++) {

                String ticker = tickersList.get(i);
                if(list.get(j).getProdutoRendaVariavel().getTicker().equals(ticker)) {

                    if(list.get(j).getTipoOrdemRendaVariavelEnum().equals(TipoOrdemRendaVariavelEnum.COMPRA)) {

                        qtdCotas = qtdCotas + list.get(j).getQuantidadeCotas();
                        tipoAtivoEnum = list.get(j).getTipoProdutoEnum();
                        valorInvestido = valorInvestido.add(list.get(j).getValorInvestido());
                        precoUnitarioInvestido = precoUnitarioInvestido.add(list.get(j).getPrecoUnitarioCota());

                    }

                    if(list.get(j).getTipoOrdemRendaVariavelEnum().equals(TipoOrdemRendaVariavelEnum.VENDA)) {

                        qtdCotas = qtdCotas - list.get(j).getQuantidadeCotas();
                        tipoAtivoEnum = list.get(j).getTipoProdutoEnum();
                        valorInvestido = valorInvestido.subtract(list.get(j).getValorInvestido());
                        precoUnitarioInvestido = precoUnitarioInvestido.subtract(list.get(j).getPrecoUnitarioCota());

                    }
                }
            }

            if(valorInvestido.compareTo(BigDecimal.ZERO) > 0 && qtdCotas > 0) {

                media = valorInvestido.divide(new BigDecimal(qtdCotas), RoundingMode.HALF_EVEN).setScale(2);
            }

            relatorioBasico.setTicker(tickersList.get(i));
            //relatorioBasico.setQuantidadeCotas(qtdCotas);
            //relatorioBasico.setTipoAtivoEnum(tipoAtivoEnum);
            relatorioBasico.setValorInvestido(valorInvestido);
            relatorioBasico.setMediaInvestida(media);

            relatorioBasicosList.add(relatorioBasico);
        }

        return relatorioBasicosList;
    }

    public List<RelatorioBasico> relatorioBasicoRendaVariavel() {
        // Sua consulta SQL nativa
//        String sql = "select " +
//                "o.tipo_produto_enum as tipoProdutoEnum, " +
//                "prv.ticker, " +
//                "avg(preco_unitario_cota) as precoUnitarioCota, " +
//                "COALESCE(SUM(quantidade_cotas), 0) AS quantidadeCotas, " +
//                "sum(valor_investido) as valorInvestido, " +
//                "ROUND(sum(valor_investido) / sum(quantidade_cotas), 2) as mediaInvestida " +
//                "from ordens_de_compra o " +
//                "inner join produtos_renda_variavel prv on prv.produto_id = o.produto_id " +
//                "group by o.produto_id ";


        String sql = "SELECT " +
                "o.tipo_produto_enum AS tipoProdutoEnum, " +
                "prv.ticker, " +
                "avg(preco_unitario_cota) as precoUnitarioCota, " +
                "COALESCE(SUM(quantidade_cotas), 0) AS quantidadeCotas, " +
                "sum(valor_investido) as valorInvestido, " +
                "ROUND(sum(valor_investido) / sum(quantidade_cotas), 2) as mediaInvestida, " +
                "ROUND((SUM(valor_investido) / total_valor_investido) * 100, 2) AS percentualValorInvestido " +
                "from ordens_de_compra o " +
                "inner join produtos_renda_variavel prv on prv.produto_id = o.produto_id " +
                "CROSS JOIN ( SELECT COALESCE(SUM(valor_investido), 0) AS total_valor_investido " +
                "FROM ordens_de_compra ) total " +
                "group by o.produto_id ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioBasico.class));

        // Executar a consulta
        List<RelatorioBasico> resultados = query.getResultList();

        return resultados;
    }


    public List<RelatorioBasico> relatorioBasicoVisualizacaoRV() {

        String sql = "SELECT " +
                "o.tipo_produto_enum AS tipoProdutoEnum, " +
                "prv.ticker, " +
                "avg(preco_unitario_cota) as precoUnitarioCota, " +
                "COALESCE(SUM(quantidade_cotas), 0) AS quantidadeCotas, " +
                "SUM(valor_investido) AS valorInvestido, " +
                "ROUND(SUM(valor_investido) / SUM(quantidade_cotas), 2) AS mediaInvestida, " +
                "ROUND((COALESCE(SUM(valor_investido), 0) / total_quantidade_cotas) * 100, 2) AS percentualValorInvestido " +
                "from ordens_de_compra o " +
                "INNER JOIN produtos_renda_variavel prv ON prv.produto_id = o.produto_id " +
                "CROSS JOIN ( SELECT COALESCE(SUM(valor_investido), 0) AS total_quantidade_cotas " +
                "FROM ordens_de_compra " +
                ") total " +
                "GROUP BY o.produto_id " +
                "order by o.tipo_produto_enum, valorInvestido desc ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioBasico.class));

        // Executar a consulta
        List<RelatorioBasico> resultados = query.getResultList();

        return resultados;
    }


    public List<RelatorioCompletoRendaVariavel> relatorioComplertoRV(String apiToken, String tipoProduto) {

        String sql = "SELECT " +
                "o.tipo_produto_enum AS tipoProdutoEnum, " +
                "prv.ticker, " +
                "avg(preco_unitario_cota) as precoUnitarioCota, " +
                "COALESCE(SUM(quantidade_cotas), 0) AS quantidadeCotas, " +
                "SUM(valor_investido) AS valorInvestido, " +
                "ROUND(SUM(valor_investido) / SUM(quantidade_cotas), 2) AS mediaInvestida, " +
                "ROUND((COALESCE(SUM(valor_investido), 0) / total_quantidade_cotas) * 100, 2) AS percentualValorInvestido, " +
                "prv.logo_url as logoUrl " +
                "from ordens_de_compra o " +
                "INNER JOIN produtos_renda_variavel prv ON prv.produto_id = o.produto_id " +
                "CROSS JOIN ( SELECT COALESCE(SUM(valor_investido), 0) AS total_quantidade_cotas " +
                "FROM ordens_de_compra " +
                "where tipo_produto_enum =  '" + tipoProduto + "'" +
                ") total " +
                "where o.tipo_produto_enum =  '" + tipoProduto + "'" +
                "GROUP BY o.produto_id " +
                "order by o.tipo_produto_enum, valorInvestido desc ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioCompletoRendaVariavel.class));

        // Executar a consulta
        List<RelatorioCompletoRendaVariavel> resultados = query.getResultList();

        List<RelatorioCompletoRendaVariavel> listaRetorno = geraDadosRelatorioFinal(resultados, apiToken);


        return listaRetorno;
    }

    private List<RelatorioCompletoRendaVariavel> geraDadosRelatorioFinal(List<RelatorioCompletoRendaVariavel> resultados, String token) {

        log.info("...: Gerando relatorio de dados GRADE {} :...", resultados.get(0).getTipoProdutoEnum());
        for (int i = 0; i < resultados.size(); i++) {

            String ticker = resultados.get(i).getTicker();
            PrecoAtualCota precoAtualCota = consultaPrecoAtualCota(ticker, token);

            BigDecimal valorAtualCota = precoAtualCota.getValorAtualCota();
            BigDecimal precoMedio = resultados.get(i).getMediaInvestida();
            BigDecimal qtdCotas = resultados.get(i).getQuantidadeCotas();
            BigDecimal valorInvestido = resultados.get(i).getValorInvestido();

            BigDecimal variacao = calcularVariacao(valorAtualCota, precoMedio);

            BigDecimal saldoVariacao = calculaSaldoVariacao(qtdCotas, valorAtualCota);

            BigDecimal ganhoPerdaProjetiva = saldoVariacao.subtract(valorInvestido);

            resultados.get(i).setValorCotacaoAtual(valorAtualCota);
            resultados.get(i).setVariacao(variacao);
            resultados.get(i).setSaldoVariacao(saldoVariacao);
            resultados.get(i).setGanhoPerdaProjetiva(ganhoPerdaProjetiva);

        }

        return resultados;
    }


    public List<RelatorioPercentualAcoesFiis> relatorioPercentualAcoesFiis() {

        String sql = "SELECT " +
                        "tipo_produto_enum as tipoAtivoEnum, " +
                        "SUM(valor_investido) AS total, " +
                        "ROUND((SUM(valor_investido) / (SELECT SUM(valor_investido) FROM ordens_de_compra)) * 100, 2) AS percentual " +
                "FROM ordens_de_compra o " +
                        "GROUP BY tipo_produto_enum ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioPercentualAcoesFiis.class));

        // Executar a consulta
        List<RelatorioPercentualAcoesFiis> resultados = query.getResultList();

        return resultados;
    }

    public List<RelatorioPercentualAcoesFiis> relatorioPercentualAcoesFiisRendaFixa() {

        String sql = "SELECT " +
                "tipo_produto_enum as tipoAtivoEnum, " +
                "SUM(total) AS total, " +
                "ROUND((SUM(total) / (SELECT SUM(total) FROM ( " +
                "SELECT SUM(valor_investido) AS total " +
                "FROM ordens_de_compra " +
                "GROUP BY tipo_produto_enum " +
                "" +
                "UNION ALL " +
                "" +
                "SELECT SUM(valor_transacao) AS total " +
                "FROM ordem_renda_fixa " +
                "GROUP BY tipo_produto_enum " +
                ") AS subquery)) * 100, 2) AS percentual " +
                "FROM ( " +
                "SELECT " +
                "tipo_produto_enum, " +
                "SUM(valor_investido) AS total " +
                "FROM ordens_de_compra o " +
                "GROUP BY tipo_produto_enum " +
                "" +
                "UNION ALL " +
                "" +
                "SELECT " +
                "'RENDA_FIXA' AS tipo_produto_enum, " +
                "SUM(valor_transacao) AS total " +
                "FROM ordem_renda_fixa " +
                "GROUP BY tipo_produto_enum " +
                ") AS subquery " +
                "GROUP BY tipo_produto_enum";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioPercentualAcoesFiis.class));

        // Executar a consulta
        List<RelatorioPercentualAcoesFiis> resultados = query.getResultList();

        return resultados;
    }



    private BigDecimal calcularVariacao(BigDecimal cotacaoAtual, BigDecimal precoMedio) {

        BigDecimal numerador = cotacaoAtual.subtract(precoMedio);
        BigDecimal denominador = precoMedio;

        BigDecimal variacaoPercentual = numerador.divide(denominador, 4, BigDecimal.ROUND_HALF_EVEN)
                .multiply(new BigDecimal(100));

        return variacaoPercentual.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    private BigDecimal calculaSaldoVariacao(BigDecimal qtdCotas, BigDecimal valorAtualCota) {

        BigDecimal resultado = valorAtualCota.multiply(qtdCotas);

        return resultado;
    }

    private BigDecimal calculaValorInvestido(OrdensDeCompra ordemCompra) {

        Long quantidadeCotas = ordemCompra.getQuantidadeCotas();
        BigDecimal valorUnitarioCota = ordemCompra.getPrecoUnitarioCota();
        return valorUnitarioCota.multiply(BigDecimal.valueOf(quantidadeCotas));
    }

    private OrdensDeCompra buscaPorId(Long codigo) {

        OrdensDeCompra ordemSalva = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return ordemSalva;
    }



}

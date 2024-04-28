package com.carrafasoft.bsuldo.api.service.rendavariavel;

import com.carrafasoft.bsuldo.api.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.enums.TipoOrdemRendaVariavelEnum;
import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.*;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.OrdemDeCompraRepository;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import com.carrafasoft.bsuldo.api.service.gerenciamentoapi.GerenciamentoProdutosListService;
import com.carrafasoft.bsuldo.api.utils.FuncoesUtils;
import com.carrafasoft.bsuldo.braviapi.modelo.PrecoAtualCota;
import com.carrafasoft.bsuldo.braviapi.service.ConsultarProdutoSimples;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrdemDeCompraRVService {

    @Value("${bsuldo.horariob3.inicio}")
    private String horarioInicioStr;

    @Value("${horario.horariob3.fim}")
    private String horarioFimStr;

    @Autowired
    private OrdemDeCompraRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private GerenciamentoProdutosListService cotacaoFechamentoService;

    @Autowired
    private PessoaService pessoaService;


    public PrecoAtualCota consultaPrecoAtualCota(String ticker, String apiToken) {
        PrecoAtualCota precoAtualCota = new PrecoAtualCota();
        try{
            precoAtualCota = ConsultarProdutoSimples.buscaPrecoAtualCota(ticker, apiToken);

            return precoAtualCota;
        } catch (Exception e) {

            precoAtualCota.setTicker(ticker);
            precoAtualCota.setValorAtualCota(BigDecimal.ZERO);

            log.error("Erro ao consultar ativo {} ", precoAtualCota.getTicker());

            return precoAtualCota;
        }
    }

    public OrdensDeCompra cadastrarOrdemDeCompraVenda(OrdensDeCompra ordemCompra, HttpServletResponse response, String tokenId) {

        Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
        ordemCompra.setPessoa(pessoaSalva);

        BigDecimal resultadoValorInvestido = calculaValorInvestido(ordemCompra);
        ordemCompra.setValorInvestido(resultadoValorInvestido);

        OrdensDeCompra ordemDeCompraSalva = repository.save(ordemCompra);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, ordemCompra.getOrdemDeCompraId()));

        return ordemDeCompraSalva;
    }

    public OrdensDeCompra atualizarOrdemCompraVenda(Long codigo, OrdensDeCompra ordensDeCompra, String tokenId) {

        Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
        ordensDeCompra.setPessoa(pessoaSalva);

        OrdensDeCompra ordemSalva = buscaPorId(codigo);
        BeanUtils.copyProperties(ordensDeCompra, ordemSalva, "ordemDeCompraId");

        return repository.save(ordemSalva);
    }

    public Long pesquisaQtdAtivoComprado(String ticker, Long pessoaId) {

        return repository.findQtdAtivosAcompadosByTyckerAndPessoa(ticker, pessoaId);
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
                        tipoAtivoEnum = list.get(j).getTipoAtivoEnum();
                        valorInvestido = valorInvestido.add(list.get(j).getValorInvestido());
                        precoUnitarioInvestido = precoUnitarioInvestido.add(list.get(j).getPrecoUnitarioCota());

                    }

                    if(list.get(j).getTipoOrdemRendaVariavelEnum().equals(TipoOrdemRendaVariavelEnum.VENDA)) {

                        qtdCotas = qtdCotas - list.get(j).getQuantidadeCotas();
                        tipoAtivoEnum = list.get(j).getTipoAtivoEnum();
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

    public List<RelatorioBasico> relatorioBasicoRendaVariavel(String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

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
                "where o.pessoa_id =  " + pessoaId + " " +
                "group by o.produto_id ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioBasico.class));

        // Executar a consulta
        List<RelatorioBasico> resultados = query.getResultList();

        return resultados;
    }


    public List<RelatorioBasico> relatorioBasicoVisualizacaoRV(String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

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
                "where pessoa_id = " + pessoaId + " " +
                ") total " +
                "where o.pessoa_id = " + pessoaId + " " +
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


    public List<RelatorioCompletoRendaVariavel> relatorioComplertoRV(String apiToken, String tipoProduto, String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

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
                "where tipo_produto_enum =  '" + tipoProduto + "' " +
                "AND pessoa_id = " + pessoaId + " " +
                ") total " +
                "where o.tipo_produto_enum =  '" + tipoProduto + "' " +
                "and o.pessoa_id = " + pessoaId + " " +
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

    //Gera relatório geral de ações / fiis e futuramente BDr's
    private List<RelatorioCompletoRendaVariavel> geraDadosRelatorioFinal(List<RelatorioCompletoRendaVariavel> resultados, String token) {

        //log.info("...: Gerando relatorio de dados GRADE {} :...", resultados.get(0).getTipoProdutoEnum());
        for (int i = 0; i < resultados.size(); i++) {

            String ticker = resultados.get(i).getTicker();
            //PrecoAtualCota precoAtualCota = consultaPrecoAtualCota(ticker, token); //TODO colocar verificação aqui

            PrecoAtualCota precoAtualCota = retornaCotacaoAtualOuFechamento(ticker, token);

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


    public List<RelatorioPercentualAcoesFiis> relatorioPercentualAcoesFiis(String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

        String sql = "SELECT " +
                        "tipo_produto_enum as tipoAtivoEnum, " +
                        "SUM(valor_investido) AS total, " +
                        "ROUND((SUM(valor_investido) / (SELECT SUM(valor_investido) FROM ordens_de_compra where pessoa_id = " + pessoaId +" )) * 100, 2) AS percentual " +
                    "FROM ordens_de_compra o " +
                    "where pessoa_id = " + pessoaId + " " +
                        "GROUP BY tipo_produto_enum ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioPercentualAcoesFiis.class));

        // Executar a consulta
        List<RelatorioPercentualAcoesFiis> resultados = query.getResultList();

        return resultados;
    }

    public List<RelatorioPercentualAcoesFiis> relatorioPercentualAcoesFiisRendaFixa(String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

        String sql = "SELECT " +
                "tipo_produto_enum as tipoAtivoEnum, " +
                "SUM(total) AS total, " +
                "ROUND((SUM(total) / (SELECT SUM(total) FROM ( " +
                "SELECT SUM(valor_investido) AS total " +
                "FROM ordens_de_compra " +
                "WHERE pessoa_id = " + pessoaId + " " +
                "GROUP BY tipo_produto_enum " +
                "" +
                "UNION ALL " +
                "" +
                "SELECT SUM(valor_transacao) AS total " +
                "FROM ordem_renda_fixa " +
                "WHERE pessoa_id = " + pessoaId + " " +
                "GROUP BY tipo_ordem_renda_fixa_enum " +
                ") AS subquery)) * 100, 2) AS percentual " +
                "FROM ( " +
                "SELECT " +
                "tipo_produto_enum, " +
                "SUM(valor_investido) AS total " +
                "FROM ordens_de_compra o " +
                "WHERE pessoa_id = " + pessoaId + " " +
                "GROUP BY tipo_produto_enum " +
                "" +
                "UNION ALL " +
                "" +
                "SELECT " +
                "'RENDA_FIXA' AS tipo_produto_enum, " +
                "SUM(valor_transacao) AS total " +
                "FROM ordem_renda_fixa " +
                "WHERE pessoa_id = " + pessoaId + " " +
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


    public List<RelatorioBasicoComTotalDiviREcebido> relatorioBasicoComDividendoRecebido(String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

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
                "where pessoa_id = " + pessoaId + " " +
                ") total " +
                "where o.pessoa_id = " + pessoaId + " " +
                "GROUP BY o.produto_id " +
                //"order by o.tipo_produto_enum, valorInvestido desc ";
                "order by percentualValorInvestido desc ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioBasicoComTotalDiviREcebido.class));

        // Executar a consulta
        List<RelatorioBasicoComTotalDiviREcebido> resultados = query.getResultList();
        List<DividendoRecebidoByTicker> resultadoDivRecebido = retornaDividendoPorTicker();

        List<RelatorioBasicoComTotalDiviREcebido> resultadoFinal = unificaRelatorioBasicoComDivsRecebidos(resultados, resultadoDivRecebido);

        return resultados;
    }

    private List<RelatorioBasicoComTotalDiviREcebido> unificaRelatorioBasicoComDivsRecebidos(
            List<RelatorioBasicoComTotalDiviREcebido> resultados, List<DividendoRecebidoByTicker> resultadoDivRecebido) {

        List<RelatorioBasicoComTotalDiviREcebido> retorno = new ArrayList<>();

        for (int i = 0; i < resultados.size(); i++) {
            String ticker = resultados.get(i).getTicker();
            resultados.get(i).setTotalDividendosRecebido(BigDecimal.ZERO);

            for (int j = 0; j < resultadoDivRecebido.size(); j++) {

                if(resultadoDivRecebido.get(j).getTicker().contains(ticker)) {

                    resultados.get(i).setTotalDividendosRecebido(resultadoDivRecebido.get(j).getValorRecebido());
                    retorno.add(resultados.get(i));
                }
            }
        }



        return retorno;
    }

    public List<DividendoRecebidoByTicker> retornaDividendoPorTicker() {

        String sql = "SELECT " +
                "pr.ticker, " +
                "sum(c.valor_recebido) as valorRecebido " +
                "from controle_dividendos c " +
                "inner join pessoas p on p.pessoa_id = c.pessoa_id " +
                "inner join produtos_renda_variavel pr on pr.produto_id = c.produto_id " +
                "where year(data_referencia) in (2023, 2024, 2025) " +
                "and c.pessoa_id = 1 " +
                "and c.tipo_div_recebimento_enum = 'RECEBIDO' " +
                "group by c.tipo_ativo_enum, pr.ticker " +
                "order by valorRecebido desc ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(DividendoRecebidoByTicker.class));

        // Executar a consulta
        List<DividendoRecebidoByTicker> resultados = query.getResultList();

        return resultados;
    }


    private PrecoAtualCota retornaCotacaoAtualOuFechamento(String ticker, String token) {

        Boolean isFimDeSemana = FuncoesUtils.fimDeSemanaChecker(); //Se true é Fim de semana
        Boolean isHorarioFuncionamentoB3 = FuncoesUtils.estaNoIntervalo(LocalTime.now(ZoneId.of("America/Sao_Paulo"))
                                            ,horarioInicioStr, horarioFimStr); //Se true está no horario de funcionamento
        PrecoAtualCota precoAtualCota = new PrecoAtualCota();

        try {

            if(isFimDeSemana || !isHorarioFuncionamentoB3) {

                //log.info("É fim de semana ou fora do horario de abertura");

                BigDecimal valorCotacaoFechamento = cotacaoFechamentoService.retornaValorCotacaoFechamento(ticker);
                precoAtualCota.setTicker(ticker);
                precoAtualCota.setValorAtualCota(valorCotacaoFechamento);

            } else {

               // log.info("Está tudo ok");
                //TODO quando tiver os regras para usuarios free e pagantes, aqui ficará a atualização temporária ou Ontime

                precoAtualCota = consultaPrecoAtualCota(ticker, token);
            }
            return precoAtualCota;

        } catch (Exception e) {

            precoAtualCota.setTicker(ticker);
            precoAtualCota.setValorAtualCota(BigDecimal.ZERO);

            log.error("Erro ao consultar ativo {} ", precoAtualCota.getTicker());

            return precoAtualCota;
        }
    }



}

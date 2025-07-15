package com.carrafasoft.bsuldo.api.v1.service.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemManualAutoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemRendaVariavelEnum;
import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.OrdemDeCompraInputReppresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.OrdemDeCompraInputUpdateRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel.OrdemDeCompraMapper;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.exceptionmodel.OrdemDeCompraNaoEncontradaException;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.*;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.OrdemDeCompraRepository;
import com.carrafasoft.bsuldo.api.v1.service.FeriadosService;
import com.carrafasoft.bsuldo.api.v1.service.OrdemDeCompraRVService;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.ProdutoRendaVariavelService;
import com.carrafasoft.bsuldo.api.v1.service.gerenciamentoapi.GerenciamentoProdutosListService;
import com.carrafasoft.bsuldo.api.v1.utils.FuncoesUtils;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class OrdemDeCompraRVServiceImpl implements OrdemDeCompraRVService {

    @Value("${bsuldo.horariob3.inicio}")
    private String horarioInicioStr;

    @Value("${horario.horariob3.fim}")
    private String horarioFimStr;

    @Autowired
    private OrdemDeCompraRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private FeriadosService feriadosService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private GerenciamentoProdutosListService cotacaoFechamentoService;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ProdutoRendaVariavelService produtoRendaVariavelService;

    @Autowired
    private OrdemDeCompraMapper ordemDeCompraMapper;


    @Override
    public List<OrdensDeCompra> findAll(String tokenId) {

        return repository.findAllDesc(pessoaService.recuperaIdPessoaByToken(tokenId));
    }

    @Override
    public OrdensDeCompra buscaPorCodidoOrdemDeCompraAndTokenId(String codigoOrdemDeCompra, String tokenId) {

        return repository.findByCodigoOrdemCompraAndPessoaId(codigoOrdemDeCompra,
                pessoaService.recuperaIdPessoaByToken(tokenId)).orElseThrow(() -> new OrdemDeCompraNaoEncontradaException(codigoOrdemDeCompra));
    }


    @Override
    public PrecoAtualCota consultaPrecoAtualCota(String ticker, String apiToken) {
        PrecoAtualCota precoAtualCota = new PrecoAtualCota();
        try{
            precoAtualCota = ConsultarProdutoSimples.buscaPrecoAtualCota(ticker, apiToken);

            if(precoAtualCota.getValorAtualCota() == null) {
                precoAtualCota.setValorAtualCota(BigDecimal.ZERO);
            }

            return precoAtualCota;
        } catch (Exception e) {

            precoAtualCota.setTicker(ticker);
            precoAtualCota.setValorAtualCota(BigDecimal.ZERO);

            log.error("Erro ao consultar ativo {} ", precoAtualCota.getTicker());

            return precoAtualCota;
        }
    }

    @Transactional
    @Override
    public OrdensDeCompra cadastrarOrdemDeCompraVenda(OrdemDeCompraInputReppresentation ordemCompra, HttpServletResponse response, String tokenId) {

        try {
            Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
            ordemCompra.setTipoOrdemManualAutoEnum(TipoOrdemManualAutoEnum.MANUAL);

            BigDecimal resultadoValorInvestido = calculaValorInvestido(ordemCompra);
            ordemCompra.setValorInvestido(resultadoValorInvestido);

            ProdutosRendaVariavel produtosRendaVariavelSalvo = produtoRendaVariavelService.findByCodigoProdutoRVAndTokenId(
                    ordemCompra.getProdutoRendaVariavel().getCodigoProdutoRV(), tokenId
            );

            OrdensDeCompra ordemCompraToSave = ordemDeCompraMapper.toEntity(ordemCompra, pessoaSalva, produtosRendaVariavelSalvo);

            OrdensDeCompra ordemDeCompraSalva = repository.save(ordemCompraToSave);
            publisher.publishEvent(new RecursoCriadoEvent(this, response, ordemDeCompraSalva.getOrdemDeCompraId()));

            return ordemDeCompraSalva;

        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteByCodigo(String codigo) {

        repository.deleteByCodigoOrdemDeComppra(codigo);
    }

    @Transactional
    @Override
    public OrdensDeCompra atualizarOrdemCompraVenda(String codigo, OrdemDeCompraInputUpdateRepresentation ordensDeCompra, String tokenId) {

        try {
            Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
            ordensDeCompra.setTipoOrdemManualAutoEnum(TipoOrdemManualAutoEnum.MANUAL);

            var produtoSalvo = produtoRendaVariavelService.findByCodigoProdutoRVAndTokenId(
                    ordensDeCompra.getProdutoRendaVariavel().getCodigoProdutoRV(),tokenId);

            OrdensDeCompra ordemSalva = buscaPorCodidoOrdemDeCompraAndTokenId(codigo, tokenId);
            BeanUtils.copyProperties(ordensDeCompra, ordemSalva, "ordemDeCompraId");

            ordemSalva.setPessoa(pessoaSalva);
            ordemSalva.setProdutoRendaVariavel(produtoSalvo);

            return repository.save(ordemSalva);

        } catch (OrdemDeCompraNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
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
            //resultados.get(i).setValorCotacaoAtual(BigDecimal.ZERO); // SIMULAR ERRO
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
                "tipo_produto_enum AS tipoAtivoEnum, " +
                "SUM(total) AS total, " +
                "ROUND((SUM(total) / ( " +
                "SELECT SUM(total) " +
                "FROM ( " +
                "SELECT SUM(valor_investido) AS total  " +
                "FROM ordens_de_compra " +
                "WHERE pessoa_id = :pessoaId " +
                "GROUP BY tipo_produto_enum " +

                "UNION ALL " +

                "SELECT SUM(valor_transacao) AS total " +
                "FROM ordem_renda_fixa " +
                "WHERE pessoa_id = :pessoaId " +
                "GROUP BY tipo_ordem_renda_fixa_enum " +

                "UNION ALL  " +

                "SELECT SUM(valor_investido) AS total  " +
                "FROM cripto_transacao  " +
                "WHERE pessoa_id = :pessoaId " +
                "GROUP BY 'CRIPTO' "  +// Aqui é o valor fixo para todas as transações de cripto ;
                ") AS subquery " +
                ")) * 100, 2) AS percentual " +
                "FROM ( " +
                "SELECT " +
                "tipo_produto_enum, " +
                "SUM(valor_investido) AS total " +
                "FROM ordens_de_compra o " +
                "WHERE pessoa_id = :pessoaId " +
                "GROUP BY tipo_produto_enum " +

                "UNION ALL " +

                "SELECT " +
                "'RENDA_FIXA' AS tipo_produto_enum, " +
                "SUM(valor_transacao) AS total " +
                "FROM ordem_renda_fixa " +
                "WHERE pessoa_id = :pessoaId " +
                "GROUP BY tipo_produto_enum " +

                "UNION ALL " +

                "SELECT " +
                "'CRYPTO' AS tipo_produto_enum, " + //Substituindo tipo_ordem_cripto por 'CRYPTO'
                "SUM(valor_investido) AS total " +
                "FROM cripto_transacao " +
                "WHERE pessoa_id = :pessoaId " +
                "GROUP BY 'CRYPTO' " + //Agrupando todas as transações de cripto como 'CRIPTO'
                ") AS subquery " +
                "GROUP BY tipo_produto_enum " +
                "order by percentual desc ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("pessoaId", pessoaId);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioPercentualAcoesFiis.class));

        // Executar a consulta
        List<RelatorioPercentualAcoesFiis> resultados = query.getResultList();

        return resultados;
    }



    private BigDecimal calcularVariacao(BigDecimal cotacaoAtual, BigDecimal precoMedio) {

        BigDecimal numerador = cotacaoAtual.subtract(precoMedio);
        BigDecimal denominador = precoMedio;

        BigDecimal variacaoPercentual = numerador.divide(denominador, 4, RoundingMode.HALF_EVEN)
                .multiply(new BigDecimal(100));

        return variacaoPercentual.setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculaSaldoVariacao(BigDecimal qtdCotas, BigDecimal valorAtualCota) {

        BigDecimal resultado = valorAtualCota.multiply(qtdCotas);

        return resultado;
    }

    private BigDecimal calculaValorInvestido(OrdemDeCompraInputReppresentation ordemCompra) {

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

        // Ordenar a lista pelo campo totalDividendosRecebido em ordem decrescente
        Collections.sort(resultadoFinal, new Comparator<RelatorioBasicoComTotalDiviREcebido>() {
            @Override
            public int compare(RelatorioBasicoComTotalDiviREcebido o1, RelatorioBasicoComTotalDiviREcebido o2) {
                return o2.getTotalDividendosRecebido().compareTo(o1.getTotalDividendosRecebido());
            }
        });

        // Alternativamente, usando uma expressão lambda:
        // resultadoFinal.sort((o1, o2) -> o2.getTotalDividendosRecebido().compareTo(o1.getTotalDividendosRecebido()));

        return resultadoFinal;
    }

    private List<RelatorioBasicoComTotalDiviREcebido> unificaRelatorioBasicoComDivsRecebidos(
            List<RelatorioBasicoComTotalDiviREcebido> resultados, List<DividendoRecebidoByTicker> resultadoDivRecebido) {

        List<RelatorioBasicoComTotalDiviREcebido> retorno = new ArrayList<>();

        for (RelatorioBasicoComTotalDiviREcebido resultado : resultados) {
            String ticker = resultado.getTicker();
            resultado.setTotalDividendosRecebido(BigDecimal.ZERO);

            for (DividendoRecebidoByTicker divRecebido : resultadoDivRecebido) {
                if (divRecebido.getTicker().contains(ticker)) {
                    resultado.setTotalDividendosRecebido(divRecebido.getValorRecebido());
                    retorno.add(resultado);
                    break; // Para evitar duplicações se houver múltiplos matches
                }
            }
        }

        List<String> tickersSemCadastrodividendo = encontrarItemExtra(resultados, resultadoDivRecebido);

        for (String tickerSemDividendo : tickersSemCadastrodividendo) {
            String ticker = tickerSemDividendo.replace("[", "").replace("]", "");

            for (RelatorioBasicoComTotalDiviREcebido resultado : resultados) {
                if (resultado.getTicker().equals(ticker)) {
                    RelatorioBasicoComTotalDiviREcebido r = new RelatorioBasicoComTotalDiviREcebido();

                    r.setTicker(ticker); // Corrigir aqui para usar o ticker individual
                    r.setTotalDividendosRecebido(BigDecimal.ZERO);
                    r.setMediaInvestida(resultado.getMediaInvestida());
                    r.setValorInvestido(resultado.getValorInvestido());
                    r.setQuantidadeCotas(resultado.getQuantidadeCotas());
                    r.setTipoProdutoEnum(resultado.getTipoProdutoEnum());
                    r.setPercentualValorInvestido(resultado.getPercentualValorInvestido());

                    retorno.add(r);
                    break; // Para evitar duplicações se houver múltiplos matches
                }
            }
        }

        return retorno;
    }


    private List<String> encontrarItemExtra(
            List<RelatorioBasicoComTotalDiviREcebido> resultados,
            List<DividendoRecebidoByTicker> resultadoDivRecebido) {

        List<String> retorno = new ArrayList<>();

        // Cria um Set para armazenar os tickers da segunda lista
        Set<String> tickersDivRecebido = new HashSet<>();
        for (DividendoRecebidoByTicker div : resultadoDivRecebido) {
            tickersDivRecebido.add(div.getTicker());
        }

        // Verifica quais tickers da primeira lista não estão no Set
        List<String> tickersExtras = new ArrayList<>();
        for (RelatorioBasicoComTotalDiviREcebido relatorio : resultados) {
            if (!tickersDivRecebido.contains(relatorio.getTicker())) {
                tickersExtras.add(relatorio.getTicker());
            }
        }

        // Imprime os tickers que estão na primeira lista mas não na segunda
        if (!tickersExtras.isEmpty()) {
            for (String ticker : tickersExtras) {
                retorno.add(ticker);
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

        Boolean isFeriado = feriadosService.isFeriado(LocalDate.now(ZoneId.of("America/Sao_Paulo")));// Se true é feriado

        PrecoAtualCota precoAtualCota = new PrecoAtualCota();

        try {

            if(isFimDeSemana || !isHorarioFuncionamentoB3 || isFeriado) {

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

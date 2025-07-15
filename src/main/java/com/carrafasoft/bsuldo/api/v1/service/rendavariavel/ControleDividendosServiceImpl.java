package com.carrafasoft.bsuldo.api.v1.service.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.carrafasoft.bsuldo.api.v1.mapper.ControleDividendosRowMapper;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ControleDividendoInput;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ControleDividendosInputUpdate;
import com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel.ControleDividendosMapper;
import com.carrafasoft.bsuldo.api.v1.model.AvisosAutomaticos;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.exceptionmodel.ControleDividendosNaoEncontradoException;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ControleDividendos;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.ControleDividendosCadastroCombobox;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.GraficoDividendosRecebidosPorMesEAno;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.ControleDividendosRepository;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.OrdemDeCompraRepository;
import com.carrafasoft.bsuldo.api.v1.service.AvisosAutomaticosService;
import com.carrafasoft.bsuldo.api.v1.service.ControleDividendoService;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.ProdutoRendaVariavelService;
import com.carrafasoft.bsuldo.api.v1.service.consultassql.ControleDividendosSQL;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ControleDividendosServiceImpl implements ControleDividendoService {

    //private static final Logger logger = LoggerFactory.getLogger(ControleDividendos.class);

    private final String TITULO_DIVIDENDOS_A_RECEBER = "HOJE TEM DIVIDENDOS ";
    private final String MENSAGEM_DIVIDENDOS_A_RECEBER = "Você tem dividendos a receber: ";

    @Autowired
    private ControleDividendosRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OrdemDeCompraRepository ordemDeCompraRepository;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private AvisosAutomaticosService avisosAutoService;

    //@Autowired
    private ControleDividendosSQL controleDividendosSQL;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProdutoRendaVariavelService produtoRendaVariavelService;

    @Autowired
    private ControleDividendosMapper controleDividendosMapper;

    @Transactional
    @Override
    public ControleDividendos cadastrarControleDividendo(ControleDividendoInput controleDividendos, HttpServletResponse response, String tokenId) {

        try {
            log.info("..: Iniciando o cadastro de dividendos :...");

            // String ticker = controleDividendos.getProdutosRendaVariavel().getTicker();

            Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));

            ProdutosRendaVariavel produtosRendaVariavelSalvo = produtoRendaVariavelService.findByCodigoProdutoRVAndTokenId(
                    controleDividendos.getProdutosRendaVariavel().getCodigoProdutoRV(), tokenId
            );

            ControleDividendos controleDivToSave = controleDividendosMapper.toEntity(controleDividendos, pessoaSalva, produtosRendaVariavelSalvo);

            ControleDividendos controleDivSalvo = repository.save(controleDivToSave);
            publisher.publishEvent(new RecursoCriadoEvent(this, response, controleDivSalvo.getControleDividendoId()));

            log.info("..: Cadastro realizado com sucesso: PessoaId: {}, ProdutoRVID: {} :...: ", pessoaSalva.getPessoaID(),
                    controleDivSalvo.getProdutosRendaVariavel().getProdutoId());

            return controleDivSalvo;

        } catch (ControleDividendosNaoEncontradoException e) {
            log.error("....: Erro ao cadastrar dividendo :....");
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public ControleDividendos atualizarControleDividendos(String codigo, ControleDividendosInputUpdate controleDividendos, String tokenId) {

        try {
            log.info("..: Iniciando atualização de dividendos :...");

            Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
            ControleDividendos controlDivSalvo = findByCodigoControleDivAndTokenId(codigo, tokenId);

            ProdutosRendaVariavel produtosRendaVariavelSalvo = produtoRendaVariavelService.findByCodigoProdutoRVAndTokenId(
                    controleDividendos.getProdutosRendaVariavel().getCodigoProdutoRV(), tokenId
            );

            BeanUtils.copyProperties(controleDividendos, controlDivSalvo, "controleDividendoId");

            controlDivSalvo.setProdutosRendaVariavel(produtosRendaVariavelSalvo);
            controlDivSalvo.setPessoa(pessoaSalva);

            log.info("..: Atualização realizado com sucesso :...: " + controlDivSalvo.getProdutosRendaVariavel().getTicker());

            return repository.save(controlDivSalvo);
        } catch (ControleDividendosNaoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void removerControleDividendo(String codigo) {

        repository.deleteByCodigoControleDividendo(codigo);
    }

    public void atualizaAtatusDividendoAtivo(String codigo, Boolean usado) {

        log.info("...: Atualizando status ativo dividendo :...");
        ControleDividendos controleDivSalvo = buscaPorCodigoControleDividendo(codigo);
        controleDivSalvo.setDivUtilizado(usado);
        repository.save(controleDivSalvo);
        log.info("...: Status atualizado: " + controleDivSalvo.getProdutosRendaVariavel().getTicker() + " ID = " + codigo + " = " + usado + " :...");
    }

    @Override
    public List<ControleDividendosCadastroCombobox> buscaControleDividendosCombobox(String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);


        List<ControleDividendosCadastroCombobox> controleDivList = new ArrayList<>();
        List<OrdensDeCompra> ordemCompraAgrupada = new ArrayList<>();
        ordemCompraAgrupada = ordemDeCompraRepository.buscaListaDeComprasAgrupadasPorProduto(pessoaId);

        for (int i = 0; i < ordemCompraAgrupada.size(); i++) {

            ControleDividendosCadastroCombobox controleBox = new ControleDividendosCadastroCombobox();

            controleBox.setCodigoProdutoRV(ordemCompraAgrupada.get(i).getProdutoRendaVariavel().getCodigoProdutoRV());
            controleBox.setTipoProdutoEnum(ordemCompraAgrupada.get(i).getTipoAtivoEnum().toString());
            controleBox.setTicker(ordemCompraAgrupada.get(i).getProdutoRendaVariavel().getTicker());

            controleDivList.add(controleBox);
        }

        return controleDivList;
        }


    @Override
    public ControleDividendos findByCodigoControleDivAndTokenId(String codigoControleDividendo, String tokenId) {

        return repository.findByCodigoControleDivAndTokenId(codigoControleDividendo,
                pessoaService.recuperaIdPessoaByToken(tokenId)).orElseThrow(() -> new ControleDividendosNaoEncontradoException(codigoControleDividendo));
    }

    private ControleDividendos buscaPorCodigoControleDividendo(String codigo) {

        return repository.buscaPorCodigoControleDividendo(codigo).orElseThrow(() -> new ControleDividendosNaoEncontradoException(codigo));
    }

    private ControleDividendos buscaPorId(Long codigo) {

        ControleDividendos controleDividendo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return controleDividendo;
    }

    @Scheduled(cron = "${bsuldo.task.schedule.verifica_dividendos_a_receber}")
    public void verificaDividendoAReceber() {

        log.info("...: Iniciando consulta automática de dividendo para a data: {} :...", LocalDate.now());
        List<ControleDividendos> dividendosList = repository.verificaDividendosAReceber();
        log.info("...: Quandidade de linhas: {}", dividendosList.size());

        if(dividendosList.size() > 0) {

            List<AvisosAutomaticos> avisosList = new ArrayList<>();

            List<Long> pessoasIdList = repository.retornaListaDePessoasComDividendosAReceberNoDiaAtual();
            log.info("...: Quantidade de pessoas a receceber dividendos: {} :...", pessoasIdList.size());

            for (int i = 0; i < pessoasIdList.size(); i++) {

                String titulo = TITULO_DIVIDENDOS_A_RECEBER;
                String mensagem = MENSAGEM_DIVIDENDOS_A_RECEBER;
                Long pessoaId = pessoasIdList.get(i);

                AvisosAutomaticos avisosAutomaticos = new AvisosAutomaticos();

                for (int j = 0; j < dividendosList.size(); j++) {

                    if(dividendosList.get(j).getPessoa().getPessoaID().equals(pessoaId)) {

                        mensagem = mensagem//.concat("| ")
                                .concat(dividendosList.get(j).getProdutosRendaVariavel().getTicker()
                                .concat(" | "));
                    }
                }

                Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaId);
                avisosAutomaticos.setTitulo(titulo);
                avisosAutomaticos.setMensagem(mensagem);
                avisosAutomaticos.setPessoa(pessoaSalva);

                avisosList.add(avisosAutomaticos);
            }

            log.info("...: Enviando lista para cadastro :...");
            avisosAutoService.cadastrarAvisoAutomaticoList(avisosList);

        } else {
            log.info("...: Consulta automática retornou (0) zero registros :....");
        }
    }



    // ******************************************  SQL *****************************************************************

    public List<GraficoDividendosRecebidosPorMesEAno> getDadosGraficoDividendosPorMesEAno(String ano, String mes, Long pessoaId) {

        String sql = "select p.ticker, sum(c.valor_recebido) as valorRecebido " +
                "from controle_dividendos c " +
                "inner join produtos_renda_variavel p on p.produto_id = c.produto_id " +
                "WHERE YEAR(c.data_referencia) = '" + ano + "' " +
                "AND MONTH(c.data_referencia) = '" + mes + "' " +
                "and c.pessoa_id =  " + pessoaId + " " +
                "group by p.ticker " +
                "order by c.valor_recebido ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(GraficoDividendosRecebidosPorMesEAno.class));

        // Executar a consulta
        List<GraficoDividendosRecebidosPorMesEAno> resultados = query.getResultList();

        return resultados;
    }

    @Override
    public List<ControleDividendos> listarTodosOsDividendos(String tokenId, String ticker, String tipoRecebimento,
                                                            String dataReferencia, String dataPagamento) {

        Long codigoPessoa = pessoaService.recuperaIdPessoaByToken(tokenId);

        List<ControleDividendos> retorno = new ArrayList<>();
        String sql = "SELECT " +
                "c.controle_dividendos_id, " +
                "c.codigo_controle_dividendo, " +
                "c.tipo_ativo_enum, " +
                "c.tipo_div_recebimento_enum, " +
                "c.data_referencia, " +
                "c.tipo_dividendo_enum, " +
                "c.data_com, " +
                "c.data_pagamento, " +
                "c.valor_por_cota, " +
                "c.valor_recebido, " +
                "c.div_utilizado, " +
                "c.pessoa_id, " +
                "c.produto_id, " +
                "p.nome_pessoa AS nome_pessoa, " +
                "pr.produto_id, " +
                "pr.codigo_produto_rv, " +
                "pr.long_name, " +
                "pr.short_name, " +
                "pr.ticker, " +
                "pr.currency, " +
                "pr.cnpj, " +
                "pr.gera_dividendos, " +
              //  "pr.status AS status, " +
                "pr.cotas_emitidas, " +
                "pr.logo_url, " +
                "pr.descricao, " +
                "pr.emissor_id, " +
                "pr.segmento_id, " +
                "s.codigo_segmento, " +
                "pr.setor_id, " +
                "se.codigo_setor, " +
                "e.nome_emissor, " +
               // "e.status AS emissor_status, " +
                "e.data_cadastro AS emissor_data_cadastro, " +
                "s.nome_segmento, " +
                "s.status AS segmento_status, " +
                "se.nome_setor," +

                "(SELECT SUM(o.quantidade_cotas) " +
                "FROM ordens_de_compra o " +
                "INNER JOIN produtos_renda_variavel p ON pr.produto_id = o.produto_id " +
                "WHERE p.produto_id = c.produto_id " +
                "AND o.data_execucao <= c.data_com " +
                "AND o.pessoa_id = " + codigoPessoa + " " +
                ") as qtdCota " +

               // "se.status AS setor_status " +
                "FROM " +
                "controle_dividendos c " +
                "INNER JOIN " +
                "pessoas p ON p.pessoa_id = c.pessoa_id " +
                "INNER JOIN " +
                "produtos_renda_variavel pr ON pr.produto_id = c.produto_id " +
                "INNER JOIN " +
                "segmentos s ON s.segmento_id = pr.segmento_id " +
                "INNER JOIN " +
                "setores se ON se.setor_id = pr.setor_id " +
                "INNER JOIN " +
                "emissores e ON e.emissor_id = pr.emissor_id " +
                "WHERE " +
                "c.pessoa_id =  " + codigoPessoa + " ";

        if(StringUtils.hasLength(ticker)) {
            sql = sql + "AND pr.ticker = '" + ticker.trim().toUpperCase() + "' ";
        }

        if(StringUtils.hasLength(tipoRecebimento)) {
            sql = sql + "and c.tipo_div_recebimento_enum = '" + tipoRecebimento.trim().toUpperCase() + "' ";
        }

        if(StringUtils.hasLength(dataReferencia)) {
            sql = sql + "and c.data_referencia = '" + dataReferencia.trim() + "' ";
        }

        if(StringUtils.hasLength(dataPagamento)) {
            sql = sql + "and c.data_pagamento = '" + dataPagamento.trim() + "' ";
        }

        sql = sql + "ORDER BY c.controle_dividendos_id DESC";
        retorno = jdbcTemplate.query(sql, new ControleDividendosRowMapper());

        return retorno;
    }


}

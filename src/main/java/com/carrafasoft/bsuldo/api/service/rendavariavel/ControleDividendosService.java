package com.carrafasoft.bsuldo.api.service.rendavariavel;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.AvisosAutomaticos;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.model.rendavariavel.ControleDividendos;
import com.carrafasoft.bsuldo.api.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.ControleDividendosCadastroCombobox;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.GraficoDividendosRecebidosPorMesEAno;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.RelatorioBasico;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.ControleDividendosRepository;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.OrdemDeCompraRepository;
import com.carrafasoft.bsuldo.api.service.AvisosAutomaticosService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import com.carrafasoft.bsuldo.api.service.consultassql.ControleDividendosSQL;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ControleDividendosService {

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

    public ControleDividendos cadastrarControleDividendo(ControleDividendos controleDividendos, HttpServletResponse response) {

        log.info("..: Iniciando o cadastro de dividendos :...");
        ControleDividendos controleDivSalvo = repository.save(controleDividendos);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, controleDivSalvo.getControleDividendoId()));

        log.info("..: Cadastro realizado com sucesso :...: " + controleDivSalvo.getProdutosRendaVariavel().getTicker());

        return controleDivSalvo;
    }

    public ControleDividendos atualizarControleDividendos(Long codigo, ControleDividendos controleDividendos) {

        log.info("..: Iniciando atualização de dividendos :...");
        ControleDividendos controlDivSalvo = buscaPorId(codigo);
        BeanUtils.copyProperties(controleDividendos, controlDivSalvo, "controleDividendoId");

        log.info("..: Atualização realizado com sucesso :...: " + controlDivSalvo.getProdutosRendaVariavel().getTicker());

        return repository.save(controlDivSalvo);
    }

    public void atualizaAtatusDividendoAtivo(Long codigo, Boolean usado) {

        log.info("...: Atualizando status ativo dividendo :...");
        ControleDividendos controleDivSalvo = buscaPorId(codigo);
        controleDivSalvo.setDivUtilizado(usado);
        repository.save(controleDivSalvo);
        log.info("...: Status atualizado: " + controleDivSalvo.getProdutosRendaVariavel().getTicker() + " ID = " + codigo + " = " + usado + " :...");
    }

    public List<ControleDividendosCadastroCombobox> buscaControleDividendosCombobox() {


        List<ControleDividendosCadastroCombobox> controleDivList = new ArrayList<>();
        List<OrdensDeCompra> ordemCompraAgrupada = new ArrayList<>();
        ordemCompraAgrupada = ordemDeCompraRepository.buscaListaDeComprasAgrupadasPorProduto();

        for (int i = 0; i < ordemCompraAgrupada.size(); i++) {

            ControleDividendosCadastroCombobox controleBox = new ControleDividendosCadastroCombobox();

            controleBox.setProdutoId(ordemCompraAgrupada.get(i).getProdutoRendaVariavel().getProdutoId());
            controleBox.setTipoProdutoEnum(ordemCompraAgrupada.get(i).getTipoAtivoEnum().toString());
            controleBox.setTicker(ordemCompraAgrupada.get(i).getProdutoRendaVariavel().getTicker());

            controleDivList.add(controleBox);
        }

        return controleDivList;
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


}

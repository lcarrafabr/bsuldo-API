package com.carrafasoft.bsuldo.api.service.rendavariavel;

import com.carrafasoft.bsuldo.api.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.enums.TipoOrdemRendaVariavelEnum;
import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.model.rendavariavel.consultasdao.ConsultaRelatorioBasicoDAO;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.RelatorioBasico;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.OrdemDeCompraRepository;
import com.carrafasoft.bsuldo.braviapi.modelo.PrecoAtualCota;
import com.carrafasoft.bsuldo.braviapi.service.ConsultarProdutoSimples;
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

@Service
public class OrdemDeCompraRVService {

    @Autowired
    private OrdemDeCompraRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PersistenceContext
    private EntityManager entityManager;


    public PrecoAtualCota consultaPrecoAtualCota(String ticker, String apiToken) {

        PrecoAtualCota precoAtualCota = ConsultarProdutoSimples.buscaPrecoAtualCota(ticker, apiToken);

        return precoAtualCota;
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

    public List<RelatorioBasico> teste() {
        // Sua consulta SQL nativa
        String sql = "select " +
                "o.tipo_produto_enum as tipoProdutoEnum, " +
                "prv.ticker, " +
                "avg(preco_unitario_cota) as precoUnitarioCota, " +
                "COALESCE(SUM(quantidade_cotas), 0) AS quantidadeCotas, " +
                "sum(valor_investido) as valorInvestido, " +
                "ROUND(sum(valor_investido) / sum(quantidade_cotas), 2) as mediaInvestida " +
                "from ordens_de_compra o " +
                "inner join produtos_renda_variavel prv on prv.produto_id = o.produto_id " +
                "group by o.produto_id ";

        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(RelatorioBasico.class));

        // Executar a consulta
        List<RelatorioBasico> resultados = query.getResultList();

        return resultados;
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

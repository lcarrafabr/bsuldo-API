package com.carrafasoft.bsuldo.api.service.rendavariavel;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Lancamentos;
import com.carrafasoft.bsuldo.api.model.rendavariavel.ControleDividendos;
import com.carrafasoft.bsuldo.api.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.ControleDividendosCadastroCombobox;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.ControleDividendosRepository;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.OrdemDeCompraRepository;
import com.carrafasoft.bsuldo.api.service.consultassql.ControleDividendosSQL;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class ControleDividendosService {

    private static final Logger logger = LoggerFactory.getLogger(ControleDividendos.class);

    @Autowired
    private ControleDividendosRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OrdemDeCompraRepository ordemDeCompraRepository;

    //@Autowired
    private ControleDividendosSQL controleDividendosSQL;

    public ControleDividendos cadastrarControleDividendo(ControleDividendos controleDividendos, HttpServletResponse response) {

        logger.info("..: Iniciando o cadastro de dividendos :...");
        ControleDividendos controleDivSalvo = repository.save(controleDividendos);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, controleDivSalvo.getControleDividendoId()));

        logger.info("..: Cadastro realizado com sucesso :...: " + controleDivSalvo);

        return controleDivSalvo;
    }

    public ControleDividendos atualizarControleDividendos(Long codigo, ControleDividendos controleDividendos) {

        ControleDividendos controlDivSalvo = buscaPorId(codigo);
        BeanUtils.copyProperties(controleDividendos, controlDivSalvo, "controleDividendoId");

        return repository.save(controlDivSalvo);
    }

    public void atualizaAtatusDividendoAtivo(Long codigo, Boolean usado) {

        ControleDividendos controleDivSalvo = buscaPorId(codigo);
        controleDivSalvo.setDivUtilizado(usado);
        repository.save(controleDivSalvo);
    }

    public List<ControleDividendosCadastroCombobox> buscaControleDividendosCombobox() {


        List<ControleDividendosCadastroCombobox> controleDivList = new ArrayList<>();
        List<OrdensDeCompra> ordemCompraAgrupada = new ArrayList<>();
        ordemCompraAgrupada = ordemDeCompraRepository.buscaListaDeComprasAgrupadasPorProduto();

        for (int i = 0; i < ordemCompraAgrupada.size(); i++) {

            ControleDividendosCadastroCombobox controleBox = new ControleDividendosCadastroCombobox();

            controleBox.setProdutoId(ordemCompraAgrupada.get(i).getProdutoRendaVariavel().getProdutoId());
            controleBox.setTipoProdutoEnum(ordemCompraAgrupada.get(i).getTipoProdutoEnum().toString());
            controleBox.setTicker(ordemCompraAgrupada.get(i).getProdutoRendaVariavel().getTicker());

            controleDivList.add(controleBox);
        }

        return controleDivList;
        }



    private ControleDividendos buscaPorId(Long codigo) {

        ControleDividendos controleDividendo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return controleDividendo;
    }


}

package com.carrafasoft.bsuldo.api.service.rendavariavel;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Emissores;
import com.carrafasoft.bsuldo.api.model.rendavariavel.ProdutosRendaVariavel;
import com.carrafasoft.bsuldo.api.model.rendavariavel.Segmentos;
import com.carrafasoft.bsuldo.api.model.rendavariavel.Setores;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.ProdutosRendaVariavelRepository;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.SetoresRepository;
import com.carrafasoft.bsuldo.api.service.EmissoresService;
import com.carrafasoft.bsuldo.braviapi.service.ConsultarProdutoSimples;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoRendaVariavelService {

    public static final String INFORMACAO_NA_DESCRICAO = "O cadastro automático não pode validar os seguintes campos abaixo:\n" +
            "* Gera Dividendos;\n" +
            "* Cotas Emitidas;\n" +
            "CNPJ.\n" +
            "Os campos com * influenciam nos relatórios. Favor confirmar e ajustar esses campos";

    public static final String SETOR_NAO_ENCONTRADO = "SETOR AUTOMÁTICO NÃO ENCONTRADO";

    @Autowired
    private SetoresRepository setoresRepository;

    @Autowired
    private SetoresService setorService;

    @Autowired
    private SegmentosService segmentosService;

    @Autowired
    private EmissoresService emissorService;

    @Autowired
    private ProdutosRendaVariavelRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;


    public ProdutosRendaVariavel cadastrarProdutoRV(ProdutosRendaVariavel produtosRendaVariavel, HttpServletResponse response) {

        ProdutosRendaVariavel produtoRVSalvo = repository.save(produtosRendaVariavel);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, produtoRVSalvo.getProdutoId()));

        return produtoRVSalvo;
    }


    public ProdutosRendaVariavel cadastrarAutomaticoRV(String ticker, String tokenApi, HttpServletResponse response) {

        ProdutosRendaVariavel produtoToSave = new ProdutosRendaVariavel();
        var retornoTicker = ConsultarProdutoSimples.consultarProdutoPorTicker(ticker, tokenApi);

        String sectorName = ConsultarProdutoSimples.retornaSetor(ticker);

        if(!StringUtils.hasLength(sectorName)) {
            sectorName = SETOR_NAO_ENCONTRADO;
        }

        Setores setorId = setorService.verificaSetorCadastrado(sectorName);
        Segmentos segmentoPesquisa = segmentosService.pesquisaPorNomeSegmento(sectorName);
        List<Emissores> emissorPesquisa = emissorService.buscaEmissorPorNome(retornoTicker.getResults().get(0).getLongName());

        produtoToSave.setLongName(retornoTicker.getResults().get(0).getLongName());
        produtoToSave.setShortName(retornoTicker.getResults().get(0).getShortName());
        produtoToSave.setTicker(ticker);
        produtoToSave.setCurrency(retornoTicker.getResults().get(0).getCurrency());
        //produtoToSave.setCnpj();
        produtoToSave.setGeraDividendos(true);//Inserir na descrição
        produtoToSave.setStatus(true);
        produtoToSave.setCotasEmitidas(0L);// inserir na descrição
        produtoToSave.setLogoUrl(retornoTicker.getResults().get(0).getLogourl());
        produtoToSave.setDescricao(INFORMACAO_NA_DESCRICAO);


        if(setorId == null) {
            //Cadastrar novo setor
            Setores setorSalvo = salvarSetorAutomatico(sectorName, response);
            produtoToSave.setSetor(setorSalvo);
        } else {
            produtoToSave.setSetor(setorId);
        }

        if(segmentoPesquisa == null) {
            //Cadastrar novo segmento
            Segmentos segmentoSalvo = salvarSegmentoAutomatico(sectorName, response);
            produtoToSave.setSegmento(segmentoSalvo);

        } else {
            produtoToSave.setSegmento(segmentoPesquisa);
        }

        if(emissorPesquisa.isEmpty()) {
            //CadastrarEmissor
            Emissores emissorSalvo =  salvarEmissorAutomatico(retornoTicker.getResults().get(0).getLongName(), response);
            produtoToSave.setEmissor(emissorSalvo);

        } else {
            produtoToSave.setEmissor(emissorPesquisa.get(0));
        }

        return cadastrarProdutoRV(produtoToSave, response);
    }

    public ProdutosRendaVariavel atualizarProdutoRV(Long codigo, ProdutosRendaVariavel produtosRendaVariavel) {

        ProdutosRendaVariavel produtoSalvo = buscaPorId(codigo);
        BeanUtils.copyProperties(produtosRendaVariavel, produtoSalvo, "produtoId");

        return repository.save(produtoSalvo);
    }

    private Setores salvarSetorAutomatico(String sector, HttpServletResponse response) {

        Setores setor = new Setores();
        setor.setNomeSetor(sector);
        setor.setStatus(true);

        Setores setorSalvo = new Setores();
        setorSalvo = setorService.cadastrarSetorAutomatico(setor, response);

        return  setorSalvo;
    }

    private Segmentos salvarSegmentoAutomatico(String sectorName, HttpServletResponse response) {
        Segmentos segmentoToSave = new Segmentos();
        segmentoToSave.setNomeSegmento(sectorName);
        segmentoToSave.setStatus(true);

        Segmentos segmentoSalvo = new Segmentos();
        segmentoSalvo = segmentosService.cadastrarSegmentoAutomatico(segmentoToSave, response);

        return  segmentoSalvo;
    }

    private Emissores salvarEmissorAutomatico(String nomeEmissor, HttpServletResponse response) {

        Emissores emissorToSave = new Emissores();
        emissorToSave.setNomeEmissor(nomeEmissor);
        emissorToSave.setStatus(true);
        emissorToSave.setDataCadastro(LocalDate.now());

        Emissores emissorSalvo = new Emissores();
        emissorSalvo = emissorService.cadastrarEmissorAutomatico(emissorToSave, response);

        return emissorSalvo;
    }

    private ProdutosRendaVariavel buscaPorId(Long codigo) {

        ProdutosRendaVariavel produtoSalvo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return  produtoSalvo;
    }
}

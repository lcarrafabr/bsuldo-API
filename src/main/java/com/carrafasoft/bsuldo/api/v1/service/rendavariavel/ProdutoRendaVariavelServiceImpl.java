package com.carrafasoft.bsuldo.api.v1.service.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.enums.AvaiableSectorsEnum;
import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutoRVResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutoRvInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutosRVInputUpdateRerpesentation;
import com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel.ProdutoRendaVariavelMapper;
import com.carrafasoft.bsuldo.api.v1.model.Emissores;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.exceptionmodel.ProdutoRVNaoEncontradoException;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Segmentos;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Setores;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.ProdutosRendaVariavelRepository;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.SegmentosRepository;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.SetoresRepository;
import com.carrafasoft.bsuldo.api.v1.service.EmissoresService;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.ProdutoRendaVariavelService;
import com.carrafasoft.bsuldo.braviapi.service.ConsultarProdutoSimples;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoRendaVariavelServiceImpl implements ProdutoRendaVariavelService {

    public static final String INFORMACAO_NA_DESCRICAO = "O cadastro automático não pode validar os seguintes campos abaixo:\n" +
            "* Gera Dividendos;\n" +
            "* Cotas Emitidas;\n" +
            "CNPJ.\n" +
            "Os campos com * influenciam nos relatórios. Favor confirmar e ajustar esses campos";

    public static final String SETOR_NAO_ENCONTRADO = "SETOR AUTOMÁTICO NÃO ENCONTRADO";

    private static final String PRODUTO_EM_USO = "O produto de código %s não pode ser removido pois está em uso.";

    @Autowired
    private SetoresRepository setoresRepository;

    @Autowired
    private SetoresServiceImpl setorService;

    @Autowired
    private SegmentosService segmentosService;

    @Autowired
    private EmissoresService emissorService;

    @Autowired
    private ProdutosRendaVariavelRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ProdutoRendaVariavelMapper produtoRendaVariavelMapper;

    @Autowired
    private SegmentosRepository segmentoRepository;

    @Autowired
    private SetoresRepository setorRepository;


    @Transactional
    public ProdutosRendaVariavel cadastrarProdutoRV(ProdutosRendaVariavel produtosRendaVariavel, HttpServletResponse response) {

        ProdutosRendaVariavel produtoRVSalvo = repository.save(produtosRendaVariavel);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, produtoRVSalvo.getProdutoId()));

        return produtoRVSalvo;
    }


    @Transactional
    @Override
    public ProdutosRendaVariavel preparaCadastroProdutoRVManual(ProdutoRvInputRepresentation produtosRendaVariavel, HttpServletResponse response,
                                                    String tokenId) {

        try {
            Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));

            Segmentos segmentoSalvo = segmentosService.findByCodigoSegmentoAndTokenId(
                    produtosRendaVariavel.getSegmento().getCodigoSegmento(), tokenId);

            Setores setorSalvo = setorService.findByCodigoSetorAndTokenId(
                    produtosRendaVariavel.getSetor().getCodigoSetor(), tokenId);

            ProdutosRendaVariavel produtoRVToSave = produtoRendaVariavelMapper.toEntity(produtosRendaVariavel,
                    pessoaSalva,
                    segmentoSalvo,
                    setorSalvo);

            return cadastrarProdutoRV(produtoRVToSave, response);

        } catch (ProdutoRVNaoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }


    @Transactional
    @Override
    public ProdutosRendaVariavel cadastrarAutomaticoRV(String ticker, String tokenApi, HttpServletResponse response, String tokenId) {

        ProdutosRendaVariavel produtoToSave = new ProdutosRendaVariavel();
        var retornoTicker = ConsultarProdutoSimples.consultarProdutoPorTicker(ticker, tokenApi);
        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);
        Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaId);

        produtoToSave.setPessoa(pessoaSalva);

        String sectorName = ConsultarProdutoSimples.retornaSetor(ticker);

        if(!StringUtils.hasLength(sectorName)) {
            sectorName = SETOR_NAO_ENCONTRADO;
        } else {
            sectorName = AvaiableSectorsEnum.getSetorTraduzido(sectorName);
        }

        Setores setorId = setorService.verificaSetorCadastrado(sectorName, pessoaId);
        Segmentos segmentoPesquisa = segmentosService.pesquisaPorNomeSegmento(sectorName, pessoaId);
        List<Emissores> emissorPesquisa = emissorService.buscaEmissorPorNome(retornoTicker.getResults().get(0).getLongName(), tokenId);

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
            Setores setorSalvo = salvarSetorAutomatico(sectorName, response, pessoaSalva);
            produtoToSave.setSetor(setorSalvo);
        } else {
            produtoToSave.setSetor(setorId);
        }

        if(segmentoPesquisa == null) {
            //Cadastrar novo segmento
            Segmentos segmentoSalvo = salvarSegmentoAutomatico(sectorName, response, pessoaSalva);
            produtoToSave.setSegmento(segmentoSalvo);

        } else {
            produtoToSave.setSegmento(segmentoPesquisa);
        }

        if(emissorPesquisa.isEmpty()) {
            //CadastrarEmissor
            Emissores emissorSalvo =  salvarEmissorAutomatico(retornoTicker.getResults().get(0).getLongName(), response, pessoaSalva);
            produtoToSave.setEmissor(emissorSalvo);

        } else {
            produtoToSave.setEmissor(emissorPesquisa.get(0));
        }

        return cadastrarProdutoRV(produtoToSave, response);
    }

    @Override
    public ProdutosRendaVariavel findByCodigoProdutoRVAndTokenId(String codigoProdutoRV, String tokenId) {

        return repository.findByCodigoProdutoRVAndPessoaId(codigoProdutoRV, pessoaService.recuperaIdPessoaByToken(tokenId))
                .orElseThrow(() -> new ProdutoRVNaoEncontradoException(codigoProdutoRV));
    }

    @Transactional
    @Override
    public ProdutoRVResponseRepresentation atualizarProdutoRV(String codigo,
                                                              ProdutosRVInputUpdateRerpesentation produtosRVInputUpdateRerpesentation,
                                                              String tokenId) {
        try {
            ProdutosRendaVariavel produtoRVSalvo = findByCodigoProdutoRVAndTokenId(codigo, tokenId);

            // Copia os campos simples (sem sobrescrever o ID e o código gerado)
            BeanUtils.copyProperties(produtosRVInputUpdateRerpesentation, produtoRVSalvo, "produtoId", "codigoProdutoRV", "segmento", "setor");

            // Atualiza o SEGMENTO (buscando a entidade completa)
            if (produtosRVInputUpdateRerpesentation.getSegmento() != null && produtosRVInputUpdateRerpesentation.getSegmento().getCodigoSegmento() != null) {
                Segmentos segmento = segmentoRepository.findByCodigoSegmentoAndTokenId(produtosRVInputUpdateRerpesentation.getSegmento().getCodigoSegmento(),
                                pessoaService.recuperaIdPessoaByToken(tokenId))
                        .orElseThrow(() -> new NegocioException("Segmento não encontrado"));
                produtoRVSalvo.setSegmento(segmento);
            }

            // Atualiza o SETOR (buscando a entidade completa)
            if (produtosRVInputUpdateRerpesentation.getSetor() != null && produtosRVInputUpdateRerpesentation.getSetor().getCodigoSetor() != null) {
                Setores setor = setorRepository.findByCodigoSetorAndPessoaId(produtosRVInputUpdateRerpesentation.getSetor().getCodigoSetor(),
                                pessoaService.recuperaIdPessoaByToken(tokenId))
                        .orElseThrow(() -> new NegocioException("Setor não encontrado"));
                produtoRVSalvo.setSetor(setor);
            }

            // Salva a entidade e retorna o DTO de resposta
            return produtoRendaVariavelMapper.toProdutoRVResponseRepresentation(
                    repository.save(produtoRVSalvo)
            );

        } catch (ProdutoRVNaoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void removerProduto(String codigoProdutoRv) {

        try {
            repository.deleteByCodigoProdutoRV(codigoProdutoRv);
        } catch (EmptyResultDataAccessException e) {
            throw new ProdutoRVNaoEncontradoException(codigoProdutoRv);
        } catch (DataIntegrityViolationException e) {
            throw new NegocioException(String.format(PRODUTO_EM_USO, codigoProdutoRv));
        }
    }


    private Setores salvarSetorAutomatico(String sector, HttpServletResponse response, Pessoas pessoaSalva) {

        Setores setor = new Setores();
        setor.setPessoa(pessoaSalva);
        setor.setNomeSetor(sector);
        setor.setStatus(true);

        Setores setorSalvo = new Setores();
        setorSalvo = setorService.cadastrarSetorAutomatico(setor, response);

        return  setorSalvo;
    }

    private Segmentos salvarSegmentoAutomatico(String sectorName, HttpServletResponse response, Pessoas pessoaSalva) {

        Segmentos segmentoToSave = new Segmentos();
        segmentoToSave.setPessoa(pessoaSalva);
        segmentoToSave.setNomeSegmento(sectorName);
        segmentoToSave.setStatus(true);

        Segmentos segmentoSalvo = new Segmentos();
        segmentoSalvo = segmentosService.cadastrarSegmentoAutomatico(segmentoToSave, response);

        return  segmentoSalvo;
    }

    private Emissores salvarEmissorAutomatico(String nomeEmissor, HttpServletResponse response, Pessoas pessoaSalva) {

        Emissores emissorToSave = new Emissores();
        emissorToSave.setPessoa(pessoaSalva);
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

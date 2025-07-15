package com.carrafasoft.bsuldo.api.v1.controller.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutoRVResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutoRvInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutosRVInputUpdateRerpesentation;
import com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel.ProdutoRendaVariavelMapper;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.ProdutosRendaVariavelRepository;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.ProdutoRendaVariavelService;
import com.carrafasoft.bsuldo.api.v1.service.rendavariavel.ProdutoRendaVariavelServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/produto-renda-variavel")
public class ProdutoRendaVariavelController {

    private final ProdutosRendaVariavelRepository repository;
    private final ProdutoRendaVariavelServiceImpl serviceImpl;
    private final PessoaService pessoaService;
    private final String apiToken;

    private final ProdutoRendaVariavelMapper produtoRendaVariavelMapper;
    private final ProdutoRendaVariavelService service;

    public ProdutoRendaVariavelController(
            ProdutosRendaVariavelRepository repository,
            ProdutoRendaVariavelServiceImpl serviceImpl,
            PessoaService pessoaService,
            @Value("${bsuldo.tokemApiBravi.tokemApiBravi}") String apiToken,
            ProdutoRendaVariavelMapper produtoRendaVariavelMapper,
            ProdutoRendaVariavelService service) {

        this.repository = repository;
        this.serviceImpl = serviceImpl;
        this.pessoaService = pessoaService;
        this.apiToken = apiToken;
        this.produtoRendaVariavelMapper = produtoRendaVariavelMapper;
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<ProdutoRVResponseRepresentation>> findAll(@RequestParam("tokenId") String tokenId) {

        return ResponseEntity.ok(produtoRendaVariavelMapper.toListProdutoRendaVariavelRepresentation(
                repository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId))));
    }

    @PostMapping("/automatico")
    public ResponseEntity<ProdutoRVResponseRepresentation> cadastrarAutomaticoProdutoRV(@RequestParam("ticker") String ticker, HttpServletResponse response,
                                                                              @RequestParam("tokenId") String tokenId) {

        ProdutosRendaVariavel produtoSalvo = service.cadastrarAutomaticoRV(ticker, apiToken, response, tokenId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                produtoRendaVariavelMapper.toProdutoRVResponseRepresentation(produtoSalvo)
        );
    }

    @PostMapping
    public ResponseEntity<ProdutoRVResponseRepresentation> cadastroManualRendaVariavel(@Valid @RequestBody ProdutoRvInputRepresentation produtoRvInputRepresentation,
                                                                             HttpServletResponse response,
                                                                             @RequestParam("tokenId") String tokenId) {

        ProdutosRendaVariavel produtoSalvo = service.preparaCadastroProdutoRVManual(produtoRvInputRepresentation, response, tokenId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                produtoRendaVariavelMapper.toProdutoRVResponseRepresentation(produtoSalvo)
        );
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ProdutoRVResponseRepresentation> buscaPorId(@PathVariable String codigo,
                                                                      @RequestParam("tokenId") String tokenId) {

        ProdutosRendaVariavel produtoSalvo = service.findByCodigoProdutoRVAndTokenId(codigo, tokenId);

        return ResponseEntity.ok(produtoRendaVariavelMapper.toProdutoRVResponseRepresentation(produtoSalvo));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ProdutoRVResponseRepresentation> atualizarProdutoRendaVariavel(@PathVariable String codigo,
                                                                               @Valid @RequestBody ProdutosRVInputUpdateRerpesentation produtosRVInputUpdateRerpesentation,
                                                                               @RequestParam("tokenId") String tokenId) {


        ProdutoRVResponseRepresentation produtoEditado = service.atualizarProdutoRV(codigo, produtosRVInputUpdateRerpesentation, tokenId);

        return ResponseEntity.ok(produtoEditado);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerProdutoRV(@PathVariable String codigo) {

        service.removerProduto(codigo);
    }

    @GetMapping("/busca-por-nome-produto-rv")
    public List<ProdutosRendaVariavel> buscaPorNomeProdutoRV(@RequestParam("ticker") String ticker) {

        if(StringUtils.hasLength(ticker)) {
            ticker = ticker.trim();
        }
        return repository.buscaPorNomeProdutoRV(ticker);
    }


}

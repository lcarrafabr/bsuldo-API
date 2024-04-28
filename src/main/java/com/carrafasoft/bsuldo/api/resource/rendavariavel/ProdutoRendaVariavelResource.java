package com.carrafasoft.bsuldo.api.resource.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.ProdutosRendaVariavel;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.ProdutosRendaVariavelRepository;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import com.carrafasoft.bsuldo.api.service.rendavariavel.ProdutoRendaVariavelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produto-renda-variavel")
public class ProdutoRendaVariavelResource {

    @Autowired
    private ProdutosRendaVariavelRepository repository;

    @Autowired
    private ProdutoRendaVariavelService service;

    @Autowired
    private PessoaService pessoaService;

    @Value("${bsuldo.tokemApiBravi.tokemApiBravi}") // Anotação para ler o valor da propriedade do arquivo application.properties
    private String apiToken;

    @GetMapping
    public List<ProdutosRendaVariavel> findAll(@RequestParam("tokenId") String tokenId) {

        return repository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId));
    }

    @PostMapping("/automatico")
    public ResponseEntity<ProdutosRendaVariavel> cadastrarAutomaticoProdutoRV(@RequestParam("ticker") String ticker, HttpServletResponse response,
                                                                              @RequestParam("tokenId") String tokenId) {

        ProdutosRendaVariavel produtoSalvo = service.cadastrarAutomaticoRV(ticker, apiToken, response, tokenId);

        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    @PostMapping
    public ResponseEntity<ProdutosRendaVariavel> cadastroManualRendaVariavel(@Valid @RequestBody ProdutosRendaVariavel produtosRendaVariavel,
                                                                             HttpServletResponse response,
                                                                             @RequestParam("tokenId") String tokenId) {

        ProdutosRendaVariavel produtoSalvo = service.cadastrarProdutoRV(produtosRendaVariavel, response, tokenId);

        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ProdutosRendaVariavel> buscaPorId(@PathVariable Long codigo, @RequestParam("tokenId") String tokenId) {

        Optional<ProdutosRendaVariavel> produtoRVSalvo = repository.findByIdAndPessoaId(codigo, pessoaService.recuperaIdPessoaByToken(tokenId));

        return produtoRVSalvo.isPresent() ? ResponseEntity.ok(produtoRVSalvo.get()) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ProdutosRendaVariavel> atualizarProdutoRendaVariavel(@PathVariable Long codigo,
                                                                               @Valid @RequestBody ProdutosRendaVariavel produtosRendaVariavel,
                                                                               @RequestParam("tokenId") String tokenId) {

        ProdutosRendaVariavel produtoEditado = service.atualizarProdutoRV(codigo, produtosRendaVariavel, tokenId);

        return ResponseEntity.ok(produtoEditado);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerProdutoRV(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }

    @GetMapping("/busca-por-nome-produto-rv")
    public List<ProdutosRendaVariavel> buscaPorNomeProdutoRV(@RequestParam("ticker") String ticker) {

        if(StringUtils.hasLength(ticker)) {
            ticker = ticker.trim();
        }
        return repository.buscaPorNomeProdutoRV(ticker);
    }


}

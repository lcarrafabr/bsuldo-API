package com.carrafasoft.bsuldo.api.controller.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.*;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.OrdemDeCompraRepository;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import com.carrafasoft.bsuldo.api.service.rendavariavel.OrdemDeCompraRVService;
import com.carrafasoft.bsuldo.braviapi.modelo.PrecoAtualCota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ordens-de-compra")
public class OrdensDeCompraResource {

    @Autowired
    private OrdemDeCompraRepository repository;

    @Autowired
    private OrdemDeCompraRVService service;

    @Autowired
    private PessoaService pessoaService;

    @Value("${bsuldo.tokemApiBravi.tokemApiBravi}") // Anotação para ler o valor da propriedade do arquivo application.properties
    private String apiToken;


    @GetMapping
    public List<OrdensDeCompra> findAll(@RequestParam("tokenId") String tokenId) {

        return repository.findAllDesc(pessoaService.recuperaIdPessoaByToken(tokenId));
    }

    @GetMapping("/consulta-valor-atual-cota")
    public PrecoAtualCota consultaPrecoAtualCota(@RequestParam("ticker") String ticker) {

        //Busca preço na API de cotação
        return service.consultaPrecoAtualCota(ticker, apiToken);
    }

    @PostMapping
    public ResponseEntity<OrdensDeCompra> cadastrarOrdemDeCompraVenda(@Valid @RequestBody OrdensDeCompra ordemCompra, HttpServletResponse response,
                                                                      @RequestParam("tokenId") String tokenId) {

        OrdensDeCompra ordemDeCompraSalva = service.cadastrarOrdemDeCompraVenda(ordemCompra, response, tokenId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ordemDeCompraSalva);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<OrdensDeCompra> buscaPorId(@PathVariable Long codigo, @RequestParam("tokenId") String tokenId) {

        Optional<OrdensDeCompra> ordemSalva = repository.findByIdAndPessoaId(codigo, pessoaService.recuperaIdPessoaByToken(tokenId));

        return ordemSalva.isPresent() ? ResponseEntity.ok(ordemSalva.get()) : ResponseEntity.noContent().build();
    }


    @PutMapping("{codigo}")
    public ResponseEntity<OrdensDeCompra> editarOrdemCCompraVenda(@PathVariable Long codigo, @Valid @RequestBody OrdensDeCompra ordensDeCompra,
                                                                  @RequestParam("tokenId") String tokenId) {

        OrdensDeCompra ordemCompraEditada = service.atualizarOrdemCompraVenda(codigo, ordensDeCompra, tokenId);

        return ResponseEntity.ok(ordemCompraEditada);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerOrdemCompraVenda(@PathVariable Long codigo) {
        repository.deleteById(codigo);
    }

    //************************ RELATÓRIOS *********************************************************

    @GetMapping("/relatorio-basico")
    public List<RelatorioBasico> listarRelatorioBasico(@RequestParam("tokenId") String tokenId) {

        //List<RelatorioBasico> relatorioBasico = service.listarRelatorioBasico(repository.findAll());
        List<RelatorioBasico> relatorioBasico = service.relatorioBasicoRendaVariavel(tokenId);

        return relatorioBasico;
    }

    @GetMapping("/relatorio-basico-visualizacao")
    public List<RelatorioBasico> listarRelatorioBasicoVisualizacao(@RequestParam("tokenId") String tokenId) {

        List<RelatorioBasico> relatorioBasico = service.relatorioBasicoVisualizacaoRV(tokenId);

        return relatorioBasico;
    }

    @GetMapping("/relatorio-acoes-fiis")
    public List<RelatorioPercentualAcoesFiis> buscaRelatorioPercentualAcoesFiis(@RequestParam("tokenId") String tokenId) {

        List<RelatorioPercentualAcoesFiis> relatorioAcoesFiis = service.relatorioPercentualAcoesFiis(tokenId);

        return  relatorioAcoesFiis;
    }

    @GetMapping("/relatorio-acoes-fiis-renda-fixa")
    public List<RelatorioPercentualAcoesFiis> buscaRelatorioPercentualAcoesFiisRendaFixa(@RequestParam("tokenId") String tokenId) {

        List<RelatorioPercentualAcoesFiis> relatorioAcoesFiis = service.relatorioPercentualAcoesFiisRendaFixa(tokenId);

        return  relatorioAcoesFiis;
    }

    @GetMapping("/relatorio-completo")
    public List<RelatorioCompletoRendaVariavel> listarRelatorioCompletoRV(@RequestParam("tipoProduto") String tipoProduto,
                                                                          @RequestParam("tokenId") String tokenId) {

        List<RelatorioCompletoRendaVariavel> relatorioBasico = service.relatorioComplertoRV(apiToken, tipoProduto, tokenId);

        return relatorioBasico;
    }

    @GetMapping("/busca-ordem-por-nome-ticker")
    public List<OrdensDeCompra> buscaPorNomeTicker(@RequestParam("ticker") String ticker,
                                                   @RequestParam("tokenId") String tokenId) {

        return repository.buscaPorNomeProduto(ticker, pessoaService.recuperaIdPessoaByToken(tokenId));
    }

    @GetMapping("/valor-total-investido-rv")
    public TotalInvestidoRendaVariavelDTO buscaTotalInvestidoRV(@RequestParam("tokenId") String tokenId) {

        TotalInvestidoRendaVariavelDTO totalInvestido = new TotalInvestidoRendaVariavelDTO();
        BigDecimal valor = repository.valorTotalInvestidoRV(pessoaService.recuperaIdPessoaByToken(tokenId));
        totalInvestido.setValorTotalInvestidoRendaVariavel(valor);
        return totalInvestido;
    }

    @GetMapping("/relatorio-basico-com-div-recebido")
    public List<RelatorioBasicoComTotalDiviREcebido> relatorioBasicoComDivRecebido(@RequestParam("tokenId") String tokenId) {

        List<RelatorioBasicoComTotalDiviREcebido> retorno = service.relatorioBasicoComDividendoRecebido(tokenId);

        return retorno;
    }






}

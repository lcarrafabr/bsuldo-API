package com.carrafasoft.bsuldo.api.v1.controller.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.OrdemDeCompraInputReppresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.OrdemDeCompraInputUpdateRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.OrdemDeCompraResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel.OrdemDeCompraMapper;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.*;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.OrdemDeCompraRepository;
import com.carrafasoft.bsuldo.api.v1.service.OrdemDeCompraRVService;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.rendavariavel.OrdemDeCompraRVServiceImpl;
import com.carrafasoft.bsuldo.braviapi.modelo.PrecoAtualCota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/ordens-de-compra", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrdensDeCompraController {

    @Autowired
    private OrdemDeCompraRepository repository;

    @Autowired
    private OrdemDeCompraRVServiceImpl serviceImpl;

    @Autowired
    private OrdemDeCompraRVService service;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private OrdemDeCompraMapper ordemDeCompraMapper;

    @Value("${bsuldo.tokemApiBravi.tokemApiBravi}") // Anotação para ler o valor da propriedade do arquivo application.properties
    private String apiToken;


    @GetMapping
    public ResponseEntity<List<OrdemDeCompraResponseRepresentation>> findAll(@RequestParam("tokenId") String tokenId) {

        var ordensDeCompraList = service.findAll(tokenId);

        return ResponseEntity.ok(ordemDeCompraMapper.toListOrdemDeCompraRepresentation(ordensDeCompraList));
    }

    @GetMapping("/consulta-valor-atual-cota")
    public PrecoAtualCota consultaPrecoAtualCota(@RequestParam("ticker") String ticker) {

        //Busca preço na API de cotação
        return service.consultaPrecoAtualCota(ticker, apiToken);
    }

    @PostMapping
    public ResponseEntity<OrdemDeCompraResponseRepresentation> cadastrarOrdemDeCompraVenda(@Valid @RequestBody OrdemDeCompraInputReppresentation ordemCompra, HttpServletResponse response,
                                                                      @RequestParam("tokenId") String tokenId) {

        OrdensDeCompra ordemDeCompraSalva = service.cadastrarOrdemDeCompraVenda(ordemCompra, response, tokenId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ordemDeCompraMapper.toOrdemDeCompraResponseRepresentation(ordemDeCompraSalva)
        );
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<OrdemDeCompraResponseRepresentation> buscaPorId(@PathVariable String codigo, @RequestParam("tokenId") String tokenId) {

        var ordemSalva = service.buscaPorCodidoOrdemDeCompraAndTokenId(codigo, tokenId);

        return ResponseEntity.ok(ordemDeCompraMapper.toOrdemDeCompraResponseRepresentation(ordemSalva));
    }


    @PutMapping("{codigo}")
    public ResponseEntity<OrdensDeCompra> editarOrdemCCompraVenda(@PathVariable String codigo,
                                                                  @Valid @RequestBody OrdemDeCompraInputUpdateRepresentation ordensDeCompra,
                                                                  @RequestParam("tokenId") String tokenId) {

        OrdensDeCompra ordemCompraEditada = serviceImpl.atualizarOrdemCompraVenda(codigo, ordensDeCompra, tokenId);

        return ResponseEntity.ok(ordemCompraEditada);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerOrdemCompraVenda(@PathVariable String codigo) {

        service.deleteByCodigo(codigo);
    }

    //************************ RELATÓRIOS *********************************************************

    @GetMapping("/relatorio-basico")
    public List<RelatorioBasico> listarRelatorioBasico(@RequestParam("tokenId") String tokenId) {

        //List<RelatorioBasico> relatorioBasico = service.listarRelatorioBasico(repository.findAll());
        List<RelatorioBasico> relatorioBasico = serviceImpl.relatorioBasicoRendaVariavel(tokenId);

        return relatorioBasico;
    }

    @GetMapping("/relatorio-basico-visualizacao")
    public List<RelatorioBasico> listarRelatorioBasicoVisualizacao(@RequestParam("tokenId") String tokenId) {

        List<RelatorioBasico> relatorioBasico = serviceImpl.relatorioBasicoVisualizacaoRV(tokenId);

        return relatorioBasico;
    }

    @GetMapping("/relatorio-acoes-fiis")
    public List<RelatorioPercentualAcoesFiis> buscaRelatorioPercentualAcoesFiis(@RequestParam("tokenId") String tokenId) {

        List<RelatorioPercentualAcoesFiis> relatorioAcoesFiis = serviceImpl.relatorioPercentualAcoesFiis(tokenId);

        return  relatorioAcoesFiis;
    }

    @GetMapping("/relatorio-acoes-fiis-renda-fixa")
    public List<RelatorioPercentualAcoesFiis> buscaRelatorioPercentualAcoesFiisRendaFixa(@RequestParam("tokenId") String tokenId) {

        List<RelatorioPercentualAcoesFiis> relatorioAcoesFiis = serviceImpl.relatorioPercentualAcoesFiisRendaFixa(tokenId);

        return  relatorioAcoesFiis;
    }

    @GetMapping("/relatorio-completo")
    public List<RelatorioCompletoRendaVariavel> listarRelatorioCompletoRV(@RequestParam("tipoProduto") String tipoProduto,
                                                                          @RequestParam("tokenId") String tokenId) {

        List<RelatorioCompletoRendaVariavel> relatorioBasico = serviceImpl.relatorioComplertoRV(apiToken, tipoProduto, tokenId);

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

        List<RelatorioBasicoComTotalDiviREcebido> retorno = serviceImpl.relatorioBasicoComDividendoRecebido(tokenId);

        return retorno;
    }






}

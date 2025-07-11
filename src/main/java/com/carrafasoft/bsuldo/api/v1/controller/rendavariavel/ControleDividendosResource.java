package com.carrafasoft.bsuldo.api.v1.controller.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ControleDividendos;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.ControleDividendosCadastroCombobox;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.GraficoDividendosRecebidosPorMesEAno;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.TotalDivDisponivelDTO;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.TotalDivRecebidoDTO;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.ControleDividendosRepository;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.rendavariavel.ControleDividendosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/controle-dividendos")
public class ControleDividendosResource {

    @Autowired
    private ControleDividendosRepository repository;

    @Autowired
    private ControleDividendosService service;

    @Autowired
    private PessoaService pessoaService;


    @GetMapping
    public List<ControleDividendos> findAll(@RequestParam("tokenId") String tokenId,
                                            @RequestParam(value = "ticker", required = false) String ticker,
                                            @RequestParam(value = "tipoRecebimento", required = false) String tipoRecebimento,
                                            @RequestParam(value = "dataReferencia", required = false) String dataReferencia,
                                            @RequestParam(value = "dataPagamento", required = false) String dataPagamento) {

        return service.listarTodosOsDividendos(tokenId, ticker, tipoRecebimento, dataReferencia, dataPagamento);
    }

    @PostMapping
    public ResponseEntity<ControleDividendos> cadastrarControlDiv(@Valid @RequestBody ControleDividendos controleDividendo, HttpServletResponse response,
                                                                  @RequestParam("tokenId") String tokenId) {

        ControleDividendos controleDividendoSolvo = service.cadastrarControleDividendo(controleDividendo,response, tokenId);

        return ResponseEntity.status(HttpStatus.CREATED).body(controleDividendoSolvo);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ControleDividendos> buscaPorId(@PathVariable Long codigo) {

        Optional<ControleDividendos> controleDivSalvo = repository.findById(codigo);

        return controleDivSalvo.isPresent() ? ResponseEntity.ok(controleDivSalvo.get()) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ControleDividendos> atualizarControlDiv(@PathVariable Long codigo,
                                                                  @Valid @RequestBody ControleDividendos controleDividendos,
                                                                  @RequestParam("tokenId") String tokenId) {

        ControleDividendos controlDivAtualizado = service.atualizarControleDividendos(codigo, controleDividendos, tokenId);

        return ResponseEntity.ok(controlDivAtualizado);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerControleDividendo(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }

    //******************************************************************************************************************

    @PutMapping("/{codigo}/divUsado")
    @ResponseStatus(HttpStatus.OK)
    public void atualizaAtatusDivAtivo(@PathVariable Long codigo, @RequestBody Boolean divUsado) {
        service.atualizaAtatusDividendoAtivo(codigo, divUsado);
    }

    @GetMapping("/busca-ticker-combobox")
    public List<ControleDividendosCadastroCombobox> buscaControleDividendosCombobox(@RequestParam("tokenId") String tokenId) {

        List<ControleDividendosCadastroCombobox> list = service.buscaControleDividendosCombobox(tokenId);

        return list;
    }

    //******************************************************************************************************************

    @GetMapping("/valor-total-div-recebido")
    public TotalDivRecebidoDTO valorTotalDivsRecebidos(@RequestParam("idToken") String idToken) {

        TotalDivRecebidoDTO totalDiv = new TotalDivRecebidoDTO();

        BigDecimal valor = repository.valorTotalDivRecebido(pessoaService.recuperaIdPessoaByToken(idToken));
        totalDiv.setTotalDivRecebido(valor);

        return totalDiv;
    }

    @GetMapping("/valor-total-div-disponivel")
    public TotalDivDisponivelDTO valorTotalDivDisponivel(@RequestParam("tokenId") String tokenId) {

        TotalDivDisponivelDTO totalDivDisp = new TotalDivDisponivelDTO();

        BigDecimal valorDisp = repository.valorTotalDivDisponivel(pessoaService.recuperaIdPessoaByToken(tokenId));
        totalDivDisp.setTotalDivDisponivel(valorDisp);

        return totalDivDisp;
    }

    //TODO não lembro desse endpoint verificar ele, no dashboard investimentos tem o dados-dividendos-por-mes-e-ano que não mostra nada
    @GetMapping("/dados-dividendos-por-mes-e-ano")
    public List<GraficoDividendosRecebidosPorMesEAno> getDadosGraficoDivMesEAno(@RequestParam("idToken") String idToken,
                                                                                @RequestParam("ano") String ano,
                                                                                @RequestParam("mes") String mes) {

        return service.getDadosGraficoDividendosPorMesEAno(ano,
                mes, pessoaService.recuperaIdPessoaByToken(idToken));
    }

    @GetMapping("/verifica-div-a-receber-manual")
    public void verificaDividendoAReceberManual() {

        service.verificaDividendoAReceber();
    }
}

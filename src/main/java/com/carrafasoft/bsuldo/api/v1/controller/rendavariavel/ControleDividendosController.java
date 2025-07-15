package com.carrafasoft.bsuldo.api.v1.controller.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ControleDividendoInput;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ControleDividendosInputUpdate;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ControleDividendosResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel.ControleDividendosMapper;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ControleDividendos;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.ControleDividendosCadastroCombobox;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.GraficoDividendosRecebidosPorMesEAno;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.TotalDivDisponivelDTO;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.TotalDivRecebidoDTO;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.ControleDividendosRepository;
import com.carrafasoft.bsuldo.api.v1.service.ControleDividendoService;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.rendavariavel.ControleDividendosServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(path = "/controle-dividendos", produces = MediaType.APPLICATION_JSON_VALUE)
public class ControleDividendosController {


    private final ControleDividendosRepository repository;
    private final ControleDividendosServiceImpl serviceImpl;
    private final PessoaService pessoaService;

    private final ControleDividendoService service;

    private final ControleDividendosMapper controleDividendosMapper;

    public ControleDividendosController(ControleDividendosRepository repository, ControleDividendosServiceImpl serviceImpl,
                                        PessoaService pessoaService, ControleDividendoService service,
                                        ControleDividendosMapper controleDividendosMapper) {
        this.repository = repository;
        this.serviceImpl = serviceImpl;
        this.pessoaService = pessoaService;
        this.service = service;
        this.controleDividendosMapper = controleDividendosMapper;
    }

    @GetMapping
    public ResponseEntity<List<ControleDividendosResponseRepresentation>> findAll(@RequestParam("tokenId") String tokenId,
                                            @RequestParam(value = "ticker", required = false) String ticker,
                                            @RequestParam(value = "tipoRecebimento", required = false) String tipoRecebimento,
                                            @RequestParam(value = "dataReferencia", required = false) String dataReferencia,
                                            @RequestParam(value = "dataPagamento", required = false) String dataPagamento) {

        List<ControleDividendos> findAll = service.listarTodosOsDividendos(tokenId, ticker, tipoRecebimento, dataReferencia, dataPagamento);

        return ResponseEntity.ok(controleDividendosMapper.toListControleDividendoRepresentation(findAll));
    }

    @PostMapping
    public ResponseEntity<ControleDividendos> cadastrarControlDiv(@Valid @RequestBody ControleDividendoInput controleDividendo, HttpServletResponse response,
                                                                  @RequestParam("tokenId") String tokenId) {

        ControleDividendos controleDividendoSolvo = service.cadastrarControleDividendo(controleDividendo,response, tokenId);

        return ResponseEntity.status(HttpStatus.CREATED).body(controleDividendoSolvo);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ControleDividendosResponseRepresentation> buscaPorId(@PathVariable String codigo,
                                                         @RequestParam("tokenId") String tokenId) {

        ControleDividendos controleDividendos = service.findByCodigoControleDivAndTokenId(codigo, tokenId);

        return ResponseEntity.ok(controleDividendosMapper.toControleDividendoRepresentation(controleDividendos));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ControleDividendosResponseRepresentation> atualizarControlDiv(@PathVariable String codigo,
                                                                  @Valid @RequestBody ControleDividendosInputUpdate controleDividendos,
                                                                  @RequestParam("tokenId") String tokenId) {

        ControleDividendos controlDivAtualizado = service.atualizarControleDividendos(codigo, controleDividendos, tokenId);

        return ResponseEntity.ok(controleDividendosMapper.toControleDividendoRepresentation(controlDivAtualizado));
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerControleDividendo(@PathVariable String codigo) {

        service.removerControleDividendo(codigo);
    }

    //******************************************************************************************************************

    @PutMapping("/{codigo}/divUsado")
    @ResponseStatus(HttpStatus.OK)
    public void atualizaAtatusDivAtivo(@PathVariable String codigo, @RequestBody Boolean divUsado) {

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

        return serviceImpl.getDadosGraficoDividendosPorMesEAno(ano,
                mes, pessoaService.recuperaIdPessoaByToken(idToken));
    }

    @GetMapping("/verifica-div-a-receber-manual")
    public void verificaDividendoAReceberManual() {

        serviceImpl.verificaDividendoAReceber();
    }
}

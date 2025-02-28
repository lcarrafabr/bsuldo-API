package com.carrafasoft.bsuldo.api.controller.criptomoedas;

import com.carrafasoft.bsuldo.api.mapper.CriptoTransacaoMapper;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.CriptoTransacaoInput;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.CriptoTransacaoResponse;
import com.carrafasoft.bsuldo.api.model.criptomoedas.CriptoTransacao;
import com.carrafasoft.bsuldo.api.repository.criptomoedas.CriptoTransacaoRepository;
import com.carrafasoft.bsuldo.api.service.CriptoTransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/cripto-transacao", produces = MediaType.APPLICATION_JSON_VALUE)
public class CriptoTransacaoController {

    @Autowired
    private CriptoTransacaoRepository repository;

    @Autowired
    private CriptoTransacaoMapper mapper;

    @Autowired
    private CriptoTransacaoService service;


    @GetMapping
    public ResponseEntity<List<CriptoTransacaoResponse>> listar(@RequestParam("tokenId") String tokenId) {

        return ResponseEntity.ok(mapper.toListCriptoTransacaoResponse(service.listar(tokenId)));
    }

    @GetMapping("/{codigoCriptoTransacao}")
    public ResponseEntity<CriptoTransacaoResponse> findByCodigoAndTokenId(@PathVariable String codigoCriptoTransacao,
                                                                          @RequestParam("tokenId") String tokenId) {

        return ResponseEntity.ok(mapper.toCriptoTransacaoResponse(
                service.findByCodigoAndTokenId(codigoCriptoTransacao, tokenId)
        ));
    }

    @PostMapping
    public ResponseEntity<CriptoTransacaoResponse> cadastrarCriptoTrasacao(@Valid @RequestBody CriptoTransacaoInput criptoTransacaoInput,
                                                                           @RequestParam("tokenid") String tokenId,
                                                                           HttpServletResponse response) {

        CriptoTransacao transacaoSalva = service.cadastrarCriptoTransacao(criptoTransacaoInput, tokenId, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toCriptoTransacaoResponse(transacaoSalva)
        );
    }
}

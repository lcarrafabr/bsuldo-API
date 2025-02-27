package com.carrafasoft.bsuldo.api.controller.criptomoedas;

import com.carrafasoft.bsuldo.api.mapper.CriptoTransacaoMapper;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.CriptoTransacaoResponse;
import com.carrafasoft.bsuldo.api.model.criptomoedas.CriptoTransacao;
import com.carrafasoft.bsuldo.api.repository.criptomoedas.CriptoTransacaoRepository;
import com.carrafasoft.bsuldo.api.service.CriptoTransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<CriptoTransacaoResponse>> listar() {

        return ResponseEntity.ok(mapper.toListCriptoTransacaoResponse(service.listar()));
    }
}

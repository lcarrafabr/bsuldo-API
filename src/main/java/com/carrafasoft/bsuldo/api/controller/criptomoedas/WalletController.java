package com.carrafasoft.bsuldo.api.controller.criptomoedas;

import com.carrafasoft.bsuldo.api.mapper.WalletMapper;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletResponse;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Wallets;
import com.carrafasoft.bsuldo.api.repository.criptomoedas.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/v1/wallets", produces = MediaType.APPLICATION_JSON_VALUE)
public class WalletController {

    @Autowired
    private WalletRepository repository;

    @Autowired
    private WalletMapper mapper;


    @GetMapping
    public ResponseEntity<List<WalletResponse>> listar() {

        return ResponseEntity.ok(mapper.toListWalletResponse(repository.findAll()));
    }
}

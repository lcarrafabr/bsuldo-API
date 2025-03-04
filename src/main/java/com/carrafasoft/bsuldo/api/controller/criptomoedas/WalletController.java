package com.carrafasoft.bsuldo.api.controller.criptomoedas;

import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.mapper.WalletMapper;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletInput;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletPesquisaResponse;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletResponse;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Wallets;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.WalletNaoEncontradoException;
import com.carrafasoft.bsuldo.api.repository.criptomoedas.WalletRepository;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import com.carrafasoft.bsuldo.api.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/wallets", produces = MediaType.APPLICATION_JSON_VALUE)
public class WalletController {

    @Autowired
    private WalletRepository repository;

    @Autowired
    private WalletMapper mapper;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private WalletService service;


    @GetMapping
    public ResponseEntity<List<WalletResponse>> listar(@RequestParam("tokenId") String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

//        return ResponseEntity.ok(mapper.toListWalletResponse(
//                repository.findAllByPessoaId(pessoaId))
//        );

        return ResponseEntity.ok(service.getListaWalletComSaldo(pessoaId));
    }

    @GetMapping("/{codigoWallet}")
    public ResponseEntity<WalletResponse> findById(@PathVariable String codigoWallet,
                                                   @RequestParam("tokenId") String tokenId) {

        return ResponseEntity.ok(
                mapper.toWalletResponse(service.findById(codigoWallet, tokenId))
        );
    }

    @PostMapping
    public ResponseEntity<WalletResponse> cadastrarWallet(@Valid @RequestBody WalletInput walletInput,
                                                          @RequestParam("tokenId") String tokenId, HttpServletResponse response) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toWalletResponse(service.cadastrarVallet(walletInput,tokenId,response))
        );
    }

    @PutMapping("/{codigoWallet}")
    public ResponseEntity<WalletResponse> atualizarWallet(@PathVariable String codigoWallet,
                                                          @Valid @RequestBody WalletResponse walletInput,
                                                          @RequestParam("tokenId") String tokenId) {

        return ResponseEntity.ok(
                mapper.toWalletResponse(service.atualizarWallet(codigoWallet,walletInput,tokenId))
        );
    }

    @DeleteMapping("/{codigoWallet}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerWallet(@PathVariable String codigoWallet) {

        service.removerWalet(codigoWallet);
    }

    @PutMapping("{codigoWallet}/ativo")
    public void atualizaStatusAtivo(@PathVariable String codigoWallet,@RequestBody Boolean ativo) {

        service.atualizaStatusAtivo(codigoWallet, ativo);
    }
}

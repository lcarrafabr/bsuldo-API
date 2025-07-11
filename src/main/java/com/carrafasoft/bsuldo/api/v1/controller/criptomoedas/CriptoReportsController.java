package com.carrafasoft.bsuldo.api.v1.controller.criptomoedas;

import com.carrafasoft.bsuldo.api.v1.model.reports.criptos.CryptoGradeSaldoVariacao;
import com.carrafasoft.bsuldo.api.v1.service.CriptoReportsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/v1/crypto-reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class CriptoReportsController {


    private final CriptoReportsService criptoReportsService;

    public CriptoReportsController(CriptoReportsService criptoReportsService) {
        this.criptoReportsService = criptoReportsService;
    }


    @GetMapping("/crypto-grade-saldo-variacao")
    public ResponseEntity<List<CryptoGradeSaldoVariacao>> gerarReportGradeSaldoVariacao(@RequestParam("tokenId") String tokenId) {

        return ResponseEntity.ok(criptoReportsService.gerarReportGradeSaldoVariacao(tokenId));
    }
}

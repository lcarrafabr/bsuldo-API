package com.carrafasoft.bsuldo.api.controller.criptomoedas;

import com.carrafasoft.bsuldo.api.binanceapi.v1.modelo.PrecoCriptoBinance;
import com.carrafasoft.bsuldo.api.binanceapi.v1.service.BinanceApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/api-cripto", produces = MediaType.APPLICATION_JSON_VALUE)
public class BinanceApiController {


    private final BinanceApiService binanceApiService;

    @Autowired
    public BinanceApiController(BinanceApiService binanceApiService) {
        this.binanceApiService = binanceApiService;
    }

    @GetMapping("/cotacao-cripto")
    public PrecoCriptoBinance getCriptoCotacao(@RequestParam("ticker") String ticker) {

       var retorno = binanceApiService.getPriceBiTicker(ticker);

        return retorno;
    }
}

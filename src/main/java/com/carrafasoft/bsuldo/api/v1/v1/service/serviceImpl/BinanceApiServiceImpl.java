package com.carrafasoft.bsuldo.api.v1.v1.service.serviceImpl;

import com.carrafasoft.bsuldo.api.v1.v1.modelo.PrecoCriptoBinance;
import com.carrafasoft.bsuldo.api.v1.v1.service.BinanceApiService;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class BinanceApiServiceImpl implements BinanceApiService {

    private static final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/price?symbol=";
    private static final String BINANCE_URL_COTACAO_DOLLAR = "https://api.binance.com/api/v3/ticker/price?symbol=USDTBRL";
    private static final String USDT = "USDT";

    @Override
    public PrecoCriptoBinance getPriceBiTicker(String ticker) {

        try {

            PrecoCriptoBinance cotacaoCriptoDollar = getCriptoPrice(ticker.toUpperCase());
            //log.info(cotacaoCriptoDollar.getSymbol());
            PrecoCriptoBinance cotacaoDollarBRL = consultaCotacaoDollar();

            BigDecimal valorCriptoBRL = cotacaoCriptoDollar.getPrice().multiply(cotacaoDollarBRL.getPrice());

            return  PrecoCriptoBinance.builder()
                    .symbol(cotacaoCriptoDollar.getSymbol())
                    .price(valorCriptoBRL)
                    .build();

        } catch (Exception e) {
            log.info("Ticker com erro: {}", ticker);
            throw new NegocioException(e.getMessage());
        }
    }

    @Override
    public PrecoCriptoBinance getCotacaoDollar() {

        try {
            return consultaCotacaoDollar();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PrecoCriptoBinance  getCriptoPrice(String ticker) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BINANCE_URL.concat(ticker).concat(USDT)))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        if(response.statusCode() == 400) {
            throw new NegocioException("Ticker inválido");
        }

        // Parseando JSON com Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body());

        return PrecoCriptoBinance.builder()
                .symbol(jsonNode.get("symbol").asText())
                .price(new BigDecimal(jsonNode.get("price").asText()))
                .build();
    }


    public static PrecoCriptoBinance  consultaCotacaoDollar() throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BINANCE_URL_COTACAO_DOLLAR))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parseando JSON com Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body());

        return PrecoCriptoBinance.builder()
                .symbol(jsonNode.get("symbol").asText())
                .price(new BigDecimal(jsonNode.get("price").asText()))
                .build();
    }
}


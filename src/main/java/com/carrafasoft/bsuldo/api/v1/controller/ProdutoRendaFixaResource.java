package com.carrafasoft.bsuldo.api.v1.controller;

import com.carrafasoft.bsuldo.api.v1.model.Indexes;
import com.carrafasoft.bsuldo.api.v1.model.ProdutoRendaFixa;
import com.carrafasoft.bsuldo.api.v1.repository.ProdutoRendaFixaRepository;
import com.carrafasoft.bsuldo.api.v1.service.ProdutoRendaFixaService;
import com.carrafasoft.bsuldo.braviapi.modelo.Results;
import com.carrafasoft.bsuldo.braviapi.service.ConsultarProdutoSimples;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produto-renda-fixa")
public class ProdutoRendaFixaResource {

    @Autowired
    private ProdutoRendaFixaRepository repository;

    @Autowired
    private ProdutoRendaFixaService service;

    @Value("${bsuldo.tokemApiBravi.tokemApiBravi}") // Anotação para ler o valor da propriedade do arquivo application.properties
    private String apiToken;

    @GetMapping
    public List<ProdutoRendaFixa> findAll() {

        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<ProdutoRendaFixa> cadastrarProdutoRendaFixa(@Valid @RequestBody ProdutoRendaFixa produtoRendaFixa, HttpServletResponse response) {

        return service.cadastrarProdutoRF(produtoRendaFixa, response);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ProdutoRendaFixa> buscaPorId(@PathVariable Long codigo) {

        Optional<ProdutoRendaFixa> produtoRFSalvo = repository.findById(codigo);
        return produtoRFSalvo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ProdutoRendaFixa> atualizarProdutoRendaFixa(@PathVariable Long codigo, @Valid @RequestBody ProdutoRendaFixa produtoRendaFixa) {

        ProdutoRendaFixa produtoRendaFixaSalva = service.atualizaProdutoRendaFixa(codigo, produtoRendaFixa);

        return ResponseEntity.ok(produtoRendaFixaSalva);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerProdutoRendaFixa(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }

    //**********************************************************************************************

    @GetMapping("/lista-produto-renda-fixa-ativo")
    public List<ProdutoRendaFixa> findProdutoRendaFixaAtivo() {

        return repository.findProdutorendaFixaAtivo();
    }

    @GetMapping("/teste")
    public Indexes teste() {

        StringBuilder jsonCotas = new StringBuilder();
        Indexes indexes = new Indexes();

        try {
            URL url = new URL("https://brapi.dev/api/quote/list");
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String cotas;


            while ((cotas = br.readLine()) != null) {
                jsonCotas.append(cotas);
            }

            indexes = new Gson().fromJson(jsonCotas.toString(), Indexes.class);


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return indexes;
    }


    @GetMapping("/teste2")
    public Results teste2() {

        ConsultarProdutoSimples consultarProdutoSimples = new ConsultarProdutoSimples();

        Results results = ConsultarProdutoSimples.consultarProdutoPorTicker("petr4", apiToken);

        return results;
    }

    @GetMapping("/yahoo-finance")
    public void testeYahooFinance() {
        String symbol = "PETR4.SA"; // Símbolo da Petrobras na B3

        try {
            // Obtenha a instância do Stock para o símbolo fornecido
            Stock stock = YahooFinance.get(symbol, true);

            // Verifique se a resposta não é nula e contém dados
            if (stock != null && stock.getQuote() != null) {
                // Obtenha informações sobre a ação
                System.out.println("Nome da Empresa: " + stock.getName());
                System.out.println("Preço Atual: " + stock.getQuote().getPrice());
                System.out.println("Variação: " + stock.getQuote().getChange());
                System.out.println("Variação Percentual: " + stock.getQuote().getChangeInPercent());
            } else {
                System.out.println("Resposta nula ou sem dados.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

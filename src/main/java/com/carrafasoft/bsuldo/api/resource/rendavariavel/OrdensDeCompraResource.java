package com.carrafasoft.bsuldo.api.resource.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.RelatorioBasico;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.OrdemDeCompraRepository;
import com.carrafasoft.bsuldo.api.service.rendavariavel.OrdemDeCompraRVService;
import com.carrafasoft.bsuldo.braviapi.modelo.PrecoAtualCota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ordens-de-compra")
public class OrdensDeCompraResource {

    @Autowired
    private OrdemDeCompraRepository repository;



    @Autowired
    private OrdemDeCompraRVService service;

    @Value("${bsuldo.tokemApiBravi.tokemApiBravi}") // Anotação para ler o valor da propriedade do arquivo application.properties
    private String apiToken;


    @GetMapping
    public List<OrdensDeCompra> findAll() {

        return repository.findAll();
    }

    @GetMapping("/consulta-valor-atual-cota")
    public PrecoAtualCota consultaPrecoAtualCota(@RequestParam("ticker") String ticker) {

        return service.consultaPrecoAtualCota(ticker, apiToken);
    }

    @PostMapping
    public ResponseEntity<OrdensDeCompra> cadastrarOrdemDeCompraVenda(@Valid @RequestBody OrdensDeCompra ordemCompra, HttpServletResponse response) {

        OrdensDeCompra ordemDeCompraSalva = service.cadastrarOrdemDeCompraVenda(ordemCompra, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(ordemDeCompraSalva);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<OrdensDeCompra> buscaPorId(@PathVariable Long codigo) {

        Optional<OrdensDeCompra> ordemSalva = repository.findById(codigo);

        return ordemSalva.isPresent() ? ResponseEntity.ok(ordemSalva.get()) : ResponseEntity.noContent().build();
    }


    @PutMapping("{codigo}")
    public ResponseEntity<OrdensDeCompra> editarOrdemCCompraVenda(@PathVariable Long codigo, @Valid @RequestBody OrdensDeCompra ordensDeCompra) {

        OrdensDeCompra ordemCompraEditada = service.atualizarOrdemCompraVenda(codigo, ordensDeCompra);

        return ResponseEntity.ok(ordemCompraEditada);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerOrdemCompraVenda(@PathVariable Long codigo) {
        repository.deleteById(codigo);
    }

    //************************ RELATÓRIOS *********************************************************

    @GetMapping("/relatorio-basico")
    public List<RelatorioBasico> listarRelatorioBasico() {

        //List<RelatorioBasico> relatorioBasico = service.listarRelatorioBasico(repository.findAll());
        List<RelatorioBasico> relatorioBasico = service.teste();

        return relatorioBasico;
    }

    @GetMapping("/busca-ordem-por-nome-ticker")
    public List<OrdensDeCompra> buscaPorNomeTicker(@RequestParam("ticker") String ticker) {

        return repository.buscaPorNomeProduto(ticker);
    }




}

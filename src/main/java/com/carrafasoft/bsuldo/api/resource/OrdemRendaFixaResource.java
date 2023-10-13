package com.carrafasoft.bsuldo.api.resource;

import com.carrafasoft.bsuldo.api.model.OrdemRendaFixa;
import com.carrafasoft.bsuldo.api.repository.OrdemRendaFixaRepository;
import com.carrafasoft.bsuldo.api.service.OrdemRendaFixaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ordem-renda-fixa")
public class OrdemRendaFixaResource {

    @Autowired
    private OrdemRendaFixaRepository repository;

    @Autowired
    private OrdemRendaFixaService service;

    @GetMapping
    public List<OrdemRendaFixa> findAll() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> cadastrarOrdemRendaFixa(@Valid @RequestBody OrdemRendaFixa ordemRendaFixa, HttpServletResponse response) {

        return service.cadastrarOrdemRendaFixa(ordemRendaFixa, response);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<OrdemRendaFixa> buscaPorId(@PathVariable Long codigo) {

        Optional<OrdemRendaFixa> ordemRendaFixaSalva = repository.findById(codigo);

        return ordemRendaFixaSalva.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<OrdemRendaFixa> atualizarOrdemRendaFixa(@PathVariable Long codigo, @Valid @RequestBody OrdemRendaFixa ordemRendaFixa) {

        OrdemRendaFixa ordemRendaFixaSalva = service.atualizaOrdemRendaFixa(codigo, ordemRendaFixa);
        return ResponseEntity.ok(ordemRendaFixaSalva);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerLancamento(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }

    @GetMapping("/valor-total-investido")
    public BigDecimal listarTotalInvestido() {

        return repository.listaTotalInvestido();
    }

    @GetMapping("/valor-total-resgatado")
    public BigDecimal listarTotalIResgatado() {

        return repository.listaTotalResgatado();
    }

    @GetMapping("/valor-total-disponivel")
    public BigDecimal listarTotalDisponivel() {

        return repository.listaTotalDisponivel();
    }
}

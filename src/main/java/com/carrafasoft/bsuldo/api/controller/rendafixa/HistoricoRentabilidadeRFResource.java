package com.carrafasoft.bsuldo.api.controller.rendafixa;

import com.carrafasoft.bsuldo.api.model.rendafixa.HistoricoRentabilidadeRendaFixa;
import com.carrafasoft.bsuldo.api.repository.rendafixa.HistoricoRentabilidadeRFRepository;
import com.carrafasoft.bsuldo.api.service.rendafixa.HistoricoRentabilidadeRFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/historico-rentabilidade-rf")
public class HistoricoRentabilidadeRFResource {

    @Autowired
    private HistoricoRentabilidadeRFRepository repository;

    @Autowired
    private HistoricoRentabilidadeRFService service;

    @GetMapping
    public List<HistoricoRentabilidadeRendaFixa> listarTodos() {

        return repository.findAllDesc();
    }

    @PostMapping
    public ResponseEntity<?> cadastrarRentabilidadeRendaFixa(
            @Valid @RequestBody HistoricoRentabilidadeRendaFixa historicoRF, HttpServletResponse response) {

        return service.cadastrarHistoricoRendaFixa(historicoRF, response);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<HistoricoRentabilidadeRendaFixa> buscaPorId(@PathVariable Long codigo) {

        Optional<HistoricoRentabilidadeRendaFixa> historicoSalvo = repository.findById(codigo);

        return historicoSalvo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<HistoricoRentabilidadeRendaFixa> atualizaHistoricoRF(
            @PathVariable Long codigo, @Valid @RequestBody HistoricoRentabilidadeRendaFixa historicoRF) {

        HistoricoRentabilidadeRendaFixa historicoAtualizado = service.atualizarHistoricoRF(codigo, historicoRF);

        return ResponseEntity.ok(historicoAtualizado);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerHistoricoRendimento(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }
}

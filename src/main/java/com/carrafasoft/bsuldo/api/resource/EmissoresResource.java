package com.carrafasoft.bsuldo.api.resource;

import com.carrafasoft.bsuldo.api.model.Emissores;
import com.carrafasoft.bsuldo.api.repository.EmissoresRepository;
import com.carrafasoft.bsuldo.api.service.EmissoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/emissores")
public class EmissoresResource {

    @Autowired
    private EmissoresRepository repository;

    @Autowired
    private EmissoresService service;

    @GetMapping
    public List<Emissores> findAll() {

        var retorno = repository.findAll();

        return retorno;
    }

    @PostMapping
    public ResponseEntity<Emissores> cadastrarEmissores(@Valid @RequestBody Emissores emissores, HttpServletResponse response) {

        return service.cadastrarEmissor(emissores, response);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Emissores> buscaPorId(@PathVariable  Long codigo) {

        Optional<Emissores> emissorSalvo = repository.findById(codigo);

        return emissorSalvo.isPresent() ? ResponseEntity.ok(emissorSalvo.get()) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Emissores> atualizarEmissor(@PathVariable Long codigo, @Valid @RequestBody Emissores emissores) {

        Emissores emissorSalvo = service.atualizaEmissor(codigo, emissores);

        return ResponseEntity.ok(emissorSalvo);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerEmissor(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }

    @PutMapping("/{codigo}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizaStatusAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {

        service.atualizaStatusAtivo(codigo, ativo);
    }

    @GetMapping("/busca-por-nome-emisor")
    public List<Emissores> buscaPorNomeEmissor(@RequestParam("nomeEmissor") String nomeEmissor) {

        return repository.buscaPorNomeEmissor(nomeEmissor.trim());
    }

    @GetMapping("/emissores-ativos")
    public List<Emissores> buscaEmissoresAtivos() {
        return repository.buscaEmissoresAtivos();
    }
}

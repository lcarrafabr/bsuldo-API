package com.carrafasoft.bsuldo.api.resource.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.Segmentos;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.SegmentosRepository;
import com.carrafasoft.bsuldo.api.service.rendavariavel.SegmentosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/segmentos")
public class SegmentosResource {

    @Autowired
    private SegmentosRepository repository;

    @Autowired
    private SegmentosService service;

    @GetMapping
    public List<Segmentos> findAll() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Segmentos> cadastrarSegmento(@Valid @RequestBody Segmentos segmento, HttpServletResponse response) {

        return service.cadastrarSegmento(segmento, response);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Segmentos> buscaPorId(@PathVariable Long codigo) {

        Optional<Segmentos> segmentoSalvo = repository.findById(codigo);

        return segmentoSalvo.isPresent() ? ResponseEntity.ok(segmentoSalvo.get()) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Segmentos> atualizarSegmento(@PathVariable Long codigo, @Valid @RequestBody Segmentos segmento) {

        Segmentos segmentoSalvo = service.atualizarSegmento(codigo, segmento);

        return ResponseEntity.ok(segmentoSalvo);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerSegmento(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }
}

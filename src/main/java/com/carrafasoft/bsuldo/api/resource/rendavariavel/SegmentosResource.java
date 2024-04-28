package com.carrafasoft.bsuldo.api.resource.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.Segmentos;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.SegmentosRepository;
import com.carrafasoft.bsuldo.api.service.PessoaService;
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

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public List<Segmentos> findAll(@RequestParam("tokenId") String tokenId) {

        return repository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId));
    }

    @PostMapping
    public ResponseEntity<Segmentos> cadastrarSegmento(@Valid @RequestBody Segmentos segmento,
            @RequestParam("tokenId") String tokenId,HttpServletResponse response) {

        return service.cadastrarSegmento(segmento, response, tokenId);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Segmentos> buscaPorId(@PathVariable Long codigo, @RequestParam("tokenId") String tokenId) {

        Optional<Segmentos> segmentoSalvo = repository.findByIdAndPessoaId(codigo, pessoaService.recuperaIdPessoaByToken(tokenId));

        return segmentoSalvo.isPresent() ? ResponseEntity.ok(segmentoSalvo.get()) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Segmentos> atualizarSegmento(@PathVariable Long codigo, @Valid @RequestBody Segmentos segmento,
                                                       @RequestParam("tokenId") String tokenId) {

        Segmentos segmentoSalvo = service.atualizarSegmento(codigo, segmento, tokenId);

        return ResponseEntity.ok(segmentoSalvo);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerSegmento(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }

    @PutMapping("/{codigo}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizaStatusAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {

        service.atualizaStatusAtivo(codigo, ativo);
    }

    @GetMapping("/segmentos-ativos")
    public List<Segmentos> buscaSegmentosAtivos(@RequestParam("tokenId") String tokenId) {

        return repository.buscaSegmentosAtivos(pessoaService.recuperaIdPessoaByToken(tokenId));
    }
}

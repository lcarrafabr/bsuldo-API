package com.carrafasoft.bsuldo.api.v1.controller.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.carrafasoft.bsuldo.api.v1.mapper.SegmentoMapper;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SegmentoInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SegmentoInputUpdateRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SegmentoResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Segmentos;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.SegmentosRepository;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.rendavariavel.SegmentosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/segmentos")
public class SegmentosController {

    @Autowired
    private SegmentosRepository repository;

    @Autowired
    private SegmentosService service;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private SegmentoMapper segmentoMapper;

    @GetMapping
    public List<SegmentoResponseRepresentation> findAll(@RequestParam("tokenId") String tokenId) {

        return segmentoMapper.toListSegmentoResponseRepresentationMapper(
                repository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId))
        );
    }

    @PostMapping
    public ResponseEntity<SegmentoResponseRepresentation> cadastrarSegmento(@Valid @RequestBody SegmentoInputRepresentation segmento,
                                                       @RequestParam("tokenId") String tokenId,HttpServletResponse response) {

        SegmentoResponseRepresentation segmentoResponse = service.cadastrarSegmento(segmento,response ,tokenId);
        return ResponseEntity.status(HttpStatus.CREATED).body(segmentoResponse);
    }


    @GetMapping("/{codigo}")
    public SegmentoResponseRepresentation buscaPorId(@PathVariable String codigo, @RequestParam("tokenId") String tokenId) {

        Segmentos segmentoSalvo = service.findByCodigoSegmentoAndTokenId(codigo, tokenId);

        return segmentoMapper.toSegmentoResponseRepresentationMapper(segmentoSalvo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<SegmentoResponseRepresentation> atualizarSegmento(@PathVariable String codigo,
                                                                            @Valid @RequestBody SegmentoInputUpdateRepresentation segmento,
                                                       @RequestParam("tokenId") String tokenId) {

        SegmentoResponseRepresentation segmentoSalvo = service.atualizarSegmento(codigo, segmento, tokenId);

        return ResponseEntity.ok(segmentoSalvo);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerSegmento(@PathVariable String codigo) {

        service.removerSegmento(codigo);
    }

    @PutMapping("/{codigo}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizaStatusAtivo(@PathVariable String codigo, @RequestBody Boolean ativo) {

        service.atualizaStatusAtivo(codigo, ativo);
    }

    @GetMapping("/segmentos-ativos")
    public List<Segmentos> buscaSegmentosAtivos(@RequestParam("tokenId") String tokenId) {

        try {
            return repository.buscaSegmentosAtivos(pessoaService.recuperaIdPessoaByToken(tokenId));
        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }
}

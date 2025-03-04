package com.carrafasoft.bsuldo.api.controller.criptomoedas;

import com.carrafasoft.bsuldo.api.mapper.OrigenMapper;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.OrigemInput;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.OrigemResponse;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.OrigemUpdateInput;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Origens;
import com.carrafasoft.bsuldo.api.repository.criptomoedas.OrigemRepository;
import com.carrafasoft.bsuldo.api.service.OrigemService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/origens", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrigensController {

    @Autowired
    private OrigemRepository repository;

    @Autowired
    private OrigenMapper mapper;

    @Autowired
    private OrigemService service;

    @Autowired
    private PessoaService pessoaService;


    @GetMapping
    public ResponseEntity<List<OrigemResponse>> findAll(@RequestParam("tokenId") String tokenId) {

        return ResponseEntity.ok(
                mapper.toListOrigemResponse(repository.findAllByTokenId(
                        pessoaService.recuperaIdPessoaByToken(tokenId))));
    }

    @GetMapping("/{codigoOrigem}")
    public ResponseEntity<OrigemResponse> findById(@PathVariable String codigoOrigem,
                                                   @RequestParam("tokenId") String tokenId) {

        OrigemResponse origemResponse = mapper.toOriremResponse(
                service.buscaOrigemPorIdAndToken(codigoOrigem, tokenId));

        return ResponseEntity.ok(origemResponse);
    }

    @PostMapping
    public ResponseEntity<OrigemResponse> cadastrarOrigem(@Valid @RequestBody OrigemInput origemInput,
                                                          @RequestParam("tokenId") String tokenId, HttpServletResponse response) {

        Origens origenSalvo = service.cadastrarOrigem(origemInput,tokenId,response);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toOriremResponse(origenSalvo));
    }

    @PutMapping("/{codigoOrigem}")
    public ResponseEntity<OrigemResponse> atualizarOrigem(@PathVariable String codigoOrigem,
                                                          @Valid @RequestBody OrigemUpdateInput origemInput,
                                                          @RequestParam("tokenId") String tokenId) {

        Origens origemAtualizado = service.atualizarOrigem(codigoOrigem, origemInput, tokenId);

        return ResponseEntity.ok(mapper.toOriremResponse(origemAtualizado));
    }

    @DeleteMapping("/{codigoOrigem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerOrigem(@PathVariable String codigoOrigem) {

        service.removerOrigem(codigoOrigem);
    }

    @PutMapping("/{codigoOrigem}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizaStatusAtivo(@PathVariable String codigoOrigem, @RequestBody Boolean ativo) {

        service.atualizaStatusAtivo(codigoOrigem, ativo);
    }

    //***************************************************************************************************************************************//

    @GetMapping("/busca-origem-por-nome")
    public ResponseEntity<List<OrigemResponse>> buscaOrigemPorNome(@RequestParam("nomeOrigem") String nomeOrigem,
                                                                   @RequestParam("tokenId") String tokenId) {

        return ResponseEntity.ok(mapper.toListOrigemResponse(
                service.buscaOrigemPorNome(nomeOrigem,tokenId)
        ));
    }

    @GetMapping("/busca-origem-ativo")
    public ResponseEntity<List<OrigemResponse>> findByOPrigemAtivo(@RequestParam("tokenId") String tokenId) {

        return ResponseEntity.ok(
                mapper.toListOrigemResponse(service.findByOrigemAtivo(tokenId))
        );
    }
}

package com.carrafasoft.bsuldo.api.resource;

import com.carrafasoft.bsuldo.api.model.Emissores;
import com.carrafasoft.bsuldo.api.repository.EmissoresRepository;
import com.carrafasoft.bsuldo.api.service.EmissoresService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
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

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public List<Emissores> findAll(@RequestParam("tokenId") String tokenid) {

        var retorno = repository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenid));

        return retorno;
    }

    @PostMapping
    public ResponseEntity<Emissores> cadastrarEmissores(@Valid @RequestBody Emissores emissores, HttpServletResponse response,
                                                        @RequestParam("tokenId") String tokenId) {

        return service.cadastrarEmissor(emissores, response, tokenId);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Emissores> buscaPorId(@PathVariable Long codigo, @RequestParam("tokenId") String tokenId) {

        Optional<Emissores> emissorSalvo = repository.findByIdAndPessoaId(codigo, pessoaService.recuperaIdPessoaByToken(tokenId));

        return emissorSalvo.isPresent() ? ResponseEntity.ok(emissorSalvo.get()) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Emissores> atualizarEmissor(@PathVariable Long codigo, @Valid @RequestBody Emissores emissores,
                                                      @RequestParam("tokenId") String tokenId) {

        Emissores emissorSalvo = service.atualizaEmissor(codigo, emissores, tokenId);

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
    public List<Emissores> buscaPorNomeEmissor(@RequestParam("nomeEmissor") String nomeEmissor, @RequestParam("tokenId") String tokenId) {

        return repository.buscaPorNomeEmissor(nomeEmissor.trim(), pessoaService.recuperaIdPessoaByToken(tokenId));
    }

    @GetMapping("/emissores-ativos")
    public List<Emissores> buscaEmissoresAtivos(@RequestParam("tokenId") String tokenId) {

        return repository.buscaEmissoresAtivos(pessoaService.recuperaIdPessoaByToken(tokenId));
    }
}

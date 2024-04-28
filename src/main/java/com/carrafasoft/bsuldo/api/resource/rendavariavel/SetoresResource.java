package com.carrafasoft.bsuldo.api.resource.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.Setores;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.SetoresRepository;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import com.carrafasoft.bsuldo.api.service.rendavariavel.SetoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/setores")
public class SetoresResource {

    @Autowired
    private SetoresRepository repository;

    @Autowired
    private SetoresService service;

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public List<Setores> findAll(@RequestParam("tokenId") String tokenId) {

        return repository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId));
    }

    @PostMapping
    public ResponseEntity<Setores> cadastrarSetor(@Valid @RequestBody Setores setor,
                                                  @RequestParam("tokenId") String tokenId, HttpServletResponse response) {

        return service.cadastrarSetor(setor, response, tokenId);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Setores> buscaPorId(@PathVariable Long codigo, @RequestParam("tokenId") String tokenId) {

        Optional<Setores> setorSalvo = repository.findByIdAndPessoaId(codigo, pessoaService.recuperaIdPessoaByToken(tokenId));

        return setorSalvo.isPresent() ? ResponseEntity.ok(setorSalvo.get()) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Setores> atualizarSetor(@PathVariable Long codigo, @Valid @RequestBody Setores setor,
                                                  @RequestParam("tokenId") String tokenId) {

        Setores setorSalvo = service.atualizarSetor(codigo, setor, tokenId);

        return ResponseEntity.ok(setorSalvo);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerSetor(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }

    @PutMapping("/{codigo}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizaStatusAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {

        service.atualizaStatusAtivo(codigo, ativo);
    }

    @GetMapping("/busca-por-nome-setor")
    public List<Setores> buscaPorNomeSetor(@RequestParam("nomeSetor") String nomeSetor) {

        return repository.buscaPorNomeSetor(nomeSetor);
    }

    @GetMapping("/setores-ativos")
    public List<Setores> buscaSetorAtivo(@RequestParam("tokenId") String tokenId) {

        return repository.buscaSetorAtivo(pessoaService.recuperaIdPessoaByToken(tokenId));
    }
}

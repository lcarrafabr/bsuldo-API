package com.carrafasoft.bsuldo.api.v1.controller.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorInputUpdateRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel.SetoresMapper;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Setores;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.SetoresRepository;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.SetoresService;
import com.carrafasoft.bsuldo.api.v1.service.rendavariavel.SetoresServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/setores")
public class SetoresController {

    private SetoresRepository repository;
    private SetoresService service;
    private PessoaService pessoaService;
    private SetoresMapper setoresMapper;

    public SetoresController(SetoresRepository repository, SetoresService service, PessoaService pessoaService, SetoresMapper setoresMapper) {
        this.repository = repository;
        this.service = service;
        this.pessoaService = pessoaService;
        this.setoresMapper = setoresMapper;
    }

    @GetMapping
    public List<SetorResponseRepresentation> findAll(@RequestParam("tokenId") String tokenId,
                                                     @RequestParam(value = "nomeSetor", required = false) String nomeSetor) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

        List<Setores> setores = repository.findAllByPessoaId(pessoaId,
                (nomeSetor == null || nomeSetor.trim().isEmpty()) ? null : nomeSetor.trim());

        return setoresMapper.toListSetorRepresentationMapper(setores);

//        if(nomeSetor != null || !nomeSetor.trim().isEmpty()) {
//
//            return setoresMapper.toListSetorRepresentationMapper(
//                    repository.findByPessoaIdAndNomeContainingIgnoreCase(pessoaService.recuperaIdPessoaByToken(tokenId), nomeSetor.trim())
//            );
//        }
//
//        return setoresMapper.toListSetorRepresentationMapper(
//                repository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId))
//        );
    }

    @PostMapping
    public ResponseEntity<SetorResponseRepresentation> cadastrarSetor(@Valid @RequestBody SetorInputRepresentation setorInput,
                                                  @RequestParam("tokenId") String tokenId, HttpServletResponse response) {

        SetorResponseRepresentation setorResponse = service.cadastrarSetor(setorInput,response, tokenId);

        return ResponseEntity.status(HttpStatus.CREATED).body(setorResponse);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<SetorResponseRepresentation> buscaPorId(@PathVariable String codigo, @RequestParam("tokenId") String tokenId) {

        Setores setorSalvo = service.findByCodigoSetorAndTokenId(codigo, tokenId);

        return ResponseEntity.ok(setoresMapper.toSetorResponseRepresentationMapper(setorSalvo));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<SetorResponseRepresentation> atualizarSetor(@PathVariable String codigo, @Valid @RequestBody SetorInputUpdateRepresentation setor,
                                                  @RequestParam("tokenId") String tokenId) {

        SetorResponseRepresentation setoratualizado = service.atualizarSetor(codigo, setor, tokenId);

        return ResponseEntity.ok(setoratualizado);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerSetor(@PathVariable String codigo) {

        service.removerSetor(codigo);
    }

    @PutMapping("/{codigo}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizaStatusAtivo(@PathVariable String codigo, @RequestBody Boolean ativo) {

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

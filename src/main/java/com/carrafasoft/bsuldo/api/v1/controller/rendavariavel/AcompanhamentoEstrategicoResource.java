package com.carrafasoft.bsuldo.api.v1.controller.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.AcompanhamentoEstrategico;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.AcompanhamentoEstrategicoRepository;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.rendavariavel.AcompanhamentoEstrategicoService;
import com.carrafasoft.bsuldo.api.v1.utils.FuncoesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/acompanhamento-estrategico")
public class AcompanhamentoEstrategicoResource {

    @Autowired
    private AcompanhamentoEstrategicoRepository repository;

    @Autowired
    private AcompanhamentoEstrategicoService service;

    @Autowired
    PessoaService pessoaService;

    @GetMapping
    public List<AcompanhamentoEstrategico> findAll(@RequestParam("tokenId") String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

        return  repository.findAllByPessoaId(pessoaId);
    }

    @GetMapping("/find-by-filtros")
    public List<AcompanhamentoEstrategico> findByFiltros(@RequestParam("tokenId") String tokenId, @RequestParam("ticker") String ticker,
                                                         @RequestParam("setorId") String setorId, @RequestParam("segmentoId") String segmentoId,
                                                         @RequestParam("statusAcompanhamento") String statusAcompanhamento) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

        return repository.findByFiltros(FuncoesUtils.converteVazioParaNulo(ticker),
                FuncoesUtils.converteVazioParaNulo(setorId),
                FuncoesUtils.converteVazioParaNulo(segmentoId),
                FuncoesUtils.converteVazioParaNulo(statusAcompanhamento),
                pessoaId
        );
    }

    @PostMapping
    public ResponseEntity<?> cadastrarAcompanhamentoEstrategico(@Valid @RequestBody AcompanhamentoEstrategico acompEstr,
                                                                                        HttpServletResponse response,
                                                                @RequestParam("tokenId") String tokenId) {
        try {
            AcompanhamentoEstrategico acompSalvo = service.cadastrarAcompanhamentoEstrategico(acompEstr, response, tokenId);
            return ResponseEntity.status(HttpStatus.CREATED).body(acompSalvo);

        } catch (EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{codigo}")
    public  ResponseEntity<AcompanhamentoEstrategico> buscaPorId(@PathVariable Long codigo, @RequestParam("tokenId") String tokenId) {

        Optional<AcompanhamentoEstrategico> acompEstrSalvo = repository.findByCodigoAndPessoaId(codigo, pessoaService.recuperaIdPessoaByToken(tokenId));

        return acompEstrSalvo.isPresent() ? ResponseEntity.ok(acompEstrSalvo.get()) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<AcompanhamentoEstrategico> atualziarAcompanhamentoEstrategico(@PathVariable Long codigo,
                                                                                        @Valid @RequestBody AcompanhamentoEstrategico acompEstraRequest,
                                                                                        @RequestParam("tokenId") String tokenId) {

        AcompanhamentoEstrategico acompEstrAtualizado = service.atualizaAcompanhamentoEstrategico(codigo, acompEstraRequest, tokenId);

        return ResponseEntity.ok(acompEstrAtualizado);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerAcompanhamentoEstrategico(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }
}

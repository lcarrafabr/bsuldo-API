package com.carrafasoft.bsuldo.api.resource.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.AcompanhamentoEstrategico;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.AcompanhamentoEstrategicoRepository;
import com.carrafasoft.bsuldo.api.service.rendavariavel.AcompanhamentoEstrategicoService;
import com.carrafasoft.bsuldo.api.utils.FuncoesUtils;
import com.carrafasoft.bsuldo.exceptions.AcompanhamentoEstrategicoExistenteException;
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

    @GetMapping
    public List<AcompanhamentoEstrategico> findAll(@RequestParam("pessoaId") Long pessoaId) {

        return  repository.findAllByPessoaId(pessoaId);
    }

    @GetMapping("/find-by-filtros")
    public List<AcompanhamentoEstrategico> findByFiltros(@RequestParam("pessoaId") String pessoaId, @RequestParam("ticker") String ticker,
                                                         @RequestParam("setorId") String setorId, @RequestParam("segmentoId") String segmentoId,
                                                         @RequestParam("statusAcompanhamento") String statusAcompanhamento) {


        return repository.findByFiltros(FuncoesUtils.converteVazioParaNulo(ticker),
                FuncoesUtils.converteVazioParaNulo(setorId),
                FuncoesUtils.converteVazioParaNulo(segmentoId),
                FuncoesUtils.converteVazioParaNulo(statusAcompanhamento),
                FuncoesUtils.converteVazioParaNulo(pessoaId)
        );
    }

    @PostMapping
    public ResponseEntity<?> cadastrarAcompanhamentoEstrategico(@Valid @RequestBody AcompanhamentoEstrategico acompEstr,
                                                                                        HttpServletResponse response) {
        try {
            AcompanhamentoEstrategico acompSalvo = service.cadastrarAcompanhamentoEstrategico(acompEstr, response);
            return ResponseEntity.status(HttpStatus.CREATED).body(acompSalvo);

        } catch (EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{codigo}")
    public  ResponseEntity<AcompanhamentoEstrategico> buscaPorId(@PathVariable Long codigo) {

        Optional<AcompanhamentoEstrategico> acompEstrSalvo = repository.findById(codigo);

        return acompEstrSalvo.isPresent() ? ResponseEntity.ok(acompEstrSalvo.get()) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<AcompanhamentoEstrategico> atualziarAcompanhamentoEstrategico(@PathVariable Long codigo,
                                                                                        @Valid @RequestBody AcompanhamentoEstrategico acompEstraRequest) {

        AcompanhamentoEstrategico acompEstrAtualizado = service.atualizaAcompanhamentoEstrategico(codigo, acompEstraRequest);

        return ResponseEntity.ok(acompEstrAtualizado);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerAcompanhamentoEstrategico(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }
}

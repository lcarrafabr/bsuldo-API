package com.carrafasoft.bsuldo.api.resource;

import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.model.Bancos;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.BancoNaoEncontradoException;
import com.carrafasoft.bsuldo.api.repository.BancoRepository;
import com.carrafasoft.bsuldo.api.service.BancoService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bancos")
@Slf4j
public class BancosResource {

    @Autowired
    private BancoRepository repository;

    @Autowired
    private BancoService service;

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public List<Bancos> listarTodos(@RequestParam("tokenId")String tokenId) {

        return repository.findAllById(pessoaService.recuperaIdPessoaByToken(tokenId));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<Bancos> cadastrarBanco(@RequestParam("tokenId") String tokenId, @Valid @RequestBody Bancos banco, HttpServletResponse response) {

        try {
            Bancos bancoSalvo = service.cadastrarBanco(banco, response, tokenId);
            return ResponseEntity.status(HttpStatus.CREATED).body(bancoSalvo);

        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }

    }

    @GetMapping("/{codigo}")
    public Bancos buscaPorId(@PathVariable Long codigo, @RequestParam("tokenId") String tokenId) {

        return service.buscaBancoPorId(codigo, tokenId);
    }

    @Transactional
    @PutMapping("/{codigo}")
    public ResponseEntity<Bancos> atualizarBanco(@RequestParam("pessoaId") String pessoaId, @PathVariable Long codigo, @Valid @RequestBody Bancos banco) {

        try {
            Bancos bancoSalvo = service.atualizaBancoSalvo(codigo, banco, pessoaId);

            return ResponseEntity.ok(bancoSalvo);
        } catch (BancoNaoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerbanco(@PathVariable Long codigo) {

        log.info("...: Removendo bancoId: " + codigo + " :...");
        repository.deleteById(codigo);
    }

    @Transactional
    @PutMapping("/{codigo}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizaStatusAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {

        service.atualizaStatusAtivo(codigo, ativo);
    }

    @GetMapping("/busca-banco-por-nome")
    public List<Bancos> buscaBancoPorNome(@RequestParam("nomeBanco") String nomeBanco, @RequestParam("pessoaId") String pessoaId) {

        return repository.findAllBynomeBanco(nomeBanco, pessoaService.recuperaIdPessoaByToken(pessoaId));
    }

    @GetMapping("/busca-bancos-ativos")
    public List<Bancos> buscaBancosAtivos(@RequestParam("tokenId") String tokenId) {

        return repository.findByPessoaIDAndAtivos(pessoaService.recuperaIdPessoaByToken(tokenId));
    }
}

package com.carrafasoft.bsuldo.api.resource;

import com.carrafasoft.bsuldo.api.model.Bancos;
import com.carrafasoft.bsuldo.api.repository.BancoRepository;
import com.carrafasoft.bsuldo.api.service.BancoService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public List<Bancos> listarTodos(@RequestParam("pessoaId")String pessoaId) {

        return repository.findAllById(pessoaService.recuperaIdPessoaByToken(pessoaId));
    }

    @PostMapping
    public ResponseEntity<Bancos> cadastrarBanco(@RequestParam("pessoaId") String pessoaId, @Valid @RequestBody Bancos banco, HttpServletResponse response) {

        //ResponseEntity<?> bancoSalvo = service.cadastrarBanco(banco, response);
        Bancos bancoSalvo = service.cadastrarBanco(banco, response, pessoaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bancoSalvo);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> buscaPorId(@PathVariable Long codigo, @RequestParam("tokenId") String tokenId) {

        ResponseEntity<?> bancoSalvo = service.buscaBancoPorId(codigo, tokenId);

        return bancoSalvo;
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Bancos> atualizarBanco(@RequestParam("pessoaId") String pessoaId, @PathVariable Long codigo, @Valid @RequestBody Bancos banco) {

        Bancos bancoSalvo = service.atualizaBancoSalvo(codigo, banco, pessoaId);

        return ResponseEntity.ok(bancoSalvo);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerbanco(@PathVariable Long codigo) {

        log.info("...: Removendo bancoId: " + codigo + " :...");
        repository.deleteById(codigo);
    }

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

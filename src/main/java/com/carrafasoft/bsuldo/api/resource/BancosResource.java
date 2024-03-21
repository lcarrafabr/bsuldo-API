package com.carrafasoft.bsuldo.api.resource;

import com.carrafasoft.bsuldo.api.model.Bancos;
import com.carrafasoft.bsuldo.api.repository.BancoRepository;
import com.carrafasoft.bsuldo.api.service.BancoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public List<Bancos> listarTodos(@RequestParam("pessoaId")Long pessoaId) {

        return repository.findAllById(pessoaId);
    }

    @PostMapping
    public ResponseEntity<?> cadastrarBanco(@Valid @RequestBody Bancos banco, HttpServletResponse response,
                                            @RequestParam("pessoaId")Long pessoaId) {

        ResponseEntity<?> bancoSalvo = service.cadastrarBanco(banco, response, pessoaId);
        return bancoSalvo;
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> buscaPorId(@PathVariable Long codigo) {

        ResponseEntity<?> bancoSalvo = service.buscaBancoPorId(codigo);

        return bancoSalvo;
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Bancos> atualizarBanco(@PathVariable Long codigo, @Valid @RequestBody Bancos banco) {

        Bancos bancoSalvo = service.atualizaBancoSalvo(codigo, banco);

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

        log.info("...: atualizando status banco para: " + ativo + " :...");
        service.atualizaStatusAtivo(codigo, ativo);
    }
}

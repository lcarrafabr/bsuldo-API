package com.carrafasoft.bsuldo.api.resource;

import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.mapper.BancoMapper;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.BancoRequestInputRepresentation;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.BancoResponseRepresentation;
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

    @Autowired
    private BancoMapper bancoMapper;

    @GetMapping
    public List<BancoResponseRepresentation> listarTodos(@RequestParam("tokenId")String tokenId) {

        return bancoMapper.toListBancoResponseRepresentationMapper(
                repository.findAllById(pessoaService.recuperaIdPessoaByToken(tokenId))
        );
    }

    @PostMapping
    public ResponseEntity<BancoResponseRepresentation> cadastrarBanco(@RequestParam("tokenId") String tokenId,
                                                 @Valid @RequestBody BancoRequestInputRepresentation bancoInput, HttpServletResponse response) {


            Bancos bancoSalvo = service.cadastrarBanco(bancoInput, response, tokenId);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    bancoMapper.toBancoResponseRepresentationMapper(bancoSalvo));
    }

    @GetMapping("/{codigo}")
    public BancoResponseRepresentation buscaPorId(@PathVariable String codigo, @RequestParam("tokenId") String tokenId) {

        return bancoMapper.toBancoResponseRepresentationMapper(service.buscaBancoPorId(codigo, tokenId));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<BancoResponseRepresentation> atualizarBanco(@RequestParam("pessoaId") String pessoaId,
                                                 @PathVariable String codigo, @Valid @RequestBody BancoResponseRepresentation banco) {

            BancoResponseRepresentation bancoSalvo = bancoMapper.toBancoResponseRepresentationMapper(
                    service.atualizaBancoSalvo(codigo, banco, pessoaId));

            return ResponseEntity.ok(bancoSalvo);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerbanco(@PathVariable String codigo) {

        log.info("...: Removendo bancoId: " + codigo + " :...");
        service.remover(codigo);
    }

    @PutMapping("/{codigo}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizaStatusAtivo(@PathVariable String codigo, @RequestBody Boolean ativo) {

        service.atualizaStatusAtivo(codigo, ativo);
    }
    /********************************************************************************************************************/

    @GetMapping("/busca-banco-por-nome")
    public List<BancoResponseRepresentation> buscaBancoPorNome(@RequestParam("nomeBanco") String nomeBanco, @RequestParam("tokenId") String tokenId) {

        return bancoMapper.toListBancoResponseRepresentationMapper(
                repository.findAllBynomeBanco(nomeBanco, pessoaService.recuperaIdPessoaByToken(tokenId))
        );
    }

    @GetMapping("/busca-bancos-ativos")
    public List<BancoResponseRepresentation> buscaBancosAtivos(@RequestParam("tokenId") String tokenId) {

        return bancoMapper.toListBancoResponseRepresentationMapper(
                repository.findByPessoaIDAndAtivos(pessoaService.recuperaIdPessoaByToken(tokenId))
        );
    }
}

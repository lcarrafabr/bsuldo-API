package com.carrafasoft.bsuldo.api.v1.controller;

import com.carrafasoft.bsuldo.api.v1.model.AvisosAutomaticos;
import com.carrafasoft.bsuldo.api.v1.repository.AvisosAutomaticosRepository;
import com.carrafasoft.bsuldo.api.v1.service.AvisosAutomaticosService;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("avisos-automaticos")
public class AvisosAutomaticosController {

    @Autowired
    private AvisosAutomaticosRepository repository;

    @Autowired
    private AvisosAutomaticosService service;

    @Autowired
    PessoaService pessoaService;

    @GetMapping
    public List<AvisosAutomaticos> findAll() {

        return repository.findAll();
    }

    @GetMapping("/find-by-pessoaid")
    public List<AvisosAutomaticos> finsAllByPessoaId(@RequestParam("pessoaId")String pessoaId) {

        return repository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(pessoaId));
    }

    @GetMapping("/alertas-nao-visualizados")
    public List<AvisosAutomaticos> findAllByPessoaIdNaoVisualizado(@RequestParam("pessoaId")String tokenId) {

        return repository.findAlertasNãoVisualizadosByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId));
    }

    @GetMapping("/quantidade-alertas")
    public Long quantidadeAlertasByPessoaId(@RequestParam("pessoaId")String pessoaId) {

        return repository.quantidadeAlertasByPessoaId(pessoaService.recuperaIdPessoaByToken(pessoaId));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<AvisosAutomaticos> buscaAvisoPorId(@PathVariable Long codigo) {

        Optional<AvisosAutomaticos> avisoAutoSalvo = repository.findById(codigo);

        return avisoAutoSalvo.isPresent() ? ResponseEntity.ok(avisoAutoSalvo.get()) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{codigo}/visualizado")
    public void atualizaStatusVisualizado(@PathVariable Long codigo, @RequestBody Boolean visualizado) {

        service.atualizaStatusVisualizado(codigo, visualizado);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerAlerta(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }
}

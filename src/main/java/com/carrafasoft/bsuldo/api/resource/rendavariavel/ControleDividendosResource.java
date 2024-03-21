package com.carrafasoft.bsuldo.api.resource.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.ControleDividendos;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.ControleDividendosCadastroCombobox;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.TotalDivDisponivelDTO;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.TotalDivRecebidoDTO;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.ControleDividendosRepository;
import com.carrafasoft.bsuldo.api.service.rendavariavel.ControleDividendosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/controle-dividendos")
public class ControleDividendosResource {

    @Autowired
    private ControleDividendosRepository repository;

    @Autowired
    private ControleDividendosService service;


    @GetMapping
    public List<ControleDividendos> findAll() {

        return repository.findAll(Sort.by(Sort.Direction.DESC, "controleDividendoId"));
    }

    @PostMapping
    public ResponseEntity<ControleDividendos> cadastrarControlDiv(@Valid @RequestBody ControleDividendos controleDividendo, HttpServletResponse response) {

        ControleDividendos controleDividendoSolvo = service.cadastrarControleDividendo(controleDividendo,response);

        return ResponseEntity.status(HttpStatus.CREATED).body(controleDividendoSolvo);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ControleDividendos> buscaPorId(@PathVariable Long codigo) {

        Optional<ControleDividendos> controleDivSalvo = repository.findById(codigo);

        return controleDivSalvo.isPresent() ? ResponseEntity.ok(controleDivSalvo.get()) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ControleDividendos> atualizarControlDiv(@PathVariable Long codigo,
                                                                  @Valid @RequestBody ControleDividendos controleDividendos) {

        ControleDividendos controlDivAtualizado = service.atualizarControleDividendos(codigo, controleDividendos);

        return ResponseEntity.ok(controlDivAtualizado);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerControleDividendo(@PathVariable Long codigo) {

        repository.deleteById(codigo);
    }

    //******************************************************************************************************************

    @PutMapping("/{codigo}/divUsado")
    @ResponseStatus(HttpStatus.OK)
    public void atualizaAtatusDivAtivo(@PathVariable Long codigo, @RequestBody Boolean divUsado) {
        service.atualizaAtatusDividendoAtivo(codigo, divUsado);
    }

    @GetMapping("/busca-ticker-combobox")
    public List<ControleDividendosCadastroCombobox> buscaControleDividendosCombobox() {

        List<ControleDividendosCadastroCombobox> list = service.buscaControleDividendosCombobox();

        return list;
    }

    //******************************************************************************************************************

    @GetMapping("/valor-total-div-recebido")
    public TotalDivRecebidoDTO valorTotalDivsRecebidos() {

        TotalDivRecebidoDTO totalDiv = new TotalDivRecebidoDTO();

        BigDecimal valor = repository.valorTotalDivRecebido();
        totalDiv.setTotalDivRecebido(valor);

        return totalDiv;
    }

    @GetMapping("/valor-total-div-disponivel")
    public TotalDivDisponivelDTO valorTotalDivDisponivel() {

        TotalDivDisponivelDTO totalDivDisp = new TotalDivDisponivelDTO();

        BigDecimal valorDisp = repository.valorTotalDivDisponivel();
        totalDivDisp.setTotalDivDisponivel(valorDisp);

        return totalDivDisp;
    }
}

package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.model.OrdemRendaFixa;
import com.carrafasoft.bsuldo.api.v1.repository.OrdemRendaFixaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Service
public class OrdemRendaFixaService {

    @Autowired
    private OrdemRendaFixaRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;


    public ResponseEntity<?> cadastrarOrdemRendaFixa(OrdemRendaFixa ordemRendaFixa, HttpServletResponse response) {

        BigDecimal valorRetorno = repository.verificaResgate(
                ordemRendaFixa.getValorTransacao(), ordemRendaFixa.getProdutoRendaFixa().getProdutoRendaFixaId());

        //if(!valorRetorno.equals(null) ){// || valorRetorno.compareTo(BigDecimal.ZERO) >= 0) {

            OrdemRendaFixa ordemRFSalvo = repository.save(ordemRendaFixa);
            publisher.publishEvent(new RecursoCriadoEvent(this, response, ordemRFSalvo.getOrdemRendaFixaId()));

            return ResponseEntity.status(HttpStatus.CREATED).body(ordemRFSalvo);

        //} else {

            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar. O velor de resgate não pode ser maior que o investido");
       // }


    }

    public OrdemRendaFixa atualizaOrdemRendaFixa(Long codigo, OrdemRendaFixa ordemRendaFixa) {

        OrdemRendaFixa ordemRFSalva = buscaPorId(codigo);
        BeanUtils.copyProperties(ordemRendaFixa, ordemRFSalva, "ordemRendaFixaId");

        return repository.save(ordemRFSalva);
    }

    private OrdemRendaFixa buscaPorId(Long codigo) {

        OrdemRendaFixa ordemRFSalva = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return ordemRFSalva;
    }
}

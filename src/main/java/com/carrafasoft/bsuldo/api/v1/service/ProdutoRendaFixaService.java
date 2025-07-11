package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.model.ProdutoRendaFixa;
import com.carrafasoft.bsuldo.api.v1.repository.ProdutoRendaFixaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class ProdutoRendaFixaService {

    @Autowired
    private ProdutoRendaFixaRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    public ResponseEntity<ProdutoRendaFixa> cadastrarProdutoRF(ProdutoRendaFixa produtoRendaFixa, HttpServletResponse response) {

        ProdutoRendaFixa produtoRFSalvo = repository.save(produtoRendaFixa);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, produtoRFSalvo.getProdutoRendaFixaId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(produtoRFSalvo);
    }

    public ProdutoRendaFixa atualizaProdutoRendaFixa(Long codigo, ProdutoRendaFixa produtoRendaFixa) {

        ProdutoRendaFixa produtoRFSalvo = buscaPorId(codigo);
        BeanUtils.copyProperties(produtoRendaFixa, produtoRFSalvo, "produtoRendaFixaId");

        return repository.save(produtoRFSalvo);
    }

    private ProdutoRendaFixa buscaPorId(Long codigo) {

        ProdutoRendaFixa produtoRFSalvo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return  produtoRFSalvo;
    }
}

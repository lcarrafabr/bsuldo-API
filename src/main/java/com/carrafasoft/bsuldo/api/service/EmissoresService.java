package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Emissores;
import com.carrafasoft.bsuldo.api.repository.EmissoresRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class EmissoresService {

    @Autowired
    private EmissoresRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    public ResponseEntity<Emissores> cadastrarEmissor(Emissores emissores, HttpServletResponse response) {

        Emissores emissorSalvo = repository.save(emissores);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, emissorSalvo.getEmissorId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(emissorSalvo);
    }

    public Emissores atualizaEmissor(Long codigo, Emissores emissores) {

        Emissores emissorSalvo = buscaPorId(codigo);
        BeanUtils.copyProperties(emissores, emissorSalvo, "emissorId");
        return repository.save(emissorSalvo);
    }

    public void atualizaStatusAtivo(Long codigo, Boolean ativo) {

        Emissores emissorSalvo = buscaPorId(codigo);
        emissorSalvo.setStatus(ativo);
        repository.save(emissorSalvo);
    }

    private Emissores buscaPorId(Long codigo) {

        Emissores emissorSalvo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return emissorSalvo;
    }


}

package com.carrafasoft.bsuldo.api.service.rendavariavel;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.rendavariavel.Segmentos;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.SegmentosRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class SegmentosService {

    @Autowired
    private SegmentosRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    public ResponseEntity<Segmentos> cadastrarSegmento(Segmentos segmento, HttpServletResponse response) {

        segmento.setStatus(true);
        Segmentos segmentoSalvo = repository.save(segmento);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, segmentoSalvo.getSegmentoId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(segmentoSalvo);
    }

    public Segmentos atualizarSegmento(Long codigo, Segmentos segmento) {

        Segmentos segmentoSalvo = buscaPorID(codigo);
        BeanUtils.copyProperties(segmento, segmentoSalvo, "segmentoId");

        return repository.save(segmentoSalvo);
    }

    public Segmentos pesquisaPorNomeSegmento(String nomeSegmento) {

        return repository.buscaPorNomeSegmento(nomeSegmento);
    }

    public Segmentos cadastrarSegmentoAutomatico(Segmentos segmento, HttpServletResponse response) {

        segmento.setStatus(true);
        Segmentos segmentoSalvo = repository.save(segmento);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, segmentoSalvo.getSegmentoId()));

        return segmentoSalvo;
    }

    public void atualizaStatusAtivo(Long codigo, Boolean ativo) {

        Segmentos segmentoSalvo = buscaPorID(codigo);
        segmentoSalvo.setStatus(ativo);
        repository.save(segmentoSalvo);
    }

    private Segmentos buscaPorID(Long codigo) {

        Segmentos segmentoSalvo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return  segmentoSalvo;
    }


}

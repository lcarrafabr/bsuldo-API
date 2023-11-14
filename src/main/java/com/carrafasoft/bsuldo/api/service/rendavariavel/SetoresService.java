package com.carrafasoft.bsuldo.api.service.rendavariavel;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.rendavariavel.Setores;
import com.carrafasoft.bsuldo.api.repository.rendavariavel.SetoresRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class SetoresService {

    @Autowired
    private SetoresRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    public ResponseEntity<Setores> cadastrarSetor(Setores setor, HttpServletResponse response) {

        setor.setStatus(true);
        Setores setorSalvo = repository.save(setor);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, setorSalvo.getSetorId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(setorSalvo);
    }

    public Setores atualizarSetor(Long codigo, Setores setor) {

        Setores setorSalvo = buscaPorId(codigo);
        BeanUtils.copyProperties(setor, setorSalvo, "setorId");
        return repository.save(setorSalvo);
    }

    public Setores verificaSetorCadastrado(String nomeSetor) {

        Setores setorId = repository.buscaPorNomeCategoria(nomeSetor);
        return setorId;
    }

    public Setores cadastrarSetorAutomatico(Setores setor, HttpServletResponse response) {

        setor.setStatus(true);
        Setores setorSalvo = repository.save(setor);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, setorSalvo.getSetorId()));

        return setorSalvo;
    }

    public void atualizaStatusAtivo(Long codigo, Boolean ativo) {

        Setores setorSalvo = buscaPorId(codigo);
        setorSalvo.setStatus(ativo);
        repository.save(setorSalvo);
    }

    private Setores buscaPorId(Long codigo) {

        Setores setorSalvo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));

        return setorSalvo;
    }
}

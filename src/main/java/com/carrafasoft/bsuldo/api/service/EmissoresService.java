package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Emissores;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.repository.EmissoresRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class EmissoresService {

    @Autowired
    private EmissoresRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private PessoaService pessoaService;

    public ResponseEntity<Emissores> cadastrarEmissor(Emissores emissores, HttpServletResponse response, String tokenId) {

        Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));

        emissores.setPessoa(pessoaSalva);

        Emissores emissorSalvo = repository.save(emissores);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, emissorSalvo.getEmissorId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(emissorSalvo);
    }

    public Emissores atualizaEmissor(Long codigo, Emissores emissores, String tokenId) {

        Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));

        emissores.setPessoa(pessoaSalva);

        Emissores emissorSalvo = buscaPorId(codigo);
        BeanUtils.copyProperties(emissores, emissorSalvo, "emissorId");
        return repository.save(emissorSalvo);
    }

    public void atualizaStatusAtivo(Long codigo, Boolean ativo) {

        Emissores emissorSalvo = buscaPorId(codigo);
        emissorSalvo.setStatus(ativo);
        repository.save(emissorSalvo);
    }

    public List<Emissores> buscaEmissorPorNome(String nomeEmissor, String tokenId) {
        return repository.buscaPorNomeEmissor(nomeEmissor, pessoaService.recuperaIdPessoaByToken(tokenId));
    }

    public Emissores cadastrarEmissorAutomatico(Emissores emissores, HttpServletResponse response) {

        Emissores emissorSalvo = repository.save(emissores);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, emissorSalvo.getEmissorId()));

        return emissorSalvo;
    }

    private Emissores buscaPorId(Long codigo) {

        Emissores emissorSalvo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return emissorSalvo;
    }


}

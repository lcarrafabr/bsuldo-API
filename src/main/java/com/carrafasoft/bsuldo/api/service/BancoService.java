package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Bancos;
import com.carrafasoft.bsuldo.api.repository.BancoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@Slf4j
public class BancoService {

    @Autowired
    private BancoRepository repository;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ApplicationEventPublisher publisher;

    public ResponseEntity<?> cadastrarBanco(Bancos bancos, HttpServletResponse response, Long pessoaId) {

        log.info("...: Iniciando cadastro. banco: " + bancos.getNomeBanco() + " |  pessoaId: " +pessoaId + " :...");
        try {
            if(!pessoaId.equals(null)) {
                bancos.setPessoa(pessoaService.buscaPessoaPorId(pessoaId));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
            }

            Bancos bancoSalvo = repository.save(bancos);
            publisher.publishEvent(new RecursoCriadoEvent(this, response, bancoSalvo.getBancoId()));

            log.info("...: Banco cadastrado com sucesso :...");

            return ResponseEntity.status(HttpStatus.CREATED).body(bancoSalvo);

        } catch (Exception e) {
            log.error("...: Erro ao cadastrar banco: " + bancos.getNomeBanco() + "| pessoaId: " +pessoaId + " :...", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    public ResponseEntity<?> buscaBancoPorId(Long codigo) {

        Optional<Bancos> bancoSalvo = repository.findById(codigo);

        return bancoSalvo.isPresent() ? ResponseEntity.ok(bancoSalvo.get()) : ResponseEntity.noContent().build();
    }

    public Bancos atualizaBancoSalvo(Long codigo, Bancos banco) {

        if(banco.getStatus() == null) {
            banco.setStatus(true) ;
        }

        Bancos bancoSalvo = buscaPorId(codigo);
        BeanUtils.copyProperties(banco, bancoSalvo, "bancoId");

        return repository.save(bancoSalvo);
    }

    public void atualizaStatusAtivo(Long codigo, Boolean ativo) {

        Bancos bancoSalvo = buscaPorId(codigo);
        bancoSalvo.setStatus(ativo);
        repository.save(bancoSalvo);
    }

    private Bancos buscaPorId(Long codigo) {

        Bancos bancoSalvo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return bancoSalvo;
    }
}

package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Bancos;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.BancoNaoEncontradoException;
import com.carrafasoft.bsuldo.api.repository.BancoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public Bancos cadastrarBanco(Bancos bancos, HttpServletResponse response, String tokenId) {

        Long pessoaSalvaId = pessoaService.recuperaIdPessoaByToken(tokenId);
        Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaSalvaId);

        bancos.setPessoa(pessoaSalva);

        log.info("...: Iniciando cadastro. banco: " + bancos.getNomeBanco() + " |  pessoaId: " + bancos.getPessoa().getPessoaID() + " :...");
        try {
            Bancos bancoSalvo = repository.save(bancos);
            publisher.publishEvent(new RecursoCriadoEvent(this, response, bancoSalvo.getBancoId()));

            log.info("...: Banco cadastrado com sucesso :...");

            //return ResponseEntity.status(HttpStatus.CREATED).body(bancoSalvo);
            return bancoSalvo;

        } catch (Exception e) {
            log.error("...: Erro ao cadastrar banco: " + bancos.getNomeBanco() + "| pessoaId: " + bancos.getPessoa().getPessoaID() + " :...", e);
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Bancos());
            return new Bancos();
        }
    }

    public Bancos buscaBancoPorId(Long codigo, String pessoaId) {

        Long pessoaSalvaId = pessoaService.recuperaIdPessoaByToken(pessoaId);
        Pessoas pessoa = pessoaService.buscaPessoaPorId(pessoaSalvaId);
        return repository.findByIdAndPessoaId(pessoaSalvaId, codigo).orElseThrow(() -> new BancoNaoEncontradoException(codigo));
    }

    public Bancos atualizaBancoSalvo(Long codigo, Bancos banco, String pessoaId) {

        Long pessoaSalvaId = pessoaService.recuperaIdPessoaByToken(pessoaId);
        Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaSalvaId);

        banco.setPessoa(pessoaSalva);

        log.info("...: Iniciando atualização de banco ID: {}", codigo + " :...");
        if(banco.getStatus() == null) {
            banco.setStatus(true) ;
        }

        Bancos bancoSalvo = buscaPorId(codigo);
        BeanUtils.copyProperties(banco, bancoSalvo, "bancoId");
        log.info("...: Nome banco após da alteração: {}", bancoSalvo.getNomeBanco() + " :...");

        return repository.save(bancoSalvo);
    }

    public void atualizaStatusAtivo(Long codigo, Boolean ativo) {

        log.info("...: Atualizando status do bancoId {} para {}", codigo, ativo + " :...");
        Bancos bancoSalvo = buscaPorId(codigo);
        bancoSalvo.setStatus(ativo);
        repository.save(bancoSalvo);
        log.info("...: Status atualizado com sucesso :...");
    }

    public Bancos buscaPorId(Long codigo) {

        return repository.findById(codigo).orElseThrow(() -> new BancoNaoEncontradoException(codigo));
    }
}

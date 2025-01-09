package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.exception.entidadeException.EntidadeEmUsoException;
import com.carrafasoft.bsuldo.api.mapper.BancoMapper;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.BancoRequestInputRepresentation;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.BancoResponseRepresentation;
import com.carrafasoft.bsuldo.api.model.Bancos;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.BancoNaoEncontradoException;
import com.carrafasoft.bsuldo.api.repository.BancoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@Slf4j
public class BancoService {

    private static final String BANCO_EM_USO = "O banco de código %s não pode ser removido, pois está em uso.";

    @Autowired
    private BancoRepository repository;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private BancoMapper bancoMapper;




    @Transactional
    public Bancos cadastrarBanco(BancoRequestInputRepresentation bancos, HttpServletResponse response, String tokenId) {

        Long pessoaSalvaId = pessoaService.recuperaIdPessoaByToken(tokenId);
        try {
            log.info("...: Iniciando cadastro. banco: " + bancos.getNomeBanco() + " |  pessoaId: " + pessoaSalvaId + " :...");

            Bancos bancoToSave = bancoMapper.toBancoMapper(bancos, pessoaSalvaId);

            Bancos bancoSalvo = repository.save(bancoToSave);
            publisher.publishEvent(new RecursoCriadoEvent(this, response, bancoSalvo.getBancoId()));

            log.info("...: Banco cadastrado com sucesso :...");
            return bancoSalvo;

        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public Bancos buscaBancoPorId(String codigo, String tokenId) {

        return repository.findByCodigoBancoAndPessoaId(codigo, pessoaService.recuperaIdPessoaByToken(tokenId))
                .orElseThrow(() -> new BancoNaoEncontradoException(codigo));
    }

    @Transactional
    public Bancos atualizaBancoSalvo(String codigo, BancoResponseRepresentation banco, String tokenId) {

        try {
            log.info("...: Iniciando atualização de banco ID: {}", codigo + " :...");
            if(banco.getStatus() == null) {
                banco.setStatus(true) ;
            }

            Bancos bancoToSave = buscaPorCodigoEPessoaId(codigo, tokenId);
            BeanUtils.copyProperties(banco, bancoToSave, "bancoId");
            log.info("...: Nome banco após da alteração: {}", bancoToSave.getNomeBanco() + " :...");

            return repository.save(bancoToSave);
        } catch (BancoNaoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    public void remover(String codigo) {

        try {
            repository.deleteByCodigoBanco(codigo);
        } catch (EmptyResultDataAccessException e) {
            throw new BancoNaoEncontradoException(codigo);
        } catch (DataIntegrityViolationException e) {
            throw new EntidadeEmUsoException(String.format(BANCO_EM_USO, codigo));
        }
    }

    @Transactional
    public void atualizaStatusAtivo(String codigo, Boolean ativo) {

        try {
            log.info("...: Atualizando status do bancoId {} para {}", codigo, ativo + " :...");
            Bancos bancoSalvo = findByCodigoBanco(codigo);
            bancoSalvo.setStatus(ativo);
            repository.save(bancoSalvo);
            log.info("...: Status atualizado com sucesso :...");
        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public Bancos buscaPorCodigoEPessoaId(String codigo, String tokenId) {

        return repository.findByCodigoBancoAndPessoaId(
                codigo, pessoaService.recuperaIdPessoaByToken(tokenId))
                .orElseThrow(() -> new BancoNaoEncontradoException(codigo));
    }

    public Bancos findByCodigoBanco(String codigo) {

        return repository.findByCodigoBanco(codigo).orElseThrow(() -> new BancoNaoEncontradoException(codigo.toString()));
    }
}

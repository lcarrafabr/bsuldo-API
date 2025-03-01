package com.carrafasoft.bsuldo.api.service.serviceImpl.critomoeda;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.mapper.OrigenMapper;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.OrigemInput;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.OrigemUpdateInput;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Origens;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.OrigemNaoEncontradaException;
import com.carrafasoft.bsuldo.api.repository.criptomoedas.OrigemRepository;
import com.carrafasoft.bsuldo.api.service.OrigemService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Service
public class OrigemServiceImpl implements OrigemService {


    @Autowired
    private OrigemRepository repository;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private OrigenMapper origenMapper;

    @Autowired
    private ApplicationEventPublisher publisher;



    @Override
    public Origens buscaOrigemPorIdAndToken(String codigoOrigem, String tokenId) {

        return repository.findByCodigoOrigemAndPessoaId(codigoOrigem,
                pessoaService.recuperaIdPessoaByToken(tokenId)).orElseThrow(() -> new OrigemNaoEncontradaException(codigoOrigem));
    }

    @Transactional
    @Override
    public Origens cadastrarOrigem(OrigemInput origemInput, String tokenId, HttpServletResponse response) {

        try {

            log.info("...: Preparando pra cadastrar Origem - Pessoa: {}:...", tokenId);

            Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
            Origens origemToSave = origenMapper.toOrigemModel(origemInput, pessoaSalva);

            Origens origemSalvo = repository.save(origemToSave);

            publisher.publishEvent(new RecursoCriadoEvent(this, response, origemSalvo.getOrigemId()));

            log.info("...: Origem cadastrado com sucesso. :...");

            return origemSalvo;
        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public Origens atualizarOrigem(String codigoOrigem, OrigemUpdateInput origemInput, String tokenId) {

        try {
            Origens origemSalva = buscaOrigemPorIdAndToken(codigoOrigem, tokenId);
            BeanUtils.copyProperties(origemInput, origemSalva, "origemId");
            return repository.save(origemSalva);

        } catch (OrigemNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void removerOrigem(String codigoOrigem) {

        try {
            repository.deleteByCodigoOrigem(codigoOrigem);
        } catch (EmptyResultDataAccessException e) {
            throw new OrigemNaoEncontradaException(codigoOrigem);
        } catch (DataIntegrityViolationException e) {
            throw new EntidadeNaoEncontradaException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void atualizaStatusAtivo(String codigoOrigem, Boolean ativo) {

        try {
            Origens origemSalva = findByCodigoOrigem(codigoOrigem);
            origemSalva.setStatusAtivo(ativo);
            repository.save(origemSalva);
        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }

    }

    public Origens findByCodigoOrigem(String codigoOrigem){

        return repository.findByCodigoOrigem(codigoOrigem).orElseThrow(() -> new OrigemNaoEncontradaException(codigoOrigem));
    }

    @Override
    public List<Origens> buscaOrigemPorNome(String nomeOrigem, String tokenid) {

        List<Origens> origemList = repository.findByNomeOrigemAndPessoaId(
                nomeOrigem, pessoaService.recuperaIdPessoaByToken(tokenid)
        );

        return origemList;
    }
}

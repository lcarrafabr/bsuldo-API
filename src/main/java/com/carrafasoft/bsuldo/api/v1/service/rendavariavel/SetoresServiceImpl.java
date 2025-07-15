package com.carrafasoft.bsuldo.api.v1.service.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorInputUpdateRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel.SetoresMapper;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.exceptionmodel.SetorNaoEncontradoException;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Setores;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.SetoresRepository;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.SetoresService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
public class SetoresServiceImpl implements SetoresService {

    private static final String SETOR_EM_USO = "O setor de código %s não pode ser removido pois está em uso.";

    @Autowired
    private SetoresRepository repository;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private SetoresMapper setoresMapper;

    @Transactional
    @Override
    public SetorResponseRepresentation cadastrarSetor(SetorInputRepresentation setorInput, HttpServletResponse response, String tokenId) {

        try {
            Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));

            Setores setorToSave = setoresMapper.toentity(setorInput, pessoaSalva);

            Setores setorSalvo = repository.save(setorToSave);
            publisher.publishEvent(new RecursoCriadoEvent(this, response, setorSalvo.getSetorId()));

            return setoresMapper.toSetorResponseRepresentationMapper(setorSalvo);

        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public SetorResponseRepresentation atualizarSetor(String codigo, SetorInputUpdateRepresentation setor, String tokenId) {

        try {
            Setores setorSalvo = findByCodigoSetorAndTokenId(codigo, tokenId);

            BeanUtils.copyProperties(setor, setorSalvo, "setorId");
            return setoresMapper.toSetorResponseRepresentationMapper(repository.save(setorSalvo));

        } catch (SetorNaoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void removerSetor(String codigo) {

        try {
            repository.deleteByCodigoSetor(codigo);
        } catch (EmptyResultDataAccessException e) {
            throw new SetorNaoEncontradoException(codigo);
        } catch (DataIntegrityViolationException e) {
            throw new NegocioException(String.format(SETOR_EM_USO, codigo));
        }
    }

    public Setores verificaSetorCadastrado(String nomeSetor, Long pessoaId) {

        Setores setorId = repository.buscaPorNomeCategoria(nomeSetor, pessoaId);
        return setorId;
    }

    public Setores cadastrarSetorAutomatico(Setores setor, HttpServletResponse response) {

        setor.setStatus(true);
        Setores setorSalvo = repository.save(setor);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, setorSalvo.getSetorId()));

        return setorSalvo;
    }

    public void atualizaStatusAtivo(String codigo, Boolean ativo) {

        Setores setorSalvo = findBiCodigoSetor(codigo);
        setorSalvo.setStatus(ativo);
        repository.save(setorSalvo);
    }

    private Setores findBiCodigoSetor(String codigo) {

        return repository.findByCodigoSetor(codigo).orElseThrow(() -> new SetorNaoEncontradoException(codigo));
    }

    private Setores buscaPorId(Long codigo) {

        Setores setorSalvo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));

        return setorSalvo;
    }

    @Override
    public Setores findByCodigoSetorAndTokenId(String codigoSetor, String tokenId) {

        return repository.findByCodigoSetorAndPessoaId(codigoSetor,
                pessoaService.recuperaIdPessoaByToken(tokenId))
                .orElseThrow(() -> new SetorNaoEncontradoException(codigoSetor));
    }
}

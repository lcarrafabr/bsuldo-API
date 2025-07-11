package com.carrafasoft.bsuldo.api.v1.service.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.carrafasoft.bsuldo.api.v1.exception.entidadeException.EntidadeEmUsoException;
import com.carrafasoft.bsuldo.api.v1.mapper.SegmentoMapper;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SegmentoInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SegmentoInputUpdateRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SegmentoResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.exceptionmodel.SegmentoNãoEncontradoException;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Segmentos;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.SegmentosRepository;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
public class SegmentosService {

    private static final String SEGMENTO_EM_USO = "O segmento de código %s não pode ser removido pois está em uso.";

    @Autowired
    private SegmentosRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private SegmentoMapper segmentoMapper;


    @Transactional
    public SegmentoResponseRepresentation cadastrarSegmento(SegmentoInputRepresentation segmento, HttpServletResponse response, String tokenId) {

       try {
           Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
           segmento.setStatus(true);

           Segmentos segmentoToSave = segmentoMapper.toEntity(segmento, pessoaSalva);

           Segmentos segmentoSalvo = repository.save(segmentoToSave);
           publisher.publishEvent(new RecursoCriadoEvent(this, response, segmentoSalvo.getSegmentoId()));

           return segmentoMapper.toSegmentoResponseRepresentationMapper(segmentoSalvo);
       } catch (EntidadeNaoEncontradaException e) {
           throw new NegocioException(e.getMessage());
       }
    }

    @Transactional
    public SegmentoResponseRepresentation atualizarSegmento(String codigo, SegmentoInputUpdateRepresentation segmento, String tokenId) {

        try {
            Segmentos segmentoSalvo = findByCodigoSegmentoAndTokenId(codigo, tokenId);
            BeanUtils.copyProperties(segmento, segmentoSalvo, "segmentoId");
            return segmentoMapper.toSegmentoResponseRepresentationMapper(repository.save(segmentoSalvo));
        } catch (SegmentoNãoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    public void removerSegmento(String codigoSegmento) {

        try {
            repository.deleleteByCodigoSegmento(codigoSegmento);
        } catch (EmptyResultDataAccessException e) {
            throw new SegmentoNãoEncontradoException(codigoSegmento);
        } catch (DataIntegrityViolationException e) {
            throw new EntidadeEmUsoException(String.format(SEGMENTO_EM_USO, codigoSegmento));
        }
    }

    public Segmentos pesquisaPorNomeSegmento(String nomeSegmento, Long pessoaId) {

        return repository.buscaPorNomeSegmento(nomeSegmento, pessoaId);
    }

    public Segmentos cadastrarSegmentoAutomatico(Segmentos segmento, HttpServletResponse response) {

        segmento.setStatus(true);
        Segmentos segmentoSalvo = repository.save(segmento);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, segmentoSalvo.getSegmentoId()));

        return segmentoSalvo;
    }

    @Transactional
    public void atualizaStatusAtivo(String codigo, Boolean ativo) {

        try {
            Segmentos segmentoSalvo = findByCodigo(codigo);
            segmentoSalvo.setStatus(ativo);
            repository.save(segmentoSalvo);
        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public Segmentos findByCodigoSegmentoAndTokenId(String codigoSegmento, String tokenId) {

        return repository.findByCodigoSegmentoAndTokenId(codigoSegmento,
                pessoaService.recuperaIdPessoaByToken(tokenId)).orElseThrow(() -> new SegmentoNãoEncontradoException(codigoSegmento));
    }

    public Segmentos findByCodigo(String codigoSegmento) {

        return repository.findByCodigoSegmento(codigoSegmento).orElseThrow(() -> new SegmentoNãoEncontradoException(codigoSegmento));
    }

    private Segmentos buscaPorID(Long codigo) {

        Segmentos segmentoSalvo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return  segmentoSalvo;
    }
}

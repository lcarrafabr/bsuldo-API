package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.exception.entidadeException.EntidadeEmUsoException;
import com.carrafasoft.bsuldo.api.mapper.MetodoCobrancaMapper;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.MetodoCobrancaInputRepresentation;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.MetodoCobrancaRequestInputRepresentation;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.MetodoCobrancaResponseRepresentation;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.MetodoDeCobrancaNaoEncontradoException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.model.MetodoDeCobranca;
import com.carrafasoft.bsuldo.api.repository.MetodoDeCobrancaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

@Service
public class MetodoCobracaService {

	private static final String METODO_DE_COBRANCA_EM_USO = "M método de cobrança de código %s não pode ser removida, pois está em uso.";
	
	@Autowired
	private MetodoDeCobrancaRepository metodoCobrancaRepository;

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private MetodoCobrancaMapper metodoCobrancaMapper;

	@Autowired
	private ApplicationEventPublisher publisher;


	@Transactional
	public MetodoCobrancaResponseRepresentation cadastrarMetodoCobranca(MetodoCobrancaInputRepresentation metodoCobrancaInput,
																		String tokenId, HttpServletResponse response) {

		try {
			Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

			MetodoDeCobranca metodoDeCobranca = metodoCobrancaMapper.toMetodoCobrancaMapper(metodoCobrancaInput, pessoaId);

			MetodoDeCobranca metodoCobSalvo = metodoCobrancaRepository.save(metodoDeCobranca);
			publisher.publishEvent(new RecursoCriadoEvent(this, response, metodoCobSalvo.getMetodoCobrancaId()));

			return metodoCobrancaMapper.toMetodoCobrancaResponseRepresentationMapper(metodoCobSalvo);
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@Transactional
	public MetodoCobrancaResponseRepresentation atualizarMetodoCob(String codigo, MetodoCobrancaRequestInputRepresentation metodoCob, String tokenId) {

		
		try {
			MetodoDeCobranca metodoSalvo = findMetodoCobPorCodigoAndTokenId(codigo, tokenId);
			BeanUtils.copyProperties(metodoCob, metodoSalvo, "metodoCobrancaId");

			return metodoCobrancaMapper.toMetodoCobrancaResponseRepresentationMapper(metodoCobrancaRepository.save(metodoSalvo));
		} catch (MetodoDeCobrancaNaoEncontradoException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@Transactional
	public void remover(String codigo) {

		try {
			metodoCobrancaRepository.deleteByCodigoMetodoCobranca(codigo);
		} catch (EmptyResultDataAccessException e) {
			throw new MetodoDeCobrancaNaoEncontradoException(codigo);
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(String.format(METODO_DE_COBRANCA_EM_USO, codigo));
		}
	}

	@Transactional
	public void atualizarStatus(String codigo, Boolean ativo) {
		
		MetodoDeCobranca metodoSalvo = findCodigoMetodoCobranca(codigo);
		metodoSalvo.setStatus(ativo);
		metodoCobrancaRepository.save(metodoSalvo);
	}


	public MetodoDeCobranca findMetodoCobPorCodigoAndTokenId(String metodoCobCodigo, String tokenId) {

		return metodoCobrancaRepository.findByCodigoMetodoCobrancaAndPessoaId(
				metodoCobCodigo, pessoaService.recuperaIdPessoaByToken(tokenId)
		).orElseThrow(() -> new MetodoDeCobrancaNaoEncontradoException(metodoCobCodigo));
	}

	public MetodoDeCobranca findCodigoMetodoCobranca(String codigoMetodoCobranca) {

		return metodoCobrancaRepository.findByCodigoMetodoCobranca(codigoMetodoCobranca)
				.orElseThrow(() -> new MetodoDeCobrancaNaoEncontradoException(codigoMetodoCobranca));
	}

	
	private MetodoDeCobranca buscaPorId(Long codigo) {
		
		MetodoDeCobranca metodoCobSalvo = metodoCobrancaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
		return metodoCobSalvo;
	}
}

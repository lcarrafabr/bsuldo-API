package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.carrafasoft.bsuldo.api.v1.exception.entidadeException.EntidadeEmUsoException;
import com.carrafasoft.bsuldo.api.v1.mapper.MetodoCobrancaMapper;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.MetodoCobrancaInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.MetodoCobrancaRequestInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.MetodoCobrancaResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.MetodoDeCobranca;
import com.carrafasoft.bsuldo.api.v1.model.exceptionmodel.MetodoDeCobrancaNaoEncontradoException;
import com.carrafasoft.bsuldo.api.v1.repository.MetodoDeCobrancaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
public class MetodoCobracaService {

	private static final String METODO_DE_COBRANCA_EM_USO = "O método de cobrança de código %s não pode ser removido, pois está em uso.";
	
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
		
		try {
			MetodoDeCobranca metodoSalvo = findCodigoMetodoCobranca(codigo);
			metodoSalvo.setStatus(ativo);
			metodoCobrancaRepository.save(metodoSalvo);
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
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

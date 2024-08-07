package com.carrafasoft.bsuldo.api.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.CoresConfig;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.repository.CoresConfigRepository;
import com.carrafasoft.bsuldo.api.repository.PessoaRepository;

@Service
public class CoresConfigService {
	
	@Autowired
	private CoresConfigRepository configRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private PessoaService pessoaService;
	
	
	public HashMap<String, Object> listaCoresParaGraficos(String tokenId) {

		Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);
		
		var coresGrafico = new HashMap<String, Object>();
		List<String> coresPrincipais = configRepository.coresGraficosPrincipais(pessoaId);
		List<String> coresSecundarias = configRepository.coresGraficosSecundarias(pessoaId);
		
		coresGrafico.put("cores_principais", coresPrincipais);
		coresGrafico.put("cores_secundarias", coresSecundarias);
		
		return coresGrafico;
	}


	public ResponseEntity<CoresConfig> salvarCores(@Valid CoresConfig coresConfig, HttpServletResponse response) {
		
		
		coresConfig.setPessoa(buscaPessoaId(coresConfig.getPessoa().getPessoaID()));
		
		CoresConfig corSalva = configRepository.save(coresConfig);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, corSalva.getCoresConfigId()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(corSalva);
	}

	public void atualizaUsarCoresPadrao(Long pessoaId, Boolean usarCoresPadrao) {

		configRepository.atualizaCoresPadrao(pessoaId, usarCoresPadrao);

	}
	
	private Pessoas buscaPessoaId(Long codigo) {
		
		Pessoas pessoaSalva = pessoaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
		
		return pessoaSalva;
	}

}

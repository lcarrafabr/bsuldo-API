package com.carrafasoft.bsuldo.api.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.enums.SituacaoEnum;
import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Lancamentos;
import com.carrafasoft.bsuldo.api.repository.LancamentoRepository;
import com.carrafasoft.bsuldo.api.utils.FuncoesUtils;

@Service
public class LancamentoService {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	
	public ResponseEntity<?> cadastrarLancamentoSemParcelamento(Lancamentos lancamento, HttpServletResponse response) {
		
		ResponseEntity<Lancamentos> httpStatus = new ResponseEntity<Lancamentos>(HttpStatus.METHOD_NOT_ALLOWED);
		
		String chavePesquisa = gerarChavePesquisa();
		
		lancamento.setChavePesquisa(chavePesquisa);
		
		Lancamentos lancamentoSalvo = lancamentoRepository.save(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getLancamentoId()));
		
		httpStatus = ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
		
		return httpStatus;
	}
	
	
	private String gerarChavePesquisa() {
		
		String chavePesquisa = FuncoesUtils.gerarHash();
		List<Lancamentos> verificaLanc = lancamentoRepository.buscarPorchave(chavePesquisa);
		
		while(!verificaLanc.isEmpty()) {
			 
			 chavePesquisa = FuncoesUtils.gerarHash();
			 verificaLanc = lancamentoRepository.buscarPorchave(chavePesquisa);
		 }
		
		return chavePesquisa;
	}

}

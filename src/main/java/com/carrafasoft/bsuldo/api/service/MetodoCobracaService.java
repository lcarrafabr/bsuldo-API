package com.carrafasoft.bsuldo.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.model.MetodoDeCobranca;
import com.carrafasoft.bsuldo.api.repository.MetodoDeCobrancaRepository;

@Service
public class MetodoCobracaService {
	
	@Autowired
	private MetodoDeCobrancaRepository metodoCobrancaRepository;
	
	
	public MetodoDeCobranca atualizarMetodoCob(Long codigo, MetodoDeCobranca metodoCob) {
		
		MetodoDeCobranca metodoSalvo = buscaPorId(codigo);
		BeanUtils.copyProperties(metodoCob, metodoSalvo, "metodoCobrancaId");
		
		return metodoCobrancaRepository.save(metodoSalvo);
	}
	
	public void atualizarStatus(Long codigo, Boolean ativo) {
		
		MetodoDeCobranca metodoSalvo = buscaPorId(codigo);
		metodoSalvo.setStatus(ativo);
		metodoCobrancaRepository.save(metodoSalvo);
	}
	
	
	private MetodoDeCobranca buscaPorId(Long codigo) {
		
		MetodoDeCobranca metodoCobSalvo = metodoCobrancaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
		return metodoCobSalvo;
	}

}

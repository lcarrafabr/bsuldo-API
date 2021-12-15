package com.carrafasoft.bsuldo.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.model.Permissao;
import com.carrafasoft.bsuldo.api.repository.PermissaoRepository;

@Service
public class PermissoesService {
	
	@Autowired
	private PermissaoRepository permissaoRepository;
	
	public Permissao atualizarPermissao(Permissao permissao, Long codigo) {
		
		Permissao permissaoSalvo = permissaoRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
		BeanUtils.copyProperties(permissao, permissaoSalvo, "permissaoId");
		return permissaoRepository.save(permissaoSalvo);
	}

}

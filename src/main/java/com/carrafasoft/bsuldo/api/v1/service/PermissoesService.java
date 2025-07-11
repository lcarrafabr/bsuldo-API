package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.model.Permissao;
import com.carrafasoft.bsuldo.api.v1.repository.PermissaoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

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

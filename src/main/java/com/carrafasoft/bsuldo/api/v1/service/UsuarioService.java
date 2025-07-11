package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.model.Usuarios;
import com.carrafasoft.bsuldo.api.v1.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	
	public Usuarios atualizaUsuario(Usuarios usuario, Long codigo) {
		
		Usuarios usuarioSalvo = buscaPorId(codigo);
		BeanUtils.copyProperties(usuario, usuarioSalvo, "usuarioId");
		
		return usuarioRepository.save(usuarioSalvo);
	}
	
	public void atualizaStatusAtivo(Long codigo, Boolean ativo) {
		
		Usuarios usuarioSalvo = buscaPorId(codigo);
		usuarioSalvo.setStatus(ativo);
		usuarioRepository.save(usuarioSalvo);
	}
	
	private Usuarios buscaPorId(Long codigo) {
		
		Usuarios usuarioSalvo = usuarioRepository.findById(codigo).orElseThrow(()-> new EmptyResultDataAccessException(1));
		
		return usuarioSalvo;
	}

}

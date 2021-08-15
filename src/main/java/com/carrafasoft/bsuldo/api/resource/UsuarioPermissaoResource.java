package com.carrafasoft.bsuldo.api.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.UsuarioPermissao;
import com.carrafasoft.bsuldo.api.repository.UsuarioPermissaoRepository;

@RestController
@RequestMapping("/user-permition")
public class UsuarioPermissaoResource {
	
	@Autowired
	private UsuarioPermissaoRepository usuarioPermissaoRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	public List<UsuarioPermissao> listarTodos() {
		
		return usuarioPermissaoRepository.findAll();
	}
	
	@PostMapping
	public ResponseEntity<UsuarioPermissao> cadastrarPermissoesUsuario(@Valid @RequestBody UsuarioPermissao userPermition, HttpServletResponse response) {
		
		UsuarioPermissao userPermitionSalvo = usuarioPermissaoRepository.save(userPermition);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, userPermitionSalvo.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(userPermitionSalvo);
	}

}

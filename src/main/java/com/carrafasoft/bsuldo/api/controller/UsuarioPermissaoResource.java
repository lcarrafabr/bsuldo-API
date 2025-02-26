package com.carrafasoft.bsuldo.api.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Permissao;
import com.carrafasoft.bsuldo.api.model.UsuarioPermissao;
import com.carrafasoft.bsuldo.api.repository.UsuarioPermissaoRepository;
import com.carrafasoft.bsuldo.api.service.UsuarioPermissaoService;

@RestController
@RequestMapping("/user-permition")
public class UsuarioPermissaoResource {
	
	@Autowired
	private UsuarioPermissaoRepository usuarioPermissaoRepository;
	
	@Autowired
	private UsuarioPermissaoService usuarioPermissaoService;
	
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
	
	
	@GetMapping("/{codigo}")
	public ResponseEntity<UsuarioPermissao> buscaPorId(@PathVariable Long codigo) {
		
		Optional<UsuarioPermissao> userPermition = usuarioPermissaoRepository.findById(codigo);
		
		return userPermition.isPresent() ? ResponseEntity.ok(userPermition.get()) : ResponseEntity.notFound().build();
	}
	
	@GetMapping("/user-permition-disponiveis/{codigo}")
	public List<Permissao> permissoesDisponiveis(@PathVariable Long codigo) {
		
		return usuarioPermissaoService.retornaPermissoesDisponiveis(codigo);
	}
	
	@PostMapping("cadastrat-permissoes")
	public List<Permissao> cadastraPermissoesusuarioViaList(@RequestBody List<Permissao> lista,@RequestParam("id") String id, HttpServletResponse response) {
		
		usuarioPermissaoService.gerenciarPermissoesUsuario(lista, id);
		
		
		return null;
	}

}

package com.carrafasoft.bsuldo.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Usuarios;
import com.carrafasoft.bsuldo.api.repository.UsuarioRepository;
import com.carrafasoft.bsuldo.api.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping
	public List<Usuarios> listarTodosUsuarios() {
		
		return usuarioRepository.findAll();
	}
	
	
	@PostMapping
	public ResponseEntity<Usuarios> cadastrarUsuario(@Valid @RequestBody Usuarios usuario, HttpServletResponse response) {
		
		Usuarios usuarioSalvo = usuarioRepository.save(usuario);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, usuarioSalvo.getUsuarioId()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Usuarios> buscaPorId(@PathVariable Long codigo) {
		
		Optional<Usuarios> usuarioSalvo = usuarioRepository.findById(codigo);
		
		return usuarioSalvo.isPresent() ? ResponseEntity.ok(usuarioSalvo.get()) : ResponseEntity.notFound().build();
	}
	
	@PutMapping("/{codigo}")
	public ResponseEntity<Usuarios> atualizarUsuario(@PathVariable Long codigo, @Valid @RequestBody Usuarios usuario) {
		
		Usuarios usuarioSalvo = usuarioService.atualizaUsuario(usuario, codigo);
		
		return ResponseEntity.ok(usuarioSalvo);
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerUsuario(@PathVariable Long codigo) {
		
		usuarioRepository.deleteById(codigo);
	}
	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizaStatusAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		
		usuarioService.atualizaStatusAtivo(codigo, ativo);
	}

}

package com.carrafasoft.bsuldo.api.controller;

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
import com.carrafasoft.bsuldo.api.model.Permissao;
import com.carrafasoft.bsuldo.api.repository.PermissaoRepository;
import com.carrafasoft.bsuldo.api.service.PermissoesService;

@RestController
@RequestMapping("/permissoes")
public class PermissaoResource {
	
	@Autowired
	private PermissaoRepository permissaoRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private PermissoesService permissaoService;

	@GetMapping
	public List<Permissao> listarTodos() {
		
		return permissaoRepository.findAll();
	}
	
	@PostMapping
	public ResponseEntity<Permissao> cadastrarPermissao(@Valid @RequestBody Permissao permissao, HttpServletResponse response) {
		
		Permissao permissaoSalva = permissaoRepository.save(permissao);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, permissaoSalva.getPermissaoId()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(permissaoSalva);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Permissao> buscaPorID(@PathVariable Long codigo) {
		
		Optional<Permissao> permissaoSalva = permissaoRepository.findById(codigo);
		
		return permissaoSalva.isPresent() ? ResponseEntity.ok(permissaoSalva.get()) : ResponseEntity.notFound().build();
	}
	
	@PutMapping("/{codigo}")
	public ResponseEntity<Permissao> atualizarPermissao(@PathVariable Long codigo, @Valid @RequestBody Permissao permissao) {
		
		Permissao permissaoSalvo = permissaoService.atualizarPermissao(permissao, codigo);
		
		return ResponseEntity.ok(permissaoSalvo);
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerPermissao(@PathVariable Long codigo) {
		
		permissaoRepository.deleteById(codigo);
	}
}

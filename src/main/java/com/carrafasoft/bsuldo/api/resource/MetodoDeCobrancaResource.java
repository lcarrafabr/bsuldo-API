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
import com.carrafasoft.bsuldo.api.model.MetodoDeCobranca;
import com.carrafasoft.bsuldo.api.repository.MetodoDeCobrancaRepository;
import com.carrafasoft.bsuldo.api.service.MetodoCobracaService;

@RestController
@RequestMapping("/metodo-de-cobranca")
public class MetodoDeCobrancaResource {
	
	@Autowired
	private MetodoDeCobrancaRepository metodoCobrancaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private MetodoCobracaService metodoCobService;
	
	@GetMapping
	public List<MetodoDeCobranca> listarTodos() {
		
		return metodoCobrancaRepository.findAll();
	}
	
	@PostMapping
	public ResponseEntity<MetodoDeCobranca> cadastrarMetodoCobranca(@Valid @RequestBody MetodoDeCobranca metodoCobranca, HttpServletResponse response) {
		
		MetodoDeCobranca metodoCobSalvo = metodoCobrancaRepository.save(metodoCobranca);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, metodoCobSalvo.getMetodoCobrancaId()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(metodoCobSalvo);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<MetodoDeCobranca> buscaPorId(@PathVariable Long codigo) {
		
		Optional<MetodoDeCobranca> metodoCobSalvo = metodoCobrancaRepository.findById(codigo);
		
		return metodoCobSalvo.isPresent() ? ResponseEntity.ok(metodoCobSalvo.get()) : ResponseEntity.notFound().build();
	}
	
	@PutMapping("/{codigo}")
	public ResponseEntity<MetodoDeCobranca> atualizarMetodoCobranca(@PathVariable Long codigo, @Valid @RequestBody MetodoDeCobranca metodoCob) {
		
		MetodoDeCobranca metodoSalvo = metodoCobService.atualizarMetodoCob(codigo, metodoCob);
		
		return ResponseEntity.ok(metodoSalvo);
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerMetodoCobranca(@PathVariable Long codigo) {
		
		metodoCobrancaRepository.deleteById(codigo);
	}
	
	
	@PutMapping("{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizaStatusAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		
		metodoCobService.atualizarStatus(codigo, ativo);
	}

}

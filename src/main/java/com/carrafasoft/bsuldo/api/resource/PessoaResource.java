package com.carrafasoft.bsuldo.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.repository.PessoaRepository;
import com.carrafasoft.bsuldo.api.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private PessoaService pessoaService;
	
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA')")
	public List<Pessoas> listarPessoas() {
		
		return pessoaRepository.findAll();
	}
	
	@PostMapping
	public ResponseEntity<Pessoas> cadastrarPessoa(@Valid @RequestBody Pessoas pessoa, HttpServletResponse response) {
		
		Pessoas pessoaSalva = pessoaRepository.save(pessoa);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getPessoaID()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
	}
	
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA')")
	public ResponseEntity<Pessoas> buscaPorId(@PathVariable Long codigo) {
		
		Optional<Pessoas> pessoaSalva = pessoaRepository.findById(codigo);
		
		return pessoaSalva.isPresent() ? ResponseEntity.ok(pessoaSalva.get()) : ResponseEntity.notFound().build();
	}
	
	@PutMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_EDITAR_PESSOA')")
	public ResponseEntity<Pessoas> atualizarPessoa(@PathVariable Long codigo, @Valid @RequestBody Pessoas pessoa) {
		
		Pessoas pessoaSalva = pessoaService.atualizarPessoa(codigo, pessoa);
		
		return ResponseEntity.ok(pessoaSalva);
	}
	
	@DeleteMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerPessoa(@PathVariable Long codigo) {
		
		pessoaRepository.deleteById(codigo);
	}
	
	/*********************************************************************************************************************************************************/
	
	@GetMapping("/busca-por-nome-pessoa")
	public List<Pessoas> buscaPorNome(@RequestParam("nome") String nomePessoa) {
		
		return pessoaRepository.buscaPorNome(nomePessoa);
	}

}

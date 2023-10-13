package com.carrafasoft.bsuldo.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import com.carrafasoft.bsuldo.api.model.Categorias;
import com.carrafasoft.bsuldo.api.repository.CategoriaRepository;
import com.carrafasoft.bsuldo.api.service.CategoriaService;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoriaResource.class);
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private CategoriaService categoriaService;
	
	
	@GetMapping
	public List<Categorias> listarTodos() {
		//LOGGER.info("******************** Listou categorias ************************");
		
		return categoriaRepository.findAll();
	}
	
	@PostMapping
	public ResponseEntity<Categorias> cadastrarCategoria(@Valid @RequestBody Categorias categoria, HttpServletResponse response) {
		
		Categorias categoriaSalva = categoriaRepository.save(categoria);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCategoriaId()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Categorias> buscaPorId(@PathVariable Long codigo) {
		
		Optional<Categorias> categoriaSalva = categoriaRepository.findById(codigo);
		
		return categoriaSalva.isPresent() ? ResponseEntity.ok(categoriaSalva.get()) : ResponseEntity.notFound().build();
	}
	
	@PutMapping("/{codigo}")
	public ResponseEntity<Categorias> atualizaCategoria(@PathVariable Long codigo, @Valid @RequestBody Categorias categoria) {
		
		Categorias categoriaSalva = categoriaService.atualizaCategoria(codigo, categoria);
		
		return ResponseEntity.ok(categoriaSalva);
	}
	
	@DeleteMapping("/{codigo}")
	public void removerCategoria(@PathVariable Long codigo) {
		
		categoriaRepository.deleteById(codigo);
	}
	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizaStatusAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		
		categoriaService.atualizarStatusAtivo(codigo, ativo);
	}
	
	/*******************************************************************************************************************************************************/
	
	@GetMapping("/busca-por-nome-categoria")
	public List<Categorias> buscaPorNomeCategoria(@RequestParam("nomeCategoria") String nomeCategoria) {
		
		return categoriaRepository.buscaPorNomeCategoria(nomeCategoria.trim());
	}
	
	@GetMapping("/busca-categorias-ativas")
	public List<Categorias> buscaCategoriasAtivas() {
		
		return categoriaRepository.buscaCategoriasAtivas();
	}

}

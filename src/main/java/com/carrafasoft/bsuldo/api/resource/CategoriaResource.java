package com.carrafasoft.bsuldo.api.resource;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.exception.entidadeException.CategoriaNaoEncontradaException;
import com.carrafasoft.bsuldo.api.model.Categorias;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.repository.CategoriaRepository;
import com.carrafasoft.bsuldo.api.service.CategoriaService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private PessoaService pessoaService;
	
	
	@GetMapping
	public List<Categorias> listarTodos(@RequestParam("tokenId") String tokenId) {
		
		return categoriaRepository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId));
	}
	
	@PostMapping
	public ResponseEntity<Categorias> cadastrarCategoria(@Valid @RequestBody Categorias categoria, HttpServletResponse response,
														 @RequestParam("tokenId") String tokenId) {

		try {
			Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
			categoria.setPessoa(pessoaSalva);

			Categorias categoriaSalva = categoriaRepository.save(categoria);
			publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCategoriaId()));

			return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}
	
	@GetMapping("/{codigo}")
	public Categorias buscaPorId(@PathVariable Long codigo, @RequestParam("tokenId") String tokenId) {

		Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);
		return categoriaRepository.findByIdAndPessoaId(codigo, pessoaId)
				.orElseThrow(() -> new CategoriaNaoEncontradaException(codigo));

	}
	
	@PutMapping("/{codigo}")
	public ResponseEntity<Categorias> atualizaCategoria(@PathVariable Long codigo, @Valid @RequestBody Categorias categoria,
														@RequestParam("tokenId") String tokenId) {

		Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
		categoria.setPessoa(pessoaSalva);

		Categorias categoriaSalva = categoriaService.atualizaCategoria(codigo, categoria);
		
		return ResponseEntity.ok(categoriaSalva);
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
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
	public List<Categorias> buscaPorNomeCategoria(@RequestParam("nomeCategoria") String nomeCategoria,
												  @RequestParam("tokenId") String tokenId) {
		
		return categoriaRepository.buscaPorNomeCategoria(nomeCategoria.trim(), pessoaService.recuperaIdPessoaByToken(tokenId));
	}
	
	@GetMapping("/busca-categorias-ativas")
	public List<Categorias> buscaCategoriasAtivas(@RequestParam("tokenId") String tokenId) {
		
		return categoriaRepository.buscaCategoriasAtivas(pessoaService.recuperaIdPessoaByToken(tokenId));
	}

}

package com.carrafasoft.bsuldo.api.controller;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.mapper.CategoriaMapper;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.CategoriaInputRepresentation;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.CategoriaRequestRepesentation;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.CategoriaResponseRepresentation;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.CategoriaNaoEncontradaException;
import com.carrafasoft.bsuldo.api.model.Categorias;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.repository.CategoriaRepository;
import com.carrafasoft.bsuldo.api.service.CategoriaService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

	@Autowired
	private CategoriaMapper categoriaMapper;



	@GetMapping
	public List<CategoriaResponseRepresentation> listarTodos(@RequestParam("tokenId") String tokenId) {

		return categoriaMapper.categoriaResponseRepresentationMapperList(categoriaRepository.
				findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId)));
	}


	@Transactional
	@PostMapping
	public ResponseEntity<CategoriaResponseRepresentation> cadastrarCategoria(@Valid @RequestBody CategoriaInputRepresentation categoriainput,
																			  HttpServletResponse response,
														 @RequestParam("tokenId") String tokenId) {

		try {
			Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));

			Categorias categoria = categoriaMapper.categoriaInputRepresentationMapper(categoriainput);
			categoria.setPessoa(pessoaSalva);

			Categorias categoriaSalva = categoriaRepository.save(categoria);
			publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCategoriaId()));

			return ResponseEntity.status(HttpStatus.CREATED).body(categoriaMapper.categoriaResponseRepresentationMapper(categoriaSalva));
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}


	@GetMapping("/{codigo}")
	public CategoriaResponseRepresentation buscaPorId(@PathVariable String codigo, @RequestParam("tokenId") String tokenId) {

		Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);
		return categoriaMapper.categoriaResponseRepresentationMapper(categoriaService.verificaCategoriaExistente(codigo, tokenId));

	}


	@PutMapping("/{codigo}")
	public ResponseEntity<CategoriaResponseRepresentation> atualizaCategoria(@PathVariable String codigo,
																			 @Valid @RequestBody CategoriaRequestRepesentation request,
																			 @RequestParam("tokenId") String tokenId) {

		try {
			Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));

			Categorias categoria = categoriaMapper.categoriaRequestRepesentationMapper(request);

			categoria.setPessoa(pessoaSalva);

			CategoriaResponseRepresentation categoriaSalva = categoriaMapper.categoriaResponseRepresentationMapper(
					categoriaService.atualizaCategoria(codigo, categoria, tokenId));

			return ResponseEntity.ok(categoriaSalva);
		} catch (CategoriaNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerCategoria(@PathVariable String codigo) {

		categoriaService.remover(codigo);
	}
	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizaStatusAtivo(@PathVariable String codigo, @RequestBody Boolean ativo) {
		
		categoriaService.atualizarStatusAtivo(codigo, ativo);
	}
	
	/*******************************************************************************************************************************************************/
	
	@GetMapping("/busca-por-nome-categoria")
	public List<CategoriaResponseRepresentation> buscaPorNomeCategoria(@RequestParam("nomeCategoria") String nomeCategoria,
												  @RequestParam("tokenId") String tokenId) {
		
		return categoriaMapper.categoriaResponseRepresentationMapperList(
				categoriaRepository.buscaPorNomeCategoria(nomeCategoria.trim(), pessoaService.recuperaIdPessoaByToken(tokenId))
		);
	}
	
	@GetMapping("/busca-categorias-ativas")
	public List<CategoriaResponseRepresentation> buscaCategoriasAtivas(@RequestParam("tokenId") String tokenId) {
		
		return categoriaMapper.categoriaResponseRepresentationMapperList(
				categoriaRepository.buscaCategoriasAtivas(pessoaService.recuperaIdPessoaByToken(tokenId))
		);
	}

}

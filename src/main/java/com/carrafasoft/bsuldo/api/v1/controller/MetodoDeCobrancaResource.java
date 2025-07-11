package com.carrafasoft.bsuldo.api.v1.controller;

import com.carrafasoft.bsuldo.api.v1.mapper.MetodoCobrancaMapper;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.MetodoCobrancaInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.MetodoCobrancaRequestInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.MetodoCobrancaResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.repository.MetodoDeCobrancaRepository;
import com.carrafasoft.bsuldo.api.v1.service.MetodoCobracaService;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/metodo-de-cobranca")
public class MetodoDeCobrancaResource {
	
	@Autowired
	private MetodoDeCobrancaRepository metodoCobrancaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private MetodoCobracaService metodoCobService;

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private MetodoCobrancaMapper metodoCobrancaMapper;
	
	@GetMapping
	public List<MetodoCobrancaResponseRepresentation> listarTodos(@RequestParam("tokenId") String tokenId) {
		
		return metodoCobrancaMapper.toListMetodoCobrancaResponseRepresentationMapper(
				metodoCobrancaRepository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId))
		);
	}


	@PostMapping
	public ResponseEntity<MetodoCobrancaResponseRepresentation> cadastrarMetodoCobranca(@Valid @RequestBody MetodoCobrancaInputRepresentation metodoCobrancaInput,
																						HttpServletResponse response,
																	@RequestParam("tokenId") String tokenId) {

		return ResponseEntity.status(HttpStatus.CREATED).body(
				metodoCobService.cadastrarMetodoCobranca(metodoCobrancaInput, tokenId, response)
		);
	}

	@GetMapping("/{codigo}")
	public MetodoCobrancaResponseRepresentation buscaPorId(@PathVariable String codigo,
																		   @RequestParam("tokenId") String tokenId) {

		return metodoCobrancaMapper.toMetodoCobrancaResponseRepresentationMapper(
				metodoCobService.findMetodoCobPorCodigoAndTokenId(codigo, tokenId));
	}
	
	@PutMapping("/{codigo}")
	public ResponseEntity<MetodoCobrancaResponseRepresentation> atualizarMetodoCobranca(@PathVariable String codigo,
																	@Valid @RequestBody MetodoCobrancaRequestInputRepresentation metodoCob,
																	@RequestParam("tokenId") String tokenId) {

		MetodoCobrancaResponseRepresentation metodoSalvo = metodoCobService.atualizarMetodoCob(codigo, metodoCob, tokenId);
		return ResponseEntity.ok(metodoSalvo);
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerMetodoCobranca(@PathVariable String codigo) {
		
		metodoCobService.remover(codigo);
	}
	
	
	@PutMapping("{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizaStatusAtivo(@PathVariable String codigo, @RequestBody Boolean ativo) {
		
		metodoCobService.atualizarStatus(codigo, ativo);
	}
	
	/*******************************************************************************************************************************************************************/
	
	@GetMapping("/busca-por-nome-metodo-cobranca")
	public List<MetodoCobrancaResponseRepresentation> buscaPorNomeMetodoCobranca(@RequestParam("nomeMetodoCobranca") String nomeMetodoCobranca) {
		
		return metodoCobrancaMapper.toListMetodoCobrancaResponseRepresentationMapper(
				metodoCobrancaRepository.buscaPorNomeMetodoCobranca(nomeMetodoCobranca.trim())
		);
	}
	
	
	@GetMapping("/busca-por-nome-metodo-cobranca-ativo")
	public List<MetodoCobrancaResponseRepresentation> buscaPorMetodoDeCobrancaAtivo(@RequestParam("tokenId") String tokenId) {
		
		return metodoCobrancaMapper.toListMetodoCobrancaResponseRepresentationMapper(
				metodoCobrancaRepository.buscaPorNomeMetodoCobrancaAtivo(pessoaService.recuperaIdPessoaByToken(tokenId))
		);
	}

}

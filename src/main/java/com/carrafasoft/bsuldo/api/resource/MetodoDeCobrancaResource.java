package com.carrafasoft.bsuldo.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.service.PessoaService;
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
import org.springframework.web.bind.annotation.RequestParam;
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

	@Autowired
	private PessoaService pessoaService;
	
	@GetMapping
	public List<MetodoDeCobranca> listarTodos(@RequestParam("tokenId") String tokenId) {
		
		return metodoCobrancaRepository.findAllByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId));
	}
	
	@PostMapping
	public ResponseEntity<MetodoDeCobranca> cadastrarMetodoCobranca(@Valid @RequestBody MetodoDeCobranca metodoCobranca, HttpServletResponse response,
																	@RequestParam("tokenId") String tokenId) {

		Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
		metodoCobranca.setPessoa(pessoaSalva);
		
		MetodoDeCobranca metodoCobSalvo = metodoCobrancaRepository.save(metodoCobranca);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, metodoCobSalvo.getMetodoCobrancaId()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(metodoCobSalvo);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<MetodoDeCobranca> buscaPorId(@PathVariable Long codigo, @RequestParam("tokenId") String tokenId) {
		
		Optional<MetodoDeCobranca> metodoCobSalvo = metodoCobrancaRepository.findByIdAndPessoaId(codigo,
				pessoaService.recuperaIdPessoaByToken(tokenId));
		
		return metodoCobSalvo.isPresent() ? ResponseEntity.ok(metodoCobSalvo.get()) : ResponseEntity.notFound().build();
	}
	
	@PutMapping("/{codigo}")
	public ResponseEntity<MetodoDeCobranca> atualizarMetodoCobranca(@PathVariable Long codigo, @Valid @RequestBody MetodoDeCobranca metodoCob,
																	@RequestParam("tokenId") String tokenId) {

		Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
		metodoCob.setPessoa(pessoaSalva);

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
	
	/*******************************************************************************************************************************************************************/
	
	@GetMapping("/busca-por-nome-metodo-cobranca")
	public List<MetodoDeCobranca> buscaPorNomeMetodoCobranca(@RequestParam("nomeMetodoCobranca") String nomeMetodoCobranca) {
		
		return metodoCobrancaRepository.buscaPorNomeMetodoCobranca(nomeMetodoCobranca.trim());
	}
	
	
	@GetMapping("/busca-por-nome-metodo-cobranca-ativo")
	public List<MetodoDeCobranca> buscaPorMetodoDeCobrancaAtivo(@RequestParam("tokenId") String tokenId) {
		
		return metodoCobrancaRepository.buscaPorNomeMetodoCobrancaAtivo(pessoaService.recuperaIdPessoaByToken(tokenId));
	}

}

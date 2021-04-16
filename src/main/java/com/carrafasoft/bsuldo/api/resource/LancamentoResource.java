package com.carrafasoft.bsuldo.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrafasoft.bsuldo.api.model.Lancamentos;
import com.carrafasoft.bsuldo.api.repository.LancamentoRepository;
import com.carrafasoft.bsuldo.api.service.LancamentoService;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	
	@GetMapping
	public List<Lancamentos> listarTodos() {
		
		return lancamentoRepository.findAll();
	}
	
	
	@PostMapping
	public ResponseEntity<?> cadastrarLancamento(@Valid @RequestBody Lancamentos lancamento, HttpServletResponse response) {
		
		ResponseEntity<?> retornoResponse = null;
		Boolean existParcelamento = lancamento.getParcelado();
		
		if(!existParcelamento) { //Se não for parcelado entrar aqui
			
			retornoResponse = lancamentoService.cadastrarLancamentoSemParcelamento(lancamento, response);
		
		} else { // Se for parcelado entrar aqui
			
			retornoResponse = lancamentoService.cadastrarLancamentoComParcelamento(lancamento, response);
		}
		
		return retornoResponse;
	}
	
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Lancamentos> buscaPorId(@PathVariable Long codigo) {
		
		Optional<Lancamentos> lancamentoSalvo = lancamentoRepository.findById(codigo);
		
		return lancamentoSalvo.isPresent() ? ResponseEntity.ok(lancamentoSalvo.get()) : ResponseEntity.notFound().build();
	}
	
	@PutMapping("/{codigo}")
	public ResponseEntity<Lancamentos> atualizaLancamentoIdividual(@PathVariable Long codigo, @Valid @RequestBody Lancamentos lancamento) {
		
		Lancamentos lancamentoSalvo = lancamentoService.atualizaLancamentoIdividual(codigo, lancamento);
		
		return ResponseEntity.ok(lancamentoSalvo);
	}

}

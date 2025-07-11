package com.carrafasoft.bsuldo.api.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.model.CoresConfig;
import com.carrafasoft.bsuldo.api.v1.service.CoresConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carrafasoft.bsuldo.api.v1.repository.CoresConfigRepository;

@RestController
@RequestMapping("/cores")
public class CoresConfigResource {
	
	@Autowired
	private CoresConfigRepository configRepository;

	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private CoresConfigService coresConfigService;
	
	@GetMapping
	public List<CoresConfig> findAll(@RequestParam("tokenId") String tokenId) {
		
		return configRepository.listarTodosByPessoaId(pessoaService.recuperaIdPessoaByToken(tokenId));
	}
	
	@GetMapping("/cores-graficos/{tokenId}")
	public HashMap<String, Object> listarCoresParaGraficos(@RequestParam("tokenId") String tokenId) {
				
		return coresConfigService.listaCoresParaGraficos(tokenId);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<CoresConfig> buscaCorPorId(@PathVariable Long codigo) {
		
		Optional<CoresConfig> corSalva = configRepository.findById(codigo);
		return corSalva.isPresent() ? ResponseEntity.ok(corSalva.get()) : ResponseEntity.notFound().build();
	}
	
	@PostMapping
	public ResponseEntity<CoresConfig> cadastrarCores(@Valid @RequestBody CoresConfig coresConfig, HttpServletResponse response) {
		
		return coresConfigService.salvarCores(coresConfig, response);
	}

	@PutMapping("/atualiza-cor-padrao")
	public void atualizaCoresPadrao(@RequestParam("pessoaId") Long pessoaId, @Valid @RequestBody Boolean usarCorPadrao) {

		coresConfigService.atualizaUsarCoresPadrao(pessoaId, usarCorPadrao);
	}
	
	

}

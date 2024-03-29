package com.carrafasoft.bsuldo.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrafasoft.bsuldo.api.model.CoresConfig;
import com.carrafasoft.bsuldo.api.repository.CoresConfigRepository;
import com.carrafasoft.bsuldo.api.service.CoresConfigService;

@RestController
@RequestMapping("/cores")
public class CoresConfigResource {
	
	@Autowired
	private CoresConfigRepository configRepository;
	
	@Autowired
	private CoresConfigService coresConfigService;
	
	@GetMapping
	public List<CoresConfig> findAll() {
		
		return configRepository.findAll();
	}
	
	@GetMapping("/cores-graficos/{pessoaId}")
	public HashMap<String, Object> listarCoresParaGraficos(@PathVariable Long pessoaId) {
				
		return coresConfigService.listaCoresParaGraficos(pessoaId);
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
	
	

}

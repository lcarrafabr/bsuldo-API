package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.utils.FuncoesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.repository.PessoaRepository;

@Service
@Slf4j
public class PessoaService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	public Pessoas atualizarPessoa(Long codigo, Pessoas pessoa) {
		
		Pessoas pessoaSalva = pessoaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
		BeanUtils.copyProperties(pessoa, pessoaSalva, "pessoaID");
		
		return pessoaRepository.save(pessoaSalva);
	}

	public Pessoas buscaPessoaPorId(Long codigo) {
		Pessoas pessoaSalva = new Pessoas();
		try {
			log.info("...: Consultando pessoa pelo ID: " + codigo + ":...");
			pessoaSalva = pessoaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));

			return pessoaSalva;
		} catch (Exception e) {

			log.error("...: Falha ao encontrar pessoaId: " + codigo, e.getMessage());
		}

		return pessoaSalva;
	}

	public Long recuperaIdPessoaByToken(String tokenCriptografado) {

		return pessoaRepository.buscaIdPessoaByToken(FuncoesUtils.decryptFromBase64(tokenCriptografado));
	}

}

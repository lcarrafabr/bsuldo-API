package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.utils.FuncoesUtils;
import com.carrafasoft.bsuldo.api.v1.repository.PessoaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.v1.model.Pessoas;

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
			pessoaSalva = pessoaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));

			return pessoaSalva;
		} catch (Exception e) {

			log.error("...: Falha ao encontrar pessoaId: {} {}",codigo, e.getMessage());
		}

		return pessoaSalva;
	}

	public Long recuperaIdPessoaByToken(String tokenCriptografado) {

		return pessoaRepository.buscaIdPessoaByToken(FuncoesUtils.decryptFromBase64(tokenCriptografado));
	}

}

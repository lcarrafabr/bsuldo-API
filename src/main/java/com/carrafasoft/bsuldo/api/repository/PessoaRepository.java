package com.carrafasoft.bsuldo.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Pessoas;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoas, Long>{
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from pessoas "
					+ "where nome_pessoa like %:nomePessoa% ")
	public List<Pessoas> buscaPorNome(String nomePessoa);

}

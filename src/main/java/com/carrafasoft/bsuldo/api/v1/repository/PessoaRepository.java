package com.carrafasoft.bsuldo.api.v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.v1.model.Pessoas;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoas, Long>{
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from pessoas "
					+ "where nome_pessoa like %:nomePessoa% ")
	public List<Pessoas> buscaPorNome(String nomePessoa);
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from pessoas "
					+ "where pessoa_id not in (select pessoa_id from usuarios) ")
	public List<Pessoas> buscaPessoaSemUsuario();

	@Query(nativeQuery = true,
	value = "select pessoa_id from pessoas " +
			"where pessoa_id_token = :tokenDescript ")
    Long buscaIdPessoaByToken(@Param("tokenDescript") String tokenDescript);
}

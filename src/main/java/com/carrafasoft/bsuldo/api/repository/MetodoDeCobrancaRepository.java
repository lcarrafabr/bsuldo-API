package com.carrafasoft.bsuldo.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.MetodoDeCobranca;

@Repository
public interface MetodoDeCobrancaRepository extends JpaRepository<MetodoDeCobranca, Long>{

	@Query(nativeQuery = true,
	value = "select * from metodo_de_cobranca " +
			"where pessoa_id = :pessoaId ")
	List<MetodoDeCobranca> findAllByPessoaId(Long pessoaId);

	@Query(nativeQuery = true,
	value = "select * from metodo_de_cobranca " +
			"where pessoa_id = :pessoaID " +
			"and metodo_de_cobranca_id = :metodoDeCobrancaID ")
	Optional<MetodoDeCobranca> findByIdAndPessoaId(Long metodoDeCobrancaID, Long pessoaID);
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from metodo_de_cobranca "
					+ "where nome_metodo_cobranca like %:nomeMetodoCobranca% ")
	public List<MetodoDeCobranca> buscaPorNomeMetodoCobranca(String nomeMetodoCobranca);
	
	
	@Query(nativeQuery = true,
			value = "select * from metodo_de_cobranca "
					+ "where pessoa_id = :pessoaId "
					+ "and status = 1 ")
	public List<MetodoDeCobranca> buscaPorNomeMetodoCobrancaAtivo(Long pessoaId);

}

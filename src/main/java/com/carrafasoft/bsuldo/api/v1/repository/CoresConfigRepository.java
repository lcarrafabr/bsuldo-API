package com.carrafasoft.bsuldo.api.v1.repository;

import java.util.List;

import com.carrafasoft.bsuldo.api.v1.model.CoresConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CoresConfigRepository extends JpaRepository<CoresConfig, Long>{

	@Query(nativeQuery = true,
	value = "select *  " +
			"from cores_config where pessoa_id = :pessoaId ")
	List<CoresConfig> listarTodosByPessoaId(Long pessoaId);
	
	@Query(nativeQuery = true,
			value = "select cor_principal from cores_config where pessoa_id = :pessoaId ")
	public List<String> coresGraficosPrincipais(Long pessoaId); 
	
	@Query(nativeQuery = true,
			value = "select cor_secundaria from cores_config where pessoa_id = :pessoaId ")
	public List<String> coresGraficosSecundarias(Long pessoaId);

	@Transactional
	@Modifying
	@Query(nativeQuery = true,
	value = "update cores_config " +
			"set usar_cores_padrao = :usarCoresPadrao " +
			"where pessoa_id = :pessoaId ")
	void atualizaCoresPadrao(Long pessoaId, Boolean usarCoresPadrao);

}

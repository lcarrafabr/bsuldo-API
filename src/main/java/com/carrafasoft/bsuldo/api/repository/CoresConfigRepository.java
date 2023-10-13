package com.carrafasoft.bsuldo.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.CoresConfig;

@Repository
public interface CoresConfigRepository extends JpaRepository<CoresConfig, Long>{
	
	@Query(nativeQuery = true,
			value = "select cor_principal from cores_config where pessoa_id = :pessoaId ")
	public List<String> coresGraficosPrincipais(Long pessoaId); 
	
	@Query(nativeQuery = true,
			value = "select cor_secundaria from cores_config where pessoa_id = :pessoaId ")
	public List<String> coresGraficosSecundarias(Long pessoaId); 

}

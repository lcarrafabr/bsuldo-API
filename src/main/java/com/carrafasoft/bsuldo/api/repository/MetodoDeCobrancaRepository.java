package com.carrafasoft.bsuldo.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.MetodoDeCobranca;

@Repository
public interface MetodoDeCobrancaRepository extends JpaRepository<MetodoDeCobranca, Long>{
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from metodo_de_cobranca "
					+ "where nome_metodo_cobranca like %:nomeMetodoCobranca% ")
	public List<MetodoDeCobranca> buscaPorNomeMetodoCobranca(String nomeMetodoCobranca);
	
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from metodo_de_cobranca "
					+ "where status = 1 ")
	public List<MetodoDeCobranca> buscaPorNomeMetodoCobrancaAtivo();

}

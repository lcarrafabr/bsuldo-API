package com.carrafasoft.bsuldo.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Lancamentos;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamentos, Long>{
	
	@Query(nativeQuery = true,
			value = "select * from lancamentos where chave_pesquisa = :chave ")
	public List<Lancamentos> buscarPorchave(String chave);
	
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from lancamentos "
					+ "where data_vencimento < CURRENT_DATE "
					+ "and situacao = 'PENDENTE' ")
	public List<Lancamentos> buscaLancamentosVencidos();
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true,
			value = "update lancamentos "
					+ "set situacao = 'VENCIDO' "
					+ "where data_vencimento < CURRENT_DATE "
					+ "and situacao = 'PENDENTE' ")
	public void atualizaLancamentosVencidos();

}

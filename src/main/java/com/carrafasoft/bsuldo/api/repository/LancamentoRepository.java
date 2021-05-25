package com.carrafasoft.bsuldo.api.repository;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Lancamentos;
import com.carrafasoft.bsuldo.api.model.reports.LancamentosReportsTotaisPorSemana;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamentos, Long>{
	
	@Query(nativeQuery = true,
			value = "select * from lancamentos order by lancamento_id desc ")
	public List<Lancamentos> findByAllDesc();
	
	@Query(nativeQuery = true,
			value = "select * from lancamentos where chave_pesquisa = :chave ")
	public List<Lancamentos> buscarPorchave(String chave);
	
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from lancamentos "
					+ "where data_vencimento < CURRENT_DATE "
					+ "and situacao = 'PENDENTE' ")
	public List<Lancamentos> buscaLancamentosVencidos();
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from lancamentos "
					+ "where descricao like %:descricao% ")
	public List<Lancamentos> buscaPorDescricao(String descricao);
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from lancamentos "
					+ "where data_vencimento between :vencimentoInicio and :vencimentoFim ")
	public List<Lancamentos> buscaPorDataVencimentoDataIniDataFim(LocalDate vencimentoInicio, LocalDate vencimentoFim);
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from lancamentos "
					+ "where data_vencimento <= :dataVencimento ")
	public List<Lancamentos> buscaPorDataVencimentoMenorQueDataInformada(LocalDate dataVencimento);
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from lancamentos "
					+ "where situacao = :situacao ")
	public List<Lancamentos> buscaBySituacao(String situacao);
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from lancamentos "
					+ "where chave_pesquisa = :chavePesquisa ")
	public List<Lancamentos> buscaByChavePesquisa(String chavePesquisa);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true,
			value = "update lancamentos "
					+ "set situacao = 'VENCIDO' "
					+ "where data_vencimento < CURRENT_DATE "
					+ "and situacao = 'PENDENTE' ")
	public void atualizaLancamentosVencidos();
	
	
	@Query(nativeQuery = true,
			value = "select sum(valor) as JAN, "
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '2' "
					+ "And year(data_vencimento) = '2021') as FEV, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '3' "
					+ "And year(data_vencimento) = '2021') as MAR, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '4' "
					+ "And year(data_vencimento) = '2021') as ABR, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '5' "
					+ "And year(data_vencimento) = '2021') as MAI, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '6' "
					+ "And year(data_vencimento) = '2021') as JUN, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '7' "
					+ "And year(data_vencimento) = '2021') as JUL, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '8' "
					+ "And year(data_vencimento) = '2021') as AGO, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '9' "
					+ "And year(data_vencimento) = '2021') as \"SET\", "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '10' "
					+ "And year(data_vencimento) = '2021') as \"OUT\", "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '11' "
					+ "And year(data_vencimento) = '2021') as NOV, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '12' "
					+ "And year(data_vencimento) = '2021') as DEZ, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "WHERE year(data_vencimento) = '2021') as TOTAL "
					+ ""
					+ "from lancamentos "
					+ "where month(data_vencimento) = '1' "
					+ "And year(data_vencimento) = '2021' ")
	public List<?> listarTotaisPorAno();

}

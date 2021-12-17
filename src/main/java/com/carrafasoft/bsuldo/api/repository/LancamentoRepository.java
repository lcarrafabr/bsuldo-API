package com.carrafasoft.bsuldo.api.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Lancamentos;
import com.carrafasoft.bsuldo.api.model.reports.LancamentosReportsTotaisPorSemana;
import com.carrafasoft.bsuldo.api.model.reports.TotalPorCategoriaMes;

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
			value = "select sum(valor) as valor_a_pagar "
					+ "from lancamentos "
					+ "where data_vencimento between :dataIni and :dataFim "
					+ "and situacao in ('PENDENTE', 'VENCIDO') ")
	public BigDecimal valorApagarNoMes(LocalDate dataIni, LocalDate dataFim);
	
	@Query(nativeQuery = true,
			value = "select sum(valor) as total_pago "
					+ "from lancamentos "
					+ "where data_vencimento between :dataIni and :dataFim "
					+ "and situacao = 'PAGO' ")
	public BigDecimal valorPagoNoMes(LocalDate dataIni, LocalDate dataFim);
	
	
	@Query(nativeQuery = true,
			value = "select sum(valor) as total_pago "
					+ "from lancamentos "
					+ "where data_vencimento between :dataIni and :dataFim "
					+ "and situacao = 'VENCIDO' ")
	public BigDecimal valorVencidoNoMes(LocalDate dataIni, LocalDate dataFim);
	
	@Query(nativeQuery = true,
			value = "select sum(valor) as total_devedor "
					+ "from lancamentos "
					+ "where year(data_vencimento) = :ano ")
	public BigDecimal totalDevedorPorAno(int ano);
	
	@Query(nativeQuery = true,
			value = "select if(total_pago is not null, total_pago, 0) as perc_pago "
					+ "from ( "
					+ "select (total_devedor / ( "
					+ "select sum(l2.valor) as total_pago "
					+ "from lancamentos l2 "
					+ "where situacao in ('PENDENTE', 'VENCIDO', 'PAGO') "
					+ "and data_vencimento between :dataIni and :dataFim "
					+ ")) * 100 as total_pago "
					+ "from ( "
					+ "select sum(l.valor) as total_devedor "
					+ "from lancamentos l "
					+ "where situacao in ('PAGO') "
					+ "and data_vencimento between :dataIni and :dataFim ) as total_devedor ) as total ")
	public BigDecimal percentualPagoNoMes(LocalDate dataIni, LocalDate dataFim);
	
	@Query(nativeQuery = true,
			value = "select c.nome_categoria, sum(l.valor) totais "
					+ "from lancamentos l "
					+ "inner join categorias c on c.categoria_id = l.categoria_id "
					+ "where l.data_vencimento between :dataIni and :dataFim "
					+ "group by c.nome_categoria ")
	public List<String> totalPorCategoriaMes(LocalDate dataIni, LocalDate dataFim);
	
	
	@Query(nativeQuery = true,
			value = "select m.nome_metodo_cobranca, sum(l.valor) as totais "
					+ "from lancamentos l "
					+ "inner join metodo_de_cobranca m on m.metodo_de_cobranca_id = l.metodo_de_cobranca_id "
					+ "where l.data_vencimento between :dataIni and :dataFim "
					+ "group by m.nome_metodo_cobranca ")
	public List<String> totalPorMetodoCobrancaMes(LocalDate dataIni, LocalDate dataFim);
	
	@Query(nativeQuery = true,
			value = "select day(data_vencimento) as dia, sum(valor) as total "
					+ "from lancamentos "
					+ "where data_vencimento between :dataIni and :dataFim "
					+ "and situacao <> 'CANCELADO' "
					+ "group by data_vencimento "
					+ "order by dia ")
	public List<String> totalLancamentosPorDiaMes(LocalDate dataIni, LocalDate dataFim);
	
	
	@Query(nativeQuery = true,
			value = "select sum(valor) as JAN, "
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '2' "
					+ "And year(data_vencimento) = :ano) as FEV, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '3' "
					+ "And year(data_vencimento) = :ano) as MAR, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '4' "
					+ "And year(data_vencimento) = :ano) as ABR, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '5' "
					+ "And year(data_vencimento) = :ano) as MAI, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '6' "
					+ "And year(data_vencimento) = :ano) as JUN, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '7' "
					+ "And year(data_vencimento) = :ano) as JUL, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '8' "
					+ "And year(data_vencimento) = :ano) as AGO, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '9' "
					+ "And year(data_vencimento) = :ano) as \"SET\", "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '10' "
					+ "And year(data_vencimento) = :ano) as \"OUT\", "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '11' "
					+ "And year(data_vencimento) = :ano) as NOV, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "where month(data_vencimento) = '12' "
					+ "And year(data_vencimento) = :ano) as DEZ, "
					+ ""
					+ "(select sum(valor) "
					+ "from lancamentos "
					+ "WHERE year(data_vencimento) = :ano) as TOTAL "
					+ ""
					+ "from lancamentos "
					+ "where month(data_vencimento) = '1' "
					+ "And year(data_vencimento) = :ano ")
	public List<String> listarTotaisPorAno(String ano);
	
	@Query(nativeQuery = true,
			value = "select  m.nome_metodo_cobranca, l.descricao, l.valor, l.situacao, concat(l.numero_parcela,'/', l.quantidade_de_parcelas) as parcela "
					+ "from lancamentos l "
					+ "inner join metodo_de_cobranca m on m.metodo_de_cobranca_id = l.metodo_de_cobranca_id "
					+ "where month(l.data_vencimento) = :mes "
					+ "and year(l.data_vencimento) = :ano ")
	public List<String> lancamentosPorMetodoCobranca(int mes, int ano);
	
	@Query(nativeQuery = true,
			value = "select * from lancamentos where situacao = 'VENCIDO' ")
	public List<Lancamentos> getLancamentosVencidos();
	
	@Query(nativeQuery = true,
			value = "select * from lancamentos "
					+ "where data_vencimento between curdate() and curdate() + 7 "
					+ "and situacao in ('PENDENTE', 'VENCIDO') ")
	public List<Lancamentos> getLancamentosProximosSeteDias();

}

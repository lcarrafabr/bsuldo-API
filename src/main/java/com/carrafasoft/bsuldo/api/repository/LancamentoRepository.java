package com.carrafasoft.bsuldo.api.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Lancamentos;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamentos, Long>{

	Optional<Lancamentos> findByCodigoLancamento(@Param("codigoLancamento") String codigoLancamento);

	void deleteByCodigoLancamento(@Param("codigoLancamento") String codigoLancamento);
	
	@Query(nativeQuery = true,
			value = "select * from lancamentos " +
					"where pessoa_id = :pessoaId " +
					"order by lancamento_id desc ")
	public List<Lancamentos> findByAllDesc(Long pessoaId);

	@Query(nativeQuery = true,
	value = "select * from lancamentos " +
			"where pessoa_id = :pessoaId " +
			"and codigo_lancamento = :codigoLancamento ")
	Optional<Lancamentos> findByCodigoLancamentoAndPessoaId(@Param("codigoLancamento") String codigoLancamento,
															@Param("pessoaId") Long pessoaId);


	@Query(nativeQuery = true,
	value = "SELECT * FROM lancamentos l " +
			"WHERE (:descricao IS NULL OR UPPER(l.descricao) LIKE UPPER(CONCAT('%', :descricao, '%'))) " +
			"AND (:dataVencimento IS NULL OR l.data_vencimento = :dataVencimento) " +
			"AND (:dataIni IS NULL OR :dataVencimentoFim IS NULL OR l.data_vencimento BETWEEN :dataIni AND :dataVencimentoFim) " +
			"AND (:metodoDeCobrancaId IS NULL OR l.metodo_de_cobranca_id = :metodoDeCobrancaId) " +
			"AND (:situacao IS NULL OR l.situacao = :situacao) " +
			"and (:chavePesquisa is null or l.chave_pesquisa = :chavePesquisa) " +
			"and (:tipoLancamento is null or l.tipo_lancamento = :tipoLancamento) " +
			"AND l.pessoa_id = :pessoaId " +
			"ORDER BY l.lancamento_id DESC ")
	public List<Lancamentos> findByAllDescNew(@Param("descricao") String descricao,
											  @Param("dataVencimento") LocalDate dataVencimento,
											  @Param("dataIni") LocalDate dataIni,
											  @Param("dataVencimentoFim") LocalDate dataVencimentoFim,
											  @Param("metodoDeCobrancaId") String metodoDeCobrancaId,
											  @Param("situacao") String situacao,
											  @Param("chavePesquisa") String chavePesquisa,
											  @Param("tipoLancamento") String tipoLancamento,
											  @Param("pessoaId") Long pessoaId);
	
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
					+ "and situacao = 'PENDENTE' " +
					"and tipo_lancamento = 'DESPESA' ")
	public void atualizaLancamentosVencidos();

	@Transactional
	@Modifying
	@Query(nativeQuery = true,
			value = "update lancamentos "
					+ "set situacao = 'ATRASADO' "
					+ "where data_vencimento < CURRENT_DATE "
					+ "and situacao = 'PENDENTE' " +
					"and tipo_lancamento = 'RECEITA' ")
	public void atualizaLancamentosRecebimentoVencidos();
	
	@Query(nativeQuery = true,
			value = "select COALESCE(sum(valor), 0) as valor_a_pagar "
					+ "from lancamentos "
					+ "where pessoa_id = :pessoaId " +
					"AND data_vencimento between :dataIni and :dataFim "
					+ "and situacao in ('PENDENTE', 'VENCIDO') " +
					"and tipo_lancamento = 'DESPESA' ")
	BigDecimal valorApagarNoMes(LocalDate dataIni, LocalDate dataFim, Long pessoaId);
	
	@Query(nativeQuery = true,
			value = "select COALESCE(sum(valor), 0) as total_pago "
					+ "from lancamentos "
					+ "where data_vencimento between :dataIni and :dataFim "
					+ "and situacao = 'PAGO' " +
					"and tipo_lancamento = 'DESPESA' " +
					"and pessoa_id = :pessoaId ")
	BigDecimal valorPagoNoMes(LocalDate dataIni, LocalDate dataFim, Long pessoaId);
	
	
	@Query(nativeQuery = true,
			value = "select COALESCE(sum(valor), 0) as total_pago "
					+ "from lancamentos "
					+ "where data_vencimento between :dataIni and :dataFim "
					+ "and situacao = 'VENCIDO' " +
					"and tipo_lancamento = 'DESPESA' " +
					"and pessoa_id = :pessoaId ")
	BigDecimal valorVencidoNoMes(LocalDate dataIni, LocalDate dataFim, Long pessoaId);
	
	@Query(nativeQuery = true,
			value = "select sum(valor) as total_devedor "
					+ "from lancamentos "
					+ "where pessoa_id = :pessoaId " +
					"and year(data_vencimento) = :ano " +
					"and tipo_lancamento = 'DESPESA' " +
					"and situacao in ('PENDENTE', 'VENCIDO') ")
	BigDecimal totalDevedorPorAno(int ano, Long pessoaId);
	
	@Query(nativeQuery = true,
			value = "select coalesce(sum(valor), 0) as valor " +
					"from lancamentos " +
					"where pessoa_id = :pessoaId " +
					"and tipo_lancamento = 'DESPESA' " +
					"and year(data_vencimento) = :ano ")
	BigDecimal totalPagoNoAno(int ano, Long pessoaId);
	
	@Query(nativeQuery = true,
			value = "select if(total_pago is not null, total_pago, 0) as perc_pago "
					+ "from ( "
					+ "select (total_devedor / ( "
					+ "select sum(l2.valor) as total_pago "
					+ "from lancamentos l2 "
					+ "where situacao in ('PENDENTE', 'VENCIDO', 'PAGO') "
					+ "and data_vencimento between :dataIni and :dataFim " +
					"and pessoa_id = :pessoaId " +
					"and tipo_lancamento = 'DESPESA' "
					+ ")) * 100 as total_pago "
					+ "from ( "
					+ "select sum(l.valor) as total_devedor "
					+ "from lancamentos l "
					+ "where situacao in ('PAGO') " +
					"and tipo_lancamento = 'DESPESA' " +
					"and pessoa_id = :pessoaId "
					+ "and data_vencimento between :dataIni and :dataFim ) as total_devedor ) as total ")
	BigDecimal percentualPagoNoMes(LocalDate dataIni, LocalDate dataFim, Long pessoaId);


	@Query(nativeQuery = true,
	value = "select COALESCE(sum(valor), 0) as valor_a_pagar " +
			"from lancamentos " +
			"where pessoa_id = :pessoaId " +
			"AND data_vencimento between :dataIni and :dataFim " +
			"and situacao in ('PENDENTE', 'ATRASADO') " +
			"and tipo_lancamento = 'RECEITA' ")
	BigDecimal valorAReceberNoMes(LocalDate dataIni, LocalDate dataFim, Long pessoaId);

	@Query(nativeQuery = true,
			value = "select COALESCE(sum(valor), 0) as total_recebido " +
					"from lancamentos " +
					"where pessoa_id = :pessoaId " +
					"AND data_vencimento between :dataIni and :dataFim " +
					"and situacao in ('RECEBIDO') " +
					"and tipo_lancamento = 'RECEITA' ")
	BigDecimal valorRecebidoNoMes(LocalDate dataIni, LocalDate dataFim, Long pessoaId);


	@Query(nativeQuery = true,
			value = "select COALESCE(sum(valor), 0) as total_pago "
					+ "from lancamentos "
					+ "where data_vencimento between :dataIni and :dataFim "
					+ "and situacao = 'ATRASADO' " +
					"and tipo_lancamento = 'RECEITA' " +
					"and pessoa_id = :pessoaId ")
	BigDecimal valorAtrasadoNoMes(LocalDate dataIni, LocalDate dataFim, Long pessoaId);


	@Query(nativeQuery = true,
			value = "select sum(valor) as total_devedor "
					+ "from lancamentos "
					+ "where pessoa_id = :pessoaId " +
					"and year(data_vencimento) = :ano " +
					"and tipo_lancamento = 'RECEITA' " +
					"and situacao in ('PENDENTE', 'ATRASADO') ")
	BigDecimal totalAReceberNoAno(int ano, Long pessoaId);

	@Query(nativeQuery = true,
			value = "select coalesce(sum(valor), 0) as valor " +
					"from lancamentos " +
					"where pessoa_id = :pessoaId " +
					"and tipo_lancamento = 'RECEITA' " +
					"and year(data_vencimento) = :ano ")
	BigDecimal receitatotalAno(int ano, Long pessoaId);

	@Query(nativeQuery = true,
			value = "select if(total_pago is not null, total_pago, 0) as perc_recebido "
					+ "from ( "
					+ "select (total_devedor / ( "
					+ "select sum(l2.valor) as total_pago "
					+ "from lancamentos l2 "
					+ "where situacao in ('PENDENTE', 'ATRASADO', 'RECEBIDO') "
					+ "and data_vencimento between :dataIni and :dataFim " +
					"and pessoa_id = :pessoaId " +
					"and tipo_lancamento = 'RECEITA' "
					+ ")) * 100 as total_pago "
					+ "from ( "
					+ "select sum(l.valor) as total_devedor "
					+ "from lancamentos l "
					+ "where situacao in ('RECEBIDO') " +
					"and tipo_lancamento = 'RECEITA' " +
					"and pessoa_id = :pessoaId "
					+ "and data_vencimento between :dataIni and :dataFim ) as total_devedor ) as total ")
	BigDecimal percentualRecebidoNoMes(LocalDate dataIni, LocalDate dataFim, Long pessoaId);
	
	@Query(nativeQuery = true,
			value = "select c.nome_categoria, sum(l.valor) totais "
					+ "from lancamentos l "
					+ "inner join categorias c on c.categoria_id = l.categoria_id "
					+ "where l.data_vencimento between :dataIni and :dataFim " +
					"and l.pessoa_id = :pessoaId " +
					"and l.tipo_lancamento = 'DESPESA' "
					+ "group by c.nome_categoria ")
	List<String> totalPorCategoriaMes(LocalDate dataIni, LocalDate dataFim, Long pessoaId);
	
	
	@Query(nativeQuery = true,
			value = "select m.nome_metodo_cobranca, sum(l.valor) as totais "
					+ "from lancamentos l "
					+ "inner join metodo_de_cobranca m on m.metodo_de_cobranca_id = l.metodo_de_cobranca_id "
					+ "where l.data_vencimento between :dataIni and :dataFim " +
					"and l.pessoa_id = :pessoaId " +
					"and l.tipo_lancamento = 'DESPESA' "
					+ "group by m.nome_metodo_cobranca ")
	List<String> totalPorMetodoCobrancaMes(LocalDate dataIni, LocalDate dataFim, Long pessoaId);
	
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
					+ "where l.pessoa_id = :pessoaId " +
					"and month(l.data_vencimento) = :mes "
					+ "and year(l.data_vencimento) = :ano ")
	public List<String> lancamentosPorMetodoCobranca(int mes, int ano, Long pessoaId);
	
	@Query(nativeQuery = true,
			value = "select * from lancamentos where situacao in('VENCIDO', 'ATRASADO') ")
	public List<Lancamentos> getLancamentosVencidos();
	
	@Query(nativeQuery = true,
			value = "select * from lancamentos "
					+ "where data_vencimento between curdate() and curdate() + 7 "
					+ "and situacao in ('PENDENTE', 'VENCIDO', 'ATRASADO') ")
	public List<Lancamentos> getLancamentosProximosSeteDias();
}

package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.enums.SituacaoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoLancamento;
import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.carrafasoft.bsuldo.api.v1.exception.entidadeException.EntidadeEmUsoException;
import com.carrafasoft.bsuldo.api.v1.mail.Mailer;
import com.carrafasoft.bsuldo.api.v1.mapper.LancamentoMapper;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.BancoResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.LancamentoInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.LancamentoResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.*;
import com.carrafasoft.bsuldo.api.v1.model.exceptionmodel.LancamentoNaoEncontradoException;
import com.carrafasoft.bsuldo.api.v1.model.reports.*;
import com.carrafasoft.bsuldo.api.v1.repository.BancoRepository;
import com.carrafasoft.bsuldo.api.v1.repository.LancamentoRepository;
import com.carrafasoft.bsuldo.api.v1.repository.UsuarioRepository;
import com.carrafasoft.bsuldo.api.v1.utils.FuncoesUtils;
import com.carrafasoft.bsuldo.api.v1.model.*;
import com.carrafasoft.bsuldo.api.v1.model.reports.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LancamentoService {

	private static final String LANCAMENTO_EM_USO = "O lançamento de código %s não pode ser removido, pois está em uso.";

	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private BancoRepository bancoRepository;

	@Autowired
	private BancoService bancoService;

	@Autowired
	private MetodoCobracaService metodoCobracaService;

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private LancamentoMapper lancamentoMapper;
	
	@Autowired
	private Mailer mailer;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	//@Scheduled(fixedDelay = 1000 * 60 * 30)
	@Scheduled(cron = "0 0 0 * * *")
	public void atualizarStatusLancamentoVencidos() {
		
		log.info("...: Atualizado a situação de todos os lancamentos vencidos :...");
		log.info("...: Verificando lançamentos DESPESA :...");
		lancamentoRepository.atualizaLancamentosVencidos();

		log.info("...: Verificando lançamentos RECEITA :...");
		lancamentoRepository.atualizaLancamentosRecebimentoVencidos();

		log.info("...: Fim da verificação automática :...");
		
	}

	//@Scheduled(fixedDelay = 1000 * 60 * 30)
	@Scheduled(cron = "0 5 0 * * *")
	public void enviarEmailLancamentosVencidos() {
		
		log.info("*********** Verificando se existe lancamentos vencidos para enviar por e-mail **************");
		
		enviarLancamentosVencidosByEmail();
		
	}
	
	//@Scheduled(fixedDelay = 1000 * 60 * 30)
	@Scheduled(cron = "0 2 0 * * *")
	public void enviarEmailLancamentosPendVencProximosSeteDias() {
		
		log.info("*********** Verificando se existe lancamentos pendentes ou vencidos nos próximos 7 dias para enviar por e-mail **************");
		
		enviarLancamentosPendentesVencidosByEmail();
		
	}


	@Transactional
	public Lancamentos cadastrarLancamentoSemParcelamento(LancamentoInputRepresentation lancamento, HttpServletResponse response, String tokenId) {

		try {
			String chavePesquisa = gerarChavePesquisa();
			lancamento.setChavePesquisa(chavePesquisa);
			Bancos bancoSalvo = new Bancos();

			BancoResponseRepresentation banco = lancamento.getBanco();
			if (banco != null) {
				if (banco.getCodigoBanco() == null && (banco.getNomeBanco() == null || banco.getNomeBanco().isEmpty())) {
					// Se o banco é inválido, definir banco como null
					lancamento.setBanco(null);
					bancoSalvo = null;
				} else {
					bancoSalvo = bancoService.buscaPorCodigoEPessoaId(lancamento.getBanco().getCodigoBanco(),tokenId);
				}
			}

			Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
			Categorias categoriaSalva = categoriaService.verificaCategoriaExistente(lancamento.getCategoria().getCodigo(), tokenId);
			MetodoDeCobranca metodoCobSalvo = metodoCobracaService.findMetodoCobPorCodigoAndTokenId(
					lancamento.getMetodoDeCobranca().getCodigoMetodoCobranca(), tokenId);

			//Envia o input pro mapper e retorna o tipo Lancamento para salvar
			Lancamentos lancamentoToSave = lancamentoMapper.toLancamentoMapper(
					lancamento,categoriaSalva,metodoCobSalvo,bancoSalvo,pessoaSalva
			);

			Lancamentos lancamentoSalvo = lancamentoRepository.save(lancamentoToSave);
			publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getLancamentoId()));

			return lancamentoSalvo;
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}


	@Transactional
	public Lancamentos cadastrarLancamentoComParcelamento(LancamentoInputRepresentation lancamento, HttpServletResponse response,
																String tokenId) {

		String chavePesquisa = gerarChavePesquisa();
		lancamento.setChavePesquisa(chavePesquisa);
		Bancos bancoSalvo = new Bancos();

		Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
		Categorias categoriaSalva = categoriaService.verificaCategoriaExistente(lancamento.getCategoria().getCodigo(), tokenId);
		MetodoDeCobranca metodoCobSalvo = metodoCobracaService.findMetodoCobPorCodigoAndTokenId(
				lancamento.getMetodoDeCobranca().getCodigoMetodoCobranca(), tokenId);

		BancoResponseRepresentation banco = lancamento.getBanco();
		if (banco != null) {
			if (banco.getCodigoBanco() == null && (banco.getNomeBanco() == null || banco.getNomeBanco().isEmpty())) {
				// Se o banco é inválido, definir banco como null
				lancamento.setBanco(null);
				bancoSalvo = null;
			} else {
				bancoSalvo = bancoService.buscaPorCodigoEPessoaId(lancamento.getBanco().getCodigoBanco(),tokenId);
			}
		}

		Integer qtdParcelas = lancamento.getQuantidadeParcelas();
		LocalDate dataPrimeiroVencimento = lancamento.getDataVencimento();
		BigDecimal valorTotal = lancamento.getValor();

		BigDecimal qtd = new BigDecimal(qtdParcelas);

		// Divisão para calcular o valor base de cada parcela, truncando os centavos extras
		BigDecimal valorParcelado = valorTotal.divide(qtd, 2, RoundingMode.DOWN);

		// Calcula a soma total das parcelas sem o ajuste
		BigDecimal ValorTotalparcelas = valorParcelado.multiply(qtd);

		// Calcula a diferença restante a ser distribuída
		BigDecimal valorRestante = ValorTotalparcelas.subtract(valorTotal);

		Lancamentos lancamentoSalvo2 = new Lancamentos();

		for (int i = 0; i < qtdParcelas; i++) {

			LocalDate dataPrimeiroVencimento2 = dataPrimeiroVencimento.plusMonths(i);
			lancamento.setNumeroParcela(i + 1);
			lancamento.setDataVencimento(dataPrimeiroVencimento2);

			if (valorRestante.compareTo(BigDecimal.ZERO) != 0 && lancamento.getNumeroParcela() == 1) {
				BigDecimal valorPrimeiraPacela = valorParcelado.subtract(valorRestante);
				lancamento.setValor(valorPrimeiraPacela);

			} else {
				lancamento.setValor(valorParcelado);
			}

			Lancamentos lancamentosToSave = lancamentoMapper.toLancamentoMapper(
					lancamento,categoriaSalva,metodoCobSalvo,bancoSalvo,pessoaSalva);

			lancamentoSalvo2 = lancamentoRepository.save(lancamentosToSave);

		} // FIM DO FOR

		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo2.getLancamentoId()));

		return lancamentoSalvo2;
	}

	@Transactional
	public Lancamentos atualizaLancamentoIdividual(String codigo, LancamentoResponseRepresentation lancamento, String tokenId) {

		try {
			LocalDate existDataPagamento = lancamento.getDataPagamento();
			Bancos bancoSalvo = new Bancos();

			Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
			Categorias categoriaSalva = categoriaService.verificaCategoriaExistente(lancamento.getCategoria().getCodigo(), tokenId);
			MetodoDeCobranca metodoCobSalvo = metodoCobracaService.findMetodoCobPorCodigoAndTokenId(
					lancamento.getMetodoDeCobranca().getCodigoMetodoCobranca(), tokenId);

			BancoResponseRepresentation banco = lancamento.getBanco();
			if (banco != null) {
				if (banco.getCodigoBanco() == null && (banco.getNomeBanco() == null || banco.getNomeBanco().isEmpty())) {
					// Se o banco é inválido, definir banco como null
					lancamento.setBanco(null);
					bancoSalvo = null;
				}else {
					bancoSalvo = bancoService.buscaPorCodigoEPessoaId(lancamento.getBanco().getCodigoBanco(),tokenId);
				}
			}

			if(existDataPagamento != null && lancamento.getTipoLancamento() == TipoLancamento.DESPESA) {

				lancamento.setSituacao(SituacaoEnum.PAGO);

			} else if (existDataPagamento == null && lancamento.getSituacao() == SituacaoEnum.VENCIDO) {

				lancamento.setSituacao(SituacaoEnum.VENCIDO);

			} else if(existDataPagamento != null && lancamento.getTipoLancamento() == TipoLancamento.RECEITA) {
				lancamento.setSituacao(SituacaoEnum.RECEBIDO);
			} else {

				lancamento.setSituacao(SituacaoEnum.PENDENTE);
			}

			Lancamentos lancamentoSalvo = findByCodigoLancamentoAndPessoaId(lancamento.getCodigoLancamento(), tokenId);

			//Verificar se aqui precisa ser corrigido****************
			lancamentoSalvo.setBanco(bancoSalvo);
			lancamentoSalvo.setCategoria(categoriaSalva);
			lancamentoSalvo.setPessoa(pessoaSalva);
			//*********************************************************

			BeanUtils.copyProperties(lancamento, lancamentoSalvo, "lancamentoId");

			return lancamentoRepository.save(lancamentoSalvo);
		} catch (LancamentoNaoEncontradoException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@Transactional
	public void deleteLancamento(String codigoLancamento) {

		try {
			lancamentoRepository.deleteByCodigoLancamento(codigoLancamento);
		} catch (EmptyResultDataAccessException e) {
			throw new LancamentoNaoEncontradoException(codigoLancamento);
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(String.format(LANCAMENTO_EM_USO, codigoLancamento));
		}
	}
	
	public Lancamentos cancelarLancamento(Long codigo, Boolean cancelar) {
		
		Lancamentos lancamentoSalvo = buscaPorId(codigo);
		
		if(cancelar == true) {
			
			lancamentoSalvo.setSituacao(SituacaoEnum.CANCELADO);
		
		} else if (cancelar == false && lancamentoSalvo.getDataPagamento() == null) {
			
			lancamentoSalvo.setSituacao(SituacaoEnum.PENDENTE);
		
		} else {
			
			lancamentoSalvo.setSituacao(SituacaoEnum.PAGO);
		}
		
		return lancamentoRepository.save(lancamentoSalvo);
	}

	public void remover(Long lancamentoId) {

		try {
			lancamentoRepository.deleteById(lancamentoId);
		} catch (EmptyResultDataAccessException e) {
			throw new LancamentoNaoEncontradoException(lancamentoId.toString());
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(String.format(LANCAMENTO_EM_USO, lancamentoId));
		}
	}
	
	public List<TotalMetodoCobranca> geraGradelancamentosPorMetodoCobranca(String dataIni, String dataFim, String tokenId) {
		
		int mes = FuncoesUtils.converterStringParaLocalDate(dataIni).getMonthValue();
		int ano = FuncoesUtils.converterStringParaLocalDate(dataFim).getYear();
		Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);
		
		/*************************** PRIMEIRA PARTE **********************************************************************/
		List<String> listaString = lancamentoRepository.lancamentosPorMetodoCobranca(mes, ano, pessoaId);
		List<LancamentosPorMetodoCobranca> listaLancPorCobranca = new ArrayList<LancamentosPorMetodoCobranca>();
		
		for (int i = 0; i < listaString.size(); i++) {
			
			LancamentosPorMetodoCobranca lancPorMetCob = new LancamentosPorMetodoCobranca();
			String textojunto = listaString.get(i);
			String [] textoSeparado = textojunto.split(",");
			
			lancPorMetCob.setNomeMetodoCobranca(textoSeparado[0]);
			lancPorMetCob.setDescricaoLancamento(textoSeparado[1]);
			lancPorMetCob.setValor(new BigDecimal(textoSeparado[2]));
			lancPorMetCob.setSituacao(textoSeparado[3]);
			lancPorMetCob.setParcela(textoSeparado[4]);
			
			listaLancPorCobranca.add(lancPorMetCob);
			
		}
		
		/*************************** SEGUNDA PARTE **********************************************************************/
		
		List<String> listametodoCob = lancamentoRepository.totalPorMetodoCobrancaMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaId
				);
		
		List<TotalMetodoCobrancaMes> totalMetodoCobrancaMes = new ArrayList<TotalMetodoCobrancaMes>();
		
		for (int i = 0; i < listametodoCob.size(); i++) {
			
			TotalMetodoCobrancaMes totalMetCob = new TotalMetodoCobrancaMes();
			
			String textoJunto = listametodoCob.get(i);
			String [] textoSeparado = textoJunto.split(",");
			
			totalMetCob.setNomeMetodoCobranca(textoSeparado[0]);
			BigDecimal total = new BigDecimal(textoSeparado[1]);
			totalMetCob.setTotais(total);
			
			totalMetodoCobrancaMes.add(totalMetCob);
			
		}
		
		/*************************** TERCEIRA PARTE **********************************************************************/
		
		List<TotalMetodoCobranca> listFinal = new ArrayList<TotalMetodoCobranca>();
		
		Long id01 = 0L;
		Long id02 = 0L;
		
		for (int i = 0; i < totalMetodoCobrancaMes.size(); i++) {
			
			id01 = 1L * i;
			id02 = id01;

			LancamentosPorMetodoCobranca lancMetCob = new LancamentosPorMetodoCobranca();
			List<LancamentosPorMetodoCobranca> list01 = new ArrayList<LancamentosPorMetodoCobranca>();
			TotalMetodoCobranca totalmetodo = new TotalMetodoCobranca();

			
			for (int j = 0; j < listaLancPorCobranca.size(); j++) {
				
				String nomeMetodoCobranca02 = listaLancPorCobranca.get(j).getNomeMetodoCobranca();
				
				if(totalMetodoCobrancaMes.get(i).getNomeMetodoCobranca().equalsIgnoreCase(nomeMetodoCobranca02)) {
					
					lancMetCob.setId(id02);
					lancMetCob.setNomeMetodoCobranca(listaLancPorCobranca.get(j).getNomeMetodoCobranca());
					lancMetCob.setDescricaoLancamento(listaLancPorCobranca.get(j).getDescricaoLancamento());
					lancMetCob.setValor(listaLancPorCobranca.get(j).getValor());
					lancMetCob.setSituacao(listaLancPorCobranca.get(j).getSituacao());
					lancMetCob.setParcela(listaLancPorCobranca.get(j).getParcela());
					
					list01.add(lancMetCob);
					lancMetCob = new LancamentosPorMetodoCobranca();
					
					id02 ++;
				}
				
			}
			
			totalmetodo.setId(id01);
			totalmetodo.setNomeMetodoCobranca(totalMetodoCobrancaMes.get(i).getNomeMetodoCobranca());
			totalmetodo.setLancMetodoCobrancaMes(list01);
			totalmetodo.setTotais(totalMetodoCobrancaMes.get(i).getTotais());
			
			listFinal.add(totalmetodo);
		}	
		
		return listFinal;
	}
	
	
	public Lancamentos gerarLancamentoRecorrente(@Valid LancamentoInputRepresentation lancamentos, HttpServletResponse response,
																 String qtdDias, String tokenId) {

		Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));

		log.info("...: Iniciando lançamento recorrente para o usuárioID: {} :...", pessoaSalva.getPessoaID());

		Categorias categoriaSalva = categoriaService.verificaCategoriaExistente(lancamentos.getCategoria().getCodigo(), tokenId);
		MetodoDeCobranca metodoCobSalvo = metodoCobracaService.findMetodoCobPorCodigoAndTokenId(
				lancamentos.getMetodoDeCobranca().getCodigoMetodoCobranca(), tokenId);

		BancoResponseRepresentation banco = lancamentos.getBanco();
		Bancos bancoSalvo = new Bancos();
		if (banco != null) {
			if (banco.getCodigoBanco() == null && (banco.getNomeBanco() == null || banco.getNomeBanco().isEmpty())) {
				// Se o banco é inválido, definir banco como null
				lancamentos.setBanco(null);
				bancoSalvo = null;
			}else {
				bancoSalvo = bancoService.buscaPorCodigoEPessoaId(lancamentos.getBanco().getCodigoBanco(),tokenId);
			}
		}
		
		Boolean lancRecorrente = lancamentos.getLancRecorrente();
		int qtdDiasRecorrente = 1;
		
		String chavePesquisa = gerarChavePesquisa();
		lancamentos.setChavePesquisa(chavePesquisa);
		
		if(qtdDias.equals(null) || qtdDias == null || qtdDias.equals("")) {
			qtdDiasRecorrente = 1;
		} else {
		
			qtdDiasRecorrente = Integer.parseInt(qtdDias);
		}
		
		if(qtdDiasRecorrente < 1) {
			qtdDiasRecorrente = 1;
		}
		
		Lancamentos lancamentoSalvo = new Lancamentos();
		
		for (int i = 0; i < qtdDiasRecorrente; i++) {
			Lancamentos preparaSalvar = new Lancamentos();
			//preparaSalvar = lancamentos;
			
			if(i == 0) {

				Lancamentos lancamentoToSave = lancamentoMapper.toLancamentoMapper(
						lancamentos, categoriaSalva,metodoCobSalvo,bancoSalvo,pessoaSalva);
				
				lancamentoSalvo = lancamentoRepository.save(lancamentoToSave);
				publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getLancamentoId()));
				
			} else {
				
				preparaSalvar.setDatavencimento(lancamentos.getDataVencimento().plusMonths(i));
				
				preparaSalvar.setValor(lancamentos.getValor());
				preparaSalvar.setDataPagamento(lancamentos.getDataPagamento());
				preparaSalvar.setDescricao(lancamentos.getDescricao());
				preparaSalvar.setParcelado(lancamentos.getParcelado());
				preparaSalvar.setQuantidadeParcelas(lancamentos.getQuantidadeParcelas());
				preparaSalvar.setNumeroParcela(1);
				preparaSalvar.setCategoria(categoriaSalva);
				preparaSalvar.setMetodoDeCobranca(metodoCobSalvo);
				preparaSalvar.setPessoa(pessoaSalva);
				preparaSalvar.setChavePesquisa(chavePesquisa);
				preparaSalvar.setLancRecorrente(lancRecorrente);
				preparaSalvar.setTipoLancamento(lancamentos.getTipoLancamento());
				
				lancamentoRepository.save(preparaSalvar);
				
			}
		}
		log.info("...: Lançamento recorrente cadastrado com sucesso. :...");
		return lancamentoSalvo;
	}

	public List<Lancamentos> findByNewFilters(String tokenId, String descricao, String dataVencimento,
											  String dataVencimentoFim, String metodoDeCobrancaId, String chavePesquisa, String situacao,
											  String tipoLancamento) {

		Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);
		LocalDate dataVenc = null;
		LocalDate dataIni = null;
		LocalDate dataFim = null;

		if(!StringUtils.hasLength(dataVencimento)) {
			dataVenc = FuncoesUtils.converterStringParaLocalDate(dataVencimento);
		}

		if(StringUtils.hasLength(dataVencimentoFim)) {

			dataIni = FuncoesUtils.converterStringParaLocalDate(dataVencimento);
			dataFim = FuncoesUtils.converterStringParaLocalDate(dataVencimentoFim);
			dataVencimento = null;
		} else {
			dataVenc = FuncoesUtils.converterStringParaLocalDate(dataVencimento);
		}

		if(!StringUtils.hasLength(descricao)) {
			descricao = null;
		}

		if(!StringUtils.hasLength(metodoDeCobrancaId)) {
			metodoDeCobrancaId = null;
		}

		if(!StringUtils.hasLength(situacao)) {
			situacao = null;
		}

		if(!StringUtils.hasLength(tipoLancamento)) {
			tipoLancamento = null;
		}

		if(!StringUtils.hasLength(chavePesquisa)) {
			chavePesquisa = null;
		}

		return lancamentoRepository.findByAllDescNew(
				descricao,
				dataVenc,
				dataIni,
				dataFim,
				metodoDeCobrancaId,
				situacao,
				chavePesquisa,
				tipoLancamento,
				pessoaId);
	}

	public List<LancamentosDiaMesReceitasDespesas> lancamentosDiaMesReceitasDespesas(String dataIni, String dataFim, String tokenId) {

		Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

		List<LancamentosDiaMesReceitasDespesas> retorno = new ArrayList<>();

		String sql = "SELECT " +
				"DAY(d.data) AS dia, " +
				"COALESCE(SUM(CASE WHEN l.tipo_lancamento = 'DESPESA' THEN l.valor ELSE 0 END), 0) AS lancDespesa, " +
				"COALESCE(SUM(CASE WHEN l.tipo_lancamento = 'RECEITA' THEN l.valor ELSE 0 END), 0) AS lancReceita " +
				"FROM ( " +
				"SELECT DATE_ADD('" + dataIni + "', INTERVAL n.n DAY) AS data " +
				"FROM ( " +
				"SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 " +
				"UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 " +
				"UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 " +
				"UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 " +
				"UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 " +
				"UNION ALL SELECT 20 UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 " +
				"UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL SELECT 26 UNION ALL SELECT 27 " +
				"UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30 " +
				") AS n " +
				"WHERE DATE_ADD('" + dataIni + "', INTERVAL n.n DAY) <= '" + dataFim + "') d " +
				"LEFT JOIN lancamentos l ON l.data_vencimento = d.data " +
				"AND l.data_vencimento BETWEEN '2024-08-01' AND '2024-08-31' " +
				"and l.pessoa_id = " + pessoaId +  " " +
				"GROUP BY d.data " +
				"ORDER BY d.data ";

		retorno = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(LancamentosDiaMesReceitasDespesas.class));

		return retorno;
	}

	public List<TotalDespesasPorMesAno> totalDespesasPorMesAnos(String ano, String tokenId) {

		Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);
		List<TotalDespesasPorMesAno> totalList = new ArrayList<>();

		String sql = "SELECT " +
				"MONTH(calendario.data) AS mes, " +
				"IFNULL(SUM(l.valor), 0) AS totalDespesas " +
				"FROM (SELECT MAKEDATE(" + ano + ", 1) + INTERVAL (seq.seq - 1) MONTH AS data " +
				"FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION " +
				"SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION " +
				"SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) seq) AS calendario " +
				"LEFT JOIN lancamentos l ON MONTH(l.data_vencimento) = MONTH(calendario.data) " +
				"AND YEAR(l.data_vencimento) = " + ano +" " +
				"AND l.tipo_lancamento = 'DESPESA' " +
				"and pessoa_id = " + pessoaId + " " +
				"GROUP BY mes " +
				"ORDER BY mes ";

		totalList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TotalDespesasPorMesAno.class));

		return totalList;
	}

	public List<GraficoTotalReceitasEDespesasPorAno> graficoTotalReceitasEDespesasPorAnos(String ano, String tokenId) {

		List<GraficoTotalReceitasEDespesasPorAno> retornoList = new ArrayList<>();

		Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

		String sql = "SELECT " +
				"MONTH(calendario.data) AS mes, " +
				"DATE_FORMAT(calendario.data, '%b') AS mesName, " +
				"IFNULL(SUM(CASE WHEN l.tipo_lancamento = 'DESPESA' THEN l.valor ELSE 0 END), 0) AS totalDespesas, " +
				"IFNULL(SUM(CASE WHEN l.tipo_lancamento = 'RECEITA' THEN l.valor ELSE 0 END), 0) AS totalReceitas " +
				"FROM " +
				"(SELECT MAKEDATE(" + ano + ", 1) + INTERVAL (seq.seq - 1) MONTH AS data " +
				"FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION " +
				"SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION " +
				"SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) seq) AS calendario " +
				"LEFT JOIN lancamentos l ON MONTH(l.data_vencimento) = MONTH(calendario.data) " +
				"AND YEAR(l.data_vencimento) = " + ano + " " +
				"AND l.pessoa_id = " + pessoaId + " " +
				"GROUP BY mes, mesName " +
				"ORDER BY mes ";

		retornoList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(GraficoTotalReceitasEDespesasPorAno.class));

		return retornoList;
	}

	public List<GraficoBarrasReceitaDespesaPorAno> graficoBarrasReceitaDespesaPorAnos(String ano, String tokenId) {

		List<GraficoBarrasReceitaDespesaPorAno> retornoList = new ArrayList<>();
		Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

		String sql = "SELECT anos.ano, " +
				"IFNULL(SUM(CASE WHEN l.tipo_lancamento = 'DESPESA' THEN l.valor END), 0) AS despesa, " +
				"IFNULL(SUM(CASE WHEN l.tipo_lancamento = 'RECEITA' THEN l.valor END), 0) AS receita " +
				"FROM (SELECT DISTINCT YEAR(data_vencimento) as ano  " +
				"FROM lancamentos " +
				"WHERE pessoa_id = " + pessoaId + " " +
				") anos " +
				"LEFT JOIN lancamentos l " +
				"ON YEAR(l.data_vencimento) = anos.ano " +
				"AND l.pessoa_id = " + pessoaId + " " +
				"GROUP BY anos.ano " +
				"ORDER BY anos.ano ";

		retornoList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(GraficoBarrasReceitaDespesaPorAno.class));

		return retornoList;
	}

	public List<GradeTotalMetodoDeCobranca> geraRelatorioGradeMetodoCobrancaPorMes(String ano, String tokenId) {

		List<GradeTotalMetodoDeCobranca> retornoList = new ArrayList<>();
		Long peessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

		String sql = "SELECT " +
				"m.nome_metodo_cobranca AS metodoCobranca, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 1 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Jan, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 2 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Fev, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 3 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Mar, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 4 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Abr, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 5 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Mai, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 6 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Jun, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 7 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Jul, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 8 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Ago, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 9 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Setembro, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 10 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Outubro, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 11 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Nov, " +
				"SUM(CASE WHEN MONTH(l.data_vencimento) = 12 AND YEAR(l.data_vencimento) = " + ano + " THEN l.valor ELSE 0 END) AS Dez, " +
				"SUM(l.valor) AS total " +

				"FROM lancamentos l " +
				"LEFT JOIN metodo_de_cobranca m ON l.metodo_de_cobranca_id = m.metodo_de_cobranca_id " +
				"WHERE l.pessoa_id = " + peessoaId +  " " +
				"AND tipo_lancamento = 'DESPESA' " +
				"AND YEAR(l.data_vencimento) = " + ano + "  " +
				"GROUP BY m.nome_metodo_cobranca " +
				"ORDER BY m.nome_metodo_cobranca ";

		retornoList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(GradeTotalMetodoDeCobranca.class));

		return retornoList;
	}

	public Lancamentos findByCodigo(String codigoLancamento) {

		return lancamentoRepository.findByCodigoLancamento(codigoLancamento)
				.orElseThrow(() -> new LancamentoNaoEncontradoException(codigoLancamento));
	}

	private String gerarChavePesquisa() {

		String chavePesquisa = FuncoesUtils.gerarHash();
		List<Lancamentos> verificaLanc = lancamentoRepository.buscarPorchave(chavePesquisa);

		while (!verificaLanc.isEmpty()) {

			chavePesquisa = FuncoesUtils.gerarHash();
			verificaLanc = lancamentoRepository.buscarPorchave(chavePesquisa);
		}

		return chavePesquisa;
	}

	public Lancamentos findByCodigoLancamentoAndPessoaId(String codigoLancamento, String tokenId) {

		return lancamentoRepository.findByCodigoLancamentoAndPessoaId(codigoLancamento, pessoaService.recuperaIdPessoaByToken(tokenId))
				.orElseThrow(() -> new LancamentoNaoEncontradoException(codigoLancamento));
	}
	
	private Lancamentos buscaPorId(Long codigo) {

		return lancamentoRepository.findById(codigo).orElseThrow(()-> new EmptyResultDataAccessException(1));

	}
	
	private void enviarLancamentosVencidosByEmail() {
		
		List<Lancamentos> lancamentosVencidos = lancamentoRepository.getLancamentosVencidos();
		
		if(lancamentosVencidos.size() > 0) {
			
			List<Usuarios> destinatarios = usuarioRepository.getusuariosParaenviarEmail();
			
			if(destinatarios.size() > 0) {
				
				log.info("Enviando lançamentos vencidos por e-mail. Total de lançamentos vencidos: " + lancamentosVencidos.size());
				mailer.avisarLancamentosVencidos(lancamentosVencidos, destinatarios);
				
			} else {
				log.warn("...: Não existe emails cadastrados para enviar e-mail :...");
			}
		}else {

			log.info("...: Não existe. lançamentos vencidos. :...");
		}
	}
	
	
	private void enviarLancamentosPendentesVencidosByEmail() {
			
			List<Lancamentos> lancamentosPendentes = lancamentoRepository.getLancamentosProximosSeteDias();
			
			if(lancamentosPendentes.size() > 0) {
				
				List<Usuarios> destinatarios = usuarioRepository.getusuariosParaenviarEmail();
				
				if(destinatarios.size() > 0) {

					log.info("Enviando lançamentos pendentes por e-mail. Total de lançamentos pendentes: " + lancamentosPendentes.size());
					mailer.avisarLancamentosPendentes(lancamentosPendentes, destinatarios);
					
				} else {
					log.warn("Não tem emails cadastrados para enviar e-mail");
				}
			}else {

				log.info("Não existe. lançamentos pendentes nos proximos 7 dias");
			}
		}

	private SituacaoEnum ajustaSituacao(Lancamentos lancamento) {

		var dataPagamento = lancamento.getDataPagamento();
		var tipolancamento = lancamento.getTipoLancamento();
		Boolean bancoExist = false;
		SituacaoEnum situacaoRetorno = null;

		Bancos banco = lancamento.getBanco();
		if (banco != null) {
			if (banco.getBancoId() == null && (banco.getNomeBanco() == null || banco.getNomeBanco().isEmpty())) {
				// Se o banco é inválido, definir banco como null
				bancoExist = false;
			} else {
				bancoExist = true;
			}
		}

		if(TipoLancamento.DESPESA.equals(lancamento.getTipoLancamento()) &&
			dataPagamento != null) {

			situacaoRetorno = SituacaoEnum.PAGO;
		}

		return situacaoRetorno;
	}
}

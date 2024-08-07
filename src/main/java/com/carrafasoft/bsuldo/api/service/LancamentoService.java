package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.enums.SituacaoEnum;
import com.carrafasoft.bsuldo.api.enums.TipoLancamento;
import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.mail.Mailer;
import com.carrafasoft.bsuldo.api.model.Bancos;
import com.carrafasoft.bsuldo.api.model.Lancamentos;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.model.Usuarios;
import com.carrafasoft.bsuldo.api.model.reports.LancamentosPorMetodoCobranca;
import com.carrafasoft.bsuldo.api.model.reports.TotalMetodoCobranca;
import com.carrafasoft.bsuldo.api.model.reports.TotalMetodoCobrancaMes;
import com.carrafasoft.bsuldo.api.repository.BancoRepository;
import com.carrafasoft.bsuldo.api.repository.LancamentoRepository;
import com.carrafasoft.bsuldo.api.repository.UsuarioRepository;
import com.carrafasoft.bsuldo.api.utils.FuncoesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LancamentoService {

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
	private Mailer mailer;
	
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

	public ResponseEntity<?> cadastrarLancamentoSemParcelamento(Lancamentos lancamento, HttpServletResponse response, String tokenId) {

		ResponseEntity<Lancamentos> httpStatus = new ResponseEntity<Lancamentos>(HttpStatus.METHOD_NOT_ALLOWED);
		String chavePesquisa = gerarChavePesquisa();
		lancamento.setChavePesquisa(chavePesquisa);

		Bancos banco = lancamento.getBanco();
		if (banco != null) {
			if (banco.getBancoId() == null && (banco.getNomeBanco() == null || banco.getNomeBanco().isEmpty())) {
				// Se o banco é inválido, definir banco como null
				lancamento.setBanco(null);
			}
		}

		Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
		lancamento.setPessoa(pessoaSalva);

		Lancamentos lancamentoSalvo = lancamentoRepository.save(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getLancamentoId()));

		httpStatus = ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);

		return httpStatus;
	}

	public ResponseEntity<?> cadastrarLancamentoComParcelamento(Lancamentos lancamento, HttpServletResponse response,
																String tokenId) {

		ResponseEntity<Lancamentos> httpStatus = new ResponseEntity<Lancamentos>(HttpStatus.METHOD_NOT_ALLOWED);
		String chavePesquisa = gerarChavePesquisa();
		lancamento.setChavePesquisa(chavePesquisa);

		Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
		lancamento.setPessoa(pessoaSalva);

		Bancos banco = lancamento.getBanco();
		if (banco != null) {
			if (banco.getBancoId() == null && (banco.getNomeBanco() == null || banco.getNomeBanco().isEmpty())) {
				// Se o banco é inválido, definir banco como null
				lancamento.setBanco(null);
			}
		}

		Integer qtdParcelas = lancamento.getQuantidadeParcelas();
		LocalDate dataPrimeiroVencimento = lancamento.getDatavencimento();
		BigDecimal valorTotal = lancamento.getValor();

		BigDecimal qtd = new BigDecimal(qtdParcelas);

		BigDecimal valorParcelado = valorTotal.divide(qtd, 2);
		BigDecimal ValorTotalparcelas = valorParcelado.multiply(qtd);
		BigDecimal valorRestante = ValorTotalparcelas.subtract(valorTotal);

		// System.out.println("Quantidade parcelas: " + qtdParcela);
		Lancamentos lancamentoSalvo2 = new Lancamentos();

		for (int i = 0; i < qtdParcelas; i++) {

			Lancamentos preparaSalvar = new Lancamentos();

			LocalDate dataPrimeiroVencimento2 = dataPrimeiroVencimento.plusMonths(i);
			// System.out.println(i);
			lancamento.setNumeroParcela(i + 1);
			lancamento.setDatavencimento(dataPrimeiroVencimento2);

			if (valorRestante.compareTo(BigDecimal.ZERO) != 0 && lancamento.getNumeroParcela() == 1) {
//				System.out.println("Primeira parcela");
				BigDecimal valorPrimeiraPacela = valorParcelado.subtract(valorRestante);
				lancamento.setValor(valorPrimeiraPacela);
				preparaSalvar.setValor(valorPrimeiraPacela);

			} else {

				lancamento.setValor(valorParcelado);
				preparaSalvar.setValor(valorParcelado);
			}

//			System.out.println(lancamento.getDatavencimento());
//			System.out.println("Numero parcela: " + lancamento.getNumeroParcela() + "/" + qtdParcelas);
//			System.out.println("Valor parcelado: " + lancamento.getValor());
//			System.out.println("Valor total parcelado: " + ValorTotalparcelas);
//			System.out.println("Valor a descontar na 1° parcela: "+ valorRestante);
//			System.out.println("-------------------------------------------------------------------------");

			preparaSalvar.setDatavencimento(dataPrimeiroVencimento2);
			preparaSalvar.setDataPagamento(lancamento.getDataPagamento());
			preparaSalvar.setDescricao(lancamento.getDescricao());
			preparaSalvar.setParcelado(lancamento.getParcelado());
			preparaSalvar.setQuantidadeParcelas(lancamento.getQuantidadeParcelas());
			preparaSalvar.setNumeroParcela(i + 1);
			preparaSalvar.setCategoria(lancamento.getCategoria());
			preparaSalvar.setMetodoDeCobranca(lancamento.getMetodoDeCobranca());
			preparaSalvar.setPessoa(lancamento.getPessoa());
			preparaSalvar.setChavePesquisa(chavePesquisa);
			preparaSalvar.setLancRecorrente(lancamento.getLancRecorrente());
			preparaSalvar.setTipoLancamento(lancamento.getTipoLancamento());
			//preparaSalvar.setSituacao(situacaoRecebida);

			lancamentoSalvo2 = lancamentoRepository.save(preparaSalvar);

		} // FIM DO FOR

		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo2.getLancamentoId()));
		httpStatus = ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo2);

		return httpStatus;
	}
	
	public Lancamentos atualizaLancamentoIdividual(Long codigo, Lancamentos lancamento, String tokenId) {
		
		LocalDate existDataPagamento = lancamento.getDataPagamento();

		Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
		lancamento.setPessoa(pessoaSalva);

		Bancos banco = lancamento.getBanco();
		if (banco != null) {
			if (banco.getBancoId() == null && (banco.getNomeBanco() == null || banco.getNomeBanco().isEmpty())) {
				// Se o banco é inválido, definir banco como null
				lancamento.setBanco(null);
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
		
		Lancamentos lancamentoSalvo = buscaPorId(codigo);
		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "lancamentoId");
		
		return lancamentoRepository.save(lancamentoSalvo);
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
	
	public List<TotalMetodoCobranca> geraGradelancamentosPorMetodoCobranca(String dataIni, String dataFim) {
		
		int mes = FuncoesUtils.converterStringParaLocalDate(dataIni).getMonthValue();
		int ano = FuncoesUtils.converterStringParaLocalDate(dataFim).getYear();
		
		/*************************** PRIMEIRA PARTE **********************************************************************/
		List<String> listaString = lancamentoRepository.lancamentosPorMetodoCobranca(mes, ano);
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
				FuncoesUtils.converterStringParaLocalDate(dataFim)
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
			
			id01 = 1000L * i;
			id02 = id01;
			
			String nomeMetodoCobranca = totalMetodoCobrancaMes.get(i).getNomeMetodoCobranca();
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
	
	
	public ResponseEntity<Lancamentos> gerarLancamentoRecorrente(@Valid Lancamentos lancamentos, HttpServletResponse response,
																 String qtdDias, String tokenId) {

		Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
		lancamentos.setPessoa(pessoaSalva);

		log.info("...: Iniciando lançamento recorrente para o usuárioID: {} :...", pessoaSalva.getPessoaID());

		Bancos banco = lancamentos.getBanco();
		if (banco != null) {
			if (banco.getBancoId() == null && (banco.getNomeBanco() == null || banco.getNomeBanco().isEmpty())) {
				// Se o banco é inválido, definir banco como null
				lancamentos.setBanco(null);
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
				
				lancamentoSalvo = lancamentoRepository.save(lancamentos);
				publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getLancamentoId()));
				
			} else {
				
				preparaSalvar.setDatavencimento(lancamentos.getDatavencimento().plusMonths(i));
				
				preparaSalvar.setValor(lancamentos.getValor());
				preparaSalvar.setDataPagamento(lancamentos.getDataPagamento());
				preparaSalvar.setDescricao(lancamentos.getDescricao());
				preparaSalvar.setParcelado(lancamentos.getParcelado());
				preparaSalvar.setQuantidadeParcelas(lancamentos.getQuantidadeParcelas());
				preparaSalvar.setNumeroParcela(1);
				preparaSalvar.setCategoria(lancamentos.getCategoria());
				preparaSalvar.setMetodoDeCobranca(lancamentos.getMetodoDeCobranca());
				preparaSalvar.setPessoa(lancamentos.getPessoa());
				preparaSalvar.setChavePesquisa(chavePesquisa);
				preparaSalvar.setLancRecorrente(lancRecorrente);
				preparaSalvar.setTipoLancamento(lancamentos.getTipoLancamento());
				
				lancamentoRepository.save(preparaSalvar);
				
			}
		}
		log.info("...: Lançamento recorrente cadastrado com sucesso. :...");
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
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

	private String gerarChavePesquisa() {

		String chavePesquisa = FuncoesUtils.gerarHash();
		List<Lancamentos> verificaLanc = lancamentoRepository.buscarPorchave(chavePesquisa);

		while (!verificaLanc.isEmpty()) {

			chavePesquisa = FuncoesUtils.gerarHash();
			verificaLanc = lancamentoRepository.buscarPorchave(chavePesquisa);
		}

		return chavePesquisa;
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

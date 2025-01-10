package com.carrafasoft.bsuldo.api.resource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.mapper.LancamentoMapper;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.LancamentoInputRepresentation;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.LancamentoResponseRepresentation;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.CategoriaNaoEncontradaException;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.LancamentoNaoEncontradoException;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.MetodoDeCobrancaNaoEncontradoException;
import com.carrafasoft.bsuldo.api.model.reports.*;
import com.carrafasoft.bsuldo.api.service.CategoriaService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.carrafasoft.bsuldo.api.model.Lancamentos;
import com.carrafasoft.bsuldo.api.repository.LancamentoRepository;
import com.carrafasoft.bsuldo.api.service.LancamentoService;
import com.carrafasoft.bsuldo.api.utils.FuncoesUtils;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private LancamentoMapper lancamentoMapper;
	
	
	@GetMapping
	public List<LancamentoResponseRepresentation> listarTodos(@RequestParam("tokenId") String tokenId,
															  @RequestParam(value = "descricao", required = false) String descricao,
															  @RequestParam(value = "dataVencimento", required = false) String dataVencimento,
															  @RequestParam(value = "dataVencimentoFim", required = false) String dataVencimentoFim,
															  @RequestParam(value = "metodoDeCobrancaId", required = false) String metodoDeCobrancaId,
															  @RequestParam(value = "situacao", required = false) String situacao,
															  @RequestParam(value = "tipoLancamento", required = false) String tipoLancamento,
															  @RequestParam(value = "chavePesquisa", required = false) String chavePesquisa) {

		return lancamentoMapper.toListLancamentoResponseRepresentationMapper(
				lancamentoService.findByNewFilters(tokenId, descricao, dataVencimento, dataVencimentoFim,
						metodoDeCobrancaId, chavePesquisa, situacao, tipoLancamento));
	}


	@PostMapping
	public ResponseEntity<LancamentoResponseRepresentation> cadastrarLancamento(@Valid @RequestBody LancamentoInputRepresentation lancamento,
																				HttpServletResponse response,
																				@RequestParam("tokenId") String tokenId) {

			try {
				Lancamentos retorno = new Lancamentos();
				Boolean existParcelamento = lancamento.getParcelado();

				if(!existParcelamento) { //Se não for parcelado entrar aqui

					retorno = lancamentoService.cadastrarLancamentoSemParcelamento(lancamento, response, tokenId);

				} else { // Se for parcelado entrar aqui

					retorno = lancamentoService.cadastrarLancamentoComParcelamento(lancamento, response, tokenId);
				}

				return ResponseEntity.status(HttpStatus.CREATED)
						.body(lancamentoMapper.toLancamentoResponseRepresentationMapper(retorno));
			} catch (EntidadeNaoEncontradaException e) {
				throw new NegocioException(e.getMessage());
			}
	}
	
	
	@GetMapping("/{codigo}")
	public LancamentoResponseRepresentation buscaPorId(@PathVariable String codigo) {

		return lancamentoMapper.toLancamentoResponseRepresentationMapper(lancamentoService.findByCodigo(codigo));
	}

	@PutMapping("/{codigo}")
	public ResponseEntity<LancamentoResponseRepresentation> atualizaLancamentoIdividual(@PathVariable String codigo,
																						@Valid @RequestBody LancamentoResponseRepresentation lancamento,
																   @RequestParam("tokenId") String tokenId) {

			Lancamentos lancamentoSalvo = lancamentoService.atualizaLancamentoIdividual(codigo, lancamento, tokenId);

			return ResponseEntity.ok(lancamentoMapper.toLancamentoResponseRepresentationMapper(lancamentoSalvo));

	}
	
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerLancamento(@PathVariable String codigo) {
		
		lancamentoService.deleteLancamento(codigo);
	}

	//TODO verificar se mantem esse endpoint, nunca foi usado
	@Transactional
	@PutMapping("/{codigo}/cancelar-lancamento")
	public ResponseEntity<Lancamentos> cancelarLancamento(@PathVariable Long codigo, @RequestBody Boolean cancelar) {
		
		try {
			Lancamentos lancamentoCancelado = lancamentoService.cancelarLancamento(codigo, cancelar);

			return ResponseEntity.ok(lancamentoCancelado);
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@Transactional
	@PostMapping("/lancamento-recorrente")
	public ResponseEntity<Lancamentos> lancamentoRecorrente(@Valid @RequestBody Lancamentos lancamentos, HttpServletResponse response,
															@RequestParam("qtd_dias") String qtdDias,
															@RequestParam("tokenId") String tokenId) {
		
		try {
			return lancamentoService.gerarLancamentoRecorrente(lancamentos, response, qtdDias, tokenId);

		}catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}
	
	//************************************************************ RELATORIOS *****************************************************************************************************************
	
	@GetMapping("/lancamentos-vencidos")
	public List<Lancamentos> listarLancamentosVencidos() {
		
		return lancamentoRepository.buscaLancamentosVencidos();
	}
	
	@GetMapping("/pesquisa")
	public List<Lancamentos> buscaPorDescricao(@RequestParam("descricao") String descricao) {
		
		return lancamentoRepository.buscaPorDescricao(descricao);
	}
	
	@GetMapping("/pesquisa-por-data_ini_fim-vencimento")
	public List<Lancamentos> buscaPorDataVencimentoIniFim(@RequestParam("vencimentoInicio") String vencimentoInicio, @RequestParam("vencimentoFim")  String vencimentoFim) {
		
		return lancamentoRepository.buscaPorDataVencimentoDataIniDataFim(
				FuncoesUtils.converterStringParaLocalDate(vencimentoInicio), 
				FuncoesUtils.converterStringParaLocalDate(vencimentoFim)
				);
	}
	
	@GetMapping("/pesquisa-vencimento-ate")
	public List<Lancamentos> buscaPorDataVencimentoMenorQueDataInformada(@RequestParam("dataVencimento") String dataVencimento) {
		
		return lancamentoRepository.buscaPorDataVencimentoMenorQueDataInformada(
				FuncoesUtils.converterStringParaLocalDate(dataVencimento));
	}
	
	@GetMapping("/busca-por-situacao")
	public List<Lancamentos> buscaBysituacao(@RequestParam("situacao") String situacao) {
		
		return lancamentoRepository.buscaBySituacao(situacao);
	}

	@GetMapping("/busca-by-chave-pesquisa")
	public List<Lancamentos> buscaByChavePesquisa(@RequestParam("chavePesquisa") String chavePesquisa) {
		
		return lancamentoRepository.buscaByChavePesquisa(chavePesquisa);
	}
	
	@GetMapping("/valor-a-pagar-no-mes")
	public BigDecimal valorAPagarNoMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
									   @RequestParam("tokenId") String tokenId) {
		
		return lancamentoRepository.valorApagarNoMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaService.recuperaIdPessoaByToken(tokenId)
				);
	}
	
	@GetMapping("/valor-pago-no-mes")
	public BigDecimal valorPagoNoMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
									 @RequestParam("tokenId") String tokenId) {
		
		return lancamentoRepository.valorPagoNoMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaService.recuperaIdPessoaByToken(tokenId)
				);
	}
	
	@GetMapping("/valor-vencido-no-mes")
	public BigDecimal valorVencidoNoMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
										@RequestParam("tokenId") String tokenId) {
		
		return lancamentoRepository.valorVencidoNoMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaService.recuperaIdPessoaByToken(tokenId)
				);
	}
	
	@GetMapping("/total-devedor-por-ano")
	public BigDecimal totalDevedorPorAno(@RequestParam("ano") String ano, @RequestParam("tokenId") String tokenId) {
		
		return lancamentoRepository.totalDevedorPorAno(Integer.valueOf(ano),
				pessoaService.recuperaIdPessoaByToken(tokenId));
	}
	
	@GetMapping("/total-pago-por-ano")
	public BigDecimal totalPagoNoAno(@RequestParam("ano") String ano, @RequestParam("tokenId") String tokenId) {
		
		return lancamentoRepository.totalPagoNoAno(Integer.valueOf(ano),
				pessoaService.recuperaIdPessoaByToken(tokenId));
	}
	
	@GetMapping("/perc-pago-no-mes")
	public BigDecimal percentualPagoNoMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
										  @RequestParam("tokenId") String tokenId) {
		
		
		return lancamentoRepository.percentualPagoNoMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaService.recuperaIdPessoaByToken(tokenId)
				);
	}

	@GetMapping("/valor-a-receber-no-mes")
	public BigDecimal valorAReceberNoMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
										 @RequestParam("tokenId") String tokenId) {

		return lancamentoRepository.valorAReceberNoMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaService.recuperaIdPessoaByToken(tokenId)
		);
	}


	@GetMapping("/valor-recebido-no-mes")
	public BigDecimal valorARecebidoNoMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
										 @RequestParam("tokenId") String tokenId) {

		return lancamentoRepository.valorRecebidoNoMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaService.recuperaIdPessoaByToken(tokenId)
		);
	}

	@GetMapping("/valor-atrasado-no-mes")
	public BigDecimal valorAtrasadoNoMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
										@RequestParam("tokenId") String tokenId) {

		return lancamentoRepository.valorAtrasadoNoMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaService.recuperaIdPessoaByToken(tokenId)
		);
	}


	@GetMapping("/total-a-receber-no-ano")
	public BigDecimal totalAReceberNoAno(@RequestParam("ano") String ano, @RequestParam("tokenId") String tokenId) {

		return lancamentoRepository.totalAReceberNoAno(Integer.valueOf(ano),
				pessoaService.recuperaIdPessoaByToken(tokenId));
	}


	@GetMapping("/receita-total-por-ano")
	public BigDecimal receitaTotalAno(@RequestParam("ano") String ano, @RequestParam("tokenId") String tokenId) {

		return lancamentoRepository.receitatotalAno(Integer.valueOf(ano),
				pessoaService.recuperaIdPessoaByToken(tokenId));
	}


	@GetMapping("/perc-recebido-no-mes")
	public BigDecimal percentualRecebidoNoMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
										  @RequestParam("tokenId") String tokenId) {

		return lancamentoRepository.percentualRecebidoNoMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaService.recuperaIdPessoaByToken(tokenId)
		);
	}
	
	@GetMapping("/total-categoria-mes")
	public List<TotalPorCategoriaMes> totalPorCategoriaMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
														   @RequestParam("tokenId") String tokenId) {
		
		List<String> lista = lancamentoRepository.totalPorCategoriaMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaService.recuperaIdPessoaByToken(tokenId)
				);
		
		
		List<TotalPorCategoriaMes> totalCategoriaMes = new ArrayList<TotalPorCategoriaMes>();
		
		
		for (int i = 0; i < lista.size(); i++) {
			
			TotalPorCategoriaMes totalMes = new TotalPorCategoriaMes();
						
			String textoJunto = lista.get(i);
			
			String[] textoSeparado = textoJunto.split(",");
			
			totalMes.setCategoria(textoSeparado[0]);
			BigDecimal total = new BigDecimal(textoSeparado[1]);
			totalMes.setTotais(total);
			
			totalCategoriaMes.add(totalMes);
			
		}
		
		return totalCategoriaMes;
	}


	@GetMapping("/total-metodo-cobranca-mes")
	public List<GradeTotalMetodoDeCobranca> totalMetodoDeCobrancas(@RequestParam("ano") String ano, @RequestParam("tokenId") String tokenId) {

		return lancamentoService.geraRelatorioGradeMetodoCobrancaPorMes(ano, tokenId);
	}

	@GetMapping("/total-metodo-cob-mes")
	public List<TotalMetodoCobrancaMes> totalPorMetodoCobrancaMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
																  @RequestParam("tokenId") String tokenId) {
		
		List<String> lista = lancamentoRepository.totalPorMetodoCobrancaMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim),
				pessoaService.recuperaIdPessoaByToken(tokenId)
				);
		
		List<TotalMetodoCobrancaMes> totalMetodoCobrancaMes = new ArrayList<TotalMetodoCobrancaMes>();
		
		for (int i = 0; i < lista.size(); i++) {
			
			TotalMetodoCobrancaMes totalMetCob = new TotalMetodoCobrancaMes();
			
			String textoJunto = lista.get(i);
			String [] textoSeparado = textoJunto.split(",");
			
			totalMetCob.setNomeMetodoCobranca(textoSeparado[0]);
			BigDecimal total = new BigDecimal(textoSeparado[1]);
			totalMetCob.setTotais(total);
			
			totalMetodoCobrancaMes.add(totalMetCob);
			
		}
		
		return totalMetodoCobrancaMes;
	}
	
	
	@GetMapping("/lancamentos-por-dia")
	public List<LancamentosDiaMes> totalLancamentosPorDiaMes(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim) {
		
		List<String> lista = lancamentoRepository.totalLancamentosPorDiaMes(
				FuncoesUtils.converterStringParaLocalDate(dataIni),
				FuncoesUtils.converterStringParaLocalDate(dataFim)
				);
		
		int qtdDiasMes = FuncoesUtils.quantidadeDiasNoMes(FuncoesUtils.converterStringParaLocalDate(dataIni));
		
		
		List<LancamentosDiaMes> lancamentoPorDia = new ArrayList<LancamentosDiaMes>();
		
		for (int i = 0; i < lista.size(); i++) {
			
			LancamentosDiaMes lancDia = new LancamentosDiaMes();
			
			String textoJunto = lista.get(i);
			String [] textoSeparado = textoJunto.split(","); 
			
			lancDia.setDia(textoSeparado[0]);
			BigDecimal total = new BigDecimal(textoSeparado[1]);
			lancDia.setTotais(total);
			
			lancamentoPorDia.add(lancDia);	
		}
		
		
		List<LancamentosDiaMes> lancamentoPorDiaCompleto = new ArrayList<LancamentosDiaMes>();
		
		for (int i = 1; i <= qtdDiasMes; i++) {
			
			LancamentosDiaMes lancDiaCompleto = new LancamentosDiaMes();
			String dia = Integer.valueOf(i).toString();
			
			Boolean operador = true;
					
			
			for (int j = 0; j < lancamentoPorDia.size(); j++) {

				if(lancamentoPorDia.get(j).getDia().equals(dia)) {
					
					lancDiaCompleto.setDia(dia);
					lancDiaCompleto.setTotais(lancamentoPorDia.get(j).getTotais());
					operador = false;
				}
				
			}

			if(operador) {
				
				lancDiaCompleto.setDia(dia);
				lancDiaCompleto.setTotais(BigDecimal.ZERO);
				operador = true;
			}
			
			
			lancamentoPorDiaCompleto.add(lancDiaCompleto);
		}
		
		//return lancamentoPorDia;
		return lancamentoPorDiaCompleto;
	}

	@GetMapping("/lancamentos-dia-mes-receitas-despesas")
	public List<LancamentosDiaMesReceitasDespesas> lancamentosDiaMesReceitasDespesas(@RequestParam("dataIni") String dataIni,
																					 @RequestParam("dataFim") String dataFim,
																					 @RequestParam("tokenId") String tokenId) {

		List<LancamentosDiaMesReceitasDespesas> list = lancamentoService.lancamentosDiaMesReceitasDespesas(dataIni, dataFim, tokenId);

		return list;
	}

	@GetMapping("/lista-total-despesa-por-mes-ano")
	public List<TotalDespesasPorMesAno> listaTotalDespesaporMesAno(@RequestParam("ano") String ano,
																   @RequestParam("tokenId") String tokenId) {

		return lancamentoService.totalDespesasPorMesAnos(ano, tokenId);
	}

	@GetMapping("/grafico-total-receita-despesa-by-mes-ano")
	public List<GraficoTotalReceitasEDespesasPorAno> graficoTotalReceitasEDespesasPorAnos(@RequestParam("ano") String ano,
																   @RequestParam("tokenId") String tokenId) {

		return lancamentoService.graficoTotalReceitasEDespesasPorAnos(ano, tokenId);
	}

	@GetMapping("/grafico-total-receita-despesa-por-ano")
	public List<GraficoBarrasReceitaDespesaPorAno> graficoBarrasReceitaDespesaPorAnos(@RequestParam("ano") String ano,
																						  @RequestParam("tokenId") String tokenId) {

		return lancamentoService.graficoBarrasReceitaDespesaPorAnos(ano, tokenId);
	}
	
	@GetMapping("/totais-por-ano")
	public List<TotaisPorAno> lancamentosNoAno(@RequestParam("ano") String ano) {
		
		List<String> totaisAnoString = lancamentoRepository.listarTotaisPorAno(ano);
		
		List<TotaisPorAno> listaTotaisPorAno = new ArrayList<TotaisPorAno>();
		
		for (int i = 0; i < totaisAnoString.size(); i++) {
			
			TotaisPorAno totalPorAno = new TotaisPorAno();
			
			String textoJunto = totaisAnoString.get(i);
			String [] textoSeparado = textoJunto.split(","); 
			
			if(textoSeparado[0].equals("null")) {
				totalPorAno.setJan("0");
			} else {
				totalPorAno.setJan(textoSeparado[0]);
			}
			if(textoSeparado[1].equals("null")) {
				totalPorAno.setFev("0");
			} else {
				totalPorAno.setFev(textoSeparado[1]);
			}
			if(textoSeparado[2].equals("null")) {
				totalPorAno.setMar("0");
			} else {
				totalPorAno.setMar(textoSeparado[2]);
			}
			if(textoSeparado[3].equals("null")) {
				totalPorAno.setAbr("0");
			} else {
				totalPorAno.setAbr(textoSeparado[3]);
			}
			if(textoSeparado[4].equals("null")) {
				totalPorAno.setMai("0");
			} else {
				totalPorAno.setMai(textoSeparado[4]);
			}
			if(textoSeparado[5].equals("null")) {
				totalPorAno.setJun("0");
			} else {
				totalPorAno.setJun(textoSeparado[5]);
			}
			if(textoSeparado[6].equals("null")) {
				totalPorAno.setJul("0");
			} else {
				totalPorAno.setJul(textoSeparado[6]);
			}
			if(textoSeparado[7].equals("null")) {
				totalPorAno.setAgo("0");
			} else {
				totalPorAno.setAgo(textoSeparado[7]);
			}
			if(textoSeparado[8].equals("null")) {
				totalPorAno.setSet("0");
			} else {
				totalPorAno.setSet(textoSeparado[8]);
			}
			if(textoSeparado[9].equals("null")) {
				totalPorAno.setOut("0");
			} else {
				totalPorAno.setOut(textoSeparado[9]);
			}
			if(textoSeparado[10].equals("null")) {
				totalPorAno.setNov("0");
			} else {
				totalPorAno.setNov(textoSeparado[10]);
			}
			if(textoSeparado[11].equals("null")) {
				totalPorAno.setDez("0");
			} else {
				totalPorAno.setDez(textoSeparado[11]);
			}
			
			if(textoSeparado[12].equals("null")) {
				totalPorAno.setTotal(BigDecimal.ZERO);
			} else {
				totalPorAno.setTotal(new BigDecimal(textoSeparado[12]));
			}
			
			
			
			listaTotaisPorAno.add(totalPorAno);
			
		}
		
		return listaTotaisPorAno;
	}

	@GetMapping("/grade-lancamentos-por-metodo_cobranca")
	public List<TotalMetodoCobranca> gradeLancamentosPorMetodoCobranca(@RequestParam("dataIni") String dataIni, @RequestParam("dataFim") String dataFim,
																	   @RequestParam("tokenId") String tokenId) {
		
		List<TotalMetodoCobranca> lista = lancamentoService.geraGradelancamentosPorMetodoCobranca(dataIni, dataFim, tokenId);
		
		return lista;
	}
	
}

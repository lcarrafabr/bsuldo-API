package com.carrafasoft.bsuldo.api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.enums.SituacaoEnum;
import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.model.Lancamentos;
import com.carrafasoft.bsuldo.api.repository.LancamentoRepository;
import com.carrafasoft.bsuldo.api.utils.FuncoesUtils;

@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private ApplicationEventPublisher publisher;
	
	
	@Scheduled(cron = "0 0 0 * * *")
	public void atualizarStatusLancamentoVencidos() {
		
		System.out.println("Atualizado a situação de todos os lancamentos vencidos");
		
		lancamentoRepository.atualizaLancamentosVencidos();
		
	}

	public ResponseEntity<?> cadastrarLancamentoSemParcelamento(Lancamentos lancamento, HttpServletResponse response) {

		ResponseEntity<Lancamentos> httpStatus = new ResponseEntity<Lancamentos>(HttpStatus.METHOD_NOT_ALLOWED);
		String chavePesquisa = gerarChavePesquisa();
		lancamento.setChavePesquisa(chavePesquisa);

		Lancamentos lancamentoSalvo = lancamentoRepository.save(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getLancamentoId()));

		httpStatus = ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);

		return httpStatus;
	}

	public ResponseEntity<?> cadastrarLancamentoComParcelamento(Lancamentos lancamento, HttpServletResponse response) {

		ResponseEntity<Lancamentos> httpStatus = new ResponseEntity<Lancamentos>(HttpStatus.METHOD_NOT_ALLOWED);
		String chavePesquisa = gerarChavePesquisa();
		lancamento.setChavePesquisa(chavePesquisa);

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
			// preparaSalvar.setSituacao(SituacaoEnum.PENDENTE);

			lancamentoSalvo2 = lancamentoRepository.save(preparaSalvar);

		} // FIM DO FOR

		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo2.getLancamentoId()));
		httpStatus = ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo2);

		return httpStatus;
	}
	
	public Lancamentos atualizaLancamentoIdividual(Long codigo, Lancamentos lancamento) {
		
		LocalDate existDataPagamento = lancamento.getDataPagamento();
		
		if(existDataPagamento != null) {
			
			lancamento.setSituacao(SituacaoEnum.PAGO);
		
		} else if (existDataPagamento == null && lancamento.getSituacao() == SituacaoEnum.VENCIDO) {
			
			lancamento.setSituacao(SituacaoEnum.VENCIDO);
			
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
		
		Lancamentos lancamentoSalvo = lancamentoRepository.findById(codigo).orElseThrow(()-> new EmptyResultDataAccessException(1));
		return lancamentoSalvo;
	}

}

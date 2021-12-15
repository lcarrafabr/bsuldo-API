package com.carrafasoft.bsuldo.api.model.reports;

import java.math.BigDecimal;

public class LancamentosPorMetodoCobranca {

	private Long id;
	private String nomeMetodoCobranca;
	private String descricaoLancamento;
	private BigDecimal valor;
	private String situacao;
	private String parcela;

	public String getNomeMetodoCobranca() {
		return nomeMetodoCobranca;
	}

	public void setNomeMetodoCobranca(String nomeMetodoCobranca) {
		this.nomeMetodoCobranca = nomeMetodoCobranca;
	}

	public String getDescricaoLancamento() {
		return descricaoLancamento;
	}

	public void setDescricaoLancamento(String descricaoLancamento) {
		this.descricaoLancamento = descricaoLancamento;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getParcela() {
		return parcela;
	}

	public void setParcela(String parcela) {
		this.parcela = parcela;
	}

}

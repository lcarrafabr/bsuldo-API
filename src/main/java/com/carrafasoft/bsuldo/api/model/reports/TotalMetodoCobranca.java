package com.carrafasoft.bsuldo.api.model.reports;

import java.math.BigDecimal;
import java.util.List;

public class TotalMetodoCobranca {

	private Long id;
	private String nomeMetodoCobranca;
	private BigDecimal totais;
	private List<LancamentosPorMetodoCobranca> lancMetodoCobrancaMes;

	public String getNomeMetodoCobranca() {
		return nomeMetodoCobranca;
	}

	public void setNomeMetodoCobranca(String nomeMetodoCobranca) {
		this.nomeMetodoCobranca = nomeMetodoCobranca;
	}

	public BigDecimal getTotais() {
		return totais;
	}

	public void setTotais(BigDecimal totais) {
		this.totais = totais;
	}

	public List<LancamentosPorMetodoCobranca> getLancMetodoCobrancaMes() {
		return lancMetodoCobrancaMes;
	}

	public void setLancMetodoCobrancaMes(List<LancamentosPorMetodoCobranca> lancMetodoCobrancaMes) {
		this.lancMetodoCobrancaMes = lancMetodoCobrancaMes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}

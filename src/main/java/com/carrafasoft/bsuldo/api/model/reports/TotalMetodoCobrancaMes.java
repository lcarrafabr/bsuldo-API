package com.carrafasoft.bsuldo.api.model.reports;

import java.math.BigDecimal;

public class TotalMetodoCobrancaMes {

	private String nomeMetodoCobranca;
	private BigDecimal totais;

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

}

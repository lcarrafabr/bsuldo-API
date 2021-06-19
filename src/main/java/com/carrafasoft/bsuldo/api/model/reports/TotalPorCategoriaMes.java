package com.carrafasoft.bsuldo.api.model.reports;

import java.math.BigDecimal;

public class TotalPorCategoriaMes {

	private String categoria;
	private BigDecimal totais;

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public BigDecimal getTotais() {
		return totais;
	}

	public void setTotais(BigDecimal totais) {
		this.totais = totais;
	}

}

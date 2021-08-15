package com.carrafasoft.bsuldo.api.model.reports;

import java.math.BigDecimal;

public class LancamentosDiaMes {

	private String dia;
	private BigDecimal totais;

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public BigDecimal getTotais() {
		return totais;
	}

	public void setTotais(BigDecimal totais) {
		this.totais = totais;
	}

	@Override
	public String toString() {
		return dia;
	}
	
	

}

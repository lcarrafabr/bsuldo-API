package com.carrafasoft.bsuldo.api.enums;

public enum TipoLancamento {

	RECEITA("Receita"),
	DESPESA("Despesa");
	
	 
	private final String descricao;
	
	TipoLancamento(String descricao) {
		
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
}

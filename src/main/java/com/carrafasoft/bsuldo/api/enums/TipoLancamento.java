package com.carrafasoft.bsuldo.api.enums;

public enum TipoLancamento {

	RECEITA("RECEITA"),
	DESPESA("DESPESA");
	
	 
	private final String descricao;
	
	TipoLancamento(String descricao) {
		
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
}

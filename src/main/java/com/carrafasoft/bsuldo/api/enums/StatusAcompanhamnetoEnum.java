package com.carrafasoft.bsuldo.api.enums;

public enum StatusAcompanhamnetoEnum {

    EM_ANALISE("EM ANÁLISE"),
    APROVADO_COMPRA("APROVADO COMPRA"),
    COMPRADO("COMPRADO"),
    POSSIVEL_COMPRA("POSSÍVEL COMPRA"),
    CONGELADO_PARA_ANALISE("CONGELADO P/ ANÁLISE"),
    REJEITADO("REJEITADO");

    private final String descricao;

    StatusAcompanhamnetoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

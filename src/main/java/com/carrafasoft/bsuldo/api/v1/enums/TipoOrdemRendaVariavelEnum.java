package com.carrafasoft.bsuldo.api.v1.enums;

public enum TipoOrdemRendaVariavelEnum {

    COMPRA("COMPRA"),
    VENDA("VENDA"),
    BONIFICACAO("BONIFICAÇÃO"),
    DESDOBRAMENTO("DESDOBRAMENTO"),
    AGRUPAMENTO("AGRUPAMENTO"),
    AMORTIZACAO("AMORTIZAÇÃO");

    private final String descricao;

    TipoOrdemRendaVariavelEnum(String descricao) {
        this.descricao = descricao;
    }
}

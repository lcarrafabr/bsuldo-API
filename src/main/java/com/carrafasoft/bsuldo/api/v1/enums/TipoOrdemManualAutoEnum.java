package com.carrafasoft.bsuldo.api.v1.enums;

public enum TipoOrdemManualAutoEnum {

    MANUAL("MANUAL"),
    AUTOMATICO("AUTOMÁTICO");

    private final String descricao;

    TipoOrdemManualAutoEnum(String descricao) {
        this.descricao = descricao;
    }
}

package com.carrafasoft.bsuldo.api.enums;

public enum TipoOrdemManualAutoEnum {

    MANUAL("MANUAL"),
    AUTOMATICO("AUTOMÁTICO");

    private final String descricao;

    TipoOrdemManualAutoEnum(String descricao) {
        this.descricao = descricao;
    }
}

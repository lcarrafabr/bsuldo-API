package com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto;

import java.math.BigDecimal;

public class MesesValorDTO {

    private String mes;
    private BigDecimal valor = BigDecimal.ZERO;

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}

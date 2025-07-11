package com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto;

import java.math.BigDecimal;

public class RelatorioPercentualAcoesFiis {

    private String tipoAtivoEnum;

    private BigDecimal total;

    private BigDecimal percentual;

    public String getTipoAtivoEnum() {
        return tipoAtivoEnum;
    }

    public void setTipoAtivoEnum(String tipoAtivoEnum) {
        this.tipoAtivoEnum = tipoAtivoEnum;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }
}

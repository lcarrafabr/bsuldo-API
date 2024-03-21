package com.carrafasoft.bsuldo.api.model.rendavariavel.dto;

import com.carrafasoft.bsuldo.api.enums.TipoAtivoEnum;

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

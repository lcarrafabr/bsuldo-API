package com.carrafasoft.bsuldo.braviapi.modelo;

import java.math.BigDecimal;

public class PrecoAtualCota {

    private String ticker;
    private BigDecimal valorAtualCota;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public BigDecimal getValorAtualCota() {
        return valorAtualCota;
    }

    public void setValorAtualCota(BigDecimal valorAtualCota) {
        this.valorAtualCota = valorAtualCota;
    }
}

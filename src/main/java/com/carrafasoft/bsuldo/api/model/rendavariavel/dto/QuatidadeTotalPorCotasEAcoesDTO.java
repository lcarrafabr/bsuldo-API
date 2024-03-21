package com.carrafasoft.bsuldo.api.model.rendavariavel.dto;

import java.math.BigDecimal;

public class QuatidadeTotalPorCotasEAcoesDTO {

    private String ticker;
    private Long quantidade_cotas;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Long getQuantidade_cotas() {
        return quantidade_cotas;
    }

    public void setQuantidade_cotas(Long quantidade_cotas) {
        this.quantidade_cotas = quantidade_cotas;
    }
}

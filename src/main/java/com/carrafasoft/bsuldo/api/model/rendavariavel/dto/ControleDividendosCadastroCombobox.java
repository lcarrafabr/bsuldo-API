package com.carrafasoft.bsuldo.api.model.rendavariavel.dto;

import java.math.BigDecimal;

public class ControleDividendosCadastroCombobox {

    private Long produtoId;
    private String tipoProdutoEnum;
    private String ticker;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getTipoProdutoEnum() {
        return tipoProdutoEnum;
    }

    public void setTipoProdutoEnum(String tipoProdutoEnum) {
        this.tipoProdutoEnum = tipoProdutoEnum;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}

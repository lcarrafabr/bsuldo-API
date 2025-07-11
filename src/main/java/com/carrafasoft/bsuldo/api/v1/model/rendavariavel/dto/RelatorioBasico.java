package com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto;

import java.math.BigDecimal;

public class RelatorioBasico {

    private String tipoProdutoEnum;
    private String ticker;
    private BigDecimal  precoUnitarioCota;
    private BigDecimal quantidadeCotas;
    private BigDecimal valorInvestido;
    private BigDecimal mediaInvestida;
    private BigDecimal percentualValorInvestido;


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

    public BigDecimal getPrecoUnitarioCota() {
        return precoUnitarioCota;
    }

    public void setPrecoUnitarioCota(BigDecimal precoUnitarioCota) {
        this.precoUnitarioCota = precoUnitarioCota;
    }

    public BigDecimal getQuantidadeCotas() {
        return quantidadeCotas;
    }

    public void setQuantidadeCotas(BigDecimal quantidadeCotas) {
        this.quantidadeCotas = quantidadeCotas;
    }

    public BigDecimal getValorInvestido() {
        return valorInvestido;
    }

    public void setValorInvestido(BigDecimal valorInvestido) {
        this.valorInvestido = valorInvestido;
    }

    public BigDecimal getMediaInvestida() {
        return mediaInvestida;
    }

    public void setMediaInvestida(BigDecimal mediaInvestida) {
        this.mediaInvestida = mediaInvestida;
    }

    public BigDecimal getPercentualValorInvestido() {
        return percentualValorInvestido;
    }

    public void setPercentualValorInvestido(BigDecimal percentualValorInvestido) {
        this.percentualValorInvestido = percentualValorInvestido;
    }
}

package com.carrafasoft.bsuldo.api.model.rendavariavel.dto;

import com.carrafasoft.bsuldo.api.enums.TipoAtivoEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

public class RelatorioBasico {

//    @Enumerated(EnumType.STRING)
//    private TipoAtivoEnum tipoProdutoEnum;

    private String tipoProdutoEnum;
    private String ticker;

    private BigDecimal  precoUnitarioCota;
    private BigDecimal quantidadeCotas;


    private BigDecimal valorInvestido;
    private BigDecimal mediaInvestida;

//    public TipoAtivoEnum getTipoProdutoEnum() {
//        return tipoProdutoEnum;
//    }
//
//    public void setTipoProdutoEnum(TipoAtivoEnum tipoProdutoEnum) {
//        this.tipoProdutoEnum = tipoProdutoEnum;
//    }


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
}

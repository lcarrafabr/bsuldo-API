package com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto;

import java.math.BigDecimal;

public class RelatorioCompletoRendaVariavel {

    private String tipoProdutoEnum;

    private String ticker;

    private BigDecimal precoUnitarioCota;

    private BigDecimal quantidadeCotas;

    private BigDecimal valorInvestido;

    private BigDecimal mediaInvestida;

    private BigDecimal percentualValorInvestido;

    private BigDecimal valorCotacaoAtual; //pega da api o valor atual da cotacao

    private BigDecimal variacao; //calcula a variação conforme preço medio e valor cotacao atuaL

    private BigDecimal saldoVariacao; //calcula a qtd de cotas * preço atual cota

    private BigDecimal ganhoPerdaProjetiva;

    private String logoUrl;

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

    public BigDecimal getValorCotacaoAtual() {
        return valorCotacaoAtual;
    }

    public void setValorCotacaoAtual(BigDecimal valorCotacaoAtual) {
        this.valorCotacaoAtual = valorCotacaoAtual;
    }

    public BigDecimal getVariacao() {
        return variacao;
    }

    public void setVariacao(BigDecimal variacao) {
        this.variacao = variacao;
    }

    public BigDecimal getSaldoVariacao() {
        return saldoVariacao;
    }

    public void setSaldoVariacao(BigDecimal saldoVariacao) {
        this.saldoVariacao = saldoVariacao;
    }

    public BigDecimal getGanhoPerdaProjetiva() {
        return ganhoPerdaProjetiva;
    }

    public void setGanhoPerdaProjetiva(BigDecimal ganhoPerdaProjetiva) {
        this.ganhoPerdaProjetiva = ganhoPerdaProjetiva;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}

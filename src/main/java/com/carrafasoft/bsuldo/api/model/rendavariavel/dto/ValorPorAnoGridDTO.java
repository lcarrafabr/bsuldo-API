package com.carrafasoft.bsuldo.api.model.rendavariavel.dto;

import java.math.BigDecimal;

public class ValorPorAnoGridDTO {

    private BigDecimal Jan;
    private BigDecimal Fev;
    private BigDecimal Mar;
    private BigDecimal Abr;
    private BigDecimal Mai;
    private BigDecimal Jun;
    private BigDecimal Jul;
    private BigDecimal Ago;
    private BigDecimal Set;
    private BigDecimal Out;
    private BigDecimal Nov;
    private BigDecimal Dez;
    private BigDecimal total;

    public BigDecimal getJan() {
        return Jan;
    }

    public ValorPorAnoGridDTO() {
        this.Jan = BigDecimal.ZERO;
        this.Fev = BigDecimal.ZERO;
        this.Mar = BigDecimal.ZERO;
        this.Abr = BigDecimal.ZERO;
        this.Mai = BigDecimal.ZERO;
        this.Jun = BigDecimal.ZERO;
        this.Jul = BigDecimal.ZERO;
        this.Ago = BigDecimal.ZERO;
        this.Set = BigDecimal.ZERO;
        this.Out = BigDecimal.ZERO;
        this.Nov = BigDecimal.ZERO;
        this.Dez = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }

    public void setJan(BigDecimal jan) {
        Jan = jan;
    }

    public BigDecimal getFev() {
        return Fev;
    }

    public void setFev(BigDecimal fev) {
        Fev = fev;
    }

    public BigDecimal getMar() {
        return Mar;
    }

    public void setMar(BigDecimal mar) {
        Mar = mar;
    }

    public BigDecimal getAbr() {
        return Abr;
    }

    public void setAbr(BigDecimal abr) {
        Abr = abr;
    }

    public BigDecimal getMai() {
        return Mai;
    }

    public void setMai(BigDecimal mai) {
        Mai = mai;
    }

    public BigDecimal getJun() {
        return Jun;
    }

    public void setJun(BigDecimal jun) {
        Jun = jun;
    }

    public BigDecimal getJul() {
        return Jul;
    }

    public void setJul(BigDecimal jul) {
        Jul = jul;
    }

    public BigDecimal getAgo() {
        return Ago;
    }

    public void setAgo(BigDecimal ago) {
        Ago = ago;
    }

    public BigDecimal getSet() {
        return Set;
    }

    public void setSet(BigDecimal set) {
        Set = set;
    }

    public BigDecimal getOut() {
        return Out;
    }

    public void setOut(BigDecimal out) {
        Out = out;
    }

    public BigDecimal getNov() {
        return Nov;
    }

    public void setNov(BigDecimal nov) {
        Nov = nov;
    }

    public BigDecimal getDez() {
        return Dez;
    }

    public void setDez(BigDecimal dez) {
        Dez = dez;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}

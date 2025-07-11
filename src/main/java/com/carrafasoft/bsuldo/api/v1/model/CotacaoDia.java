package com.carrafasoft.bsuldo.api.v1.model;

import java.math.BigDecimal;

public class CotacaoDia {

    private String stock;
    private String name;
    private BigDecimal close;
    private BigDecimal change;
    private Long volume;
    private BigDecimal market_cap;
    private String logo;
    private String sector;

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public BigDecimal getMarket_cap() {
        return market_cap;
    }

    public void setMarket_cap(BigDecimal market_cap) {
        this.market_cap = market_cap;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}

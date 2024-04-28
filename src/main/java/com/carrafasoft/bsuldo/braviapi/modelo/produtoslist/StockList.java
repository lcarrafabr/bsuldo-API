package com.carrafasoft.bsuldo.braviapi.modelo.produtoslist;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class StockList {

    private String stock;
    private String name;
    private BigDecimal close;
    private BigDecimal change;
    private BigDecimal volume;
    private BigDecimal market_cap;
    private String logo;
    private String sector;
    private String type;
}

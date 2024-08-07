package com.carrafasoft.bsuldo.api.model.reports;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GridProventosRecebidosEFuturos {

    private Integer ano;
    private BigDecimal jan;
    private BigDecimal fev;
    private BigDecimal mar;
    private BigDecimal abr;
    private BigDecimal mai;
    private BigDecimal jun;
    private BigDecimal jul;
    private BigDecimal ago;
    private BigDecimal setembro;
    private BigDecimal outubro;
    private BigDecimal nov;
    private BigDecimal dez;
    private BigDecimal total;
    private BigDecimal media;
}

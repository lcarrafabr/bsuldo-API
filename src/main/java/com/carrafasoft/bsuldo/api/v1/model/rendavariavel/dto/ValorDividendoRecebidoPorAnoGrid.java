package com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class ValorDividendoRecebidoPorAnoGrid {

    private String ano;
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

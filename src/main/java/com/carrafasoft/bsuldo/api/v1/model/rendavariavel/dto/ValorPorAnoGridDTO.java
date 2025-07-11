package com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
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


}

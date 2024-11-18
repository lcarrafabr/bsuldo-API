package com.carrafasoft.bsuldo.api.model.reports;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GraficoBarrasReceitaDespesaPorAno {

    private Integer ano;
    private BigDecimal despesa;
    private BigDecimal receita;
}

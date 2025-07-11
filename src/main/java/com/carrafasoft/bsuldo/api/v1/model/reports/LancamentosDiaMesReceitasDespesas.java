package com.carrafasoft.bsuldo.api.v1.model.reports;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LancamentosDiaMesReceitasDespesas {

    private Integer dia;
    private BigDecimal lancDespesa;
    private BigDecimal lancReceita;
}

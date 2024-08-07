package com.carrafasoft.bsuldo.api.model.rendavariavel.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RelatorioSetores {

    private String nomeSetor;
    private BigDecimal valorInvestido;
    private BigDecimal percentual;
}

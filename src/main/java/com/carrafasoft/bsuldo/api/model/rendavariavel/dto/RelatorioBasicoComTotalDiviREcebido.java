package com.carrafasoft.bsuldo.api.model.rendavariavel.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RelatorioBasicoComTotalDiviREcebido {

    private String tipoProdutoEnum;
    private String ticker;
    private BigDecimal  precoUnitarioCota;
    private BigDecimal quantidadeCotas;
    private BigDecimal valorInvestido;

    private BigDecimal totalDividendosRecebido;
    private BigDecimal mediaInvestida;
    private BigDecimal percentualValorInvestido;
}

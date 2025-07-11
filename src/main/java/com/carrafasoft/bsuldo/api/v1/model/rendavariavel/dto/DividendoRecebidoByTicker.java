package com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DividendoRecebidoByTicker {

    private String ticker;
    private BigDecimal valorRecebido;
}

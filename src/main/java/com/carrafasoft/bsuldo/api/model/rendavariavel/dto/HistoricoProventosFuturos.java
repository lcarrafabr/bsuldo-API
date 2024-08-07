package com.carrafasoft.bsuldo.api.model.rendavariavel.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class HistoricoProventosFuturos {

    private String tipo_ativo_enum;
    private String logo_url;
    private String ticker;
    private String tipo_dividendo_enum;
    private int mes;
    private int ano;
    private LocalDate data_com;
    private LocalDate data_pagamento;
    private BigDecimal qtdCotas;
    private BigDecimal valor_por_cota;
    private BigDecimal dividendo_a_receber;
}

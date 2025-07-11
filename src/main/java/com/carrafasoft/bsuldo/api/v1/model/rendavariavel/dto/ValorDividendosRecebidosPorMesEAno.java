package com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ValorDividendosRecebidosPorMesEAno {

    private LocalDate dataReferencia;
    private BigDecimal valorRecebido;

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(LocalDate dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(BigDecimal valorRecebido) {
        this.valorRecebido = valorRecebido;
    }
}

package com.carrafasoft.bsuldo.api.v1.model.reports.criptos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CryptoGradeSaldoVariacao {

    private String moeda;
    private BigDecimal quantidadeTotal;
    private BigDecimal valorInvestidoTotal;
    private BigDecimal mediaInvestida;
    private BigDecimal percentualCarteira;
    private BigDecimal cotacaoAtual;
    private BigDecimal projetivo;
    private BigDecimal saldoVariacao;
    private BigDecimal precoMedio;
    private BigDecimal variacao;
}

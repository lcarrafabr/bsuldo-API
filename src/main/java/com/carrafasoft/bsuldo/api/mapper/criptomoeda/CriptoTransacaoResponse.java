package com.carrafasoft.bsuldo.api.mapper.criptomoeda;

import com.carrafasoft.bsuldo.api.enums.MoedaEnum;
import com.carrafasoft.bsuldo.api.enums.TipoOrdemCriptoEnum;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CriptoTransacaoResponse {

    private String codigoCriptoTransacao;
    private MoedaEnum moeda;
    private BigDecimal quantidade;
    private BigDecimal precoNegociacao;
    private BigDecimal valorInvestido;
    private TipoOrdemCriptoEnum tipoOrdemCripto;
    private LocalDateTime dataTransacao;

    private WalletResponse wallet;
}

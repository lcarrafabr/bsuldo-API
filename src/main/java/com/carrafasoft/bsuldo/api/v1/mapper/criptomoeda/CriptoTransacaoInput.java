package com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda;

import com.carrafasoft.bsuldo.api.v1.enums.MoedaEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemCriptoEnum;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@Builder
public class CriptoTransacaoInput {

    @NotNull
    private MoedaEnum moeda;

    @NotNull
    private BigDecimal quantidade;

    @NotNull
    @PositiveOrZero
    private BigDecimal precoNegociacao;

    @NotNull
    private BigDecimal valorInvestido;

    @NotNull
    private TipoOrdemCriptoEnum tipoOrdemCripto;

    @NotNull
    private WalletResponse wallet;
}

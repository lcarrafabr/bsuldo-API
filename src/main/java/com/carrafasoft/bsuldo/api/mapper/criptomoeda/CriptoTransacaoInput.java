package com.carrafasoft.bsuldo.api.mapper.criptomoeda;

import com.carrafasoft.bsuldo.api.enums.MoedaEnum;
import com.carrafasoft.bsuldo.api.enums.TipoOrdemCriptoEnum;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

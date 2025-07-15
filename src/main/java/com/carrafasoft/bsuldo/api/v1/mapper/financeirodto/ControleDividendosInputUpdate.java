package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import com.carrafasoft.bsuldo.api.v1.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoDivRecebimentoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoDividendoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControleDividendosInputUpdate {

    private String codigoControleDividendo;

    @NotNull
    private TipoAtivoEnum tipoAtivoEnum;

    @NotNull
    private TipoDivRecebimentoEnum tipoDivRecebimentoEnum;

    @NotNull
    private LocalDate dataReferencia;

    @NotNull
    private TipoDividendoEnum tipoDividendoEnum;

    @NotNull
    private LocalDate dataCom;

    @NotNull
    private LocalDate dataPagamento;

    @NotNull
    private BigDecimal valorPorCota;

    @NotNull
    private BigDecimal valorRecebido;

    private Boolean divUtilizado;

    private Integer qtdCota;

    @NotNull
    private ProdutoRVResponseRepresentation produtosRendaVariavel;
}

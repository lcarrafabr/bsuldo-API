package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import com.carrafasoft.bsuldo.api.v1.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemManualAutoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemRendaVariavelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdemDeCompraInputReppresentation {

    @NotNull
    private TipoAtivoEnum tipoAtivoEnum;

    @NotNull
    private TipoOrdemRendaVariavelEnum tipoOrdemRendaVariavelEnum;

    @NotNull
    private LocalDate dataTransacao;

    private LocalDate dataExecucao;

    @Positive
    @NotNull
    private Long quantidadeCotas;

    @NotNull
    private BigDecimal precoUnitarioCota;

    private BigDecimal valorInvestido;

    private String desdobroAgrupado;

    private LocalDate dataDesdobroAgrupamento;

    private TipoOrdemManualAutoEnum tipoOrdemManualAutoEnum;

    @NotNull
    private ProdutoRVResponseRepresentation produtoRendaVariavel;
}

package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import com.carrafasoft.bsuldo.api.v1.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemManualAutoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemRendaVariavelEnum;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdemDeCompraResponseRepresentation {

    private String codigoOrdemDeComppra;
    private TipoAtivoEnum tipoAtivoEnum;
    private TipoOrdemRendaVariavelEnum tipoOrdemRendaVariavelEnum;
    private LocalDate dataTransacao;
    private LocalDate dataExecucao;
    private Long quantidadeCotas;
    private BigDecimal precoUnitarioCota;
    private BigDecimal valorInvestido;
    private String desdobroAgrupado;
    private LocalDate dataDesdobroAgrupamento;
    private TipoOrdemManualAutoEnum tipoOrdemManualAutoEnum;
    private ProdutoRVResponseRepresentation produtoRendaVariavel;
}

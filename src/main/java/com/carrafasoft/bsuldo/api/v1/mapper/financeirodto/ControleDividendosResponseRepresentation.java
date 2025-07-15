package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import com.carrafasoft.bsuldo.api.v1.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoDivRecebimentoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoDividendoEnum;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
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
public class ControleDividendosResponseRepresentation {

    private String codigoControleDividendo;
    private TipoAtivoEnum tipoAtivoEnum;
    private TipoDivRecebimentoEnum tipoDivRecebimentoEnum;
    private LocalDate dataReferencia;
    private TipoDividendoEnum tipoDividendoEnum;
    private LocalDate dataCom;
    private LocalDate dataPagamento;
    private BigDecimal valorPorCota;
    private BigDecimal valorRecebido;
    private Boolean divUtilizado;
    private Integer qtdCota;
    private ProdutoRVResponseRepresentation produtosRendaVariavel;
}

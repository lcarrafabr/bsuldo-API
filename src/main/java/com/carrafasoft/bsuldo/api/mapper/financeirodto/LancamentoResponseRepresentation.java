package com.carrafasoft.bsuldo.api.mapper.financeirodto;

import com.carrafasoft.bsuldo.api.enums.SituacaoEnum;
import com.carrafasoft.bsuldo.api.enums.TipoLancamento;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LancamentoResponseRepresentation {

    @NotBlank
    private String codigoLancamento;

    @NotNull
    @PositiveOrZero
    private BigDecimal valor;

    @NotNull
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @NotBlank
    private String descricao;

    @Enumerated(EnumType.STRING)
    private SituacaoEnum situacao;

    @NotNull
    private Boolean parcelado;

    @NotNull
    private Integer quantidadeParcelas;

    @NotNull
    private Integer numeroParcela;

    private String chavePesquisa;

    @NotNull
    private Boolean lancRecorrente;

    @Enumerated(EnumType.STRING)
    private TipoLancamento tipoLancamento;

    @NotNull
    private CategoriaResponseRepresentation categoria;

    @NotNull
    private MetodoCobrancaResponseRepresentation metodoDeCobranca;

    private BancoResponseRepresentation banco;
}

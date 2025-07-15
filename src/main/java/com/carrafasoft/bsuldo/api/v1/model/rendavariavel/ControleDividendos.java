package com.carrafasoft.bsuldo.api.v1.model.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoDivRecebimentoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoDividendoEnum;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "controle_dividendos")
public class ControleDividendos {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "controle_dividendos_id")
    private Long controleDividendoId;

    @Column(name = "codigo_controle_dividendo", length = 36, updatable = false)
    private String codigoControleDividendo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ativo_enum")
    private TipoAtivoEnum tipoAtivoEnum;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_div_recebimento_enum")
    private TipoDivRecebimentoEnum tipoDivRecebimentoEnum;

    @NotNull
    @Column(name = "data_referencia")
    private LocalDate dataReferencia;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dividendo_enum")
    private TipoDividendoEnum tipoDividendoEnum;

    @NotNull
    @Column(name = "data_com")
    private LocalDate dataCom;

    @NotNull
    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @NotNull
    @Column(name = "valor_por_cota", precision = 19, scale = 8)
    private BigDecimal valorPorCota;

    @Column(name = "valor_recebido")
    private BigDecimal valorRecebido;

    @NotNull
    @Column(name = "div_utilizado")
    private Boolean divUtilizado;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @Transient
    private Integer qtdCota;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private ProdutosRendaVariavel produtosRendaVariavel;

    @PrePersist
    public void aoCadastrar() {

        ajustaValorDivRecebido();
        setCodigoControleDividendo(UUID.randomUUID().toString());
    }

    @PreUpdate
    public void aoAtualizar() {

        ajustaValorDivRecebido();
    }

    private void ajustaValorDivRecebido() {

        if(valorRecebido == null || valorRecebido == BigDecimal.ZERO) {
            valorRecebido = BigDecimal.ZERO;
            tipoDivRecebimentoEnum = TipoDivRecebimentoEnum.A_RECEBER;
        }
        if(valorRecebido != null && valorRecebido != BigDecimal.ZERO) {
            tipoDivRecebimentoEnum = TipoDivRecebimentoEnum.RECEBIDO;
        }
    }
}

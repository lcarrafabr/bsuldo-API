package com.carrafasoft.bsuldo.api.v1.model.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemManualAutoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemRendaVariavelEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "ordens_de_compra")
public class OrdensDeCompra {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordem_de_compra_id")
    private Long ordemDeCompraId;

    @Column(name = "codigo_ordem_de_compra", length = 36, updatable = false)
    private String codigoOrdemDeComppra;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_produto_enum", length = 45)
    private TipoAtivoEnum tipoAtivoEnum;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ordem_renda_variavel_enum", length = 45)
    private TipoOrdemRendaVariavelEnum tipoOrdemRendaVariavelEnum;

    @NotNull
    @Column(name = "data_transacao")
    private LocalDate dataTransacao;

    @Column(name = "data_execucao")
    private LocalDate dataExecucao;

    @NotNull
    @Column(name = "quantidade_cotas")
    private Long quantidadeCotas;

    @NotNull
    @Column(name = "preco_unitario_cota")
    private BigDecimal precoUnitarioCota;

    //@NotNull
    @Column(name = "valor_investido")
    private BigDecimal valorInvestido;

    @Column(name = "desdobro_agrupado")
    private String desdobroAgrupado;

    @Column(name = "data_desdobro_agrupamento")
    private LocalDate dataDesdobroAgrupamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ordem_manual_auto_enum", length = 45)
    private TipoOrdemManualAutoEnum tipoOrdemManualAutoEnum;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private ProdutosRendaVariavel produtoRendaVariavel;

    @PrePersist
    public void aoCadastrar() {
        verificaSinalOperacao();
        setCodigoOrdemDeComppra(UUID.randomUUID().toString());
    }

    @PreUpdate
    public void aoAtualizar() {
        verificaSinalOperacao();
    }

    private void verificaSinalOperacao() {

        if (tipoOrdemRendaVariavelEnum.equals(TipoOrdemRendaVariavelEnum.VENDA)) {
            valorInvestido = valorInvestido.abs().multiply(new BigDecimal("-1"));
            quantidadeCotas = -Math.abs(quantidadeCotas);

        } else if (tipoOrdemRendaVariavelEnum.equals(TipoOrdemRendaVariavelEnum.COMPRA)) {
            valorInvestido = valorInvestido.abs();
            quantidadeCotas = Math.abs(quantidadeCotas);
        }
    }
}

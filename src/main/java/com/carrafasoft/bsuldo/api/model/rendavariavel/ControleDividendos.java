package com.carrafasoft.bsuldo.api.model.rendavariavel;

import com.carrafasoft.bsuldo.api.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.enums.TipoDivRecebimentoEnum;
import com.carrafasoft.bsuldo.api.enums.TipoDividendoEnum;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "controle_dividendos")
public class ControleDividendos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "controle_dividendos_id")
    private Long controleDividendoId;

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


//    public Long getControleDividendoId() {
//        return controleDividendoId;
//    }
//
//    public void setControleDividendoId(Long controleDividendoId) {
//        this.controleDividendoId = controleDividendoId;
//    }
//
//    public TipoAtivoEnum getTipoAtivoEnum() {
//        return tipoAtivoEnum;
//    }
//
//    public void setTipoAtivoEnum(TipoAtivoEnum tipoAtivoEnum) {
//        this.tipoAtivoEnum = tipoAtivoEnum;
//    }
//
//    public TipoDivRecebimentoEnum getTipoDivRecebimentoEnum() {
//        return tipoDivRecebimentoEnum;
//    }
//
//    public void setTipoDivRecebimentoEnum(TipoDivRecebimentoEnum tipoDivRecebimentoEnum) {
//        this.tipoDivRecebimentoEnum = tipoDivRecebimentoEnum;
//    }
//
//    public LocalDate getDataReferencia() {
//        return dataReferencia;
//    }
//
//    public void setDataReferencia(LocalDate dataReferencia) {
//        this.dataReferencia = dataReferencia;
//    }
//
//    public TipoDividendoEnum getTipoDividendoEnum() {
//        return tipoDividendoEnum;
//    }
//
//    public void setTipoDividendoEnum(TipoDividendoEnum tipoDividendoEnum) {
//        this.tipoDividendoEnum = tipoDividendoEnum;
//    }
//
//    public LocalDate getDataCom() {
//        return dataCom;
//    }
//
//    public void setDataCom(LocalDate dataCom) {
//        this.dataCom = dataCom;
//    }
//
//    public LocalDate getDataPagamento() {
//        return dataPagamento;
//    }
//
//    public void setDataPagamento(LocalDate dataPagamento) {
//        this.dataPagamento = dataPagamento;
//    }
//
//    public BigDecimal getValorPorCota() {
//        return valorPorCota;
//    }
//
//    public void setValorPorCota(BigDecimal valorPorCota) {
//        this.valorPorCota = valorPorCota;
//    }
//
//    public BigDecimal getValorRecebido() {
//        return valorRecebido;
//    }
//
//    public void setValorRecebido(BigDecimal valorRecebido) {
//        this.valorRecebido = valorRecebido;
//    }
//
//    public Boolean getDivUtilizado() {
//        return divUtilizado;
//    }
//
//    public void setDivUtilizado(Boolean divUtilizado) {
//        this.divUtilizado = divUtilizado;
//    }
//
//    public Pessoas getPessoa() {
//        return pessoa;
//    }
//
//    public void setPessoa(Pessoas pessoa) {
//        this.pessoa = pessoa;
//    }
//
//    public ProdutosRendaVariavel getProdutosRendaVariavel() {
//        return produtosRendaVariavel;
//    }
//
//    public void setProdutosRendaVariavel(ProdutosRendaVariavel produtosRendaVariavel) {
//        this.produtosRendaVariavel = produtosRendaVariavel;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        ControleDividendos that = (ControleDividendos) o;
//
//        return controleDividendoId.equals(that.controleDividendoId);
//    }
//
//    @Override
//    public int hashCode() {
//        return controleDividendoId.hashCode();
//    }

    @PrePersist
    public void aoCadastrar() {

        ajustaValorDivRecebido();
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

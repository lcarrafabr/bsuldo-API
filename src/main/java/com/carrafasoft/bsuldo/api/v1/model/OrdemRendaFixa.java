package com.carrafasoft.bsuldo.api.v1.model;

import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemRendaFixaEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "ordem_renda_fixa")
public class OrdemRendaFixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordem_renda_fixa_id")
    private Long ordemRendaFixaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ordem_renda_fixa_enum", length = 50)
    private TipoOrdemRendaFixaEnum tipoOrdemRendaFixaEnum;

    @NotNull
    @Column(name = "data_transacao")
    private LocalDate dataTransacao;

    @NotNull
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @NotNull
    private BigDecimal valorTransacao;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "produto_renda_fixa_id")
    private ProdutoRendaFixa produtoRendaFixa;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdemRendaFixa)) return false;
        OrdemRendaFixa that = (OrdemRendaFixa) o;
        return Objects.equals(ordemRendaFixaId, that.ordemRendaFixaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordemRendaFixaId);
    }

    public Long getOrdemRendaFixaId() {
        return ordemRendaFixaId;
    }

    public void setOrdemRendaFixaId(Long ordemRendaFixaId) {
        this.ordemRendaFixaId = ordemRendaFixaId;
    }

    public TipoOrdemRendaFixaEnum getTipoOrdemRendaFixaEnum() {
        return tipoOrdemRendaFixaEnum;
    }

    public void setTipoOrdemRendaFixaEnum(TipoOrdemRendaFixaEnum tipoOrdemRendaFixaEnum) {
        this.tipoOrdemRendaFixaEnum = tipoOrdemRendaFixaEnum;
    }

    public LocalDate getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDate dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getValorTransacao() {
        return valorTransacao;
    }

    public void setValorTransacao(BigDecimal valorTransacao) {
        this.valorTransacao = valorTransacao;
    }

    public Pessoas getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoas pessoa) {
        this.pessoa = pessoa;
    }

    public ProdutoRendaFixa getProdutoRendaFixa() {
        return produtoRendaFixa;
    }

    public void setProdutoRendaFixa(ProdutoRendaFixa produtoRendaFixa) {
        this.produtoRendaFixa = produtoRendaFixa;
    }

    @PrePersist
    public void aoCadastrar() {
        verificaAplicacao();
    }

    @PreUpdate
    public void aoAtualizar() {
        verificaAplicacao();
    }

    public void verificaAplicacao(){

        if(valorTransacao.compareTo(BigDecimal.ZERO) > 0
                && tipoOrdemRendaFixaEnum.equals(TipoOrdemRendaFixaEnum.RESGATE)) {

            valorTransacao = valorTransacao.multiply(new BigDecimal("-1"));
        }

        if(valorTransacao.compareTo(BigDecimal.ZERO) < 0
            && tipoOrdemRendaFixaEnum.equals(TipoOrdemRendaFixaEnum.APLICACAO)) {

            valorTransacao = valorTransacao.multiply(new BigDecimal("-1"));
        }
    }
}

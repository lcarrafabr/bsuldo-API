package com.carrafasoft.bsuldo.api.model.rendavariavel;

import com.carrafasoft.bsuldo.api.enums.TipoOrdemRendaVariavelEnum;
import com.carrafasoft.bsuldo.api.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.model.Pessoas;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ordens_de_compra")
public class OrdensDeCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordem_de_compra_id")
    private Long ordemDeCompraId;

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

    @NotNull
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private ProdutosRendaVariavel produtoRendaVariavel;

    public Long getOrdemDeCompraId() {
        return ordemDeCompraId;
    }

    public void setOrdemDeCompraId(Long ordemDeCompraId) {
        this.ordemDeCompraId = ordemDeCompraId;
    }

    public TipoAtivoEnum getTipoProdutoEnum() {
        return tipoAtivoEnum;
    }

    public void setTipoProdutoEnum(TipoAtivoEnum tipoAtivoEnum) {
        this.tipoAtivoEnum = tipoAtivoEnum;
    }

    public TipoOrdemRendaVariavelEnum getTipoOrdemRendaVariavelEnum() {
        return tipoOrdemRendaVariavelEnum;
    }

    public void setTipoOrdemRendaVariavelEnum(TipoOrdemRendaVariavelEnum tipoOrdemRendaVariavelEnum) {
        this.tipoOrdemRendaVariavelEnum = tipoOrdemRendaVariavelEnum;
    }

    public LocalDate getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDate dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public LocalDate getDataExecucao() {
        return dataExecucao;
    }

    public void setDataExecucao(LocalDate dataExecucao) {
        this.dataExecucao = dataExecucao;
    }

    public Long getQuantidadeCotas() {
        return quantidadeCotas;
    }

    public void setQuantidadeCotas(Long quantidadeCotas) {
        this.quantidadeCotas = quantidadeCotas;
    }

    public BigDecimal getPrecoUnitarioCota() {
        return precoUnitarioCota;
    }

    public void setPrecoUnitarioCota(BigDecimal precoUnitarioCota) {
        this.precoUnitarioCota = precoUnitarioCota;
    }

    public BigDecimal getValorInvestido() {
        return valorInvestido;
    }

    public void setValorInvestido(BigDecimal valorInvestido) {
        this.valorInvestido = valorInvestido;
    }

    public Pessoas getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoas pessoa) {
        this.pessoa = pessoa;
    }

    public ProdutosRendaVariavel getProdutoRendaVariavel() {
        return produtoRendaVariavel;
    }

    public void setProdutoRendaVariavel(ProdutosRendaVariavel produtoRendaVariavel) {
        this.produtoRendaVariavel = produtoRendaVariavel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrdensDeCompra that = (OrdensDeCompra) o;

        return ordemDeCompraId.equals(that.ordemDeCompraId);
    }

    @Override
    public int hashCode() {
        return ordemDeCompraId.hashCode();
    }
}

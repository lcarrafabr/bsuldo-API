package com.carrafasoft.bsuldo.api.model;

import com.carrafasoft.bsuldo.api.enums.GrauDeRiscoEnum;
import com.carrafasoft.bsuldo.api.enums.ImpostoDeRendaEnum;
import com.carrafasoft.bsuldo.api.enums.LiquidezEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "produtos_renda_fixa")
public class ProdutoRendaFixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produto_renda_fixa_id")
    private Long produtoRendaFixaId;

    @NotNull
    @Column(name = "nome_produto", length = 200)
    private String nomeProduto;

    @NotNull
    @Column(length = 20)
    private String sigla;

    @NotNull
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @NotNull
    @Column(name = "tem_imposto")
    private Boolean temImposto;

    @NotNull
    @Column(name = "valor_imposto")
    private BigDecimal valorImposto;

    @Enumerated(EnumType.STRING)
    @Column(name = "grau_de_risco", length = 50)
    private GrauDeRiscoEnum grauDeRiscoEnum;

    @Enumerated(EnumType.STRING)
    @Column(name = "liquidez", length = 50)
    private LiquidezEnum liquidezEnum;

    @Enumerated(EnumType.STRING)
    @Column(name = "imposto_de_renda_enum")
    private ImpostoDeRendaEnum impostoDeRendaEnum;

    @NotNull
    @Column(name = "valor_minimo")
    private BigDecimal valorMinimo;

    @NotNull
    @Column(length = 200)
    private String rentabilidade;

    @NotNull
    private Boolean status;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "emissor_id")
    private Emissores emissores;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProdutoRendaFixa)) return false;
        ProdutoRendaFixa that = (ProdutoRendaFixa) o;
        return Objects.equals(produtoRendaFixaId, that.produtoRendaFixaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produtoRendaFixaId);
    }

    public Long getProdutoRendaFixaId() {
        return produtoRendaFixaId;
    }

    public void setProdutoRendaFixaId(Long produtoRendaFixaId) {
        this.produtoRendaFixaId = produtoRendaFixaId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Boolean getTemImposto() {
        return temImposto;
    }

    public void setTemImposto(Boolean temImposto) {
        this.temImposto = temImposto;
    }

    public BigDecimal getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public GrauDeRiscoEnum getGrauDeRiscoEnum() {
        return grauDeRiscoEnum;
    }

    public void setGrauDeRiscoEnum(GrauDeRiscoEnum grauDeRiscoEnum) {
        this.grauDeRiscoEnum = grauDeRiscoEnum;
    }

    public LiquidezEnum getLiquidezEnum() {
        return liquidezEnum;
    }

    public void setLiquidezEnum(LiquidezEnum liquidezEnum) {
        this.liquidezEnum = liquidezEnum;
    }

    public ImpostoDeRendaEnum getImpostoDeRendaEnum() {
        return impostoDeRendaEnum;
    }

    public void setImpostoDeRendaEnum(ImpostoDeRendaEnum impostoDeRendaEnum) {
        this.impostoDeRendaEnum = impostoDeRendaEnum;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public String getRentabilidade() {
        return rentabilidade;
    }

    public void setRentabilidade(String rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public Emissores getEmissores() {
        return emissores;
    }

    public void setEmissores(Emissores emissores) {
        this.emissores = emissores;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @PrePersist
    public void aoCadastrar(){
        toUpperCase();
    }

    @PreUpdate
    public void aoAtualizar() {
        toUpperCase();
    }

    private void toUpperCase() {

        nomeProduto = nomeProduto.toUpperCase();
        sigla = sigla.toUpperCase();
    }
}

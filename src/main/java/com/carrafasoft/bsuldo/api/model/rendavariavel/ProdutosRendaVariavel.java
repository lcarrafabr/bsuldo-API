package com.carrafasoft.bsuldo.api.model.rendavariavel;

import com.carrafasoft.bsuldo.api.model.Emissores;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "produtos_renda_variavel")
public class ProdutosRendaVariavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produto_id")
    private Long produtoId;

    @NotNull
    @Column(name = "long_name", length = 255)
    private String longName;

    @NotNull
    @Column(name = "short_name", length = 100)
    private String shortName;

    @NotNull
    @Column(length = 10)
    private String ticker;

    @NotNull
    @Column(length = 5)
    private String currency;

    @Column(length = 14)
    private String cnpj;

    @NotNull
    @Column(name = "gera_dividendos")
    private Boolean geraDividendos;

    private Boolean status;

    @Column(name = "cotas_emitidas")
    private Long cotasEmitidas;

    @Column(length = 255)
    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "emissor_id")
    private Emissores emissor;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "segmento_id")
    private Segmentos segmento;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "setor_id")
    private Setores setor;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Boolean getGeraDividendos() {
        return geraDividendos;
    }

    public void setGeraDividendos(Boolean geraDividendos) {
        this.geraDividendos = geraDividendos;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getCotasEmitidas() {
        return cotasEmitidas;
    }

    public void setCotasEmitidas(Long cotasEmitidas) {
        this.cotasEmitidas = cotasEmitidas;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Emissores getEmissor() {
        return emissor;
    }

    public void setEmissor(Emissores emissor) {
        this.emissor = emissor;
    }

    public Segmentos getSegmento() {
        return segmento;
    }

    public void setSegmento(Segmentos segmento) {
        this.segmento = segmento;
    }

    public Setores getSetor() {
        return setor;
    }

    public void setSetor(Setores setor) {
        this.setor = setor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProdutosRendaVariavel that = (ProdutosRendaVariavel) o;

        return Objects.equals(produtoId, that.produtoId);
    }

    @Override
    public int hashCode() {
        return produtoId != null ? produtoId.hashCode() : 0;
    }

    @PrePersist
    public void aoCadastrar() {
        toUpperCase();
    }

    @PreUpdate
    public void aoAtualizar() {
        toUpperCase();
    }

    private void toUpperCase() {

        if(StringUtils.hasLength(longName)) {
            longName = longName.toUpperCase().trim();
        }
        if(StringUtils.hasLength(shortName)) {
            shortName = shortName.toUpperCase().trim();
        }
        if(StringUtils.hasLength(ticker)) {
            ticker = ticker.toUpperCase().trim();
        }
        if(StringUtils.hasLength(currency)) {
            currency = currency.toUpperCase().trim();
        }
    }
}

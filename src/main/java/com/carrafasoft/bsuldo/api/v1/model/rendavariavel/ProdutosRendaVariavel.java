package com.carrafasoft.bsuldo.api.v1.model.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.model.Emissores;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;



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

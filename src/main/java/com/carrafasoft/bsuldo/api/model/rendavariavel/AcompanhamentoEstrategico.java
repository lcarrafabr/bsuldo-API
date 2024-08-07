package com.carrafasoft.bsuldo.api.model.rendavariavel;

import com.carrafasoft.bsuldo.api.enums.StatusAcompanhamnetoEnum;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name = "acompanhamneto_estrategico")
public class AcompanhamentoEstrategico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acompanhamento_estrategico_id")
    private Long acompEstrategicoId;

    @NotNull
    @Column(length = 10)
    private String ticker;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDate dataCadastro;

    @NotNull
    @Column(name = "valor_cota")
    private BigDecimal valorCota;

    @NotNull
    @Column(name = "valor_dividendo")
    private BigDecimal valorDividendo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_acompanhamento_enum", length = 30)
    private StatusAcompanhamnetoEnum statusAcompanhamentoEnum;

    @Column(name = "acompanhar_variacao")
    private Boolean acompanharVariacao;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "setor_id")
    private Setores setor;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "segmento_id")
    private Segmentos segmento;


    @PrePersist
    public void aoCadastrar() {
        toUpperCase();
        dataCadastro = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        //statusAcompanhamentoEnum = StatusAcompanhamnetoEnum.EM_ANALISE;
        acompanharVariacao = false;
    }

    @PreUpdate
    public void aoAtualizar() {

        toUpperCase();

        if(statusAcompanhamentoEnum == null) {
            statusAcompanhamentoEnum = StatusAcompanhamnetoEnum.EM_ANALISE;
        }

        if(acompanharVariacao == null) {
            acompanharVariacao = false;
        }

        if(statusAcompanhamentoEnum == StatusAcompanhamnetoEnum.COMPRADO) {
            acompanharVariacao = false;
        }
    }

    private void toUpperCase() {
        ticker = ticker.trim().toUpperCase();
    }
}

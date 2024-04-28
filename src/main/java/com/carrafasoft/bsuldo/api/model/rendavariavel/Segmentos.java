package com.carrafasoft.bsuldo.api.model.rendavariavel;

import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "segmentos")
public class Segmentos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "segmento_id")
    private Long segmentoId;

    @NotNull
    @Column(name = "nome_segmento", length = 100)
    private String nomeSegmento;

    private Boolean status;

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

        if(StringUtils.hasLength(nomeSegmento)) {
            nomeSegmento = nomeSegmento.toUpperCase().trim();
        }
    }
}

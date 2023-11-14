package com.carrafasoft.bsuldo.api.model.rendavariavel;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    public Long getSegmentoId() {
        return segmentoId;
    }

    public void setSegmentoId(Long segmentoId) {
        this.segmentoId = segmentoId;
    }

    public String getNomeSegmento() {
        return nomeSegmento;
    }

    public void setNomeSegmento(String nomeSegmento) {
        this.nomeSegmento = nomeSegmento;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Segmentos segmentos = (Segmentos) o;

        return segmentoId.equals(segmentos.segmentoId);
    }

    @Override
    public int hashCode() {
        return segmentoId.hashCode();
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

        if(StringUtils.hasLength(nomeSegmento)) {
            nomeSegmento = nomeSegmento.toUpperCase().trim();
        }
    }
}

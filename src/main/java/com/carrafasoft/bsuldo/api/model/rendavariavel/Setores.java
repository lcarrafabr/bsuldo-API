package com.carrafasoft.bsuldo.api.model.rendavariavel;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "setores")
public class Setores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setor_id")
    private Long setorId;

    @NotNull
    @Column(name = "nome_setor", length = 150)
    private String nomeSetor;

    private Boolean status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Setores setores = (Setores) o;

        return setorId.equals(setores.setorId);
    }

    public Long getSetorId() {
        return setorId;
    }

    public void setSetorId(Long setorId) {
        this.setorId = setorId;
    }

    public String getNomeSetor() {
        return nomeSetor;
    }

    public void setNomeSetor(String nomeSetor) {
        this.nomeSetor = nomeSetor;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return setorId.hashCode();
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

        if(StringUtils.hasLength(nomeSetor)) {
            nomeSetor = nomeSetor.toUpperCase().trim();
        }
    }
}

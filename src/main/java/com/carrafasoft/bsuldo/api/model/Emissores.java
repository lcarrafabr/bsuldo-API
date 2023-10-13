package com.carrafasoft.bsuldo.api.model;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "emissores")
public class Emissores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emissor_id")
    private Long emissorId;

    @NotNull
    @Column(name = "nome_emissor")
    private String nomeEmissor;

    @NotNull
    private Boolean status;

    @NotNull
    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Emissores)) return false;
        Emissores emissores = (Emissores) o;
        return Objects.equals(emissorId, emissores.emissorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emissorId);
    }

    public Long getEmissorId() {
        return emissorId;
    }

    public void setEmissorId(Long emissorId) {
        this.emissorId = emissorId;
    }

    public String getNomeEmissor() {
        return nomeEmissor;
    }

    public void setNomeEmissor(String nomeEmissor) {
        this.nomeEmissor = nomeEmissor;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
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

        if(StringUtils.hasLength(nomeEmissor)) {

            nomeEmissor = nomeEmissor.toUpperCase();
        }
    }
}

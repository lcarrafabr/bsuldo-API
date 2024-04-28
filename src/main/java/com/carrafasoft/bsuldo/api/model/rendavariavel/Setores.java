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

        if(StringUtils.hasLength(nomeSetor)) {
            nomeSetor = nomeSetor.toUpperCase().trim();
        }
    }
}

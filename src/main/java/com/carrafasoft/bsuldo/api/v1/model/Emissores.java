package com.carrafasoft.bsuldo.api.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
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

        if(StringUtils.hasLength(nomeEmissor)) {

            nomeEmissor = nomeEmissor.toUpperCase();
        }
    }
}

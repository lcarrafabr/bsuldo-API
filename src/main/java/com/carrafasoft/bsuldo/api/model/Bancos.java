package com.carrafasoft.bsuldo.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "bancos")
public class Bancos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banco_id")
    private Long bancoId;

    @NotNull
    @Column(name = "nome_banco", length = 50)
    private String nomeBanco;

    private Boolean status;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @PrePersist
    public void aoCadastrar() {

        toUppercase();
        status = true;
    }

    @PreUpdate
    public void aoAtualizar() {

        toUppercase();
    }

    private void toUppercase() {
        nomeBanco = nomeBanco.trim().toUpperCase();
    }

}



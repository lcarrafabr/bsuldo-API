package com.carrafasoft.bsuldo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@Table(name = "bancos")
public class Bancos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banco_id")
    private Long bancoId;

    @NotBlank
    @Column(name = "nome_banco", length = 50)
    private String nomeBanco;

    private Boolean status;

    @JsonIgnore
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

        if(StringUtils.hasLength(nomeBanco)) {
            nomeBanco = nomeBanco.trim().toUpperCase();
        }
    }

}



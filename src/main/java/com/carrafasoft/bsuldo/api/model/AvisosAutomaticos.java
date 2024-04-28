package com.carrafasoft.bsuldo.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name = "avisos_automaticos")
public class AvisosAutomaticos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aviso_automatico_id")
    private Long avisoAutomaticoId;

    @NotNull
    @Column(length = 100)
    private String titulo;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDate dataCadastro;

    private Boolean visualizado;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @PrePersist
    public void aoCadastrar() {

        toUpperCase();
        dataCadastro = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        visualizado = false;
    }

    private void toUpperCase() {

        titulo = titulo.toUpperCase();
    }
}

package com.carrafasoft.bsuldo.api.v1.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "feriados")
public class Feriados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feriado_id")
    private Long feriadoID;

    @NotNull
    @Column(name = "nome_feriado", length = 60)
    private String nomeFeriado;

    @NotNull
    private Integer dia;

    @NotNull
    private Integer mes;

    @NotNull
    private Boolean status;
}

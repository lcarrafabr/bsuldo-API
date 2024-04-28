package com.carrafasoft.bsuldo.api.model.gerenciamentoapi;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@Table(name = "stock_list")
@Getter
@Setter
public class StoksListModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_produto_id")
    private Long stockId;


    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;

    @NotNull
    @Column(length = 20)
    private String stock;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    private BigDecimal close;

    @NotNull
    private BigDecimal changes;

    @NotNull
    private BigDecimal volume;

    @Column(name = "market_cap")
    private BigDecimal marketCap;

    private String logo;

    private String sector;

    private String type;

    @PrePersist
    public void aoCadastrar() {

        toUpperCase();
        dataAtualizacao = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
    }

    @PreUpdate
    public void aoAtualizar() {

        dataAtualizacao = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        toUpperCase();
    }

    public void toUpperCase() {

        stock = stock.toUpperCase();
        name = name.toUpperCase();
    }
}

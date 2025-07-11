package com.carrafasoft.bsuldo.api.v1.model.criptomoedas;

import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "origens")
public class Origens {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "origem_id")
    private Long origemId;

    @NotBlank
    @Column(name = "codigo_origem", length = 36, updatable = false)
    private String codigoOrigem;

    @NotBlank
    @Column(name = "nome_origem", length = 80)
    private String nomeOrigem;

    private Boolean statusAtivo;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoas;

    @PrePersist
    public void aoCadastrar() {

        toUperCase();
        setDataUltimaAtualizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        setCodigoOrigem(UUID.randomUUID().toString());
        setStatusAtivo(true);
    }

    @PreUpdate
    public void aoAtualizar() {

        toUperCase();
        setDataUltimaAtualizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
    }

    private void toUperCase() {

        setNomeOrigem(nomeOrigem.toUpperCase().trim());
    }
}

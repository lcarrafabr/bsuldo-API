package com.carrafasoft.bsuldo.api.v1.model.criptomoedas;

import com.carrafasoft.bsuldo.api.v1.enums.TipoCarteiraEnum;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "wallets")
public class Wallets {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long walletId;

    @NotBlank
    @Column(name = "codigo_wallet", length = 36, updatable = false)
    private String codigoWallet;

    @NotBlank
    @Column(name = "nome_carteira", length = 80)
    private String nomeCarteira;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_carteira", length = 8)
    private TipoCarteiraEnum tipoCarteira;

    private Boolean status;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @ManyToOne
    @JoinColumn(name = "origem_id")
    private Origens origem;

    @Transient
    private BigDecimal saldo;

    @PrePersist
    public void aoCadastrar() {

        toUpperCase();
        setStatus(true);
        setDataCriacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        setDataUltimaAtualizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        setCodigoWallet(UUID.randomUUID().toString());
    }

    @PreUpdate
    public void aoAtualizar() {

        toUpperCase();
        setDataUltimaAtualizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
    }

    private void toUpperCase() {

        setNomeCarteira(nomeCarteira.toUpperCase().trim());
    }
}

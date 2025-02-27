package com.carrafasoft.bsuldo.api.model.criptomoedas;

import com.carrafasoft.bsuldo.api.enums.MoedaEnum;
import com.carrafasoft.bsuldo.api.enums.TipoCarteiraEnum;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
    @Column(name = "codigo_wallet", length = 36)
    private String codigoWallet;

    @NotBlank
    @Column(name = "nome_carteira", length = 80)
    private String nomeCarteira;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_carteira", length = 8)
    private TipoCarteiraEnum tipoCarteira;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private MoedaEnum moeda;

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

    @PrePersist
    public void aoCadastrar() {

        toUpperCase();
        setStatus(true);
        setDataCriacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        setDataUltimaAtualizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
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

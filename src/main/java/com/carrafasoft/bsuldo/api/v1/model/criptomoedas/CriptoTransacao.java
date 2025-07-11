package com.carrafasoft.bsuldo.api.v1.model.criptomoedas;

import com.carrafasoft.bsuldo.api.v1.enums.MoedaEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemCriptoEnum;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
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
@Table(name = "cripto_transacao")
public class CriptoTransacao {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cripto_trasacao_id")
    private Long criptoTransacaoId;

    @NotBlank
    @Column(name = "codigo_cripto_transacao", length = 36, nullable = false, updatable = false)
    private String codigoCrioptoTransacao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private MoedaEnum moeda;

    @NotNull
    @Column(precision = 19, scale = 8, nullable = false)
    private BigDecimal quantidade;

    @NotNull
    @PositiveOrZero
    @Column(name = "preco_negociacao", precision = 19, scale = 8, nullable = false)
    private BigDecimal precoNegociacao;

    @NotNull
    @Column(name = "valor_investido", precision = 19, scale = 8, nullable = false)
    private BigDecimal valorInvestido;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ordem_cripto", length = 40, nullable = false)
    private TipoOrdemCriptoEnum tipoOrdemCripto;

    @Column(name = "data_transacao", nullable = false, updatable = false)
    private LocalDateTime dataTransacao;

    @Column(name = "data_ultima_atualizacao", nullable = false)
    private LocalDateTime dataUltimaAtualizacao;

    @ManyToOne
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoas pessoa;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallets wallet;

    @PrePersist
    public void aoCadastrar() {

        setDataTransacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        setDataUltimaAtualizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        setCodigoCrioptoTransacao(UUID.randomUUID().toString());

        verificaSinalOperacao();
    }

    @PreUpdate
    public void aoAtualizar() {

        setDataUltimaAtualizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        verificaSinalOperacao();
    }

    private void verificaSinalOperacao() {

        if (tipoOrdemCripto.equals(TipoOrdemCriptoEnum.VENDA) ||
                tipoOrdemCripto.equals(TipoOrdemCriptoEnum.TRANSFERENCIA_SAIDA) ||
                tipoOrdemCripto.equals(TipoOrdemCriptoEnum.QUEIMA)) {

            // Garantir que VENDA, TRANSFERÊNCIA_SAÍDA e QUEIMA tenham valores negativos
            if (valorInvestido.compareTo(BigDecimal.ZERO) > 0) {
                valorInvestido = valorInvestido.negate();
            }
            if (quantidade.compareTo(BigDecimal.ZERO) > 0) {
                quantidade = quantidade.negate();
            }
        } else {
            // Garantir que COMPRA e outros tipos de entrada tenham valores positivos
            if (valorInvestido.compareTo(BigDecimal.ZERO) < 0) {
                valorInvestido = valorInvestido.negate();
            }
            if (quantidade.compareTo(BigDecimal.ZERO) < 0) {
                quantidade = quantidade.negate();
            }
        }
    }


}

package com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletPesquisaResponse {

    private String codigoWallet;
    private String nomeCarteira;
    private String tipoCarteira;
    private BigDecimal saldo;
    private Boolean status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;
    private OrigemResponse origem;
}

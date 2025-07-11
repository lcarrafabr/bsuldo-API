package com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletSaldoResponse {

    private String codigoWallet;
    private BigDecimal saldo;
}

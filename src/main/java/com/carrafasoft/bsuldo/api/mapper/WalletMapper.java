package com.carrafasoft.bsuldo.api.mapper;

import com.carrafasoft.bsuldo.api.mapper.criptomoeda.OrigemResponse;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletInput;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletResponse;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletsAtivosListResponse;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Origens;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Wallets;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WalletMapper {

    public WalletResponse toWalletResponse(Wallets wallet) {

        return WalletResponse.builder()
                .codigoWallet(wallet.getCodigoWallet())
                .nomeCarteira(wallet.getNomeCarteira())
                .tipoCarteira(wallet.getTipoCarteira())
                .saldo(BigDecimal.ZERO.setScale(5))//TODO incluir calculo de saldo
                .status(wallet.getStatus())
                .dataCriacao(wallet.getDataCriacao())
                .dataUltimaAtualizacao(wallet.getDataUltimaAtualizacao())
                .origem(getOrigemResponse(wallet))
                .build();
    }

    public List<WalletResponse> toListWalletResponse(List<Wallets> walletsList) {

        return walletsList.stream()
                .map(this::toWalletResponse)
                .collect(Collectors.toList());
    }

    public Wallets toWalletModel(WalletInput walletInput, Origens origens, Pessoas pessoas) {

        return Wallets.builder()
                .nomeCarteira(walletInput.getNomeCarteira())
                .tipoCarteira(walletInput.getTipoCarteira())
                .pessoa(pessoas)
                .origem(origens)
                .build();
    }



    public List<WalletsAtivosListResponse> toWalletAtivoListResponse(List<Wallets> walletsList) {

        return walletsList.stream()
                .map(this::toWalletAtivoResponse)
                .collect(Collectors.toList());
    }

    private OrigemResponse getOrigemResponse(Wallets wallet) {

        return OrigemResponse.builder()
                .codigoOrigem(wallet.getOrigem().getCodigoOrigem())
                .nomeOrigem(wallet.getOrigem().getNomeOrigem())
                .status(wallet.getOrigem().getStatusAtivo())
                .build();
    }

    private WalletsAtivosListResponse toWalletAtivoResponse(Wallets wallet) {

        return WalletsAtivosListResponse.builder()
                .codigoWallet(wallet.getCodigoWallet())
                .nomeWallet(wallet.getNomeCarteira())
                .build();
    }
}

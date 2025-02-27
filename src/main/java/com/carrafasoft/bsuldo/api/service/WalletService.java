package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletInput;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Wallets;

import javax.servlet.http.HttpServletResponse;

public interface WalletService {

    Wallets findById(String codigoWallet, String tokenId);

    Wallets cadastrarVallet(WalletInput walletInput, String tokenId, HttpServletResponse response);

    Wallets atualizarWallet(String codigoWallet, WalletInput walletInput, String tokenId);

    void removerWalet(String codigoWallet);

    void atualizaStatusAtivo(String codigoWallet, Boolean ativo);

}

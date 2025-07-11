package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.WalletInput;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.WalletResponse;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.WalletSaldoResponse;
import com.carrafasoft.bsuldo.api.v1.model.criptomoedas.Wallets;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface WalletService {

    Wallets findById(String codigoWallet, String tokenId);

    Wallets cadastrarVallet(WalletInput walletInput, String tokenId, HttpServletResponse response);

    Wallets atualizarWallet(String codigoWallet, WalletResponse walletInput, String tokenId);

    void removerWalet(String codigoWallet);

    void atualizaStatusAtivo(String codigoWallet, Boolean ativo);

    List<WalletSaldoResponse> getSaldoWalletsList(Long pessoaId);

    List<WalletResponse> getListaWalletComSaldo(Long pessoaId);

    List<Wallets> listaWalletsAtivos(String tokenId);

}

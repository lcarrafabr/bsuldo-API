package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletInput;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletPesquisaResponse;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletResponse;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletSaldoResponse;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Wallets;

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

}

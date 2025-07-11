package com.carrafasoft.bsuldo.api.v1.model.exceptionmodel;

import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;

public class WalletNaoEncontradoException extends EntidadeNaoEncontradaException {

    public static final String NAO_EXISTE_CADASTRO_COM_ID = "Não existe cadastro de carteira/wallet com o código %s";

    public WalletNaoEncontradoException(String message) {
        super(String.format(NAO_EXISTE_CADASTRO_COM_ID, message));
    }
}

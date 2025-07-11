package com.carrafasoft.bsuldo.api.v1.model.exceptionmodel;

import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;

public class CriptoTransacaoNaoEncontradoException extends EntidadeNaoEncontradaException {

    public static final String NAO_EXISTE_CADASTRO_COM_ID = "Não existe cadastro de transação de cripto com o código %s";

    public CriptoTransacaoNaoEncontradoException(String message) {
        super(String.format(NAO_EXISTE_CADASTRO_COM_ID, message));
    }
}

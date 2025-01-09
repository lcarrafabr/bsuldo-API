package com.carrafasoft.bsuldo.api.model.exceptionmodel;

import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;

public class BancoNaoEncontradoException extends EntidadeNaoEncontradaException {

    public static final String NAO_EXISTE_CADASTRO_COM_ID = "Não existe cadastro de banco com o código %s";

    public BancoNaoEncontradoException(String bancoId) {

        super(String.format(NAO_EXISTE_CADASTRO_COM_ID, bancoId));
    }
}
